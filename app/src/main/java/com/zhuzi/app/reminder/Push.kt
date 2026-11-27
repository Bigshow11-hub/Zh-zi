package com.zhuzi.app.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.zhuzi.app.MainActivity
import com.zhuzi.app.R
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.WordOfDay
import com.zhuzi.app.data.allLessons
import com.zhuzi.app.data.examples
import com.zhuzi.app.ui.LangPref
import com.zhuzi.app.ui.tr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Les notifications envoyées par l'appli elle-même, sans serveur :
 * rappel du soir (Reminders), mot du jour le matin (DailyWord), nouvelle version disponible (Updates).
 */
object Push {
    const val CH_WORD = "word_of_day"
    const val CH_UPDATE = "updates"
    const val ID_WORD = 2
    const val ID_UPDATE = 3

    /** Programme tout : à appeler au lancement de l'appli (sans effet si déjà programmé). */
    fun scheduleAll(context: Context) {
        Reminders.schedule(context)
        DailyWord.schedule(context)
        Updates.schedule(context)
    }

    fun channel(context: Context, id: String, fr: String, en: String, ru: String, importance: Int) {
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(id, tr(LangPref.get(context), fr, en, ru), importance),
        )
    }

    /** Ouvre l'appli (éventuellement sur une leçon). */
    fun openApp(context: Context, request: Int, lessonId: Int = 0): PendingIntent = PendingIntent.getActivity(
        context, request,
        Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .apply { if (lessonId > 0) putExtra(MainActivity.EXTRA_LESSON, lessonId) },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    fun settings(context: Context) = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /** Heure suivante (h:m) à partir de maintenant, en minutes. */
    fun minutesUntil(h: Int, m: Int): Long {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(h, m)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMinutes()
    }
}

/** Le mot du jour : une notification chaque matin avec un mot nouveau, sa prononciation, son sens et un exemple. */
object DailyWord {
    const val HOUR = 9
    private const val WORK = "word_of_day"

    fun isEnabled(context: Context) = Push.settings(context).getBoolean("word_enabled", true)
    fun setEnabled(context: Context, on: Boolean) {
        Push.settings(context).edit().putBoolean("word_enabled", on).apply()
    }

    fun time(context: Context): Pair<Int, Int> =
        Push.settings(context).getInt("word_hour", HOUR) to Push.settings(context).getInt("word_minute", 0)

    fun setTime(context: Context, hour: Int, minute: Int) {
        Push.settings(context).edit().putInt("word_hour", hour).putInt("word_minute", minute).apply()
        schedule(context, replace = true)
    }

    fun schedule(context: Context, replace: Boolean = false) {
        val (h, m) = time(context)
        val request = PeriodicWorkRequestBuilder<DailyWordWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(Push.minutesUntil(h, m), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK,
            if (replace) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE else ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun show(context: Context) {
        if (!isEnabled(context) || !Reminders.canNotify(context)) return
        val p = Push.settings(context)
        val today = LocalDate.now().toEpochDay()
        if (p.getLong("word_shown_day", -1) == today) return
        val lang = LangPref.get(context)
        val word = WordOfDay.today(context)
        val meaning = word.meaning(lang)
        val ex = examples[word.hanzi]
        val body = buildString {
            append(word.pinyin).append(" · ").append(meaning)
            if (ex != null) append("\n\n").append(ex.hanzi).append("\n").append(ex.pinyin).append("\n").append(ex.meaning(lang))
        }
        Push.channel(context, Push.CH_WORD, "Mot du jour", "Word of the day", "Слово дня",
            NotificationManager.IMPORTANCE_DEFAULT)
        val learn = PendingIntent.getBroadcast(
            context, 20,
            Intent(context, LearnWordReceiver::class.java).putExtra(LearnWordReceiver.EXTRA_HANZI, word.hanzi),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val lesson = WordOfDay.lessonOf(word)
        val notification = NotificationCompat.Builder(context, Push.CH_WORD)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(tr(lang, "Mot du jour : ", "Word of the day: ", "Слово дня: ") + word.hanzi)
            .setContentText(word.pinyin + " · " + meaning)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(Push.openApp(context, 21, lesson?.id ?: 0))
            .addAction(0, tr(lang, "Ajouter à mes révisions", "Add to my reviews", "В повторение"), learn)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(Push.ID_WORD, notification)
        p.edit().putLong("word_shown_day", today).apply()
    }
}

class DailyWordWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        DailyWord.show(applicationContext)
        return Result.success()
    }
}

/** Bouton « Ajouter à mes révisions » de la notification : le mot entre dans les révisions, sans ouvrir l'appli. */
class LearnWordReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val hanzi = intent.getStringExtra(EXTRA_HANZI) ?: return
        val word = allLessons.flatMap { it.words }.firstOrNull { it.hanzi == hanzi } ?: return
        Srs(context).learn(word)
        context.getSystemService(NotificationManager::class.java).cancel(Push.ID_WORD)
        val lang = LangPref.get(context)
        Toast.makeText(context, tr(lang, "$hanzi ajouté : à revoir demain.", "$hanzi added: review it tomorrow.",
            "$hanzi добавлено: повтори завтра."), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_HANZI = "hanzi"
    }
}

