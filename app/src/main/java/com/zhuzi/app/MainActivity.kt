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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import com.zhuzi.app.data.Lang
import com.zhuzi.app.data.Progress
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.Streak
import com.zhuzi.app.data.allLessons
import com.zhuzi.app.reminder.Reminders
import com.zhuzi.app.ui.CharactersScreen
import com.zhuzi.app.ui.HomeScreen
import com.zhuzi.app.ui.LangPref
import com.zhuzi.app.ui.LocalLang
import com.zhuzi.app.ui.LessonScreen
import com.zhuzi.app.ui.LessonsScreen
import com.zhuzi.app.ui.MainFrame
import com.zhuzi.app.ui.ProgressScreen
import com.zhuzi.app.ui.ReadingColumn
import com.zhuzi.app.ui.ReviewScreen
import com.zhuzi.app.ui.Speaker
import com.zhuzi.app.ui.Tab
import com.zhuzi.app.data.level
import com.zhuzi.app.ui.tr
import com.zhuzi.app.ui.theme.ZhuziTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import com.zhuzi.app.data.AvatarLook
import com.zhuzi.app.data.AvatarPrefs
import com.zhuzi.app.data.AvatarStats
import com.zhuzi.app.data.Backup
import com.zhuzi.app.data.Celebrations
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Milestone
import com.zhuzi.app.data.MyWords
import com.zhuzi.app.data.WordOfDay
import com.zhuzi.app.data.greetingNow
import com.zhuzi.app.data.levelFor
import com.zhuzi.app.data.proverbOfDay
import com.zhuzi.app.reminder.Alarms
import com.zhuzi.app.reminder.DailyWord
import com.zhuzi.app.reminder.Push
import com.zhuzi.app.reminder.Updates
import com.zhuzi.app.ui.AvatarScreen
import com.zhuzi.app.ui.CelebrationOverlay
import com.zhuzi.app.ui.ExamScreen
import com.zhuzi.app.ui.MyWordsScreen
import com.zhuzi.app.ui.OnboardingScreen
import com.zhuzi.app.ui.PhotoImportScreen
import com.zhuzi.app.ui.RestoreDialog
import com.zhuzi.app.ui.ThemePref
import com.zhuzi.app.ui.Voice
import com.zhuzi.app.ui.VoiceSheet

/** Écran plein (sans barre du bas) ouvert par-dessus les onglets. */
private sealed interface Overlay {
    /** Aucun : les onglets, avec la barre du bas. */
    object None : Overlay
    /** La révision du jour. */
    object Review : Overlay
    /** Mon avatar. */
    object Avatar : Overlay
    /** Mes mots, l'import d'une photo, la révision de mes mots. */
    object MyWords : Overlay
    object Photo : Overlay
    object MyReview : Overlay
    /** Examen blanc d'un niveau HSK (1 à 4). */
    data class Exam(val level: Int) : Overlay
    /** Une leçon (son numéro). */
    data class LessonOf(val id: Int) : Overlay
}

/**
 * L'écran plein est gardé (rotation, retour dans l'appli) sous forme d'un nombre, comme avant :
 * 0 aucun, -1 révision, -2 avatar, -3 mes mots, -4 photo, -5 révision de mes mots,
 * -11 à -14 examen blanc HSK 1 à 4, sinon le numéro de la leçon.
 */
private val OverlaySaver = Saver<Overlay, Int>(
    save = { o ->
        when (o) {
            Overlay.None -> 0
            Overlay.Review -> -1
            Overlay.Avatar -> -2
            Overlay.MyWords -> -3
            Overlay.Photo -> -4
            Overlay.MyReview -> -5
            is Overlay.Exam -> -10 - o.level
            is Overlay.LessonOf -> o.id
        }
    },
    restore = { n ->
        when {
            n == -1 -> Overlay.Review
            n == -2 -> Overlay.Avatar
            n == -3 -> Overlay.MyWords
            n == -4 -> Overlay.Photo
            n == -5 -> Overlay.MyReview
            n in -14..-11 -> Overlay.Exam(-10 - n)
            n > 0 -> Overlay.LessonOf(n)
            else -> Overlay.None
        }
    },
)

class MainActivity : ComponentActivity() {
    companion object {
        /** Leçon à ouvrir directement (depuis la notification du mot du jour). */
        const val EXTRA_LESSON = "open_lesson"
    }

    private lateinit var speaker: Speaker
    // Réglages Android pour les notifications appli fermée (relus à chaque retour dans l'appli)
    private var exactOk by mutableStateOf(true)
    private var batteryOk by mutableStateOf(true)

