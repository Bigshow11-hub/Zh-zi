package com.zhuzi.app.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Rule

/** Onglet Leçons : le parcours HSK 1, avec la progression de chaque leçon. */
@Composable
fun LessonsScreen(
    bestScore: (Int, QuizMode) -> Int,
    lessonKnown: (Lesson) -> Int,
    onOpen: (Lesson) -> Unit,
    contentPadding: PaddingValues,
) {
    val total = hsk1Lessons.sumOf { it.words.size }
    val mastered: (Lesson) -> Boolean = { l -> QuizMode.entries.all { bestScore(l.id, it) >= 80 } }
    val masteredCount = hsk1Lessons.count(mastered)
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
    ) {
        item {
            ScreenTitle(
                tr("Leçons", "Lessons", "Уроки"),
                "HSK 1 · " + lessonsCount(currentLang(), hsk1Lessons.size) + " · " + wordsCount(currentLang(), total) +
                    " · $masteredCount " + tr(if (masteredCount > 1) "maîtrisées" else "maîtrisée", "mastered",
                        ruPlural(masteredCount, "освоен", "освоено", "освоено")),
            )
        }
        items(hsk1Lessons, key = { it.id }) { lesson ->
            LessonRow(lesson, lessonKnown(lesson), mastered(lesson), bestScore, onOpen)
        }
    }
}

/** Titre d'onglet : grand titre et sous-titre. */
@Composable
fun ScreenTitle(title: String, subtitle: String) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = InkSoft,
            modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun LessonRow(
    lesson: Lesson,
    known: Int,
    isMastered: Boolean,
    bestScore: (Int, QuizMode) -> Int,
    onOpen: (Lesson) -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(Modifier.padding(bottom = 10.dp)) {
        Row(
            Modifier.fillMaxWidth()
                .pressScale(interaction)
                .softCard(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
                .clickable(interactionSource = interaction, indication = null) { onOpen(lesson) }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(lesson.id.toString().padStart(2, '0'), color = InkSoft, fontSize = 13.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
            CharGrid(lesson.symbol, 48.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(lesson.title(currentLang()), fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProgressBar(known.toFloat() / lesson.words.size, Modifier.weight(1f), height = 4.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("$known/${lesson.words.size}", fontSize = 12.sp, color = InkSoft)
                }
                // Les trois quiz : plein quand le score atteint 80 %
                Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuizMode.entries.forEach { m ->
                        val done = bestScore(lesson.id, m) >= 80
                        Text(
                            (if (done) "● " else "○ ") + m.label(currentLang()),
                            fontSize = 11.sp,
                            color = if (done) Ink else InkSoft,
                            fontWeight = if (done) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            if (isMastered) {
                SealStamp("记", 30.dp, animate = false)
            } else {
                Text("›", fontSize = 24.sp, color = InkSoft)
            }
        }
    }
}
