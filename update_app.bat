@echo off
echo 🔄 Building and updating WearOS app...
echo.

echo 📦 Building APK...
flutter build apk --debug
if %errorlevel% neq 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo.
echo 📱 Installing APK...
adb install -r build/app/outputs/flutter-apk/app-debug.apk
if %errorlevel% neq 0 (
    echo ❌ Install failed!
    pause
    exit /b 1
)

echo.
echo 🚀 Launching app...
adb shell am start -n io.angol.dayl/.KepadSkren
if %errorlevel% neq 0 (
    echo ❌ Launch failed!
    pause
    exit /b 1
)

echo.
echo ✅ App updated and launched successfully!
echo Check your WearOS emulator for the updated app.
pause