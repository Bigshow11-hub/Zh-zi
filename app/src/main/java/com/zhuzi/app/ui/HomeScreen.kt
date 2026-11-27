package com.zhuzi.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R
import com.zhuzi.app.data.Greeting
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Proverb
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Accent2
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.TileShape

/** Bleu des tuiles « bien retenus ». */
private val TileBlue = Color(0xFF4C8DF6)

/** Onglet Accueil : salut, chiffres clés, objectif du jour, programme, mot et proverbe du jour. */
@Composable
fun HomeScreen(
    todayLabel: String,
    greeting: Greeting,
    streakDays: Int,
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    total: Int,
    next: Lesson,
    nextKnown: Int,
    wordOfDay: Word,
    wordOfDayKnown: Boolean,
    proverb: Proverb,
    speak: (String) -> Unit,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
    onSearch: () -> Unit,
    onReminder: () -> Unit,
    onSeeAll: () -> Unit,
    look: com.zhuzi.app.data.AvatarLook,
    onAvatar: () -> Unit,
    contentPadding: PaddingValues,
    activeToday: Boolean = false,
    reminderOn: Boolean = true,
    hour: Int = java.time.LocalTime.now().hour,
    startWithNotes: Boolean = false,
    reminderAt: Pair<Int, Int> = 19 to 0,
) {
    val lang = currentLang()
    val seen = remember { mutableSetOf<Any>() }
    var showNotes by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(startWithNotes) }
    val notes = dayNotes(activeToday, streakDays, dueWords.size, dueTomorrow, reminderOn, hour, next, nextKnown, wordOfDay,
        onReview, onOpen, onReminder, speak, reminderAt)
    if (showNotes) NotificationsSheet(notes, todayLabel) { showNotes = false }
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 28.dp,
        ),
    ) {
        // Boutons ronds : recherche et rappel
        staggerItem(0, seen) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(todayLabel, style = MaterialTheme.typography.bodySmall, color = InkSoft, modifier = Modifier.weight(1f))
                RoundIcon(tr("Rechercher", "Search", "Поиск"), onSearch) { c -> SearchGlyph(c) }
                Spacer(Modifier.width(10.dp))
                BellButton(notes.count { it.pending }) { showNotes = true }
            }
        }
        // Salut + avatar
        staggerItem(1, seen) {
            Row(Modifier.padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting.local.get(lang) + ",", style = MaterialTheme.typography.titleMedium, color = InkSoft)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(greeting.hanzi, fontFamily = Kai, fontSize = 38.sp, color = Ink)
                        Text("  " + greeting.pinyin, fontSize = 14.sp, color = InkSoft,
                            fontFamily = com.zhuzi.app.ui.theme.PinyinFont,
                            modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Text(tr("Un peu de chinois aujourd'hui ?", "A little Chinese today?", "Немного китайского сегодня?"),
                        style = MaterialTheme.typography.bodyMedium, color = InkSoft)
                }
                // Avatar : la mascotte dans un halo lavande ; la toucher ouvre « Mon avatar »
                Box(
                    Modifier.size(92.dp).clip(CircleShape)
                        .background(com.zhuzi.app.ui.theme.Mist)
                        .clickable(onClick = onAvatar)
                        .semantics { contentDescription = tr(lang, "Mon avatar", "My avatar", "Мой аватар") },
                    contentAlignment = Alignment.Center,
                ) {
                    PandaAvatar(look, com.zhuzi.app.data.levelFor(knownCount).number, Modifier.size(80.dp))
                }
            }
        }
        // Trois tuiles : découverts, bien retenus, série
        staggerItem(2, seen) {
            Row(Modifier.padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("字", Ink, knownCount, tr("Découverts", "Discovered", "Открыто"), Modifier.weight(1f))
                StatTile("记", Ink, masteredCount, tr("Retenus", "Learned", "Выучено"), Modifier.weight(1f))
                StatTile("天", Seal, streakDays,
                    tr("Série", "Streak", "Серия"),
                    Modifier.weight(1f))
            }
        }
        // Objectif du jour
        staggerItem(3, seen) { GoalCard(dueWords.size, dueTomorrow, knownCount, total, next, onReview, onOpen) }

        // Au programme
        staggerItem(4, seen) {
            Row(Modifier.padding(top = 28.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(tr("Au programme", "On the agenda", "План на сегодня"), style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f))
                Text(tr("Tout voir ›", "See all ›", "Все ›"), color = Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    modifier = Modifier.clickable(onClick = onSeeAll).padding(4.dp))
            }
        }
        staggerItem(5, seen) {
            TaskRow(
                done = dueWords.isEmpty() && knownCount > 0,
                title = tr("Révision du jour", "Today's review", "Повторение дня"),
                subtitle = if (dueWords.isNotEmpty()) wordsCount(lang, dueWords.size) + tr(" à revoir", " to review", " повторить")
                else tr("Tout est révisé", "All reviewed", "Всё повторено"),
                onClick = if (dueWords.isNotEmpty()) onReview else ({}),
            )
        }
        staggerItem(6, seen) {
            TaskRow(
                done = nextKnown >= next.words.size,
                title = tr("Leçon ", "Lesson ", "Урок ") + next.id + " · " + next.title(lang),
                subtitle = "$nextKnown / ${next.words.size} " + tr("mots découverts", "words discovered", "слов открыто"),
                onClick = { onOpen(next) },
            )
        }
        staggerItem(7, seen) {
            TaskRow(
                done = wordOfDayKnown,
                title = tr("Mot du jour : ", "Word of the day: ", "Слово дня: ") + wordOfDay.hanzi,
                subtitle = wordOfDay.pinyin + " · " + wordOfDay.meaning(lang),
                onClick = { speak(wordOfDay.hanzi) },
            )
        }
        staggerItem(8, seen) { WordOfDayCard(wordOfDay, speak) }
        staggerItem(9, seen) { ProverbCard(proverb, speak) }
    }
}

