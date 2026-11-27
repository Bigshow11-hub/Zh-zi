package com.zhuzi.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.ExamBuilder
import com.zhuzi.app.data.ExamKind
import com.zhuzi.app.data.ExamPart
import com.zhuzi.app.data.ExamQuestion
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Right
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.Wrong
import kotlinx.coroutines.delay

/**
 * Examen blanc d'un niveau HSK : un écran d'accueil, 20 questions (10 écoute, 10 lecture)
 * sans correction pendant l'épreuve, puis le bilan sur 100.
 */
@Composable
fun ExamScreen(
    level: Int,
    best: Int,
    speak: (String) -> Unit,
    onFinish: (score: Int, missed: List<Word>) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
    startStep: Int = 0,
    previewAnswers: List<Int>? = null,
) {
    val lang = currentLang()
    val questions = remember { ExamBuilder.build(level, lang, seed = if (previewAnswers != null) 7L else System.currentTimeMillis()) }
    // 0 : accueil · 1 : questions · 2 : bilan
    var step by remember { mutableIntStateOf(startStep) }
    val answers = remember { mutableStateListOf<Int>().apply { previewAnswers?.let { addAll(it) } } }
    Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
        TopBar(tr("Retour", "Back", "Назад"), tr("Examen blanc HSK $level", "HSK $level mock exam", "Пробный экзамен HSK $level"), onBack)
        AnimatedContent(step, transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
            label = "examStep", modifier = Modifier.weight(1f)) { s ->
            when (s) {
                0 -> ExamIntro(level, best, contentPadding) { step = 1 }
                1 -> ExamRun(questions, speak, contentPadding) { list ->
                    answers.clear(); answers.addAll(list)
                    val score = score(questions, list)
                    onFinish(score, questions.filterIndexed { i, q -> list.getOrNull(i) != q.answer }.map { it.word })
                    step = 2
                }
                else -> ExamResult(level, questions, answers, speak, contentPadding, onRetry = { step = 0 }, onBack = onBack)
            }
        }
    }
}

private fun score(qs: List<ExamQuestion>, answers: List<Int>): Int =
    qs.indices.count { answers.getOrNull(it) == qs[it].answer } * 100 / qs.size

@Composable
private fun ExamIntro(level: Int, best: Int, contentPadding: PaddingValues, onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(tr("Examen blanc", "Mock exam", "Пробный экзамен"), style = MaterialTheme.typography.headlineMedium)
                Text("HSK $level", color = Accent, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            PandaHead(Modifier.size(72.dp))
        }
        Text(tr("Comme le vrai HSK, en plus court : 20 questions, sans correction pendant l'épreuve. Tu sauras à la fin si tu es prêt.",
            "Like the real HSK, only shorter: 20 questions, no corrections during the test. You'll know at the end if you're ready.",
            "Как настоящий HSK, только короче: 20 вопросов, без проверки во время теста. В конце узнаешь, готов ли ты."),
            fontSize = 15.sp, color = InkSoft, lineHeight = 21.sp, modifier = Modifier.padding(top = 12.dp))
        Spacer(Modifier.height(20.dp))
        PartCard("听力", tr("Écoute", "Listening", "Аудирование"),
            tr("10 questions · un mot ou une phrase lus à voix haute, deux écoutes", "10 questions · a word or sentence read aloud, two plays",
                "10 вопросов · слово или фраза вслух, два прослушивания"))
        Spacer(Modifier.height(10.dp))
        PartCard("阅读", tr("Lecture", "Reading", "Чтение"),
            tr("10 questions · le sens des mots et des phrases à trou", "10 questions · word meanings and fill-in-the-blank sentences",
                "10 вопросов · значение слов и фразы с пропуском"))
        Row(Modifier.padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Fact("~10 min", tr("durée", "duration", "время"), Modifier.weight(1f))
            Fact("${ExamBuilder.PASS} / 100", tr("pour réussir", "to pass", "для сдачи"), Modifier.weight(1f))
            Fact(if (best >= 0) "$best" else "—", tr("ton record", "your best", "твой рекорд"), Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
        InkButton(tr("Commencer l'examen", "Start the exam", "Начать экзамен"), onStart, Modifier.fillMaxWidth())
        Text(tr("Mets le son : la première partie se fait à l'oreille.", "Turn the sound on: the first part is all listening.",
            "Включи звук: первая часть — на слух."),
            fontSize = 12.sp, color = InkSoft, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
    }
}

@Composable
private fun PartCard(glyph: String, title: String, body: String) {
    Row(Modifier.fillMaxWidth().softCard().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Mist), contentAlignment = Alignment.Center) {
            Text(glyph, fontFamily = Kai, fontSize = 20.sp, color = Ink)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(body, fontSize = 13.sp, color = InkSoft)
        }
    }
}

