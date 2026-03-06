@echo off
echo Stopping IME service...
adb shell ime disable io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod
adb shell ime set io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod

echo Stopping old Compose app...
adb shell am force-stop io.angol.dayl

echo Uninstalling old Compose app...
adb uninstall io.angol.dayl

echo Installing new Compose app...
adb install build/app/outputs/flutter-apk/app-debug.apk

echo Launching Compose app...
adb shell am start -n io.angol.dayl/.KepadSkren

echo Done! IME restarted.














