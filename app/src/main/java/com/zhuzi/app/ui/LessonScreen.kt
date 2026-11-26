package com.zhuzi.app.ui

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Right
import com.zhuzi.app.ui.theme.RightBg
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Wrong
import com.zhuzi.app.ui.theme.WrongBg

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
        TopBar("← Leçons", "Leçon ${lesson.id}", onBack)
        Text(lesson.title, style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp))
        // Onglets soulignés
        Row(Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp)) {
            UnderlineTab("Cartes", tab == 0) { tab = 0 }
            Spacer(Modifier.width(24.dp))
            UnderlineTab("Quiz", tab == 1) { tab = 1 }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Rule))
        if (tab == 0) {
            Flashcards(lesson.words, speaker, onSeen)
        } else {
            Quiz(lesson, speaker, onQuizDone, onQuizAnswer)
        }
    }
}

/** Barre du haut : retour à gauche, repère à droite. */
@Composable
fun TopBar(back: String, right: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text(back, color = Ink) }
        Spacer(Modifier.weight(1f))
        Text(right, color = InkSoft, style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 12.dp))
    }
}

@Composable
private fun UnderlineTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = if (selected) Ink else InkSoft,
            modifier = Modifier.padding(vertical = 10.dp))
        Box(Modifier.width(if (selected) 40.dp else 0.dp).height(2.dp).background(Ink))
    }
}

/** Lien souligné « Écouter ». */
@Composable
fun ListenLink(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text("Écouter", color = Ink, fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline)
    }
}

/** Cartes : on touche la case pour voir le pinyin et la traduction. */
@Composable
private fun Flashcards(words: List<Word>, speaker: Speaker, onSeen: (Word) -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var revealed by rememberSaveable { mutableStateOf(false) }
    val word = words[index]

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Carte ${index + 1} / ${words.size}", style = Kicker,
            modifier = Modifier.align(Alignment.End))
        Spacer(Modifier.height(20.dp))
        WordGrids(
            word.hanzi, maxSize = 240.dp, diagonals = true,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                revealed = !revealed
                if (revealed) { onSeen(word); speaker.speak(word.hanzi) }
            },
        )
        Spacer(Modifier.height(20.dp))
        Column(Modifier.heightIn(min = 72.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (revealed) {
                Text(word.pinyin, fontSize = 28.sp)
                Text(word.french, fontSize = 18.sp, color = InkSoft, textAlign = TextAlign.Center)
            } else {
                Text("Touche la case pour voir la réponse", color = InkSoft)
            }
        }
        ListenLink { speaker.speak(word.hanzi) }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InkOutlinedButton("Précédent", {
                index = (index - 1 + words.size) % words.size; revealed = false
            }, Modifier.weight(1f))
            InkButton("Suivant", {
                index = (index + 1) % words.size; revealed = false
            }, Modifier.weight(1f))
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

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Choix du mode : trois segments, le choisi est à l'encre
        Row(Modifier.fillMaxWidth().border(1.5.dp, Ink, SquareShape)) {
            QuizMode.entries.forEach { m ->
                val on = mode == m
                Box(
                    Modifier.weight(1f).background(if (on) Ink else Paper)
                        .clickable { mode = m }.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(m.label, color = if (on) Paper else Ink, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(Modifier.height(20.dp))

        if (finished) {
            val percent = score * 100 / questions.size
            Spacer(Modifier.height(40.dp))
            Text(if (percent == 100) "Parfait !" else "Terminé", style = MaterialTheme.typography.headlineMedium)
            Text("$score / ${questions.size} bonnes réponses · $percent %", fontSize = 18.sp, color = InkSoft)
            Spacer(Modifier.height(24.dp))
            InkButton("Recommencer", { round++ }, Modifier.fillMaxWidth())
            return@Column
        }

        val q = questions[index]
        if (mode == QuizMode.LISTENING) {
            LaunchedEffect(q) { speaker.speak(q.word.hanzi) }
        }
        Text("QUESTION ${index + 1} / ${questions.size} · SCORE $score", style = Kicker)
        Spacer(Modifier.height(16.dp))

        when (mode) {
            QuizMode.MEANING -> WordGrids(
                q.word.hanzi, maxSize = 140.dp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { speaker.speak(q.word.hanzi) },
            )
            QuizMode.WRITING -> Text(q.word.french, fontSize = 26.sp, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 24.dp))
            QuizMode.LISTENING -> InkOutlinedButton("Réécouter", { speaker.speak(q.word.hanzi) },
                Modifier.padding(vertical = 24.dp))
        }
        Spacer(Modifier.height(12.dp))
        val hint = when {
            picked != null -> "${q.word.hanzi} · ${q.word.pinyin} · ${q.word.french}"
            mode == QuizMode.WRITING -> "Quel mot veut dire ça ?"
            mode == QuizMode.LISTENING -> "Qu'as-tu entendu ?"
            else -> "Que veut dire ce mot ?"
        }
        Text(hint, color = InkSoft, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))

        q.choices.forEach { choice ->
            val state = when {
                picked == null -> 0
                choice == q.word -> 1
                choice == picked -> 2
                else -> 0
            }
            Box(
                Modifier.fillMaxWidth().padding(vertical = 4.dp).heightIn(min = 56.dp)
                    .border(if (state == 0) 1.dp else 1.5.dp,
                        when (state) { 1 -> Right; 2 -> Wrong; else -> Rule }, SquareShape)
                    .background(when (state) { 1 -> RightBg; 2 -> WrongBg; else -> Paper }, SquareShape)
                    .clickable(enabled = picked == null) {
                        picked = choice
                        val ok = choice == q.word
                        if (ok) score++
                        onAnswer(q.word, ok)
                        if (mode != QuizMode.LISTENING) speaker.speak(q.word.hanzi)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (mode == QuizMode.WRITING) {
                    Text(choice.hanzi, fontFamily = Kai, fontSize = 30.sp, color = Ink)
                } else {
                    Text(choice.french, fontSize = 17.sp, color = Ink)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        if (picked != null) {
            val last = index + 1 >= questions.size
            InkButton(if (last) "Voir le résultat" else "Suivant", {
                if (!last) {
                    index++; picked = null
                } else {
                    finished = true
                    onDone(mode, score * 100 / questions.size)
                }
            }, Modifier.fillMaxWidth())
        }
    }
}
