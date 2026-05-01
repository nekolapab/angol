# Updeyt Angol Dayl App Script
$ErrorActionPreference = "Stop"
Write-Host "Building and updating Angol Dayl app..."

# Step 1: Clean and Build the APK once
Write-Host "Cleaning and Building APK..."
./gradlew clean
./gradlew :composeApp:assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!"
    exit 1
}

# Step 2: Get all connected devices
$devices = adb devices | Select-String -Pattern "\tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devices.Count -eq 0) {
    Write-Host "No devices or emulators found!"
    exit 1
}

foreach ($device in $devices) {
    Write-Host "--- Processing Device: $device ---"

    # Step 0: Aggressive Cleanup
    Write-Host "Uninstalling existing package on $device..."
    adb -s $device uninstall io.angol.dayl

    # Step 2: Install on device
    Write-Host "Installing APK on $device..."
    $apkPath = Join-Path (Get-Location) "composeApp\build\outputs\apk\debug\composeApp-debug.apk"
    if (Test-Path $apkPath) {
        adb -s $device install -r $apkPath
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Install failed on $device!"
            continue
        }
        Write-Host "Waiting for package manager..."
        Start-Sleep -Seconds 2
    } else {
        Write-Host "APK not found at $apkPath"
        exit 1
    }

    # Step 3: Deep Activation
    Write-Host "Performing Deep Activation on $device..."
    $imeId = "io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod"

    # Layer 1: Standard IME commands
    adb -s $device shell ime enable $imeId
    adb -s $device shell ime set $imeId

    # Layer 2: Secure Settings
    $currentEnabled = adb -s $device shell settings get secure enabled_input_methods
    if ($currentEnabled -notlike "*$imeId*") {
        $newEnabled = "${currentEnabled}:${imeId}"
        adb -s $device shell settings put secure enabled_input_methods "'$newEnabled'"
    }
    adb -s $device shell settings put secure default_input_method $imeId

    # Step 4: Launch app
    Write-Host "Launching app on $device..."
    adb -s $device shell am start -n io.angol.dayl/com.example.angol.ime.MeynAktevede
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Launch failed on $device!"
    }
}

Write-Host "All devices updated and activation pushed!"
