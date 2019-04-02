function Resolve-RelativePath {
  param(
    [Parameter(Mandatory = $True)]
    [string] $RootPath,

    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter(Mandatory = $True)]
    [string] $NewRootPath
  )

  $FullRootPath = (Resolve-Path -Path (Join-Path -Path $RootPath -ChildPath '')).Path
  $FullNewRootPath = (Resolve-Path -Path (Join-Path -Path $NewRootPath -ChildPath '')).Path
  $FullPath = (Resolve-Path -Path $Path).Path
  $FullPath.Replace($FullRootPath, $FullNewRootPath)
}

function Watch-Progress {
  [CmdletBinding()]
  param(
    [Parameter(Mandatory = $True, ValueFromPipeline = $True)]
    [object[]] $InputObject,

    [Parameter()]
    [int] $Id = (Get-Random -Maximum 1000),

    [Parameter()]
    [int] $ParentId = -1,

    [Parameter(Mandatory = $True)]
    [string] $Activity,

    [Parameter()]
    [string] $Status,

    [Parameter()]
    [scriptblock] $CurrentOperation = { $_.ToString() }
  )

  $TotalStopWatch = [System.Diagnostics.Stopwatch]::New()
  $TotalStopWatch.Start()
  $PeriodStopWatch = [System.Diagnostics.Stopwatch]::New()
  $PeriodStopWatch.Start()

  $Data = @($Input)
  $TotalCount = $Data.Count
  $CurrentItem = 0

  try {
    Write-Progress -ParentId:$ParentId -Id:$Id -Activity:$Activity -Status:$Status
    $Data | ForEach-Object {
      $CurrentItem += 1
      if ($PeriodStopWatch.ElapsedMilliseconds -gt 500 -or $CurrentItem -eq 0) {
        $RemainingSeconds = $TotalStopWatch.ElapsedMilliseconds / $CurrentItem * ($TotalCount - $CurrentItem) / 1000
        $CurrentOp = &$CurrentOperation
        Write-Progress -ParentId:$ParentId -Id:$Id -Activity:$Activity -Status:$Status -CurrentOperation:$CurrentOp -PercentComplete ($CurrentItem / $TotalCount * 100) -SecondsRemaining $RemainingSeconds
        $PeriodStopWatch.Restart()
      }
      Write-Output -InputObject $_
    }
  } finally {
    Write-Progress -ParentId:$ParentId -Id:$Id -Activity:$Activity -Completed
    $TotalStopWatch.Stop()
    $PeriodStopWatch.Stop()
  }
}

function Get-CuePoints {
  param(
    [Parameter(Mandatory = $True, ValueFromPipeline = $True)]
    [string] $Path
  )
  Process {
    $LineRegex = "\s*INDEX 01 (?<time>\d{2}:\d{2}:\d{2})\s*"
    Get-Content -Path $Path | Where-Object { $_ -match $LineRegex } | ForEach-Object { $Matches.time }
  }
}

function Get-VorbisComments {
  param(
    [Parameter(Mandatory = $True, ValueFromPipeline = $True)]
    [string] $Path
  )
  Process {
    $LineRegex = "\s*comment\[\d+\]: (?<tag>.+?)=(?<value>.+)"
    metaflac.exe '--list' '--block-type=VORBIS_COMMENT' $Path | Where-Object { $_ -match $LineRegex } | ForEach-Object { $Tags = @{ } } { $Tags.Add($Matches.tag, $Matches.value) } { [pscustomobject]$Tags }
  }
}

function Get-DiscId {
  param(
    [Parameter(Mandatory = $True)]
    [string] $CuePath,

    [Parameter(Mandatory = $True)]
    [string] $FlacPath
  )

  $Cues = Get-CuePoints -Path $CuePath
  $SampleRate = [int](metaflac.exe '--show-sample-rate' $FlacPath | Out-String)
  $TotalSamples = [int](metaflac.exe '--show-total-samples' $FlacPath | Out-String)

  $LeadOutOffset = [int]($TotalSamples * 75 / $SampleRate + 150)

  $CueOffsets = $Cues | ForEach-Object {
    $Parts = $_ -split ':'
    $Minutes = [int]$Parts[0]
    $Seconds = [int]$Parts[1]
    $Frames = [int]$Parts[2]
    (60 * $Minutes + $Seconds) * 75 + $Frames + 150
  }

  $Offsets = , $LeadOutOffset + $CueOffsets

  $Parts = ('{0:X2}' -f 1), ('{0:X2}' -f $Cues.Count)
  for ($i = 0; $i -lt 100; $i++) {
    if ($i -lt $Offsets.Count) {
      $Offset = $Offsets[$i]
    } else {
      $Offset = 0
    }
    $Parts += '{0:X8}' -f $Offset
  }
  $BaseString = $Parts -join ''

  $SHA1 = New-Object -TypeName System.Security.Cryptography.SHA1CryptoServiceProvider
  $UTF8 = New-Object -TypeName System.Text.UTF8Encoding
  $ShaBytes = $SHA1.ComputeHash($UTF8.GetBytes($BaseString))
  (([Convert]::ToBase64String($ShaBytes) -replace '\+', '.') -replace '/', '_') -replace '=', '-'
}

