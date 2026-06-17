import os
import re

dir_path = r"C:\Users\nicli\angol"
replacements = {
    r"\bouterLongPress\b": "awdirLonqPres",
    r"\bsecondaryLabel\b": "sekondereLeybil",
    r"\bsecondaryLabels\b": "sekondereLeybilz",
    r"\bGredItem\b": "GredUydem"
}

for root, _, files in os.walk(dir_path):
    if "build" in root or ".gradle" in root or ".git" in root or "tmp" in root:
        continue
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, "r", encoding="utf-8") as f:
                    content = f.read()
                
                new_content = content
                for pattern, replacement in replacements.items():
                    new_content = re.sub(pattern, replacement, new_content)
                    
                if new_content != content:
                    with open(filepath, "w", encoding="utf-8") as f:
                        f.write(new_content)
                    print(f"Updated {filepath}")
            except Exception as e:
                pass
