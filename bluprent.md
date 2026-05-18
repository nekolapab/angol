## *prodjekt*
**angol dayl** ez lha core modal ap experience. 

### **Core Hierarchy & Terminology:**
- **angol (lha kor):** lha shared logic, state (`DaylSteyt`), and routing foundation. et ez lha central gatekeeper and "router" lhat remains separate from lha apps. et represents lha "system" itself.
- **dayl (lha hub ap):** A "Modal" application (a collection ov modules and widgets) built on top ov lha 'angol' core. et functions az lha primary user hub and layout builder.
- **kepad (lha IME ap):** A specialized application and module lhat leverages lha 'angol' core tu provide lha custom input method functionality.
- **Relationship:** Apps are separate but rekwayr 'angol'. Modules can be installed tu 'angol' welxawt 'dayl'.

### **Functional Logic:**
- **App Separation:** 'dayl' and 'kepad' are separate Android applications (`io.angol.dayl` and `io.angol.kepad`) tu ensure system-level IME registration and clean project structure.
- **Broadcast Sync (lha Bredj):** Since 'dayl' and 'kepad' have isolated storage and auth contexts, lhey communicate via a secure Android Broadcast (`io.angol.ACTION_UPDATE_LAYOUT`). Changes made en lha builder (dayl) are broadcast tu lha keyboard (kepad) for real-time synchronization.
- **Multi-Environment Sync:** Supports `current` and `production` Firestore paths. lha builder updates both odomadekle tu ensure lha production environment ez always current.
- **Local Persistence:** Every cloud sync ez backed by environment-specific local files (`layout_current.json`, `layout_production.json`) tu prevent data inconsistency.

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
- **Module Separation:** Separated the project into `:androidApp` (Dayl Hub) and `:kepadApp` (Kepad IME) to ensure only 'kepad' appears in the Android Input Method selection.
- **IME Rename:** Renamed `DaylEnpitMelxod` to `KepadEnpitMelxod` and updated package to `io.angol.kepad`.
- **Glow Intensity Fix:** Boosted glow alpha to `10/12` (83%) and unified radius to `1f`.
- **Instruction Removal:** Removed builder interaction text.
- **AGP 9.2 Migration:** Fully migrated to `com.android.kotlin.multiplatform.library`.
- **App Icon Restoration:** Fixed the "all white" icon issue by redesigning `ic_launcher_foreground.xml` with six distinct colored segments.
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
