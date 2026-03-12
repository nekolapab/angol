# Updeyt Angol Dayl App Script
$ErrorActionPreference = "Stop"
Write-Host "🔄 Building and updating Angol Dayl app..." -ForegroundColor Cyan

# Step 0: Aggressive Cleanup
Write-Host "🧹 Uninstalling existing package to clear cache..." -ForegroundColor Yellow
adb -s emulator-5554 uninstall io.angol.dayl

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
    adb -s emulator-5554 install -r $apkPath
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Install failed!" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ APK not found at $apkPath" -ForegroundColor Red
    exit 1
}

# Step 3: Deep Activation
Write-Host "⌨️ Performing Deep Activation of 'kepad'..." -ForegroundColor Yellow
$imeId = "io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod"

# Layer 1: Standard IME commands
adb -s emulator-5554 shell ime enable $imeId
adb -s emulator-5554 shell ime set $imeId

# Layer 2: Secure Settings (Forces it into the 'enabled' list)
$currentEnabled = adb -s emulator-5554 shell settings get secure enabled_input_methods
if ($currentEnabled -notlike "*$imeId*") {
    $newEnabled = "${currentEnabled}:${imeId}"
    adb -s emulator-5554 shell settings put secure enabled_input_methods "'$newEnabled'"
}
adb -s emulator-5554 shell settings put secure default_input_method $imeId

# Step 4: Launch app
Write-Host "🚀 Launching app..." -ForegroundColor Yellow
adb -s emulator-5554 shell am start -n io.angol.dayl/com.example.angol.ime.MainActivity
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Launch failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ App updated and activation pushed!" -ForegroundColor Green
