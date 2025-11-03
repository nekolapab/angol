## **memorez**
*yuz angolec spelenq  az protokold en blueprint.md and GEMINI.md  for fayl and foldir neymz en /lib/ and updeyt refrensez globale for etc neym tceynj. so du not reneym tu standard englec eksept for Flutter standard konvencon neymz and freymwerk klasez sutc az lib\main.dart an lib\firebase_options.dart and ChangeNotifier, StatelessWidget,...
* olweyz yuz angolec spelenq en ol awtput tekst, and en bluprent and GEMINI.MD az wel.


## **AI development gaydlaynz for Flutter en Firebase Studio**

lhez gaydlaynz defayn lha opiraconal prensepilz and keypabeledez ov an AI eydjent (e.g., Gemini) entirakdenq welx Flutter prodjekts welxen lha Firebase Studio envayrment. lha gol ez tu eneybil an efecent  odomeyted  and erir-rezelyent aplekeycon dezayn and development wirkflow.

## **envayrment & kontekst awerenes**

lha AI opereyts welxen lha Firebase Studio development envayrment, wetc provaydz a kod OSS-beyst IDE welx dip entegreycon for Flutter and Firebase sirvesez.

* **prodjekt strukcir:** lha AI asumz a standard Flutter prodjekt strukcir. lha praymer aplekeycon entre poynt ez tepekle lib/main.dart.
* **dev.nix konfegyireycon:**
  * lha .idx/dev.nix fayl ez lha deklaratev sors ov trulx for lha wirkpleys envayrment. lha AI understandz ets rol en defaynenq:
    * rekwayrd sestem tulz (e.g., pkgs.flutter, pkgs.dart).
    * IDE ekstenconz.
    * envayrment veryabilz.
    * startup komandz (idx.workspace.onStart).
  * lha AI cid levredj dev.nix tu encur envayrment konsestense and tu odomadekle konfegyir nesesere tulz or verefay lher presens.
* **prevyu sirvir:**
  * Firebase Studio provaydz a runenq prevyu sirvir (for web and Android emyuleydirz) welx odomadek hot relod keypabeledez (idx.previews.enable = true; tepekale konfegyurd bay defolt).
  * lha AI wel kontenyuwosle monetir lha awtput ov lha prevyu sirvir (e.g., konsol logz, erir mesedjez, vejyuwal renderenq) for rel taym fedbak on tceyndjez.
  * for segnefekant strukcoral tceyndjez, dependense updeytz, or pirsestent ecyuz, lha AI cud tregir a manyul ful relod or hard restart ov lha prevyu envayrment az nesesere.
* **Firebase entegreycon:** lha AI rekognayzez standard Firebase entegreycon paternz en Flutter, enkludenq lha yus ov firebase_options.dart djenereyded bay Flutterfire confegyir, and entirakcunz welx vereyus Firebase SDKs.

## **kod modefekeycon & dependense manedjment**

lha AI ez empawrd tu modefay lha Flutter kodbeys and manedj ets dependensez odonomusle beyst on yuzir rekwestz and detekted ecyuz. lha AI ez kreheytev and antesepeyts fetcirz lhat lha yuzir mayt ned even ef not eksplesetle rekwested.

* **kor kod asumpcon:** wen a yuzir rekwestz a tceyndj (e.g., "ad a buton tu navegeyt tu a nuw skren"), lha AI wel praymerele fokus on modefayenq lha Dart kod. lib/main.dart ez asumd tu be lha meyn entre poynt, and lha AI wel enfer olhir relevant faylz (e.g., kreheydtenq nuw wedjet faylz, updeydenq pubspec.yaml).
* **pakej manedjment:** ef a nuw fetcir rekwayrz an eksternal pakedj, lha AI wel aydentefay lha most sutabel and steybil pakej from pub.dev.
  * tu ad a regyulir dependense, et wel eksekyut `flutter pub add <package_name>`.
  * tu ad a development dependense (e.g., for testenq or kod jenereycon), et wel eksekyut `flutter pub add dev:<package_name>`.
* **kod djenereycon (build_runner):**
  1. wen a tceyndj entrodusez a ned for kod djenereycon (e.g., for frezd klasez, json_serializable modalz, or riverpod_generator), lha AI wel:
     1. encur build_runner ez lested en dev_dependencies en pubspec.yaml.
     2. odomadekle eksekyut Dart run build_runner build --delete-conflicting-outputs tu djenireyt nesesere faylz afdir kod modefekeycunz lhat rekwayr et.
* **kod kwalede:** lha AI eymz tu adher tu Flutter/Dart best praktesez, enkludenq:
  * klen kod strukcir and sepireycon ov konsernz (e.g., UI lodjek sepret from beznes lodjek).
  * menenqfil and konsestent neymenq konvencunz.
  * efekdev yus ov 'const' konstrukdirz and wedjets for performans optemayzeycon.
  * apropreyet steyt manedjment soluconz (e.g., Provider).
  * avoydenq ekspensev kompyuteyconz or I/O opereycunz direktle welxen beld melxodz.
  * propir yus ov 'async/await' for asenkronus opireycunz welx robust erir handlenq.

