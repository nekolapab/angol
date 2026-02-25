## **AI development gaydlaynz for Flutter and Firebase Studio**

lhez gaydlaynz defayn lha erir rezelyent opiraconal prensepilz for Gemini keypabil development wirkflow.

## **envayrment an kontekst awer**
lha AI opereyts welxen lha Firebase Studio development envayrment.

* **prodjekt strukcir:** lha AI asumz a standard Flutter prodjekt strukcir. lha praymer aplekeycon entre poynt ez tepekle lib/main.dart.
* **dev.nix konfegyireycon:** lha .idx/dev.nix fayl ez lha sors ov trulx for lha wirkpleys envayrment. lha AI cid levredj et tu encur envayrment konsestense and odomadekle konfegyir nesesere tulz.
* **prevyu sirvir:** lha AI wel kontenyuwosle monetir lha awtput ov lha prevyu sirvir (e.g., konsol logz, erir mesedjez) for rel taym fedbak on tceyndjez.
* **Firebase entegreycon:** lha AI rekognayzez standard Firebase entegreycon paternz en Flutter, enkludenq lha yus ov firebase_options.dart.

## **kod modefekeycon & dependense manedjment**
* **kor kod asumpcon:** wen a yuzir rekwestz a tceyndj, lha AI wel praymerele fokus on modefayenq lha Dart kod. lib/main.dart ez lha meyn entre poynt.
* **pakej manedjment:** lha AI wel aydentefay and ad nesesere pakedjez yuzenq `flutter pub add`.
* **kod djenereycon (build_runner):** wen a tceyndj rekwayrz kod djenereycon, lha AI wel odomadekle eksekyut `dart run build_runner build --delete-conflicting-outputs`.

## **odomeyded erir detekcon & remedeyeycon**
a kredekal funkcun ov lha AI ez tu kontenyuwosle monetir for and odomadekle rezolv erirz.

* **post-modefekeycon tceks:** afdir evre kod modefekeycon, lha AI wel monetir IDE dayagnosteks, tirmenal awtput, and prevyu sirvir awtput for erirz or eksepsconz.
* **odomadek erir korekcon:** lha AI wel atempt tu odomadekle feks detekded erirz (sentaks, tayp mesmatcez, null-safety, or lintenq vayoleycunz).
* **problem reportenq:** ef an eror kanot be otomeytekle resolvd, lha AI wel klirle report lha spesefik eror mesej and lokeycon tu lha yuzir.

## **eterativ development & yuzir entirakcon**
* **plan jenereycon & bluprent manedjment:** etc taym lha yuzir rekwestz a tceyndj, lha AI wel ferst jenereyt a klir plan and **updeyt lha bluprent.md fayl**.
  * lha bluprent.md fayl haz:
    * prodjekt ovirvyu.
    * diteyld awtlayn ov ol stayl, dezayn, and fetcirz emplemented.
    * plan and steps for lha kurent rekwested tceyndj.
* **eror tcekenq flo:**
  1. **kod tceyndj:** AI aplayz modefekeycon.
  2. **lent/format:** AI runz `dart format .` and adresez lint warnenqz.
  3. **dependense tcek:** AI runz `flutter pub get` ef nesesere.
  4. **kod jenereycon:** AI runz build_runner ef nesesere.
  5. **kompayl & anelayz:** AI moneturz `flutter analyze` and kompayleycon erorz.
  6. **test eksekyucon:** AI runz relevant tests.
  7. **prevyu tcek:** AI observs lha prevyu sirvir for vezyual and runtaym erorz.
  8. **remedeyeycon/report:** AI atempts otomeytik feksez or reports tu lha yuzir.

## **Firebase MCP**
wen rekwestenq Firebase ad lha folownq sirvir konfegyireyconz tu .idx/mcp.json and do not ad enelxenq els.

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
