**prodjekt angol**
###**arketekcir (AGP 9.2+)** Kotlin Multiplatform (onle)
*cerd laybrere***`:angolModyil`** `com.android.kotlin.multiplatform.library` kor rawdir reprezents lha 'sestom etself' ov cerd lodjek  AngolSteyt  deytabeys clawd entirfeysez (`FirebaseSirves`)  enkrepcon  and fawndeycon geytkepir yuteledez ez rekwayrd tu entsol or unenstol ol ap adonz.
    `src/commonMain/kotlin/`:
    `src/androidMain/kotlin/`: emplementeyconz (e.g. Android sirvesez  fayl storedj  TTS).
    
**:daylModyil**{`:angolModyil` + `:daylWedjet`} drag an drop fayl 'sestom program' yuzir entirfeys entirakcon lhat adz or remuvz adonz lxru angol modyil sutc az kepad apekstencon and beld apadon.
**:daylWedjet**{dependz on `:daylModyil`}  hub leyawt ov ap adonz kolekdenq arawnd angol modyil az standard yusir entirfeys entirakcon.

*ap entre***:angolDaylAp**{`:angolModyil` + `:daylModyil` + `:daylWedjet`}(`com.android.application`).
kombaynz lhes kor freymwirk welx lha fayl sestom yusir entirakcon (**dayl modyil**) and praymere hub leyawt (**dayl wedjet**).

*cerd laybrere***:kepadModyil**{`:angolModyil` + `:daylWedjet`}hawzez lha kustom heksagonal kebord gred leyawt  key lodjek  font sayzenq  and yuzir entirfeys entirakconz.
*ap entre***:kepadApekstencon**{`:angolModyil` + `:kepadModyil` + `:daylWedjet`}(`com.android.application`). redjestirz az 'system etself' enpit editor and konteynz lha `InputMethodService` emplementeycon for kebord enpit akros lhe OS.

*cerd laybrere***:rebeldModyil**{ + `:daylModyil` + `:daylWedjet`}yusir entirfeys entirakcon tu dayl modyil.
*ap entre***:rebeldApadon**{`:angolModyil` + `:kepadModyil` + `:daylModyil` + `:daylWedjet`} redjestirz az 'sestom program' awtpit editor.`com.android.kotlin.multiplatform.library` emplements lha beldir entirfeys leyawt (`rebeld`), drag an drop konfegyireycon gredz  swap vejuwalz  foldir kreyeycon lodjek  and leyawts uysoleyded frum lha produkcon leyawt.


###**funkcon lodjek:**
**ap separeycon:** 'dayl' and 'kepad' are sepret Android aps (`io.angol.dayl` and `io.angol.kepad`) tu encur sestom level enpit edetor redjestreycon and klen prodjekt strukcir.
**brodkast senk (lha bredj):** sens 'dayl' and 'kepad' hav eysoleyded storedj and 'auth' konteks, lhey kumyunekeyt veya a sekyir Android brodkast `io.angol.ACTION_UPDATE_LAYOUT`.
tceyndjez meyd en lha 'rebeld' ap (on dayl) ar not brodkast tu lha 'kepad' ap ekstencon for rel taym senkronayzeycon. et must be muvd awt ov 'rebeld' and ontu dayl tu repleys kepad.
**multay envurnment senk:** suports `kirent` and `produkcon` Firestore palxs. lha 'rebeld' updeyts bolx odomadekle tu encsur lha produkcon envuronment ez alweyz kirent.
**prevyu sirvir:** lha AI eydjent monetirz lhe awtput ov lha prevyu sirvir kontenyuwosle (e.g., konsol logz, erir mesedjez) for rel taym fedbak on tceyndjez.

##**eteratev development yuzir entirakcon**
**kumper etc rekwest tu plan and revayz plan befor refakdir.**
  1. **erir detekt and remede odomadekle** monetir erirz kontenyuwosle and rezolv (sentaks, tayp mesmatcez, null seyfde, or lintenq vayoleycunz).
  2. **lent/format:**  or `gradlew format` (ef konfegyird).
  3. **dependens tcek:** run `./gradlew build` tu manadj dependensez.
  4. **kod djenereycon:** run build_runner odomadekle ef nesesere.
  5. **kompayl & anelayz:** monetir `./gradlew analyze` and kompayleycon erorz and eksepsconz.
  6. **test eksekyucon:** monetir IDE dayagnosteks and run relevant tests.
  7. **prevyu tcek:** observ lha prevyu sirvir and tirmenal awtput for vezyual and runtaym erorz.
  8. **remedeyeyt and report:** odomeyt feks and report tu lha yuzir onle ef erir kanot rezolv odomadekle.

##**beld an deploy**:
**klen beld:** olweyz yuz `./gradlew clean` to ensure no remnants.
**enstol dayl:** yuz `enstol dayl klen` (klen beld + dep akdeveyon + lontc) or `enstol dayl` (fast updeyt + lontc).
**enstol kepad:** yuz `enstol kepad klen` (klen beld + dep akdeveyon) or `enstol kepad` (fast updeyt).
**WearOS:** yuz `enstol WearOS` tu lontc lha emyuleydir.

##**spetc tu tekst** emplemented neydev.
**kebord tranzleycon**: lhe 'angol' togil at sentir tregirz AI voys.
?**awtpit feld entirakcon**: tutcenq lhe awtpit feld kan be moneitird bay lhe enpit editor veya `onUpdateSelection`.
**angol spelenq lodjek** `36 sawndz` map 1:1 tu:
**12 vowalz**
**1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
**24 konsonants**
l lh lx h x nb, d, f, g, h, j, k, l, m, n, p, r, s, t, v, w, y, z.
**c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

## **stadus**:
- [x] **Consistent Navigation:** All modules (Dayl, Beld, Kepad) use the center hexagon tap to navigate "up" (back to Hub) when used within the Dayl app. The Kepad module's outer space tap for closing has been removed. In the IME app, the Kepad center remains a Space key.
- [x] **Hexagonal Grid Widget:** Supports long-press dragging, swapping, copying, and hover glow effects, with custom pressed states (contrast color tap) synchronized across widgets.
- [x] **kunsestent Keypad Font Size:** The keypad uses a consistent font size based on the largest label length (2 characters), scaled 1/12 larger.
- [x] **Voice Input:** Integrate conversion and AI (Gemini 3.5) refinement in `DaylEnpitMelxod.kt`.
- [x] **Auth & Hub:** Login (GitHub) and Home screens port to Kotlin Compose.
- [x] **Cloud Sync:** Module layouts synchronize with Firebase Firestore via `FirebaseSirves`.
- [x] **Beld Modyil:** Supports module editing and custom layouts.
- [x] **TTS (Text-to-Speech):** (`AndroidPlatformServices`).
- [x] **Ultimate Versions:** Kotlin 2.3.21, Compose 1.10.3, AGP 9.2.0, CompileSdk 37.
- [x] **Drag-to-Folder Logic:** Drag any module to folder, move inside, and remove from previous. Keep center drag from disappearing on main screen.
- [x] **Swap Visuals:** Short press triggers Glow, Long press triggers Contrast Color.
- [/] **Manual Copy Sync:** Support drag to center in Beld to manually copy to Dayl's keypad module, and drag to 'beld' in Dayl to copy module to 'beld'.
- [/] **Replace Same-Name Files:** Show confirmation dialog when dropping a file/module on another of the same name.