@Composable
private fun Fact(value: String, label: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Mist).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Ink)
        Text(label, fontSize = 12.sp, color = InkSoft)
    }
}

@Composable
private fun ExamRun(questions: List<ExamQuestion>, speak: (String) -> Unit, contentPadding: PaddingValues, onDone: (List<Int>) -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<Int>() }
    var picked by remember(index) { mutableIntStateOf(-1) }
    val q = questions[index]
    val partSize = questions.count { it.part == q.part }
    val inPart = questions.take(index + 1).count { it.part == q.part }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    (if (q.part == ExamPart.LISTENING) "听力 · " + tr("Écoute", "Listening", "Аудирование")
                    else "阅读 · " + tr("Lecture", "Reading", "Чтение")),
                    fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink, modifier = Modifier.weight(1f),
                )
                Text("$inPart / $partSize", fontSize = 13.sp, color = InkSoft, fontWeight = FontWeight.Bold)
            }
            ProgressBar((index + 1f) / questions.size, Modifier.padding(top = 8.dp), height = 6.dp)
        }
        AnimatedContent(
            index,
            transitionSpec = {
                (slideInHorizontally(tween(260)) { it / 4 } + fadeIn(tween(260))) togetherWith
                    (slideOutHorizontally(tween(180)) { -it / 4 } + fadeOut(tween(180)))
            },
            label = "q", modifier = Modifier.weight(1f),
        ) { i ->
            QuestionBody(questions[i], speak, if (i == index) picked else -1) { picked = it }
        }
        BottomActions(contentPadding) {
            InkButton(
                if (index + 1 < questions.size) tr("Suivant", "Next", "Далее") else tr("Terminer l'examen", "Finish the exam", "Завершить экзамен"),
                {
                    answers += picked
                    if (index + 1 < questions.size) index++ else onDone(answers.toList())
                },
                Modifier.weight(1f), enabled = picked >= 0,
            )
        }
    }
}

