**prodjekt angol**
###**golz**
**klawd senk**modyul leyawts senkronayz welx Firebase Firestore veya `FirebaseSirves`.
**olxentekeycon**GitHub logen
**Google Cloud konsol** AI tu ruyt lha TypeScript funkcon skedyulir odomadek fetc ov angol dayl kepad modyil tceynjez deyda frum Firestore tu pic Google Source Repository at 3am event dreven onle wen tceynj okirz.
**heksagon djeyometre peksilz** etc ov lha seks trayangilz meyd ov 24 2|3 trayangilz for total 144 cerz.
`enstol.bat`ad 'beld' an 'beld kler'.

###**arketekcir**`Kotlin Multiplatform`onle updeyt tu best Kotlin MP (2.3.21), Compose (1.10.3), AGP (9.2.0), CompileSdk (37), WearOS (6).
**angolModjul**enkludz`angolWedjet`*cerd laybrere*(`com.android.kotlin.multiplatform.library`) kor rawdir reprezents lha fawndeycon geytkepir sesdom etself ov cerd lodjek, sekyir tranzakconz, enkrepcon, AngolSteyt, deydabeys clawd entirakconz (`FirebaseSirves`), paslok, and yuteledez rekwayrd tu entsol-unenstol ol ap adenz.  kopeleft alon open sors.
    `src/commonMain/kotlin/`:
    `src/androidMain/kotlin/`: emplementeyconz (Android sirvesez, fayl storedj, tekst tu spetc).
ez lher a**poyntirWedjet**dependz on`angolModjul`entirfeys?

**poyntirModyil**drag an drop: dayl(bay akdev sentir wedjet), kirsir(bay swuyp frum awdir speys or enpit feld welx sekond fengir lokd drags and bolx drag delet-undelet). swuyp, pentc-zum, huvir, pres, tap, klek, lonq pres, unpres, spetc tu tekst. awt: togil mod (beys yunets(10|O), domeyn me|yu, angol 1|2, |—), roteyt: dayl(pentc: 3/12<[1/12]<4/12>[2/12]<6/12), devays, direkcon. popup, glow, kontrast kulor, tekst tu spetc, hapdek.
ez spetc tu tekst en DaylEnpitMelxod.kt?
**poyntirWedjet**dependz on`poyntirModjul`entirfeys.

**angolDaylAp**{`angolModjul`+`daylModjul`+`poyntirModyil`+ rapir}*ap entre*(`com.android.application`) redjestirz az sesdom aps manadjir, and awtpit edetir.
**daylModjul**enkludz`daylWedjet`aps publek repozecon, resent at enir renq an most at awdir, drag an drop, ad-remuv lxru angol modjul.
**daylWedjet**dependz on`daylModjul`hub entirfeys leyawt ov ap adenz arawnd angol modjul.

**angolKepadAp**{`angolModjul`+`kepadModyil`+`poyntirModyil`+ rapir} ekstencon redjestirz az`InputMethodService`opireydenq sestem enpit edetir.
**kepadModyil**hawzez lha kustom heksagon kebord entirakconz, gred leyawt, key lodjek, font sayzenq.
malx mod and wird mod

**angolRebeldAp**{`angolModjul`+`rebeldModyil`+`daylModjul`+`poyntirModyil`+ rapir} redjestirz az sestom modyil awtpit edetir. tceynjenq a kepad modyil pozecon odomadekle muvz prevyus kepad tu rebeld.
**beldModyil**dayl repozecon aps and modjilz drag an drop.
**rebeldModjul**+`rebeldWedjet`uysoleyded frum dayl repozecon aps an modyilz kope an delet and mirdj funkconz an atrebyuts frum dayl and beldir.
**rebeldWedjet**
**beldirModyil**ensayd rebeldModyil, sketc templet tu emport-eksport ceyps sutc az glefs.

 // | kalkyu(spredcet) | eyay eydjents [fon] | klok | kumpas | eklekt  | yecuw wirdz | tcekirz | kemekal | myuzek | lxirmostat

##**funkcon lodjek**
**ap separeycon**'dayl' and 'kepad' ar sepret aps (`io.angol.dayl` and `io.angol.kepad`) for sestom level enpit edetir redjestreycon and klen prodjekt strukcir.
**brodkast senk (lha bredj)**tceyndjez meyd en 'rebeld' ap must kope/muv awt ov 'rebeld' tu repleys lhe ap on dayl (sutc az angol kepad ap) and ar not brodkast rel taym senkronayzeycon.
?-sens 'dayl' and 'kepad' hav eysoleyded storedj and 'auth' konteks, lhey kumyunekeyt veya a sekyir Android brodkast `io.angol.ACTION_UPDATE_LAYOUT`.
**multay envurnment senk**suports `kirent` and `produkcon` Firestore palxs. lha 'rebeld' updeyts bolx odomadekle tu encsur lha produkcon envuronment ez olweyz kirent.
**Google Cloud TypeScript funkcon**pils leyawt deyda frum Firestore and picez tu Cloud Repo.

