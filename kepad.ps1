# Sempil Kepad Updeyt Skrept
Write-Host "beldenq kepad..." -ForegroundColor Cyan
./gradlew :composeApp:assembleDebug

$devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devaysez.Count -eq 0) {
    Write-Host "no devaysez fawnd!" -ForegroundColor Red
    exit 1
}

foreach ($dev in $devaysez) {
    Write-Host "enstolenq kepad on $dev..." -ForegroundColor Yellow
    adb -s $dev install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
}
