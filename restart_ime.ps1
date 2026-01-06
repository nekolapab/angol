Write-Host "Stopping IME service..." -ForegroundColor Yellow
adb shell ime disable com.example.angol.ime/.AngolImeService
adb shell ime set com.example.angol.ime/.AngolImeService

Write-Host "Uninstalling old IME..." -ForegroundColor Yellow
adb uninstall com.example.angol.ime

Write-Host "Installing new IME..." -ForegroundColor Yellow
adb install android\ime\build\outputs\apk\debug\ime-debug.apk

Write-Host "Enabling IME..." -ForegroundColor Yellow
adb shell ime enable com.example.angol.ime/.AngolImeService
adb shell ime set com.example.angol.ime/.AngolImeService

Write-Host "Done! IME restarted." -ForegroundColor Green














