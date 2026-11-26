package com.zhuzi.app.data

import android.content.Context
import java.time.LocalDate

/** Série de jours d'affilée où l'on a étudié (carte retournée, quiz ou révision). */
class Streak(context: Context) {
    private val prefs = context.getSharedPreferences("streak", Context.MODE_PRIVATE)
    private val activity = context.getSharedPreferences("activity", Context.MODE_PRIVATE)

    private fun today() = LocalDate.now().toEpochDay()
    private val lastDay get() = prefs.getLong("last_day", Long.MIN_VALUE)
    private val count get() = prefs.getInt("count", 0)

    fun markActive() {
        val today = today()
        // Activité du jour : une action de plus (carte retournée, réponse, trait…)
        activity.edit().putInt("d$today", activity.getInt("d$today", 0) + 1).apply()
        val last = lastDay
        if (last == today) return
        val next = if (last == today - 1) count + 1 else 1
        prefs.edit().putLong("last_day", today).putInt("count", next).apply()
    }

    fun activeToday(): Boolean = lastDay == today()

    /** Nombre d'actions des `days` derniers jours, du plus ancien à aujourd'hui. */
    fun lastDays(days: Int = 7): List<Pair<LocalDate, Int>> {
        val today = LocalDate.now()
        return (days - 1 downTo 0).map { back ->
            val d = today.minusDays(back.toLong())
            d to activity.getInt("d${d.toEpochDay()}", 0)
        }
    }

    /** La série tient tant qu'on a étudié aujourd'hui ou hier. */
    fun current(): Int = if (lastDay >= today() - 1) count else 0
}
