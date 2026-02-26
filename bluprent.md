## **AI development gaydlaynz for Flutter and Firebase Studio**

lhez gaydlaynz defayn lha erir rezelyent opiraconal prensepilz for Gemini keypabil development wirkflow.

## **envayrment an kontekst awer**
lha AI opereyts welxen lha Firebase Studio development envayrment.

* **prodjekt strukcir:** lha AI asumz a standard Flutter prodjekt strukcir. lha praymer aplekeycon entre poynt ez tepekle lib/main.dart.
* **dev.nix konfegyireycon:** lha .idx/dev.nix fayl ez lha sors ov trulx for lha wirkpleys envayrment. lha AI cid levredj et tu encur envayrment konsestense and odomadekle konfegyir nesesere tulz.
* **prevyu sirvir:** lha AI wel kontenyuwosle monetir lha awtput ov lha prevyu sirvir (e.g., konsol logz, erir mesedjez) for rel taym fedbak on tceyndjez.
* **Firebase entegreycon:** lha AI rekognayzez standard Firebase entegreycon paternz en Flutter, enkludenq lha yus ov firebase_options.dart.

## **kod modefekeycon & dependense manedjment**
* **kor kod asumpcon:** wen a yuzir rekwestz a tceyndj, lha AI wel praymerele fokus on modefayenq lha Dart kod. lib/main.dart ez lha meyn entre poynt.
* **pakej manedjment:** lha AI wel aydentefay and ad nesesere pakedjez yuzenq `flutter pub add`.
* **kod djenereycon (build_runner):** wen a tceyndj rekwayrz kod djenereycon, lha AI wel odomadekle eksekyut `dart run build_runner build --delete-conflicting-outputs`.

## **odomeyded erir detekcon & remedeyeycon**
a kredekal funkcun ov lha AI ez tu kontenyuwosle monetir for and odomadekle rezolv erirz.

* **post-modefekeycon tceks:** afdir evre kod modefekeycon, lha AI wel monetir IDE dayagnosteks, tirmenal awtput, and prevyu sirvir awtput for erirz or eksepsconz.
* **odomadek erir korekcon:** lha AI wel atempt tu odomadekle feks detekded erirz (sentaks, tayp mesmatcez, null-safety, or lintenq vayoleycunz).
* **problem reportenq:** ef an eror kanot be otomeytekle resolvd, lha AI wel klirle report lha spesefik eror mesej and lokeycon tu lha yuzir.

## **eterativ development & yuzir entirakcon**
* **plan jenereycon & bluprent manedjment:** etc taym lha yuzir rekwestz a tceyndj, lha AI wel ferst jenereyt a klir plan and **updeyt lha bluprent.md fayl**.
  * lha bluprent.md fayl haz:
    * prodjekt ovirvyu.
    * diteyld awtlayn ov ol stayl, dezayn, and fetcirz emplemented.
    * plan and steps for lha kurent rekwested tceyndj.
* **eror tcekenq flo:**
  1. **kod tceyndj:** AI aplayz modefekeycon.
  2. **lent/format:** AI runz `dart format .` and adresez lint warnenqz.
  3. **dependense tcek:** AI runz `flutter pub get` ef nesesere.
  4. **kod jenereycon:** AI runz build_runner ef nesesere.
  5. **kompayl & anelayz:** AI moneturz `flutter analyze` and kompayleycon erorz.
  6. **test eksekyucon:** AI runz relevant tests.
  7. **prevyu tcek:** AI observs lha prevyu sirvir for vezyual and runtaym erorz.
  8. **remedeyeycon/report:** AI atempts otomeytik feksez or reports tu lha yuzir.

## **Firebase MCP**
wen rekwestenq Firebase ad lha folownq sirvir konfegyireyconz tu .idx/mcp.json and do not ad enelxenq els.

```json
{
    "mcpServers": {
        "firebase": {
            "command": "npx",
            "args": [
                "-y",
                "firebase-tools@latest",
                "experimental:mcp"
            ]
        }
    }
}
```

## *ovirvyuw*
angol ez lha sestom neym and dayl ez lhe ap neym ov a sentir heksagon and 2 or 3 sirawndenq heksagonal renqz en Flutter and ets first modyil ez kepad kustom enput melxod en Compose. 

