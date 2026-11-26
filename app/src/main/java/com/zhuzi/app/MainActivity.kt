package com.zhuzi.app

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.zhuzi.app.data.Progress
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.Streak
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.reminder.Reminders
import com.zhuzi.app.ui.LessonListScreen
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.ReviewScreen
import com.zhuzi.app.ui.Speaker
import com.zhuzi.app.ui.theme.ZhuziTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Écran affiché : la liste des leçons, la révision du jour, ou une leçon (son numéro). */
private const val HOME = 0
private const val REVIEW = -1

class MainActivity : ComponentActivity() {
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fond blanc : icônes de la barre d'état toujours sombres, même en mode sombre du téléphone
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        speaker = Speaker(this)
        val progress = Progress(this)
        val srs = Srs(this)
        val streak = Streak(this)
        val allWords = hsk1Lessons.flatMap { it.words }
        val settings = getSharedPreferences("settings", MODE_PRIVATE)
        Reminders.schedule(this)

        setContent {
            ZhuziTheme {
                var screen by rememberSaveable { mutableIntStateOf(HOME) }
                // Change à chaque progrès pour rafraîchir l'accueil
                var version by rememberSaveable { mutableIntStateOf(0) }
                val goHome: () -> Unit = { screen = HOME; version++ }

                // Accord pour les notifications (demandé une seule fois automatiquement)
                var reminderOn by remember { mutableStateOf(Reminders.canNotify(this@MainActivity)) }
                var fromButton by remember { mutableStateOf(false) }
                val askPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    reminderOn = granted
                    // Refus définitif : on ouvre les réglages de notification de l'appli
                    if (!granted && fromButton) {
                        startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= 33 && !reminderOn &&
                        !settings.getBoolean("asked_notifications", false)
                    ) {
                        settings.edit().putBoolean("asked_notifications", true).apply()
                        askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                Scaffold { padding ->
                    val lesson = hsk1Lessons.firstOrNull { it.id == screen }
                    when {
                        screen == REVIEW -> {
                            BackHandler(onBack = goHome)
                            ReviewScreen(
                                dueWords = srs.dueWords(allWords),
                                speak = speaker::speak,
                                onAnswer = { word, knew ->
                                    srs.answer(word, knew)
                                    streak.markActive()
                                },
                                onBack = goHome,
                                contentPadding = padding,
                            )
                        }
                        lesson != null -> {
                            BackHandler(onBack = goHome)
                            LessonScreen(
                                lesson = lesson,
                                speak = speaker::speak,
                                onBack = goHome,
                                onQuizDone = { mode, percent ->
                                    progress.saveScore(lesson.id, mode, percent)
                                },
                                onSeen = { word ->
                                    srs.learn(word)
                                    streak.markActive()
                                },
                                onQuizAnswer = { word, correct ->
                                    srs.quizAnswer(word, correct)
                                    streak.markActive()
                                },
                                contentPadding = padding,
                            )
                        }
                        else -> {
                            // Lire « version » force le recalcul après chaque retour à l'accueil
                            val v = version
                            LessonListScreen(
                                bestScore = { id, mode -> if (v >= 0) progress.bestScore(id, mode) else -1 },
                                lessonKnown = { l -> srs.knownCount(l.words) },
                                onOpen = { screen = it.id },
                                todayLabel = todayLabel(),
                                dueWords = srs.dueWords(allWords),
                                dueTomorrow = srs.dueTomorrow(allWords),
                                knownCount = srs.knownCount(allWords),
                                masteredCount = srs.masteredCount(allWords),
                                onReview = { screen = REVIEW },
                                streakDays = streak.current(),
                                reminderOn = reminderOn,
                                onEnableReminder = {
                                    fromButton = true
                                    askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
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

/** « Mercredi 26 novembre » */
private fun todayLabel(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH))
        .replaceFirstChar { it.titlecase(Locale.FRENCH) }