/**
 * Nouvelle version : l'appli regarde régulièrement (avec internet) le petit fichier version.json publié
 * à côté de l'APK sur GitHub. Si la version est plus récente, une notification propose de la télécharger.
 */
object Updates {
    const val APK_URL = "https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/zhuzi.apk"
    private const val VERSION_URL = "https://github.com/Bigshow11-hub/Zh-zi/releases/latest/download/version.json"
    private const val WORK = "update_check"

    fun isEnabled(context: Context) = Push.settings(context).getBoolean("updates_enabled", true)
    fun setEnabled(context: Context, on: Boolean) {
        Push.settings(context).edit().putBoolean("updates_enabled", on).apply()
    }

    fun currentCode(context: Context): Long = runCatching {
        PackageInfoCompat.getLongVersionCode(context.packageManager.getPackageInfo(context.packageName, 0))
    }.getOrDefault(0L)

    /** Le nom de la nouvelle version si elle est plus récente que celle installée, sinon null. */
    fun available(context: Context): String? {
        val p = Push.settings(context)
        val code = p.getLong("update_code", 0L)
        return if (code > currentCode(context)) p.getString("update_name", null) else null
    }

    fun schedule(context: Context) {
        val online = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val wm = WorkManager.getInstance(context)
        wm.enqueueUniquePeriodicWork(
            WORK, ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<UpdateWorker>(12, TimeUnit.HOURS).setConstraints(online).build(),
        )
        // Et une vérification tout de suite, à chaque ouverture de l'appli
        wm.enqueueUniqueWork(
            "$WORK-now", ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<UpdateWorker>().setConstraints(online).build(),
        )
    }

    /** Lit version.json sur GitHub : (code, nom), ou null sans réseau. */
    suspend fun fetch(): Pair<Long, String>? = withContext(Dispatchers.IO) {
        runCatching {
            val c = URL(VERSION_URL).openConnection() as HttpURLConnection
            c.connectTimeout = 15_000
            c.readTimeout = 15_000
            c.instanceFollowRedirects = true
            try {
                if (c.responseCode != 200) return@runCatching null
                val json = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
                json.getLong("versionCode") to json.getString("versionName")
            } finally {
                c.disconnect()
            }
        }.getOrNull()
    }

    suspend fun check(context: Context) {
        val (code, name) = fetch() ?: return
        val p = Push.settings(context)
        p.edit().putLong("update_code", code).putString("update_name", name).apply()
        if (code <= currentCode(context) || !isEnabled(context) || !Reminders.canNotify(context)) return
        if (p.getLong("update_notified", 0L) >= code) return
        val lang = LangPref.get(context)
        Push.channel(context, Push.CH_UPDATE, "Mises à jour", "Updates", "Обновления",
            NotificationManager.IMPORTANCE_DEFAULT)
        val download = PendingIntent.getActivity(
            context, 30,
            Intent(Intent.ACTION_VIEW, Uri.parse(APK_URL)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val text = tr(lang, "La version $name est prête. Touche pour la télécharger, puis installe-la par-dessus : tes progrès sont gardés.",
            "Version $name is ready. Tap to download it, then install it over the current one: your progress is kept.",
            "Версия $name готова. Нажми, чтобы скачать, и установи поверх: прогресс сохранится.")
        val notification = NotificationCompat.Builder(context, Push.CH_UPDATE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(tr(lang, "Nouvelle version de Zhúzi", "New Zhúzi version", "Новая версия Zhúzi"))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(download)
            .addAction(0, tr(lang, "Télécharger", "Download", "Скачать"), download)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(Push.ID_UPDATE, notification)
        p.edit().putLong("update_notified", code).apply()
    }
}

class UpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Updates.check(applicationContext)
        return Result.success()
    }
}
