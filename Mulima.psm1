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

function ConvertFrom-Cue {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path
  )
  $LineRegex = "\s*INDEX 01 (?<time>\d{2}:\d{2}:\d{2})\s*"
  Get-Content -Path $Path | Where-Object { $_ -match $LineRegex } | ForEach-Object { $Matches.time }
}

function Get-DiscId {
  param(
    [Parameter(Mandatory = $True)]
    [string] $CuePath,

    [Parameter(Mandatory = $True)]
    [string] $FlacPath
  )

  $Cues = ConvertFrom-Cue -Path $CuePath
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

function Split-Discs {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path,

    [Parameter(Mandatory = $True)]
    [string] $DestPath
  )

  for ($i = 0; $i -lt 1000; $i++) {
    $CuePath = Join-Path -Path $Path -ChildPath ("D{0:D3}.cue" -f $DiscNumber)
    $FlacPath = Join-Path -Path $Path -ChildPath ("D{0:D3}.flac" -f $DiscNumber)
    $ArtworkPath = 'thumb.png', 'thumb.jpg' | ForEach-Object { Join-Path -Path $Path -ChildPath $_ } | Where-Object { Test-Path -Path $_ } | Select-Object -First 1

    if (Test-Path -Path $CuePath -or Test-Path -Path $FlacPath) {
      break
    }

    Write-Progress -Activity "Splitting $Path into $DestPath" -Status "Disc $i"

    $DiscId = Get-DiscId -CuePath $CuePath -FlacPath $FlacPath
    $Cues = ConvertFrom-Cue -Path $CuePath | ForEach-Object { $_ -replace ":(\d+)$", '.$1' }
    if ('00:00.00' -in $Cues) {
      $StartNum = 1
    } else {
      $StartNum = 0
    }
    $FilePrefix = 'D{0:D3}T' -f $DiscNumber
    $Cues | shntool split -q -i flac -o flac -O never -d $DestPath -a $FilePrefix -c $StartNum $FlacPath

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
  Write-Progress -Activity "Splitting $Path into $DestPath" -Completed
}

function Format-SourceDir {
  param(
    [Parameter(Mandatory = $True)]
    [string] $Path
  )

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
}

function Split-AllDiscs {
  param(
    [Parameter(Mandatory = $True)]
    [string] $RootPath,

    [Parameter(Mandatory = $True)]
    [string] $DestRootPath
  )

  $DiscDirs = Get-ChildItem -Path $RootPath -Directory | Where-Object { -Not (Get-ChildItem -Path $_.FullName -Directory) }

  $DiscDirs | ForEach-Object {
    $Source = $_.FullName
    $Dest = Resolve-RelativePath -RootPath $RootPath -Path $Source -NewRootPath $DestRootPath
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
