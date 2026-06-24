import os
import re

dir_path = r"C:\angol"
replacements = [
    (r"import androidx\.compose\.foundation\.layout\.padding\b", "import yuteledez.padenq"),
    (r"import androidx\.compose\.foundation\.clickable\b", "import yuteledez.klekabil"),
    (r"\bstartLongPressTimer\b", "startLonqPresTaymir"),
    (r"\bsecondaryLabel\b", "sekondLeybil"),
    (r"\bsecondaryLabels\b", "sekondLeybilz"),
    (r"\bsekondereLeybil\b", "sekondLeybil"),
    (r"\bsekondereLeybilz\b", "sekondLeybilz"),
    (r"\bouterLongPress\b", "sekondRenqLonqPres"),
    (r"\bawdirLonqPres\b", "sekondRenqLonqPres"),
    (r"\bouterLongPressNumber\b", "sekondRenqNumbirLonqPres"),
    (r"\bsaveLocally\b", "seyvLokale"),
    (r"\bsaveLocallySilent\b", "seyvLokaleSaylent"),
    (r"\bseyvLokaleSilent\b", "seyvLokaleSaylent"),
    (r"\bbroadcastLayout\b", "brodkastLeyawt"),
    (r"\bsaveModuleLayout\b", "seyvModjilLeyawt"),
    (r"\bisLongPressed\b", "ezLonqPresd"),
    (r"\bonClose\b", "onKloz"),
    (r"\bcloseNestedMod\b", "klozNestedMod"),
    (r"\bpadding\b", "padenq"),
    (r"\bclickable\b", "klekabil")
]

for root, _, files in os.walk(dir_path):
    if "build" in root or ".gradle" in root or ".git" in root or "tmp" in root:
        continue
    for file in files:
        if file.endswith(".kt") and file != "ModifierExtensions.kt":
            filepath = os.path.join(root, file)
            try:
                with open(filepath, "r", encoding="utf-8") as f:
                    content = f.read()
                
                new_content = content
                for pattern, replacement in replacements:
                    new_content = re.sub(pattern, replacement, new_content)
                    
                if new_content != content:
                    with open(filepath, "w", encoding="utf-8") as f:
                        f.write(new_content)
                    print(f"Updated {filepath}")
            except Exception as e:
                print(f"Error updating {filepath}: {e}")
