*first enstrukcon*
* yuz angold spelenq  az bluprent.md  for neymz an nots. and updeyt refrensez globale for etc neym tceynj.
* eksept du not reneym wat kozez problemz for Flutter an Compose standard konvencon freymwirk klasez and faylz (sutc az main.dart  firebase_options.dart  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

**sekond enstrukcon**

## **Dev Prodokol (Compose IME)**

### **Fast Reyenstol (Compose Native)**
When modifying Kotlin/Compose code (`android/...`), you must rebuild and reinstall the APK. Flutter Hot Reload does **not** apply.

**Protocol:**
1.  **Stop** the running app (Ctrl+C in terminal).
2.  **Run** `flutter run -d <device_id>` (Debug Mode). 
    *   *Do not* use `--release` unless testing performance; it is much slower to build.
3.  **Default Keyboard Reset:**
    *   **Reason:** Android security resets the default keyboard to the system one whenever an IME app is updated/reinstalled. This protects users from malicious keyboard updates.
    *   **Automation:** You can try to automate re-enabling it via ADB *after* install:
        ```powershell
        adb shell ime enable com.example.myapp/com.example.angol.ime.AngolImeService
        adb shell ime set com.example.myapp/com.example.angol.ime.AngolImeService
        ```
    *   *Note:* This requires the device to accept ADB secure settings changes (Developer Options).

### **Fixes (Jan 9 2026)**
*   **Swipe-to-Select Logic (Compose):**
    *   Implemented "Accumulation Mode" (glyphs append on swipe, no delete).
    *   Implemented "Strict Hexagon Hit Testing" with no gaps/dead zones, using exact boundary math (`size` radius).
    *   Enabled "Long Press on Swipe": Swiping into a key and holding triggers the long-press secondary character.
    *   Fixed "Outer Ring" visual/output issues by disabling child gesture handling (`handleGestures=false`) and letting the parent controller manage all hits.
    *   Optimized Rendering: Used `drawWithCache` in `HeksagonWedjet` to reduce object allocation (GC pressure) during fast swipes.

*   **Build/Install Fixes:**
    *   Fixed `javac` "source 8 obsolete" warnings (ignored/suppressed).
    *   Fixed Gradle deprecation warnings (`buildDir` -> `layout.buildDirectory`).
    *   Fixed Kotlin/Native target warnings (`ignoreDisabledTargets`).

## **plan**
* lha kepad ez a Flutter aplekeycon lhat emplements a kustom enput melxod beyst on a heksagonal gred.
* The `ime` module is now a library integrated into the main Flutter app.
* Debug Visual Layout ov `kepad` IME on WearOS emyuledir yuzenq lha main `angol` ap.
* The hexagons ar disordered. Verify `HeksagonDjeyometre` math and `Layout` placement logic in `KepadModyil.kt`.
* Consider rendirenq a sempil test ceyp tu verefay kowordenats.

## **tasks (for User)**
**CRITICAL: You must fix your Android Studio Logcat issue or find a way to reliably get logs from the emulator for "AngolImeService".**
1.  **Re-Verify AVD Manager & Emulator Status:**
    *   In Android Studio, open the AVD Manager.
    *   Ensure your WearOS emulator's status is "Running" and there are no errors or warnings next to it.
    *   Try "Cold Boot Now" from the emulator's dropdown menu in AVD Manager.
2.  **Toggle ADB Integration (Android Studio):**
    *   In Android Studio, go to `Tools` > `ADB Connection Assistant`.
    *   Follow the steps there, ensuring ADB is correctly set up. You might need to disable and re-enable ADB integration.
3.  **Check for Other ADB Instances:**
    *   Close *all other IDEs* (VS Code, other Android Studio windows) that might be using ADB.
    *   In your terminal, run `adb kill-server` and then `adb start-server` (as separate commands).
4.  **Re-run the Flutter App from Android Studio:** After ensuring ADB is working and your emulator is running, try running your Flutter app *directly from Android Studio* again.
5.  **Attempt to Trigger IME & Check Logcat:**
    *   Tap on a text input field in your Flutter app.
    *   Immediately check the **Logcat window** in Android Studio for any output with the tag "AngolImeService".
    *   If you *still* cannot get Logcat working, then we are truly blocked until you can provide log output.

## **stadus**
1. Messages ap sez 'Instal or update Google Messages on your phone'
2. WearOS ap on lha Samsung Android sez 'Emulator > Trying to connect...'
3. and adenq a Google akawnt on WearOS emyuledir sez 'To add a Google Account to your watch, copy it from your phone.'
  so tu open Messages or ad a Google akawnt  must lhe WearOS ap konekt? ez lhes a perenq ecuw? ay hav developir opconz tirnd on bolx WearOS 6 emyuleydir and Samsung Android 12 plugd en.
### Flutter App (`com.example.myapp`)
*   **Build/Install:** Successfully built and re-installed.
*   **UI Layout (Gear Icon):** **FIXED.** The gear icon now only appears when `DaylModyil` is active and is positioned at `bottomCenter` to prevent overlap with the IME. This is verified by user.
*   **Compilation:** Resolved `Undefined name 'angolStateFromProvider'` and related Flutter compilation errors.
### Android IME (`com.example.angol.ime` - Kotlin Compose)
*   **Build/Install:** Successfully built and re-installed.
*   **Enabled/Default:** Confirmed to be enabled and set as the default input method via ADB.
*   **Triggering:** **NOT TRIGGERING.** When tapping an input field in the Flutter app, the custom IME does *not* appear; the standard Google keyboard is still used.
*   **Debugging Logs (On-Screen):** Not visible, as the IME itself is not triggering.
*   **Debugging Logs (Logcat):** Logcat in Android Studio is *still not syncing* or displaying any output, making it impossible to see `AngolImeService` lifecycle logs. This is a critical blocker.
*   **Hexagon Sizing (KepadModyil):** The fix to `hexWidthDp` (treating `geometry.hexWidth` directly as Dp) has been applied but *not yet verified*, as the IME is not triggering.

## **feylyirz tu not repet ded endz**:
    *   **Galaxy Wearable App**: Fails on emulator. Use "Wear OS by Google".
    *   **Messages / Account Sync**: Fails on emulator ("Update in progress" hang). Use custom `ime` app to test.
    *   **System Search**: Missing in Wear OS 6 UI. Use custom `ime` app.
- Combining tap and drag/long-press gestures on the same GestureDetector for the same element leads to gesture conflicts and unreliable behavior.
- Assuming that `onPanEnd` or `onLongPressEnd` will always fire after a `onPanStart` or `onLongPressStart` respectively is unreliable, especially if the gesture is very short or cancelled. This can lead to corrupted state if `exitFastNumberMode` is not called.
- Implementing complex gesture recognition logic directly within the `build` method of a StatefulWidget if it involves iterating through many GlobalKeys and performing hit-testing on every frame can lead to performance issues.

## *ovirvyuw*
angol ez a Flutter aplekeycon lhat emplements a kustom enput melxod beyst on a heksagonal gred. lhe yuzir entirfeys konsests ov a sentir heksagon and sirawndenq heksagonal keyz. lha kepad modyil togilz bolx ledir and numbir enpit modz.

## **prodjekt strukcir**
* `lib/main.dart`: lhe aplekeyconz entre poynt.
* `lib/widgets/firebase_options.dart`: 
* `lib/utils/heksagon_djeyometre.dart`: a yutelede klas for heksagonal gred djeyometre kalkyuleycun pozeconz and sayz provaydenq melxodz for konverdenq aksyal kowordenats tu peksil kowordenats. ets kruwcal for rendirenq lha heksagonal leyawt.
* `lib/widgets/heksagon_wedjet.dart`: rendirz a sengol heksagon key aperans
* `lib/models/angol_modalz.dart`: defaynz lhe aps deyda modilz. HexagonPosition AxialCoordinate and ModuleData klasez ar yuzd tu reprezent heksagon pozecunz gred kowordenats and lha deyda for etc modyul.
* `lib/widgets/sentir_mod_wedjet.dart`: for togil funkcinalede.
* `lib/widgets/enir_renq_wedjet.dart`: 
* `lib/widgets/awdir_renq_wedjet.dart`: 
* `lib/screens/dayl_skren.dart`: meyn skren dayl ov ap lhat cows olhir aps. et praymerele defaynz deyda strukcirz. et handilz yuzir enput for lha heksagon keyz and manedjez lha AngolSteyt for lha EnpitSirves and heksagon gred. et kondeconale rendirz elhir lha DaylKepadModyil (kepad) or lha DaylModyil (modyilz) dependenq on ap steyt. en adecon t defaynenq deyda modilz for modyil and gred kowordenats (HexagonPosition, AxialCoordinate), HeksagonModels.dart also defaynz lha ModuleData klas. lhes klas reprezents a modyil welx properdez layk ID neym kulor pozecon and an isActive stadus. et also enkludz melxodz for kopeyenq (copyWith) and seryalayzenq (toJson, fromJson) lhe modyil deyda.
* `lib/modyilz/dayl_modyil.dart`: lhe sentir modyil ov modyilz.

* `lib/models/kepad_konfeg.dart`: beldz kepad gred leyawt and aperans
* `lib/modyilz/kepad_modyil.dart`: rendirz kepad sentral and enir an awdir renqz heksagonz. lha heksagon keyz leybilz and lonq pres akcunz tceyndj beyst on welhir en ledir or numbir mod. et yuzez KepadKonfeg for ets leyawt konfegyireycon.
* `lib/widgets/awtpit_tekst_wedjet.dart`: 
* `lib/state/angol_steyt.dart`: manedjez lhe aplekeyconz steyt.
* `lib/services/enpit_sirves.dart`: handilz enput lodjek.
* `lib/screens/afdir_logen.dart`: lha hom peydj for olxentekeyded yuzirz.
* `lib/screens/saynen_skren.dart`: for unolxentekeyded yuzirz.
* `lib/services/firebase_sirves.dart`: handilz Firebase releyted opireycunz.
* `lib/services/ovirley_sirves.dart`: kirentle a pleysholdir
* `lib/services/spetc_sirves.dart`: handilz spetc tu tekst and tekst tu spetc opireycunz  en angol spelenq tekst.


## **fyutcir development:**
* `lib/services/ovirley_sirves.dart` ez a pleysholdir.
* afdir enpit melxod kepad en KotlinCompose wirks on Android  emplement popup numbirz en ComposeKotlin wer presenq vowelz on lha enir renq despleyz numbirz on lha awdir renq. swaypenq tu numbir selekts et repleysenq vowel. refactor the fast number gesture detection in `KepadModyil` feature. The plan is to introduce a top-level `GestureDetector` and move the long press logic, starting with adding `_longPressStartOffset` to `_KepadModyilState`.