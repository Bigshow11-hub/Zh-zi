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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.heading
import com.zhuzi.app.ui.theme.AccentFill
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
    val list = androidx.compose.foundation.lazy.rememberLazyListState()
    // La barre compacte apparaît quand le grand titre (« Bonsoir ») a quitté l'écran
    val compact by remember {
        androidx.compose.runtime.derivedStateOf {
            list.firstVisibleItemIndex > 1 || (list.firstVisibleItemIndex == 1 && list.firstVisibleItemScrollOffset > 120)
        }
    }
    Box(Modifier.fillMaxSize()) {
    LazyColumn(
        state = list,
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = contentPadding.calculateTopPadding() + 4.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
    ) {
        // Barre d'outils : la date, recherche et notifications
        staggerItem(0, seen) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(todayLabel, style = MaterialTheme.typography.bodyMedium, color = InkSoft,
                    fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                val ink = Ink
                ToolbarButton(tr("Rechercher", "Search", "Поиск"), onSearch) { ZIcon(Glyph.SEARCH, ink, size = 21.dp, weight = 1.1f) }
                BellButton(notes.pending) { showNotes = true }
            }
        }
        // Grand titre : le salut ; à droite, la mascotte
        staggerItem(1, seen) {
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting.local.get(lang), style = MaterialTheme.typography.displaySmall, color = Ink,
                        modifier = Modifier.semantics { heading() })
                    Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.Bottom) {
                        Text(greeting.hanzi, fontFamily = Kai, fontSize = 26.sp, color = Ink)
                        Text("  " + greeting.pinyin, fontSize = 15.sp, color = InkSoft,
                            fontFamily = com.zhuzi.app.ui.theme.PinyinFont,
                            modifier = Modifier.padding(bottom = 4.dp))
                    }
                    Text(tr("Un peu de chinois aujourd'hui ?", "A little Chinese today?", "Немного китайского сегодня?"),
                        style = MaterialTheme.typography.bodyMedium, color = InkSoft, modifier = Modifier.padding(top = 2.dp))
                }
                // La mascotte ; la toucher ouvre « Mon avatar »
                Box(
                    Modifier.size(84.dp).clip(CircleShape)
                        .background(Paper)
                        .clickable(onClick = onAvatar)
                        .semantics { contentDescription = tr(lang, "Mon avatar", "My avatar", "Мой аватар") },
                    contentAlignment = Alignment.Center,
                ) {
                    PandaAvatar(look, com.zhuzi.app.data.levelFor(knownCount).number, Modifier.size(74.dp))
                }
            }
        }
        // Les trois chiffres, dans une seule carte, séparés par des filets
        staggerItem(2, seen) {
            StatsCard(knownCount, masteredCount, streakDays, Modifier.padding(top = 20.dp))
        }
        // Objectif du jour
        staggerItem(3, seen) { GoalCard(dueWords.size, dueTomorrow, knownCount, total, next, onReview, onOpen) }

        // Au programme : une liste groupée de trois lignes à cocher
        staggerItem(4, seen) {
            Column {
                SectionTitle(tr("Au programme", "On the agenda", "План на сегодня"),
                    action = tr("Tout voir", "See all", "Все"), onAction = onSeeAll)
                Column(Modifier.fillMaxWidth().clip(com.zhuzi.app.ui.theme.GroupShape).background(Paper)) {
                    TaskRow(
                        done = dueWords.isEmpty() && knownCount > 0,
                        title = tr("Révision du jour", "Today's review", "Повторение дня"),
                        subtitle = if (dueWords.isNotEmpty()) wordsCount(lang, dueWords.size) + tr(" à revoir", " to review", " повторить")
                        else tr("Tout est révisé", "All reviewed", "Всё повторено"),
                        onClick = if (dueWords.isNotEmpty()) onReview else null,
                    )
                    RowDivider(56.dp)
                    TaskRow(
                        done = nextKnown >= next.words.size,
                        title = lessonName(next) + " · " + next.title(lang),
                        subtitle = "$nextKnown / ${next.words.size} " + tr("mots découverts", "words discovered", "слов открыто"),
                        onClick = { onOpen(next) },
                    )
                    RowDivider(56.dp)
                    TaskRow(
                        done = wordOfDayKnown,
                        title = tr("Mot du jour : ", "Word of the day: ", "Слово дня: ") + wordOfDay.hanzi,
                        subtitle = wordOfDay.pinyin + " · " + wordOfDay.meaning(lang),
                        onClick = { speak(wordOfDay.hanzi) },
                    )
                }
            }
        }
        staggerItem(5, seen) {
            Column {
                SectionTitle(tr("Mot du jour", "Word of the day", "Слово дня"))
                WordOfDayCard(wordOfDay, speak)
            }
        }
        staggerItem(6, seen) {
            Column {
                SectionTitle(tr("Proverbe du jour", "Proverb of the day", "Пословица дня"))
                ProverbCard(proverb, speak)
            }
        }
    }
    CompactTitleBar(tr("Accueil", "Home", "Главная"), compact, contentPadding.calculateTopPadding())
    }
}

