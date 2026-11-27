package com.zhuzi.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Right
import com.zhuzi.app.ui.theme.Seal
import kotlinx.coroutines.delay

/** Une notification du jour : pastille (caractère + couleur), titre, texte, heure ou statut. */
data class DayNote(
    val glyph: String,
    val tint: Color,
    val title: String,
    val body: String,
    val time: String,
    /** À faire (compte dans la pastille rouge de la cloche). */
    val pending: Boolean,
    /** Déjà fait aujourd'hui (coche verte). */
    val done: Boolean = false,
    val action: String? = null,
    val onClick: (() -> Unit)? = null,
)

/**
 * La cloche de l'accueil : pastille rouge avec le nombre de choses à faire, et un petit
 * balancement de temps en temps tant qu'il en reste.
 */
@Composable
fun BellButton(pending: Int, onClick: () -> Unit) {
    val ink = Ink
    val swing = remember { Animatable(0f) }
    LaunchedEffect(pending) {
        if (pending == 0) return@LaunchedEffect
        delay(900)
        while (true) {
            swing.animateTo(0f, keyframes {
                durationMillis = 900
                16f at 90; -14f at 210; 10f at 330; -7f at 450; 4f at 570; -2f at 690
            })
            delay(5000)
        }
    }
    val label = tr("Notifications", "Notifications", "Уведомления")
    Box(Modifier.size(44.dp)) {
        Box(
            Modifier.size(44.dp).softCard(CircleShape).clickable(onClick = onClick)
                .semantics { contentDescription = label },
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.graphicsLayer {
                rotationZ = swing.value
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.1f)
            }) { BellIcon(ink) }
        }
        if (pending > 0) {
            val pop = remember { Animatable(0f) }
            LaunchedEffect(pending) {
                pop.snapTo(0.4f)
                pop.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium))
            }
            Box(
                Modifier.align(Alignment.TopEnd).offset(x = 3.dp, y = (-3).dp)
                    .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                    .size(19.dp).clip(CircleShape).background(Seal).border(2.dp, Paper, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$pending", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun BellIcon(c: Color) {
    androidx.compose.foundation.Canvas(Modifier.size(20.dp)) {
        val w = size.width
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.18f, w * 0.74f)
            cubicTo(w * 0.26f, w * 0.62f, w * 0.24f, w * 0.52f, w * 0.26f, w * 0.40f)
            cubicTo(w * 0.30f, w * 0.18f, w * 0.70f, w * 0.18f, w * 0.74f, w * 0.40f)
            cubicTo(w * 0.76f, w * 0.52f, w * 0.74f, w * 0.62f, w * 0.82f, w * 0.74f)
            close()
        }
        drawPath(p, c, style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.10f))
        drawLine(c, androidx.compose.ui.geometry.Offset(w * 0.42f, w * 0.88f),
            androidx.compose.ui.geometry.Offset(w * 0.58f, w * 0.88f), w * 0.10f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round)
    }
}

