@echo off
echo Angol Auto-Build Script
echo Builds APK automatically after changes
echo Press Ctrl+C to stop

echo [%date% %time%] Starting auto-build...
flutter build apk --debug
if %errorlevel% equ 0 (
    echo [%date% %time%] Initial build successful!
    echo APK ready at: build\app\outputs\flutter-apk\app-debug.apk
    echo.
    echo To install: flutter install
    echo.
) else (
    echo [%date% %time%] Initial build failed!
    exit /b 1
)

:watch
    echo [%date% %time%] Watching for changes... (rebuild manually with: flutter build apk --debug)
    echo Press any key to rebuild, or Ctrl+C to exit
    pause >nul
    echo [%date% %time%] Rebuilding...
    flutter build apk --debug
    if %errorlevel% equ 0 (
        echo [%date% %time%] Rebuild successful! APK updated.
        echo To install latest: flutter install
    ) else (
        echo [%date% %time%] Rebuild failed!
    )
goto watch