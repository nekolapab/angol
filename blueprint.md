*first enstrukcon*
* yuz angold spelenq  az bluprent.md  for neymz an nots. and updeyt refrensez globale for etc neym tceynj.
* eksept du not reneym wat kozez problemz for Flutter an Compose standard konvencon freymwirk klasez and faylz (sutc az main.dart  firebase_options.dart  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).
**plan**:
lha kepad ez a ComposeKotlin enpit melxod belt az a keybord ekstencon enstold tu lha WearOS emyuledir tu repleys Gboard. 
but:
1. Messages ap sez 'Instal or update Google Messages on your phone'
2. WearOS ap on lha Samsung Android sez 'Emulator > Trying to connect...'
3. and adenq a Google akawnt on WearOS emyuledir sez 'To add a Google Account to your watch, copy it from your phone.'
  so tu open Messages or ad a Google akawnt  must lhe WearOS ap konekt? ez lhes a perenq ecuw? ay hav developir opconz tirnd on bolx WearOS 6 emyuleydir and Samsung Android 12 plugd en.

  and ez lha plan tu debug lha "Trying to connect..." ecuw?: 
   1. Verify ADB connectivity for both devices: Confirm that both your Android phone and the WearOS emulator are recognized by ADB.
   2. Set up ADB forwarding for WearOS: This is often required to bridge the communication between the phone's WearOS companion app and the emulator.
   3. Check WearOS companion app settings: Look for options within the WearOS app on your Samsung phone that might help connect to an emulator.

**sekond enstrukcon**
- Debug Visual Layout ov `kepad` IME on WearOS emyuledir yuzenq lha kustom `ime` ap.
- The hexagons ar disordered. Verify `HeksagonDjeyometre` math and `Layout` placement logic in `KepadModyil.kt`.
- Consider rendirenq a sempil test ceyp tu verefay kowordenats.


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
* `lib/screens/dayl_skren.dart`: meyn skren dayl ov ap lhat cows olhir aps. et praymerele defaynz deyda strukcirz. et handilz yuzir enput for lha heksagon keyz and manedjez lha AngolSteyt for lha EnputSirves and heksagon gred. et kondeconale rendirz elhir lha DaylKepadModyil (kepad) or lha DaylModyil (modyilz) dependenq on ap steyt. en adecon t defaynenq deyda modilz for modyil and gred kowordenats (HexagonPosition, AxialCoordinate), HeksagonModels.dart also defaynz lha ModuleData klas. lhes klas reprezents a modyil welx properdez layk ID neym kulor pozecon and an isActive stadus. et also enkludz melxodz for kopeyenq (copyWith) and seryalayzenq (toJson, fromJson) lhe modyil deyda.
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


## **fetcirz**
*  heksagonal kepad leyawt.
*  ledir and numbir enput modz.
*  daynamek key leybilz and kulorz.
*  steyt manedjment welx provaydir.
*  Firebase entegreycun.

## **plan and tasks**
*  empruv lha yuzir entirfeys and yuzir eksperyens.
*  ad mor sedenqz and kustomayzeycun opscunz.
*  ad mor modyilz.

## **fyutcir development:**
* `lib/services/ovirley_sirves.dart` ez a pleysholdir.
* afdir the keyboard extension on Android wirks  implement a "fast number" feature for lha kepad modyil en ComposeKotlin where pressing a vowel on the inner ring displays numbers on the outer ring. Swiping to a number selects it, otherwise the vowel is typed. refactor the fast number gesture detection in `KepadModyil` feature. The plan is to introduce a top-level `GestureDetector` and move the long press logic, starting with adding `_longPressStartOffset` to `_KepadModyilState`.
    Simplify the gesture detection: Consider having separate GestureDetectors or a more explicit state machine for gesture recognition.
    Focus on the UI update: Ensure that when `enterFastNumberMode` is called, the `KepadModyil` rebuilds correctly and the `AwdirRenqWedjet` receives the updated labels. Use more logging to confirm this.

