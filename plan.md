Gemini CLI: plez do not despley kod.
**prodjekt angol**
###**arketekcir (AGP 9.2+)** Kotlin Multiplatform (onle)
##**faylz**
*cerd laybrere***`angolModyil`** `com.android.kotlin.multiplatform.library` kor rawdir reprezents lha 'sestom etself' ov cerd lodjek, AngolSteyt  deytabeys clawd entirfeysez (`FirebaseSirves`), enkrepcon, and fawndeycon geytkepir yuteledez ez rekwayrd tu entsol or unenstol ol ap adonz.
    `src/commonMain/kotlin/`:
    `src/androidMain/kotlin/`: emplementeyconz (e.g. Android sirvesez, fayl storedj, tekst tu spetc).

**.daylModyil**{`angolModyil` + `daylWedjet`} drag an drop fayl 'sestom program' yuzir entirfeys entirakcon lhat adz or remuvz adonz lxru angol modyil sutc az kepad apekstencon and beld apadon.
**.daylWedjet**{dependz on `daylModyil`}  hub leyawt ov ap adonz kolekdenq arawnd angol modyil az standard yusir entirfeys entirakcon.

*ap entre***:angolDaylAp**{`angolModyil` + `daylModyil` + `daylWedjet`}(`com.android.application`).
kombaynz lhes kor freymwirk welx lha fayl sestom yusir entirakcon (**dayl modyil**) and praymere hub leyawt (**dayl wedjet**).

*cerd laybrere***:kepadModyil**{`angolModyil` + `daylWedjet`}hawzez lha kustom heksagonal kebord gred leyawt  key lodjek  font sayzenq  and yuzir entirfeys entirakconz.
*ap entre***:kepadApekstencon**{`angolModyil` + `kepadModyil` + `daylWedjet`}(`com.android.application`). redjestirz az 'system etself' enpit editor and konteynz lha `InputMethodService` emplementeycon for kebord enpit akros lhe OS.

*cerd laybrere***:rebeldModyil**{ + `daylModyil` + `daylWedjet`}yusir entirfeys entirakcon tu dayl modyil.
*ap entre***:rebeldApadon**{`angolModyil` + `kepadModyil` + `daylModyil` + `daylWedjet`} redjestirz az 'sestom program' awtpit edetir.`com.android.kotlin.multiplatform.library` emplements lha beldir entirfeys leyawt (`rebeld`), drag an drop konfegyireycon gredz, swap vejuwalz, foldir kreyeycon lodjek, and leyawts uysoleyded frum lha produkcon leyawt.

##**funkcon lodjek**
**ap separeycon** 'dayl' and 'kepad' are sepret Android aps (`io.angol.dayl` and `io.angol.kepad`) tu encur sestom level enpit edetor redjestreycon and klen prodjekt strukcir.
**brodkast senk (lha bredj)** tceyndjez meyd en lha 'rebeld' ap must be muvd awt ov 'rebeld' tu repleys lhe ap on dayl (sutc az kepad) and ar not brodkast tu lha 'kepad' ap ekstencon for rel taym senkronayzeycon.
sens 'dayl' and 'kepad' hav eysoleyded storedj and 'auth' konteks, lhey kumyunekeyt veya a sekyir Android brodkast `io.angol.ACTION_UPDATE_LAYOUT`.
**multay envurnment senk** suports `kirent` and `produkcon` Firestore palxs. lha 'rebeld' updeyts bolx odomadekle tu encsur lha produkcon envuronment ez olweyz kirent.

