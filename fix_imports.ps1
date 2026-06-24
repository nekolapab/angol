$dirPath = "C:\angol"

Get-ChildItem -Path $dirPath -Filter *.kt -Recurse | Where-Object { $_.FullName -notmatch "build|tmp|\.gradle|\.git|ModifierExtensions\.kt" } | ForEach-Object {
    $filePath = $_.FullName
    $content = Get-Content -Path $filePath -Raw
    $newContent = $content
    
    $isPackageYuteledez = $content -match "package\s+yuteledez"
    
    if ($content -match "\bpadenq\b" -and -not $isPackageYuteledez -and $content -notmatch "import\s+yuteledez\.padenq") {
        $newContent = [regex]::Replace($newContent, "(package\s+[a-zA-Z0-9\.]+)", "`$1`r`nimport yuteledez.padenq")
    }
    
    if ($content -match "\bklekabil\b" -and -not $isPackageYuteledez -and $content -notmatch "import\s+yuteledez\.klekabil") {
        $newContent = [regex]::Replace($newContent, "(package\s+[a-zA-Z0-9\.]+)", "`$1`r`nimport yuteledez.klekabil")
    }
    
    if ($content -ne $newContent) {
        Set-Content -Path $filePath -Value $newContent -Encoding UTF8
        Write-Host "Added imports to $filePath"
    }
}