/** Titre de section (titre 3, 20 sp) avec, à droite, une action en accent (« Tout voir »). */
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, action: String? = null, onAction: () -> Unit = {}) {
    Row(modifier.fillMaxWidth().padding(top = 28.dp, bottom = 8.dp, start = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Ink, modifier = Modifier.weight(1f).semantics { heading() })
        action?.let {
            Text(it, color = Accent, style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clip(com.zhuzi.app.ui.theme.PillShape)
                    .clickable(role = androidx.compose.ui.semantics.Role.Button, onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 12.dp))
        }
    }
}

/** Les trois chiffres clés (découverts, retenus, série) dans une carte, séparés par des filets verticaux. */
@Composable
private fun StatsCard(known: Int, mastered: Int, streak: Int, modifier: Modifier = Modifier) {
    val line = Rule
    Row(
        modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(com.zhuzi.app.ui.theme.GroupShape).background(Paper)
            .padding(vertical = 14.dp),
    ) {
        val items = listOf(
            Triple(known, tr("Découverts", "Discovered", "Открыто"), Color.Unspecified),
            Triple(mastered, tr("Retenus", "Learned", "Выучено"), Color.Unspecified),
            Triple(streak, tr("Jours de série", "Day streak", "Дней подряд"), Seal),
        )
        items.forEachIndexed { i, (value, label, color) ->
            if (i > 0) Box(Modifier.width(1.dp).fillMaxHeight().padding(vertical = 6.dp)
                .drawBehind { drawLine(line, Offset(0f, 0f), Offset(0f, size.height), 1f) })
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                CountUpText(value, style = MaterialTheme.typography.headlineMedium, color = color)
                Text(label, style = MaterialTheme.typography.bodySmall, color = InkSoft, maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
    }
}

/**
 * La carte « Objectif du jour » : aplat d'accent aux coins continus, bambou en filigrane,
 * barre de progression. C'est la seule grande action de l'accueil.
 */
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
    val a = AccentFill
    val percent = knownCount * 100 / total
    val action: () -> Unit = if (due > 0) onReview else ({ onOpen(next) })
    val shape = com.zhuzi.app.ui.theme.SmoothShape(22.dp)
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier.padding(top = 16.dp).fillMaxWidth().pressScale(interaction)
            .shadow(10.dp, shape, ambientColor = a.copy(alpha = 0.10f), spotColor = a.copy(alpha = 0.28f))
            .clip(shape)
            // Un très léger dégradé vertical : la carte paraît éclairée par le haut
            .background(Brush.verticalGradient(listOf(lerp(a, Color.White, 0.08f), a)))
            .clickable(interactionSource = interaction, indication = null,
                role = androidx.compose.ui.semantics.Role.Button, onClick = action),
    ) {
        BambooArt(Modifier.align(Alignment.TopEnd).size(150.dp, 190.dp))
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(tr("Objectif du jour", "Today's goal", "Цель дня"), color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.titleSmall)
                    Text(
                        when {
                            due > 0 -> wordsCount(lang, due) + tr(" à revoir", " to review", " повторить")
                            knownCount == 0 -> tr("Ta première leçon", "Your first lesson", "Твой первый урок")
                            else -> tr("Continue comme ça !", "Keep going!", "Так держать!")
                        },
                        color = Color.White, style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        when {
                            due > 0 -> tr("Quelques minutes suffisent.", "A few minutes is all it takes.", "Хватит нескольких минут.")
                            knownCount == 0 -> tr("Commence par dire bonjour : 你好.", "Start by saying hello: 你好.", "Начни с приветствия: 你好.")
                            dueTomorrow > 0 -> tr("Demain : ", "Tomorrow: ", "Завтра: ") + wordsCount(lang, dueTomorrow)
                            else -> tr("Prochaine étape : ", "Next up: ", "Дальше: ") + next.short(lang)
                        },
                        color = Color.White.copy(alpha = 0.86f), style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                // Flèche ronde
                Box(Modifier.size(44.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                    ZIcon(Glyph.CHEVRON_RIGHT, a, size = 20.dp, weight = 1.6f, modifier = Modifier.offset(x = 1.dp))
                }
            }
            Row(Modifier.padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                ProgressBar(knownCount.toFloat() / total, Modifier.weight(1f), color = Color.White,
                    track = Color.White.copy(alpha = 0.26f), height = 6.dp)
                Text("  $percent %", color = Color.White, style = MaterialTheme.typography.labelMedium)
            }
            Text(tr("du vocabulaire HSK découvert", "of HSK vocabulary discovered", "словаря HSK открыто"),
                color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/** Une ligne du programme (dans la liste groupée) : rond à cocher, titre, sous-titre, chevron. */
@Composable
private fun TaskRow(done: Boolean, title: String, subtitle: String, onClick: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 60.dp)
            .then(if (onClick != null) Modifier.clickable(role = androidx.compose.ui.semantics.Role.Button, onClick = onClick) else Modifier)
            .padding(start = 16.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckCircle(done)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                color = if (done) InkSoft else Ink)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = InkSoft, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                fontFamily = com.zhuzi.app.ui.theme.PinyinFont)
        }
        if (onClick != null) {
            Spacer(Modifier.width(8.dp))
            Chevron()
        }
    }
}

