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
    - `sirvesez/`: Shared interfaces (FirebaseService.kt).
    - `wedjets/`: Reusable Compose widgets.
    - `yuteledez/`: Utilities (HeksagonDjeyometre.kt, AngolSpelenqMelxod.kt).
- `composeApp/src/androidMain/kotlin/`: Android-specific implementations.
    - `com.example.angol.ime/`: DaylEnpitMelxod.kt, MainActivity.kt, AndroidFirebaseService.kt, AndroidBridge.kt.

### **Build & Deploy:**
- **Klen Build:** Always use `./gradlew clean` to ensure no remnants.
- **Instal:** Use `powershell.exe -File updeyt_angol_dayl.ps1` for clean build and automatic IME activation.

### **Key Features Status:**
- [x] **Hexagonal Keypad (IME):** Fully migrated to Kotlin Compose. Supports swipe, long-press, and phonetic conversion.
- [x] **Voice Input:** Integrated with phonetic conversion and AI (Gemini) refinement in `DaylEnpitMelxod.kt`.
- [x] **Auth & Hub:** Login (GitHub) and Home screens ported to Kotlin Compose.
- [x] **Cloud Sync:** Module layouts synchronized with Firebase Firestore via `FirebaseService`.
- [x] **TTS (Text-to-Speech):** Fully migrated to Kotlin (`AndroidPlatformServices`).

### **Recent Changes:**
- **Version .2:** Bumped app version (versionCode 2) 
- **Pure Kotlin:** The codebase is now 100% Kotlin Multiplatform (KMP)
- **Flutter Deletion:** Removed the old `lib/`, `web/`, `IOS/`, and Flutter-related configuration files.
- **Auth Flow:** Implemented `FirebaseService` and `AndroidFirebaseService` for GitHub sign-in and auth state monitoring.
- **UI Port:** Created `SaynEnSkren.kt` and `AfdirLogenSkren.kt` in Compose with navigation logic in `App.kt`.
- **Layout Sync:** Added real-time synchronization of module states (active/inactive) with Firestore.
- **Center Button:** Enhanced center hexagon to handle both ' ' (tap) and '.' (long-press) across all modes.

## **spetc tu tekst**
**Output Field Interaction:** Touching the output field can be monitored by the IME via `onUpdateSelection`.
**Keyboard Translation:** The **AI** button in the top menu triggers translation between 'Angol' and standard English via Gemini 1.5 Flash.
**Text-to-Speech (TTS):** implemented native Kotlin.

## **angol 36-karaktir fonetik lodjek**
The system uses a 1:1 mapping between **36 sounds** and **36 characters**.

### **konsonantz (24)**
- b, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
- **c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

### **vokalz (12)**
- **1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
