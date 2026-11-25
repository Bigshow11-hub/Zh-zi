package com.zhuzi.app.data

import android.content.Context

/** Les trois sortes de quiz. */
enum class QuizMode(val label: String, val key: String) {
    MEANING("Sens", ""),
    WRITING("Écrit", "_writing"),
    LISTENING("Écoute", "_listening"),
}

/** Garde sur le téléphone le meilleur score (en %) de chaque leçon, pour chaque quiz. */
class Progress(context: Context) {
    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)

    fun bestScore(lessonId: Int, mode: QuizMode): Int = prefs.getInt("best_$lessonId${mode.key}", -1)

    fun saveScore(lessonId: Int, mode: QuizMode, percent: Int) {
        if (percent > bestScore(lessonId, mode)) {
            prefs.edit().putInt("best_$lessonId${mode.key}", percent).apply()
        }
    }
}
