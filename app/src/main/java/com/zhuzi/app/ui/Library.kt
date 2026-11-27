package com.zhuzi.app.ui

import com.zhuzi.app.data.level
import com.zhuzi.app.data.number
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.CharInfo
import com.zhuzi.app.data.GrammarPoint
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.T3
import com.zhuzi.app.data.Tones
import com.zhuzi.app.data.Word
import com.zhuzi.app.data.charInfos
import com.zhuzi.app.data.allLessons
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Accent2
import com.zhuzi.app.ui.theme.AccentFill
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.PinyinFont
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.TileShape
import com.zhuzi.app.ui.theme.toneColor

/** Le ton d'un caractère seul (d'après son pinyin). */
fun CharInfo.tone(): Int = Tones.syllables(pinyin).firstOrNull()?.second ?: 5

/** Le nom du type de caractère. */
@Composable
fun kindName(kind: Int): String = when (kind) {
    1 -> tr("Pictogramme", "Pictograph", "Пиктограмма")
    2 -> tr("Idéogramme", "Ideograph", "Идеограмма")
    3 -> tr("Idéo-phonogramme", "Phono-semantic", "Фоноидеограмма")
    else -> tr("Forme simplifiée", "Simplified form", "Упрощённая форма")
}

/** Ce que veut dire ce type de caractère. */
@Composable
fun kindExplain(kind: Int): String = when (kind) {
    1 -> tr("C'est le dessin simplifié d'une chose : on peut encore deviner ce qu'il représente.",
        "It's a simplified drawing of a thing: you can still guess what it shows.",
        "Это упрощённый рисунок предмета: его ещё можно узнать.")
    2 -> tr("Il assemble des éléments pour exprimer une idée.",
        "It puts elements together to express an idea.",
        "Он соединяет элементы, чтобы выразить идею.")
    3 -> tr("Une partie donne le sens (la clé), l'autre donne le son. C'est le cas de 8 caractères sur 10.",
        "One part gives the meaning (the radical), the other gives the sound. 8 characters out of 10 work this way.",
        "Одна часть даёт смысл (ключ), другая — звучание. Так устроены 8 иероглифов из 10.")
    else -> tr("Sa forme a été simplifiée : le dessin d'origine ne se voit plus vraiment.",
        "Its shape was simplified: the original drawing is hard to see now.",
        "Его форма упрощена: исходный рисунок почти не виден.")
}

/** Carte d'un caractère seul : grand caractère couleur du ton, pinyin, sens, nombre de traits. */
@Composable
fun CharCard(info: CharInfo, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val lang = currentLang()
    val color = toneColor(info.tone())
    Column(
        modifier.fillMaxWidth().pressScale(interaction)
            .softCard(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(58.dp).clip(RoundedCornerShape(14.dp))
                .background(Mist),
            contentAlignment = Alignment.Center,
        ) {
            Text(info.char.toString(), fontFamily = Kai, fontSize = 38.sp, color = color)
        }
        Text(info.pinyin, fontFamily = PinyinFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color,
            modifier = Modifier.padding(top = 6.dp))
        Text(info.meaning.get(lang), fontSize = 11.sp, color = InkSoft, maxLines = 1, overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center)
        Text(countOf(lang, info.strokes, "trait" to "traits", "stroke" to "strokes", Triple("черта", "черты", "черт")),
            fontSize = 11.sp, color = InkSoft.copy(alpha = 0.8f), fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp))
    }
}

