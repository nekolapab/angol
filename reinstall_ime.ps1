# Simple script to reinstall the IME APK
Write-Host "Uninstalling IME..." -ForegroundColor Yellow
adb uninstall com.example.angol.ime

Write-Host "`nInstalling new IME..." -ForegroundColor Yellow
adb install android\ime\build\outputs\apk\debug\ime-debug.apk

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nEnabling IME..." -ForegroundColor Yellow
    adb shell ime enable com.example.angol.ime/.DaylEnpitMelxod
    adb shell ime set com.example.angol.ime/.DaylEnpitMelxod
    Write-Host "`nDone! IME reinstalled. Text should now be white and selectable." -ForegroundColor Green
} else {
    Write-Host "`nInstall failed!" -ForegroundColor Red
}














