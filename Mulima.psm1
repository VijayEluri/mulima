function Resolve-RelativePath {
  param(
    [Parameter(Mandatory = $True)]
    [string] $RootPath,

    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter(Mandatory = $True)]
    [string] $NewRootPath
  )

  $FullRootPath = [IO.Path]::GetFullPath($RootPath)
  $FullNewRootPath = [IO.Path]::GetFullPath($NewRootPath)
  $FullPath = [IO.Path]::GetFullPath($Path)
  $FullPath.Replace($FullRootPath, $FullNewRootPath)
}

function Get-CuePoints {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path
  )
  $LineRegex = "\s*INDEX 01 (?<time>\d{2}:\d{2}:\d{2})\s*"
  Get-Content -Path $Path | Where-Object { $_ -match $LineRegex } | ForEach-Object { $Matches.time }
}

function Get-VorbisComments {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path
  )
  $LineRegex = "\s*comment\[\d+\]: (?<tag>.+?)=(?<value>.+)"
  metaflac '--list' '--block-type=VORBIS_COMMENT' $Path | Where-Object { $_ -match $LineRegex } | ForEach-Object { $Tags = @{} } { $Tags.Add($Matches.tag, $Matches.value) } { [pscustomobject]$Tags }
}

function Get-DiscId {
  param(
    [Parameter(Mandatory = $True)]
    [string] $CuePath,

    [Parameter(Mandatory = $True)]
    [string] $FlacPath
  )

  $Cues = Get-CuePoints -Path $CuePath
  $SampleRate = [int](metaflac '--show-sample-rate' $FlacPath | Out-String)
  $TotalSamples = [int](metaflac '--show-total-samples' $FlacPath | Out-String)

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
  $UTF8 = New-object -TypeName System.Text.UTF8Encoding
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
      $Response = Invoke-RestMethod -Uri "https://musicbrainz.org/ws/2/release/$($Choice)?inc=artists+labels+recordings"
    } catch {
      Write-Warning "No releases found for release ID: $ReleaseId"
      return @()
    }

    [pscustomobject]@{
      'Artist'         = $Response.metadata.release.'artist-credit'.'name-credit'.'artist'.'name' -join ', '
      'Title'          = $Response.metadata.release.title
      'Disambiguation' = $Response.metadata.release.disambiguation
      'DiscNumbers'    = [pscustomobject]@{}
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

    $OptionIndex = 0
    $Response.metadata.disc.'release-list'.release | ForEach-Object {
      [pscustomobject]@{
        'Index'          = $OptionIndex++
        'Artist'         = $_.'artist-credit'.'name-credit'.'artist'.'name' -join ', '
        'Title'          = $_.title
        'Disambiguation' = $_.disambiguation
        'DiscNumbers'    = $_.'medium-list'.medium | ForEach-Object -Begin { $Discs = @{} } -End { [pscustomobject]$Discs  } -Process {
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

function Repair-SourceDir {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter()]
    [switch] $Force
  )

  Write-Host "***** Repairing: $Path *****"

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
    magick "$Path\folder.jpeg" -resize '1000x1000>' "$Path\thumb.jpg"
  } elseif (Test-Path "$Path\folder.jpg") {
    magick "$Path\folder.jpg" -resize '1000x1000>' "$Path\thumb.jpg"
  } elseif (Test-Path "$Path\folder.png") {
    magick "$Path\folder.png" -resize '1000x1000>' "$Path\thumb.png"
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

  Write-Host ('**** Existing tags for: {0} ****' -f $Path)
  $CurrentState | Format-Table -Property FlacPath, DiscId, Tags -AutoSize | Out-Host
  # $CurrentState | ForEach-Object {
  #   Write-Host ('{0} ({1})' -f (Split-Path -Path $_.FlacPath -Leaf), $_.DiscId)
  #   $_.Tags | Format-Table | Out-Host
  # }

  $Options = $CurrentState.DiscId | ForEach-Object { Get-Releases -DiscId $_ } | Select-Object -Unique

  Write-Host '*** Options ***'
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
    if (-not $DiscNumber) {
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

    metaflac '--remove-all-tags' "--set-tag=MUSICBRAINZ_ALBUMID=$($Release.ReleaseId)" "--set-tag=MUSICBRAINZ_DISCID=$($CurrentDisc.DiscId)" "--set-tag=ALBUMARTST=$Artist" "--set-tag=ALBUM=$Album" "--set-tag=DISCNUMBER=$DiscNumber" $CurrentDisc.FlacPath
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

  for ($DiscNumber = 1; $DiscNumber -lt 1000; $DiscNumber++) {
    $CuePath = Join-Path -Path $Path -ChildPath ("D{0:D3}.cue" -f $DiscNumber)
    $FlacPath = Join-Path -Path $Path -ChildPath ("D{0:D3}.flac" -f $DiscNumber)
    $ArtworkPath = 'thumb.png', 'thumb.jpg' | ForEach-Object { Join-Path -Path $Path -ChildPath $_ } | Where-Object { Test-Path -Path $_ } | Select-Object -First 1

    if (-Not ((Test-Path -Path $CuePath) -and (Test-Path -Path $FlacPath))) {
      break
    }

    Write-Progress -Activity "Splitting $Path" -Status "Disc $DiscNumber"

    $ExistingTags = Get-VorbisComments -Path $FlacPath
    $Cues = Get-CuePoints -Path $CuePath | ForEach-Object { $_ -replace ":(\d+)$", '.$1' }
    if ('00:00.00' -in $Cues) {
      $StartNum = 1
    } else {
      $StartNum = 0
    }
    $FilePrefix = 'D{0:D3}T' -f $DiscNumber
    $Cues | shntool split -q -i flac -o flac -O always -d $DestPath -a $FilePrefix -c $StartNum $FlacPath

    if ($ArtworkPath) {
      $ImageArg = "--import-picture-from=$ArtworkPath"
    } else {
      $ImageArg = ''
    }
    $TagArgs = $ExistingTags.PSObject.Properties | ForEach-Object { "--set-tag=$($_.Name)=$($_.Value)" }

    Get-ChildItem -Path $DestPath -Filter "$($FilePrefix)*.flac" | ForEach-Object {
      if ($_.Name -match 'D\d+T(\d+).flac') {
        $TrackNumber = [int]$Matches[1]
      }
      metaflac @TagArgs "--set-tag=TRACKNUMBER=$TrackNumber" $ImageArg $_.FullName
    }

    $Track0 = Join-Path -Path $DestPath -ChildPath "$($FilePrefix)00.flac"
    if (Test-Path $Track0) {
      Remove-Item -Path $Track0
    }
  }
  Write-Progress -Activity "Splitting $Path" -Completed
}

function Split-AllDiscs {
  param(
    [Parameter(Mandatory = $True)]
    [string] $RootPath,

    [Parameter(Mandatory = $True)]
    [string] $DestRootPath
  )

  $DiscDirs = Get-ChildItem -Path $RootPath -Directory -Recurse | Where-Object { -Not (Get-ChildItem -Path $_.FullName -Directory) }

  $DiscDirs | ForEach-Object {
    $Source = $_.FullName
    $Dest = Resolve-RelativePath -RootPath $RootPath -Path $Source -NewRootPath $DestRootPath
    New-Item -Path $Dest -ItemType Directory -Force | Out-Null
    Format-SourceDir -Path $Source
    Split-Discs -Path $Source -DestPath $Dest
  }
}

function ConvertTo-Opus {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter(Mandatory = $True)]
    [string] $DestPath
  )
}
