# Build and restart IME script
Write-Host "Building IME..." -ForegroundColor Cyan
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath
Set-Location android
.\gradlew.bat :ime:assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`nStopping IME service..." -ForegroundColor Yellow
adb shell ime disable com.example.angol.ime/.AngolImeService 2>$null
Start-Sleep -Milliseconds 500

Write-Host "Uninstalling old IME..." -ForegroundColor Yellow
adb uninstall com.example.angol.ime 2>$null
Start-Sleep -Milliseconds 500

Write-Host "Installing new IME..." -ForegroundColor Yellow
Set-Location $scriptPath
adb install -r android\ime\build\outputs\apk\debug\ime-debug.apk

if ($LASTEXITCODE -ne 0) {
    Write-Host "Install failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Enabling IME..." -ForegroundColor Yellow
adb shell ime enable com.example.angol.ime/.AngolImeService
adb shell ime set com.example.angol.ime/.AngolImeService

Write-Host "Launching IME Settings/Test App..." -ForegroundColor Yellow
adb shell am start -n com.example.angol.ime/.MainActivity

Write-Host "`nDone! IME rebuilt and restarted." -ForegroundColor Green