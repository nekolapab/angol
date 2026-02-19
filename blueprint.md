*first enstrukcon*
* yuz angold spelenq  az bluprent.md  for neymz an nots. and updeyt refrensez globale for etc neym tceynj.
* eksept du not reneym wat kozez problemz for Flutter an Compose standard konvencon freymwirk klasez and faylz (sutc az main.dart  firebase_options.dart  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

**sekond enstrukcon**

## **Dev Prodokol (Compose IME)**

### **onle Compose!**
* **onle lha Compose virjon ov kepad ez tu be yuzd!** do not yuz lha Flutter virjon for lha enpit melxod.
* **REMOVED:** lha entirnal Flutter keypad overlay haz bin removed from `DaylSkren.dart`.
* we ar naw yuzenq `ComposeMainActivity` tu test lha kepad dayrektle welxen lha Android part ov lha prodjekt.

### **Fast Reyenstol (Compose Native)**
When modifying Kotlin/Compose code (`android/...`), you must rebuild and reinstall the APK. Flutter Hot Reload does **not** apply.

**Protocol:**
1.  **Stop** the running app (Ctrl+C in terminal).
2.  **Run** `flutter build apk --debug --target-platform android-arm64 --android-skip-build-dependency-validation` (for physical device).
3.  **Instol** `adb install -r build/app/outputs/flutter-apk/app-debug.apk`.
4.  **Default Keyboard Reset:**
    *   **Automation:**
        ```powershell
        adb shell ime enable com.example.myapp/com.example.angol.ime.AngolImeService
        adb shell ime set com.example.myapp/com.example.angol.ime.AngolImeService
        ```

### **Fixes (Feb 13 2026)**
*   **Kotlin Downgrade:** fixed `speech_to_text` and Compose compiler conflicts by downgrading to Kotlin `1.9.23` and Compose `1.6.11`.
*   **Removed speech_to_text:** Removed unused and incompatible `speech_to_text` dependency.
*   **Target Platform:** Build now targets `android-arm64` for physical devices.
*   **Heksagonal Gred:** lha heksagonz ar naw korrektle pozecond en lha Compose virjon.
*   **Live Selekcon:** (wirkenq on et) swaypenq betwin keyz cid deled lha prevyus tcar and ad lha nuw on.

## **plan**
* lha kepad ez a Compose aplekeycon lhat emplements a kustom enput melxod beyst on a heksagonal gred.
* The `ime` module is now a library integrated into the main Flutter app.
* Debug Visual Layout ov `kepad` IME on WearOS emyuledir yuzenq `ComposeMainActivity`.
* Verify `HeksagonDjeyometre` math and `Layout` placement logic in `KepadModyil.kt`.

## **stadus**
* **Compose Keypad:** wirkenq and pozecond korrektle!
* **IME Sirves:** enabled and set az defolt.
* **Live Selekcon:** implemented and working! Sliding between keys deletes the previous char and types the new one (supports multi-char labels).
* **Popup Numbirz:** implemented! Pressing or sliding onto a vowel shows numbers on the outer ring with a 1.25x scaling "popup" effect.
* **Kapedolz (Capitalization):** Fixed! Now uses relative `initialY` for each key and 0.4f threshold.
* **Sentir Hex (Enter & Toggle):** Fixed! Robust `\n` commit and faster 300ms "peek" mode toggle (returns to original mode before release).
* **Backspace Repeat:** Fixed! Deletes exactly 12 characters per tick for strings without spaces, and caps word deletion at 12.
* **Build:** suksesful build for x64 and arm64.

## **tasks (for Gemini)**
1.  **DONE - Emplement Live Selekcon:** updeyt `KepadModyil.android.kt` tu deled prevyus tcar wen swaypenq tu a nuw key.
2.  **DONE - Popip Numbirz:** emplement popup numbirz en Compose wer presenq vowelz on lha enir renq despleyz numbirz on lha awdir renq.
3.  **DONE - Kapedolz:** emplement "Swayp Up" for capitalization en `KepadModyil.android.kt` welx improved 0.4f threshold.
4.  **DONE - Sentir Hex Enhancements:** emplement newline (Enter) and temporary mode toggle on 1s hold.
5.  **DONE - Fix Capitalization, Enter, and Retoggle:** Improved gesture reliability and service-level compatibility.
6.  **DONE - Refine Backspace and Toggle Peek:** exactly 12-char deletion and faster 300ms peek-back toggle.
7.  **Refine Fast Number Gesture:** (Next step) consider a top-level `GestureDetector` to further unify the gesture handling if needed.

## **Nots**
* **Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`, allowing us to respond to cursor jumps or selection changes.
* **Custom Context Menu:** We can add "Angol" commands (like Translate) to the system's text selection menu (Cut/Copy/Paste toolbar) by registering a `PROCESS_TEXT` intent handler in the Android app.

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