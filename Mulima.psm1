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

function Split-Disc {
  param(
    [Parameter(Mandatory = $True)]
    [int] $DiscNumber,

    [Parameter(Mandatory = $True)]
    [string] $CuePath,

    [Parameter(Mandatory = $True)]
    [string] $FlacPath,

    [Parameter(Mandatory = $True)]
    [string] $DestPath
  )

  $DiscId = Get-DiscId -CuePath $CuePath -FlacPath $FlacPath
  $Cues = ConvertFrom-Cue -Path $CuePath | ForEach-Object { $_ -replace ":(\d+)$", '.$1' }
  if ('00:00.00' -in $Cues) {
    $StartNum = 1
  } else {
    $StartNum = 0
  }
  $FilePrefix = 'D{0:D3}T' -f $DiscNumber
  $Cues | shntool split -q -i flac -o flac -O never -d $DestPath -a $FilePrefix -c $StartNum $FlacPath

  Get-ChildItem -Path $DestPath -Filter "$($FilePrefix)*.flac" | ForEach-Object {
    metaflac "--set-tag=MUSICBRAINZ_DISCID=$DiscId" $_.FullName
  }
}
