# Fast Updeyt Entayr Angol Sestem (Dayl + Kepad)
Write-Host "beldenq..." -ForegroundColor Cyan
./gradlew :daylAp:assembleDebug :kepadAp:assembleDebug

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
    adb -s $dev install -r daylAp/build/outputs/apk/debug/daylAp-debug.apk
    adb -s $dev install -r kepadAp/build/outputs/apk/debug/kepadAp-debug.apk
    adb -s $dev shell am start -n io.angol.dayl/.app.MeynAktevede
}
Write-Host "Angol sestem updeyded!" -ForegroundColor Green
