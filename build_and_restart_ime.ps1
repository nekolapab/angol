# Helper script to build, install, and restart the Angol IME on a connected device.
# Usage: .\build_and_restart_ime.ps1 [device_id]

param (
    [string]$deviceId = ""
)

$adbPackage = "com.example.myapp"
$imeService = "com.example.myapp/com.example.angol.ime.AngolImeService"

Write-Host ">>> Building and Installing Angol IME (Debug Mode)..." -ForegroundColor Cyan

$flutterArgs = @("install")
if ($deviceId) {
    $flutterArgs += "-d"
    $flutterArgs += $deviceId
}

# Run Flutter Install (builds debug APK and installs it)
& flutter @flutterArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "!!! Build/Install Failed. Exiting." -ForegroundColor Red
    exit 1
}

Write-Host ">>> Enabling IME via ADB..." -ForegroundColor Cyan
if ($deviceId) {
    adb -s $deviceId shell ime enable $imeService
    adb -s $deviceId shell ime set $imeService
} else {
    adb shell ime enable $imeService
    adb shell ime set $imeService
}

Write-Host ">>> Done! IME should be updated and active." -ForegroundColor Green
Write-Host ">>> Note: If 'ime set' fails (security exception), you must select the keyboard manually on the device." -ForegroundColor Yellow
