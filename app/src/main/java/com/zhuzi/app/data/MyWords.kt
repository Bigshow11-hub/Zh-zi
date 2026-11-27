package com.zhuzi.app.data

import android.content.Context
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import org.json.JSONArray
import org.json.JSONObject

/** Un mot ajouté par l'élève (à la main ou depuis une photo de son cours). */
data class MyWord(
    val id: Long,
    val hanzi: String,
    val pinyin: String,
    val meaning: String,
    /** "photo" ou "main". */
    val source: String,
) {
    /** Le même mot sous la forme des mots de l'appli : il passe par les cartes et la révision espacée. */
    fun toWord() = Word(hanzi, pinyin, meaning, meaning, meaning)
}

/** Les mots de l'élève, gardés sur le téléphone. */
class MyWords(context: Context) {
    private val prefs = context.getSharedPreferences("mywords", Context.MODE_PRIVATE)

    fun all(): List<MyWord> {
        val raw = prefs.getString("list", null) ?: return emptyList()
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            MyWord(o.getLong("id"), o.getString("h"), o.getString("p"), o.getString("m"), o.optString("s", "main"))
        }
    }

    private fun save(list: List<MyWord>) {
        val arr = JSONArray()
        list.forEach { w ->
            arr.put(JSONObject().put("id", w.id).put("h", w.hanzi).put("p", w.pinyin).put("m", w.meaning).put("s", w.source))
        }
        prefs.edit().putString("list", arr.toString()).apply()
    }

    /** Ajoute des mots (un mot déjà présent, mêmes caractères, est mis à jour). Rend les mots ajoutés. */
    fun add(items: List<Triple<String, String, String>>, source: String): List<MyWord> {
        val list = all().toMutableList()
        var id = (list.maxOfOrNull { it.id } ?: 0L) + 1
        val added = mutableListOf<MyWord>()
        items.forEach { (h, p, m) ->
            val hz = h.trim()
            if (hz.isEmpty()) return@forEach
            list.removeAll { it.hanzi == hz }
            val w = MyWord(id++, hz, p.trim(), m.trim(), source)
            list += w
            added += w
        }
        save(list)
        return added
    }

    fun remove(id: Long) = save(all().filterNot { it.id == id })
}

/** Le pinyin d'un texte chinois, avec les accents des tons (premier son connu pour chaque caractère). */
object PinyinAuto {
    private val format = HanyuPinyinOutputFormat().apply {
        caseType = HanyuPinyinCaseType.LOWERCASE
        toneType = HanyuPinyinToneType.WITH_TONE_MARK
        vCharType = HanyuPinyinVCharType.WITH_U_UNICODE
    }

    /** Les sons déjà connus de l'appli passent en premier (ex. 了 le, 的 de). */
    private val known: Map<String, String> by lazy {
        allLessons.flatMap { it.words }.associate { it.hanzi to it.pinyin }
    }

    fun of(text: String): String {
        val t = text.trim()
        known[t]?.let { return it }
        val parts = mutableListOf<String>()
        t.forEach { c ->
            val ci = charInfos[c]
            val py = when {
                ci != null -> ci.pinyin
                isHan(c) -> try {
                    PinyinHelper.toHanyuPinyinStringArray(c, format)?.firstOrNull()
                } catch (e: Exception) {
                    null
                }
                else -> null
            }
            if (py != null) parts += py else if (!c.isWhitespace()) parts += c.toString()
        }
        return parts.joinToString(" ")
    }

    fun isHan(c: Char) = c in '一'..'鿿' || c in '㐀'..'䶿'

    /** Sens déjà connu de l'appli : le mot entier, sinon caractère par caractère (« tu + bon »). */
    fun meaningHint(text: String, lang: Lang): String {
        val t = text.trim()
        allLessons.flatMap { it.words }.firstOrNull { it.hanzi == t }?.let { return it.meaning(lang) }
        val parts = t.mapNotNull { charInfos[it]?.meaning?.get(lang)?.substringBefore(",") }
        return if (parts.size == t.length && parts.isNotEmpty()) parts.joinToString(" + ") else ""
    }
}
