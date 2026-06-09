**prodjekt angol**
###**arketekcir**`Kotlin Multiplatform`onle (AGP 9.2+)
**angolModjul**enkludz`angolWedjet`*cerd laybrere*(`com.android.kotlin.multiplatform.library`) kor rawdir reprezents lha fawndeycon geytkepir 'sestom etself' ov cerd lodjek, sekyir tranzakconz, enkrepcon, AngolSteyt, deydabeys clawd entirakconz (`FirebaseSirves`), paslok, and yuteledez rekwayrd tu entsol-unenstol ol ap adenz.  kopeleft alon open sors.
    `src/commonMain/kotlin/`:
    `src/androidMain/kotlin/`: emplementeyconz (Android sirvesez, fayl storedj, tekst tu spetc).
**angolWedjet**dependz on`angolModjul`entirfeys.

**angolDaylAp**{`angolModjul`+`daylModjul`+rapir}*ap entre*(`com.android.application`) redjestirz az sestom modjul awtpit edetir.
**daylModjul**enkludz`daylWedjet`drag an drop modjul awtpit edetir. ap lontchir enir renq resent an awdir most yuzd. modjul ap adenz drag an drop kope-remuv entirakcon leyawts lxru angol modjul.
  -angol or dayl or poyntirModyil? atrebyuts: poyntir|kirsir, tutc (pres, tap, lonq pres, unpres, klek, multe), hapdek, popup, pentc zum, roteyt, glow, kontrast kulor, huvir, togil: yunet, neym, me-yu, angol 1-2, |—.
**daylWedjet**dependz on`daylModjul`hub entirfeys leyawt ov ap adenz arawnd angol modjul.

**angolKepadAp**{`angolModjul`+`kepadModyil`+`daylModjul`+rapir} ekstencon redjestirz az`InputMethodService`OS sestom enpit edetir.
**kepadModyil**hawzez lha kustom heksagon kebord entirakconz, gred leyawt, key lodjek, font sayzenq.

**angolRebeldAp**{`angolModjul`+`rebeldModyil`+`daylModjul`+rapir} redjestirz az sestom modyil awtpit edetir.
**rebeldModyil**modyilz drag an drop and kope an delet entirakcon leyawts uysoleyded frum lha produkcon leyawt dayl modjul konfegyireycon gredz, swap vejuwalz, foldir kreyeycon lodjek, and atrebyuts.
**beldWedjet**cerd for rebeld and beldir +- mirdj rebeld and beldir atrebyuts.
**beldirModyil**ensayd rebeldModyil, tulz meyk ceyps sutc az glefs.

 // | kalkyu(spredcet) | eyay eydjents [fon] | klok | kumpas | elekt  | yecuw wirdz | tcekirz | kemekal | myuzek | lxirmostat

##**funkcon lodjek**
**ap separeycon**'dayl' and 'kepad' ar sepret aps (`io.angol.dayl` and `io.angol.kepad`) for sestom level enpit edetir redjestreycon and klen prodjekt strukcir.
**brodkast senk (lha bredj)**tceyndjez meyd en 'rebeld' ap must kope/muv awt ov 'rebeld' tu repleys lhe ap on dayl (sutc az angol kepad ap) and ar not brodkast rel taym senkronayzeycon.
?-sens 'dayl' and 'kepad' hav eysoleyded storedj and 'auth' konteks, lhey kumyunekeyt veya a sekyir Android brodkast `io.angol.ACTION_UPDATE_LAYOUT`.
**multay envurnment senk**suports `kirent` and `produkcon` Firestore palxs. lha 'rebeld' updeyts bolx odomadekle tu encsur lha produkcon envuronment ez olweyz kirent.

