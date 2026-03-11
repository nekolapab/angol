# Updeyt Angol Dayl App Script
$ErrorActionPreference = "Stop"
Write-Host "🔄 Building and updating Angol Dayl app..." -ForegroundColor Cyan

# Step 0: Uninstall old app to ensure fresh state
Write-Host "🧹 Uninstalling old app..." -ForegroundColor Yellow
adb uninstall io.angol.dayl
# Also uninstall the other possible package name just in case
adb uninstall com.example.angol.ime

# Step 1: Clean and Build the APK
Write-Host "📦 Cleaning and Building APK..." -ForegroundColor Yellow
./gradlew clean
./gradlew :composeApp:assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}

# Step 2: Install on device
Write-Host "📱 Installing APK..." -ForegroundColor Yellow
$apkPath = Join-Path (Get-Location) "composeApp\build\outputs\apk\debug\composeApp-debug.apk"
if (Test-Path $apkPath) {
    adb install -r $apkPath
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Install failed!" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ APK not found at $apkPath" -ForegroundColor Red
    exit 1
}

# Step 3: Launch app
Write-Host "🚀 Launching app..." -ForegroundColor Yellow
adb shell am start -n io.angol.dayl/com.example.angol.ime.MainActivity
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Launch failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ App updated and launched successfully!" -ForegroundColor Green
