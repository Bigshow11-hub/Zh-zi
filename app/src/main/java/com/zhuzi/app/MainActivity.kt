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
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.ui.LessonListScreen
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.Speaker
import com.zhuzi.app.ui.theme.ZhuziTheme

class MainActivity : ComponentActivity() {
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        speaker = Speaker(this)
        val progress = Progress(this)

        setContent {
            ZhuziTheme {
                // 0 = liste des leçons, sinon numéro de la leçon ouverte
                var openLesson by rememberSaveable { mutableIntStateOf(0) }
                // Change à chaque nouveau score pour rafraîchir la liste
                var scoreVersion by rememberSaveable { mutableIntStateOf(0) }

                Scaffold { padding ->
                    val lesson = hsk1Lessons.firstOrNull { it.id == openLesson }
                    if (lesson == null) {
                        LessonListScreen(
                            bestScore = { id -> if (scoreVersion >= 0) progress.bestScore(id) else -1 },
                            onOpen = { openLesson = it.id },
                            contentPadding = padding,
                        )
                    } else {
                        BackHandler { openLesson = 0 }
                        LessonScreen(
                            lesson = lesson,
                            speaker = speaker,
                            onBack = { openLesson = 0 },
                            onQuizDone = { percent ->
                                progress.saveScore(lesson.id, percent)
                                scoreVersion++
                            },
                            contentPadding = padding,
                        )
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
