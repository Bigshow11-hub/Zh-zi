package com.zhuzi.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal
import kotlinx.coroutines.delay

/** Une étape de la journée (séance, révisions, série) : faite ou non. */
data class DayStep(val title: String, val detail: String, val done: Boolean)

/** Une information du jour (rappel, leçon, mot du jour) : ligne simple avec une action. */
data class DayNote(
    val glyph: String,
    val title: String,
    val body: String,
    val action: String? = null,
    val onClick: (() -> Unit)? = null,
)

/** Le fil du jour : les étapes à cocher, l'action principale, puis les informations. */
data class DayFeed(
    val steps: List<DayStep>,
    val startLabel: String?,
    val onStart: () -> Unit,
    val infos: List<DayNote>,
) {
    /** Ce qui reste à faire : le chiffre de la cloche. */
    val pending: Int get() = steps.take(2).count { !it.done }
}

/**
 * La cloche de l'accueil. Une petite pastille rouge, bien posée dans le coin, avec le nombre
 * de choses qui restent ; un halo qui respire autour, et un balancement de temps en temps.
 */
@Composable
fun BellButton(pending: Int, onClick: () -> Unit) {
    val ink = Ink
    val seal = Seal
    val swing = remember { Animatable(0f) }
    val still = reduceMotion()
    LaunchedEffect(pending) {
        if (pending == 0 || still) return@LaunchedEffect
        delay(1200)
        // Trois balancements puis la cloche se tient tranquille (batterie, calme)
        repeat(3) {
            swing.animateTo(0f, keyframes {
                durationMillis = 800
                14f at 90; -12f at 200; 8f at 320; -5f at 440; 2f at 560
            })
            delay(6000)
        }
    }
    val label = tr("Notifications", "Notifications", "Уведомления") +
        if (pending > 0) " · " + tr("$pending à faire", "$pending to do", "сделать: $pending") else ""
    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
        ToolbarButton(label, onClick) {
            Box(Modifier.graphicsLayer {
                rotationZ = swing.value
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.1f)
            }) { ZIcon(Glyph.BELL, ink, size = 21.dp, weight = 1.1f) }
        }
        if (pending > 0) {
            val pop = remember { Animatable(0f) }
            LaunchedEffect(pending) {
                pop.snapTo(0.3f)
                pop.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
            }
            // Halo : trois fois, puis il s'arrête (pas d'animation sans fin)
            val ringAnim = remember { Animatable(1f) }
            LaunchedEffect(pending) {
                if (still) return@LaunchedEffect
                repeat(3) { ringAnim.snapTo(0f); ringAnim.animateTo(1f, tween(1800)) }
            }
            val ring = ringAnim.value
            Box(
                Modifier.align(Alignment.TopEnd).offset(x = (-3).dp, y = 3.dp).size(18.dp),
                contentAlignment = Alignment.Center,
            ) {
                // halo qui s'élargit et s'efface
                Canvas(Modifier.size(18.dp)) {
                    drawCircle(seal.copy(alpha = 0.35f * (1f - ring)), radius = size.minDimension / 2 * (1f + ring * 0.8f))
                }
                Box(
                    Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                        .size(18.dp).clip(CircleShape).background(seal).border(1.5.dp, Paper, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (pending > 9) "9+" else "$pending", color = Color.White, fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold, lineHeight = 10.sp)
                }
            }
        }
    }
}

/** Le panneau des notifications du jour, qui monte du bas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(feed: DayFeed, dateLabel: String, onDismiss: () -> Unit) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ZhuziSheet(onDismiss, grouped = true, sheetState = state) {
        NotificationsContent(feed, dateLabel, onDismiss)
    }
}

/**
 * Le contenu : la carte « Ta journée » (encre profonde, panda 3D, étapes qui se cochent,
 * une seule grande action), puis les informations en liste simple.
 */
@Composable
fun NotificationsContent(feed: DayFeed, dateLabel: String, onDismiss: () -> Unit) {
    val seen = remember { mutableSetOf<Any>() }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
    ) {
        Text(dateLabel, color = InkSoft, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
        Text(tr("Ta journée", "Your day", "Твой день"), style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 16.dp).semantics { heading() })
        DayCard(feed, Modifier.staggerIn("day", 0, seen)) { onDismiss(); feed.onStart() }
        if (feed.infos.isNotEmpty()) {
            GroupedSection(
                Modifier.padding(top = 28.dp).staggerIn("infos", 1, seen),
                header = tr("À SAVOIR", "GOOD TO KNOW", "К СВЕДЕНИЮ"),
            ) {
                feed.infos.forEachIndexed { i, n ->
                    if (i > 0) RowDivider(68.dp)
                    InfoRow(n) { n.onClick?.let { onDismiss(); it() } }
                }
            }
        }
    }
}

