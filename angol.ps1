# Smart Updeyt Entayr Angol Sestem (Dayl + Kepad)
$ErrorActionPreference = "Stop"

if ($args -contains "kler") {
    Write-Host "klerenq blowt..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force -ErrorAction SilentlyContinue .gradle, build, angolDaylAp\build, angolKepadAp\build, angolModyil\build, angolRebeldAp\build, C:\Users\nicli\angol_beld
    Write-Host "kler dun." -ForegroundColor Green
    exit 0
}

Write-Host "senkenq trejir tu C:\..." -ForegroundColor Cyan
robocopy "H:\My Drive\angol" "C:\Users\nicli\angol_beld\angol_mirror" /MIR /XD .git build .gradle /R:0 /W:0 /NDL /NFL /NP | Out-Null

$daylApk = "C:/Users/nicli/angol_beld/angolDaylAp/build/outputs/apk/debug/angolDaylAp-debug.apk"
$kepadApk = "C:/Users/nicli/angol_beld/angolKepadAp/build/outputs/apk/debug/angolKepadAp-debug.apk"
$trackFile = ".enstol_track"

$gradleCache = "C:/Users/nicli/angol_beld/.gradle"
New-Item -ItemType Directory -Force $gradleCache | Out-Null

$gradleBin = "C:\Users\nicli\angol_beld\angol_mirror\gradlew.bat"
$projDir = "C:\Users\nicli\angol_beld\angol_mirror"

Write-Host "beldenq..." -ForegroundColor Cyan
Push-Location $projDir
& cmd /c $gradleBin --project-cache-dir $gradleCache --offline :angolDaylAp:assembleDebug :angolKepadAp:assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "beld feyld. atemptenq klen beld..." -ForegroundColor Yellow
    & cmd /c $gradleBin --project-cache-dir $gradleCache clean
    & cmd /c $gradleBin --project-cache-dir $gradleCache --offline :angolDaylAp:assembleDebug :angolKepadAp:assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "beld feyld agen!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
}
Pop-Location

$srcFiles = Get-ChildItem -Path . -Recurse -Include *.kt,*.xml,*.gradle.kts,*.ps1 -Exclude *build*
$latestSrcMod = ($srcFiles | Measure-Object -Property LastWriteTime -Maximum).Maximum.Ticks

$newDaylMod = if (Test-Path $daylApk) { $latestSrcMod } else { 0 }
$newKepadMod = if (Test-Path $kepadApk) { $latestSrcMod } else { 0 }

$devaysez = adb devices | Select-String -Pattern "`tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }

if ($devaysez.Count -eq 0) {
    Write-Host "no devaysez fawnd. atemptenq tu lontc emyuleydir..." -ForegroundColor Yellow
    & .\WearOS.ps1
    Write-Host "weydenq for emyuleydir..." -ForegroundColor Yellow
    $taymawt = 60
    $elapst = 0
    while ($devaysez.Count -eq 0 -and $elapst -lt $taymawt) {
        Start-Sleep -Seconds 5
        $elapst += 5
        $devaysez = adb devices | Select-String -Pattern "`tdevice$" | ForEach-Object { $_.ToString().Split("`t")[0] }
    }
    if ($devaysez.Count -eq 0) { exit 1 }
    adb wait-for-device
}

foreach ($dev in $devaysez) {
    Write-Host "--- prosesenq devays: $dev ---"
    
    $devTrackFile = "$PSScriptRoot\.angol_last_install_$dev"
    $installedDayl = 0
    $installedKepad = 0
    if (Test-Path $devTrackFile) {
        $parts = (Get-Content $devTrackFile).Split(",")
        if ($parts.Length -ge 2) {
            $installedDayl = [long]$parts[0]
            $installedKepad = [long]$parts[1]
        }
    }
    
    $daylChanged = $newDaylMod -ne $installedDayl -or $installedDayl -eq 0
    $kepadChanged = $newKepadMod -ne $installedKepad -or $installedKepad -eq 0
    
    if ($daylChanged) {
        Write-Host "enstolenq Dayl on $dev..." -ForegroundColor Yellow
        adb -s $dev install -r $daylApk
    } else {
        Write-Host "skepenq dayl (no tceynjez)"
    }
    
    if ($kepadChanged) {
        Write-Host "enstolenq and akteyveydenq Kepad on $dev..." -ForegroundColor Yellow
        adb -s $dev install -r $kepadApk
        
        $imeId = "io.angol.kepad/.app.KepadEnpitMelxod"
        adb -s $dev shell ime enable $imeId
        adb -s $dev shell ime set $imeId
        adb -s $dev shell settings put secure default_input_method $imeId
    } else {
        Write-Host "skepenq kepad (no tceynjez)"
    }
    
    if ($daylChanged -or $kepadChanged) {
        Write-Host "lontchenq Dayl on $dev..." -ForegroundColor Yellow
        adb -s $dev shell am start -n io.angol.dayl/.app.MeynAktevede
        Set-Content -Path $devTrackFile -Value "$newDaylMod,$newKepadMod"
    }
}

Write-Host "Angol sestem updeyded!" -ForegroundColor Green
