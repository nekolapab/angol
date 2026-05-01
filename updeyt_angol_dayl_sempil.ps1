# Simplified Updeyt Script
Write-Host "Building..."
./gradlew :composeApp:assembleDebug

$devices = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

foreach ($dev in $devices) {
    Write-Host "Installing on $dev"
    adb -s $dev install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
}
