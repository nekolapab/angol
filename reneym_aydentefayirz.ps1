$dirPath = "C:\Users\nicli\angol"
$replacements = @{
    "\bouterLongPress\b" = "awdirLonqPres"
    "\bsecondaryLabel\b" = "sekondereLeybil"
    "\bsecondaryLabels\b" = "sekondereLeybilz"
    "\bGredItem\b" = "GredUydem"
    "\bhasTraveler\b" = "hazTravlir"
}

Get-ChildItem -Path $dirPath -Filter *.kt -Recurse | Where-Object { $_.FullName -notmatch "build|tmp|\.gradle|\.git" } | ForEach-Object {
    $filePath = $_.FullName
    $content = Get-Content -Path $filePath -Raw
    $newContent = $content
    
    foreach ($key in $replacements.Keys) {
        $newContent = [regex]::Replace($newContent, $key, $replacements[$key])
    }
    
    if ($content -ne $newContent) {
        Set-Content -Path $filePath -Value $newContent -Encoding UTF8
        Write-Host "Updated $filePath"
    }
}
