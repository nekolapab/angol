@echo off
echo Building Angol APK...
flutter build apk --debug
if %errorlevel% equ 0 (
    echo.
    echo ✅ Build successful!
    echo APK location: build\app\outputs\flutter-apk\app-debug.apk
    echo.
    echo To install: flutter install
    echo To run: flutter run
) else (
    echo.
    echo ❌ Build failed!
)