# Update WearOS App Script
Write-Host "🔄 Building and updating WearOS app..." -ForegroundColor Cyan

# Step 1: Build the APK
Write-Host "📦 Building APK..." -ForegroundColor Yellow
flutter build apk --debug
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}

# Step 2: Install on device
Write-Host "📱 Installing APK..." -ForegroundColor Yellow
adb install -r build/app/outputs/flutter-apk/app-debug.apk
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Install failed!" -ForegroundColor Red
    exit 1
}

# Step 3: Launch app
Write-Host "🚀 Launching app..." -ForegroundColor Yellow
adb shell am start -n com.example.myapp/.ComposeMainActivity
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Launch failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ App updated and launched successfully!" -ForegroundColor Green
Write-Host "Check your WearOS emulator for the updated app." -ForegroundColor Cyan