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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Word

@Composable
fun LessonScreen(
    lesson: Lesson,
    speaker: Speaker,
    onBack: () -> Unit,
    onQuizDone: (Int) -> Unit,
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
            Flashcards(lesson.words, speaker)
        } else {
            Quiz(lesson, speaker, onQuizDone)
        }
    }
}

/** Cartes : on touche la carte pour voir le pinyin et la traduction. */
@Composable
private fun Flashcards(words: List<Word>, speaker: Speaker) {
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
        Card(Modifier.fillMaxWidth().height(280.dp).clickable { revealed = !revealed }) {
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

/** Quiz : on voit le caractère, on choisit la bonne traduction parmi 4. */
@Composable
private fun Quiz(lesson: Lesson, speaker: Speaker, onDone: (Int) -> Unit) {
    var round by remember { mutableIntStateOf(0) }
    val questions = remember(round) { buildQuiz(lesson.words) }
    var index by remember(round) { mutableIntStateOf(0) }
    var score by remember(round) { mutableIntStateOf(0) }
    var picked by remember(round) { mutableStateOf<Word?>(null) }
    var finished by remember(round) { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
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
        Text("Question ${index + 1} / ${questions.size} · Score $score",
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Text(q.word.hanzi, fontSize = 72.sp, modifier = Modifier.clickable { speaker.speak(q.word.hanzi) })
        Text(if (picked != null) q.word.pinyin else "Que veut dire ce mot ?",
            fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(24.dp))

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
                        speaker.speak(q.word.hanzi)
                    }
                },
                colors = colors,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            ) { Text(choice.french, fontSize = 18.sp) }
        }

        Spacer(Modifier.height(16.dp))
        if (picked != null) {
            Button(onClick = {
                if (index + 1 < questions.size) {
                    index++; picked = null
                } else {
                    finished = true
                    onDone(score * 100 / questions.size)
                }
            }) { Text(if (index + 1 < questions.size) "Suivant" else "Voir le résultat") }
        }
    }
}
