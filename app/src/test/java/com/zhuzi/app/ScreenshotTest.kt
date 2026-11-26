package com.zhuzi.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import androidx.compose.runtime.CompositionLocalProvider
import com.zhuzi.app.data.Lang
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.ui.LocalLang
import com.zhuzi.app.data.Word
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.ui.CharactersScreen
import com.zhuzi.app.ui.HomeScreen
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.LessonsScreen
import com.zhuzi.app.ui.MainFrame
import com.zhuzi.app.ui.ProgressScreen
import com.zhuzi.app.ui.ReviewScreen
import com.zhuzi.app.ui.Tab
import com.zhuzi.app.ui.theme.ZhuziTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Photos des écrans principaux, avec des données d'exemple.
 * Lancé sur GitHub : les images sont publiées à côté de l'APK.
 */
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w393dp-h852dp-xxhdpi")
class ScreenshotTest {
    @get:Rule
    val compose = createComposeRule()

    private val bars = PaddingValues(top = 24.dp, bottom = 24.dp)
    private val all = hsk1Lessons.flatMap { it.words }
    private val known = mapOf(1 to 12, 2 to 14, 3 to 9, 4 to 2)
    private val scores = mapOf(
        1 to mapOf(QuizMode.MEANING to 100, QuizMode.WRITING to 90, QuizMode.LISTENING to 85),
        2 to mapOf(QuizMode.MEANING to 80),
    )
    private val boxes: (Word) -> Int = { w ->
        val i = all.indexOf(w)
        when {
            i < 20 -> 4
            i < 37 -> 2
            else -> 0
        }
    }

    private fun shot(name: String) = compose.onRoot().captureRoboImage("build/screens/$name.png")

    /** Un onglet dans le cadre avec la barre du bas. */
    private fun inTab(
        tab: Tab,
        night: Boolean = false,
        lang: Lang = Lang.FR,
        content: @Composable (PaddingValues) -> Unit,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalLang provides lang) {
                ZhuziTheme(darkTheme = night) { MainFrame(tab, {}) { content(it) } }
            }
        }
    }

    @Test
    fun accueilAnglais() {
        inTab(Tab.HOME, lang = Lang.EN) { home(it) }
        shot("15-accueil-anglais")
    }

    @Test
    fun caracteresRusse() {
        inTab(Tab.CHARS, lang = Lang.RU) { CharactersScreen(boxOf = boxes, speak = {}, contentPadding = it) }
        shot("16-caracteres-russe")
    }

    @Composable
    private fun home(p: PaddingValues, due: Int = 8, knownTotal: Int = 37, streak: Int = 12) = HomeScreen(
        todayLabel = "Mercredi 26 novembre",
        streakDays = streak,
        dueWords = all.take(due),
        dueTomorrow = 5,
        knownCount = knownTotal,
        masteredCount = knownTotal / 3,
        total = all.size,
        next = hsk1Lessons[2],
        nextKnown = if (knownTotal > 0) 9 else 0,
        wordOfDay = all[40],
        speak = {},
        onReview = {},
        onOpen = {},
        contentPadding = p,
    )

    @Test
    fun accueil() {
        inTab(Tab.HOME) { home(it) }
        shot("1-accueil")
    }

    @Test
    fun accueilPremierLancement() {
        inTab(Tab.HOME) { home(it, due = 0, knownTotal = 0, streak = 0) }
        shot("2-accueil-vide")
    }

    @Test
    fun accueilNuit() {
        inTab(Tab.HOME, night = true) { home(it) }
        shot("9-accueil-nuit")
    }

    @Test
    fun lecons() {
        inTab(Tab.LESSONS) {
            LessonsScreen(
                bestScore = { id, m -> scores[id]?.get(m) ?: -1 },
                lessonKnown = { l -> known[l.id] ?: 0 },
                onOpen = {},
                contentPadding = it,
            )
        }
        shot("10-lecons")
    }

    @Test
    fun caracteres() {
        inTab(Tab.CHARS) { CharactersScreen(boxOf = boxes, speak = {}, contentPadding = it) }
        shot("11-caracteres")
        compose.onNodeWithText(all[1].pinyin).performClick()
        compose.waitForIdle()
        // La fiche s'ouvre dans un panneau à part : on photographie tout l'écran
        captureScreenRoboImage("build/screens/12-fiche-mot.png")
    }

    @Test
    fun progres() {
        inTab(Tab.PROGRESS) {
            ProgressScreen(
                streakDays = 12, knownCount = 37, masteredCount = 20, total = all.size, dueTomorrow = 5,
                lessonsMastered = 1, lessonsTotal = hsk1Lessons.size, reminderOn = true,
                onReminderChange = {}, versionName = "0.9.0", lang = Lang.FR, onLangChange = {},
                contentPadding = it,
            )
        }
        shot("13-progres")
    }

    @Test
    fun traits() {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            ZhuziTheme {
                Surface {
                    androidx.compose.foundation.layout.Column(
                        androidx.compose.ui.Modifier.padding(PaddingValues(24.dp)),
                    ) { com.zhuzi.app.ui.StrokePanel(all[0]) }
                }
            }
        }
        // Au milieu de l'animation : quelques traits finis, un en cours
        compose.mainClock.advanceTimeBy(1500)
        shot("14-ordre-traits")
    }

    private fun lesson(tab: Int) {
        compose.setContent {
            ZhuziTheme {
                Surface {
                    LessonScreen(
                        lesson = hsk1Lessons[0], speak = {}, onBack = {}, onQuizDone = { _, _ -> },
                        onSeen = {}, onQuizAnswer = { _, _ -> }, contentPadding = bars, startTab = tab,
                    )
                }
            }
        }
    }

    @Test
    fun carte() {
        lesson(0)
        shot("3-carte-recto")
        compose.onNodeWithText("Touche la carte pour la retourner").performClick()
        compose.waitForIdle()
        shot("4-carte-verso")
    }

    @Test
    fun quiz() {
        lesson(1)
        shot("5-quiz-question")
        // Clique la première réponse proposée qu'on trouve à l'écran
        val choice = hsk1Lessons[0].words.first {
            compose.onAllNodesWithText(it.french).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodesWithText(choice.french)[0].performClick()
        compose.waitForIdle()
        shot("6-quiz-reponse")
    }

    @Test
    fun revision() {
        compose.setContent {
            ZhuziTheme {
                Surface {
                    ReviewScreen(dueWords = listOf(all[1]), speak = {}, onAnswer = { _, _ -> },
                        onBack = {}, contentPadding = bars)
                }
            }
        }
        shot("7-revision")
        compose.onNodeWithText("Voir la réponse").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Je savais").performClick()
        compose.waitForIdle()
        shot("8-revision-terminee")
    }
}