/** Le panneau des notifications du jour, qui monte du bas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(notes: List<DayNote>, dateLabel: String, onDismiss: () -> Unit) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = Paper) {
        NotificationsContent(notes, dateLabel, onDismiss)
    }
}

@Composable
fun NotificationsContent(notes: List<DayNote>, dateLabel: String, onDismiss: () -> Unit) {
    val pending = notes.count { it.pending }
    val seen = remember { mutableSetOf<Any>() }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
    ) {
        Text(tr("Notifications", "Notifications", "Уведомления"), style = MaterialTheme.typography.headlineMedium)
        Text(
            dateLabel + " · " + if (pending == 0) tr("tout est fait, bravo !", "all done, well done!", "всё сделано, молодец!")
            else tr("$pending à faire", "$pending to do", "сделать: $pending"),
            color = InkSoft, fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp, bottom = 16.dp),
        )
        Text(tr("AUJOURD'HUI", "TODAY", "СЕГОДНЯ"), style = Kicker, modifier = Modifier.padding(bottom = 10.dp))
        notes.forEachIndexed { i, n ->
            NoteRow(n, Modifier.staggerIn("note$i", i, seen)) {
                n.onClick?.let { onDismiss(); it() }
            }
        }
    }
}

@Composable
private fun NoteRow(note: DayNote, modifier: Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier.padding(bottom = 10.dp).fillMaxWidth().pressScale(interaction)
            .softCard(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, enabled = note.onClick != null, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.size(42.dp).clip(CircleShape)
            .background(if (note.tint == Ink) com.zhuzi.app.ui.theme.Mist else note.tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center) {
            Text(note.glyph, fontFamily = Kai, fontSize = 22.sp, color = note.tint)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Ink,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Text(note.time, fontSize = 11.sp, color = InkSoft, fontWeight = FontWeight.Bold)
            }
            Text(note.body, fontSize = 13.sp, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                when {
                    note.done -> StatusPill("✓ " + tr("Fait", "Done", "Готово"), Right)
                    note.pending -> StatusPill(tr("À faire", "To do", "Сделать"), Seal)
                }
                note.action?.let { Text("$it ›", color = Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Box(Modifier.clip(com.zhuzi.app.ui.theme.PillShape).background(color.copy(alpha = 0.13f))
        .padding(horizontal = 9.dp, vertical = 3.dp)) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

/** Fabrique les notifications du jour à partir de l'état de l'appli. */
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
): List<DayNote> {
    val at = "%02d:%02d".format(reminderAt.first, reminderAt.second)
    val passed = hour * 60 >= reminderAt.first * 60 + reminderAt.second
    val lang = currentLang()
    val accent = Accent
    // Une seule couleur pour l'action ; le reste en encre neutre, le rouge pour la série
    val blue = Ink
    val green = Ink
    val amber = Ink
    val start: () -> Unit = if (due > 0) onReview else ({ onOpen(next) })
    val notes = mutableListOf<DayNote>()
    // 1. La journée : valider par une séance
    notes += if (activeToday) DayNote("好", Right, tr("Journée validée", "Day complete", "День засчитан"),
        tr("Tu as étudié aujourd'hui. Reviens demain pour faire grandir ta série.",
            "You studied today. Come back tomorrow to grow your streak.",
            "Сегодня ты позанимался. Возвращайся завтра, чтобы серия росла."),
        tr("Fait", "Done", "Готово"), pending = false, done = true)
    else DayNote("学", accent, tr("Fais une séance pour valider ta journée", "Do a session to complete your day", "Позанимайся, чтобы засчитать день"),
        tr("Une révision ou quelques cartes suffisent : 5 minutes et c'est validé.",
            "A review or a few cards is enough: 5 minutes and it counts.",
            "Хватит повторения или пары карточек: 5 минут — и день засчитан."),
        tr("Aujourd'hui", "Today", "Сегодня"), pending = true,
        action = tr("Commencer", "Start", "Начать"), onClick = start)
    // 2. La série en jeu
    if (streakDays > 0 && !activeToday) {
        notes += DayNote("天", Seal, tr("Ta série de ", "Your ", "Твоя серия: ") + daysCount(lang, streakDays) +
            tr(" t'attend", " streak is waiting", ""),
            tr("Étudie avant minuit pour ne pas la perdre.", "Study before midnight to keep it.", "Позанимайся до полуночи, чтобы её не потерять."),
            tr("Avant minuit", "By midnight", "До полуночи"), pending = true,
            action = tr("Garder ma série", "Keep my streak", "Сохранить серию"), onClick = start)
    }
    // 3. Les révisions
    notes += if (due > 0) DayNote("记", blue, wordsCount(lang, due) + tr(" à revoir", " to review", " повторить"),
        tr("Ils sont prêts : c'est le meilleur moment pour ne pas les oublier.",
            "They're ready: now is the best time not to forget them.",
            "Они готовы: сейчас лучшее время, чтобы их не забыть."),
        tr("Prêt", "Ready", "Готово"), pending = true, action = tr("Réviser", "Review", "Повторить"), onClick = onReview)
    else DayNote("记", blue, tr("Révisions à jour", "Reviews up to date", "Повторения выполнены"),
        if (dueTomorrow > 0) tr("Demain : ", "Tomorrow: ", "Завтра: ") + wordsCount(lang, dueTomorrow)
        else tr("Rien à revoir pour l'instant.", "Nothing to review for now.", "Пока нечего повторять."),
        tr("Aujourd'hui", "Today", "Сегодня"), pending = false, done = true)
    // 4. Le rappel quotidien, à l'heure choisie
    notes += when {
        !reminderOn -> DayNote("钟", InkSoft, tr("Rappel désactivé", "Reminder off", "Напоминание выключено"),
            tr("Active-le pour recevoir un petit mot chaque jour à l'heure de ton choix.",
                "Turn it on to get a little nudge every day at the time you choose.",
                "Включи его, чтобы каждый день получать напоминание в удобное время."),
            at, pending = false, action = tr("Régler", "Settings", "Настроить"), onClick = onReminder)
        passed -> DayNote("钟", amber, tr("Rappel de $at", "$at reminder", "Напоминание в $at"),
            tr("« 学习时间到了 » : c'est l'heure de ton chinois du jour.",
                "“学习时间到了”: it's time for your daily Chinese.",
                "«学习时间到了»: время для китайского."),
            at, pending = false, action = tr("Régler", "Settings", "Настроить"), onClick = onReminder)
        else -> DayNote("钟", amber, tr("Prochain rappel à $at", "Next reminder at $at", "Следующее напоминание в $at"),
            tr("Un petit mot si tu n'as pas encore étudié d'ici là.",
                "A little nudge if you haven't studied by then.",
                "Напомню, если ты к тому времени ещё не позанимаешься."),
            at, pending = false, action = tr("Régler", "Settings", "Настроить"), onClick = onReminder)
    }
    // 5. La leçon en cours
    notes += DayNote(next.symbol, green, lessonName(next) + " · " + next.title(lang),
        "$nextKnown / ${next.words.size} " + tr("mots découverts", "words discovered", "слов открыто"),
        tr("En cours", "In progress", "В процессе"), pending = false, done = nextKnown >= next.words.size,
        action = tr("Continuer", "Continue", "Продолжить"), onClick = { onOpen(next) })
    // 6. Le mot du jour
    notes += DayNote(wordOfDay.hanzi.take(1), Seal, tr("Mot du jour : ", "Word of the day: ", "Слово дня: ") + wordOfDay.hanzi,
        wordOfDay.pinyin + " · " + wordOfDay.meaning(lang),
        tr("Nouveau", "New", "Новое"), pending = false,
        action = tr("Écouter", "Listen", "Слушать"), onClick = { speak(wordOfDay.hanzi) })
    return notes
}