function Get-Releases {
  param(
    [Parameter(Mandatory = $True, ParameterSetName = 'ByReleaseId')]
    [string] $ReleaseId,

    [Parameter(Mandatory = $True, ParameterSetName = 'ByDiscId')]
    [string] $DiscId
  )

  Start-Sleep -Seconds 1
  if ($PSCmdlet.ParameterSetName -eq 'ByReleaseId') {
    try {
      $Response = Invoke-RestMethod -Uri "https://musicbrainz.org/ws/2/release/$($ReleaseId)?inc=artists+labels+recordings"
    } catch {
      Write-Warning "No releases found for release ID: $ReleaseId"
      return @()
    }

    [pscustomobject]@{
      'Artist'         = $Response.metadata.release.'artist-credit'.'name-credit'.'artist'.'name' -join ', '
      'Title'          = $Response.metadata.release.title
      'Disambiguation' = $Response.metadata.release.disambiguation
      'DiscNumbers'    = [pscustomobject]@{ }
      'DiscTotal'      = $Response.metadata.release.'medium-list'.count
      'Date'           = $Response.metadata.release.date
      'Country'        = $Response.metadata.release.'release-event-list'.'release-event'.area.name
      'Label'          = $Response.metadata.release.'label-info-list'.'label-info'.label.name
      'CatalogNumber'  = $Response.metadata.release.'label-info-list'.'label-info'.'catalog-number'
      'Barcode'        = $Response.metadata.release.barcode
      'ReleaseId'      = $Response.metadata.release.id
    }
  } else {
    try {
      $Response = Invoke-RestMethod -Uri "https://musicbrainz.org/ws/2/discid/$($DiscId)?inc=artists+labels"
    } catch {
      Write-Warning "No releases found for disc ID: $DiscId"
      return @()
    }

    if ($Response.metadata.cdstub.id -or ($Response.metadata.disc.'release-list'.count -eq '0')) {
      Write-Warning "CD stub found for disc ID: $DiscId"
      return @()
    }

    $OptionIndex = 0
    $Response.metadata.disc.'release-list'.release | ForEach-Object {
      [pscustomobject]@{
        'Index'          = $OptionIndex++
        'Artist'         = $_.'artist-credit'.'name-credit'.'artist'.'name' -join ', '
        'Title'          = $_.title
        'Disambiguation' = $_.disambiguation
        'DiscNumbers'    = $_.'medium-list'.medium | ForEach-Object -Begin { $Discs = @{ } } -End { [pscustomobject]$Discs } -Process {
          $DiscIds = $_.'disc-list'.disc.id
          $Discs.Add($_.position, $DiscIds)
        }
        'DiscTotal'      = $_.'medium-list'.count
        'Date'           = $_.date
        'Country'        = $_.'release-event-list'.'release-event'.area.name
        'Label'          = $_.'label-info-list'.'label-info'.label.name
        'CatalogNumber'  = $_.'label-info-list'.'label-info'.'catalog-number'
        'Barcode'        = $_.barcode
        'ReleaseId'      = $_.id
      }
    }
  }
}

function Get-ReleaseTracks {
  param(
    [Parameter(Mandatory = $True)]
    [string] $ReleaseId
  )

  Start-Sleep -Seconds 1
  try {
    $Response = Invoke-RestMethod -Uri "https://musicbrainz.org/ws/2/release/$($ReleaseId)?inc=recordings"
  } catch {
    Write-Warning "No releases found for release ID: $ReleaseId"
    return
  }

  $Result = @{ }
  foreach ($Medium in $Response.metadata.release.'medium-list'.medium) {
    $DiscNumber = $Medium.position
    $Disc = @{ }

    foreach ($Track in $Medium.'track-list'.track) {
      $Disc.Add($Track.position, [pscustomobject]@{
          'TrackId'     = $Track.id
          'RecordingId' = $Track.recording.id
          'Title'       = $Track.recording.title
        })
    }

    $Result.Add($DiscNumber, [pscustomobject]$Disc)
  }
  [pscustomobject]$Result
}