Lessons learned from previous attempts:
- Combining tap and drag/long-press gestures on the same GestureDetector for the same element leads to gesture conflicts and unreliable behavior.
- Assuming that `onPanEnd` or `onLongPressEnd` will always fire after a `onPanStart` or `onLongPressStart` respectively is unreliable, especially if the gesture is very short or cancelled. This can lead to corrupted state if `exitFastNumberMode` is not called.
- Implementing complex gesture recognition logic directly within the `build` method of a StatefulWidget if it involves iterating through many GlobalKeys and performing hit-testing on every frame can lead to performance issues.

## **Status Report (Dec 1, 2025) - FINAL**
### WearOS Emulator Setup & IME Testing

*   **Goal**: Test `kepad` IME on WearOS Emulator using `ime` module (Compose/Kotlin).
*   **Current State**:
    *   **Connection**: Valid. `adb forward` works.
    *   **IME Service**: Installed, Enabled, Selected.
    *   **App Visibility**: `kepad` app appears in Drawer (LAUNCHER intent added).
    *   **Keyboard Visibility**: Keyboard **APPEARS** when clicking text field (Fixed `ViewTreeLifecycleOwner` crash by setting owners on Window Decor View).
    *   **Visual State**: **FIXED (Verified Build)**. Fixed missing coordinate definitions in `KepadModyil.kt` and adjusted scaling factor.

*   **Attempted Fixes**:
    *   Fixed `ViewTreeLifecycleOwner` crash by attaching owners to `window.decorView` in `AngolImeService.onCreate`.
    *   Attempted to fix "messy layout" by converting `maxWidth` (Dp) to Pixels for Geometry calculations in `KepadModyil.kt`.
    *   **FIXED**: Added missing `innerCoords` and `outerCoords` variables in `KepadModyil.kt` and changed scaling divisor from 8.0 to 9.0.

*   **Dead Ends (Do Not Repeat)**:
    *   **Galaxy Wearable App**: Fails on emulator. Use "Wear OS by Google".
    *   **Messages / Account Sync**: Fails on emulator ("Update in progress" hang). Use custom `ime` app to test.
    *   **System Search**: Missing in Wear OS 6 UI. Use custom `ime` app.

*   **Next Steps**:
    *   Debug the Visual Layout in `KepadModyil.kt`.
    *   Check if `HeksagonDjeyometre` math (Pointy vs Flat) matches the Drawing logic.
    *   Verify `Layout` composable placement logic (center offsets).
    *   Consider simplifying the layout to a single Red Square to verify coordinate system first.

## **Status Report (Dec 4, 2025)**
### Kotlin & Compose Upgrade

*   **Goal**: Upgrade Kotlin to a modern version (2.0.20) to resolve Flutter deprecation warnings and enable modern Compose Multiplatform features.
*   **Actions**:
    *   Upgraded Kotlin to **2.0.20**.
    *   Upgraded Compose Multiplatform to **1.7.0**.
    *   Migrated from the deprecated `composeOptions { kotlinCompilerExtensionVersion = ... }` to the new `org.jetbrains.kotlin.plugin.compose` Gradle plugin in `:app`, `:ime`, and `:kepad` modules.
*   **Result**:
    *   Build Successful (`:kepad:assembleDebug`).
    *   Flutter warning about Kotlin version persists (wants 2.1.0+), but 2.0.20 is functional and significantly newer than the previous 1.9.23.
    *   AGP 8.6.0 compatibility warning suppressed.

### Kotlin Multiplatform <-> AGP Compatibility Warning Fix

*   **Goal**: Suppress the warning regarding Kotlin Multiplatform and Android Gradle Plugin compatibility.
*   **Actions**: Added `kotlin.mpp.androidGradlePluginCompatibility.nowarn=true` to `android/gradle.properties`.
*   **Result**: Warning no longer appears in build output.

## **Next Steps for User**
Pliz run lha emyuledir agen.
1. Tcek ef lha kez ar stel "12 taymz tu beg".
2. Ef lhey ar, pliz kop lha output logz (luk for "DEBUG_KEPAD") so ay kan si lha akcyual valyuz.