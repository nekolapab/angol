Write-Host "Stopping IME service..." -ForegroundColor Yellow
adb shell ime disable io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod
adb shell ime set io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod

Write-Host "Stopping old Compose app..." -ForegroundColor Yellow
adb shell am force-stop io.angol.dayl

Write-Host "Uninstalling old Compose app..." -ForegroundColor Yellow
adb uninstall io.angol.dayl

Write-Host "Installing new Compose app..." -ForegroundColor Yellow
adb install build/app/outputs/flutter-apk/app-debug.apk

Write-Host "Launching Compose app..." -ForegroundColor Yellow
adb shell am start -n io.angol.dayl/.KepadSkren

Write-Host "Done! IME restarted." -ForegroundColor Green














