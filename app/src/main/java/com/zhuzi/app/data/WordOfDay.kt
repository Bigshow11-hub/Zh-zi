package com.zhuzi.app.data

import android.content.Context
import java.time.LocalDate

/**
 * Le mot du jour : chaque jour un mot nouveau, pas encore découvert, dans l'ordre des leçons.
 * Le même mot est montré dans l'appli et dans la notification du matin (gardé pour la journée).
 */
object WordOfDay {
    private fun prefs(context: Context) = context.getSharedPreferences("word_of_day", Context.MODE_PRIVATE)

    fun today(context: Context): Word {
        val all = allLessons.flatMap { it.words }
        val p = prefs(context)
        val day = LocalDate.now().toEpochDay()
        if (p.getLong("day", -1) == day) {
            all.firstOrNull { it.hanzi == p.getString("hanzi", null) }?.let { return it }
        }
        val srs = Srs(context)
        val seen = p.getStringSet("seen", emptySet()) ?: emptySet()
        val word = all.firstOrNull { srs.boxOf(it) == 0 && it.hanzi !in seen }
            ?: all.firstOrNull { srs.boxOf(it) == 0 }
            ?: all[(day % all.size).toInt()]
        p.edit().putLong("day", day).putString("hanzi", word.hanzi)
            .putStringSet("seen", seen + word.hanzi).apply()
        return word
    }

    /** La leçon où se trouve ce mot (pour l'ouvrir depuis la notification). */
    fun lessonOf(word: Word): Lesson? = allLessons.firstOrNull { l -> l.words.any { it.hanzi == word.hanzi } }
}
