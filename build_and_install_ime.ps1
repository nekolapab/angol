# Build and install IME script
Write-Host "Building IME APK..." -ForegroundColor Cyan

# Change to android directory
Set-Location android

# Build the IME
.\gradlew.bat :ime:assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

# Find the APK
$apkPath = Get-ChildItem -Path "." -Recurse -Include "*.apk" | Where-Object { $_.FullName -like "*ime*" } | Select-Object -First 1

if ($apkPath) {
    Write-Host "Found IME APK: $($apkPath.FullName)" -ForegroundColor Green

    Write-Host "Installing IME APK..." -ForegroundColor Yellow
    adb install -r $apkPath.FullName

    if ($LASTEXITCODE -eq 0) {
        Write-Host "IME APK installed successfully!" -ForegroundColor Green
        Write-Host "Now enable the IME in Android Settings > System > Languages & input > Virtual keyboard > Manage keyboards" -ForegroundColor Cyan
    } else {
        Write-Host "Install failed!" -ForegroundColor Red
    }
} else {
    Write-Host "No IME APK found!" -ForegroundColor Red
}