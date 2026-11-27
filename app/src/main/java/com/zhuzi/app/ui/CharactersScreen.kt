package com.zhuzi.app.ui

import com.zhuzi.app.data.level
import com.zhuzi.app.data.number
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Word
import com.zhuzi.app.data.allLessons
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.TileShape
import java.text.Normalizer

/** Enlève les accents et tons (nǐ → ni, é → e) pour chercher sans se soucier des signes. */
private fun plain(s: String): String =
    Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{M}"), "").lowercase().trim()

/** Onglet Caractères : les mots, les caractères un par un et la grammaire, avec recherche et fiches. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    boxOf: (Word) -> Int,
    speak: (String) -> Unit,
    contentPadding: PaddingValues,
    startSection: Int = 0,
) {
    val entries = remember { allLessons.flatMap { l -> l.words.map { it to l } } }
    var section by rememberSaveable { mutableIntStateOf(startSection) }
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Pair<Word, Lesson>?>(null) }
    var selectedChar by remember { mutableStateOf<com.zhuzi.app.data.CharInfo?>(null) }
    var selectedPoint by remember { mutableStateOf<com.zhuzi.app.data.GrammarPoint?>(null) }
    val seen = remember { mutableSetOf<Any>() }

    val lang = currentLang()
    val q = plain(query)
    val shown = entries.filter { (w, _) ->
        val box = boxOf(w)
        val keep = when (filter) {
            1 -> box > 0
            2 -> box >= 4
            3 -> box == 0
            else -> true
        }
        keep && (q.isEmpty() || plain(w.pinyin).contains(q) || plain(w.meaning(lang)).contains(q) ||
            w.hanzi.contains(query.trim()))
    }
    val chars = com.zhuzi.app.data.charInfos.values.filter { c ->
        q.isEmpty() || plain(c.pinyin).contains(q) || plain(c.meaning.get(lang)).contains(q) || query.trim().contains(c.char)
    }
    val points = com.zhuzi.app.data.grammarPoints.filter { p ->
        q.isEmpty() || plain(p.title.get(lang)).contains(q) || plain(p.pattern.get(lang)).contains(q) || p.key.contains(query.trim())
    }
    val known = entries.count { boxOf(it.first) > 0 }

    val grid = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val compact by remember {
        androidx.compose.runtime.derivedStateOf { grid.firstVisibleItemIndex > 0 || grid.firstVisibleItemScrollOffset > 140 }
    }
    Box(Modifier.fillMaxSize()) {
    // 6 colonnes : les mots en prennent 3 (2 par ligne), les caractères 2 (3 par ligne), la grammaire 6
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        state = grid,
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "header") {
            Column {
                ScreenTitle(
                    tr("Caractères", "Characters", "Иероглифы"),
                    when (section) {
                        0 -> tr("$known mots découverts sur ${entries.size} · touche un mot pour sa fiche",
                            "$known of ${entries.size} words discovered · tap one for details",
                            "Открыто $known из ${entries.size} слов · нажми на слово")
                        1 -> tr("${com.zhuzi.app.data.charInfos.size} caractères : sens, clé et astuce pour les retenir",
                            "${com.zhuzi.app.data.charInfos.size} characters: meaning, radical and a tip to remember them",
                            "${com.zhuzi.app.data.charInfos.size} иероглифа: смысл, ключ и подсказка")
                        else -> tr("${com.zhuzi.app.data.grammarPoints.size} règles du HSK 1 à 3, avec des exemples à écouter",
                            "${com.zhuzi.app.data.grammarPoints.size} HSK 1–3 rules, with examples to listen to",
                            "${com.zhuzi.app.data.grammarPoints.size} правил HSK 1–3 с примерами")
                    },
                )
                Segmented(listOf(tr("Mots", "Words", "Слова"), tr("Caractères", "Characters", "Знаки"),
                    tr("Grammaire", "Grammar", "Грамматика")), section, { section = it; filter = 0 })
                Spacer(Modifier.height(4.dp))
                SearchField(query) { query = it }
                if (section == 0) {
                    Spacer(Modifier.height(4.dp))
                    Segmented(listOf(tr("Tous", "All", "Все"), tr("Vus", "Seen", "Видел"), tr("Retenus", "Learned", "Выучено"),
                        tr("Nouveaux", "New", "Новые")), filter, { filter = it })
                    Spacer(Modifier.height(4.dp))
                    ToneLegend(Modifier.padding(start = 4.dp))
                }
                if (section == 1) {
                    Spacer(Modifier.height(12.dp))
                    ToneLegend(Modifier.padding(start = 4.dp))
                }
                Spacer(Modifier.height(6.dp))
            }
        }
        val empty = when (section) { 0 -> shown.isEmpty(); 1 -> chars.isEmpty(); else -> points.isEmpty() }
        if (empty) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "empty") {
                Text(tr("Rien trouvé.", "Nothing found.", "Ничего не найдено."), color = InkSoft, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp))
            }
        }
        when (section) {
            0 -> itemsIndexed(shown, key = { _, it -> "w" + it.first.hanzi }, span = { _, _ -> GridItemSpan(3) }) { i, (w, l) ->
                WordCard(w, l, boxOf(w), Modifier.animateItem().staggerIn("w" + w.hanzi, i, seen)) { selected = w to l }
            }
            1 -> itemsIndexed(chars, key = { _, it -> "c" + it.char }, span = { _, _ -> GridItemSpan(2) }) { i, c ->
                CharCard(c, Modifier.animateItem().staggerIn("c" + c.char, i, seen)) { selectedChar = c }
            }
            else -> itemsIndexed(points, key = { _, it -> "g" + it.id }, span = { _, _ -> GridItemSpan(6) }) { i, p ->
                GrammarCard(p, Modifier.animateItem().staggerIn("g" + p.id, i, seen)) { selectedPoint = p }
            }
        }
    }

    CompactTitleBar(tr("Caractères", "Characters", "Иероглифы"), compact, contentPadding.calculateTopPadding())
    }

    selected?.let { (w, l) ->
        ZhuziSheet({ selected = null }) {
            WordDetail(w, l, boxOf(w), speak)
        }
    }
    selectedChar?.let { c ->
        ZhuziSheet({ selectedChar = null }) { CharDetail(c, speak) }
    }
    selectedPoint?.let { p ->
        ZhuziSheet({ selectedPoint = null }) { GrammarSheetContent(p, speak) }
    }
}

/** Champ de recherche façon iOS : loupe, fond gris translucide, croix pour effacer. Zone tactile de 48 dp. */
@Composable
private fun SearchField(value: String, onChange: (String) -> Unit) {
    val soft = InkSoft
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink),
        cursorBrush = SolidColor(com.zhuzi.app.ui.theme.Accent),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        decorationBox = { inner ->
            Row(
                Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(12.dp)).background(Mist)
                    .padding(start = 10.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ZIcon(Glyph.SEARCH, soft, size = 18.dp, weight = 1.2f)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(tr("Chercher : ni hao, merci, 你…", "Search: ni hao, thanks, 你…", "Поиск: ni hao, спасибо, 你…"),
                        color = soft, style = MaterialTheme.typography.bodyLarge, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    inner()
                }
                if (value.isNotEmpty()) {
                    // Effacer : rond gris avec une croix (cible de 40 dp)
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).clickable(role = androidx.compose.ui.semantics.Role.Button) { onChange("") }
                            .semantics { contentDescription = tr("Effacer", "Clear", "Очистить") },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(Modifier.size(18.dp).clip(CircleShape).background(soft.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                            ZIcon(Glyph.CLOSE, Paper, size = 12.dp, weight = 2f)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun WordCard(word: Word, lesson: Lesson, box: Int, modifier: Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier.fillMaxWidth().pressScale(interaction)
            .softCard()
            .clickable(interactionSource = interaction, indication = null,
                role = androidx.compose.ui.semantics.Role.Button, onClick = onClick)
            .padding(10.dp),
    ) {
        // Le mot posé sur un fond doux, comme une feuille sur le bureau
        Box(
            Modifier.fillMaxWidth().height(78.dp).clip(com.zhuzi.app.ui.theme.TileShape).background(Mist)
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            WordGrids(word.hanzi, maxSize = 50.dp, pinyin = word.pinyin)
        }
        Spacer(Modifier.height(8.dp))
        PinyinText(word.pinyin, 16.sp, modifier = Modifier.padding(horizontal = 2.dp))
        Text(word.meaning(currentLang()), fontSize = 13.sp, color = InkSoft, maxLines = 2, minLines = 2,
            overflow = TextOverflow.Ellipsis, lineHeight = 17.sp, modifier = Modifier.padding(horizontal = 2.dp))
        Row(Modifier.padding(top = 6.dp, start = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text((if (lesson.level > 1) "H${lesson.level}·L" else "L") + lesson.number, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkSoft,
                modifier = Modifier.clip(PillShape).background(Mist)
                    .padding(horizontal = 7.dp, vertical = 2.dp))
            Spacer(Modifier.width(8.dp))
            StatusTag(box)
        }
    }
}

/** Statut du mot : petit sceau « Retenu », point « Vu », ou rien (hauteur gardée). */
@Composable
private fun StatusTag(box: Int) {
    Row(Modifier.height(16.dp), verticalAlignment = Alignment.CenterVertically) {
        when {
            box >= 4 -> {
                Box(
                    Modifier.size(9.dp).graphicsLayer { rotationZ = -6f }
                        .clip(RoundedCornerShape(1.5.dp)).background(Seal),
                )
                Text("  " + tr("Retenu", "Learned", "Выучено"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Seal)
            }
            box > 0 -> {
                Box(Modifier.size(6.dp).clip(CircleShape).background(InkSoft))
                Text("  " + tr("Vu", "Seen", "Видел"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkSoft)
            }
            else -> Text(tr("Nouveau", "New", "Новое"), fontSize = 10.sp, color = InkSoft)
        }
    }
}

@Composable
private fun WordDetail(word: Word, lesson: Lesson, box: Int, speak: (String) -> Unit) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WordGrids(word.hanzi, maxSize = 120.dp, diagonals = true, pinyin = word.pinyin)
        Spacer(Modifier.height(18.dp))
        PinyinText(word.pinyin, 30.sp)
        Text(word.meaning(currentLang()), fontSize = 18.sp, color = InkSoft, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Text(lessonName(lesson).uppercase() + " · " + lesson.short(currentLang()).uppercase(), style = Kicker)
        Text(
            when {
                box >= 4 -> tr("Bien retenu", "Well learned", "Хорошо выучено")
                box > 0 -> tr("Découvert · en révision", "Discovered · in review", "Открыто · на повторении")
                else -> tr("Pas encore découvert", "Not discovered yet", "Ещё не открыто")
            },
            style = MaterialTheme.typography.bodyMedium, color = Ink,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(14.dp))
        ToneLegend()
        Spacer(Modifier.height(20.dp))
        InkButton(tr("Écouter", "Listen", "Слушать"), { speak(word.hanzi) }, Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        ExampleBlock(word, speak)
        Spacer(Modifier.height(18.dp))
        CharBreakdown(word)
        Spacer(Modifier.height(28.dp))
        StrokePanel(word)
    }
}