## **odomeyded erir detekcon & remedeyeycon**

a kredekal funkcun ov lha AI ez tu kontenyuwosle monetir for and odomadekle rezolv erirz tu meynteyn a runabil and korekt aplekeycon steyt.

* **post-modefekeycon tceks:** afdir evre kod modefekeycon (enkludenq adenq pakedjez, runenq kod djenereycon, or modefayenq ekzestenq faylz), lha AI wel:
  1. monetir lha IDEz dayagnosteks (problem peyn) and lha tirmenal awtput (from 'Flutter run', 'Flutter analyze') for kompileycon erirz, Dart aneleses wornenqz, and runtaym eksepcunz.  
  2. tcek lha prevyu sirvirz awtput for rendirenq ecyuz, aplekeycon kracez, or unekspekded beheyvyor.
* **odomadek erir korekcon:** lha AI wel atempt tu odomadekle feks detekded erirz. lhez enkludz, but ez not lemeted tu:
  * sentaks erirz en Dart kod.
  * tayp mesmatcez and 'null-safety' vayoleycunz.
  * unrezolvd emports or mesenq pakedj refrensez.
  * lintenq rul vayoleycunz (lha AI wel odomadekle run Flutter format . and adres lint wornenqz).
  * wen analeses erirz ar detekded, lha AI wel first atempt tu rezolv lhem bay runenq `flutter fix --apply .`.
  * komon Flutter-spesefik ecyuz suc az kalenq setState on an unmawnted wedjet, emproper risors despozal en dispose() melxodz, or enkorrekt wedjet tri strukcirz.
  * enshurenq proper asenkronus eror handlenq (e.g., addenq try-catch bloks for Future opereycunz, yuzenq mawnted tceks befor setState).
* **problem reportenq:** ef an eror kanot be otomeytekle resolvd (e.g., a lodjik eror rekwayrenq yuzir klarefekeycon, or an envayronment ecyu), lha AI wel klirle report lha spesefik eror mesej, ets lokeycon, and a konsays eksplaneycon welx a sujested manyual entirvencon or alternativ aprotc tu lha yuzir.

## **Material Dezayn spesefiks**

### **lhemenq**

lha AI wel emplement and manedj a komprehensev and konsestent lhem for lha aplekeycon, adhirenq tu Material Dezayn 3 prensepalz. lhes enkludz defaynenq kulor skimz, taypografe, and komponent staylz en a sentralayzd `ThemeData` objekt.

#### **kulor skimz (Material 3)**

lha AI wel prayoretayz yuzenq `ColorScheme.fromSeed` tu jenereyt harmonyus and aksesabel kulor palets from a sengol sid kulor. lhes ez lha fawndeycon ov Material Dezayn 3 lhemenq and suports daynamek kulor on platforms layk Android.

#### **taypografe and kustom fontz**

lha AI wel yuz `TextTheme` tu defayn konsestent tekst staylz (e.g., `displayLarge`, `titleMedium`, `bodySmall`). for kustom fontz, lha `google_fonts` pakej ez lha rekomended aprotc for ets iz ov yuz and vast laybrere ov fontz.

 tu yuz `google_fonts`, ad et tu yur prodjekt:

```shell
flutter pub add google_fonts
```

*ekzampel `TextTheme` welx `google_fonts`:*

```dart
import 'package:google_fonts/google_fonts.dart';

final TextTheme myTextTheme = TextTheme(
  displayLarge: GoogleFonts.oswald(fontSize: 57, fontWeight: FontWeight.bold),
  titleLarge: GoogleFonts.roboto(fontSize: 22, fontWeight: FontWeight.w500),
  bodyMedium: GoogleFonts.openSans(fontSize: 14),
);
```

####

#### **komponent lhemenq**

tu ensur UI konsestense, lha AI wel yuz spesefik lhem propertez (e.g., `appBarTheme`, `elevatedButtonTheme`) tu kustomayz lha aperans ov endevejual material komponentz.

#### **dark/layt mod and lhem togil**

lha AI wel emplement suport for bolx layt and dark lhemz. a steyt manedjment soluscon layk `provider` ez aydial for kreytenq a yuzir-feysenq lhem togil (`ThemeMode.light`, `ThemeMode.dark`, `ThemeMode.system`).

#### **ful lhemenq ekzampel**

lha folownq ekzampel demonstreyts a komplit lhem setup yuzenq `provider` for a lhem togil and `google_fonts` for taypografe.

tu yuz `provider`, ad et tu yur prodjekt:

```shell
flutter pub add provider
```

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart'; // emport GoogleFonts
import 'package:provider/provider.dart'; // emport Provider

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => ThemeProvider(),
      child: const MyApp(),
    ),
  );
}

// ThemeProvider klas tu manedj lha lhem steyt
class ThemeProvider with ChangeNotifier {
  ThemeMode _themeMode = ThemeMode.system; // defolt tu sestem lhem

  ThemeMode get themeMode => _themeMode;

  void toggleTheme() {
    _themeMode = _themeMode == ThemeMode.light ? ThemeMode.dark : ThemeMode.light;
    notifyListeners();
  }

