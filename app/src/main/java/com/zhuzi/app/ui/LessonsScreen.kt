package com.zhuzi.app.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.allLessons
import com.zhuzi.app.data.level
import com.zhuzi.app.data.lessonIntros
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Accent2
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape


/** Onglet Leçons : synthèse du parcours, puis une carte par leçon. */
@Composable
fun LessonsScreen(
    bestScore: (Int, QuizMode) -> Int,
    lessonKnown: (Lesson) -> Int,
    onOpen: (Lesson) -> Unit,
    contentPadding: PaddingValues,
    myCount: Int = 0,
    onMyWords: () -> Unit = {},
    bestExam: (Int) -> Int = { -1 },
    onExam: (Int) -> Unit = {},
) {
    val lang = currentLang()
    // La leçon en cours, toutes niveaux confondus ; le niveau affiché part de celui-là
    val current = allLessons.firstOrNull { lessonKnown(it) < it.words.size }
    var level by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableIntStateOf(current?.level ?: 1) }
    val lessons = allLessons.filter { it.level == level }
    val total = lessons.sumOf { it.words.size }
    val known = lessons.sumOf { lessonKnown(it) }
    val mastered: (Lesson) -> Boolean = { l -> QuizMode.core.all { bestScore(l.id, it) >= 80 } }
    val masteredCount = lessons.count(mastered)
    val seen = remember(level) { mutableSetOf<Any>() }
    val list = androidx.compose.foundation.lazy.rememberLazyListState()
    val compact by remember {
        androidx.compose.runtime.derivedStateOf { list.firstVisibleItemIndex > 0 || list.firstVisibleItemScrollOffset > 140 }
    }
    Box(Modifier.fillMaxSize()) {
    LazyColumn(
        state = list,
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        staggerItem(0, seen) {
            Column {
                LargeTitle(tr("Leçons", "Lessons", "Уроки"),
                    subtitle = tr("Le parcours HSK, thème par thème", "The HSK path, theme by theme", "Путь HSK, тема за темой"))
                Segmented(listOf("HSK 1", "HSK 2", "HSK 3", "HSK 4"), level - 1, { level = it + 1 })
            }
        }
        // Synthèse du parcours
        staggerItem(1, seen) {
            val a = Accent
            Column(
                Modifier.fillMaxWidth().softCard().padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$known", style = MaterialTheme.typography.displaySmall, color = a)
                    Text(" / $total " + tr("mots du HSK $level", "HSK $level words", "слов HSK $level"), color = InkSoft,
                        style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 6.dp))
                }
                ProgressBar(known.toFloat() / total, Modifier.padding(top = 12.dp), color = a, height = 6.dp)
                Text(
                    tr("$masteredCount leçon${if (masteredCount > 1) "s" else ""} maîtrisée${if (masteredCount > 1) "s" else ""} sur ${lessons.size}",
                        "$masteredCount of ${lessons.size} lessons mastered",
                        "Освоено уроков: $masteredCount из ${lessons.size}"),
                    color = InkSoft, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        // Mes mots et l'examen blanc : une petite liste groupée
        staggerItem(2, seen) {
            Column(Modifier.fillMaxWidth().clip(com.zhuzi.app.ui.theme.GroupShape).background(Paper)) {
                MyWordsEntry(myCount, onMyWords)
                RowDivider(78.dp)
                ExamEntry(level, bestExam(level)) { onExam(level) }
            }
        }
        lessons.forEachIndexed { i, lesson ->
            staggerItem(i + 3, seen) {
                LessonCard(lesson, lessonKnown(lesson), mastered(lesson), lesson == current, bestScore, onOpen)
            }
        }
    }
    CompactTitleBar(tr("Leçons", "Lessons", "Уроки"), compact, contentPadding.calculateTopPadding())
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun LessonCard(
    lesson: Lesson,
    known: Int,
    isMastered: Boolean,
    isCurrent: Boolean,
    bestScore: (Int, QuizMode) -> Int,
    onOpen: (Lesson) -> Unit,
) {
    val lang = currentLang()
    val interaction = remember { MutableInteractionSource() }
    val tint = Accent
    val shape = com.zhuzi.app.ui.theme.PanelShape
    val complete = known >= lesson.words.size
    Column(
        Modifier.fillMaxWidth()
            .pressScale(interaction)
            .softCard(shape)
            .then(if (isCurrent) Modifier.border(1.5.dp, tint, shape) else Modifier)
            .clickable(interactionSource = interaction, indication = null,
                role = androidx.compose.ui.semantics.Role.Button) { onOpen(lesson) }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Le caractère symbole de la leçon, sur fond neutre (accent seulement pour la leçon en cours)
            Box(
                Modifier.size(52.dp).clip(com.zhuzi.app.ui.theme.TileShape).background(if (isCurrent) tint.copy(alpha = 0.12f) else Mist),
                contentAlignment = Alignment.Center,
            ) { Text(lesson.symbol, fontFamily = Kai, fontSize = 30.sp, color = if (isCurrent) tint else com.zhuzi.app.ui.theme.Ink) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(lesson.title(lang), style = MaterialTheme.typography.titleMedium, maxLines = 2,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    when {
                        isMastered -> SealStamp("记", 24.dp, animate = false)
                        isCurrent -> Badge(tr("En cours", "In progress", "В процессе"), Accent)
                        complete -> Badge(tr("Découverte", "Discovered", "Пройден"), InkSoft)
                    }
                }
                Text(lessonName(lesson) + " · " + wordsCount(lang, lesson.words.size),
                    style = MaterialTheme.typography.bodySmall, color = InkSoft, modifier = Modifier.padding(top = 1.dp))
                lessonIntros[lesson.id]?.let {
                    Text(it.get(lang), style = MaterialTheme.typography.bodyMedium, color = InkSoft, maxLines = 2,
                        overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        Row(Modifier.padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            ProgressBar(known.toFloat() / lesson.words.size, Modifier.weight(1f), color = if (complete) InkSoft else tint, height = 4.dp)
            Text("  $known/${lesson.words.size}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
        }
        // Les quiz, en pilules : pleines quand le score atteint 80 % (passent à la ligne si besoin)
        androidx.compose.foundation.layout.FlowRow(
            Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            QuizMode.entries.forEach { m ->
                val done = bestScore(lesson.id, m) >= 80
                Row(
                    Modifier.clip(PillShape).background(if (done) tint.copy(alpha = 0.12f) else Mist)
                        .padding(start = if (done) 7.dp else 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (done) ZIcon(Glyph.CHECK, tint, size = 13.dp, weight = 1.8f, modifier = Modifier.padding(end = 3.dp))
                    Text(m.label(lang), style = MaterialTheme.typography.labelMedium, fontSize = 12.sp,
                        color = if (done) tint else InkSoft)
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Box(Modifier.clip(PillShape).background(color.copy(alpha = 0.13f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

/** Entrée « Mes mots » : ajouter ses propres mots ou les prendre en photo (ligne de liste groupée). */
@Composable
private fun MyWordsEntry(count: Int, onClick: () -> Unit) {
    val lang = currentLang()
    GroupedRow(
        title = tr("Mes mots", "My words", "Мои слова"),
        subtitle = if (count == 0) tr("Ajoute les mots de tes cours, ou prends une page en photo",
            "Add words from your classes, or snap a page", "Добавь слова с занятий или сфотографируй страницу")
        else wordsCount(lang, count) + tr(" · ajouter ou photographier", " · add or snap more", " · добавить или сфотографировать"),
        icon = { KaiTile("词", size = 48.dp) },
        chevron = true,
        onClick = onClick,
    )
}
