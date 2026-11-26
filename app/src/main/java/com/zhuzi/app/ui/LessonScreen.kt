package com.zhuzi.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.PanelShape
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Right
import com.zhuzi.app.ui.theme.RightBg
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.TileShape
import com.zhuzi.app.ui.theme.Wrong
import com.zhuzi.app.ui.theme.WrongBg
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(
    lesson: Lesson,
    speak: (String) -> Unit,
    onBack: () -> Unit,
    onQuizDone: (QuizMode, Int) -> Unit,
    onSeen: (Word) -> Unit,
    onQuizAnswer: (Word, Boolean) -> Unit,
    contentPadding: PaddingValues,
    startTab: Int = 0,
    startQuizMode: QuizMode = QuizMode.MEANING,
) {
    var tab by rememberSaveable { mutableIntStateOf(startTab) }
    Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
        TopBar(tr("Leçons", "Lessons", "Уроки"), tr("Leçon ", "Lesson ", "Урок ") + lesson.id, onBack)
        Text(lesson.title(currentLang()), style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp))
        // L'accroche de la leçon
        com.zhuzi.app.data.lessonIntros[lesson.id]?.let { intro ->
            Text(intro.get(currentLang()), style = MaterialTheme.typography.bodyMedium, color = InkSoft,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp))
        }
        // Choix Cartes / Quiz en pilules
        Segmented(listOf(tr("Cartes", "Cards", "Карточки"), tr("Écrire", "Write", "Писать"),
            tr("Grammaire", "Grammar", "Грамматика"), tr("Quiz", "Quiz", "Тест")),
            tab, { tab = it },
            Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp))
        Box(Modifier.weight(1f)) {
            when (tab) {
                0 -> Flashcards(lesson.words, speak, onSeen, contentPadding)
                1 -> WritingTab(lesson.words, speak, onSeen, contentPadding)
                2 -> GrammarTab(lesson, speak, contentPadding)
                else -> Quiz(lesson, speak, onQuizDone, onQuizAnswer, onBack, contentPadding, startQuizMode)
            }
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
        TextButton(onClick = onBack) { Text("‹  $back", color = Ink, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.weight(1f))
        Text(right, color = InkSoft, style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 12.dp))
    }
}

