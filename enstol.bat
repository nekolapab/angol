@echo off
set "TARGET=%~1"
set "MOD=%~2"

if "%TARGET%"=="" goto usage
if "%TARGET%"=="dayl" goto dayl
if "%TARGET%"=="kepad" goto kepad
if "%TARGET%"=="WearOS" goto WearOS

:usage
echo Angol CLI Tool (enstol)
echo.
echo Usage: enstol [target] [modifier]
echo.
echo Targets:
echo   dayl     The main Angol Dayl application
echo   kepad    The Angol Input Method (IME)
echo   WearOS   Launch the Wear OS emulator
echo.
echo Modifiers:
echo   klen     Clean build and deep activation (for dayl/kepad)
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
