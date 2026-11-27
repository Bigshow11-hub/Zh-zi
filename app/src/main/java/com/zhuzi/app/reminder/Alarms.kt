package com.zhuzi.app.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Les vraies alarmes du téléphone : elles réveillent l'appli à l'heure dite, même fermée,
 * même quand le téléphone est en veille. (WorkManager reste en secours.)
 */
object Alarms {
    const val ACTION_REMINDER = "com.zhuzi.app.ALARM_REMINDER"
    const val ACTION_WORD = "com.zhuzi.app.ALARM_WORD"

    /** Programme (ou reprogramme) les deux alarmes quotidiennes : rappel du soir et mot du jour. */
    fun scheduleAll(context: Context) {
        val (rh, rm) = Reminders.time(context)
        set(context, ACTION_REMINDER, 10, rh, rm, Reminders.isEnabled(context))
        val (wh, wm) = DailyWord.time(context)
        set(context, ACTION_WORD, 11, wh, wm, DailyWord.isEnabled(context))
    }

    private fun set(context: Context, action: String, request: Int, h: Int, m: Int, on: Boolean) {
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = PendingIntent.getBroadcast(
            context, request,
            Intent(context, AlarmReceiver::class.java).setAction(action),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        am.cancel(pi)
        if (!on) return
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(h, m)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val at = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        try {
            if (canExact(context)) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
            else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        } catch (e: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    /** Android 12+ : l'accord « Alarmes et rappels » permet l'heure exacte (sinon, à quelques minutes près). */
    fun canExact(context: Context): Boolean =
        Build.VERSION.SDK_INT < 31 || context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    /** La batterie ne restreint pas l'appli (indispensable sur certains Samsung pour être à l'heure). */
    fun batteryFree(context: Context): Boolean =
        context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)

    /** Écran d'Android pour autoriser l'heure exacte. */
    fun exactSettings(context: Context): Intent =
        if (Build.VERSION.SDK_INT >= 31) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
        }

    /** Fenêtre d'Android pour lever la restriction de batterie de l'appli. */
    @android.annotation.SuppressLint("BatteryLife")
    fun batterySettings(context: Context): Intent =
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}"))
}

/** Reçoit l'alarme : affiche la notification puis programme celle du lendemain. */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Alarms.ACTION_REMINDER -> Reminders.fire(context)
            Alarms.ACTION_WORD -> DailyWord.show(context)
        }
        Alarms.scheduleAll(context)
    }
}

/** Après un redémarrage, une mise à jour de l'appli ou un changement d'heure : on reprogramme tout. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Push.scheduleAll(context)
    }
}
