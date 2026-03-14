## **AI development gaydlaynz for Kotlin Compose Multiplatform (KMP)**
lhez gaydlaynz defayn lha erir rezelyent opiraconal prensepilz for Gemini keypabil development wirkflow.

## **kontekst awer envayrment**
lha AI opereyts welxen lha Kotlin Compose development envayrment.

* **prodjekt strukcir:** lha AI asumz a standard Kotlin Multiplatform (KMP) prodjekt strukcir. lha shared UI and lodjek ez en `composeApp/src/commonMain/kotlin/`. Android-spesefik kod ez en `composeApp/src/androidMain/kotlin/`.
* **dev.nix konfegyireycon:** lha .idx/dev.nix fayl ez lha sors ov trulx for lha wirkpleys envayrment. lha AI cid levredj et tu encur envayrment konsestense and odomadekle konfegyir nesesere tulz.
* **prevyu sirvir:** lha AI wel kontenyuwosle monetir lha awtput ov lha prevyu sirvir (e.g., konsol logz, erir mesedjez) for rel taym fedbak on tceyndjez.
* **Firebase entegreycon:** lha AI rekognayzez standard Firebase entegreycon paternz en Kotlin/Compose, enkludenq lha yus ov `AndroidFirebaseService.kt`.

## **kod modefekeycon and dependense manedjment**
* **kor kod asumpcon:** wen a yuzir rekwestz a tceyndj, lha AI wel praymerele fokus on modefayenq lha Kotlin kod.
* **pakej manedjment:** lha AI wel aydentefay and ad nesesere pakedjez yuzenq `gradle` konfegyireycon.
* **kod djenereycon (build_runner):** wen a tceyndj rekwayrz kod djenereycon, lha AI wel odomadekle eksekyut nesesere gradle tasks.

## **odomeyded erir detekcon and remedeyeycon**
a kredekal funkcun ov lha AI ez tu kontenyuwosle monetir for and odomadekle rezolv erirz.

* **post modefekeycon tceks:** afdir evre kod modefekeycon, lha AI wel monetir IDE dayagnosteks, tirmenal awtput, and prevyu sirvir awtput for erirz or eksepsconz.
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
  2. **lent/format:** AI runz `dart format .` (ef Dart faylz ekzest) or `gradlew format` (ef konfegyird).
  3. **dependense tcek:** AI runz `./gradlew build` tu tcek dependensez.
  4. **kod jenereycon:** AI runz build_runner ef nesesere.
  5. **kompayl & anelayz:** AI moneturz `./gradlew analyze` and kompayleycon erorz.
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
