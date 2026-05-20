@echo off
set "TARGET=%~1"
set "MOD=%~2"

if "%TARGET%"=="" goto usage
if "%TARGET%"=="angol" goto angol
if "%TARGET%"=="dayl" goto dayl
if "%TARGET%"=="kepad" goto kepad
if "%TARGET%"=="WearOS" goto WearOS

:usage
echo Angol CLI Tool (enstol)
echo.
echo Usage: enstol [target] [modifier]
echo.
echo Targets:
echo   angol    The entire Angol system (Dayl + Kepad)
echo   dayl     The main Angol Dayl application
echo   kepad    The Angol Input Method (IME)
echo   WearOS   Launch the Wear OS emulator
echo.
echo Modifiers:
echo   klen     Clean build and deep activation (for dayl/kepad/angol)
goto :eof

:angol
if "%MOD%"=="klen" (
    powershell -ExecutionPolicy Bypass -File .\angol_klen.ps1
) else (
    powershell -ExecutionPolicy Bypass -File .\angol.ps1
)
goto :eof

:dayl
if "%MOD%"=="klen" (
    powershell -ExecutionPolicy Bypass -File .\dayl_klen.ps1
) else (
    powershell -ExecutionPolicy Bypass -File .\dayl.ps1
)
goto :eof

:kepad
if "%MOD%"=="klen" (
    powershell -ExecutionPolicy Bypass -File .\kepad_klen.ps1
) else (
    powershell -ExecutionPolicy Bypass -File .\kepad.ps1
)
goto :eof

:WearOS
powershell -ExecutionPolicy Bypass -File .\WearOS.ps1
goto :eof
