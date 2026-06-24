$dirPath = "C:\angol"
$replacements = [ordered]@{
    "import androidx\.compose\.foundation\.layout\.padding\b" = "import yuteledez.padenq"
    "import androidx\.compose\.foundation\.clickable\b" = "import yuteledez.klekabil"
    "\bstartLongPressTimer\b" = "startLonqPresTaymir"
    "\bsecondaryLabel\b" = "sekondLeybil"
    "\bsecondaryLabels\b" = "sekondLeybilz"
    "\bsekondereLeybil\b" = "sekondLeybil"
    "\bsekondereLeybilz\b" = "sekondLeybilz"
    "\bouterLongPress\b" = "sekondRenqLonqPres"
    "\bawdirLonqPres\b" = "sekondRenqLonqPres"
    "\bouterLongPressNumber\b" = "sekondRenqNumbirLonqPres"
    "\bsaveLocally\b" = "seyvLokale"
    "\bsaveLocallySilent\b" = "seyvLokaleSaylent"
    "\bseyvLokaleSilent\b" = "seyvLokaleSaylent"
    "\bbroadcastLayout\b" = "brodkastLeyawt"
    "\bsaveModuleLayout\b" = "seyvModjilLeyawt"
    "\bisLongPressed\b" = "ezLonqPresd"
    "\bonClose\b" = "onKloz"
    "\bcloseNestedMod\b" = "klozNestedMod"
    "\bpadding\b" = "padenq"
    "\bclickable\b" = "klekabil"
}

Get-ChildItem -Path $dirPath -Filter *.kt -Recurse | Where-Object { $_.FullName -notmatch "build|tmp|\.gradle|\.git|ModifierExtensions\.kt" } | ForEach-Object {
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