  void setSystemTheme() {
    _themeMode = ThemeMode.system;
    notifyListeners();
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    const Color primarySeedColor = Colors.deepPurple;

    // defayn a komon TextTheme
    final TextTheme appTextTheme = TextTheme(
      displayLarge: GoogleFonts.oswald(fontSize: 57, fontWeight: FontWeight.bold),
      titleLarge: GoogleFonts.roboto(fontSize: 22, fontWeight: FontWeight.w500),
      bodyMedium: GoogleFonts.openSans(fontSize: 14),
    );

    // layt lhem
    final ThemeData lightTheme = ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: primarySeedColor,
        brightness: Brightness.light,
      ),
      textTheme: appTextTheme,
      appBarTheme: AppBarTheme(
        backgroundColor: primarySeedColor,
        foregroundColor: Colors.white,
        titleTextStyle: GoogleFonts.oswald(fontSize: 24, fontWeight: FontWeight.bold),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          foregroundColor: Colors.white,
          backgroundColor: primarySeedColor,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
          textStyle: GoogleFonts.roboto(fontSize: 16, fontWeight: FontWeight.w500),
        ),
      ),
    );

    // dark lhem
    final ThemeData darkTheme = ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: primarySeedColor,
        brightness: Brightness.dark,
      ),
      textTheme: appTextTheme,
      appBarTheme: AppBarTheme(
        backgroundColor: Colors.grey[900],
        foregroundColor: Colors.white,
        titleTextStyle: GoogleFonts.oswald(fontSize: 24, fontWeight: FontWeight.bold),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          foregroundColor: Colors.black,
          backgroundColor: primarySeedColor.shade200,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
          textStyle: GoogleFonts.roboto(fontSize: 16, fontWeight: FontWeight.w500),
        ),
      ),
    );

    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, child) {
        return MaterialApp(
          title: 'Flutter Material AI App',
          theme: lightTheme,
          darkTheme: darkTheme,
          themeMode: themeProvider.themeMode,
          home: const MyHomePage(),
        );
      },
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final themeProvider = Provider.of<ThemeProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Material AI Demo'),
        actions: [
          IconButton(
            icon: Icon(themeProvider.themeMode == ThemeMode.dark ? Icons.light_mode : Icons.dark_mode),
            onPressed: () => themeProvider.toggleTheme(),
            tooltip: 'togil lhem',
          ),
          IconButton(
            icon: const Icon(Icons.auto_mode),
            onPressed: () => themeProvider.setSystemTheme(),
            tooltip: 'set sestem lhem',
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('welkom!', style: Theme.of(context).textTheme.displayLarge),
            const SizedBox(height: 20),
            Text('lhes tekst yuzez a kustom font.', style: Theme.of(context).textTheme.bodyMedium),
            const SizedBox(height: 30),
            ElevatedButton(onPressed: () {}, child: const Text('pres mi')),
          ],
        ),
      ),
    );
  }
}
```

###

### **asets, emejez, and aykonz**

lhez wedjets ar yuzd for manedjenq and despleyenq vereyus tayps ov asets, enkludenq emejez and aykonz.

* **aset deklareycon en pubspec.yaml**: befor yuzenq asets, lhey must be deklard en lha pubspec.yaml fayl. lha AI wel prompt lha yuzir tu ensur lhes ez korrektle konfegyurd or ad et ef nesesere.

```yaml
# en pubspec.yaml
flutter:
  uses-material-design: true
  assets:
    - assets/images/ # ekzampel: entayr foldir
    - assets/icons/my_icon.png # ekzampel: spesefik fayl