function Repair-SourceDir {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter()]
    [switch] $Force
  )

  Write-Host "*** Repairing: $Path ***"

  Get-ChildItem -Path $Path -Filter '*.flac' | Where-Object { $_.Name -notmatch 'D\d+\.flac' } | Rename-Item -NewName {
    if ($_ -match '.*\((\d+)\)\.flac') {
      'D{0:D3}.flac' -f [int]$Matches[1]
    } else {
      'D001.flac'
    }
  }

  Get-ChildItem -Path $Path -Filter '*.cue' | Where-Object { $_.Name -notmatch 'D\d+\.cue' } | Rename-Item -NewName {
    if ($_ -match '.*\((\d+)\)\.cue') {
      'D{0:D3}.cue' -f [int]$Matches[1]
    } else {
      'D001.cue'
    }
  }

  if (Test-Path "$Path\folder.jpeg") {
    magick.exe "$Path\folder.jpeg" -resize '1000x1000>' "$Path\thumb.jpg"
  } elseif (Test-Path "$Path\folder.jpg") {
    magick.exe "$Path\folder.jpg" -resize '1000x1000>' "$Path\thumb.jpg"
  } elseif (Test-Path "$Path\folder.png") {
    magick.exe "$Path\folder.png" -resize '1000x1000>' "$Path\thumb.png"
  }

  $CurrentState = Get-ChildItem -Path $Path -Filter '*.flac' | ForEach-Object {
    $FlacPath = $_.FullName
    $CuePath = Join-Path -Path $Path -ChildPath "$($_.BaseName).cue"
    $ExistingTags = Get-VorbisComments -Path $FlacPath
    if ($ExistingTags.PSObject.Properties.Name -contains 'MUSICBRAINZ_DISCID') {
      $DiscId = $ExistingTags.'MUSICBRAINZ_DISCID'
    } else {
      $DiscId = Get-DiscId -CuePath $CuePath -FlacPath $FlacPath
    }
    [pscustomobject]@{
      'FlacPath' = $FlacPath
      'CuePath'  = $CuePath
      'DiscId'   = $DiscId
      'Tags'     = $ExistingTags
    }
  }

  $WithoutReleaseId = $CurrentState | Where-Object { $_.Tags.PSObject.Properties.Name -notcontains 'MUSICBRAINZ_ALBUMID' }

  if ($WithoutReleaseId.Count -eq 0 -and (-not $Force)) {
    return
  }

  Write-Host ('***** Existing tags for: {0} *****' -f $Path)
  $CurrentState | Format-Table -Property FlacPath, DiscId, Tags -AutoSize | Out-Host

  Write-Host '***** Options *****'
  $Options = $CurrentState.DiscId | ForEach-Object { Get-Releases -DiscId $_ } | Select-Object -Unique
  $Options | Format-Table -Property Index, Artist, Title, Disambiguation, DiscTotal, Date, Country, Label, CatalogNumber, Barcode, ReleaseId -AutoSize | Out-Host

  $Release = $Null
  $ChoiceNum = -1
  while ($True) {
    $Choice = Read-Host -Prompt 'Enter an option number or release ID (''s'' to skip, ? to open in browser)'
    if ($Choice -eq '?') {
      Start-Process -FilePath 'https://musicbrainz.org/search'
    } elseif ($Choice -eq 's') {
      return
    } elseif ($Choice -match '^\d+$') {
      $ChoiceNum = [int]$Choice
      if ($ChoiceNum -ge 0 -and $ChoiceNum -lt @($Options).Count) {
        $Release = $Options[$ChoiceNum]
        $Release | Format-List | Out-Host
        $Verified = (Read-Host -Prompt 'Does this look right? (y/n)') -eq 'y'
        if ($Verified) {
          break
        }
      }
    } else {
      $Release = Get-Releases -ReleaseId $Choice
      if ($Release) {
        $Release | Format-List | Out-Host
        $Verified = (Read-Host -Prompt 'Does this look right? (y/n)') -eq 'y'
        if ($Verified) {
          break
        }
      }
    }
  }

  $CurrentState | ForEach-Object {
    $CurrentDisc = $_
    $Artist = $Release.Artist

    $DiscNumber = $Release.DiscNumbers.PSObject.Properties | Where-Object {
      $_.Value -contains $CurrentDisc.DiscId
    } | ForEach-Object { [int]$_.Name }

    $FlacName = Split-Path -Path $CurrentDisc.FlacPath -Leaf
    if (-not $DiscNumber -or @($DiscNumber).Count -gt 1) {
      $DiscNumber = [int](Read-Host -Prompt "What disc id this? $FlacName")
    }

    if ($Release.Disambiguation) {
      $Album = "{0} ({1})" -f $Release.Title, $Release.Disambiguation
    } else {
      $Album = $Release.Title
    }

    if ($FlacName -ne ('D{0:D3}.flac' -f $DiscNumber)) {
      Write-Warning -Message "Disc number does not match file name: $($CurrentDisc.FlacPath)"
    }

    metaflac.exe '--remove-all-tags' "--set-tag=MUSICBRAINZ_ALBUMID=$($Release.ReleaseId)" "--set-tag=MUSICBRAINZ_DISCID=$($CurrentDisc.DiscId)" "--set-tag=ALBUMARTST=$Artist" "--set-tag=ALBUM=$Album" "--set-tag=DISCNUMBER=$DiscNumber" $CurrentDisc.FlacPath
  }
}

