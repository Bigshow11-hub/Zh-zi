package com.zhuzi.app.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Word
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.reminder.Reminders
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.PanelShape
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.TileShape

/** Accueil : aujourd'hui (révision, progrès), reprendre, puis le parcours HSK 1. */
@Composable
fun LessonListScreen(
    bestScore: (Int, QuizMode) -> Int,
    lessonKnown: (Lesson) -> Int,
    onOpen: (Lesson) -> Unit,
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    onReview: () -> Unit,
    streakDays: Int,
    todayLabel: String,
    reminderOn: Boolean,
    onEnableReminder: () -> Unit,
    contentPadding: PaddingValues,
) {
    val total = hsk1Lessons.sumOf { it.words.size }
    // Leçon à reprendre : la première pas encore entièrement découverte
    val next = hsk1Lessons.firstOrNull { lessonKnown(it) < it.words.size } ?: hsk1Lessons.last()
    val mastered: (Lesson) -> Boolean = { l -> QuizMode.entries.all { bestScore(l.id, it) >= 80 } }

    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
    ) {
        // En-tête : marque, date, série
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.ic_logo_mark), contentDescription = null,
                    modifier = Modifier.size(44.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Zhúzi", style = MaterialTheme.typography.titleLarge)
                    Text(todayLabel, style = MaterialTheme.typography.bodySmall, color = InkSoft)
                }
                if (streakDays > 0) StreakBadge(streakDays)
            }
        }

        // Aujourd'hui
        item {
            TodayPanel(
                dueWords = dueWords, dueTomorrow = dueTomorrow, knownCount = knownCount,
                masteredCount = masteredCount, total = total, next = next,
                onReview = onReview, onOpen = onOpen,
            )
        }

        // Reprendre (quand le bouton principal sert à réviser)
        if (dueWords.isNotEmpty()) {
            item { ContinueCard(next, lessonKnown(next), onOpen) }
        }

        // Parcours
        item {
            Row(Modifier.padding(top = 32.dp, bottom = 4.dp), verticalAlignment = Alignment.Bottom) {
                Text("Parcours HSK 1", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                Text("${hsk1Lessons.size} leçons · $total mots", style = MaterialTheme.typography.bodySmall,
                    color = InkSoft)
            }
        }
        items(hsk1Lessons, key = { it.id }) { lesson ->
            LessonRow(lesson, lessonKnown(lesson), mastered(lesson), bestScore, onOpen)
        }

        // Rappel
        item {
            Box(Modifier.padding(top = 20.dp)) {
                if (reminderOn) {
                    Text("Rappel chaque soir vers ${Reminders.HOUR} h si tu n'as pas encore étudié.",
                        style = MaterialTheme.typography.bodySmall, color = InkSoft)
                } else {
                    TextButton(onClick = onEnableReminder, contentPadding = PaddingValues(0.dp)) {
                        Text("Activer le rappel quotidien", color = Ink, fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline)
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayPanel(
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    total: Int,
    next: Lesson,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
) {
    Column(
        Modifier.padding(top = 20.dp).fillMaxWidth().clip(PanelShape).background(Mist).padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("AUJOURD'HUI", style = Kicker)
                when {
                    dueWords.isNotEmpty() -> {
                        Text("${dueWords.size}", style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.offset(x = (-2).dp))
                        Text(if (dueWords.size > 1) "mots à revoir" else "mot à revoir",
                            fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    knownCount == 0 -> {
                        Text("Tes premiers mots t'attendent", style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 6.dp))
                    }
                    else -> {
                        Text("Tout est révisé", style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 6.dp))
                        if (dueTomorrow > 0) {
                            Text("Demain : $dueTomorrow mot${if (dueTomorrow > 1) "s" else ""} à revoir",
                                color = InkSoft, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
            // Anneau : mots découverts sur le total
            Box(contentAlignment = Alignment.Center) {
                ProgressRing(knownCount.toFloat() / total, 96.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$knownCount", fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text("sur $total", fontSize = 11.sp, color = InkSoft)
                }
            }
        }

        if (dueWords.isNotEmpty()) {
            Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                val shown = dueWords.take(6)
                shown.forEachIndexed { i, w ->
                    CharGrid(w.hanzi.take(1), 40.dp, Modifier.offset(x = -(1.5.dp * i)))
                }
                if (dueWords.size > shown.size) {
                    Text("+${dueWords.size - shown.size}", color = InkSoft, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        if (knownCount > 0) {
            Text("$knownCount découverts · $masteredCount bien retenus",
                style = MaterialTheme.typography.bodySmall, color = InkSoft,
                modifier = Modifier.padding(top = 14.dp))
        }

        Spacer(Modifier.height(16.dp))
        if (dueWords.isNotEmpty()) {
            InkButton("Réviser maintenant", onReview, Modifier.fillMaxWidth())
        } else {
            InkButton(
                if (knownCount == 0) "Commencer la leçon 1" else "Continuer : ${next.short}",
                { onOpen(next) }, Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ContinueCard(lesson: Lesson, known: Int, onOpen: (Lesson) -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.padding(top = 12.dp).fillMaxWidth()
            .pressScale(interaction)
            .clip(TileShape).background(Paper).border(1.dp, Rule, TileShape)
            .clickable(interactionSource = interaction, indication = null) { onOpen(lesson) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharGrid(lesson.symbol, 52.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text("CONTINUER · LEÇON ${lesson.id}", style = Kicker)
            Text(lesson.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
            ProgressBar(known.toFloat() / lesson.words.size, height = 6.dp)
        }
        Spacer(Modifier.width(12.dp))
        Text("›", fontSize = 28.sp, color = InkSoft)
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
    Column {
        Row(
            Modifier.fillMaxWidth()
                .clickable(interactionSource = interaction, indication = null) { onOpen(lesson) }
                .pressScale(interaction)
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(lesson.id.toString().padStart(2, '0'), color = InkSoft, fontSize = 13.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
            CharGrid(lesson.symbol, 48.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(lesson.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
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
                            (if (done) "● " else "○ ") + m.label,
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
        Box(Modifier.fillMaxWidth().height(1.dp).background(Rule))
    }
}