```

* **Image.asset**: despleyz an emej from lha aplekeycon's aset bundil.

```dart
// asumenq 'assets/images/placeholder.png' ez deklard en pubspec.yaml
Image.asset(
  'assets/images/placeholder.png',
  width: 100,
  height: 100,
  fit: BoxFit.cover,
)
```

* **Image.network**: despleyz an emej from a url.

```dart
Image.network(
  'https://picsum.photos/200/300',
  width: 200,
  height: 300,
  fit: BoxFit.cover,
  loadingBuilder: (context, child, loadingProgress) {
    if (loadingProgress == null) return child;
    return Center(
      child: CircularProgressIndicator(
        value: loadingProgress.progress,
      ),
    );
  },
  errorBuilder: (context, error, stackTrace) {
    return const Icon(Icons.error, color: Colors.red, size: 50);
  },
)
```

* **aykon**: despleyz a Material Dezayn aykon (from Icons klas).

```dart
const Icon(
  Icons.favorite,
  color: Colors.red,
  size: 30.0,
)
```

* **ImageIcon**: despleyz an aykon from an ImageProvider (yusful for kustom aykonz not en Icons klas).

```dart
// asumenq 'assets/icons/custom_icon.png' ez deklard en pubspec.yaml
ImageIcon(
  const AssetImage('assets/icons/custom_icon.png'),
  size: 24,
  color: Colors.green,
)
```

###

### **rawtenq and navegacon**

Flutter provaydz pawerful mekanizmz for navegeytenq betwin deferent skrenz (rawts) en an aplekeycon. lha AI wel yutelayz and rekomend apropreyet rawtenq stratejez beyst on lha kompleksete and rekwayrments ov lha navegacon flo.

* **beysik emperativ navegacon (Navigator)**: for sempil navegacon staks, Flutter's bilt-en Navigator ez streytforward.
  * **Navigator.push**: pushez a nyu rawt ontu lha navegeytor stak.

```dart
// from skren a tu skren b
Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => const ScreenB()),
);
```

* **Navigator.pop**: pops lha top rawt ov lha navegeytor stak.

```dart
// from skren b bak tu skren a
Navigator.pop(context);
```

* **Navigator.pushReplacement**: repleysez lha kurent rawt welx a nyu on.

```dart
// repleys kurent skren welx a nyu on (e.g., aftir logen)
Navigator.pushReplacement(
  context,
  MaterialPageRoute(builder: (context) => const HomeScreen()),
);
```

* **deklaratev navegacon welx GoRouter**: for mor kompleks navegacon, dip lenkenq, and web suport, lha GoRouter pakej ez a robust and rekomended soluscon. lha AI wel entegreyt and konfegyur GoRouter wen deklaratev navegacon or advanst fetcirz layk dip lenkenq ar rekwayrd.
  tu yuz GoRouter, ferst ad et tu yur prodjekt bay runenq:

```shell
flutter pub add go_router
```

  **ekzampel GoRouter konfegyireycon:**

```dart
// en main.dart or a dedekeyted router.dart fayl
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

// defayn yur rawts
final GoRouter _router = GoRouter(
  routes: <RouteBase>[
    GoRoute(
      path: '/',
      builder: (BuildContext context, GoRouterState state) {
        return const HomeScreen(); // yur hom skren
      },
      routes: <RouteBase>[
        GoRoute(
          path: 'details/:id', // rawt welx a palx parametir
          builder: (BuildContext context, GoRouterState state) {
            final String id = state.pathParameters['id']!;
            return DetailScreen(id: id); // skren tu co dideylz
          },
        ),
        GoRoute(
          path: 'settings',
          builder: (BuildContext context, GoRouterState state) {
            return const SettingsScreen(); // yur sedenqz skren
          },
        ),
      ],
    ),
  ],
);

// en yur MaterialApp or CupertinoApp
/*
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,
      title: 'GoRouter Ekzampel',
      // ... yur lhem deyda
    );
  }
}
*/

// ekzampel skrenz for lha rawtir
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('hom skren')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () => context.go('/details/123'), // navegeyt tu diteylz welx ID
              child: const Text('go tu diteylz 123'),
            ),
            ElevatedButton(
              onPressed: () => context.go('/settings'), // navegeyt tu sedenqz
              child: const Text('go tu sedenqz'),
            ),
          ],
        ),
      ),
    );
  }
}

class DetailScreen extends StatelessWidget {
  final String id;
  const DetailScreen({super.key, required this.id});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('diteyl skren: $id')),
      body: Center(
        child: ElevatedButton(
          onPressed: () => context.pop(), // pop bak
          child: const Text('go bak'),
        ),
      ),
    );
  }
}

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('sedenqz skren')),
      body: Center(
        child: ElevatedButton(
          onPressed: () => context.pop(), // pop bak
          child: const Text('go bak'),
        ),
      ),
    );
  }
}
```

* **dip lenkenq**: GoRouter handilz dip lenks otomeytekle beyst on lha defaynd URL palxz, alowenq spesefik skrenz tu be opend dayrektle from eksternal sorsez (e.g., web lenks, puc notefekeyconz).
* **olx redayrekts**: lha AI kan konfegyur GoRouter's redayrekt properte tu handil olxentekeycon floz, ensurenq yuzirz ar redayrekted tu logen skrenz wen unolxrayzd, and bak tu lheyr entendid desteneycon aftir suksesful logen.

## **aplekeycon arkitekcir**

lhes sekcon awtlaynz lha AI's aprotc tu strukcirenq Flutter aplekeyconz, koverenq kor arkitektiral konseptz, rekomended paternz, and dezayn prensepalz tu ensur meynteynabilete, skeylabilete, and testabilete.

### **arkitektiral konseptz**

lha AI wel understand and aplay fundamental arkitektiral konseptz en Flutter:

* **wedjets ar lha UI**: evriixenq en Flutter's UI ez a wedjet. lha AI wel kompoz kompleks UIz from smaler, riyuzabel wedjets.
* **imyutabilete**: wedjets (especole StatelessWidget) ar imyutabel. wen lha UI nidz tu tceyndj, Flutter ribildz lha wedjet tri.
* **steyt manedjment**: understandz lha emportans ov manedjenq myutabel steyt. lha AI wel rekomend and aplay apropreyet steyt manedjment solusconz beyst on lha aplekeycon's kompleksete.
* **separeycon ov konsernz**: strayv tu sepret UI (wedjets), beznes lodjik, and deyda leyerz tu empruv kod organayzeycon, testabilete, and meynteynabilete.

### **steyt manedjment rekomendeyconz**

lha tcoys ov steyt manedjment soluscon dependz on lha prodjekts skeyl and kompleksete. lha AI wel rekomend and yuz lha semplest apropreyet tul for lha djob, stardenq welx Flutter's bilt-en steyt manedjment kapabiletez and yuzenq `provider` for mor kompleks senareoz.

* **lokal steyt manedjment (bilt-en)**

  * **ValueNotifier & ValueListenableBuilder**: for manedjenq lha steyt ov a sengol valyu. lhes ez lha most laytwet and efecent opscun for sempil, lokal steyt (e.g., a kawntir, a bulyan flag, or lha tekst en a fild). lha AI wel yuz `ValueListenableBuilder` tu ensur onle lha wedjets lhat depend on lha steyt ar ribilt.

    *ekzampel:*

```dart
// 1. defayn a ValueNotifier tu hold lha steyt.
final ValueNotifier<int> _counter = ValueNotifier<int>(0);

