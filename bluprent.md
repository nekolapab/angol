## *prodjekt*
angol ez lha sestom and dayl ez lha ap ov a sentir heksagon and 2 or 3 sirawndenq heksagon renqz. ets first modyil ez kepad kustom enput melxod en KotlinCompose. 

*first enstrukcon*
yuz angol spelenq and updeyt refrensez globale for etc neym tceynj. eksept du not reneym KotlinCompose standard konvencon freymwirk klasez and faylz lhat koz problemz (sutc az  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

## **angol dayl ap development prodokol**
**KotlinCompose Multiplatform (KMP) ez 100% and ol Flutter kod deleded.**

### **Prodjekt Strukcir (Pure Clean KMP):**
- `composeApp/src/commonMain/kotlin/`: Shared UI and logic.
    - `modyilz/`: Main app components (App.kt, KepadModyil.kt, DaylModyil.kt).
    - `skrenz/`: Screen definitions (DaylSkren.kt, SaynEnSkren.kt, AfdirLogenSkren.kt).
    - `steyt/`: State management (DaylSteyt.kt).
    - `modalz/`: Data models and config (AngolModalz.kt, KepadKonfeg.kt).
    - `sirvesez/`: Shared interfaces (FirebaseSirves.kt).
    - `wedjets/`: Reusable Compose widgets.
    - `yuteledez/`: Utilities (HeksagonDjeyometre.kt, AngolSpelenqMelxod.kt).
- `sirvesez/`: Shared interfaces (FirebaseSirves.kt).
- `com.example.angol.ime/`: DaylEnpitMelxod.kt, MeynAktevede.kt, AndroidFirebaseSirves.kt, AndroidBredj.kt, PermisconAktevede.kt.
- `io.angol.dayl/`: TranzleytAngol.kt

### Build & Deploy:
- **Klen Build:** Always use `./gradlew clean` to ensure no remnants.
- **Instal:** Use `enstol klen` (clean build + deep activation) or `enstol fast` (fast update).
- **WearOS:** Use `enstol WearOS` tu launch lha emyuleydir.


### Key Features Status:
- [x] **Hexagonal Keypad (IME):** Fully migrated to Kotlin Compose. Supports swipe, long-press, and phonetic conversion.
- [x] **Voice Input:** Integrated with phonetic conversion and AI (Gemini 3.1) refinement in `DaylEnpitMelxod.kt`.
- [x] **Auth & Hub:** Login (GitHub) and Home screens ported to Kotlin Compose.
- [x] **Cloud Sync:** Module layouts synchronized with Firebase Firestore via `FirebaseSirves`.
- [x] **Beldir Modyil (Builder):** Transitioned from a list-based UI to a hexagon ring layout.
- **Tap:** Rename a module or individual glyph.
- **Long-press (Vibrate):** Start drag-and-drop to swap positions or move to center for deletion.
- **Long-press again (Vibrate):** If on the same spot as a previous drag/tap, start a copy-drag to empty slots.
- **Center Navigation:** Tap the center hexagon (**dayl** or **Hub**) to go back or close the builder.
- [x] **TTS (Text-to-Speech):** Fully migrated to Kotlin (`AndroidPlatformServices`).
- [x] **Versions Updeyt:** Kotlin 2.3.+, Compose 1.10.+, AGP 9.1+, CompileSdk 36.

### Recent Changes:
- **Microphone (Press-to-Talk):** Fixed by adding `<queries>` to `AndroidManifest.xml` and implementing runtime permission requests in `DaylEnpitMelxod.kt` (using `PermisconAktevede.kt`).
- **Script Renames:** Renamed `angol.bat` tu `enstol.bat`. Remapped commands: `updeyt` -> `fil`, `beld` -> `updeyt`, and `launch` -> `WearOS`.
- **Update Script:** Renamed `updeyt_dayl_sempil.ps1` and updated to use `install -r` for in-place updates.
- **Phonetic Cleanup:** Renamed `FirebaseService` to `FirebaseSirves` and `PermissionActivity` to `PermisconAktevede`.
- **Hexagon Glefz:** Added `glefz` list to `ModyilDeyda` and implemented `reneymGlef` and `swopGlefz` in `DaylSteyt`.
- **Builder UI:** Updated `BeldirModyil.kt` with a sub-screen for editing individual hexagon labels and swapping their positions via a simplified drag-and-drop (long-press to start, tap target to swap).
- **Keypad Sync:** `KepadModyil` now prioritizes custom `glefz` labels if they are provided by the active module.
- **Beldir Modyil:** Implemented `BeldirModyil.kt` for copying, deleting, and renaming modules.
- **State Management:** Added `kopeModyil`, `deletModyil`, and `reneymModyil` to `DaylSteyt.kt`.
- **UI Integration:** Integrated `BeldirModyil` into `DaylSkren.kt` with automatic Firebase sync.
- **AI Entegreycon:** Migrated to `firebase-ai` SDK and `gemini-3.1-flash`.
- **Top Menu Mirjd:** 'angol' toggle at top-left handles toggle (tap) and AI Voice (long-press). Redundant top-right AI button removed.
- **Geometry Fix:** Added missing `getAwdirRenqKowordenats` to `HeksagonDjeyometre.kt` and resolved build failures in `KepadModyil` and `AwdirRenqWedjet`.
- **Geometry Fix:** Fixed hex positioning to correctly honor `geometry.sentir` offset.
- **Dependency Migration:** Switched from `firebase-vertexai` to `firebase-ai` and updated to latest Firebase BoM.
- **IME Fix:** Prioritized "keypad" module at position 1 and "dayl" at position 7 to ensure the keypad is the default screen for the IME extension.
- **State Logic Restoration:** Restored `swopModyilz` and `swopGlefz` methods in `DaylSteyt.kt` and updated UI callers.
- **UI Improvement:** Replaced circle borders with a "soft glow" effect (radial gradient) in `HeksagonGred`. Opacity set to 1/12 (MOVE/hover) and 4/12 (COPY).
- **Geometry & Platform Fix:** Added `getAwdirRenqKowordenats` and implemented cross-platform `getCurrentTimeMillis()` to fix build failures.
- **Squeeze & Shift Interaction:** Replaced "Swap" logic with a circular shift behavior. Moving an item now "squeezes" it into the target slot, pushing other items in the ring to fill the original gap.
- **Deletion Refactor:** Dragging an item to the center now "sends it back" (deletes it from the ring) and automatically collapses the remaining items to maintain a continuous layout.
- **Stable Center Refactor:** Refactored the hexagonal grid logic to use **Index 0** as the stable center for both data (`glefz`) and UI (`HeksagonGred`).

## **Plan and steps for kurent rekwest**
- [x] **Research:** Verify `HeksagonGred.kt` interaction logic vs `bluprent.md`.
- [x] **Strategy:** Implement circular "Squeeze & Shift" logic for glyphs and modules.
- [x] **Execution:**
    - [x] Update `DaylSteyt.kt` with `muvGlef` and `muvModyil` (remove and insert at new index).
    - [x] Update `muvGlefTuHub` and `muvModyilTuParent` to collapse rings after deletion.
    - [x] Update `HeksagonGred` to support insertion-based movement (`onMove`).
    - [x] Improve `HeksagonGred` drop detection to handle "between-hex" insertion points.
    - [x] Fix color persistence during all move and copy operations.
- [x] **Validation:** Run build and verify smooth insertion and collapse behavior.

## **spetc tu tekst**
**Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`.
**Keyboard Translation:** The 'angol' toggle at the top-left triggers AI Voice refinement via long-press.
**Text-to-Speech (TTS):** implemented native Kotlin.

## **angol 36-karaktir fonetik lodjek**
The system uses a 1:1 mapping between **36 sounds** and **36 characters**.

### **konsonantz (24)**
- b, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
- **c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

### **vokalz (12)**
- **1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
 /oʊ/, **A:** /o/, **O:** /ɔ/.
ʊ/, **A:** /o/, **O:** /ɔ/.
