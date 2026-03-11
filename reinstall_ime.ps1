# Simple script to reinstall the KMP IME APK
$imeId = "io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod"
$packageId = "io.angol.dayl"

Write-Host "Uninstalling IME ($packageId)..." -ForegroundColor Yellow
adb uninstall $packageId

Write-Host "`nInstalling new IME..." -ForegroundColor Yellow
$apkPath = "composeApp\build\outputs\apk\debug\composeApp-debug.apk"

if (Test-Path $apkPath) {
    adb install -r $apkPath
} else {
    Write-Host "`nAPK not found at $apkPath. Please build it first with ./gradlew :composeApp:assembleDebug" -ForegroundColor Red
    exit 1
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nEnabling IME..." -ForegroundColor Yellow
    adb shell ime enable $imeId
    adb shell ime set $imeId
    Write-Host "`nDone! IME reinstalled and activated." -ForegroundColor Green
} else {
    Write-Host "`nInstall failed!" -ForegroundColor Red
}














