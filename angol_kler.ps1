# Updeyt Entayr Angol Sestem (Dayl + Kepad)
$ErrorActionPreference = "Stop"
Write-Host "beldenq and updeydenq lha howl Angol sestem..." -ForegroundColor Cyan

# Step 1: Klin and Beld ol APKs
Write-Host "klenenq and beldenq APKs..." -ForegroundColor Yellow
./gradlew clean
./gradlew :angolDaylAp:assembleDebug :angolKepadAp:assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "beld feyld!" -ForegroundColor Red
    exit 1
}

# Step 2: Get ol konekted devaysez
$devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devaysez.Count -eq 0) {
    Write-Host "no devaysez fawnd. atemptenq tu lontc emyuleydir..." -ForegroundColor Yellow
    & .\WearOS.ps1
    Write-Host "weydenq for emyuleydir..." -ForegroundColor Yellow
    $taymawt = 60
    $elapst = 0
    while ($devaysez.Count -eq 0 -and $elapst -lt $taymawt) {
        Start-Sleep -Seconds 5
        $elapst += 5
        $devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }
    }
    if ($devaysez.Count -eq 0) {
        Write-Host "emyuleydir feyld tu lontc." -ForegroundColor Red
        exit 1
    }
    adb wait-for-device
}

foreach ($dev in $devaysez) {
    Write-Host "--- prosesenq devays: $dev ---"

    # Step 3: Install Dayl
    Write-Host "enstolenq Dayl on $dev..." -ForegroundColor Yellow
    adb -s $dev install -r angolDaylAp/build/outputs/apk/debug/angolDaylAp-debug.apk

    # Step 4: Install and Activate Kepad
    Write-Host "enstolenq and akteyveydenq Kepad on $dev..." -ForegroundColor Yellow
    adb -s $dev install -r angolKepadAp/build/outputs/apk/debug/angolKepadAp-debug.apk
    
    $imeId = "io.angol.kepad/.app.KepadEnpitMelxod"
    adb -s $dev shell ime enable $imeId
    adb -s $dev shell ime set $imeId
    
    # Layer 2 activation
    $kurentEnaybild = adb -s $dev shell settings get secure enabled_input_methods
    if ($kurentEnaybild -notlike "*$imeId*") {
        $nyuEnaybild = "${kurentEnaybild}:${imeId}"
        adb -s $dev shell settings put secure enabled_input_methods "'$nyuEnaybild'"
    }
    adb -s $dev shell settings put secure default_input_method $imeId

    # Step 5: Launch Dayl
    Write-Host "lontchenq Dayl on $dev..." -ForegroundColor Yellow
    adb -s $dev shell am start -n io.angol.dayl/.app.MeynAktevede
}

Write-Host "Angol sestem ez frec and aktev!" -ForegroundColor Green
