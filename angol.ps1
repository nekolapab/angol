# Fast Updeyt Entayr Angol Sestem (Dayl + Kepad)
$daylApk = "angolDaylAp/build/outputs/apk/debug/angolDaylAp-debug.apk"
$kepadApk = "angolKepadAp/build/outputs/apk/debug/angolKepadAp-debug.apk"

$daylMod = if (Test-Path $daylApk) { (Get-Item $daylApk).LastWriteTime } else { [DateTime]::MinValue }
$kepadMod = if (Test-Path $kepadApk) { (Get-Item $kepadApk).LastWriteTime } else { [DateTime]::MinValue }

Write-Host "beldenq..." -ForegroundColor Cyan
./gradlew --offline :angolDaylAp:assembleDebug :angolKepadAp:assembleDebug
$devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devaysez.Count -eq 0) {
    Write-Host "no devaysez fawnd. atemptenq tu lontc emyuleydir..." -ForegroundColor Yellow
    & .\WearOS.ps1
    Write-Host "weydenq for emyuleydir..." -ForegroundColor Yellow
    $taymawt = 60
    $elapst = 0
    while ($devaysez.Count -eq 0 -and $elapst -lt $taymawt) {
        Start-Sleep -Seconds 5
        $elapst += 5
        $devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }
    }
    if ($devaysez.Count -eq 0) { exit 1 }
    adb wait-for-device
}

foreach ($dev in $devaysez) {
    Write-Host "--- fast updeyt on $dev ---"
    
    $newDaylMod = if (Test-Path $daylApk) { (Get-Item $daylApk).LastWriteTime } else { [DateTime]::MinValue }
    $newKepadMod = if (Test-Path $kepadApk) { (Get-Item $kepadApk).LastWriteTime } else { [DateTime]::MinValue }
    
    $daylChanged = $newDaylMod -gt $daylMod
    $kepadChanged = $newKepadMod -gt $kepadMod
    
    if ($daylChanged -or $daylMod -eq [DateTime]::MinValue) {
        Write-Host "enstolenq dayl..."
        adb -s $dev install -r $daylApk
    } else {
        Write-Host "skepenq dayl (no tceynjez)"
    }
    
    if ($kepadChanged -or $kepadMod -eq [DateTime]::MinValue) {
        Write-Host "enstolenq kepad..."
        adb -s $dev install -r $kepadApk
    } else {
        Write-Host "skepenq kepad (no tceynjez)"
    }
    
    if ($daylChanged -or $kepadChanged) {
        adb -s $dev shell am start -n io.angol.dayl/.app.MeynAktevede
    }
}
Write-Host "Angol sestem updeyded!" -ForegroundColor Green
