## **memorez**
*yuz angolec spelenq  az protokold en blueprint.md and GEMINI.md  for fayl and foldir neymz en /lib/ and updeyt refrensez globale for etc neym tceynj. so du not reneym tu standard englec eksept for Flutter standard konvencon neymz and freymwerk klasez sutc az lib\main.dart an lib\firebase_options.dart and ChangeNotifier, StatelessWidget,...
* olweyz yuz angolec spelenq en ol awtput tekst, and en bluprent and GEMINI.MD az wel.

## Gemini Added Memories
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user prefers responses in "angolec" style, a form of phonetic English spelling. I should adopt this style for my text-based communication with them.
- The user wants to implement a "fast number" feature where pressing a vowel on the inner ring displays numbers on the outer ring. Swiping to a number selects it, otherwise the vowel is typed.

Lessons learned from previous attempts:
- Combining tap and drag/long-press gestures on the same GestureDetector for the same element leads to gesture conflicts and unreliable behavior.
- Assuming that `onPanEnd` or `onLongPressEnd` will always fire after a `onPanStart` or `onLongPressStart` respectively is unreliable, especially if the gesture is very short or cancelled. This can lead to corrupted state if `exitFastNumberMode` is not called.
- Implementing complex gesture recognition logic directly within the `build` method of a StatefulWidget if it involves iterating through many GlobalKeys and performing hit-testing on every frame can lead to performance issues.

Plan for tomorrow:
1. Simplify the gesture detection: Consider having separate GestureDetectors or a more explicit state machine for gesture recognition.
2. Focus on the UI update: Ensure that when `enterFastNumberMode` is called, the `KepadModyil` rebuilds correctly and the `AwdirRenqWedjet` receives the updated labels. Use more logging to confirm this.
3. Revisit the "other problems": Ask the user to describe the "tuw maynir problemz agen" so they can be addressed.
- The user wants to continue the task of refactoring the fast number gesture detection in `KepadModyil` tomorrow. The current plan is to introduce a top-level `GestureDetector` and move the long press logic, starting with adding `_longPressStartOffset` to `_KepadModyilState`.

---
## **Kompoz Entigreycon Plan**

**kurent steyt:**
*   wi ar atemptenq tu entegreyt Kompoz Multiplatform entu lha Android part ov a Flutter prodjekt.
*   `android/app/build.gradle.kts` haz bin updeyted welx Kompoz dependensez and `buildFeatures`.
*   `MainActivity.kt` haz bin temporarele modefayd tu co a sempil Kompoz `Text` for testenq lha setup.
*   lha bild iz kurentle feylenq welx lha mesej: "Plagen [id: 'org.jetbrains.kotlin.plugin.compose'] waz not fawnd."

**problem:**
Greydl iz unable tu korektle rezolv or apli lha `org.jetbrains.kotlin.plugin.compose` plagen, even afdir trayenq tu ad et en vereyus `build.gradle.kts` and `settings.gradle.kts` lokeycunz.

**plan for tomorow:**

1.  **Re-evaluyat Kompoz Plagen Aplekeycon:**
    *   lha `org.jetbrains.kotlin.plugin.compose` plagen iz not a standard plagen tu be aded welx `apply false` en `settings.gradle.kts`. et iz tepekale aplaid dayrektle en lha modyul's `build.gradle.kts` (`android/app/build.gradle.kts`) *afdir* lha `kotlin-android` plagen, and ets artefakt iz implisetle provayded by lha Kotlin Greydl plagen.
    *   lha eror "Plagen [id: 'org.jetbrains.kotlin.plugin.compose'] waz not fawnd" wen aplaid en `android/app/build.gradle.kts` sujesz lhat lha korekt ID iz `org.jetbrains.kotlin.compose` (welxawt `.plugin`) or lhat lha Kotlin Greydl plagen versun iz not korektle pullenq en lha Kompoz kompaylir.

2.  **Modefay `android/app/build.gradle.kts`:**
    *   ay wel encur `id("kotlin-android")` iz prezent.
    *   ay wel ad `id("org.jetbrains.kotlin.compose")` tu lha `plugins` blok welxen `android/app/build.gradle.kts`. (lhes iz lha korekt plagen ID welxawt `.plugin`.)