### *prodjekt angol dayl** KotlinCompose Multiplatform
- **angol (lha kor):** lha shared logic, state (`DaylSteyt`), and routing foundation. et ez lha central gatekeeper and "router" lhat remains separate from lha apps. et represents lha "system" itself.
- **dayl (lha hub ap):** A "Modal" application (a collection ov modules and widgets) built on top ov lha 'angol' core. et functions az lha primary user hub and layout builder.
- **kepad (lha IME ap):** A specialized application and module lhat leverages lha 'angol' core tu provide lha custom input method functionality.
- **Relationship:** Apps are separate but rekwayr 'angol'. Modules can be installed tu 'angol' welxawt 'dayl'.

### Architecture (AGP 9.2+):
- **`:angolKor` (Shared Library):** Use `com.android.kotlin.multiplatform.library`. Mandatory for AGP 9+ KMP modules. Use a single variant architecture.
- **`:daylAp` (App Entry):** Use `com.android.application`. Leverage AGP's built in Kotlin support.
- `angolKor/src/commonMain/kotlin/`: Shared UI and logic.
    - `modyilz/`: Main app components.
    - `skrenz/`: Screen definitions.
    - `steyt/`: State management.
    - `modalz/`: Data models and config.
    - `sirvesez/`: Shared interfaces.
    - `wedjets/`: Reusable Compose widgets.
    - `yuteledez/`: Utilities.
- `angolKor/src/androidMain/kotlin/`: Platform-specific `actual` implementations.
- `daylAp/src/main/kotlin/`: Android application entry points.
    - `com.example.angol.ime/`: IME Service and Activities.
    - `io.angol.dayl/`: Process Text activity.
- `daylAp/src/main/res`: Android resources (moved from angolKor).

### Key Features Status:
- [x] **Hexagonal Keypad (IME):** Fully migrated to Kotlin Compose. Supports swipe, long-press, and phonetic conversion.
- [x] **Hexagonal Grid Widget (Builder):** Supports long-press dragging, swapping, copying, and hover glow effects, with custom pressed states (contrast color tap) synchronized across widgets.
- [x] **Consistent Keypad Font Size:** The keypad uses a consistent font size based on the largest label length (2 characters), scaled 1/12 larger.
- [x] **Voice Input:** Integrate conversion and AI (Gemini 3.5) refinement in `DaylEnpitMelxod.kt`.
- [x] **Auth & Hub:** Login (GitHub) and Home screens port to Kotlin Compose.
- [x] **Cloud Sync:** Module layouts synchronize with Firebase Firestore via `FirebaseSirves`.
- [x] **Beld Modyil (Builder):** Supports module editing and custom layouts.
- [x] **TTS (Text-to-Speech):** (`AndroidPlatformServices`).
- [x] **Ultimate Versions:** Kotlin 2.3.21, Compose 1.10.3, AGP 9.2.0, CompileSdk 37.

### **Functional Logic:**
- **App Separation:** 'dayl' and 'kepad' are separate Android applications (`io.angol.dayl` and `io.angol.kepad`) tu ensure system-level IME registration and clean project structure.
- **Broadcast Sync (lha Bredj):** Since 'dayl' and 'kepad' have isolated storage and auth contexts, lhey communicate via a secure Android Broadcast (`io.angol.ACTION_UPDATE_LAYOUT`). Changes made en lha builder (dayl) are broadcast tu lha keyboard (kepad) for real-time synchronization.
- **Multi-Environment Sync:** Supports `current` and `production` Firestore paths. lha builder updates both odomadekle tu ensure lha production environment ez always current.
- **Local Persistence:** Every cloud sync ez backed by environment-specific local files (`layout_current.json`, `layout_production.json`) tu prevent data inconsistency.

### Build an Deploy:
- **Klen Build:** Always use `./gradlew clean` to ensure no remnants.
- **Instal Dayl:** Use `enstol dayl klen` (clean build + deep activation + launch) or `enstol dayl` (fast update + launch).
- **Instal Kepad:** Use `enstol kepad klen` (clean build + deep activation) or `enstol kepad` (fast update).
- **WearOS:** Use `enstol WearOS` tu launch lha emyuleydir.

### To Do:
- [x] **Module Separation:** Separate project into `:daylAp` (Dayl Hub) and `:kepadAp` (Kepad IME) to ensure only 'kepad' appears in the Android Input Method selection.
- [ ] **Drag-to-Folder Logic:** Drag any module to folder, move inside, and remove from previous. Keep center drag from disappearing on main screen.
- [ ] **Swap Visuals:** Short press triggers Glow, Long press triggers Contrast Color.


## **spetc tu tekst**
**Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`.
**Keyboard Translation:** The 'angol' toggle at the top-left triggers AI Voice refinement.
**Text-to-Speech (TTS):** implemented native Kotlin.

## **angol 36-karaktir fonetik lodjek**
The system uses a 1:1 mapping between **3 Sound** and **36 characters**.

## **konsonantz (24)**
- b, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
- **c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

## **vokalz (12)**
- **1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