/** Petit bouton rond blanc avec un pictogramme dessiné. */
@Composable
private fun RoundIcon(label: String, onClick: () -> Unit, glyph: @Composable (Color) -> Unit) {
    val ink = Ink
    Box(
        Modifier.size(44.dp).softCard(CircleShape).clickable(onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) { glyph(ink) }
}

@Composable
private fun SearchGlyph(c: Color) {
    Canvas(Modifier.size(20.dp)) {
        val w = size.width
        drawCircle(c, radius = w * 0.32f, center = Offset(w * 0.42f, w * 0.42f), style = Stroke(w * 0.11f))
        drawLine(c, Offset(w * 0.66f, w * 0.66f), Offset(w * 0.92f, w * 0.92f), w * 0.12f, cap = StrokeCap.Round)
    }
}

/** Tuile chiffre : pastille colorée avec un caractère, le nombre, son libellé. */
@Composable
private fun StatTile(glyph: String, tint: Color, value: Int, label: String, modifier: Modifier) {
    Column(modifier.softCard(RoundedCornerShape(16.dp)).padding(14.dp)) {
        Box(Modifier.size(36.dp).clip(CircleShape)
            .background(if (tint == Seal) tint.copy(alpha = 0.12f) else com.zhuzi.app.ui.theme.Mist),
            contentAlignment = Alignment.Center) {
            Text(glyph, fontFamily = Kai, fontSize = 20.sp, color = tint)
        }
        CountUpText(value, Modifier.padding(top = 10.dp))
        Text(label, fontSize = 12.sp, color = InkSoft, maxLines = 1)
    }
}

/** La carte « Objectif du jour » : dégradé indigo → violet, bambou en filigrane, barre de progression. */
@Composable
private fun GoalCard(
    due: Int,
    dueTomorrow: Int,
    knownCount: Int,
    total: Int,
    next: Lesson,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
) {
    val lang = currentLang()
    val a = Accent
    val b = Accent2
    val percent = knownCount * 100 / total
    val action: () -> Unit = if (due > 0) onReview else ({ onOpen(next) })
    Box(
        Modifier.padding(top = 18.dp).fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = a.copy(alpha = 0.12f), spotColor = a.copy(alpha = 0.22f))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(a, b)))
            .clickable(onClick = action),
    ) {
        BambooArt(Modifier.align(Alignment.TopEnd).size(150.dp, 190.dp))
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pastille « cible »
                        Box(Modifier.size(22.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                        }
                        Text("  " + tr("Objectif du jour", "Today's goal", "Цель дня"), color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        when {
                            due > 0 -> wordsCount(lang, due) + tr(" à revoir", " to review", " повторить")
                            knownCount == 0 -> tr("Ta première leçon", "Your first lesson", "Твой первый урок")
                            else -> tr("Continue comme ça !", "Keep going!", "Так держать!")
                        },
                        color = Color.White, style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        when {
                            due > 0 -> tr("Quelques minutes suffisent.", "A few minutes is all it takes.", "Хватит нескольких минут.")
                            knownCount == 0 -> tr("Commence par dire bonjour : 你好.", "Start by saying hello: 你好.", "Начни с приветствия: 你好.")
                            dueTomorrow > 0 -> tr("Demain : ", "Tomorrow: ", "Завтра: ") + wordsCount(lang, dueTomorrow)
                            else -> tr("Prochaine étape : ", "Next up: ", "Дальше: ") + next.short(lang)
                        },
                        color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp),
                    )
                }
                // Flèche ronde
                Box(Modifier.size(46.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                    Text("›", color = a, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 3.dp))
                }
            }
            Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                ProgressBar(knownCount.toFloat() / total, Modifier.weight(1f), color = Color.White,
                    track = Color.White.copy(alpha = 0.28f), height = 8.dp)
                Text("  $percent %", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Text(tr("du HSK 1 découvert", "of HSK 1 discovered", "HSK 1 открыто"), color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/** Une ligne du programme : rond à cocher, titre, sous-titre, flèche. */
@Composable
private fun TaskRow(done: Boolean, title: String, subtitle: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.padding(bottom = 10.dp).fillMaxWidth().pressScale(interaction)
            .softCard(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (done) {
            Box(Modifier.size(28.dp).clip(CircleShape).background(Accent), contentAlignment = Alignment.Center) {
                Text("✓", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        } else {
            Box(Modifier.size(28.dp).clip(CircleShape).border(2.dp, Rule, CircleShape))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                color = if (done) InkSoft else Ink)
            Text(subtitle, fontSize = 13.sp, color = InkSoft, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                fontFamily = com.zhuzi.app.ui.theme.PinyinFont)
        }
        Text("›", fontSize = 22.sp, color = InkSoft)
    }
}

/** Le mot du jour, avec sa phrase d'exemple. */
@Composable
private fun WordOfDayCard(word: Word, speak: (String) -> Unit) {
    Column(Modifier.padding(top = 18.dp).fillMaxWidth().softCard().padding(20.dp)) {
        Text(tr("MOT DU JOUR", "WORD OF THE DAY", "СЛОВО ДНЯ"), style = Kicker)
        Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(140.dp)) { WordGrids(word.hanzi, maxSize = 68.dp, pinyin = word.pinyin) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                PinyinText(word.pinyin, 22.sp)
                Text(word.meaning(currentLang()), color = InkSoft, fontSize = 16.sp)
                ListenLink { speak(word.hanzi) }
            }
        }
        ExampleBlock(word, speak, Modifier.padding(top = 8.dp))
    }
}

/** Le proverbe du jour : une sagesse chinoise sur l'apprentissage (et le bambou). */
@Composable
private fun ProverbCard(proverb: Proverb, speak: (String) -> Unit) {
    Column(
        Modifier.padding(top = 18.dp).fillMaxWidth().softCard()
            .clickable { speak(proverb.hanzi) }.padding(22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("PROVERBE DU JOUR", "PROVERB OF THE DAY", "ПОСЛОВИЦА ДНЯ"), style = Kicker,
                modifier = Modifier.weight(1f))
            SealStamp("竹", 26.dp, animate = false)
        }
        Text(proverb.hanzi, fontFamily = Kai, fontSize = 32.sp, color = Ink, modifier = Modifier.padding(top = 12.dp))
        PinyinText(proverb.pinyin, 15.sp, fontWeight = FontWeight.Medium, maxLines = 2)
        Text(proverb.meaning.get(currentLang()), style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 10.dp))
        Text(tr("Touche pour l'écouter", "Tap to listen", "Нажми, чтобы послушать"), fontSize = 12.sp,
            color = InkSoft, modifier = Modifier.padding(top = 8.dp))
    }
}
