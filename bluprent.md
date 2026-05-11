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
- **Instal Dayl:** Use `enstol dayl klen` (clean build + deep activation + launch) or `enstol dayl` (fast update + launch).
- **Instal Kepad:** Use `enstol kepad klen` (clean build + deep activation) or `enstol kepad` (fast update).
- **WearOS:** Use `enstol WearOS` tu launch lha emyuleydir.


### Key Features Status:
- [x] **Hexagonal Keypad (IME):** Fully migrated to Kotlin Compose. Supports swipe, long-press, and phonetic conversion.
- [x] **Voice Input:** Integrated with phonetic conversion and AI (Gemini 3.1) refinement in `DaylEnpitMelxod.kt`.
- [x] **Auth & Hub:** Login (GitHub) and Home screens ported to Kotlin Compose.
- [x] **Cloud Sync:** Module layouts synchronized with Firebase Firestore via `FirebaseSirves`.
- [x] **Beld Modyil (Builder):** Transitioned from a list-based UI to a hexagon ring layout.
- **Tap:** Rename a module or individual glyph.
- **Long-press (Vibrate):** Start drag-and-drop to swap positions or move to center for deletion.
- **Long-press again (Vibrate):** If on the same spot as a previous drag/tap, start a copy-drag to empty slots.
- **Center Navigation:** Tap the center hexagon (**dayl** or **Hub**) to go back or close the builder.
- [x] **TTS (Text-to-Speech):** Fully migrated to Kotlin (`AndroidPlatformServices`).
- [x] **Versions Updeyt:** Kotlin 2.3.+, Compose 1.10.+, AGP 9.1+, CompileSdk 36.

### Recent Changes:
- **Beld Rename:** Renamed 'beldir' module to 'beld'. Updated all UI labels and internal IDs.
- **Multi-Layout Support:** Added `type` property to modules. Users can now create and switch between multiple keypad layouts from the hub.
- **Platform-Aware Scaling:** Refactored Hub and Keypad scaling to fit tight to the screen walls. Mobile shows at least 2 rings, while WearOS shows 1 less (1 ring minimum).
- **Dynamic Fit:** The glyph editor now uses content-aware scaling to ensure all active hexagons fit on screen without clipping, regardless of where they are added.
- **Fix Text Contrast & Fitting:** Improved `HeksagonWedjet.kt` to automatically calculate high-contrast text (Black/White) based on the background luminance. Added aggressive text scaling to ensure labels fit perfectly within hexagons.
- **Restore Rainbow Colors:** Restored the full 12-color rainbow in `KepadKonfeg.kt` to resolve the "half black and white" issue in the outer ring.
- **Refactor Hexagon Colors:** Replaced the color-inversion logic in `HeksagonWedjet.kt` with a more stable background-dimming effect for pressed states. 
- **Fix Text Doubling:** Resolved an issue where voice input would double text by ensuring final results surgically replace the composing preview.
- **Script Refactor:** Renamed installation scripts to match the CLI command names: `dayl.ps1`, `kepad.ps1`, `dayl_klen.ps1`, and `kepad_klen.ps1`.
- **Angol Modes Refactor:**
    - **Of (0):** Dimmed label. Angol 2 preview → Standard English output.
    - **Black (1):** Visible label. Angol 1 preview (numbers) → Angol 2 output (standard vowels).
    - **Interaction:** Simple tap toggles between states.

## **spetc tu tekst**
**Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`.
**Keyboard Translation:** The 'angol' toggle at the top-left triggers AI Voice refinement.
**Text-to-Speech (TTS):** implemented native Kotlin.

## **angol 36-karaktir fonetik lodjek**
The system uses a 1:1 mapping between **36 sounds** and **36 characters**.

### **konsonantz (24)**
- b, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
- **c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

### **vokalz (12)**
- **1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
