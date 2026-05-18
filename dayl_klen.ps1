# Updeyt Dayl Ap Skrept
$ErrorActionPreference = "Stop"
Write-Host "beldenq and updeydenq Angol Dayl ap..." -ForegroundColor Cyan

# Step 1: Klin and Beld lha APK
Write-Host "klenenq and beldenq APK..." -ForegroundColor Yellow
./gradlew clean
./gradlew :androidApp:assembleDebug
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
    $apkPax = Join-Path (Get-Location) "androidApp\build\outputs\apk\debug\androidApp-debug.apk"
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

    # Step 4: Launch ap
    Write-Host "lontchenq ap on $devays..." -ForegroundColor Yellow
    adb -s $devays shell am start -n io.angol.dayl/com.example.angol.ime.MeynAktevede
    if ($LASTEXITCODE -ne 0) {
        Write-Host "lontc feyld on $devays!" -ForegroundColor Red
    }
}

Write-Host "ol devaysez updeyded!" -ForegroundColor Green
