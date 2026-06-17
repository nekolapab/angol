# Sempil Kepad Updeyt Skrept
$kepadApk = "angolKepadAp/build/outputs/apk/debug/angolKepadAp-debug.apk"
$kepadMod = if (Test-Path $kepadApk) { (Get-Item $kepadApk).LastWriteTime } else { [DateTime]::MinValue }

Write-Host "beldenq..." -ForegroundColor Cyan
./gradlew --offline :angolKepadAp:assembleDebug

$devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devaysez.Count -eq 0) {
    Write-Host "no devaysez fawnd. atemptenq tu lontc emyuleydir..." -ForegroundColor Yellow
    & .\WearOS.ps1
    
    Write-Host "weydenq for emyuleydir..." -ForegroundColor Yellow
    $taymawt = 60 # sekondz
    $elapst = 0
    while ($devaysez.Count -eq 0 -and $elapst -lt $taymawt) {
        Start-Sleep -Seconds 5
        $elapst += 5
        $devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }
    }

    if ($devaysez.Count -eq 0) {
        Write-Host "emyuleydir feyld tu lontc welxen $taymawt sekondz." -ForegroundColor Red
        exit 1
    }
    adb wait-for-device
}

foreach ($dev in $devaysez) {
    $newKepadMod = if (Test-Path $kepadApk) { (Get-Item $kepadApk).LastWriteTime } else { [DateTime]::MinValue }
    $kepadChanged = $newKepadMod -gt $kepadMod
    
    if ($kepadChanged -or $kepadMod -eq [DateTime]::MinValue) {
        Write-Host "enstolenq on $dev..." -ForegroundColor Yellow
        adb -s $dev install -r $kepadApk
        
        Write-Host "aplayenq kepad..." -ForegroundColor Yellow
        adb -s $dev shell ime enable io.angol.dayl/io.angol.kepad.app.KepadEnpitMelxod
        adb -s $dev shell ime set io.angol.dayl/io.angol.kepad.app.KepadEnpitMelxod
    } else {
        Write-Host "skepenq (no tceynjez)"
    }
}
