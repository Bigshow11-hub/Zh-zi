package com.zhuzi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.zhuzi.app.data.Progress
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.ui.LessonListScreen
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.ReviewScreen
import com.zhuzi.app.ui.Speaker
import com.zhuzi.app.ui.theme.ZhuziTheme

/** Écran affiché : la liste des leçons, la révision du jour, ou une leçon (son numéro). */
private const val HOME = 0
private const val REVIEW = -1

class MainActivity : ComponentActivity() {
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        speaker = Speaker(this)
        val progress = Progress(this)
        val srs = Srs(this)
        val allWords = hsk1Lessons.flatMap { it.words }

        setContent {
            ZhuziTheme {
                var screen by rememberSaveable { mutableIntStateOf(HOME) }
                // Change à chaque progrès pour rafraîchir l'accueil
                var version by rememberSaveable { mutableIntStateOf(0) }
                val goHome: () -> Unit = { screen = HOME; version++ }

                Scaffold { padding ->
                    val lesson = hsk1Lessons.firstOrNull { it.id == screen }
                    when {
                        screen == REVIEW -> {
                            BackHandler(onBack = goHome)
                            ReviewScreen(
                                dueWords = srs.dueWords(allWords),
                                speaker = speaker,
                                onAnswer = { word, knew -> srs.answer(word, knew) },
                                onBack = goHome,
                                contentPadding = padding,
                            )
                        }
                        lesson != null -> {
                            BackHandler(onBack = goHome)
                            LessonScreen(
                                lesson = lesson,
                                speaker = speaker,
                                onBack = goHome,
                                onQuizDone = { mode, percent ->
                                    progress.saveScore(lesson.id, mode, percent)
                                },
                                onSeen = { word -> srs.learn(word) },
                                onQuizAnswer = { word, correct -> srs.quizAnswer(word, correct) },
                                contentPadding = padding,
                            )
                        }
                        else -> {
                            // Lire « version » force le recalcul après chaque retour à l'accueil
                            val v = version
                            LessonListScreen(
                                bestScore = { id, mode -> if (v >= 0) progress.bestScore(id, mode) else -1 },
                                onOpen = { screen = it.id },
                                dueCount = srs.dueWords(allWords).size,
                                dueTomorrow = srs.dueTomorrow(allWords),
                                knownCount = srs.knownCount(allWords),
                                masteredCount = srs.masteredCount(allWords),
                                onReview = { screen = REVIEW },
                                contentPadding = padding,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }
}
