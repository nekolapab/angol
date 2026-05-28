# Sempil Updeyt Skrept
Write-Host "beldenq..." -ForegroundColor Cyan
./gradlew :daylAp:assembleDebug

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
    Write-Host "enstolenq on $dev..." -ForegroundColor Yellow
    adb -s $dev install -r daylAp/build/outputs/apk/debug/daylAp-debug.apk
    
    Write-Host "lontchenq ap on $dev..." -ForegroundColor Yellow
    adb -s $dev shell am start -n io.angol.dayl/.app.MeynAktevede
}

