## *prodjekt*
angol ez lha neym and dayl ez lha ap ov a sentir heksagon and 2 or 3 sirawndenq heksagon renqz. ets first modyil ez kepad kustom enput melxod en KotlinCompose. 

*first enstrukcon*
yuz angol spelenq and updeyt refrensez globale for etc neym tceynj. eksept du not reneym KotlinCompose standard konvencon freymwirk klasez and faylz lhat koz problemz (sutc az main.dart  firebase_options.dart  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

## **angol dayl ap development prodokol**
**kepad (Input Method Editor)** yuzez KotlinCompose. lha Flutter kepad ez remuvd. so do not yuz a Flutter virjon for lha enpit melxod and ensted delet remnant duplekat faylz and refaktor.
*   Verify `HeksagonDjeyometre` math and `Layout` placement logic in `KepadModyil.kt`.
**Fast Reyenstol** rebeld and reyenstal lhe APK etc taym KotlinCompose code (`android/...`) ez modefayd. Flutter Hot Reload need **not** apply.
*   **Odomadek Klenup:** Always run `flutter clean` before significant builds to reclaim disk space.
*   **Kac Manedjment:** Delete old Gradle and Flutter caches. Keep only the **latest build** per module (`ime`, `kepad`, `app`).
*   **No Redundant Beldz:** Ensure each module has only one active build artifact. Get rid of anything not strictly needed for the current iteration.
1.  **Stop** the running app (Ctrl+C in terminal).
2.  **Run** `flutter build apk --debug --target-platform android-arm64 --android-skip-build-dependency-validation` (for physical device).
3.  **Instol** `adb install -r build/app/outputs/flutter-apk/app-debug.apk`.
4.  **Default Keyboard Reset:** The keypad starts in 'letter' mode by default. Persistence of mode state across sessions is disabled to ensure consistent startup behavior.
    *   **Automation:**
        ```powershell
        adb shell ime enable io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod
        adb shell ime set io.angol.dayl/com.example.angol.ime.DaylEnpitMelxod
        ```

## **spetc tu tekst**
**Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`, allowing us to respond to cursor jumps or selection changes.
**Custom Context Menu:** The 'angol' option in the text selection system menu is implemented as a translucent Activity with a loading indicator and error handling. Verified implementation in `TranslateActivity.kt`.
**Keyboard Translation:** The **AI** button in the top menu provides a reliable way to trigger translation between 'Angol' and standard English. This feature is powered by Firebase Vertex AI (Gemini 1.5 Flash). It is separate from the 'angol' mode toggle because it is an active transformation tool for existing text, rather than a typing mode. It has been moved to the top right corner to avoid overlapping with the 'p' and 'm' keys on the outer ring.

## **angol 36-karaktir fonetik lodjek**
The system uses a 1:1 mapping between **36 sounds** and **36 characters**. There are NO silent letters and NO memorized spelling rules. It is a pure sound-stream transcription.

### **konsonantz (24)**
- **Standard (18):** b, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
- **Specal (6):**
    - **c:** 'sh' sound (e.g., ci = she).
    - **tc:** 'ch' sound (e.g., tcips = chips).
    - **lx:** 'thin' (unvoiced th).
    - **lh:** 'the' (voiced th).
    - **nq:** nasal 'ng' sound (e.g., lx4nq = thing).
    - **q:** voiced velar fricative (Greek 'gamma' [ɣ]).
    - **x:** unvoiced velar fricative (Greek 'chi' [x]).
- **NG Lodjek:** Use `nq` for the nasal sound alone. Use `ng` (n + g) if a hard 'g' sound follows (e.g., 2ngAl = Angol).

### **vokalz (12)**
- **1:** /ɑ/ (pasta, mama).
- **2:** /æ/ (cat, trap).
- **3:** /ɛ/ (bed, dress).
- **4:** /ɪ/ (fit, is).
- **5:** /i/ (be, keep).
- **6:** /ɝ/ (her, bird).
- **7:** /ʊ/ or /ə/ (book, good, schwa).
- **8:** /ʌ/ (but, luck).
- **9:** /u/ (too, food).
- **0:** /oʊ/ (go, no).
- **A:** /o/ (beau, faux).
- **O:** /ɔ/ (all, tall).

### **Lodjek Padirnz:**
- **Long Vokalz:** `make` -> `m3yk`, `like` -> `l1yk`.
- **No 't' sawnd:** `exactly` -> `3gz2kl5` (if spoken without 't').
- **Voys sawnd onle!**
**angol neym melxod - pyir spelenq lodjek stradedje**
The goal is to achieve 100% consistent pirfekt conversions, prioritizing AI learning reliability over algorithmic speed. 
- Use a multi stage transformation engine in `convertToAngolSpelling`.
- Process suffixes first, then consonant clusters, then vowel shifts to avoid double-conversion.
- Maintain a small "Core Vocabulary" map for high-frequency irreducible words.
- Use Gemini for broader context/tone, but rely on Logic for spelling.
**Vowel Phonetic Mapping (Keypad Shortcuts):** pirfektle balansd 12 vowel layout (2-3-2-2-3) ring.
- **a1**: /ɑ/ (pasta, mama, spa, father, palm)
- **a2**: /æ/ (cat, bad, man, at, as, and, trap)
- **e3**: /ɛ/ (bed, get, set, end, met, red, dress)
- **e4**: /ɪ/ (fit, sit, is, sin, ship, kit, bit)
- **e5**: /i/ (be, me, see, he, tree, key, peace, keep)
- **i6**: /ɝ/ (her, bur, bird, nurse, turn, word)
- **i7**: /ʊ/ (book, good, put, full, push, foot)
- **u8**: /ʌ/ (but, cup, fun, up, us, cut, luck, strut)
- **u9**: /u/ (to, do, too, zoo, food, who, boot, flute)
- **o0**: /oʊ/ (go, no, though, boat, tone, solo)
- **oA**: /o/ (beau, faux, haut, chaud, tôt, dos)
- **oO**: /ɔ/ or /ɒ/ (all, tall, fall, on, off, odd, boss, not, log, dog)
1.  **Vowel Shifts:**
    *   Short **i** -> **e** (e.g., `it` -> `et`, `is` -> `ez`).
    *   Long **a** -> **ey** (e.g., `base` -> `beys`, `make` -> `meyk`).
    *   Long **i** -> **ay** (e.g., `I` -> `ay`, `like` -> `layk`).
    *   Long **e** -> **e** (e.g., `keep` -> `kep`).
2.  **Consonant Clusters:**
    *   **th** -> **lh** (at start) or **lx** (middle/end) (e.g., `the` -> `lha` or `lha`, `with` -> `welx`).
    *   **ch** -> **tc** (e.g., `change` -> `tceynj`, `each` -> `etc`).
    *   **sh** -> **c** (e.g., `she` -> `ce`).
    *   **j** / **soft g** -> **dj** (e.g., `logic` -> `lodjek`, `project` -> `prodjekt`).
    *   **hard c** / **ck** -> **k** (e.g., `can` -> `kan`, `back` -> `bak`).
3.  **Suffixes:**
    *   `-ing` -> `-enq`.
    *   `-tion` / `-sion` -> `-con`.
    *   Plural **s** -> **z** (when voiced).

## **olhir fetcirz**
* **Live Selekcon:** Sliding between keys deletes the previous char and types the new one (supports multi-char labels).
* **Popup Numbirz:** Pressing or sliding onto a vowel shows numbers on the outer ring.
* **Kapedolz (Capitalization):** 
* **Vejyuwalz:** automatically fit any screen size. 
* **angol mode toggle:** between Angol and Standard English spelenq for voice input az ovirley at lha top.
* **Sentir Heksagon:** voys tu teks | Mod Togil | ' ' '.' 
* **Backspace Repeat:** Deletes exactly 12 characters per tick for strings without spaces.
* **Voice PTT:** Voice input uses a robust "Push-to-Talk" interaction—starts on press, stops on release, and automatically restarts if the button is still held.
* **Automatic Prompts:** The app automatically prompts the user to enable/select the keyboard and pirmeconz on startup.
* **Build:** build for x64 and arm64.

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