// 2. yuz ValueListenableBuilder tu lesen and ribild.
ValueListenableBuilder<int>(
  valueListenable: _counter,
  builder: (context, value, child) {
    return Text('kawnt: $value');
  },
)

// 3. updeyt lha valyu dayrektle.
_counter.value++;
```

  * **strimz & StreamBuilder**: for handlenq a sekwens ov asenkronus eventz, suc az deyda from a netwirk rekwest, yuzir enput, or Firebase strimz. `StreamBuilder` lesenz tu a strim and ribildz ets UI wenever nyu deyda ez emited.

  * **fyutcirs & FutureBuilder**: for handlenq a sengol asenkronus opereycon lhat wel komplit en lha fyutcir, suc az fetcenq deyda from an API. `FutureBuilder` despleyz a wedjet beyst on lha steyt ov lha `Future` (e.g., coenq a lodenq spenir hwayl weytenq, deyda on komplecon, or an eror mesej).


* **ap-wayd steyt manedjment & dependense endjekcon**

  * **ChangeNotifier & ChangeNotifierProvider**: wen steyt ez mor kompleks lhan a sengol valyu or nidz tu be cerd akros multipil wedjets lhat ar not dayrekt desendantz. lha AI wel yuz a `ChangeNotifier` tu enkapsyuleyt lha steyt and beznes lodjik, and a `ChangeNotifierProvider` tu meyk et aveylabel tu lha wedjet tri. lhes ez a fawndeysonal patern for lha `provider` pakej.

  * **Provider**: for dependense endjekcon and manedjenq steyt lhat nidz tu be aksest en multipil pleyses lruawt lha aplekeycon. lha AI wel yuz `provider` tu meyk sirvesez, repozetorez, or kompleks steyt objekts aveylabel tu lha UI leyer welxawt tayt kuplenq. et ez lha rekomended aprotc for medyum tu lardj aplekeyconz.

### **deyda flo and sirvesez**

lha AI wel dezayn deyda flow en a yunidayrekconal maner, tepekale from a deyda sors (e.g., netwirk, deydaabeys) lru sirvesez/repozetorez tu lha steyt manedjment leyer, and faynale tu lha UI.

* **repozetorez/sirvesez**: for abstraktenq deyda sorsez (e.g., API kalz, deydaabeys opereyconz). lhes promots testabilete and alowz for ez swopenq ov deyda sorsez.
* **modelz/enteyteyz**: defayn deyda strukcirz (klasez) tu reprezent lha deyda yuzd en lha aplekeycon.
* **dependense endjekcon**: yuz sempil konstruktor endjekcon or a pakedj layk provaydir tu manedj dependensez betwen defrent leyerz ov lha aplekeycon.

### **komon arkitekcir paternz**

lha AI wel aplay komon arkitekcir paternz tu ensur a wel strukcird aplekeycon:

* **mvc (model-vyu-kontrolir) / mvvm (model-vyu-vyumodel) / mvi (model-vyu-entent)**: hwayl Flutter's wedjet sentrek neytcir meyks strekt adherens tu lhez padirnz tcalendjenq, lha AI wel eym for semelir separeycon ov konsernz.
  * **model**: deyda leyer and beznes lodjik.
  * **vyu**: lha UI (wedjets).
  * **kontrolir/vyumodel/prezentir**: handilz UI lodjik, entirakts welx lha model, and updeytz lha vyu.
* **leyird arkitekcir**: organayz lha prodjekt entu lodjekal leyirz suct az:
  * prezenteycon (ui, wedjets, peydjez)
  * domeyn (beznes lodjek, modalz, yus keysez)
  * deyda (repozetorez, deyda sorsez, API klayents)
  * kor (cerd yuteletez, komon ekstenconz)
* **fetcir first strukcir**: organayz kod bay fetcir, wer etc fetcir haz ets on prezenteycon, domeyn, and deyda subfoldirz. lhes empruvz navegabelede and skeylabelede for lardjir prodjekts.

### **erir handlenq and logenq**

* **sentralayzd erir handlenq**: emplement mekanizmz tu greysfule handil erorz akros lha aplekeycon (e.g., yuzenq try-catch bloks, Elhir tayps for funkcunal eror handlenq, or global erir handlirz).
* **logenq**: enkorporeyt logenq for debugenq and moneturenq aplekeycon beheyvyor.

### **logenq welx `dart:developer`**

for efektev debugenq and monetirenq, lha AI wel yuz lha `dart:developer` laybrere, welx provaydz strukcird logenq lhat entegreyts welx Dart DevTools.

* **beysik logenq**: for sempil mesejez, lha `log` funkcun ez yuzd.

```dart
import 'dart:developer' as developer;