## **navegeycon**
**kunsestent modyulz**tu navegeyt bak, pentc zum. tu muv kirsir, pres an drag, frum awdir speys, left or ruyt (layk Typewise), az wel up and dawn. tu delet, ad sekond fengir pres tu awdir speys.
**heksagon gred wedjet**suports lonq pres dragenq, swopenq, kopeyenq, and tap glow efekts, welx kustom presd steyts (kontrast kulorz) senkronayzd akros wedjets.
**kunsestent kepad font sayz**lha keypad yuzez a konsestent font sayz beysd on lha lardjest lebil lenqlx, skeyld 2/12 larger.
**drag tu foldir lodjek**drag ene modyul  tu foldir, muv ensayd, and remuv frum prevyus. kep sentir drag frum desaperenq on meyn skren.
**manyuwl kope**suport drag tu sentir en 'rebeld' tu manyuwle kope tu 'dayl'z kepad modyul, and konvirsle drag tu rebeld frum dayl tu kope tu rebeld.
**beld modyul**suports edetenq heksagonz and aps: pozecons leyawt, tutc funkcon, kulor, glow, kontrast kulor, leybil...
**spetc tu tekst voys enpit**emplement neydev (?Gemini 3.5) konvirjon refaynment en `DaylEnpitMelxod.kt`.
**tekst tu spetc**(`AndroidPlatformSirvesez`).
**swop vejuwalz**pres tregirz glow, lonq pres tregirz kontrast kulor.
**repleys seym neym faylz**cow konfirmeycon dayalog wen dropenq a fayl/foldir modyul on anulhir ov lha seym neym.

##**spetc tu tekst**
**kebord tranzleycon**lhe 'angol' togil at sentir tregirz AI voys.
?**awtpit feld entirakcon**tutcenq lhe awtpit feld kan be monetird bay lhe enpit editor veya `onUpdeytSelekcon`.
**angol spelenq lodjek**`36 sawndz` map 1:1 tu:
**12 vowalz**
**1** /ɑ/, **2** /æ/, **3** /ɛ/, **4** /ɪ/, **5** /i/, **6** /ɝ/, **7** /ʊ/, **8** /ʌ/, **9** /u/, **0** /oʊ/, **A** /o/, **O** /ɔ/.
**24 konsonants**
l,lh, lx,h, x,n, d,y, t,r, c,j, g,nq, k,q, f,v, b,w, p,m, s,z.
**c** 'sh', **tc** 'ch', **lx** 'thin', **lh** 'the', **nq** 'ng', **q** [ɣ], **x** [x].

###**beld an deploy**
##**edireyt**
**kumper etc rekwest tuw plan for kler lhen presuys and klen plan befor refakdir.**
  1. **erir detekt and remede odomadekle**monetir erirz kontenyuwosle and rezolv (sentaks, tuyp mesmatcez, nul seyfde, or lentenq vayoleycunz).
  2. **lent/format**or `gradlew format`(ef konfegyird).
  3. **dependens tcek**run `./gradlew beld` tu manadj dependensez.
  4. **kod djenereycon**run 'beld_runir' odomadekle.
  5. **kompayl & anelayz**monetir `./gradlew analayz` and kompayleycon erirz and eksepsconz.
  6. **test eksekyucon**monetir IDE dayagnosteks and run relevant tests.
  7. **prevyu sirvir tcek**obzirv lha prevyu sirvir kontenyuwosle and tirmenal awtput (konsol logz, erir mesedjez) for vezyual rel taym fedbak and runtaym erirz.
  8. **remedeyeyt and report**odomeyt feks and report tu lha yuzir onle ef erir kanot rezolv odomadekle.

**klawd senk**modyul leyawts senkronayz welx Firebase Firestore veya `FirebaseSirves`.
**auth & hub**logen(GitHub) and hom skrenz port tu Kotlin Compose.
**klen beld**olweyz yuz `./gradlew klen` to encuwr no remnants.
**enstol dayl**yuz `enstol dayl kler` (klen beld + dep akdeveycon + lontc) or `enstol dayl` (fast updeyt + lontc).
**enstol kepad**yuz `enstol kepad kler` (klen beld + dep akdeveycon) or `enstol kepad` (fast updeyt).
**WearOS**yuz `enstol WearOS` tu lontc lha emyuleydir.
**virjonz**updeyt tu best (Kotlin MP 2.3.21, Compose 1.10.3, AGP 9.2.0, CompileSdk 37, WearOS 6).