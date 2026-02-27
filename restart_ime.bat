@echo off
echo Stopping IME service...
adb shell ime disable com.example.angol.ime/.DaylEnpitMelxod
adb shell ime set com.example.angol.ime/.DaylEnpitMelxod

echo Stopping old Compose app...
adb shell am force-stop com.example.myapp

echo Uninstalling old Compose app...
adb uninstall com.example.myapp

echo Installing new Compose app...
adb install build/app/outputs/flutter-apk/app-debug.apk

echo Launching Compose app...
adb shell am start -n com.example.myapp/.ComposeMainActivity

echo Done! IME restarted.














