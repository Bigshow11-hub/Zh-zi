package com.zhuzi.app.data

import android.content.Context

/** Les sortes de quiz. Les trois premières comptent pour « leçon maîtrisée » ; les tons sont en plus. */
enum class QuizMode(private val fr: String, private val en: String, private val ru: String, val key: String) {
    MEANING("Sens", "Meaning", "Смысл", ""),
    WRITING("Écrit", "Writing", "Письмо", "_writing"),
    LISTENING("Écoute", "Listening", "Слух", "_listening"),
    TONES("Tons", "Tones", "Тоны", "_tones");

    fun label(lang: Lang): String = when (lang) {
        Lang.FR -> fr
        Lang.EN -> en
        Lang.RU -> ru
    }

    companion object {
        /** Les quiz qui comptent pour maîtriser une leçon. */
        val core = listOf(MEANING, WRITING, LISTENING)
    }
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

    /** Meilleure note (sur 100) à l'examen blanc d'un niveau HSK, ou -1. */
    fun bestExam(level: Int): Int = prefs.getInt("exam_$level", -1)

    fun saveExam(level: Int, score: Int) {
        if (score > bestExam(level)) prefs.edit().putInt("exam_$level", score).apply()
    }
}
