package com.zhuzi.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Word
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.reminder.Reminders
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Rule

@Composable
fun LessonListScreen(
    bestScore: (Int, QuizMode) -> Int,
    onOpen: (Lesson) -> Unit,
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    onReview: () -> Unit,
    streakDays: Int,
    reminderOn: Boolean,
    onEnableReminder: () -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
    ) {
        // En-tête : nom de l'appli et série de jours
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Zhúzi", style = MaterialTheme.typography.titleLarge)
                Text(" 竹子", fontFamily = Kai, fontSize = 24.sp, color = Ink)
                Spacer(Modifier.weight(1f))
                if (streakDays > 0) {
                    Text("$streakDays", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(if (streakDays > 1) " jours d'affilée" else " jour", color = InkSoft,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Révision du jour
        item {
            Column(Modifier.padding(top = 24.dp)) {
                when {
                    dueWords.isNotEmpty() -> {
                        Text("RÉVISION DU JOUR · ${dueWords.size} MOT${if (dueWords.size > 1) "S" else ""}",
                            style = Kicker)
                        Row(Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            val shown = dueWords.take(5)
                            shown.forEachIndexed { i, w ->
                                // Le premier caractère de chaque mot à revoir, cases jointives
                                CharGrid(w.hanzi.take(1), 48.dp, Modifier.offset(x = -(1.5.dp * i)))
                            }
                            if (dueWords.size > shown.size) {
                                Text("+${dueWords.size - shown.size}", color = InkSoft,
                                    modifier = Modifier.padding(start = 10.dp))
                            }
                        }
                        InkButton("Commencer la révision", onReview, Modifier.fillMaxWidth())
                    }
                    knownCount == 0 -> {
                        Text("RÉVISION DU JOUR", style = Kicker)
                        Text(
                            "Ouvre une leçon : chaque mot découvert reviendra ici au bon moment, " +
                                "pour que tu ne l'oublies pas.",
                            color = InkSoft, modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    else -> {
                        Text("RÉVISION DU JOUR", style = Kicker)
                        Text("Rien à revoir aujourd'hui.", fontWeight = FontWeight.Bold,
                            fontSize = 17.sp, modifier = Modifier.padding(top = 8.dp))
                        if (dueTomorrow > 0) {
                            Text("Demain : $dueTomorrow mot${if (dueTomorrow > 1) "s" else ""}.", color = InkSoft)
                        }
                    }
                }
                if (knownCount > 0) {
                    Text("$knownCount mots découverts · $masteredCount bien retenus",
                        style = MaterialTheme.typography.bodySmall, color = InkSoft,
                        modifier = Modifier.padding(top = 10.dp))
                }
                if (reminderOn) {
                    Text("Rappel chaque soir vers ${Reminders.HOUR} h si tu n'as pas encore étudié.",
                        style = MaterialTheme.typography.bodySmall, color = InkSoft,
                        modifier = Modifier.padding(top = 2.dp))
                } else {
                    TextButton(onClick = onEnableReminder, contentPadding = PaddingValues(0.dp)) {
                        Text("Activer le rappel quotidien", color = Ink,
                            textDecoration = TextDecoration.Underline)
                    }
                }
            }
        }

        // Titre de la liste des leçons
        item {
            Row(Modifier.padding(top = 28.dp, bottom = 10.dp), verticalAlignment = Alignment.Bottom) {
                Text("HSK 1", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Text("${hsk1Lessons.size} leçons · ${hsk1Lessons.sumOf { it.words.size }} mots",
                    style = MaterialTheme.typography.bodySmall, color = InkSoft)
            }
        }

        // Leçons sur deux colonnes
        items(hsk1Lessons.chunked(2), key = { row -> row.first().id }) { row ->
            Row(Modifier.padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { lesson -> LessonTile(lesson, bestScore, onOpen, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LessonTile(
    lesson: Lesson,
    bestScore: (Int, QuizMode) -> Int,
    onOpen: (Lesson) -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier
            .border(1.dp, Rule)
            .clickable { onOpen(lesson) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharGrid(lesson.symbol, 46.dp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(lesson.short, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text("${lesson.words.size} mots", fontSize = 12.sp, color = InkSoft)
            // Un carré par sorte de quiz : plein quand le score atteint 80 %
            Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                QuizMode.entries.forEach { m ->
                    val done = bestScore(lesson.id, m) >= 80
                    Box(
                        Modifier.size(8.dp).border(1.dp, Ink)
                            .background(if (done) Ink else Color.Transparent),
                    )
                }
            }
        }
    }
}
