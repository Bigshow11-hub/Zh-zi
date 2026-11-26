package com.zhuzi.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Tones
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.PinyinFont
import com.zhuzi.app.ui.theme.Right
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.TileShape
import com.zhuzi.app.ui.theme.Wrong
import com.zhuzi.app.ui.theme.toneColor

/**
 * Quiz des tons : on entend le mot, on voit ses caractères et son pinyin sans accents,
 * puis on choisit le ton de chaque syllabe, une par une.
 */
@Composable
fun ToneQuiz(
    lesson: Lesson,
    speak: (String) -> Unit,
    onDone: (QuizMode, Int) -> Unit,
    onAnswer: (Word, Boolean) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
    header: @Composable () -> Unit,
) {
    var round by remember { mutableIntStateOf(0) }
    val words = remember(round) { lesson.words.shuffled() }
    var index by remember(round) { mutableIntStateOf(0) }
    var score by remember(round) { mutableIntStateOf(0) }
    var finished by remember(round) { mutableStateOf(false) }
    val answers = remember(round, index) { mutableStateListOf<Int>() }
    val haptic = LocalHapticFeedback.current
    val bottom = contentPadding.calculateBottomPadding()

    val word = words[index]
    val syllables = remember(word) { Tones.syllables(word.pinyin) }
    val answered = answers.size >= syllables.size
    val ok = answered && syllables.indices.all { answers[it] == syllables[it].second }

    if (!finished) LaunchedEffect(round, index) { speak(word.hanzi) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = bottom + 220.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            header()
            Spacer(Modifier.height(18.dp))

            if (finished) {
                val percent = score * 100 / words.size
                Spacer(Modifier.height(32.dp))
                SealStamp(if (percent == 100) "好" else "声", 120.dp)
                Spacer(Modifier.height(28.dp))
                Text(
                    if (percent == 100) tr("Oreille parfaite !", "Perfect ear!", "Идеальный слух!")
                    else tr("Quiz des tons terminé", "Tone quiz complete", "Тест на тоны завершён"),
                    style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,
                )
                Text("$score / ${words.size} " + tr("mots justes", "words right", "слов верно"), fontSize = 17.sp,
                    color = InkSoft, modifier = Modifier.padding(top = 4.dp))
                Text("$percent %", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.height(28.dp))
                InkButton(tr("Recommencer", "Try again", "Ещё раз"), { round++ }, Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                InkOutlinedButton(tr("Retour aux leçons", "Back to lessons", "К урокам"), onBack, Modifier.fillMaxWidth())
                return@Column
            }

            StepProgress(index + (if (answered) 1 else 0), words.size)
            Spacer(Modifier.height(18.dp))

            // Le mot : caractères sans couleur de ton (sinon ce serait la réponse), syllabes à deviner
            Column(
                Modifier.fillMaxWidth().softCard().padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WordGrids(
                    word.hanzi, maxSize = 104.dp, diagonals = true,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() }, indication = null,
                    ) { speak(word.hanzi) },
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    syllables.forEachIndexed { i, (syl, tone) ->
                        SyllableChip(
                            text = if (i < answers.size) syl else Tones.plain(syl),
                            tone = tone,
                            state = when {
                                i < answers.size -> if (answers[i] == tone) 1 else 2
                                i == answers.size -> 0
                                else -> 3
                            },
                        )
                    }
                }
                Text(word.meaning(currentLang()), color = InkSoft, fontSize = 15.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp))
                InkOutlinedButton(tr("Réécouter", "Listen again", "Ещё раз"), { speak(word.hanzi) },
                    Modifier.padding(top = 12.dp))
            }

            Spacer(Modifier.height(18.dp))
            val current = syllables.getOrNull(answers.size)
            Text(
                if (current != null) tr("Quel ton pour « ${Tones.plain(current.first)} » ?",
                    "Which tone is “${Tones.plain(current.first)}”?",
                    "Какой тон у «${Tones.plain(current.first)}»?")
                else tr("Réponse enregistrée", "Answer saved", "Ответ сохранён"),
                color = InkSoft, fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))

            val pick: (Int) -> Unit = pick@{ t ->
                val target = current ?: return@pick
                answers += t
                haptic.performHapticFeedback(
                    if (t == target.second) HapticFeedbackType.TextHandleMove else HapticFeedbackType.LongPress,
                )
                if (answers.size == syllables.size) {
                    val good = syllables.indices.all { answers[it] == syllables[it].second }
                    if (good) score++
                    onAnswer(word, good)
                    speak(word.hanzi)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (t in 1..4) {
                    ToneButton(t, enabled = !answered, onClick = { pick(t) }, modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
            ToneButton(5, enabled = !answered, onClick = { pick(5) }, modifier = Modifier.fillMaxWidth())
            Text(
                tr("On répond avec le ton du dictionnaire : 你好 s'écrit nǐ hǎo, même si on dit ní hǎo.",
                    "Answer with the dictionary tone: 你好 is written nǐ hǎo, even though it's said ní hǎo.",
                    "Отвечай словарным тоном: 你好 пишется nǐ hǎo, хотя звучит ní hǎo."),
                fontSize = 12.sp, color = InkSoft, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp),
            )
        }

        // Panneau de réponse qui glisse depuis le bas
        AnimatedVisibility(
            visible = answered && !finished,
            enter = slideInVertically(spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)) { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            FeedbackPanel(ok, word, last = index + 1 >= words.size, seed = index) {
                if (index + 1 < words.size) {
                    index++
                } else {
                    finished = true
                    onDone(QuizMode.TONES, score * 100 / words.size)
                }
            }
        }
    }
}

