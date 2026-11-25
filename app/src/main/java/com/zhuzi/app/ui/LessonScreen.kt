package com.zhuzi.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Word

@Composable
fun LessonScreen(
    lesson: Lesson,
    speaker: Speaker,
    onBack: () -> Unit,
    onQuizDone: (QuizMode, Int) -> Unit,
    onSeen: (Word) -> Unit,
    onQuizAnswer: (Word, Boolean) -> Unit,
    contentPadding: PaddingValues,
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().padding(contentPadding)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Leçons") }
            Text("${lesson.emoji} ${lesson.title}", style = MaterialTheme.typography.titleMedium)
        }
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Cartes") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Quiz") })
        }
        if (tab == 0) {
            Flashcards(lesson.words, speaker, onSeen)
        } else {
            Quiz(lesson, speaker, onQuizDone, onQuizAnswer)
        }
    }
}

/** Cartes : on touche la carte pour voir le pinyin et la traduction. */
@Composable
private fun Flashcards(words: List<Word>, speaker: Speaker, onSeen: (Word) -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var revealed by rememberSaveable { mutableStateOf(false) }
    val word = words[index]

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Carte ${index + 1} / ${words.size}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth().height(280.dp).clickable {
            revealed = !revealed
            if (revealed) onSeen(word)
        }) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(word.hanzi, fontSize = 72.sp)
                Spacer(Modifier.height(12.dp))
                if (revealed) {
                    Text(word.pinyin, fontSize = 28.sp, color = MaterialTheme.colorScheme.secondary)
                    Text(word.french, fontSize = 22.sp, textAlign = TextAlign.Center)
                } else {
                    Text("Touche pour voir la réponse", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { speaker.speak(word.hanzi) }) { Text("🔊 Écouter") }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = {
                index = (index - 1 + words.size) % words.size; revealed = false
            }) { Text("Précédent") }
            Button(onClick = {
                index = (index + 1) % words.size; revealed = false
            }) { Text("Suivant") }
        }
    }
}

private data class Question(val word: Word, val choices: List<Word>)

private fun buildQuiz(words: List<Word>): List<Question> =
    words.shuffled().map { word ->
        val wrong = words.filter { it != word }.shuffled().take(3)
        Question(word, (wrong + word).shuffled())
    }

/** Quiz en 3 modes : sens (caractère → français), écrit (français → caractère), écoute. */
@Composable
private fun Quiz(
    lesson: Lesson,
    speaker: Speaker,
    onDone: (QuizMode, Int) -> Unit,
    onAnswer: (Word, Boolean) -> Unit,
) {
    var mode by rememberSaveable { mutableStateOf(QuizMode.MEANING) }
    var round by remember { mutableIntStateOf(0) }
    val questions = remember(round, mode) { buildQuiz(lesson.words) }
    var index by remember(round, mode) { mutableIntStateOf(0) }
    var score by remember(round, mode) { mutableIntStateOf(0) }
    var picked by remember(round, mode) { mutableStateOf<Word?>(null) }
    var finished by remember(round, mode) { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuizMode.entries.forEach { m ->
                FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m.label) })
            }
        }
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (finished) {
                val percent = score * 100 / questions.size
                Text(if (percent == 100) "⭐ Parfait !" else "Terminé !",
                    style = MaterialTheme.typography.headlineMedium)
                Text("$score / ${questions.size} bonnes réponses ($percent %)", fontSize = 20.sp)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { round++ }) { Text("Recommencer") }
                return@Column
            }

            val q = questions[index]
            if (mode == QuizMode.LISTENING) {
                LaunchedEffect(q) { speaker.speak(q.word.hanzi) }
            }
            Text("Question ${index + 1} / ${questions.size} · Score $score",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            when (mode) {
                QuizMode.MEANING -> Text(q.word.hanzi, fontSize = 64.sp,
                    modifier = Modifier.clickable { speaker.speak(q.word.hanzi) })
                QuizMode.WRITING -> Text(q.word.french, fontSize = 30.sp, textAlign = TextAlign.Center)
                QuizMode.LISTENING -> Button(onClick = { speaker.speak(q.word.hanzi) }) {
                    Text("🔊 Réécouter", fontSize = 22.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            val hint = when {
                picked != null -> "${q.word.hanzi} · ${q.word.pinyin} · ${q.word.french}"
                mode == QuizMode.WRITING -> "Quel caractère veut dire ça ?"
                mode == QuizMode.LISTENING -> "Qu'as-tu entendu ?"
                else -> "Que veut dire ce mot ?"
            }
            Text(hint, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))

            q.choices.forEach { choice ->
                val colors = when {
                    picked == null -> ButtonDefaults.outlinedButtonColors()
                    choice == q.word -> ButtonDefaults.outlinedButtonColors(containerColor = Color(0x553E8E41))
                    choice == picked -> ButtonDefaults.outlinedButtonColors(containerColor = Color(0x55C0392B))
                    else -> ButtonDefaults.outlinedButtonColors()
                }
                OutlinedButton(
                    onClick = {
                        if (picked == null) {
                            picked = choice
                            if (choice == q.word) score++
                            onAnswer(q.word, choice == q.word)
                            if (mode != QuizMode.LISTENING) speaker.speak(q.word.hanzi)
                        }
                    },
                    colors = colors,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    if (mode == QuizMode.WRITING) Text(choice.hanzi, fontSize = 28.sp)
                    else Text(choice.french, fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            if (picked != null) {
                Button(onClick = {
                    if (index + 1 < questions.size) {
                        index++; picked = null
                    } else {
                        finished = true
                        onDone(mode, score * 100 / questions.size)
                    }
                }) { Text(if (index + 1 < questions.size) "Suivant" else "Voir le résultat") }
            }
        }
    }
}