/** Sélecteur en pilules : le choix actif est à l'encre. */
@Composable
fun Segmented(labels: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().clip(PillShape).background(Mist).padding(4.dp)) {
        labels.forEachIndexed { i, label ->
            val on = i == selected
            Box(
                Modifier.weight(1f).clip(PillShape).background(if (on) com.zhuzi.app.ui.theme.Accent else Mist)
                    .clickable { onSelect(i) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, color = if (on) androidx.compose.ui.graphics.Color.White else Ink, fontWeight = FontWeight.Bold,
                    fontSize = if (labels.size >= 4) 13.sp else 14.sp, maxLines = 1, softWrap = false,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
    }
}

/** Lien souligné « Écouter ». */
@Composable
fun ListenLink(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(tr("Écouter", "Listen", "Слушать"), color = Ink, fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline)
    }
}

/** Barre d'actions fixée en bas de l'écran, à portée du pouce. */
@Composable
fun BottomActions(contentPadding: PaddingValues, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Paper)
            .padding(start = 20.dp, end = 20.dp, top = 12.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

/** Repère de progression : barre + compteur. */
@Composable
fun StepProgress(done: Int, total: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ProgressBar(done.toFloat() / total, Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Text("$done / $total", fontSize = 13.sp, color = InkSoft, fontWeight = FontWeight.Bold)
    }
}

/**
 * Carte qui se retourne en 3D : le recto montre les caractères, le verso la réponse.
 */
@Composable
fun FlipCard(
    word: Word,
    flipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
    frontHint: String = tr("Touche la carte pour la retourner", "Tap the card to flip it", "Нажми на карточку, чтобы перевернуть"),
) {
    val rotation by animateFloatAsState(
        if (flipped) 180f else 0f,
        spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "flip",
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(360.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .softCard()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null,
                onClick = onFlip)
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (rotation <= 90f) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WordGrids(word.hanzi, maxSize = 200.dp, diagonals = true)
                Spacer(Modifier.height(24.dp))
                Text(frontHint, color = InkSoft, fontSize = 14.sp)
            }
        } else {
            Column(
                Modifier.graphicsLayer { rotationY = 180f },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WordGrids(word.hanzi, maxSize = 110.dp, pinyin = word.pinyin)
                Spacer(Modifier.height(20.dp))
                PinyinText(word.pinyin, 32.sp)
                Text(word.meaning(currentLang()), fontSize = 19.sp, color = InkSoft, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp))
                // Le sens de chaque caractère, en petites pastilles
                val parts = word.hanzi.mapNotNull { com.zhuzi.app.data.charInfos[it] }
                if (parts.size > 1) {
                    Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        parts.forEach { c ->
                            Row(
                                Modifier.clip(PillShape).background(Mist).padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(c.char.toString(), fontFamily = Kai, fontSize = 17.sp, color = Ink)
                                Text(" " + c.meaning.get(currentLang()).substringBefore(","), fontSize = 12.sp, color = InkSoft,
                                    maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Écrire : on trace chaque caractère du mot, trait par trait, au doigt. */
@Composable
private fun WritingTab(
    words: List<Word>,
    speak: (String) -> Unit,
    onSeen: (Word) -> Unit,
    contentPadding: PaddingValues,
) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val word = words[index]
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StepProgress(index + 1, words.size)
            Spacer(Modifier.height(16.dp))
            // Un tableau neuf pour chaque mot
            key(index) {
                WritingPractice(word, onWordDone = { onSeen(word); speak(word.hanzi) },
                    modifier = Modifier.softCard().padding(vertical = 18.dp))
            }
            Text(
                tr("Trace chaque trait au doigt, dans l'ordre. Le point rouge de l'indice montre où commencer.",
                    "Trace each stroke with your finger, in order. The hint's red dot shows where to start.",
                    "Проведи каждую черту пальцем по порядку. Красная точка подсказки показывает начало."),
                fontSize = 13.sp, color = InkSoft, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp, bottom = 12.dp),
            )
        }
        BottomActions(contentPadding) {
            InkOutlinedButton(tr("Précédent", "Previous", "Назад"), {
                index = (index - 1 + words.size) % words.size
            }, Modifier.weight(1f))
            InkButton(tr("Suivant", "Next", "Далее"), {
                index = (index + 1) % words.size
            }, Modifier.weight(1f))
        }
    }
}

/** Grammaire de la leçon : la note de culture, puis chaque règle en entier. */
@Composable
private fun GrammarTab(lesson: Lesson, speak: (String) -> Unit, contentPadding: PaddingValues) {
    val points = com.zhuzi.app.data.grammarPoints.filter { it.lesson == lesson.id }
    val seen = remember { mutableSetOf<Any>() }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        com.zhuzi.app.data.lessonCulture[lesson.id]?.let { CultureCard(it, Modifier.staggerIn("culture", 0, seen)) }
        points.forEachIndexed { i, p ->
            GrammarDetail(
                p, speak,
                Modifier.padding(top = 14.dp).staggerIn("g${p.id}", i + 1, seen)
                    .softCard().padding(18.dp),
            )
        }
    }
}

/** Cartes : on retourne la carte pour voir le pinyin et la traduction. */
@Composable
private fun Flashcards(
    words: List<Word>,
    speak: (String) -> Unit,
    onSeen: (Word) -> Unit,
    contentPadding: PaddingValues,
) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var revealed by rememberSaveable { mutableStateOf(false) }
    var strokesFor by remember { mutableStateOf<Word?>(null) }
    val word = words[index]

    StrokeSheet(strokesFor) { strokesFor = null }
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StepProgress(index + 1, words.size)
            Spacer(Modifier.height(18.dp))
            // Pile de cartes : on glisse la carte du dessus à gauche (suivante) ou à droite (précédente)
            val drag = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            val go: (Int) -> Unit = { d -> index = (index + d + words.size) % words.size; revealed = false }
            Box(Modifier.fillMaxWidth().height(380.dp), contentAlignment = Alignment.TopCenter) {
                val lift = (kotlin.math.abs(drag.value) / 600f).coerceIn(0f, 1f)
                Box(Modifier.padding(top = 20.dp).fillMaxWidth(0.84f + 0.04f * lift).height(360.dp).softCard()
                    .graphicsLayer { alpha = 0.55f })
                Box(Modifier.padding(top = 10.dp).fillMaxWidth(0.92f + 0.04f * lift).height(360.dp).softCard()
                    .graphicsLayer { alpha = 0.8f })
                key(index) {
                    val enter = remember { Animatable(0.94f) }
                    LaunchedEffect(Unit) { enter.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow)) }
                    FlipCard(
                        word, revealed,
                        {
                            revealed = !revealed
                            if (revealed) { onSeen(word); speak(word.hanzi) }
                        },
                        Modifier
                            .graphicsLayer {
                                translationX = drag.value
                                rotationZ = drag.value / 45f
                                scaleX = enter.value; scaleY = enter.value
                            }
                            .pointerInput(index) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        val v = drag.value
                                        scope.launch {
                                            if (kotlin.math.abs(v) > size.width * 0.28f) {
                                                drag.animateTo(if (v < 0) -size.width * 1.4f else size.width * 1.4f,
                                                    androidx.compose.animation.core.tween(220))
                                                go(if (v < 0) 1 else -1)
                                                drag.snapTo(0f)
                                            } else {
                                                drag.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
                                            }
                                        }
                                    },
                                    onDragCancel = { scope.launch { drag.animateTo(0f, spring()) } },
                                ) { change, amount ->
                                    change.consume()
                                    scope.launch { drag.snapTo(drag.value + amount) }
                                }
                            },
                    )
                }
            }
            Text(tr("Glisse la carte pour passer à la suivante", "Swipe the card to move on", "Смахни карточку, чтобы перейти дальше"),
                fontSize = 12.sp, color = InkSoft, modifier = Modifier.padding(top = 4.dp))
            Row {
                ListenLink { speak(word.hanzi) }
                StrokeLink { strokesFor = word }
            }
            // La phrase d'exemple apparaît quand la carte est retournée
            androidx.compose.animation.AnimatedVisibility(
                revealed,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically(),
            ) {
                ExampleBlock(word, speak, Modifier.padding(bottom = 12.dp))
            }
        }
        // Actions en bas, sous le pouce
        BottomActions(contentPadding) {
            InkOutlinedButton(tr("Précédent", "Previous", "Назад"), {
                index = (index - 1 + words.size) % words.size; revealed = false
            }, Modifier.weight(1f))
            InkButton(tr("Suivant", "Next", "Далее"), {
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
    speak: (String) -> Unit,
    onDone: (QuizMode, Int) -> Unit,
    onAnswer: (Word, Boolean) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
    startMode: QuizMode = QuizMode.MEANING,
) {
    val lang = currentLang()
    var mode by rememberSaveable { mutableStateOf(startMode) }
    val modeSelector: @Composable () -> Unit = {
        Segmented(QuizMode.entries.map { it.label(lang) }, mode.ordinal,
            { mode = QuizMode.entries[it] })
    }
    // Le quiz des tons a son propre déroulé
    if (mode == QuizMode.TONES) {
        ToneQuiz(lesson, speak, onDone, onAnswer, onBack, contentPadding, modeSelector)
        return
    }
    var round by remember { mutableIntStateOf(0) }
    val questions = remember(round, mode) { buildQuiz(lesson.words) }
    var index by remember(round, mode) { mutableIntStateOf(0) }
    var score by remember(round, mode) { mutableIntStateOf(0) }
    var picked by remember(round, mode) { mutableStateOf<Word?>(null) }
    var finished by remember(round, mode) { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val bottom = contentPadding.calculateBottomPadding()

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = bottom + 220.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            modeSelector()
            Spacer(Modifier.height(18.dp))

            if (finished) {
                val percent = score * 100 / questions.size
                Spacer(Modifier.height(32.dp))
                SealStamp(if (percent == 100) "好" else "记", 120.dp)
                Spacer(Modifier.height(28.dp))
                Text(if (percent == 100) tr("Parfait !", "Perfect!", "Отлично!") else tr("Quiz terminé", "Quiz complete", "Тест завершён"),
                    style = MaterialTheme.typography.headlineMedium)
                Text("$score / ${questions.size} " + tr("bonnes réponses", "correct answers", "правильных ответов"), fontSize = 17.sp, color = InkSoft,
                    modifier = Modifier.padding(top = 4.dp))
                Text("$percent %", style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.height(28.dp))
                InkButton(tr("Recommencer", "Try again", "Ещё раз"), { round++ }, Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                InkOutlinedButton(tr("Retour aux leçons", "Back to lessons", "К урокам"), onBack, Modifier.fillMaxWidth())
                return@Column
            }

            val q = questions[index]
            if (mode == QuizMode.LISTENING) {
                LaunchedEffect(q) { speak(q.word.hanzi) }
            }
            StepProgress(index + (if (picked != null) 1 else 0), questions.size)
            Spacer(Modifier.height(22.dp))

            when (mode) {
                QuizMode.MEANING -> WordGrids(
                    q.word.hanzi, maxSize = 130.dp, diagonals = true,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() }, indication = null,
                    ) { speak(q.word.hanzi) },
                )
                QuizMode.WRITING -> Text(q.word.meaning(lang), style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 28.dp))
                QuizMode.LISTENING -> InkOutlinedButton(tr("Réécouter", "Listen again", "Ещё раз"), { speak(q.word.hanzi) },
                    Modifier.padding(vertical = 24.dp))
                QuizMode.TONES -> Unit // géré par ToneQuiz
            }
            Spacer(Modifier.height(14.dp))
            Text(
                when (mode) {
                    QuizMode.WRITING -> tr("Quel mot veut dire ça ?", "Which word means this?", "Какое слово это значит?")
                    QuizMode.LISTENING -> tr("Qu'as-tu entendu ?", "What did you hear?", "Что ты услышал?")
                    else -> tr("Que veut dire ce mot ?", "What does this word mean?", "Что значит это слово?")
                },
                color = InkSoft,
            )
            Spacer(Modifier.height(16.dp))

            q.choices.forEach { choice ->
                ChoiceTile(
                    choice = choice,
                    asHanzi = mode == QuizMode.WRITING,
                    state = when {
                        picked == null -> 0
                        choice == q.word -> 1
                        choice == picked -> 2
                        else -> 3
                    },
                    enabled = picked == null,
                ) {
                    picked = choice
                    val ok = choice == q.word
                    if (ok) score++
                    haptic.performHapticFeedback(
                        if (ok) HapticFeedbackType.TextHandleMove else HapticFeedbackType.LongPress,
                    )
                    onAnswer(q.word, ok)
                    if (mode != QuizMode.LISTENING) speak(q.word.hanzi)
                }
            }
        }

        // Panneau de réponse qui glisse depuis le bas
        val q = if (!finished) questions[index] else null
        AnimatedVisibility(
            visible = picked != null && q != null,
            enter = slideInVertically(spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)) { it } +
                fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            if (q != null) {
                val ok = picked == q.word
                FeedbackPanel(ok, q.word, last = index + 1 >= questions.size, seed = index) {
                    if (index + 1 < questions.size) {
                        index++; picked = null
                    } else {
                        finished = true; picked = null
                        onDone(mode, score * 100 / questions.size)
                    }
                }
            }
        }
    }
}

