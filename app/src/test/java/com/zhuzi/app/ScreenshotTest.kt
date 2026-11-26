package com.zhuzi.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.zhuzi.app.ui.theme.Kai
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.ui.LessonListScreen
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.ReviewScreen
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

    private fun shot(name: String) = compose.onRoot().captureRoboImage("build/screens/$name.png")

    private fun home(due: Int, knownTotal: Int, streak: Int, night: Boolean = false) {
        compose.setContent {
            ZhuziTheme(darkTheme = night) {
                Surface {
                    LessonListScreen(
                        bestScore = { id, m -> scores[id]?.get(m) ?: -1 },
                        lessonKnown = { l -> if (knownTotal > 0) known[l.id] ?: 0 else 0 },
                        onOpen = {},
                        dueWords = all.take(due),
                        dueTomorrow = 5,
                        knownCount = knownTotal,
                        masteredCount = knownTotal / 3,
                        onReview = {},
                        streakDays = streak,
                        todayLabel = "Mercredi 26 novembre",
                        reminderOn = true,
                        onEnableReminder = {},
                        contentPadding = bars,
                    )
                }
            }
        }
    }

    @Test
    fun accueil() {
        home(due = 8, knownTotal = 37, streak = 12)
        shot("1-accueil")
    }

    @Test
    fun accueilNuit() {
        home(due = 8, knownTotal = 37, streak = 12, night = true)
        shot("9-accueil-nuit")
    }

    /** Essai : la police pinceau chargée de trois façons, pour vérifier son affichage. */
    @Test
    fun polices() {
        compose.setContent {
            val ctx = LocalContext.current
            val tf = ResourcesCompat.getFont(ctx, R.font.wenkai)
            ZhuziTheme {
                Surface {
                    Column(Modifier.padding(24.dp)) {
                        Text("Compose Font(R.font) :", fontSize = 14.sp)
                        Text("你好谢谢竹子", fontFamily = Kai, fontSize = 44.sp)
                        Text("ResourcesCompat (${if (tf == null) "null" else "ok"}) :", fontSize = 14.sp)
                        if (tf != null) Text("你好谢谢竹子", fontFamily = FontFamily(Typeface(tf)), fontSize = 44.sp)
                        Text("Police par défaut :", fontSize = 14.sp)
                        Text("你好谢谢竹子", fontSize = 44.sp)
                    }
                }
            }
        }
        shot("0-polices")
    }

    @Test
    fun accueilPremierLancement() {
        home(due = 0, knownTotal = 0, streak = 0)
        shot("2-accueil-vide")
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
