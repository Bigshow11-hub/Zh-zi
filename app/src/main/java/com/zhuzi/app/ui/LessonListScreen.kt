package com.zhuzi.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.hsk1Lessons

@Composable
fun LessonListScreen(
    bestScore: (Int, QuizMode) -> Int,
    onOpen: (Lesson) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Zhúzi 竹子", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text("HSK 1 · ${hsk1Lessons.sumOf { it.words.size }} mots en ${hsk1Lessons.size} leçons",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.padding(4.dp))
        }
        items(hsk1Lessons, key = { it.id }) { lesson ->
            Card(Modifier.fillMaxWidth().clickable { onOpen(lesson) }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(lesson.emoji, fontSize = 32.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Leçon ${lesson.id} · ${lesson.title}",
                            style = MaterialTheme.typography.titleMedium)
                        Text("${lesson.words.size} mots", style = MaterialTheme.typography.bodySmall)
                        val scores = QuizMode.entries.joinToString(" · ") { m ->
                            val best = bestScore(lesson.id, m)
                            m.label + " " + when {
                                best == 100 -> "⭐"
                                best >= 0 -> "$best %"
                                else -> "–"
                            }
                        }
                        Text(scores, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