/** Une réponse possible. state : 0 neutre, 1 bonne, 2 choisie fausse, 3 autre (atténuée). */
@Composable
private fun ChoiceTile(choice: Word, asHanzi: Boolean, state: Int, enabled: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val shake = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(state) {
        if (state == 2) {
            // Petite secousse sur une mauvaise réponse
            for (x in listOf(10f, -8f, 6f, -4f, 0f)) shake.animateTo(x, spring(stiffness = Spring.StiffnessHigh))
        } else if (state == 1) {
            pop.animateTo(1.04f, spring(stiffness = Spring.StiffnessHigh))
            pop.animateTo(1f, spring(dampingRatio = 0.5f))
        }
    }
    val border = when (state) { 1 -> Right; 2 -> Wrong; else -> Rule }
    val bg = when (state) { 1 -> RightBg; 2 -> WrongBg; else -> Paper }
    Box(
        Modifier.fillMaxWidth().padding(vertical = 5.dp)
            .offset { IntOffset(shake.value.dp.roundToPx(), 0) }
            .graphicsLayer {
                scaleX = pop.value; scaleY = pop.value
                alpha = if (state == 3) 0.45f else 1f
            }
            .pressScale(interaction)
            .heightIn(min = 60.dp)
            .clip(TileShape)
            .background(bg)
            .border(if (state == 1 || state == 2) 2.dp else 1.dp, border, TileShape)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (asHanzi) {
            Text(choice.hanzi, fontFamily = Kai, fontSize = 32.sp, color = Ink)
        } else {
            Text(choice.meaning(currentLang()), fontSize = 17.sp, color = Ink, fontWeight = FontWeight.Medium)
        }
    }
}

