# Sempil Kepad Updeyt Skrept
Write-Host "beldenq kepad..." -ForegroundColor Cyan
./gradlew :kepadApekstencon:assembleDebug

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
    Write-Host "enstolenq kepad on $dev..." -ForegroundColor Yellow
    adb -s $dev install -r kepadApekstencon/build/outputs/apk/debug/kepadApekstencon-debug.apk
}

