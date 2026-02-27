Write-Host "Stopping IME service..." -ForegroundColor Yellow
adb shell ime disable com.example.angol.ime/.DaylEnpitMelxod
adb shell ime set com.example.angol.ime/.DaylEnpitMelxod

Write-Host "Stopping old Compose app..." -ForegroundColor Yellow
adb shell am force-stop com.example.myapp

Write-Host "Uninstalling old Compose app..." -ForegroundColor Yellow
adb uninstall com.example.myapp

Write-Host "Installing new Compose app..." -ForegroundColor Yellow
adb install build/app/outputs/flutter-apk/app-debug.apk

Write-Host "Launching Compose app..." -ForegroundColor Yellow
adb shell am start -n com.example.myapp/.ComposeMainActivity

Write-Host "Done! IME restarted." -ForegroundColor Green














