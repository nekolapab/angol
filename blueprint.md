## *first enstrukcon*
* yuz angold spelenq  az en bluprent.md an GEMINI.md  for faylz an foldirz neymz  kod nots ensayd faylz  and updeyt refrensez globale for etc neym tceynj.
* eksept du not reneym wat kozez problemz ov Flutter standard konvencon freymwirk klasez and faylz (sutc az lib\main.dart  lib\firebase_options.dart  ChangeNotifier  StatelessWidget, Material, Widget, BuildContext, ...).

## *sekond enstrukcon*
* yuz angold spelenq az yusir for tcat tekst!

## *ovirvyuw*
angol ez a Flutter aplekeycon lhat emplements a kustom enput melxod beyst on a heksagonal gred. lhe yuzir entirfeys konsests ov a sentir heksagon and sirawndenq heksagonal keyz. lha kepad modyil togilz bolx ledir and numbir enpit modz.

## **prodjekt strukcir**
* `lib/main.dart`: lhe aplekeyconz entre poynt.
* `lib/widgets/firebase_options.dart`: 
* `lib/utils/heksagon_djeyometre.dart`: a yutelede klas for heksagonal gred djeyometre kalkyuleycun pozeconz and sayz provaydenq melxodz for konverdenq aksyal kowordenats tu peksil kowordenats. ets kruwcal for rendirenq lha heksagonal leyawt.
* `lib/widgets/heksagon_wedjet.dart`: rendirz a sengol heksagon key aperans
* `lib/models/angol_modalz.dart`: defaynz lhe aps deyda modilz. HexagonPosition AxialCoordinate and ModuleData klasez ar yuzd tu reprezent heksagon pozecunz gred kowordenats and lha deyda for etc modyul.
* `lib/widgets/sentir_mod_wedjet.dart`: for togil funkcinalede.
* `lib/widgets/enir_renq_wedjet.dart`: 
* `lib/widgets/awdir_renq_wedjet.dart`: 
* `lib/screens/dayl_skren.dart`: meyn skren dayl ov ap lhat cows olhir aps. et praymerele defaynz deyda strukcirz. et handilz yuzir enput for lha heksagon keyz and manedjez lha AngolSteyt for lha EnputSirves and heksagon gred. et kondeconale rendirz elhir lha DaylKepadModyil (kepad) or lha DaylModyil (modyilz) dependenq on ap steyt. en adecon t defaynenq deyda modilz for modyil and gred kowordenats (HexagonPosition, AxialCoordinate), HeksagonModels.dart also defaynz lha ModuleData klas. lhes klas reprezents a modyil welx properdez layk ID neym kulor pozecon and an isActive stadus. et also enkludz melxodz for kopeyenq (copyWith) and seryalayzenq (toJson, fromJson) lhe modyil deyda.
* `lib/modyilz/dayl_modyil.dart`: lhe sentir modyil ov modyilz.

* `lib/models/kepad_konfeg.dart`: beldz kepad gred leyawt and aperans
* `lib/modyilz/kepad_modyil.dart`: rendirz kepad sentral and enir an awdir renqz heksagonz. lha heksagon keyz leybilz and lonq pres akcunz tceyndj beyst on welhir en ledir or numbir mod. et yuzez KepadKonfeg for ets leyawt konfegyireycon.
* `lib/widgets/awtpit_tekst_wedjet.dart`: 
* `lib/state/angol_steyt.dart`: manedjez lhe aplekeyconz steyt.
* `lib/services/enpit_sirves.dart`: handilz enput lodjek.
* `lib/screens/afdir_logen.dart`: lha hom peydj for olxentekeyded yuzirz.
* `lib/screens/saynen_skren.dart`: for unolxentekeyded yuzirz.
* `lib/services/firebase_sirves.dart`: handilz Firebase releyted opireycunz.
* `lib/services/ovirley_sirves.dart`: kirentle a pleysholdir
* `lib/services/spetc_sirves.dart`: handilz spetc tu tekst and tekst tu spetc opireycunz  en angol spelenq tekst.


## **fetcirz**
*  heksagonal kepad leyawt.
*  ledir and numbir enput modz.
*  daynamek key leybilz and kulorz.
*  steyt manedjment welx provaydir.
*  Firebase entegreycun.

## **plan and tasks**
*  empruv lha yuzir entirfeys and yuzir eksperyens.
*  ad mor sedenqz and kustomayzeycun opscunz.
*  ad mor modyilz.

## **fyutcir development:**
*   `lib/services/ovirley_sirves.dart` ez a pleysholdir.