    override fun onResume() {
        super.onResume()
        if (::speaker.isInitialized) speaker.check()
        exactOk = Alarms.canExact(this)
        batteryOk = Alarms.batteryFree(this)
        // L'accord « heure exacte » vient peut-être d'être donné : on reprogramme les alarmes
        Alarms.scheduleAll(this)
    }

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
        val myWords = MyWords(this)
        val allWords = allLessons.flatMap { it.words }
        val total = allWords.size
        val settings = getSharedPreferences("settings", MODE_PRIVATE)
        val wordOfDay = WordOfDay.today(this)
        val startLesson = intent?.getIntExtra(EXTRA_LESSON, 0) ?: 0
        val versionName = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }
            .getOrNull() ?: ""
        Push.scheduleAll(this)

        setContent {
            // Langue choisie (français, anglais, russe), gardée sur le téléphone
            var lang by remember { mutableStateOf(LangPref.get(this@MainActivity)) }
            // Mascotte : tenue, accessoire, fond choisis
            var look by remember { mutableStateOf(AvatarPrefs.get(this@MainActivity)) }
            val setLook: (AvatarLook) -> Unit = { l ->
                AvatarPrefs.set(this@MainActivity, l); look = l
            }
            // Apparence choisie : automatique, clair ou sombre
            var themeMode by remember { mutableIntStateOf(ThemePref.get(this@MainActivity)) }
            val systemDark = isSystemInDarkTheme()
            val dark = when (themeMode) {
                1 -> false
                2 -> true
                else -> systemDark
            }
            // Icônes de la barre d'état du téléphone : sombres sur clair, claires sur sombre
            LaunchedEffect(dark) {
                val style = if (dark) SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }
            CompositionLocalProvider(LocalLang provides lang) {
            ZhuziTheme(darkTheme = dark) {
                var tab by rememberSaveable { mutableStateOf(Tab.HOME) }
                var overlay: Overlay by rememberSaveable(stateSaver = OverlaySaver) {
                    mutableStateOf<Overlay>(if (allLessons.any { it.id == startLesson }) Overlay.LessonOf(startLesson) else Overlay.None)
                }
                // Change à chaque progrès pour rafraîchir les onglets
                var version by rememberSaveable { mutableIntStateOf(0) }
                val close: () -> Unit = { overlay = Overlay.None; version++ }
                val openLesson: (Lesson) -> Unit = { overlay = Overlay.LessonOf(it.id) }

                // Rappel : réglage de l'appli + accord Android pour les notifications
                var reminderOn by remember {
                    mutableStateOf(Reminders.isEnabled(this@MainActivity) && Reminders.canNotify(this@MainActivity))
                }
                var fromButton by remember { mutableStateOf(false) }
                var reminderAt by remember { mutableStateOf(Reminders.time(this@MainActivity)) }
                // Mot du jour (le matin) et nouvelles versions : réglages de l'appli
                var wordOn by remember { mutableStateOf(DailyWord.isEnabled(this@MainActivity)) }
                var wordAt by remember { mutableStateOf(DailyWord.time(this@MainActivity)) }
                var updatesOn by remember { mutableStateOf(Updates.isEnabled(this@MainActivity)) }
                var updateName by remember { mutableStateOf(Updates.available(this@MainActivity)) }
                LaunchedEffect(Unit) {
                    Updates.check(this@MainActivity)
                    updateName = Updates.available(this@MainActivity)
                }
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
                // Premier lancement : 3 écrans d'accueil (un élève qui a déjà des progrès ne les voit pas)
                var onboarded by remember {
                    mutableStateOf(settings.getBoolean("onboarded", false) || srs.knownCount(allWords) > 0)
                }
                LaunchedEffect(onboarded) {
                    if (onboarded) settings.edit().putBoolean("onboarded", true).apply()
                }
                LaunchedEffect(onboarded) {
                    if (onboarded && Build.VERSION.SDK_INT >= 33 && !Reminders.canNotify(this@MainActivity) &&
                        !settings.getBoolean("asked_notifications", false)
                    ) {
                        settings.edit().putBoolean("asked_notifications", true).apply()
                        askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val needPermission: () -> Unit = {
                    if (!Reminders.canNotify(this@MainActivity)) {
                        fromButton = true
                        askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // Voix chinoise absente : on explique comment l'installer (une fois par ouverture)
                var voiceSheet by remember { mutableStateOf(false) }
                var voiceAsked by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    speaker.onMissing = { if (!voiceAsked) { voiceAsked = true; voiceSheet = true } }
                }
                val openVoiceInstall: () -> Unit = {
                    voiceSheet = false
                    Speaker.installIntents().firstOrNull { runCatching { startActivity(it) }.isSuccess }
                }
                if (voiceSheet) VoiceSheet(onInstall = openVoiceInstall) { voiceSheet = false }

                // Sauvegarde des progrès dans un fichier, et restauration
                val exportLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json"),
                ) { uri ->
                    if (uri != null) {
                        val ok = Backup.export(this@MainActivity, uri)
                        Toast.makeText(this@MainActivity,
                            if (ok) tr(lang, "Progrès sauvegardés.", "Progress saved.", "Прогресс сохранён.")
                            else tr(lang, "La sauvegarde a échoué.", "Backup failed.", "Не удалось сохранить."),
                            Toast.LENGTH_LONG).show()
                    }
                }
                var restoreUri by remember { mutableStateOf<Uri?>(null) }
                val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                    if (uri != null) restoreUri = uri
                }
                restoreUri?.let { uri ->
                    RestoreDialog(
                        onConfirm = {
                            restoreUri = null
                            if (Backup.import(this@MainActivity, uri)) {
                                Push.scheduleAll(this@MainActivity)
                                Toast.makeText(this@MainActivity,
                                    tr(lang, "Progrès restaurés.", "Progress restored.", "Прогресс восстановлен."),
                                    Toast.LENGTH_LONG).show()
                                recreate()
                            } else {
                                Toast.makeText(this@MainActivity,
                                    tr(lang, "Ce fichier n'est pas une sauvegarde Zhúzi.", "This file isn't a Zhúzi backup.",
                                        "Это не резервная копия Zhúzi."), Toast.LENGTH_LONG).show()
                            }
                        },
                        onDismiss = { restoreUri = null },
                    )
                }

                // Mes mots : relus après chaque ajout ou suppression
                var mine by remember { mutableStateOf(myWords.all()) }
                val reviewPool = allWords + mine.map { it.toWord() }
                val addMine: (List<Triple<String, String, String>>, String) -> Unit = { items, source ->
                    myWords.add(items, source).forEach { srs.learn(it.toWord()) }
                    mine = myWords.all()
                }
                // Fêtes d'étapes : vérifiées à chaque retour sur les onglets
                var celebration by remember { mutableStateOf<Milestone?>(null) }
                LaunchedEffect(overlay, version) {
                    if (overlay == Overlay.None) {
                        celebration = Celebrations.pending(
                            this@MainActivity,
                            streak = streak.current(),
                            known = srs.knownCount(allWords),
                            mastered = allLessons.count { l -> QuizMode.core.all { progress.bestScore(l.id, it) >= 80 } },
                        )
                    }
                }
                // Lu une fois : les branches ci-dessous testent cette valeur
                val current = overlay
                val lesson = (current as? Overlay.LessonOf)?.let { o -> allLessons.firstOrNull { it.id == o.id } }
                val bars = WindowInsets.systemBars.asPaddingValues()

                when {
                    !onboarded -> FullScreen {
                        OnboardingScreen(
                            lang = lang,
                            onLang = { l -> LangPref.set(this@MainActivity, l); lang = l },
                            onDone = { level, remind, h ->
                                // Mots déjà connus : rangés dans les révisions, étalés sur ~5 semaines
                                allLessons.filter { it.level <= level }.flatMap { it.words }
                                    .forEachIndexed { i, w -> srs.markKnown(w, i % 35) }
                                Reminders.setTime(this@MainActivity, h, 0)
                                reminderAt = h to 0
                                Reminders.setEnabled(this@MainActivity, remind)
                                reminderOn = remind && Reminders.canNotify(this@MainActivity)
                                settings.edit().putBoolean("asked_notifications", true).apply()
                                if (remind && Build.VERSION.SDK_INT >= 33 && !Reminders.canNotify(this@MainActivity)) {
                                    askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                onboarded = true
                                version++
                            },
                            contentPadding = bars,
                        )
                    }
                    // Mon avatar, plein écran
                    current == Overlay.Avatar -> FullScreen {
                        BackHandler(onBack = close)
                        val known = srs.knownCount(allWords)
                        AvatarScreen(
                            look = look,
                            stats = AvatarStats(
                                known = known,
                                streak = streak.current(),
                                lessonsMastered = allLessons.count { l ->
                                    QuizMode.core.all { progress.bestScore(l.id, it) >= 80 }
                                },
                                listeningPassed = allLessons.any { progress.bestScore(it.id, QuizMode.LISTENING) >= 80 },
                            ),
                            onChange = setLook,
                            onBack = close,
                            contentPadding = bars,
                        )
                    }
                    current is Overlay.Exam && current.level in 1..4 -> FullScreen {
                        BackHandler(onBack = close)
                        val examLevel = current.level
                        ExamScreen(
                            level = examLevel,
                            best = progress.bestExam(examLevel),
                            speak = speaker::speak,
                            onFinish = { score, missed ->
                                progress.saveExam(examLevel, score)
                                missed.forEach { srs.quizAnswer(it, false) }
                                streak.markActive()
                            },
                            onBack = close,
                            contentPadding = bars,
                        )
                    }
                    current == Overlay.MyWords -> FullScreen {
                        BackHandler(onBack = close)
                        MyWordsScreen(
                            words = mine,
                            dueCount = srs.dueWords(mine.map { it.toWord() }).size,
                            speak = speaker::speak,
                            onAdd = addMine,
                            onRemove = { w -> myWords.remove(w.id); mine = myWords.all() },
                            onReview = { overlay = Overlay.MyReview },
                            onPhoto = { overlay = Overlay.Photo },
                            onBack = close,
                            contentPadding = bars,
                        )
                    }
                    current == Overlay.Photo -> FullScreen {
                        BackHandler { overlay = Overlay.MyWords }
                        PhotoImportScreen(
                            onCreate = { items -> addMine(items, "photo"); overlay = Overlay.MyWords },
                            onBack = { overlay = Overlay.MyWords },
                            contentPadding = bars,
                        )
                    }
                    current == Overlay.MyReview -> FullScreen {
                        BackHandler { overlay = Overlay.MyWords }
                        // Les mots à revoir ; sinon tous, pour s'entraîner (liste figée pendant la séance)
                        val session = remember { mine.map { it.toWord() }.let { w -> srs.dueWords(w).ifEmpty { w.shuffled() } } }
                        ReviewScreen(
                            dueWords = session,
                            speak = speaker::speak,
                            onAnswer = { word, knew ->
                                srs.answer(word, knew)
                                streak.markActive()
                            },
                            onBack = { overlay = Overlay.MyWords },
                            contentPadding = bars,
                        )
                    }
                    current == Overlay.Review -> FullScreen {
                        BackHandler(onBack = close)
                        ReviewScreen(
                            dueWords = srs.dueWords(reviewPool),
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
                    lesson != null -> FullScreen {
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
                        // Bouton central : la révision s'il y a des mots à revoir, sinon la leçon à reprendre
                        val startSession: () -> Unit = {
                            if (srs.dueWords(reviewPool).isNotEmpty()) {
                                overlay = Overlay.Review
                            } else {
                                val nextLesson = allLessons.firstOrNull { srs.knownCount(it.words) < it.words.size }
                                    ?: allLessons.first()
                                overlay = Overlay.LessonOf(nextLesson.id)
                            }
                        }
                        MainFrame(tab, { tab = it }, startSession) { padding ->
                            // Lire « version » force le recalcul après chaque leçon ou révision
                            val v = version
                            val bestScore: (Int, QuizMode) -> Int =
                                { id, mode -> if (v >= 0) progress.bestScore(id, mode) else -1 }
                            val lessonKnown: (Lesson) -> Int = { l -> srs.knownCount(l.words) }
                            AnimatedContent(tab, transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab") { t ->
                                when (t) {
                                    Tab.HOME -> {
                                        val next = allLessons.firstOrNull { lessonKnown(it) < it.words.size }
                                            ?: allLessons.last()
                                        HomeScreen(
                                            todayLabel = todayLabel(lang),
                                            greeting = greetingNow(),
                                            proverb = proverbOfDay(),
                                            streakDays = streak.current(),
                                            dueWords = srs.dueWords(reviewPool),
                                            dueTomorrow = srs.dueTomorrow(allWords),
                                            knownCount = srs.knownCount(allWords),
                                            masteredCount = srs.masteredCount(allWords),
                                            total = total,
                                            next = next,
                                            nextKnown = lessonKnown(next),
                                            wordOfDay = wordOfDay,
                                            wordOfDayKnown = srs.boxOf(wordOfDay) > 0,
                                            voiceMissing = speaker.voice == Voice.MISSING,
                                            onVoice = { voiceSheet = true },
                                            speak = speaker::speak,
                                            onReview = { overlay = Overlay.Review },
                                            onOpen = openLesson,
                                            onSearch = { tab = Tab.CHARS },
                                            onReminder = { tab = Tab.PROGRESS },
                                            onSeeAll = { tab = Tab.LESSONS },
                                            look = look,
                                            onAvatar = { overlay = Overlay.Avatar },
                                            contentPadding = padding,
                                            activeToday = streak.activeToday(),
                                            reminderOn = reminderOn,
                                            reminderAt = reminderAt,
                                        )
                                    }
                                    Tab.LESSONS -> LessonsScreen(bestScore, lessonKnown, openLesson, padding,
                                        myCount = mine.size, onMyWords = { overlay = Overlay.MyWords },
                                        bestExam = { l -> if (v >= 0) progress.bestExam(l) else -1 },
                                        onExam = { l -> overlay = Overlay.Exam(l) })
                                    Tab.CHARS -> CharactersScreen(
                                        boxOf = { w -> if (v >= 0) srs.boxOf(w) else 0 },
                                        speak = speaker::speak,
                                        contentPadding = padding,
                                        version = v,
                                    )
                                    Tab.PROGRESS -> ProgressScreen(
                                        streakDays = streak.current(),
                                        knownCount = srs.knownCount(allWords),
                                        masteredCount = srs.masteredCount(allWords),
                                        total = total,
                                        dueTomorrow = srs.dueTomorrow(allWords),
                                        lessonsMastered = allLessons.count { l ->
                                            QuizMode.core.all { bestScore(l.id, it) >= 80 }
                                        },
                                        lessonsTotal = allLessons.size,
                                        reminderOn = reminderOn,
                                        reminderTime = reminderAt,
                                        onReminderTime = { h, m ->
                                            Reminders.setTime(this@MainActivity, h, m)
                                            reminderAt = h to m
                                        },
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
                                        wordOn = wordOn,
                                        onWordChange = { on ->
                                            DailyWord.setEnabled(this@MainActivity, on)
                                            wordOn = on
                                            if (on) needPermission()
                                        },
                                        wordTime = wordAt,
                                        onWordTime = { h, m ->
                                            DailyWord.setTime(this@MainActivity, h, m)
                                            wordAt = h to m
                                        },
                                        updatesOn = updatesOn,
                                        onUpdatesChange = { on ->
                                            Updates.setEnabled(this@MainActivity, on)
                                            updatesOn = on
                                            if (on) needPermission()
                                        },
                                        updateName = updateName,
                                        exactOk = exactOk,
                                        batteryOk = batteryOk,
                                        onExact = {
                                            runCatching { startActivity(Alarms.exactSettings(this@MainActivity)) }
                                        },
                                        onBattery = {
                                            runCatching { startActivity(Alarms.batterySettings(this@MainActivity)) }
                                                .onFailure {
                                                    runCatching { startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }
                                                }
                                        },
                                        onExport = { exportLauncher.launch(Backup.fileName()) },
                                        onImport = { importLauncher.launch(arrayOf("application/json", "application/octet-stream", "text/plain")) },
                                        onTest = {
                                            if (Reminders.canNotify(this@MainActivity)) {
                                                DailyWord.show(this@MainActivity, test = true)
                                            } else {
                                                needPermission()
                                            }
                                        },
                                        onUpdate = {
                                            startActivity(Intent(Intent.ACTION_VIEW,
                                                Uri.parse(Updates.APK_URL)))
                                        },
                                        lang = lang,
                                        look = look,
                                        onAvatar = { overlay = Overlay.Avatar },
                                        week = streak.lastDays(7),
                                        themeMode = themeMode,
                                        onThemeChange = { m ->
                                            ThemePref.set(this@MainActivity, m)
                                            themeMode = m
                                        },
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
                // La fête d'une étape, par-dessus tout le reste
                celebration?.let { m ->
                    CelebrationOverlay(
                        milestone = m, look = look,
                        level = levelFor(srs.knownCount(allWords)).number,
                        onDone = { celebration = null },
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

/** Un écran plein : fond du thème, contenu limité à la largeur de lecture sur tablette ou en paysage. */
@Composable
private fun FullScreen(content: @Composable () -> Unit) {
    Surface { ReadingColumn(propagateMinConstraints = true, content = content) }
}

/** « Mercredi 26 novembre », « Wednesday 26 November », « Среда, 26 ноября » */
private fun todayLabel(lang: Lang): String {
    val locale = when (lang) {
        Lang.FR -> Locale.FRENCH
        Lang.EN -> Locale.ENGLISH
        Lang.RU -> Locale.forLanguageTag("ru")
        Lang.BN -> Locale.forLanguageTag("bn")
    }
    val pattern = if (lang == Lang.RU) "EEEE, d MMMM" else "EEEE d MMMM"
    return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, locale)).replaceFirstChar { it.titlecase(locale) }
}