/** Une syllabe du mot. state : 0 à deviner, 1 juste, 2 faux, 3 plus tard. */
@Composable
private fun SyllableChip(text: String, tone: Int, state: Int) {
    val shake = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(state) {
        if (state == 2) {
            for (x in listOf(8f, -6f, 4f, -2f, 0f)) shake.animateTo(x, spring(stiffness = Spring.StiffnessHigh))
        } else if (state == 1) {
            pop.animateTo(1.15f, spring(stiffness = Spring.StiffnessHigh))
            pop.animateTo(1f, spring(dampingRatio = 0.45f))
        }
    }
    val color = when (state) {
        1, 2 -> toneColor(tone)
        0 -> Ink
        else -> InkSoft
    }
    val border = when (state) {
        0 -> Accent
        1 -> Right
        2 -> Wrong
        else -> Rule
    }
    Box(
        Modifier
            .offset { IntOffset(shake.value.dp.roundToPx(), 0) }
            .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
            .clip(TileShape)
            .background(if (state == 0) Accent.copy(alpha = 0.10f) else Mist)
            .border(if (state == 3) 1.dp else 2.dp, border, TileShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontFamily = PinyinFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = color)
    }
}

/** Bouton d'un ton : sa courbe de hauteur (comme les lettres de ton), le signe et le numéro. */
@Composable
private fun ToneButton(tone: Int, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val color = toneColor(tone)
    val neutral = tone == 5
    Box(
        modifier
            .pressScale(interaction)
            .graphicsLayer { alpha = if (enabled) 1f else 0.5f }
            .clip(TileShape)
            .background(color.copy(alpha = 0.10f))
            .border(1.5.dp, color.copy(alpha = 0.45f), TileShape)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .padding(vertical = if (neutral) 10.dp else 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (neutral) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ToneContour(5, color, Modifier.size(28.dp, 20.dp))
                Text("  a · " + tr("ton neutre", "neutral tone", "нейтральный тон"),
                    color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = PinyinFont)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ToneContour(tone, color, Modifier.size(44.dp, 30.dp))
                Text(listOf("ā", "á", "ǎ", "à")[tone - 1], fontFamily = PinyinFont, fontWeight = FontWeight.Bold,
                    fontSize = 22.sp, color = color, modifier = Modifier.padding(top = 4.dp))
                Text(tr("${tone}${if (tone == 1) "er" else "e"}", "${tone}${listOf("st", "nd", "rd", "th")[tone - 1]}", "$tone-й"),
                    fontSize = 11.sp, color = InkSoft, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** La courbe de hauteur du ton, sur 5 niveaux (1 grave, 5 aigu) : 55, 35, 214, 51 ; le neutre est un point. */
@Composable
private fun ToneContour(tone: Int, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val pad = 3.dp.toPx()
        fun y(level: Float) = pad + (5f - level) / 4f * (h - 2 * pad)
        val pts = when (tone) {
            1 -> listOf(0f to 5f, 1f to 5f)
            2 -> listOf(0f to 3f, 1f to 5f)
            3 -> listOf(0f to 2f, 0.45f to 1f, 1f to 4f)
            4 -> listOf(0f to 5f, 1f to 1f)
            else -> emptyList()
        }
        val stroke = 3.5.dp.toPx()
        if (pts.isEmpty()) {
            drawCircle(color, radius = stroke, center = Offset(w / 2, y(3f)))
            return@Canvas
        }
        val path = Path()
        pts.forEachIndexed { i, (x, l) ->
            val px = pad + x * (w - 2 * pad)
            if (i == 0) path.moveTo(px, y(l)) else path.lineTo(px, y(l))
        }
        drawPath(path, color, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