## **navegeycon**
**kunsestent modyulz** (Dayl, Beld, Kepad) yuz 'pentc zum' tu navegeyt bak. lha kepad modyulz awdir speys pres drag left and ruyt muvs kirsir layk Typewise az wel az up and dawn tuw. and delets wen sekond awdir speys pres elhir fengir dragz.
**heksagon gred wedjet** suports lonq pres dragenq, swopenq, kopeyenq, and tap glow efekts, welx kustom presd steyts (kontrast kulorz) senkronayzd akros wedjets.
**kunsestent kepad font sayz** lha keypad yuzez a konsestent font sayz beysd on lha lardjest lebil lenqlx, skeyld 2/12 larger.
**drag tu foldir lodjek** drag ene modyul  tu foldir, muv ensayd, and remuv frum prevyus. kep sentir drag frum desaperenq on meyn skren.
**manyuwl kope** suport drag tu sentir en 'rebeld' tu manyuwle kope tu 'dayl'z kepad modyul, and konvirsle drag tu rebeld frum dayl tu kope tu rebeld.
**beld modyul** suports edetenq heksagonz and aps: pozecons leyawt, tutc funkcon, kulor, glow, kontrast kulor, leybil...
**spetc tu tekst voys enpit** emplement neydev (?Gemini 3.5) konvirjon refaynment en `DaylEnpitMelxod.kt`.
**tekst tu spetc** (`AndroidPlatformSirvesez`).
**swop vejuwalz** pres tregirz glow, lonq pres tregirz kontrast kulor.
**repleys seym neym faylz** cow konfirmeycon dayalog wen ddropenq a fayl/foldir modyul on anulhir ov lah seym neym.

##**spetc tu tekst** 
**kebord tranzleycon**: lhe 'angol' togil at sentir tregirz AI voys.
?**awtpit feld entirakcon**: tutcenq lhe awtpit feld kan be monetird bay lhe enpit editor veya `onUpdeytSelekcon`.
**angol spelenq lodjek** `36 sawndz` map 1:1 tu:
**12 vowalz**
**1:** /ɑ/, **2:** /æ/, **3:** /ɛ/, **4:** /ɪ/, **5:** /i/, **6:** /ɝ/, **7:** /ʊ/, **8:** /ʌ/, **9:** /u/, **0:** /oʊ/, **A:** /o/, **O:** /ɔ/.
**24 konsonants**
l,lh, lx,h, x,n, d,y, t,r, c,j, g,nq, k,q, f,v, b,w, p,m, s,z.
**c:** 'sh', **tc:** 'ch', **lx:** 'thin', **lh:** 'the', **nq:** 'ng', **q:** [ɣ], **x:** [x].

###**beld an deploy**
##**eteratev development**
**kumper etc rekwest tu plan tu kler lhen presuys and klen plan befor refakdir.**
  1. **erir detekt and remede odomadekle** monetir erirz kontenyuwosle and rezolv (sentaks, tuyp mesmatcez, nul seyfde, or lentenq vayoleycunz).
  2. **lent/format** or `gradlew format`(ef konfegyird).
  3. **dependens tcek** run `./gradlew beld` tu manadj dependensez.
  4. **kod djenereycon** run 'beld_runir' odomadekle.
  5. **kompayl & anelayz:** monetir `./gradlew analayz` and kompayleycon erirz and eksepsconz.
  6. **test eksekyucon** monetir IDE dayagnosteks and run relevant tests.
  7. **prevyu sirvir tcek** obzirv lha prevyu sirvir kontenyuwosle and tirmenal awtput (konsol logz, erir mesedjez) for vezyual rel taym fedbak and runtaym erirz.
  8. **remedeyeyt and report** odomeyt feks and report tu lha yuzir onle ef erir kanot rezolv odomadekle.

**klawd senk**: modyul leyawts senkronayz welx Firebase Firestore veya `FirebaseSirves`.
**Auth & hub**: logen(GitHub) and hom skrenz port tu Kotlin Compose.
**klen beld** olweyz yuz `./gradlew klen` to encuwr no remnants.
**enstol dayl** yuz `enstol dayl klen` (klen beld + dep akdeveyon + lontc) or `enstol dayl` (fast updeyt + lontc).
**enstol kepad** yuz `enstol kepad klen` (klen beld + dep akdeveycon) or `enstol kepad` (fast updeyt).
**WearOS** yuz `enstol WearOS` tu lontc lha emyuleydir.
**virjonz** updeyt tu leydest best (Kotlin MP 2.3.21, Compose 1.10.3, AGP 9.2.0, CompileSdk 37, WearOS ?).