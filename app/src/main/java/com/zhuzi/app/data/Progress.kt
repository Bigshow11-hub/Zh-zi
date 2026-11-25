package com.zhuzi.app.data

import android.content.Context

/** Garde sur le téléphone le meilleur score de quiz de chaque leçon (en %). */
class Progress(context: Context) {
    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)

    fun bestScore(lessonId: Int): Int = prefs.getInt("best_$lessonId", -1)

    fun saveScore(lessonId: Int, percent: Int) {
        if (percent > bestScore(lessonId)) {
            prefs.edit().putInt("best_$lessonId", percent).apply()
        }
    }
}
