@echo off
echo Stopping IME service...
adb shell ime disable com.example.angol.ime/.AngolImeService
adb shell ime set com.example.angol.ime/.AngolImeService

echo Uninstalling old IME...
adb uninstall com.example.angol.ime

echo Installing new IME...
adb install android\ime\build\outputs\apk\debug\ime-debug.apk

echo Enabling IME...
adb shell ime enable com.example.angol.ime/.AngolImeService
adb shell ime set com.example.angol.ime/.AngolImeService

echo Done! IME restarted.














