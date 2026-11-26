package com.zhuzi.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R
import com.zhuzi.app.data.Greeting
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Proverb
import com.zhuzi.app.data.Tones
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.DeepInk
import com.zhuzi.app.ui.theme.DeepInk2
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.OnDeep
import com.zhuzi.app.ui.theme.OnDeepSoft
import com.zhuzi.app.ui.theme.PanelShape
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.TileShape
import com.zhuzi.app.ui.theme.toneColor

/** Onglet Accueil : salut, aujourd'hui, leçon à reprendre, mot du jour, proverbe du jour. */
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
    proverb: Proverb,
    speak: (String) -> Unit,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
    contentPadding: PaddingValues,
) {
    val lang = currentLang()
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 28.dp,
        ),
    ) {
        // En-tête : marque, salut selon l'heure (en chinois et dans ta langue), date, série
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.ic_logo_mark), contentDescription = null,
                    modifier = Modifier.size(46.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(greeting.hanzi, fontFamily = Kai, fontSize = 22.sp, color = Ink)
                        Text("  " + greeting.local.get(lang), style = MaterialTheme.typography.titleMedium)
                    }
                    Text(todayLabel, style = MaterialTheme.typography.bodySmall, color = InkSoft)
                }
                if (streakDays > 0) StreakBadge(streakDays)
            }
        }
        item {
            TodayCard(dueWords, dueTomorrow, knownCount, masteredCount, total, next, onReview, onOpen)
        }
        if (dueWords.isNotEmpty() || knownCount > 0) {
            item { ContinueCard(next, nextKnown, onOpen) }
        }
        item { WordOfDayCard(wordOfDay, speak) }
        item { ProverbCard(proverb, speak) }
    }
}

