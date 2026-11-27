package com.zhuzi.app.data

import android.content.Context
import java.time.LocalDate

/**
 * Révision espacée (méthode des boîtes) : chaque mot est dans une boîte de 1 à 6.
 * Mot su : il monte d'une boîte et revient plus tard. Mot oublié : retour en boîte 1.
 */
class Srs(context: Context) {
    private val prefs = context.getSharedPreferences("srs", Context.MODE_PRIVATE)

    // Copie en mémoire (lue une fois) : les écrans consultent ~600 mots à chaque affichage.
    private val cache = HashMap<String, Pair<Int, Long>>().apply {
        prefs.all.forEach { (k, v) -> (v as? String)?.let { parse(it) }?.let { put(k, it) } }
    }
    // Un mot ajouté ailleurs (bouton de la notification) met la copie à jour
    private val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
        if (key == null) { cache.clear(); return@OnSharedPreferenceChangeListener }
        val parsed = p.getString(key, null)?.let { parse(it) }
        if (parsed == null) cache.remove(key) else cache[key] = parsed
    }.also { prefs.registerOnSharedPreferenceChangeListener(it) }

    private fun parse(raw: String): Pair<Int, Long>? = runCatching {
        val (box, due) = raw.split(":")
        box.toInt() to due.toLong()
    }.getOrNull()

    /** Jours d'attente avant de revoir un mot, selon sa boîte (1 à 6). */
    private val intervals = mapOf(1 to 1, 2 to 2, 3 to 4, 4 to 7, 5 to 15, 6 to 30)

    private fun today() = LocalDate.now().toEpochDay()

    private fun read(word: Word): Pair<Int, Long>? = cache[word.hanzi]

    private fun write(word: Word, box: Int, due: Long) {
        cache[word.hanzi] = box to due
        prefs.edit().putString(word.hanzi, "$box:$due").apply()
    }

    /**
     * Mot déjà connu (niveau choisi au premier lancement) : il entre directement en boîte 4,
     * avec une première révision étalée sur les semaines à venir (pas 150 mots le même jour).
     */
    fun markKnown(word: Word, spreadDays: Int) {
        if (read(word) == null) write(word, 4, today() + 3 + spreadDays)
    }

    /** Un mot vu pour la première fois entre en boîte 1, à revoir demain. */
    fun learn(word: Word) {
        if (read(word) == null) write(word, 1, today() + 1)
    }

    /** Réponse pendant une révision ou un quiz. */
    fun answer(word: Word, knew: Boolean) {
        val box = read(word)?.first ?: 0
        if (knew) {
            val next = (box + 1).coerceIn(1, 6)
            write(word, next, today() + intervals.getValue(next))
        } else {
            write(word, 1, today() + 1)
        }
    }

    /** Pendant un quiz : une erreur renvoie le mot en boîte 1 ; une bonne réponse ne fait que l'ajouter. */
    fun quizAnswer(word: Word, correct: Boolean) {
        if (correct) learn(word) else write(word, 1, today() + 1)
    }

    fun dueWords(all: List<Word>): List<Word> = all.filter { w ->
        val state = read(w)
        state != null && state.second <= today()
    }

    fun dueTomorrow(all: List<Word>): Int = all.count { w ->
        val state = read(w)
        state != null && state.second == today() + 1
    }

    fun knownCount(all: List<Word>): Int = all.count { read(it) != null }

    /** Mots en boîte 4 ou plus : considérés comme bien retenus. */
    fun masteredCount(all: List<Word>): Int = all.count { (read(it)?.first ?: 0) >= 4 }

    /** Boîte du mot : 0 = pas encore découvert, 1 à 6 sinon (4 et plus = bien retenu). */
    fun boxOf(word: Word): Int = read(word)?.first ?: 0
}