*first enstrukcon*
* yuz angol spelenq updeytenq refrensez globale for etc neym tceynj. eksept du not reneym wat kozez problemz for Flutter an Compose standard konvencon freymwirk klasez and faylz (sutc az main.dart  firebase_options.dart  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

## **Dev Prodokol (Compose IME)**

### **Fast Reyenstol (Compose Native)**
When modifying Kotlin/Compose code (`android/...`), you must rebuild and reinstall the APK. Flutter Hot Reload does **not** apply.

### **Disk Space Manedjment**
*   **Odomadek Klenup:** Always run `flutter clean` before significant builds to reclaim disk space.
*   **Kace Manedjment:** Delete old Gradle and Flutter caches. Keep only the **latest build** per module (`ime`, `kepad`, `app`).
*   **No Redundant Beldz:** Ensure each module has only one active build artifact. Get rid of anything not strictly needed for the current iteration.

**Prodokol:**
1.  **Stop** the running app (Ctrl+C in terminal).
2.  **Run** `flutter build apk --debug --target-platform android-arm64 --android-skip-build-dependency-validation` (for physical device).
3.  **Instol** `adb install -r build/app/outputs/flutter-apk/app-debug.apk`.
4.  **Default Keyboard Reset:**
    *   **Automation:**
        ```powershell
        adb shell ime enable com.example.myapp/com.example.angol.ime.AngolImeService
        adb shell ime set com.example.myapp/com.example.angol.ime.AngolImeService
        ```

### **kepad**
* **onle lha Compose virjon ov kepad ez tu be yuzd!** do not yuz lha Flutter virjon for lha enpit melxod.
* **REMOVED:** lha entirnal Flutter keypad overlay haz bin removed from `DaylSkren.dart`.
* we ar naw yuzenq `ComposeMainActivity` tu test lha kepad dayrektle welxen lha Android part ov lha prodjekt.
* The `ime` module is now a library integrated into the main Flutter app.
* Debug Visual Layout ov `kepad` IME on WearOS emyuledir yuzenq `ComposeMainActivity`.
* Verify `HeksagonDjeyometre` math and `Layout` placement logic in `KepadModyil.kt`.

### **angol neym melxod (pyir spelenq lodjek stradedje)**
The goal is to achieve 100% consistent phonetic conversion without exceptions, prioritizing algorithmic speed and reliability over AI learning for basic spelling.
1.  **Vowel Shifts:**
    *   Short **i** -> **e** (e.g., `it` -> `et`, `is` -> `ez`).
    *   Long **a** -> **ey** (e.g., `base` -> `beys`, `make` -> `meyk`).
    *   Long **i** -> **ay** (e.g., `I` -> `ay`, `like` -> `layk`).
2.  **Consonant Clusters:**
    *   **th** -> **lh** (at start) or **lx** (middle/end) (e.g., `the` -> `lha`, `with` -> `welx`).
    *   **ch** -> **tc** (e.g., `change` -> `tceynj`, `each` -> `etc`).
    *   **sh** -> **c** (e.g., `she` -> `ci`).
    *   **j** / **soft g** -> **dj** (e.g., `logic` -> `lodjek`, `project` -> `prodjekt`).
    *   **hard c** / **ck** -> **k** (e.g., `can` -> `kan`, `back` -> `bak`).
3.  **Suffixes:**
    *   `-ing` -> `-enq`.
    *   `-tion` / `-sion` -> `-con`.
    *   Plural **s** -> **z** (when voiced).

**Implementation Strategy:**
- Use a multi-stage transformation engine in `convertToAngolSpelling`.
- Process suffixes first, then consonant clusters, then vowel shifts to avoid double-conversion.
- Maintain a small "Core Vocabulary" map for high-frequency irreducible words.
- Use Gemini for broader context/tone, but rely on Logic for spelling.

### **spetc tu tekst**
* **Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`, allowing us to respond to cursor jumps or selection changes.
* **Custom Context Menu (Unresolved):** The "angol" option in the system text selection menu (Cut/Copy/Paste toolbar) is implemented with an icon, but its visibility is inconsistent.
* **Keyboard Workaround:** The **Swipe UP on Center Hex** gesture provides a reliable way to trigger translation regardless of the app's context menu behavior.

## **stadus**
* **Compose Keypad:** wirkenq and pozecond korrektle!
* **IME Sirves:** enabled and set az defolt.
* **Live Selekcon:** implemented and working! Sliding between keys deletes the previous char and types the new one (supports multi-char labels).
* **Popup Numbirz:** implemented! Pressing or sliding onto a vowel shows numbers on the outer ring.
* **Kapedolz (Capitalization):** Fixed!
* **Vejyuwalz:** Perfectly compact and automatic! Indices 0, 2, 4, 6, 8, 10 ar naw ol **White**. Removed all padding and optimized layout to automatically fit any screen size. 'angol' toggle and display text relocated to the top as overlays.
* **angol mode toggle:** implemented toggling between Angol and Standard English spelenq for voice input. Naw pozecond az an ovirley at lha top ov lha kepad.
* **Sentir Hex (Enter & Toggle):** Fixed!
* **Backspace Repeat:** Fixed! Deletes exactly 12 characters per tick for strings without spaces.
* **angol:** implemented! "angol" option added to system text selection menu and toggle bar.
* **Voice Tap:** Improved! Tapping the output field triggers voice input. Quiet **ToneGenerator** start sound (50% volume) and even quieter stop sound (33% volume).
* **Build:** suksesful build for x64 and arm64.

## **tasks**
* (Empte - ol tasks kompleted)

## **feylyirz tu not repet ded endz**:
    *   **Galaxy Wearable App**: Fails on emulator. Use "Wear OS by Google".
    *   **Messages / Account Sync**: Fails on emulator ("Update in progress" hang). Use custom `ime` app to test.
    *   **System Search**: Missing in Wear OS 6 UI. Use custom `ime` app.
- Combining tap and drag/long-press gestures on the same GestureDetector for the same element leads to gesture conflicts and unreliable behavior.
- Assuming that `onPanEnd` or `onLongPressEnd` will always fire after a `onPanStart` or `onLongPressStart` respectively is unreliable.
- Implementing complex gesture recognition logic directly within the `build` method of a StatefulWidget can lead to performance issues.

## **prodjekt strukcir**
* `lib/main.dart`: lhe aplekeyconz entre poynt.
* `lib/firebase_options.dart`: Firebase konfegyireycon.
* `lib/modalz/angol_modalz.dart`: defaynz lhe aps deyda modilz (HexagonPosition, AxialCoordinate, ModuleData).
* `lib/modalz/kepad_konfeg.dart`: beldz kepad gred leyawt and aperans.
* `lib/modyilz/dayl_modyil.dart`: lhe sentir modyil ov modyilz.
* `lib/sirvesez/enpit_sirves.dart`: handilz enput lodjek.
* `lib/sirvesez/firebase_sirves.dart`: handilz Firebase releyted opireycunz.
* `lib/sirvesez/ovirley_sirves.dart`: kirentle a pleysholdir.
* `lib/sirvesez/platform_sirves.dart`: platform-spesefik metod tcanelz (e.g. for IME).
* `lib/sirvesez/spetc_sirves.dart`: handilz spetc tu tekst and tekst tu spetc opireycunz.
* `lib/skrenz/afdir_logen.dart`: lha hom peydj for olxentekeyded yuzirz.
* `lib/skrenz/dayl_skren.dart`: meyn skren dayl ov ap lhat cows olhir aps. et handilz yuzir enput for lha heksagon keyz.
* `lib/skrenz/saynen_skren.dart`: for unolxentekeyded yuzirz.
* `lib/steyt/angol_steyt.dart`: manedjez lhe aplekeyconz steyt.
* `lib/wedjets/enir_renq_wedjet.dart`: rendirz lha enir renq ov heksagonz.
* `lib/wedjets/heksagon_tutcboks.dart`: handilz tutc events for heksagonz.
* `lib/wedjets/heksagon_wedjet.dart`: rendirz a sengol heksagon key aperans.
* `lib/wedjets/sentir_mod_wedjet.dart`: for togil funkcinalede.
* `lib/yutelez/heksagon_djeyometre.dart`: yutelede klas for heksagonal gred djeyometre kalkyuleycuns.