/** La carte « Aujourd'hui » : encre profonde, tiges de bambou en filigrane. */
@Composable
private fun TodayCard(
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    total: Int,
    next: Lesson,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
) {
    val lang = currentLang()
    Box(
        Modifier.padding(top = 20.dp).fillMaxWidth().clip(PanelShape)
            .background(Brush.linearGradient(listOf(DeepInk2, DeepInk))),
    ) {
        BambooArt(Modifier.align(Alignment.TopEnd).size(170.dp, 230.dp))
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(tr("AUJOURD'HUI", "TODAY", "СЕГОДНЯ"), style = Kicker, color = OnDeepSoft)
                    when {
                        dueWords.isNotEmpty() -> {
                            Text("${dueWords.size}", style = MaterialTheme.typography.displayLarge, color = OnDeep,
                                modifier = Modifier.offset(x = (-2).dp))
                            Text(tr(if (dueWords.size > 1) "mots à revoir" else "mot à revoir",
                                if (dueWords.size == 1) "word to review" else "words to review",
                                ruPlural(dueWords.size, "слово повторить", "слова повторить", "слов повторить")),
                                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = OnDeep)
                        }
                        knownCount == 0 -> Text(
                            tr("Tes premiers mots t'attendent", "Your first words are waiting", "Первые слова ждут тебя"),
                            style = MaterialTheme.typography.headlineMedium, color = OnDeep,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        else -> {
                            Text(tr("Tout est révisé", "All reviewed", "Всё повторено"),
                                style = MaterialTheme.typography.headlineMedium, color = OnDeep,
                                modifier = Modifier.padding(top = 6.dp))
                            Text(
                                if (dueTomorrow > 0) tr("Demain : ", "Tomorrow: ", "Завтра: ") + wordsCount(lang, dueTomorrow)
                                else tr("Profite d'une nouvelle leçon.", "Enjoy a new lesson.", "Время для нового урока."),
                                color = OnDeepSoft, modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }
                // Anneau : mots découverts sur le total
                Box(contentAlignment = Alignment.Center) {
                    ProgressRing(knownCount.toFloat() / total, 96.dp, color = OnDeep,
                        trackColor = OnDeep.copy(alpha = 0.16f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$knownCount", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = OnDeep)
                        Text(tr("sur", "of", "из") + " $total", fontSize = 11.sp, color = OnDeepSoft)
                    }
                }
            }

            if (dueWords.isNotEmpty()) {
                Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    val shown = dueWords.take(6)
                    shown.forEach { w ->
                        CharGrid(w.hanzi.take(1), 40.dp, Modifier.padding(end = 6.dp),
                            color = toneColor(Tones.forWord(w.hanzi, w.pinyin).first()))
                    }
                    if (dueWords.size > shown.size) {
                        Text("+${dueWords.size - shown.size}", color = OnDeepSoft, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (knownCount > 0) {
                Text("$knownCount " + tr("découverts", "discovered", "открыто") + " · $masteredCount " +
                    tr("bien retenus", "well learned", "хорошо выучено"),
                    style = MaterialTheme.typography.bodySmall, color = OnDeepSoft,
                    modifier = Modifier.padding(top = 14.dp))
            }
            Spacer(Modifier.height(18.dp))
            if (dueWords.isNotEmpty()) {
                LightButton(tr("Réviser maintenant", "Review now", "Повторить сейчас"), onReview, Modifier.fillMaxWidth())
            } else {
                LightButton(
                    if (knownCount == 0) tr("Commencer la leçon 1", "Start lesson 1", "Начать урок 1")
                    else tr("Continuer : ", "Continue: ", "Продолжить: ") + next.short(lang),
                    { onOpen(next) }, Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ContinueCard(lesson: Lesson, known: Int, onOpen: (Lesson) -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val lang = currentLang()
    Row(
        Modifier.padding(top = 12.dp).fillMaxWidth()
            .pressScale(interaction)
            .clip(TileShape).background(Paper).border(1.dp, Rule, TileShape)
            .clickable(interactionSource = interaction, indication = null) { onOpen(lesson) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharGrid(lesson.symbol, 52.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(tr("CONTINUER · LEÇON ", "CONTINUE · LESSON ", "ПРОДОЛЖИТЬ · УРОК ") + lesson.id, style = Kicker)
            Text(lesson.title(lang), fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
            ProgressBar(known.toFloat() / lesson.words.size, height = 6.dp)
        }
        Spacer(Modifier.width(12.dp))
        Text("›", fontSize = 28.sp, color = InkSoft)
    }
}

/** Un mot différent chaque jour, avec sa phrase d'exemple. */
@Composable
private fun WordOfDayCard(word: Word, speak: (String) -> Unit) {
    Column(Modifier.padding(top = 30.dp)) {
        Text(tr("MOT DU JOUR", "WORD OF THE DAY", "СЛОВО ДНЯ"), style = Kicker)
        Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(150.dp)) { WordGrids(word.hanzi, maxSize = 72.dp, pinyin = word.pinyin) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                PinyinText(word.pinyin, 22.sp)
                Text(word.meaning(currentLang()), color = InkSoft, fontSize = 16.sp)
                ListenLink { speak(word.hanzi) }
            }
        }
        ExampleBlock(word, speak, Modifier.padding(top = 10.dp))
    }
}

/** Le proverbe du jour : une sagesse chinoise sur l'apprentissage (et le bambou). */
@Composable
private fun ProverbCard(proverb: Proverb, speak: (String) -> Unit) {
    Column(
        Modifier.padding(top = 30.dp).fillMaxWidth().clip(PanelShape).border(1.dp, Rule, PanelShape)
            .clickable { speak(proverb.hanzi) }.padding(22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("PROVERBE DU JOUR", "PROVERB OF THE DAY", "ПОСЛОВИЦА ДНЯ"), style = Kicker,
                modifier = Modifier.weight(1f))
            SealStamp("竹", 26.dp, animate = false)
        }
        Text(proverb.hanzi, fontFamily = Kai, fontSize = 32.sp, color = Ink, modifier = Modifier.padding(top = 12.dp))
        PinyinText(proverb.pinyin, 15.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, maxLines = 2)
        Text(proverb.meaning.get(currentLang()), style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 10.dp))
        Text(tr("Touche pour l'écouter", "Tap to listen", "Нажми, чтобы послушать"), fontSize = 12.sp,
            color = InkSoft, modifier = Modifier.padding(top = 8.dp))
    }
}
