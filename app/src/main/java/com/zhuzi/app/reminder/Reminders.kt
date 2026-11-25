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
import com.zhuzi.app.data.hsk1Lessons
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** Rappel quotidien : une notification chaque soir vers 19 h, si on n'a pas encore étudié. */
object Reminders {
    const val HOUR = 19
    private const val CHANNEL = "daily_reminder"
    private const val WORK = "daily_reminder"

    fun schedule(context: Context) {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(HOUR, 0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(Duration.between(now, next).toMinutes(), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

    /** Texte du rappel, ou null s'il n'y a rien à rappeler. */
    fun message(context: Context): String? {
        val streak = Streak(context)
        if (streak.activeToday()) return null
        val srs = Srs(context)
        val all = hsk1Lessons.flatMap { it.words }
        val due = srs.dueWords(all).size
        val days = streak.current()
        val keep = if (days > 0) " Garde ta série de $days jour(s) 🔥" else ""
        return when {
            due > 0 -> "$due mot(s) à revoir aujourd'hui.$keep"
            days > 0 -> "Un petit quiz pour continuer ?$keep"
            srs.knownCount(all) == 0 -> "Tes premiers mots de chinois t'attendent 🎋"
            else -> null
        }
    }

    fun show(context: Context, text: String) {
        if (!canNotify(context)) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL, "Rappel quotidien", NotificationManager.IMPORTANCE_DEFAULT),
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
