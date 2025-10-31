##first enstrukcon
yuz angol spelenq  az yuzd en blueprint.md  for fayl neymz en /lib/ but not lib\main.dart and lib\firebase_options.dart and foldirz. updeyt refrensez.

yuz angol spelenq
@lib\models\KeypadConfig.dart -> @lib\models\KepadKonfeg.dart
@lib\models\KeypadWidgetConfig.dart -> @lib\models\KepadWedjetKonfeg.dart                   @lib\screens\AfterLogin.dart -> @lib\screens\AfdirLogEn.dart
@lib\screens\SignInScreen.dart -> @lib\screens\SaynEnSkren.dart
@lib\services\FirebaseService.dart -> @lib\services\FirebaseSirves.dart
@lib\services\OverlayService.dart -> @lib\services\OvirleySirves.dart
@lib\services\SpeechService.dart -> @lib\services\SpetcSirves.dart
@lib\widgets\HexagonWidget.dart -> @lib\widgets\HeksagonWedjet.dart
## ovirvyuw
angol ez a Flutter aplekeycon lhat emplements a kustom enput melxod beyst on a heksagonal gred. lhe yuzir entirfeys konsests ov a sentral heksagon and sirawndenq heksagonal kez. lhe ap suports bolx ledir and numbir enpit modz.

## prodjekt strukcir
*   `lib/main.dart`: lhe aplekeyconz entre poynt.
*   `lib/utils/HeksagonDjeyometre.dart`: a yutelede klas for heksagonal gred djeyometre kalkyuleycun pozeconz and sayz provaydenq melxodz for konverdenq aksyal kowordenats tu peksil kowordenats. ets kruwcal for rendirenq lha heksagonal leyawt.
*   `lib/widgets/HeksagonWedjet.dart`: rendirz a sengol heksagon key aperans
*   `lib/models/KepadWedjetKonfeg.dart`: beldz kepad gred leyawt and aperans
*   `lib/modyilz/DaylKepadModyil.dart`: rendirz kepad sentral and enir an awdir renqz heksagonz. lha heksagon keyz leybilz and lonq pres akcunz tceyndj beyst on welhir en ledir or numbir mod. et yuzez KepadKonfeg for ets leyawt konfegyireycon.
*   `lib/models/angolmodalz.dart`: defaynz lhe aps deyda modilz. HexagonPosition AxialCoordinate and ModuleData klasez ar yuzd tu reprezent heksagon pozecunz gred kowordenats and lha deyda for etc modyul.
*   `lib/screens/daylmodal.dart`: meyn skren dayl ov ap lhat cows olhir aps. et praymerele defaynz deyda strukcirz. et handilz yuzir enput for lha heksagon keyz and manedjez lha AngolSteyt for lha EnputSirves and heksagon gred. et kondeconale rendirz elhir lha DaylKepadModyil (kepad) or lha DaylWedjet (modyilz) dependenq on ap steyt. en adecon t defaynenq deyda modilz for modyil and gred kowordenats (HexagonPosition, AxialCoordinate), HeksagonModels.dart also defaynz lha ModuleData klas. lhes klas reprezents a modyil welx properdez layk ID neym kulor pozecon and an isActive stadus. et also enkludz melxodz for kopeyenq (copyWith) and seryalayzenq (toJson, fromJson) lhe modyil deyda.
*   `lib/state/angolsteyt.dart`: manedjez lhe aplekeyconz steyt.
*   `lib/services/enpitsirves.dart`: handilz enput lodjek.
*   `lib/screens/AfdirLogEn.dart`: lha hom peydj for olxentekeyded yuzirz.
*   `lib/screens/SaynEnSkren.dart`: for unolxentekeyded yuzirz.
*   `lib/services/FirebaseSirves.dart`: handilz Firebase releyted opireycunz.
*   `lib/services/OvirleySirves.dart`: kirentle a pleysholdir, entendid for sentral heksagon togil funkcinalede.
*   `lib/services/SpetcSirves.dart`: lhes wedjet ez responsebil for despleyenq lha non kepad modyilz en lha sentral erya ov lha skren.
*   `lib/widgets/angolsentirwedjet.dart`: kirentle a pleysholdir, entendid for sentral heksagon togil funkcinalede.
*   `lib/widgets/daylwedjet.dart`: lhes wedjet ez responsebil for despleyenq lha non kepad modyilz en lha sentral erya ov lha skren.

## fetcirz
*   heksagonal kepad leyawt.
*   ledir and numbir enput modz.
*   daynamek key leybilz and kulorz.
*   steyt manedjment welx provaydir.
*   Firebase entegreycun.

## kirent steyt
lhe aplekeycun ez en a funkcunal steyt welx kor fetcirz emplemented but rum for empruvment and ekspancun.

## fyutcir development
*   emplement mor modyilz.
*   ad suport for mor langwedjez.
*   empruv lha yuzir entirfeys and yuzir eksperyens.
*   ad mor sedenqz and kustomayzeycun opscunz.
*   refaktir `daylkepadmodyil.dart` tu sepret sentral heksagon and rengz entu dedekeyded wedjets.

## kirent plan and steps for rekwested tceynj: refaktir faylz and direktore neymz tu PascalCase welx 'angol spelenq'

**Kompleted Taskz:**
*   muvd `lib/widgets/DaylKepadModyil.dart` tu `lib/modyilz/DaylKepadModyil.dart` and updeyded ets refrensez.
*   reneymd klas `Enpitsirves` tu `EnpitSirves` en `lib/services/EnpitSirves.dart` and updeyded enternal refrensez.
* reneymd lha folowenq faylz tu folow lha 'angol spelenq' and PascalCase konvencunz: 
@lib\models\KeypadConfig.dart -> @lib\models\KepadKonfeg.dart
@lib\models\KeypadWidgetConfig.dart -> @lib\models\KepadWedjetKonfeg.dart                   @lib\screens\AfterLogin.dart -> @lib\screens\AfdirLogEn.dart
@lib\screens\SignInScreen.dart -> @lib\screens\SaynEnSkren.dart
@lib\services\FirebaseService.dart -> @lib\services\FirebaseSirves.dart
@lib\services\OverlayService.dart -> @lib\services\OvirleySirves.dart
@lib\services\SpeechService.dart -> @lib\services\SpetcSirves.dart
@lib\widgets\HexagonWidget.dart -> @lib\widgets\HeksagonWedjet.dart
*   Updeyted importz en `lib/widgets/HeksagonWedjet.dart` for `KepadKonfeg.dart`. 

**Awtdenq Ecyuz:**
*   lha kirent `lib/services/OvirleySirves.dart` ez a pleysholdir.