/** Panneau du bas après une réponse : verdict, la bonne réponse et « Continuer ». */
@Composable
internal fun FeedbackPanel(ok: Boolean, word: Word, last: Boolean, seed: Int, onNext: () -> Unit) {
    val lang = currentLang()
    val cheer = com.zhuzi.app.data.cheers[seed % com.zhuzi.app.data.cheers.size]
    val nudge = com.zhuzi.app.data.nudges[seed % com.zhuzi.app.data.nudges.size]
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(if (ok) RightBg else WrongBg)
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 22.dp, bottom = 20.dp),
    ) {
        if (ok) {
            // Encouragement en chinois, puis dans ta langue
            Row(verticalAlignment = Alignment.Bottom) {
                Text(cheer.hanzi, fontFamily = Kai, fontSize = 26.sp, color = Right)
                Text("  " + cheer.local.get(lang), color = Right, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        } else {
            Text(nudge.get(lang), color = Wrong, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }
        Row(Modifier.padding(top = 8.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            HanziText(word, 30.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                PinyinText(word.pinyin, 16.sp)
                Text(word.meaning(currentLang()), color = InkSoft)
            }
        }
        InkButton(if (last) tr("Voir le résultat", "See result", "Результат") else tr("Continuer", "Continue", "Продолжить"), onNext, Modifier.fillMaxWidth())
    }
}

/** Écran de fin commun : sceau qui se tamponne, titre, détail. */
@Composable
fun DoneBlock(seal: String, title: String, detail: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SealStamp(seal, 120.dp)
        Spacer(Modifier.height(28.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(detail, color = InkSoft, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
    }
}
