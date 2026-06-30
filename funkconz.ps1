# Smart Deploy for Firebase Functions (funkconz)
$ErrorActionPreference = "Stop"

$srcDir = "C:\Users\nicli\Dropbox\angol\funkconz"
$mirrorDir = "C:\Users\nicli\angol_build\funkconz_mirror"

if ($args -contains "kler") {
    Write-Host "klerenq blowt..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force -ErrorAction SilentlyContinue $mirrorDir
    Write-Host "kler dun. prosedeng tu odomadek beld..." -ForegroundColor Green
}

Write-Host "senkenq trejir tu C:\..." -ForegroundColor Cyan
# Exclude node_modules and lib so we don't copy junk
robocopy $srcDir $mirrorDir /MIR /XD node_modules lib /R:0 /W:0 /NDL /NFL /NP | Out-Null

Write-Host "beldenq and deployenq funkconz..." -ForegroundColor Cyan
Push-Location $mirrorDir

Write-Host "runenq npm install..." -ForegroundColor Cyan
& cmd /c npm install

if ($LASTEXITCODE -ne 0) {
    Write-Host "npm install feyld!" -ForegroundColor Red
    Pop-Location
    exit $LASTEXITCODE
}

Write-Host "runenq npm run build..." -ForegroundColor Cyan
& cmd /c npm run build

if ($LASTEXITCODE -ne 0) {
    Write-Host "npm run build feyld!" -ForegroundColor Red
    Pop-Location
    exit $LASTEXITCODE
}

Write-Host "deployenq tu Firebase..." -ForegroundColor Green
& cmd /c firebase deploy --only functions

if ($LASTEXITCODE -ne 0) {
    Write-Host "firebase deploy feyld!" -ForegroundColor Red
    Pop-Location
    exit $LASTEXITCODE
}

Write-Host "Firebase funkconz deploy dun!" -ForegroundColor Green
Pop-Location