@Composable
private fun QuestionBody(q: ExamQuestion, speak: (String) -> Unit, picked: Int, onPick: (Int) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            when (q.kind) {
                ExamKind.HEAR_WORD -> tr("Écoute, puis choisis les caractères", "Listen, then choose the characters", "Послушай и выбери иероглифы")
                ExamKind.HEAR_SENTENCE -> tr("Écoute, puis choisis la traduction", "Listen, then choose the translation", "Послушай и выбери перевод")
                ExamKind.READ_WORD -> tr("Que veut dire ce mot ?", "What does this word mean?", "Что значит это слово?")
                ExamKind.FILL_BLANK -> tr("Quel mot complète la phrase ?", "Which word completes the sentence?", "Какое слово пропущено?")
            },
            color = InkSoft, fontSize = 15.sp, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        if (q.audio != null) {
            ListenButton(q.audio, speak)
        } else if (q.prompt != null) {
            Box(
                Modifier.fillMaxWidth().softCard().padding(horizontal = 18.dp, vertical = 26.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(q.prompt, fontFamily = Kai, fontSize = if (q.kind == ExamKind.READ_WORD) 52.sp else 30.sp, color = Ink,
                    textAlign = TextAlign.Center, lineHeight = if (q.kind == ExamKind.READ_WORD) 60.sp else 42.sp)
            }
        }
        Spacer(Modifier.height(22.dp))
        if (q.hanziChoices) {
            // 4 caractères : une grille 2 × 2
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                q.choices.chunked(2).forEachIndexed { r, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEachIndexed { c, text ->
                            val i = r * 2 + c
                            ChoiceBox(text, true, i == picked, Modifier.weight(1f)) { onPick(i) }
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                q.choices.forEachIndexed { i, text -> ChoiceBox(text, false, i == picked, Modifier.fillMaxWidth()) { onPick(i) } }
            }
        }
    }
}

/** Gros bouton d'écoute : joue une fois tout seul, puis une deuxième fois à la demande (comme au HSK). */
@Composable
private fun ListenButton(text: String, speak: (String) -> Unit) {
    var plays by remember(text) { mutableIntStateOf(0) }
    val pulse = remember(text) { Animatable(0f) }
    LaunchedEffect(text) {
        delay(350); speak(text); plays = 1
        pulse.snapTo(0f); pulse.animateTo(1f, tween(900))
    }
    val accent = Accent
    val left = (2 - plays).coerceAtLeast(0)
    val label = tr("Écouter", "Listen", "Слушать")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(120.dp)) {
                drawCircle(accent.copy(alpha = 0.18f * (1f - pulse.value)), radius = size.minDimension / 2 * (0.7f + 0.3f * pulse.value))
            }
            Box(
                Modifier.size(84.dp).clip(CircleShape).background(if (left > 0) accent else Mist)
                    .clickable(enabled = left > 0) {
                        speak(text); plays++
                    }
                    .semantics { contentDescription = label },
                contentAlignment = Alignment.Center,
            ) { SpeakerIcon(if (left > 0) Color.White else InkSoft) }
        }
        Text(
            if (left > 0) tr("Encore $left écoute", "$left more play", "Ещё прослушиваний: $left")
            else tr("Plus d'écoute : choisis ta réponse", "No plays left: pick your answer", "Прослушиваний больше нет: выбери ответ"),
            fontSize = 13.sp, color = InkSoft, modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun SpeakerIcon(c: Color) {
    Canvas(Modifier.size(34.dp)) {
        val w = size.width
        val p = Path().apply {
            moveTo(w * 0.12f, w * 0.38f); lineTo(w * 0.30f, w * 0.38f); lineTo(w * 0.52f, w * 0.18f)
            lineTo(w * 0.52f, w * 0.82f); lineTo(w * 0.30f, w * 0.62f); lineTo(w * 0.12f, w * 0.62f); close()
        }
        drawPath(p, c)
        val st = Stroke(w * 0.07f, cap = StrokeCap.Round)
        drawArc(c, -45f, 90f, false, Offset(w * 0.46f, w * 0.32f), androidx.compose.ui.geometry.Size(w * 0.24f, w * 0.36f), style = st)
        drawArc(c, -50f, 100f, false, Offset(w * 0.44f, w * 0.18f), androidx.compose.ui.geometry.Size(w * 0.44f, w * 0.64f), style = st)
    }
}

@Composable
private fun ChoiceBox(text: String, hanzi: Boolean, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val accent = Accent
    Box(
        modifier.heightIn(min = if (hanzi) 76.dp else 56.dp).pressScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) accent.copy(alpha = 0.10f) else Paper)
            .border(if (selected) 2.dp else 1.dp, if (selected) accent else Rule, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = if (hanzi) Alignment.Center else Alignment.CenterStart,
    ) {
        if (hanzi) Text(text, fontFamily = Kai, fontSize = 30.sp, color = if (selected) accent else Ink)
        else Text(text, fontSize = 16.sp, color = if (selected) accent else Ink, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun ExamResult(
    level: Int,
    questions: List<ExamQuestion>,
    answers: List<Int>,
    speak: (String) -> Unit,
    contentPadding: PaddingValues,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val total = score(questions, answers)
    val passed = total >= ExamBuilder.PASS
    fun partScore(p: ExamPart) = questions.indices.count { questions[it].part == p && answers.getOrNull(it) == questions[it].answer }
    val listen = partScore(ExamPart.LISTENING)
    val read = partScore(ExamPart.READING)
    val missed = questions.indices.filter { answers.getOrNull(it) != questions[it].answer }
    val count = remember { Animatable(0f) }
    LaunchedEffect(Unit) { count.animateTo(total.toFloat(), tween(1100, easing = EaseOutQuart)) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PandaHead(Modifier.size(110.dp))
            SealStamp(if (passed) "好" else "加", 44.dp, Modifier.padding(end = 0.dp))
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 12.dp)) {
            Text("${count.value.toInt()}", style = MaterialTheme.typography.displayLarge, color = if (passed) Accent else Ink)
            Text(" / 100", fontSize = 20.sp, color = InkSoft, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
        }
        Text(
            when {
                total >= 90 -> tr("Excellent : tu es prêt pour le HSK $level !", "Excellent: you're ready for HSK $level!", "Отлично: ты готов к HSK $level!")
                passed -> tr("Réussi : le HSK $level est à ta portée.", "Passed: HSK $level is within reach.", "Сдано: HSK $level тебе по силам.")
                else -> tr("Pas encore : il faut ${ExamBuilder.PASS} pour réussir. Révise les mots ci-dessous et réessaie.",
                    "Not yet: you need ${ExamBuilder.PASS} to pass. Review the words below and try again.",
                    "Пока нет: нужно ${ExamBuilder.PASS} баллов. Повтори слова ниже и попробуй снова.")
            },
            fontSize = 16.sp, color = Ink, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        Column(Modifier.fillMaxWidth().padding(top = 20.dp).softCard().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PartBar("听力 · " + tr("Écoute", "Listening", "Аудирование"), listen, questions.count { it.part == ExamPart.LISTENING })
            PartBar("阅读 · " + tr("Lecture", "Reading", "Чтение"), read, questions.count { it.part == ExamPart.READING })
        }
        if (missed.isNotEmpty()) {
            Text(tr("À REVOIR", "TO REVIEW", "ПОВТОРИТЬ"), style = Kicker, modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 4.dp))
            Text(tr("Ces mots sont ajoutés à ta révision de demain.", "These words are added to tomorrow's review.",
                "Эти слова добавлены в завтрашнее повторение."), fontSize = 13.sp, color = InkSoft,
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))
            missed.map { questions[it].word }.distinctBy { it.hanzi }.forEach { w -> WordLine(w, speak) }
        }
        Spacer(Modifier.height(18.dp))
        InkButton(tr("Refaire un examen", "Take another exam", "Пройти ещё раз"), onRetry, Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        InkOutlinedButton(tr("Retour aux leçons", "Back to lessons", "К урокам"), onBack, Modifier.fillMaxWidth())
    }
}

@Composable
private fun PartBar(label: String, good: Int, total: Int) {
    Column {
        Row {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text("$good / $total", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = if (good * 10 >= total * 6) Right else Wrong)
        }
        ProgressBar(good.toFloat() / total, Modifier.padding(top = 6.dp), height = 6.dp)
    }
}

/** Pastille « examen blanc » de la page Leçons. */
@Composable
fun ExamEntry(level: Int, best: Int, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().pressScale(interaction).softCard()
            .clickable(interactionSource = interaction, indication = null, onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).background(Seal), contentAlignment = Alignment.Center) {
            Text("考", fontFamily = Kai, fontSize = 30.sp, color = Color.White)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(tr("Examen blanc HSK $level", "HSK $level mock exam", "Пробный экзамен HSK $level"), fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(
                if (best >= 0) tr("Ton record : $best / 100", "Your best: $best / 100", "Твой рекорд: $best / 100")
                else tr("20 questions · écoute et lecture · es-tu prêt ?", "20 questions · listening and reading · are you ready?",
                    "20 вопросов · аудирование и чтение · ты готов?"),
                fontSize = 13.sp, color = InkSoft,
            )
        }
        Text("›", fontSize = 22.sp, color = InkSoft)
    }
}
