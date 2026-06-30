const fs = require('fs');
const { execSync } = require('child_process');
const path = require('path');

const STATE_FILE_PATH = 'H:\\My Drive\\angol\\angolModyil\\src\\commonMain\\kotlin\\steyt\\AngolSteyt.kt';
const BUILD_SCRIPT_PATH = 'H:\\My Drive\\angol\\enstol.bat';

function toKotlinLong(numStr) {
    if (!numStr) return '0L';
    let str = numStr.toString();
    if (str.startsWith('-')) {
        // Handle negative longs by converting to hex 0xFF...
        const num = BigInt(str);
        if (num < 0n) {
            const hex = (num & 0xFFFFFFFFFFFFFFFFn).toString(16).toUpperCase();
            return `0x${hex}L`;
        }
    }
    return str + 'L';
}

function generateKotlinDataClass(mod) {
    const args = [];
    args.push(`id = "${mod.id}"`);
    args.push(`neym = "${mod.neym}"`);
    args.push(`kulorLong = ${toKotlinLong(mod.kulorLong)}`);
    args.push(`pozecon = ${mod.pozecon}`);
    args.push(`ezAkdev = false`); // Never hardcode active modules
    
    if (mod.glefs && mod.glefs.length > 0) {
        const glefsStr = mod.glefs.map(g => `"${g.replace(/"/g, '\\"')}"`).join(", ");
        args.push(`glefs = listOf(${glefsStr})`);
    }
    
    if (mod.glefKulorz && mod.glefKulorz.length > 0) {
        const colorsStr = mod.glefKulorz.map(c => toKotlinLong(c)).join(", ");
        args.push(`glefKulorz = listOf(${colorsStr})`);
    }
    
    if (mod.type) {
        args.push(`type = "${mod.type}"`);
    }
    
    return `            ModyilDeyda(${args.join(", ")})`;
}

async function checkAndSync() {
    try {
        // 1. Pull the request file from the device
        // run-as io.angol.dayl allows us to read internal files without root!
        const result = execSync('adb shell run-as io.angol.dayl cat files/replace_request.json 2>nul', { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] }).trim();
        
        if (!result || result.includes('No such file') || result.includes('not found') || result.includes('Permission denied') || result.includes('bad package')) {
            return; // No replace request
        }

        console.log(`[+] Replace request detected!`);
        
        // 2. Parse the JSON
        const modules = JSON.parse(result);
        if (!Array.isArray(modules)) throw new Error("Invalid JSON format");
        
        // 3. Delete the request file so we don't process it again
        execSync('adb shell run-as io.angol.dayl rm files/replace_request.json', { stdio: 'ignore' });
        console.log(`[+] Deleted replace request from device.`);

        // 4. Generate Kotlin List
        const kotlinCode = modules.map(generateKotlinDataClass).join(',\n');
        
        // 5. Inject into AngolSteyt.kt
        let steytCode = fs.readFileSync(STATE_FILE_PATH, 'utf8');
        
        // Replace initial modyilz declaration
        steytCode = steytCode.replace(
            /(var modyilz by mutableStateOf\(listOf\()([\s\S]*?)(\)\))/m,
            `$1\n${kotlinCode}\n        $3`
        );
        
        // Replace reset() function's modyilz declaration
        steytCode = steytCode.replace(
            /(fun reset\(\) \{\s*modyilz = listOf\()([\s\S]*?)(\)\s*\})/m,
            `$1\n${kotlinCode}\n        $3`
        );
        
        fs.writeFileSync(STATE_FILE_PATH, steytCode);
        console.log(`[+] Successfully injected new layout into AngolSteyt.kt!`);

        // 6. Trigger Build
        console.log(`[+] Triggering compilation (enstol.bat angol)...`);
        execSync(`"${BUILD_SCRIPT_PATH}" angol`, { 
            cwd: path.dirname(BUILD_SCRIPT_PATH),
            stdio: 'inherit' // This streams the build output directly to the terminal!
        });
        console.log(`[+] Build & Deploy complete!`);
        console.log("=====================================");
        console.log("Watching device for 'Replace' taps...");

    } catch (e) {
        // Silent fail on JSON parse or connection errors so loop doesn't crash
        if (e.message && e.message.includes('JSON')) console.error("JSON Error:", e.message);
    }
}

console.log("=====================================");
console.log("  Angol Offline Bridge Daemon v1.0   ");
console.log("=====================================");
console.log("Watching device for 'Replace' taps...");

// Loop indefinitely
setInterval(checkAndSync, 2000);
