# Updeyt Kepad Skrept
$ErrorActionPreference = "Stop"
Write-Host "beldenq and updeydenq Angol Kepad..." -ForegroundColor Cyan

# Step 1: Klin and Beld lha APK
Write-Host "klenenq and beldenq APK..." -ForegroundColor Yellow
./gradlew clean
./gradlew :composeApp:assembleDebug
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
    $taymawt = 60 # sekondz
    $elapst = 0
    while ($devaysez.Count -eq 0 -and $elapst -lt $taymawt) {
        Start-Sleep -Seconds 5
        $elapst += 5
        $devaysez = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }
    }

    if ($devaysez.Count -eq 0) {
        Write-Host "emyuleydir feyld tu lontc welxen $taymawt sekondz." -ForegroundColor Red
        exit 1
    }
    
    Write-Host "emyuleydir detekded. weydenq for but tu komplet..." -ForegroundColor Yellow
    adb wait-for-device
}

foreach ($devays in $devaysez) {
    Write-Host "--- prosesenq devays: $devays ---"

    # Step 0: En-pleys updeyt
    Write-Host "updeydenq pakedj on $devays..." -ForegroundColor Yellow
    $apkPax = Join-Path (Get-Location) "composeApp\build\outputs\apk\debug\composeApp-debug.apk"
    if (Test-Path $apkPax) {
        adb -s $devays install -r $apkPax
        if ($LASTEXITCODE -ne 0) {
            Write-Host "enstol feyld on $devays!" -ForegroundColor Red
            continue
        }
        Write-Host "waydenq for pakedj manedjir..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    } else {
        Write-Host "APK not fawnd at $apkPax" -ForegroundColor Red
        exit 1
    }

    # Step 3: Dip Aktiveycon
    Write-Host "pirformenq dip akteveycon on $devays..." -ForegroundColor Yellow
    $imeId = "io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod"

    # Layer 1: Standard IME kamandz
    adb -s $devays shell ime enable $imeId
    adb -s $devays shell ime set $imeId

    # Layer 2: Sekyir Sedenqz
    $kurentEnaybild = adb -s $devays shell settings get secure enabled_input_methods
    if ($kurentEnaybild -notlike "*$imeId*") {
        $nyuEnaybild = "${kurentEnaybild}:${imeId}"
        adb -s $devays shell settings put secure enabled_input_methods "'$nyuEnaybild'"
    }
    adb -s $devays shell settings put secure default_input_method $imeId
    
    Write-Host "kepad akteveyded on $devays! (skepenq ap lontc)" -ForegroundColor Green
}

Write-Host "ol devaysez updeyded!" -ForegroundColor Green