/** Fiche complète d'un caractère : animation des traits, sens, clé, composants, astuce, mots. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharDetail(info: CharInfo, speak: (String) -> Unit) {
    val lang = currentLang()
    val color = toneColor(info.tone())
    val words = remember(info) { allLessons.flatMap { it.words }.filter { info.char in it.hanzi } }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StrokeOrderView(info.char.toString(), 170.dp)
        Text(info.pinyin, fontFamily = PinyinFont, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = color,
            modifier = Modifier.padding(top = 12.dp))
        Text(info.meaning.get(lang), fontSize = 18.sp, color = InkSoft, textAlign = TextAlign.Center)
        FlowRow(
            Modifier.padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoPill(countOf(lang, info.strokes, "trait" to "traits", "stroke" to "strokes", Triple("черта", "черты", "черт")))
            InfoPill(kindName(info.kind))
        }
        Spacer(Modifier.height(18.dp))
        InkButton(tr("Écouter", "Listen", "Слушать"), { speak(info.char.toString()) }, Modifier.fillMaxWidth())

        // Comment il est construit
        SectionCard(tr("COMMENT IL EST FAIT", "HOW IT'S BUILT", "КАК ОН УСТРОЕН"), Modifier.padding(top = 20.dp)) {
            if (info.components.length > 1) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    info.components.forEachIndexed { i, c ->
                        if (i > 0) Text("+", color = InkSoft, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp))
                        ComponentTile(c.toString(), highlight = c.toString() == info.radical)
                    }
                    Text("=", color = InkSoft, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    ComponentTile(info.char.toString(), highlight = false, color = color)
                }
            }
            Text(kindName(info.kind), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Ink)
            Text(kindExplain(info.kind), fontSize = 14.sp, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
            Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(Seal.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center) {
                    Text(info.radical, fontFamily = Kai, fontSize = 22.sp, color = Seal)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(tr("Clé", "Radical", "Ключ"), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Seal)
                    Text(info.radicalMeaning.get(lang), fontSize = 14.sp, color = Ink)
                }
            }
        }
        // L'astuce pour le retenir
        MemoCard(info.memo, Modifier.padding(top = 12.dp))
        // Les mots de l'appli qui l'utilisent
        if (words.isNotEmpty()) {
            Text(tr("DANS LES MOTS", "IN WORDS", "В СЛОВАХ"), style = Kicker,
                modifier = Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 8.dp))
            words.forEach { w -> WordLine(w, speak) }
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    Box(Modifier.clip(PillShape).background(Mist).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Ink)
    }
}

@Composable
private fun ComponentTile(c: String, highlight: Boolean, color: Color = Ink) {
    val border = if (highlight) Seal else Color.Transparent
    Box(
        Modifier.size(48.dp).clip(TileShape).background(if (highlight) Seal.copy(alpha = 0.08f) else Mist)
            .border(1.5.dp, border, TileShape),
        contentAlignment = Alignment.Center,
    ) { Text(c, fontFamily = Kai, fontSize = 30.sp, color = if (highlight) Seal else color) }
}

/** Panneau à titre de section, sur fond lavande doux. */
@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Mist).padding(16.dp)) {
        Text(title, style = Kicker, modifier = Modifier.padding(bottom = 10.dp))
        content()
    }
}