@Composable
private fun DayCard(feed: DayFeed, modifier: Modifier, onStart: () -> Unit) {
    val deep = Color(0xFF16172C)
    val done = feed.steps.count { it.done }
    val total = feed.steps.size
    Box(modifier.fillMaxWidth().clip(com.zhuzi.app.ui.theme.SmoothShape(22.dp)).background(deep)) {
        // le panda 3D qui regarde la carte, posé en haut à droite
        PandaHead(Modifier.align(Alignment.TopEnd).offset(x = 14.dp, y = (-6).dp).size(112.dp))
        Column(Modifier.padding(20.dp)) {
            Text(
                if (done == total) tr("Tout est fait !", "All done!", "Всё сделано!")
                else tr("$done sur $total", "$done of $total", "$done из $total"),
                color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp,
            )
            Text(
                if (done == total) tr("Ton bambou a poussé aujourd'hui.", "Your bamboo grew today.", "Твой бамбук сегодня подрос.")
                else tr("Quelques minutes et ta journée est validée.", "A few minutes and your day is complete.",
                    "Несколько минут — и день засчитан."),
                color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp, end = 84.dp),
            )
            // barre en segments : un par étape, qui se remplit en cascade
            Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                feed.steps.forEachIndexed { i, s -> Segment(s.done, i, Modifier.weight(1f)) }
            }
            Column(Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                feed.steps.forEachIndexed { i, s -> StepRow(s, i) }
            }
            feed.startLabel?.let { label ->
                val interaction = remember { MutableInteractionSource() }
                Box(
                    Modifier.padding(top = 18.dp).fillMaxWidth().height(52.dp).pressScale(interaction)
                        .clip(RoundedCornerShape(50)).background(Color.White)
                        .clickable(interactionSource = interaction, indication = null,
                            role = androidx.compose.ui.semantics.Role.Button, onClick = onStart),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = deep, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun Segment(done: Boolean, index: Int, modifier: Modifier) {
    val fill = remember { Animatable(0f) }
    LaunchedEffect(done) {
        delay(250L + index * 140L)
        fill.animateTo(if (done) 1f else 0f, tween(520, easing = EaseOutQuart))
    }
    val accent = Accent
    Canvas(modifier.height(6.dp)) {
        val r = androidx.compose.ui.geometry.CornerRadius(size.height / 2, size.height / 2)
        drawRoundRect(Color.White.copy(alpha = 0.16f), cornerRadius = r)
        if (fill.value > 0f) drawRoundRect(accent, size = size.copy(width = size.width * fill.value), cornerRadius = r)
    }
}

@Composable
private fun StepRow(step: DayStep, index: Int) {
    val accent = Accent
    val check = remember { Animatable(0f) }
    LaunchedEffect(step.done) {
        delay(350L + index * 160L)
        check.animateTo(if (step.done) 1f else 0f, tween(420, easing = EaseOutQuart))
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        // rond qui se remplit, puis la coche qui se dessine
        Canvas(Modifier.size(24.dp)) {
            val w = size.width
            val c = check.value
            drawCircle(Color.White.copy(alpha = 0.28f), radius = w / 2 - 1.dp.toPx(), style = Stroke(1.5.dp.toPx()))
            if (c > 0f) {
                drawCircle(accent, radius = (w / 2) * minOf(1f, c * 1.6f))
                val t = ((c - 0.35f) / 0.65f).coerceIn(0f, 1f)
                if (t > 0f) {
                    val a = Offset(w * 0.28f, w * 0.52f)
                    val b = Offset(w * 0.44f, w * 0.67f)
                    val e = Offset(w * 0.73f, w * 0.36f)
                    val path = Path().apply {
                        moveTo(a.x, a.y)
                        if (t < 0.4f) {
                            val k = t / 0.4f
                            lineTo(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k)
                        } else {
                            lineTo(b.x, b.y)
                            val k = (t - 0.4f) / 0.6f
                            lineTo(b.x + (e.x - b.x) * k, b.y + (e.y - b.y) * k)
                        }
                    }
                    drawPath(path, Color.White, style = Stroke(2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(step.title, color = Color.White.copy(alpha = if (step.done) 0.6f else 1f),
                fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(step.detail, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun InfoRow(note: DayNote, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 64.dp)
            .clickable(enabled = note.onClick != null, role = androidx.compose.ui.semantics.Role.Button, onClick = onClick)
            .padding(start = 16.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KaiTile(note.glyph, size = 38.dp, filled = false)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(note.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Ink, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text(note.body, style = MaterialTheme.typography.bodySmall, color = InkSoft, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        note.action?.let {
            Text(it, color = Accent, style = MaterialTheme.typography.titleSmall, maxLines = 1,
                modifier = Modifier.padding(start = 10.dp))
        }
    }
}

/** La tête du panda en 3D, qui cligne des yeux de temps en temps et flotte doucement. */
@Composable
fun PandaHead(modifier: Modifier = Modifier, float: Boolean = true) {
    val blink = remember { Animatable(0f) }
    val still = reduceMotion()
    LaunchedEffect(Unit) {
        if (still) return@LaunchedEffect
        while (true) {
            delay(2600L + (0..2200).random())
            blink.snapTo(1f); delay(130); blink.snapTo(0f)
            if ((0..3).random() == 0) { delay(160); blink.snapTo(1f); delay(120); blink.snapTo(0f) }
        }
    }
    val bob = rememberInfiniteTransition(label = "bob")
    val y by bob.animateFloat(-1f, 1f, infiniteRepeatable(tween(1600, easing = EaseInOutSine), RepeatMode.Reverse), label = "y")
    val tilt by bob.animateFloat(-3f, 3f, infiniteRepeatable(tween(2300, easing = EaseInOutSine), RepeatMode.Reverse), label = "tilt")
    Image(
        painterResource(if (blink.value > 0.5f) R.drawable.panda_head_blink else R.drawable.panda_head),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            if (float && !still) {
                translationY = y * 2.5f * density
                rotationZ = tilt
            }
        },
    )
}

/** Va-et-vient doux (sinusoïdal), pour le flottement du panda. */
val EaseInOutSine = androidx.compose.animation.core.CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

/** Fabrique le fil du jour à partir de l'état de l'appli. */
@Composable
fun dayNotes(
    activeToday: Boolean,
    streakDays: Int,
    due: Int,
    dueTomorrow: Int,
    reminderOn: Boolean,
    hour: Int,
    next: com.zhuzi.app.data.Lesson,
    nextKnown: Int,
    wordOfDay: com.zhuzi.app.data.Word,
    onReview: () -> Unit,
    onOpen: (com.zhuzi.app.data.Lesson) -> Unit,
    onReminder: () -> Unit,
    speak: (String) -> Unit,
    reminderAt: Pair<Int, Int> = 19 to 0,
): DayFeed {
    val lang = currentLang()
    val at = "%02d:%02d".format(reminderAt.first, reminderAt.second)
    val passed = hour * 60 >= reminderAt.first * 60 + reminderAt.second
    val streakNow = if (activeToday) streakDays else streakDays + 1
    val steps = listOf(
        DayStep(tr("Séance du jour", "Today's session", "Занятие дня"),
            if (activeToday) tr("Validée", "Done", "Засчитано") else tr("5 minutes suffisent", "5 minutes is enough", "Хватит 5 минут"),
            activeToday),
        DayStep(tr("Révisions", "Reviews", "Повторения"),
            if (due > 0) wordsCount(lang, due) + tr(" prêts à revoir", " ready to review", " готовы к повторению")
            else if (dueTomorrow > 0) tr("À jour · demain : ", "Up to date · tomorrow: ", "Готово · завтра: ") + wordsCount(lang, dueTomorrow)
            else tr("À jour", "Up to date", "Готово"),
            due == 0),
        DayStep(tr("Série", "Streak", "Серия"),
            if (activeToday) daysCount(lang, streakDays) + tr(" d'affilée", " in a row", " подряд")
            else if (streakDays > 0) tr("Passe à ", "Goes to ", "Станет ") + daysCount(lang, streakNow) + tr(" avant minuit", " before midnight", " до полуночи")
            else tr("Commence ta série aujourd'hui", "Start your streak today", "Начни серию сегодня"),
            activeToday),
    )
    val startLabel = when {
        due > 0 -> tr("Réviser ", "Review ", "Повторить ") + wordsCount(lang, due)
        !activeToday -> tr("Commencer · 5 min", "Start · 5 min", "Начать · 5 мин")
        else -> null
    }
    val start: () -> Unit = if (due > 0) onReview else ({ onOpen(next) })
    val infos = listOf(
        DayNote(next.symbol, lessonName(next) + " · " + next.title(lang),
            "$nextKnown / ${next.words.size} " + tr("mots découverts", "words discovered", "слов открыто"),
            tr("Ouvrir", "Open", "Открыть")) { onOpen(next) },
        DayNote(wordOfDay.hanzi.take(1), tr("Mot du jour : ", "Word of the day: ", "Слово дня: ") + wordOfDay.hanzi,
            wordOfDay.pinyin + " · " + wordOfDay.meaning(lang), tr("Écouter", "Listen", "Слушать")) { speak(wordOfDay.hanzi) },
        when {
            !reminderOn -> DayNote("钟", tr("Rappel désactivé", "Reminder off", "Напоминание выключено"),
                tr("Active-le pour un petit mot chaque jour.", "Turn it on for a daily nudge.", "Включи ежедневное напоминание."),
                tr("Régler", "Settings", "Настроить"), onReminder)
            passed -> DayNote("钟", tr("Rappel de $at", "$at reminder", "Напоминание в $at"),
                tr("C'est l'heure de ton chinois du jour.", "It's time for your daily Chinese.", "Время для китайского."),
                tr("Régler", "Settings", "Настроить"), onReminder)
            else -> DayNote("钟", tr("Rappel à $at", "Reminder at $at", "Напоминание в $at"),
                tr("Seulement si tu n'as pas encore étudié.", "Only if you haven't studied yet.", "Только если ты ещё не занимался."),
                tr("Régler", "Settings", "Настроить"), onReminder)
        },
    )
    return DayFeed(steps, startLabel, start, infos)
}
