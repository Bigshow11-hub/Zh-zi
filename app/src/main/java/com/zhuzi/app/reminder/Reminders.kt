package com.zhuzi.app.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.zhuzi.app.MainActivity
import com.zhuzi.app.R
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.Streak
import com.zhuzi.app.data.allLessons
import com.zhuzi.app.ui.LangPref
import com.zhuzi.app.ui.daysCount
import com.zhuzi.app.ui.tr
import com.zhuzi.app.ui.wordsCount
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** Rappel quotidien : une notification chaque jour à l'heure choisie (19 h par défaut), si on n'a pas encore étudié. */
object Reminders {
    const val HOUR = 19
    private const val CHANNEL = "daily_reminder"
    private const val WORK = "daily_reminder"

    private fun prefs(context: Context) = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /** L'heure du rappel choisie : (heure, minute). */
    fun time(context: Context): Pair<Int, Int> =
        prefs(context).getInt("reminder_hour", HOUR) to prefs(context).getInt("reminder_minute", 0)

    /** « 19:00 ». */
    fun label(context: Context): String = time(context).let { (h, m) -> "%02d:%02d".format(h, m) }

    /** Change l'heure du rappel et reprogramme tout de suite. */
    fun setTime(context: Context, hour: Int, minute: Int) {
        prefs(context).edit().putInt("reminder_hour", hour).putInt("reminder_minute", minute).apply()
        schedule(context, replace = true)
    }

    fun schedule(context: Context, replace: Boolean = false) {
        val (h, m) = time(context)
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(h, m)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(Duration.between(now, next).toMinutes(), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK,
            if (replace) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE else ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

    /** Texte du rappel, ou null s'il n'y a rien à rappeler. */
    /** Le rappel peut être coupé dans l'onglet Progrès (réglage gardé sur le téléphone). */
    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("reminder_enabled", true)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
            .putBoolean("reminder_enabled", enabled).apply()
    }

    fun message(context: Context): String? {
        if (!isEnabled(context)) return null
        val streak = Streak(context)
        if (streak.activeToday()) return null
        val srs = Srs(context)
        val all = allLessons.flatMap { it.words }
        val due = srs.dueWords(all).size
        val days = streak.current()
        val lang = LangPref.get(context)
        val keep = if (days > 0) {
            " " + tr(lang, "Garde ta série de ${daysCount(lang, days)} d'affilée.",
                "Keep your ${daysCount(lang, days)} streak going.",
                "Сохрани серию: ${daysCount(lang, days)} подряд.")
        } else {
            ""
        }
        return when {
            due > 0 -> tr(lang, "À revoir aujourd'hui : ", "To review today: ", "Повторить сегодня: ") +
                wordsCount(lang, due) + "." + keep
            days > 0 -> tr(lang, "Un petit quiz pour continuer ?", "A quick quiz to keep going?",
                "Небольшой тест, чтобы продолжить?") + keep
            srs.knownCount(all) == 0 -> tr(lang, "Tes premiers mots de chinois t'attendent.",
                "Your first Chinese words are waiting.", "Твои первые китайские слова ждут тебя.")
            else -> null
        }
    }

    fun show(context: Context, text: String) {
        if (!canNotify(context)) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL,
                tr(LangPref.get(context), "Rappel quotidien", "Daily reminder", "Ежедневное напоминание"),
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Zhúzi 竹子")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(1, notification)
    }
}

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Reminders.message(applicationContext)?.let { Reminders.show(applicationContext, it) }
        return Result.success()
    }
}
