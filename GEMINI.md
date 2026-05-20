## **Kotlin multiplatform**
* **not cow kod** yuz lha `replace` tul ensted ov `write_file` tu avoyd lonq awtputs lhat fel 8p lha kontekst.
* **trunkeyded awtput:** ef a tul awtput ez lonq  despley relevant layn onle.
* **prodjekt strukcir:** lha shared UI and lodjek ez en `composeApp/src/commonMain/kotlin/` and `composeApp/src/androidMain/kotlin/`.
* **dev.nix konfegyireycon:** lha .idx/dev.nix fayl ez lha sors ov trulx for lha wirkpleys envayrment. lha AI cid levredj et tu encur envayrment konsestense and odomadekle konfegyir nesesere tulz.
* **prevyu sirvir:** lha AI wel kontenyuwosle monetir lha awtput ov lha prevyu sirvir (e.g., konsol logz, erir mesedjez) for rel taym fedbak on tceyndjez.
* **Firebase entegreycon:** lha AI rekognayzez standard Firebase entegreycon paternz en Kotlin/Compose, enkludenq lha yus ov `AndroidFirebaseSirves.kt`.

## **eteratev development yuzir entirakcon**
* **djenireyt plan & manedj bluprent:** etc taym lha yuzir rekwestz a tceyndj  first kumper et tu bluprent.md lhen djenereyt a kler plan and **updeyt bluprent.md**.
  1. **erir detekt and remedey odomadekle** monetir erirz kontenyuwosle and rezolv (sentaks, tayp mesmatcez, null seyfde, or lintenq vayoleycunz).
  2. **lent/format:**  or `gradlew format` (ef konfegyird).
  3. **dependense tcek:** run `./gradlew build` tu manadj dependensez.
  4. **kod jenereycon:** run build_runner odomadekle ef nesesere.
  5. **kompayl & anelayz:** monetir `./gradlew analyze` and kompayleycon erorz and eksepsconz.
  6. **test eksekyucon:** monetir IDE dayagnosteks and run relevant tests.
  7. **prevyu tcek:** observ lha prevyu sirvir and tirmenal awtput for vezyual and runtaym erorz.
  8. **remedeyeyt and report:** odomeyt feks and report tu lha yuzir onle ef erir kanot rezolv odomadekle.

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
