# Build and install KMP IME script
Write-Host "Building KMP IME APK..." -ForegroundColor Cyan

# Build the KMP APK
./gradlew :composeApp:assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

# Find the APK
$apkPath = "composeApp\build\outputs\apk\debug\composeApp-debug.apk"

if (Test-Path $apkPath) {
    Write-Host "Found KMP IME APK: $apkPath" -ForegroundColor Green

    Write-Host "Installing IME APK..." -ForegroundColor Yellow
    adb install -r $apkPath

    if ($LASTEXITCODE -eq 0) {
        $imeId = "io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod"
        adb shell ime enable $imeId
        adb shell ime set $imeId
        Write-Host "IME APK installed and activated successfully!" -ForegroundColor Green
    } else {
        Write-Host "Install failed!" -ForegroundColor Red
    }
} else {
    Write-Host "No KMP IME APK found!" -ForegroundColor Red
}