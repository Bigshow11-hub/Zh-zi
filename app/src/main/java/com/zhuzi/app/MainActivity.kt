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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import com.zhuzi.app.data.Lang
import com.zhuzi.app.data.Progress
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.Streak
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.reminder.Reminders
import com.zhuzi.app.ui.CharactersScreen
import com.zhuzi.app.ui.HomeScreen
import com.zhuzi.app.ui.LangPref
import com.zhuzi.app.ui.LocalLang
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.LessonsScreen
import com.zhuzi.app.ui.MainFrame
import com.zhuzi.app.ui.ProgressScreen
import com.zhuzi.app.ui.ReviewScreen
import com.zhuzi.app.ui.Speaker
import com.zhuzi.app.ui.Tab
import com.zhuzi.app.ui.theme.ZhuziTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Écran plein (sans barre du bas) : aucun, la révision du jour, ou une leçon (son numéro). */
private const val NONE = 0
private const val REVIEW = -1

class MainActivity : ComponentActivity() {
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barres du téléphone transparentes ; icônes sombres le jour, claires la nuit
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        speaker = Speaker(this)
        val progress = Progress(this)
        val srs = Srs(this)
        val streak = Streak(this)
        val allWords = hsk1Lessons.flatMap { it.words }
        val total = allWords.size
        val settings = getSharedPreferences("settings", MODE_PRIVATE)
        val versionName = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }
            .getOrNull() ?: ""
        Reminders.schedule(this)

        setContent {
            // Langue choisie (français, anglais, russe), gardée sur le téléphone
            var lang by remember { mutableStateOf(LangPref.get(this@MainActivity)) }
            CompositionLocalProvider(LocalLang provides lang) {
            ZhuziTheme {
                var tab by rememberSaveable { mutableStateOf(Tab.HOME) }
                var overlay by rememberSaveable { mutableIntStateOf(NONE) }
                // Change à chaque progrès pour rafraîchir les onglets
                var version by rememberSaveable { mutableIntStateOf(0) }
                val close: () -> Unit = { overlay = NONE; version++ }
                val openLesson: (com.zhuzi.app.data.Lesson) -> Unit = { overlay = it.id }

                // Rappel : réglage de l'appli + accord Android pour les notifications
                var reminderOn by remember {
                    mutableStateOf(Reminders.isEnabled(this@MainActivity) && Reminders.canNotify(this@MainActivity))
                }
                var fromButton by remember { mutableStateOf(false) }
                val askPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    reminderOn = granted && Reminders.isEnabled(this@MainActivity)
                    // Refus définitif : on ouvre les réglages de notification de l'appli
                    if (!granted && fromButton) {
                        startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= 33 && !Reminders.canNotify(this@MainActivity) &&
                        !settings.getBoolean("asked_notifications", false)
                    ) {
                        settings.edit().putBoolean("asked_notifications", true).apply()
                        askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val lesson = hsk1Lessons.firstOrNull { it.id == overlay }
                val bars = WindowInsets.systemBars.asPaddingValues()

                when {
                    // Révision du jour, plein écran
                    overlay == REVIEW -> Surface {
                        BackHandler(onBack = close)
                        ReviewScreen(
                            dueWords = srs.dueWords(allWords),
                            speak = speaker::speak,
                            onAnswer = { word, knew ->
                                srs.answer(word, knew)
                                streak.markActive()
                            },
                            onBack = close,
                            contentPadding = bars,
                        )
                    }
                    // Une leçon, plein écran
                    lesson != null -> Surface {
                        BackHandler(onBack = close)
                        LessonScreen(
                            lesson = lesson,
                            speak = speaker::speak,
                            onBack = close,
                            onQuizDone = { mode, percent -> progress.saveScore(lesson.id, mode, percent) },
                            onSeen = { word ->
                                srs.learn(word)
                                streak.markActive()
                            },
                            onQuizAnswer = { word, correct ->
                                srs.quizAnswer(word, correct)
                                streak.markActive()
                            },
                            contentPadding = bars,
                        )
                    }
                    // Les onglets, avec la barre du bas
                    else -> {
                        if (tab != Tab.HOME) BackHandler { tab = Tab.HOME }
                        MainFrame(tab, { tab = it }) { padding ->
                            // Lire « version » force le recalcul après chaque leçon ou révision
                            val v = version
                            val bestScore: (Int, QuizMode) -> Int =
                                { id, mode -> if (v >= 0) progress.bestScore(id, mode) else -1 }
                            val lessonKnown: (com.zhuzi.app.data.Lesson) -> Int = { l -> srs.knownCount(l.words) }
                            AnimatedContent(tab, transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab") { t ->
                                when (t) {
                                    Tab.HOME -> {
                                        val next = hsk1Lessons.firstOrNull { lessonKnown(it) < it.words.size }
                                            ?: hsk1Lessons.last()
                                        HomeScreen(
                                            todayLabel = todayLabel(lang),
                                            greeting = com.zhuzi.app.data.greetingNow(),
                                            proverb = com.zhuzi.app.data.proverbOfDay(),
                                            streakDays = streak.current(),
                                            dueWords = srs.dueWords(allWords),
                                            dueTomorrow = srs.dueTomorrow(allWords),
                                            knownCount = srs.knownCount(allWords),
                                            masteredCount = srs.masteredCount(allWords),
                                            total = total,
                                            next = next,
                                            nextKnown = lessonKnown(next),
                                            wordOfDay = allWords[(LocalDate.now().toEpochDay() % total).toInt()],
                                            speak = speaker::speak,
                                            onReview = { overlay = REVIEW },
                                            onOpen = openLesson,
                                            contentPadding = padding,
                                        )
                                    }
                                    Tab.LESSONS -> LessonsScreen(bestScore, lessonKnown, openLesson, padding)
                                    Tab.CHARS -> CharactersScreen(
                                        boxOf = { w -> if (v >= 0) srs.boxOf(w) else 0 },
                                        speak = speaker::speak,
                                        contentPadding = padding,
                                    )
                                    Tab.PROGRESS -> ProgressScreen(
                                        streakDays = streak.current(),
                                        knownCount = srs.knownCount(allWords),
                                        masteredCount = srs.masteredCount(allWords),
                                        total = total,
                                        dueTomorrow = srs.dueTomorrow(allWords),
                                        lessonsMastered = hsk1Lessons.count { l ->
                                            QuizMode.entries.all { bestScore(l.id, it) >= 80 }
                                        },
                                        lessonsTotal = hsk1Lessons.size,
                                        reminderOn = reminderOn,
                                        onReminderChange = { on ->
                                            Reminders.setEnabled(this@MainActivity, on)
                                            if (!on) {
                                                reminderOn = false
                                            } else if (Reminders.canNotify(this@MainActivity)) {
                                                reminderOn = true
                                            } else {
                                                fromButton = true
                                                askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        },
                                        versionName = versionName,
                                        lang = lang,
                                        onLangChange = { l ->
                                            LangPref.set(this@MainActivity, l)
                                            lang = l
                                        },
                                        contentPadding = padding,
                                    )
                                }
                            }
                        }
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

/** « Mercredi 26 novembre », « Wednesday 26 November », « Среда, 26 ноября » */
private fun todayLabel(lang: Lang): String {
    val locale = when (lang) {
        Lang.FR -> Locale.FRENCH
        Lang.EN -> Locale.ENGLISH
        Lang.RU -> Locale.forLanguageTag("ru")
    }
    val pattern = if (lang == Lang.RU) "EEEE, d MMMM" else "EEEE d MMMM"
    return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, locale)).replaceFirstChar { it.titlecase(locale) }
}
