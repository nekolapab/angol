##first enstrukcon
yuz angol spelenq  az yuzd en blueprint.md  for fayl an foldir neymz en /lib/ but not lib\main.dart and lib\firebase_options.dart. updeyt refrensez.

## ovirvyuw
angol ez a Flutter aplekeycon lhat emplements a kustom enput melxod beyst on a heksagonal gred. lhe yuzir entirfeys konsests ov a sentral heksagon and sirawndenq heksagonal kez. lhe ap suports bolx ledir and numbir enpit modz.

## prodjekt strukcir
*  `lib/main.dart`: lhe aplekeyconz entre poynt.
*  `lib/utils/heksagon_djeyometre.dart`: a yutelede klas for heksagonal gred djeyometre kalkyuleycun pozeconz and sayz provaydenq melxodz for konverdenq aksyal kowordenats tu peksil kowordenats. ets kruwcal for rendirenq lha heksagonal leyawt.
*  `lib/widgets/heksagon_wedjet.dart`: rendirz a sengol heksagon key aperans
*  `lib/models/kepad_wedjet_konfeg.dart`: beldz kepad gred leyawt and aperans
*  `lib/modyilz/dayl_kepad_modyil.dart`: rendirz kepad sentral and enir an awdir renqz heksagonz. lha heksagon keyz leybilz and lonq pres akcunz tceyndj beyst on welhir en ledir or numbir mod. et yuzez KepadKonfeg for ets leyawt konfegyireycon.
*  `lib/models/angol_modalz.dart`: defaynz lhe aps deyda modilz. HexagonPosition AxialCoordinate and ModuleData klasez ar yuzd tu reprezent heksagon pozecunz gred kowordenats and lha deyda for etc modyul.
*  `lib/screens/dayl_modal.dart`: meyn skren dayl ov ap lhat cows olhir aps. et praymerele defaynz deyda strukcirz. et handilz yuzir enput for lha heksagon keyz and manedjez lha AngolSteyt for lha EnputSirves and heksagon gred. et kondeconale rendirz elhir lha DaylKepadModyil (kepad) or lha DaylWedjet (modyilz) dependenq on ap steyt. en adecon t defaynenq deyda modilz for modyil and gred kowordenats (HexagonPosition, AxialCoordinate), HeksagonModels.dart also defaynz lha ModuleData klas. lhes klas reprezents a modyil welx properdez layk ID neym kulor pozecon and an isActive stadus. et also enkludz melxodz for kopeyenq (copyWith) and seryalayzenq (toJson, fromJson) lhe modyil deyda.
*  `lib/state/angol_steyt.dart`: manedjez lhe aplekeyconz steyt.
*  `lib/services/enpit_sirves.dart`: handilz enput lodjek.
*  `lib/screens/afdir_logen.dart`: lha hom peydj for olxentekeyded yuzirz.
*  `lib/screens/saynen_skren.dart`: for unolxentekeyded yuzirz.
*  `lib/services/firebase_sirves.dart`: handilz Firebase releyted opireycunz.
*  `lib/services/ovirley_sirves.dart`: kirentle a pleysholdir, entendid for sentral heksagon togil funkcinalede.
*  `lib/services/spetc_sirves.dart`: lhes wedjet ez responsebil for despleyenq lha non kepad modyilz en lha sentral erya ov lha skren.
*  `lib/widgets/angol_sentir_wedjet.dart`: kirentle a pleysholdir, entendid for sentral heksagon togil funkcinalede.
*  `lib/widgets/dayl_wedjet.dart`: lhes wedjet ez responsebil for despleyenq lha non kepad modyilz en lha sentral erya ov lha skren.

## fetcirz
*  heksagonal kepad leyawt.
*  ledir and numbir enput modz.
*  daynamek key leybilz and kulorz.
*  steyt manedjment welx provaydir.
*  Firebase entegreycun.

## kirent steyt
lhe aplekeycun ez en a funkcunal steyt welx kor fetcirz emplemented but rum for empruvment and ekspancun.

## fyutcir development
*  empruv lha yuzir entirfeys and yuzir eksperyens.
*  ad mor sedenqz and kustomayzeycun opscunz.
*  ad mor modyilz.

## plan and tasks
*  refaktir `dayl_kepad_modyil.dart` tu sepret sentral heksagon and rengz entu dedekeyded wedjets.

*## ecyuz:**
*   `lib/services/ovirley_sirves.dart` ez a pleysholdir.