/** L'astuce pour retenir, dans un encadré dégradé. */
@Composable
fun MemoCard(memo: T3, modifier: Modifier = Modifier) {
    val a = Accent
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Mist)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(a), contentAlignment = Alignment.Center) {
            Text("记", fontFamily = Kai, fontSize = 17.sp, color = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(tr("Pour le retenir", "To remember it", "Чтобы запомнить"), fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp, color = a)
            Text(memo.get(currentLang()), fontSize = 15.sp, color = Ink, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** Une ligne « mot » : caractères, pinyin, sens, bouton écouter. */
@Composable
fun WordLine(word: Word, speak: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 8.dp).softCard(RoundedCornerShape(16.dp))
            .clickable { speak(word.hanzi) }.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HanziText(word, 26.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            PinyinText(word.pinyin, 15.sp)
            Text(word.meaning(currentLang()), fontSize = 13.sp, color = InkSoft, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        PlayIcon(Accent, 14.dp)
    }
}

/** Le mot caractère par caractère : sens de chacun et astuce. */
@Composable
fun CharBreakdown(word: Word, modifier: Modifier = Modifier) {
    val lang = currentLang()
    val infos = word.hanzi.mapNotNull { charInfos[it] }
    if (infos.isEmpty()) return
    Column(modifier.fillMaxWidth()) {
        Text(tr("CARACTÈRE PAR CARACTÈRE", "CHARACTER BY CHARACTER", "ПО ИЕРОГЛИФАМ"), style = Kicker,
            modifier = Modifier.padding(bottom = 8.dp))
        infos.forEach { info ->
            val color = toneColor(info.tone())
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(16.dp)).background(Mist)
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center) {
                    Text(info.char.toString(), fontFamily = Kai, fontSize = 30.sp, color = color)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(info.pinyin, fontFamily = PinyinFont, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
                        Text("  " + info.meaning.get(lang), fontSize = 14.sp, color = Ink, fontWeight = FontWeight.Bold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(info.memo.get(lang), fontSize = 13.sp, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

/** Carte d'un point de grammaire dans la liste. */
@Composable
fun GrammarCard(point: GrammarPoint, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val lang = currentLang()
    val interaction = remember { MutableInteractionSource() }
    val tint = Ink
    Row(
        modifier.fillMaxWidth().pressScale(interaction).softCard(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(52.dp).clip(RoundedCornerShape(16.dp))
                .background(Mist),
            contentAlignment = Alignment.Center,
        ) {
            Text(point.key.take(2), fontFamily = Kai, fontSize = if (point.key.length > 1) 21.sp else 28.sp, color = tint)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(point.title.get(lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(point.pattern.get(lang) + "  ·  " + lessonName(point.lesson).lowercase(), fontSize = 12.sp,
                color = InkSoft, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        }
        Chevron(Modifier.padding(start = 6.dp))
    }
}

/** Un point de grammaire en entier : structure, explication, exemples, astuce. */
@Composable
fun GrammarDetail(point: GrammarPoint, speak: (String) -> Unit, modifier: Modifier = Modifier, showTitle: Boolean = true) {
    val lang = currentLang()
    val a = Accent
    Column(modifier.fillMaxWidth()) {
        if (showTitle) {
            Text(point.title.get(lang), style = MaterialTheme.typography.titleLarge)
        }
        // La structure, en grand, sur le dégradé
        Box(
            Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(a, Accent2))).padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column {
                Text(tr("Structure", "Pattern", "Структура"), color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp,
                    fontWeight = FontWeight.Bold)
                Text(point.pattern.get(lang), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp,
                    modifier = Modifier.padding(top = 4.dp))
            }
        }
        Text(point.explain.get(lang), fontSize = 15.sp, color = Ink, lineHeight = 22.sp, modifier = Modifier.padding(top = 12.dp))
        Text(tr("EXEMPLES", "EXAMPLES", "ПРИМЕРЫ"), style = Kicker, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        point.examples.forEach { ex ->
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(16.dp)).background(Mist)
                    .clickable { speak(ex.hanzi) }.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(ex.hanzi, fontFamily = Kai, fontSize = 22.sp, color = Ink)
                    PinyinText(ex.pinyin, 14.sp, fontWeight = FontWeight.Medium, maxLines = 3)
                    Text(ex.meaning(lang), fontSize = 13.sp, color = InkSoft)
                }
                Box(Modifier.size(34.dp).clip(CircleShape).background(a.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                    PlayIcon(a, 12.dp)
                }
            }
        }
        point.tip?.let { tip ->
            Row(
                Modifier.padding(top = 4.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Seal.copy(alpha = 0.08f)).padding(14.dp),
            ) {
                Text("!", color = Seal, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, modifier = Modifier.width(18.dp))
                Text(tip.get(lang), fontSize = 14.sp, color = Ink)
            }
        }
    }
}

/** Fiche « grammaire » dans un panneau. */
@Composable
fun GrammarSheetContent(point: GrammarPoint, speak: (String) -> Unit) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
    ) {
        Text(lessonName(point.lesson), style = Kicker)
        GrammarDetail(point, speak, Modifier.padding(top = 4.dp))
    }
}

/** « Le savais-tu ? » : la note de culture d'une leçon. */
@Composable
fun CultureCard(text: T3, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().heightIn(min = 60.dp).softCard(RoundedCornerShape(16.dp)).padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.size(38.dp).clip(TileShape).background(AccentFill), contentAlignment = Alignment.Center) {
            Text("知", fontFamily = Kai, fontSize = 22.sp, color = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(tr("Le savais-tu ?", "Did you know?", "А ты знал?"), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Ink)
            Text(text.get(currentLang()), fontSize = 14.sp, color = InkSoft, lineHeight = 20.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** Couleur des repères de leçon : une seule, neutre (l'accent reste pour les actions). */
@Composable
@Suppress("UNUSED_PARAMETER")
fun lessonTint(id: Int): Color = InkSoft

/** Petit triangle « lecture », dessiné (pas de police). */
@Composable
fun PlayIcon(color: Color, size: Dp) {
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val p = Path().apply {
            moveTo(w * 0.2f, w * 0.1f); lineTo(w * 0.92f, w * 0.5f); lineTo(w * 0.2f, w * 0.9f); close()
        }
        drawPath(p, color)
    }
}

/** « Leçon 3 » (HSK 1) ou « HSK 2 · Leçon 1 ». */
@Composable
fun lessonName(lesson: Lesson): String =
    (if (lesson.level > 1) "HSK ${lesson.level} · " else "") + tr("Leçon ", "Lesson ", "Урок ") + lesson.number

/** Même chose à partir du numéro global (règles de grammaire). */
@Composable
fun lessonName(id: Int): String = allLessons.firstOrNull { it.id == id }?.let { lessonName(it) }
    ?: (tr("Leçon ", "Lesson ", "Урок ") + id)
