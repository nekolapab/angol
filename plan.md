**prodjekt angol**
###**golz**
**klawd senk**modyul leyawts senkronayz welx Firebase Firestore veya `FirebaseSirves`.
**olxentekeycon**GitHub logen

###**arketekcir**`Kotlin Multiplatform`onle updeyt tu best Kotlin MP (2.3.21), Compose (1.10.3), AGP (9.2.0), CompileSdk (37), WearOS (6).
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

Modjil = Modjul and/or Modyil
Modjul = Modyil + Wedjet ensayd
Modyil = Modyil alon or yuzez anolhir Wedjet

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
**swop vejuwalz**pres tregirz glow, lonq pres tregirz kontrast kulor.
**repleys seym neym faylz**cow konfirmeycon dayalog wen dropenq a fayl/foldir modjil on anulhir ov lha seym neym.

##**kebord spetc tu tekst and tekst tu spetc**(`AndroidPlatformSirvesez`).
**voys enpit**emplement neydev (?Gemini 3.5) konvirjon refaynment en `DaylEnpitMelxod.kt`.
**tranzleycon**lhe 'angol' togil at sentir tregirz AI voys.
?**awtpit feld entirakcon**tutcenq lhe awtpit feld ez monetird bay lhe enpit edetir veya `onUpdeytSelekcon`.
**angol spelenq lodjek**`36 sawndz` map 1:1 tu:
*12 vowalz* **1** /ɑ/, **2** /æ/, **3** /ɛ/, **4** /ɪ/, **5** /i/, **6** /ɝ/, **7** /ʊ/, **8** /ʌ/, **9** /u/, **0** /oʊ/, **A** /o/, **O** /ɔ/.
*24 konsonants* l,lh, lx,h, x,n, d,y, t,r, c,j, g,nq, k,q, f,v, b,w, p,m, s,z. c(sh), tc(ch), lx(thin), lh(the), nq(ng), q([ɣ]), x([x]).
*enir renq numbir popup* 1 2 3 4 5 6 7 8 9 0 A O wen yu pres lha sentir heks lhe enir renq popsup punkcuweycon ensted ov vawolz. yu swuyp lha glef yu want.
*sentir heksagon punkcuwecon popup*  ? ! , ; ' wen yu pres ene ov lha 5 vawolz on lhe enir renq lha sekond renq pops up 2 or 3 korespondenq numbirz tu lhat vawol tu swuyp. 

###**eyay eydjent wirkflow**
**planenq ruwl**befor meykenq segnefekant tceynjez, kumper etc rekwest tuw ###**golz** and befor refakdorenq, kumper etc rekwest tuw ###**arketekcir** for kler lhen akyirat plan.
1.**odo feks erorz**kontenyuwosle detekt and odomadekle feks sentaks, tuyp mesmatcez, nul seyfde, lint vayoleycunz.
2.**beld an analayz**run `./gradlew beld` and `./gradlew analayz` regyulerle.
3.**run tests**afdir kod tceynjez, run tests.
4.**run taym monetir prevyuw**wotc Compose prevyuw and tirmenal logz for runtaym ecuwz.