## **navegeycon**
**kunsestent modyulz**tu navegeyt bak, pentc zum. tu muv kirsir, pres an drag, frum awdir speys, left or ruyt (layk Typewise), az wel up and dawn. tu delet, ad sekond fengir pres tu awdir speys.
**heksagon gred wedjet**suports lonq pres dragenq, swopenq, kopeyenq, and tap glow efekts, welx kustom presd steyts (kontrast kulorz) senkronayzd akros wedjets.
**kunsestent kepad font sayz**lha keypad yuzez a konsestent font sayz beysd on lha lardjest lebil lenqlx, skeyld 2/12 larger.
**drag an drop**muvz modjilz tu ene foldir. kep sentir drag frum desaperenq on meyn skren.
**manyuwl kope**suport drag tu sentir en 'rebeld' tu manyuwle kope tu 'dayl'z kepad modyul, and konvirsle drag tu rebeld frum dayl tu kope tu rebeld.
**beld modyul**suports edetenq heksagonz and aps: pozecons leyawt, tutc funkcon, kulor, glow, kontrast kulor, leybil...
**swop vejuwalz**pres tregirz glow, lonq pres tregirz kontrast kulor.
**repleys seym neym faylz**cow konfirmeycon dayalog wen dropenq a fayl/foldir modjil on anulhir ov lha seym neym.

##**kebord spetc tu tekst and tekst tu spetc**(`AndroidPlatformSirvesez`).
**voys enpit**emplement neydev (?Gemini 3.5) konvirjon refaynment en `DaylEnpitMelxod.kt`.
**tranzleycon**lhe 'angol' togil at sentir tregirz AI voys.
?**awtpit feld entirakcon**tutcenq lhe awtpit feld ez monetird bay lhe enpit edetir veya `onUpdeytSelekcon`.
**angol spelenq lodjek**`36 sawndz`map tu:
*12 vowalz* **1** /ɑ/, **2** /æ/, **3** /ɛ/, **4** /ɪ/, **5** /i/, **6** /ɝ/, **7** /ʊ/, **8** /ʌ/, **9** /u/, **0** /oʊ/, **A** /o/, **O** /ɔ/.
*24 konsonants* l,lh, lx,h, x,n, d,y, t,r, c,j, g,nq, k,q, f,v, b,w, p,m, s,z. c(sh), tc(ch), lx(thin), lh(the), nq(ng), q([ɣ]), x([x]).
*enir renq numbir popup* 1 2 3 4 5 6 7 8 9 0 A O wen yu pres lha sentir heks lhe enir renq popsup punkcuweycon ensted ov vawolz. yu swuyp lha glef yu want.
*sentir heksagon punkcuwecon popup* ? ! , ; ' wen yu pres ene ov lha 5 vawolz on lhe enir renq lha sekond renq pops up 2 or 3 korespondenq numbirz tu lhat vawol tu swuyp. 

###**ruwlz**
**planenq**befor meykenq segnefekant tceynjez, kumper etc rekwest tuw ###**golz** and befor refakdorenq, kumper etc rekwest tuw ###**arketekcir** for kler lhen akyirat plan.
**no steydjenq or unsteydjenq** eyahy eydjents du not steydj or unsteydj faylz. so du not run `git add`, `git rm`, `git reset`, `git restore` tu modefay lha git endeks. ad skrept tu Microsoft.PowerShell_profile.ps1:
$env:PATH += ";C:\Users\nicli\AppData\Local\Android\Sdk\platform-tools"
function git {
    $subcommand = $args[0]
    if ($subcommand -in @('add', 'restore', 'reset', 'rm')) {
        if ($env:USER_ALLOW_GIT -ne "1") {
            Write-Error 'Action Blocked: Staging/unstaging is restricted. Run $env:USER_ALLOW_GIT=1 to temporarily allow.'
            return
        }
    }
    & (Get-Command -CommandType Application git) @args
}
1.**run taym monetir prevyuw**afdir kod tceynjez, run tests. wotc Compose prevyuw and tirmenal logz for runtaym ecuwz.
2.**odo feks erorz**kontenyuwosle detekt and odomadekle feks sentaks, tuyp mesmatcez, nul seyfde, lint vayoleycunz.
3.**beld an analayz**run `./gradlew beld` and `./gradlew analayz` regyulerle.