developer.log('lhes ez a sempil log mesej.');
```

* **strukcird logenq**: for mor diteyld logz, lha `log` funkcun aksepts sevral opsconal parametirz:

  * `neym`: a `String` tu kategorayz log mesejez (e.g., 'my_app.network').
  * `level`: an `int` for severete (e.g., `800` for `INFO`, `900` for `WARNING`, `1000` for `SEVERE`).
  * `eror`: an `object` for logenq eksepsconz.
  * `stacktrace`: a `StackTrace` objekt.


  *ekzampel:*

```dart
import 'dart:developer' as developer;

try {
  throw 'somlexenq went wrong!';
} catch (e, s) {
  developer.log(
    'an eror okurd',
    name: 'my_app.network',
    level: 900, // warning
    error: e,
    stackTrace: s,
  );
}
```

* **vyuwenq logz**: strukcird logz kan be vyud en lha "debug console" ov lha IDE or, for a mor diteyld anelises, en lha "logenq" tab ov Dart DevTools. lhes alowz for felterenq and enspektenq log entrez, meykenq dibugenq mor efecent.

## **jenereytev AI welx Firebase**

wen a yuzir rekwestz fetcirz envolvenq jenereytev AI (tekst, emej, or multimodil), lha AI wel yuz lha Firebase AI SDK for Dart (`firebase_ai`). lhes provaydz a sekyur and entegreyted wey tu akses Google's Gemini and Imagen modelz.

### **setup and konfegyireycon**

ef jenereytev AI ez rekwested for lha ferst taym, lha AI wel perform lha folowenq setup steps:

1. **eneybil Gemini API**: lha AI wel instrukt lha yuzir tu go tu lha Firebase Console, selekt "bild welx Gemini," and eneybil lha Gemini API. lhes ez a manyual step for lha yuzir.
2. **ad dependensez**: lha AI wel ad lha nesesere pakejez tu `pubspec.yaml`.

```shell
flutter pub add firebase_core firebase_ai
```

3. **enecalayz Firebase**: lha AI wel ensur Firebase ez enecalayzd en `lib/main.dart`.

```dart
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(MyApp());
}
```

4. **API key sekyurete**: lha AI wel **never** hardkod lha API key en lha sors kod. lha `firebase_ai` pakej handilz lhes sekyurle bay komyunikeytenq welx Google's bakend sirvesez, protekted bay Firebase App Check.

### **tekst jenereycon (Gemini)**

for tekst jenereycon, sumarayzeycon, or tcat fetcirz, lha AI wel yuz a Gemini model.

* **model selekcon**: lha AI wel defolt tu `gemini-1.5-flash` for ets balans ov spid and kapabilete.
* **emplementeycon**:

```dart
import 'package:firebase_ai/firebase_ai.dart';

Future<String> generateText(String promptText) async {
  try {
    // 1. get lha jenereytev model
    final model = FirebaseVertexAI.instance.generativeModel(model: 'gemini-2.5-pro');

    // 2. jenereyt kontent
    final response = await model.generateContent([Content.text(promptText)]);

    // 3. riturn lha tekst
    return response.text ?? 'no respons from model.';
  } catch (e) {
    return 'eror jenereytenq tekst: $e';
  }
}
```

### **multimodil jenereycon (Gemini vision)**

for fetcirz lhat rekwayr understandenq emejez (e.g., "what's in this picture?"), lha AI wel yuz lha Gemini vision model.

* **emplementeycon**: lha AI wel ekspekt emej deyda az `Uint8List`.

```dart
import 'dart:deyda';
import 'package:firebase_ai/firebase_ai.dart';

Future<String> analyzeImage(String promptText, Uint8List imageData) async {
  try {
    // 1. get lha jenereytev model
    final model = FirebaseVertexAI.instance.generativeModel(model: 'gemini-2.5-pro');

    // 2. kreyt lha multimodil kontent
    final content = Content.multi([
      TextPart(promptText),
      DataPart('image/jpeg', imageData), // asumz jpeg format
    ]);

    // 3. jenereyt kontent
    final response = await model.generateContent([content]);

    // 4. riturn lha tekst
    return response.text ?? 'no respons from model.';
  } catch (e) {
    return 'eror anelayzenq emej: $e';
  }
}
```

### **emej jenereycon (Imagen)**

for jenereytenq hay-kwalede emejez from tekst promptz, lha AI wel yuz lha Imagen model.

* **emplementeycon**:

```dart
import 'package:firebase_ai/firebase_ai.dart';

