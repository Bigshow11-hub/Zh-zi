package com.zhuzi.app.ui

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhuzi.app.data.Lang
import com.zhuzi.app.ui.theme.Rule
import java.util.Locale

/** Langue choisie, gardée sur le téléphone (par défaut : celle du téléphone si c'est en ou ru, sinon fr). */
object LangPref {
    fun get(context: Context): Lang {
        val saved = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("lang", null)
        Lang.entries.firstOrNull { it.code == saved }?.let { return it }
        return when (Locale.getDefault().language) {
            "en" -> Lang.EN
            "ru" -> Lang.RU
            else -> Lang.FR
        }
    }

    fun set(context: Context, lang: Lang) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putString("lang", lang.code).apply()
    }
}

val LocalLang = staticCompositionLocalOf { Lang.FR }

/** La phrase dans la langue choisie. */
@Composable
@ReadOnlyComposable
fun tr(fr: String, en: String, ru: String): String = tr(LocalLang.current, fr, en, ru)

fun tr(lang: Lang, fr: String, en: String, ru: String): String = when (lang) {
    Lang.FR -> fr
    Lang.EN -> en
    Lang.RU -> ru
}

/** Forme russe selon le nombre : 1 слово, 2 слова, 5 слов. */
fun ruPlural(n: Int, one: String, few: String, many: String): String {
    val a = n % 100
    val b = n % 10
    return when {
        a in 11..14 -> many
        b == 1 -> one
        b in 2..4 -> few
        else -> many
    }
}

/** « 3 mots » / « 3 words » / « 3 слова ». */
fun countOf(lang: Lang, n: Int, fr: Pair<String, String>, en: Pair<String, String>, ru: Triple<String, String, String>): String =
    "$n " + when (lang) {
        Lang.FR -> if (n > 1) fr.second else fr.first
        Lang.EN -> if (n == 1) en.first else en.second
        Lang.RU -> ruPlural(n, ru.first, ru.second, ru.third)
    }

fun wordsCount(lang: Lang, n: Int) = countOf(lang, n, "mot" to "mots", "word" to "words", Triple("слово", "слова", "слов"))

fun daysCount(lang: Lang, n: Int) = countOf(lang, n, "jour" to "jours", "day" to "days", Triple("день", "дня", "дней"))

fun lessonsCount(lang: Lang, n: Int) =
    countOf(lang, n, "leçon" to "leçons", "lesson" to "lessons", Triple("урок", "урока", "уроков"))

@Composable
@ReadOnlyComposable
fun currentLang(): Lang = LocalLang.current

/** Petit drapeau de la langue, dessiné (pas d'image). */
@Composable
fun Flag(lang: Lang, modifier: Modifier = Modifier, width: Dp = 30.dp) {
    val outline = Rule
    Canvas(modifier.size(width, width * 2 / 3).clip(RoundedCornerShape(3.dp))) {
        val w = size.width
        val h = size.height
        when (lang) {
            Lang.FR -> {
                drawRect(Color(0xFF0055A4), Offset(0f, 0f), Size(w / 3, h))
                drawRect(Color.White, Offset(w / 3, 0f), Size(w / 3, h))
                drawRect(Color(0xFFEF4135), Offset(2 * w / 3, 0f), Size(w / 3, h))
            }
            Lang.RU -> {
                drawRect(Color.White, Offset(0f, 0f), Size(w, h / 3))
                drawRect(Color(0xFF0039A6), Offset(0f, h / 3), Size(w, h / 3))
                drawRect(Color(0xFFD52B1E), Offset(0f, 2 * h / 3), Size(w, h / 3))
            }
            Lang.EN -> {
                // Drapeau britannique, simplifié
                val blue = Color(0xFF012169)
                val red = Color(0xFFC8102E)
                drawRect(blue)
                val u = h / 30f
                drawLine(Color.White, Offset(0f, 0f), Offset(w, h), 6f * u)
                drawLine(Color.White, Offset(w, 0f), Offset(0f, h), 6f * u)
                drawLine(red, Offset(0f, 0f), Offset(w, h), 2f * u)
                drawLine(red, Offset(w, 0f), Offset(0f, h), 2f * u)
                drawRect(Color.White, Offset(w / 2 - 5f * u, 0f), Size(10f * u, h))
                drawRect(Color.White, Offset(0f, h / 2 - 5f * u), Size(w, 10f * u))
                drawRect(red, Offset(w / 2 - 3f * u, 0f), Size(6f * u, h))
                drawRect(red, Offset(0f, h / 2 - 3f * u), Size(w, 6f * u))
            }
        }
        drawRect(outline, style = Stroke(1.dp.toPx()))
    }
}