function Repair-AllSourceDirs {
  param(
    [Parameter(Mandatory = $True)]
    [string] $RootPath,

    [Parameter()]
    [switch] $Force
  )
  $DiscDirs = Get-ChildItem -Path $RootPath -Directory -Recurse | Where-Object { -Not (Get-ChildItem -Path $_.FullName -Directory) }
  $DiscDirs | ForEach-Object { Repair-SourceDir -Path $_.FullName -Force:$Force }
}

function Split-Discs {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter(Mandatory = $True)]
    [string] $DestPath
  )

  $TrackIds = $Null
  for ($DiscNumber = 1; $DiscNumber -lt 1000; $DiscNumber++) {
    $CuePath = Join-Path -Path $Path -ChildPath ("D{0:D3}.cue" -f $DiscNumber)
    $FlacPath = Join-Path -Path $Path -ChildPath ("D{0:D3}.flac" -f $DiscNumber)
    $ArtworkPath = 'thumb.png', 'thumb.jpg' | ForEach-Object { Join-Path -Path $Path -ChildPath $_ } | Where-Object { Test-Path -Path $_ } | Select-Object -First 1

    if (-Not ((Test-Path -Path $CuePath) -and (Test-Path -Path $FlacPath))) {
      break
    }

    $ExistingTags = Get-VorbisComments -Path $FlacPath

    if (-not $TrackIds) {
      $TrackIds = Get-ReleaseTracks -ReleaseId $ExistingTags.'MUSICBRAINZ_ALBUMID'
    }

    $Cues = Get-CuePoints -Path $CuePath | ForEach-Object { $_ -replace ":(\d+)$", '.$1' }
    if ('00:00.00' -in $Cues) {
      $StartNum = 1
    } else {
      $StartNum = 0
    }
    $FilePrefix = 'D{0:D3}T' -f $DiscNumber
    if ($Cues.Count -gt 1) {
      $Cues | shntool.exe split -q -i flac -o flac -O always -d $DestPath -a $FilePrefix -c $StartNum $FlacPath
    } else {
      $Dest = Join-Path -Path $DestPath -ChildPath "$($FilePrefix)01.flac"
      Copy-Item -Path $FlacPath -Destination $Dest
    }

    if ($ArtworkPath) {
      $ImageArg = "--import-picture-from=$ArtworkPath"
    } else {
      $ImageArg = ''
    }
    $TagArgs = $ExistingTags.PSObject.Properties | ForEach-Object { "--set-tag=$($_.Name)=`"$($_.Value)`"" }

    Get-ChildItem -Path $DestPath -Filter "$($FilePrefix)*.flac" | ForEach-Object {
      if ($_.Name -match 'D\d+T(\d+).flac') {
        $TrackNumber = [int]$Matches[1]
      }

      $RealDiscNumber = $ExistingTags.'DISCNUMBER'
      $TrackId = $TrackIds.$RealDiscNumber.$TrackNumber.TrackId
      $RecordingId = $TrackIds.$RealDiscNumber.$TrackNumber.RecordingId
      $Title = $TrackIds.$RealDiscNumber.$TrackNumber.Title
      metaflac.exe @TagArgs "--set-tag=TITLE=$Title" "--set-tag=MUSICBRAINZ_TRACKID=$RecordingId" "--set-tag=MUSICBRAINZ_RELEASETRACKID=$TrackId" "--set-tag=TRACKNUMBER=$TrackNumber" $ImageArg $_.FullName
    }

    $Track0 = Join-Path -Path $DestPath -ChildPath "$($FilePrefix)00.flac"
    if (Test-Path $Track0) {
      Remove-Item -Path $Track0
    }
  }
}

function ConvertTo-Opus {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter(Mandatory = $True)]
    [string] $DestPath,

    [Parameter()]
    [int] $Bitrate = 128
  )

  opusenc.exe '--quiet' '--bitrate' $Bitrate $Path $DestPath
}

function Update-LossyLibrary {
  [CmdletBinding(SupportsShouldProcess = $True, ConfirmImpact = 'Medium')]
  param(
    [Parameter(Mandatory = $True)]
    [string] $LosslessPath,

    [Parameter(Mandatory = $True)]
    [string] $LossyPath
  )

  $FlacFiles = Get-ChildItem -Path $LosslessPath -Recurse -Filter '*.flac'

  $FlacFiles | Watch-Progress -Activity 'Updating Lossy Library' -Status 'Converting flac to opus' -CurrentOperation { "Converting $_" } | ForEach-Object {
    $FlacPath = $_.FullName
    $OpusPath = (Resolve-RelativePath -RootPath $LosslessPath -Path $FlacPath -NewRootPath $LossyPath).Replace('.flac', '.opus')

    $FlacDate = (Get-Item -Path $FlacPath).LastWriteTime
    if (Test-Path -Path $OpusPath -NewerThan $FlacDate) {
      Write-Verbose "$FlacPath is up to date."
    } elseif ($PSCmdlet.ShouldProcess($FlacPath, "Convert to opus")) {
      New-Item -Path (Split-Path -Path $OpusPath -Parent) -ItemType Directory -Force | Out-Null
      ConvertTo-Opus -Path $FlacPath -DestPath $OpusPath
    }
  }
}

function Update-LosslessLibrary {
  [CmdletBinding(SupportsShouldProcess = $True, ConfirmImpact = 'Medium')]
  param(
    [Parameter(Mandatory = $True)]
    [string] $OriginalPath,

    [Parameter(Mandatory = $True)]
    [string] $StagingPath,

    [Parameter(Mandatory = $True)]
    [string] $LosslessPath
  )

  $DiscDirs = Get-ChildItem -Path $OriginalPath -Directory -Recurse | Where-Object { -Not (Get-ChildItem -Path $_.FullName -Directory) }

  $DiscDirs | Watch-Progress -Activity 'Updating Lossless Library' -Status 'Splitting flac files' -CurrentOperation { "Splitting $_" } | ForEach-Object {
    $Source = $_.FullName
    $Staging = Resolve-RelativePath -RootPath $OriginalPath -Path $Source -NewRootPath $StagingPath
    $Dest = Resolve-RelativePath -RootPath $OriginalPath -Path $Source -NewRootPath $StagingPath

    $ReleaseId = Get-ChildItem -Path $Source -Filter '*.flac' | ForEach-Object { Get-VorbisComments -Path $_.FullName } | Where-Object { $_.PSObject.Properties.Name -contains 'MUSICBRAINZ_ALBUMID' } | Select-Object -First 1 -ExpandProperty 'MUSICBRAINZ_ALBUMID'

    $SourceDate = (Get-ChildItem -Path $Source | ForEach-Object { $_.LastWriteTime } | Measure-Object -Maximum).Maximum

    $Staging = Join-Path -Path $StagingPath -ChildPath $ReleaseId
    $Lossless = Join-Path -Path $LosslessPath -ChildPath $ReleaseId

    $StagingUpToDate = (Test-Path -Path $Staging) -and (Get-ChildItem -Path $Staging | Test-Path -NewerThan $SourceDate) -contains $False
    $LosslessUpToDate = (Test-Path -Path $Lossless) -and (Get-ChildItem -Path $Lossless | Test-Path -NewerThan $SourceDate) -contains $False
    if ($StagingUpToDate -or $LosslessUpToDate) {
      Write-Verbose "$Source is up to date."
    } elseif ($PSCmdlet.ShouldProcess($Source, "Split flac disc files")) {
      New-Item -Path $Staging -ItemType Directory -Force | Out-Null
      Split-Discs -Path $Source -DestPath $Staging
    }
  }
}