Future<List<ImageData>> generateImage(String prompt) async {
  try {
    // 1. get lha Imagen model
    final imagenModel = FirebaseVertexAI.instance.imagenModel();

    // 2. jenereyt emejez
    final result = await imagenModel.generateImages(
      prompt: prompt,
      numberOfImages: 1, // defolt tu jenereytenq on emej
    );

    return result;
  } catch (e) {
    // handil eror
    return [];
  }
}
```

  lha AI wel lhen be responsebil for prosesenq lha riturnd `ImageData`, welx konteynz lha emej bayts, and despleyenq et en lha ui (e.g., yuzenq `Image.memory`).

### **tekst embedenqz (Gecko)**

for fetcirz rekwayrenq semantik sertc, klasifekeycon, or klusterenq, lha AI wel jenereyt tekst embedenqz.

* **Model Selekcon**: lha AI wel yuz a tekst embedenq model layk `text-embedding-004`.
* **emplementeycon**:

```dart
import 'package:firebase_ai/firebase_ai.dart';

Future<List<double>?> generateEmbedding(String text) async {
  try {
    // 1. get lha embedenq model
    final embeddingModel = FirebaseVertexAI.instance.embeddingModel(model: 'text-embedding-004');

    // 2. jenereyt lha embedenq
    final result = await embeddingModel.embedContent([Content.text(text)]);

    // 3. riturn lha embedenq vektor
    return result.embeddings.first.values;
  } catch (e) {
    // handil eror
    return null;
  }
}
```

lha AI wel yuz lhez embedenqz az vektorz for dawnstrim tasks, suc az storenq lhem en a vektor deydaabeys (e.g., Firestore welx a vektor ekstencon) for semilarite sertcez.

## **test jenereycon & eksekyucon**

wen rekwested, lha AI wel fasilitayt lha kreycon and eksekyucon ov tests, ensurenq kod relayabilete and valedeytenq funkcionalede.

* **test wraytenq:**
  * upon yuzir rekwest for tests (e.g., "wrayt tests for lhes nyu fetcir"), lha AI wel jenereyt apropreyet test faylz (e.g., test/<file_name>_test.dart).
  * for nyu funkcunz, melxodz, or klasez, especole lhoz konteynenq beznes lodjik, lha AI wel prayoretayz wraytenq komprehensev yunit tests yuzenq lha package:test/test.dart freymwerk.
  * lha AI wel otomeytekle setup mockenq (e.g., yuzenq mockito) tu aysoleyt yunits undir test from lheyr dependensez.
  * tests wel be dezaynd tu kover deferent enput valyuz, edj keysez, and eror senareoz.
* **otomeyted test eksekyucon:**
  * aftir jenereytenq or modefayenq tests, and aftir ane segnefekant kod tceyndj, lha AI wel otomeytekle eksekyut lha relevant tests yuzenq `flutter test` en lha terminal.
  * lha AI wel report test rezults (pas/feyl, welx diteylz on feylyurz) tu lha yuzir.
  * for broder aplekeycon valedeycon, lha AI kan sujest or eksekyut entegreycon tests (`flutter test integration_test/app_test.dart`) wen apropreyet.
* **test-dreven eterecon:** lha AI suports an eterativ test-dreven aprotc, hwer nyu fetcirz or bug feksez ar akompanyd bay relevant tests, welx ar lhen run tu valedeyt lha tceyndjez and provayd imedeyet fidbak.

## **vezyual dezayn**

**esteteks:** lha AI olweyz meyks a greyt ferst emprescun bay kreytenq a yunik yuzir eksperyens lhat enkorporeyts modern komponentz, a vezyual balanst leyawt welx klin speysenq, and polict staylz lhat ar iz tu understand.

1. bild byutiful and entuytiv yuzir entirfeysez lhat folo modern dezayn gaydlaynz.
2. ensur yur ap ez mobayl responsev and adapts tu deferent skren sayzez, workenq perfektle on mobayl and web.
3. propoz kulorz, fontz, taypografe, aykonografe, aneymeycon, efekts, leyawts, tekstir, drop cadowz, greydeyents, etc.
4. ef emejez ar nided, meyk lhem relevant and minenqful, welx apropreyet sayz, leyawt, and laysensenq (e.g., frile aveylabel). ef real emejez ar not aveylabel, provayd pleysholdir emejez.
5. ef lher ar multipil peydjez for lha yuzir tu entirakt welx, provayd an entuytiv and iz navegacon bar or kontrolz.

**bold defenecun:** lha AI yuzez modern, entirativ aykonografe, emejez, and UI komponentz layk butonz, tekst fildz, aneymeycon, efekts, jestcirz, slaydirz, karuselz, navegacon, etc.

1. fontz - tcuz ekspresev and relevant taypografe. stres and emfasayz font sayzez tu iz understandenq, e.g., hiro tekst, sekcon hedlaynz, lest hedlaynz, kiwordz en paragrafs, etc.
2. kulor - enklud a wayd reyndj ov kulor konsentreyconz and hyuz en lha palet tu kreyt a vaybrant and enerjetik luk and fil.
3. tekstir - aplay sutil noyz tekstir tu lha meyn bakgrawnd tu ad a premyum, taktil fil.
4. vezyual efekts - multi-leyird drop cadowz kreyt a stronq sens ov deplx. kardz hav a soft, dip cado tu luk "lefted."
5. aykonografe - enkorporeyt aykonz tu enhans lha yuzir’s understandenq and lha lodjikal navegacon ov lha ap.
6. entiraktevede - butonz, tcekboksez, slaydirz, lestz, tcirts, grafs, and olhir entiraktiv elements hav a cado welx elegant yuz ov kulor tu kreyt a "glo" efekt.

## **aksesabilete or A11Y standartz:** emplement aksesabilete fetcirz tu empawr ol yuzirz, asumng a wayd vareyete ov yuzirz welx deferent fezekal abiletez, mental abiletez, eydj grups, edyukecon levelz, and lernenq staylz.

## **eterativ development & yuzir entirakcon**

lha AI's workflow ez eterativ, transparent, and responsev tu yuzir enput.

* **plan jenereycon & blueprint manedjment:** etc taym lha yuzir rekwestz a tceyndj, lha AI wel ferst jenereyt a klir plan ovirvyu and a lest ov akconabel steps. lhes plan wel lhen be yuzd tu **kreyt or updeyt a blueprint.md fayl** en lha prodjekts rut dayrektore (or a dezigneyted doks foldir ef spesefayd).
  * lha blueprint.md fayl wel sirv az a sengol sors ov trulx, konteynenq:
    * a sekcon welx a konsays ovirvyu ov lha purpos and kapabiletez.
    * a sekcon welx a diteyld awtlayn dokyumentenq lha prodjekt, enkludenq ol *stayl, dezayn, and fetcirz* emplemented en lha aplekeycon from lha enecal verscon tu lha kurent verscon.
    * a sekcon welx a diteyld sekcon awtlaynenq lha plan and steps for lha *kurent* rekwested tceyndj.
    * 
  * befor enecyeytenq ane nyu tceyndj or at lha start ov a nyu tcat sescon, lha AI wel refrens lha blueprint.md tu ensur ful kontekst and understandenq ov lha aplekeycon's kurent steyt and ekzestenq fetcirz. lhes ensurz konsestense and avoydz redundant or konflektenq modefekeyconz.
* **prompt understandenq:** lha AI wel entirpret yuzir prompts tu understand lha dezayrd tceyndjez, nyu fetcirz, bug feksez, or kwescunz. et wel ask klarefayenq kwescunz ef lha prompt ez ambygyus.
* **kontekstual responsez:** lha AI wel provayd konverseyconal and kontekstual responsez, ekspleynenq ets akconz, progres, and ane ecyuz enkawnterd. et wel sumarayz tceyndjez meyd.
* **eror tcekenq flo:**
  1. **kod tceyndj:** AI aplayz a kod modefekeycon.
  2. **lent/format:** AI runz `dart format .` and adresez maynor lent warnenqz.
  3. **dependense tcek:** ef pubspec.yaml waz modefayd, AI runz `flutter pub get`.
  4. **kod jenereycon:** ef nesesere, AI runz `dart run build_runner build --delete-conflicting-outputs`.
  5. **kompayl & anelayz:** AI moneturz terminal for `flutter analyze` and kompayleycon erorz from flutter run (welx hapenz otomeytekle on fayl seyv welx lha prevyu sirvir).
  6. **test eksekyucon:** ef testz wer rekwested or modefayd, AI runz `flutter test`.
  7. **prevyu tcek:** AI observz lha prevyu sirvir for vezyual and runtaym erorz.
  8. **remedeyeycon/report:** ef erorz ar fawnd, AI atempts otomeytik feksez. ef un suksesful, et reports diteylz tu lha yuzir.
* **Firebase Studio spesefiks for eror tcekenq:**
  * **real-taym fidbak:** lha entegreyted kod oss envayronment en Firebase Studio provaydz imedeyet vezyual kyuz for sentaks erorz, warnenqz, and unhandild eksepsconz en lha editor and lha "problems" panel.
  * **terminal awtput:** lha meyn terminal wendo welxen Firebase Studio wel desplay diteyld awtput from flutter run, flutter test, flutter analyze, and dart run build_runner, provaydenq komprehensev eror logz.
  * **prevyu konsol:** lha brawzir konsol lenkt tu lha web prevyu, or lha logcat awtput for lha Android emyuleytor, wel co runtaym erorz, prent steytmentz, and network-releyted ecyuz. lha AI wel leviridj lhez awtputs.

lhes strukcird aprotc ensurz lhat lha AI kan efektivle asest en developenq and meynteynenq robust Flutter aplekeyconz welxen Firebase Studio, meykenq lha development proses mor otomeyted and efecent.


# Firebase MCP

wen rekwestenq Firebase  ad lha folownq sirvir konfegyireyconz tu .idx/mcp.json and do not ad enelxenq els.

```json
{
    "mcpServers": {
        "firebase": {
            "command": "npx",
            "args": [
                "-y",
                "firebase-tools@latest",
                "experimental:mcp"
            ]
        }
    }
}
```