/** Le mot du jour, avec sa phrase d'exemple. */
@Composable
private fun WordOfDayCard(word: Word, speak: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().softCard().padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(140.dp)) { WordGrids(word.hanzi, maxSize = 68.dp, pinyin = word.pinyin) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                PinyinText(word.pinyin, 22.sp)
                Text(word.meaning(currentLang()), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                ListenLink { speak(word.hanzi) }
            }
        }
        ExampleBlock(word, speak, Modifier.padding(top = 12.dp))
    }
}

/** Le proverbe du jour : une sagesse chinoise sur l'apprentissage (et le bambou). */
@Composable
private fun ProverbCard(proverb: Proverb, speak: (String) -> Unit) {
    Column(
        Modifier.fillMaxWidth().softCard()
            .clickable(onClickLabel = tr("Écouter", "Listen", "Слушать")) { speak(proverb.hanzi) }.padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(proverb.hanzi, fontFamily = Kai, fontSize = 32.sp, color = Ink, modifier = Modifier.weight(1f))
            SealStamp("竹", 26.dp, animate = false)
        }
        PinyinText(proverb.pinyin, 15.sp, fontWeight = FontWeight.Medium, maxLines = 2, modifier = Modifier.padding(top = 4.dp))
        Text(proverb.meaning.get(currentLang()), style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 10.dp))
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            ZIcon(Glyph.SPEAKER, InkSoft, size = 16.dp)
            Text("  " + tr("Touche pour l'écouter", "Tap to listen", "Нажми, чтобы послушать"),
                style = MaterialTheme.typography.bodySmall, color = InkSoft)
        }
    }
}
