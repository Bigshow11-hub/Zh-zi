package com.zhuzi.app.data

import java.text.Normalizer
import java.util.regex.Pattern

/**
 * Les tons du pinyin : découpe « xièxie » en syllabes (xiè, xie) et trouve le ton de chacune
 * (1 ā, 2 á, 3 ǎ, 4 à, 5 = neutre, sans signe).
 */
object Tones {
    // Une syllabe : initiale facultative, voyelles, finale facultative (n, ng, r) ; « r » final = 儿
    private val syllable: Pattern = Pattern.compile(
        "(zh|ch|sh|[bpmfdtnlgkhjqxrzcsyw])?[aeiouv]+(ng|n(?![aeiouv])|r(?![aeiouv]))?(r(?![aeiouv]))?",
    )

    private fun toneOf(mark: Char?): Int = when (mark) {
        '̄' -> 1
        '́' -> 2
        '̌' -> 3
        '̀' -> 4
        else -> 5
    }

    /** Le pinyin en morceaux : chaque syllabe avec son ton, et les séparateurs (ton null). */
    fun pieces(pinyin: String): List<Pair<String, Int?>> {
        val nfd = Normalizer.normalize(pinyin, Normalizer.Form.NFD)
        val out = mutableListOf<Pair<String, Int?>>()
        val part = StringBuilder()
        for (c in nfd) {
            // Tout ce qui n'est ni lettre ni signe de ton sépare les syllabes : espace, apostrophe, ponctuation
            if (!Character.isLetter(c) && Character.getType(c) != Character.NON_SPACING_MARK.toInt()) {
                out += segment(part.toString()); part.clear()
                out += c.toString() to null
            } else {
                part.append(c)
            }
        }
        out += segment(part.toString())
        return out
    }

    private fun segment(part: String): List<Pair<String, Int?>> {
        if (part.isEmpty()) return emptyList()
        val bases = StringBuilder()
        val starts = mutableListOf<Int>()
        val marks = mutableListOf<Char?>()
        part.forEachIndexed { i, c ->
            if (Character.getType(c) == Character.NON_SPACING_MARK.toInt()) {
                if (c == '̈') {
                    if (bases.isNotEmpty() && bases.last() == 'u') bases.setCharAt(bases.length - 1, 'v')
                } else if (marks.isNotEmpty()) {
                    marks[marks.size - 1] = c
                }
            } else {
                bases.append(c.lowercaseChar()); starts += i; marks += null
            }
        }
        val result = mutableListOf<Pair<String, Int?>>()
        val m = syllable.matcher(bases)
        var pos = 0
        while (pos < bases.length) {
            m.region(pos, bases.length)
            if (!m.lookingAt() || m.end() == pos) {
                result += nfc(part.substring(starts[pos])) to null
                break
            }
            val end = m.end()
            val tone = toneOf((pos until end).firstNotNullOfOrNull { marks[it] })
            val stop = if (end < bases.length) starts[end] else part.length
            result += nfc(part.substring(starts[pos], stop)) to tone
            pos = end
        }
        return result
    }

    private fun nfc(s: String) = Normalizer.normalize(s, Normalizer.Form.NFC)

    /** Un ton par caractère du mot (儿 fondu dans la syllabe d'avant compte comme neutre). */
    fun forWord(hanzi: String, pinyin: String): List<Int> {
        val tones = pieces(pinyin).mapNotNull { it.second }.toMutableList()
        if (tones.size < hanzi.length && hanzi.endsWith("儿")) {
            tones.add(tones.size.coerceAtMost(hanzi.length - 1), 5)
        }
        while (tones.size < hanzi.length) tones += 5
        return tones.take(hanzi.length)
    }
}
