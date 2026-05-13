# Sempil Updeyt Skrept
Write-Host "beldenq..." -ForegroundColor Cyan
./gradlew :androidApp:assembleDebug

$devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devaysez.Count -eq 0) {
    Write-Host "no devaysez fawnd!" -ForegroundColor Red
    exit 1
}

foreach ($dev in $devaysez) {
    Write-Host "enstolenq on $dev..." -ForegroundColor Yellow
    adb -s $dev install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
    
    Write-Host "lontchenq ap on $dev..." -ForegroundColor Yellow
    adb -s $dev shell am start -n io.angol.dayl/com.example.angol.ime.MeynAktevede
}
