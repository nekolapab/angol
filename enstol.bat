@echo off
set "CMD=%~1"

if "%CMD%"=="" goto usage
if "%CMD%"=="klen" goto klen
if "%CMD%"=="fast" goto fast
if "%CMD%"=="WearOS" goto WearOS

:usage
echo Angol CLI Tool (enstol)
echo.
echo Usage: enstol [command]
echo.
echo Commands:
echo   klen     Clean build, deep activation, and launch app (Full)
echo   fast     Fast build and install (no clean, no activation)
echo   WearOS   Launch the Wear OS emulator
goto :eof

:klen
powershell -ExecutionPolicy Bypass -File .\klen_dayl.ps1
goto :eof

:fast
powershell -ExecutionPolicy Bypass -File .\fast_dayl.ps1
goto :eof

:WearOS
powershell -ExecutionPolicy Bypass -File .\WearOS.ps1
goto :eof
