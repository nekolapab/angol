Write-Host "Launching Wear OS Emulator..." -ForegroundColor Cyan
$emulatorPath = "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe"
$avdName = "Wear_OS_Large_Round"

Start-Process -FilePath $emulatorPath -ArgumentList "-avd $avdName -netdelay none -netspeed full" -NoNewWindow

Write-Host "Emulator launched. Please wait for it to boot fully before running other scripts." -ForegroundColor Green
