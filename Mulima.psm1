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

function Repair-SourceDir {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter()]
    [switch] $Force
  )

  Write-Host "Repairing: $Path"

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

  $Release = $Null
  Get-ChildItem -Path $Path -Filter '*.flac' | ForEach-Object {
    $FlacPath = $_.FullName
    $CuePath = Join-Path -Path $Path -ChildPath "$($_.BaseName).cue"

    $ExistingTags = Get-VorbisComments -Path $FlacPath

    if ($ExistingTags.PSObject.Properties.Name -contains 'MUSICBRAINZ_DISCID' -and (-Not $Force)) {
      $DiscId = $ExistingTags.'MUSICBRAINZ_DISCID'
    } else {
      $DiscId = Get-DiscId -CuePath $CuePath -FlacPath $FlacPath
    }

    if ($ExistingTags.PSObject.Properties.Name -contains 'MUSICBRAINZ_ALBUMID' -and (-Not $Force)) {
      $ReleaseId = $ExistingTags.'MUSICBRAINZ_ALBUMID'
    } elseif (-Not $Release) {
      try {
        Start-Process -FilePath "https://musicbrainz.org/cdtoc/$DiscId"
        $Response = Invoke-RestMethod -Uri "https://musicbrainz.org/ws/2/discid/$($DiscId)?inc=artists"
        $OptionIndex = 0
        $Options = $Response.metadata.disc.'release-list'.release | ForEach-Object {
          [pscustomobject]@{
            'Index'          = $OptionIndex++
            'Artist'         = $_.'artist-credit'.'name-credit'.'artist'.'name' -join ', '
            'Title'          = $_.title
            'Disambiguation' = $_.disambiguation
            'Date'           = $_.date
            'Country'        = $_.'release-event-list'.'release-event'.area.name
            'Barcode'        = $_.barcode
            'ReleaseId'      = $_.id
          }
        }
        $Options | Format-Table | Out-Host

        $Choice = Read-Host -Prompt 'Choose an option (-1 to skip)'
        if ($Choice -ge 0) {
          $Release = $Options[$Choice]
          $ReleaseId = $Release.ReleaseId
        } else {
          return
        }
      } catch {
        Write-Warning -Message "No release found for $DiscId"
        return
      }
    }

    if ($ExistingTags.PSObject.Properties.Name -contains 'ARTIST' -and (-Not $Force)) {
      $Artist = $ExistingTags.'ARTIST'
    } else {
      $Artist = $Release.Artist
    }

    if ($ExistingTags.PSObject.Properties.Name -contains 'ALBUM' -and (-Not $Force)) {
      $Artist = $ExistingTags.'ALBUM'
    } elseif ($Release.Disambiguation) {
      $Album = "{0} ({1})" -f $Release.Title, $Release.Disambiguation
    } else {
      $Album = $Release.Title
    }

    if ($FlacPath -match 'D(\d+).flac') {
      $DiscNumber = [int]$Matches[1]
    }

    metaflac '--remove-all-tags' "--set-tag=MUSICBRAINZ_ALBUMID=$ReleaseId" "--set-tag=MUSICBRAINZ_DISCID=$DiscId" "--set-tag=ALBUMARTST=$Artist" "--set-tag=ALBUM=$Album" "--set-tag=DISCNUMBER=$DiscNumber" $FlacPath
  }
}

function Repair-AllSourceDirs {
  param(
    [Parameter(Mandatory = $True)]
    [string] $RootPath
  )
  $DiscDirs = Get-ChildItem -Path $RootPath -Directory -Recurse | Where-Object { -Not (Get-ChildItem -Path $_.FullName -Directory) }
  $DiscDirs | ForEach-Object { Repair-SourceDir -Path $_.FullName }
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

    $DiscId = Get-DiscId -CuePath $CuePath -FlacPath $FlacPath
    $Cues = ConvertFrom-Cue -Path $CuePath | ForEach-Object { $_ -replace ":(\d+)$", '.$1' }
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
    Get-ChildItem -Path $DestPath -Filter "$($FilePrefix)*.flac" | ForEach-Object {
      metaflac "--set-tag=MUSICBRAINZ_DISCID=$DiscId" $ImageArg $_.FullName
    }

    $Track0 = Join-Path -Path $DestPath -ChildPath "$($FilePrefix)00.flac"
    if (Test-Path $Track0) {
      Remove-Item -Path $Track0
    }
  }

  $Artist = Split-Path -Path (Split-Path -Path $DestPath -Parent) -Leaf
  $Album = Split-Path -Path $DestPath -Leaf
  Get-ChildItem -Path $DestPath -Filter '*.flac' | Where-Object { $_.Name -match 'D(\d+)T(\d+)\.flac' } | ForEach-Object {
    $Disc = [int]($Matches[1])
    $Track = [int]($Matches[2])
    metaflac "--set-tag=ALBUMARTIST=$Artist" "--set-tag=ALBUM=$Album" "--set-tag=DISCNUMBER=$Disc" "--set-tag=TRACKNUMBER=$Track" $_.FullName
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
