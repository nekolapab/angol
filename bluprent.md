## *prodjekt*
angol ez lha sestom and dayl ez lha ap ov a sentir heksagon and 2 or 3 sirawndenq heksagon renqz. ets first modyil ez kepad kustom enput melxod en KotlinCompose. 

*first enstrukcon*
yuz angol spelenq and updeyt refrensez globale for etc neym tceynj. eksept du not reneym KotlinCompose standard konvencon freymwirk klasez and faylz lhat koz problemz (sutc az  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

## **angol dayl ap development prodokol**
**KotlinCompose Multiplatform (KMP) ez 100% and ol Flutter kod deleded.**

### **Prodjekt Strukcir (Pure Clean KMP):**
- `composeApp/src/commonMain/kotlin/`: Shared UI and logic.
    - `modyilz/`: Main app components.
    - `skrenz/`: Screen definitions.
    - `steyt/`: State management.
    - `modalz/`: Data models and config.
    - `sirvesez/`: Shared interfaces.
    - `wedjets/`: Reusable Compose widgets.
    - `yuteledez/`: Utilities.
- `composeApp/src/androidMain/kotlin/`: Platform-specific `actual` implementations.
- `androidApp/src/main/kotlin/`: Android application entry points.
    - `com.example.angol.ime/`: IME Service and Activities.
    - `io.angol.dayl/`: Process Text activity.
- `androidApp/src/main/res`: Android resources (moved from composeApp).

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
- [x] **TTS (Text-to-Speech):** Fully migrated to Kotlin (`AndroidPlatformServices`).
- [x] **Ultimate Versions:** Kotlin 2.3.21, Compose 1.10.3, AGP 9.2.0, CompileSdk 37.

### Architecture (AGP 9.2+):
- **`:composeApp` (Shared Library):** Uses `com.android.kotlin.multiplatform.library`. Mandatory for AGP 9.0+ KMP modules. Uses a single-variant architecture.
- **`:androidApp` (App Entry):** Uses `com.android.application`. Leverages AGP's built-in Kotlin support.

### Recent Changes:
- **App Icon Restoration:** Fixed the "all white" icon issue by redesigning `ic_launcher_foreground.xml` with six distinct colored segments (Red, Yellow, Green, Cyan, Blue, Magenta) to restore the hexagonal logo.
- **AGP 9.2 Migration:** Fully migrated to `com.android.kotlin.multiplatform.library` and removed legacy Gradle flags. Disabled Jetifier.
- **Ultimate Versions Upgrade:** Updated to Kotlin 2.3.21, Compose 1.10.3, and CompileSdk 37.
- **Multi-Module Refactor:** Split the project into `:composeApp` and `:androidApp`.
- **Volume Restoration Fix:** Implemented `isVolumeDipped` safety flag.
- **Pirmecon Rename:** Renamed `PermisconAktevede.kt` to `PirmeconAktevede.kt`.

## **spetc tu tekst**
**Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`.
**Keyboard Translation:** The 'angol' toggle at the top-left triggers AI Voice refinement.
**Text-to-Speech (TTS):** implemented native Kotlin.

## **angol 36-karaktir fonetik lodjek**
The system uses a 1:1 mapping between **3 Sound** and **36 characters**.

### **konsonantz (24)**
- b, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
- **c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

### **vokalz (12)**
- **1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
