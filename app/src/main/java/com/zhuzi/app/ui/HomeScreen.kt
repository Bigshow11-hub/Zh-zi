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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.PanelShape
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.TileShape

/** Onglet Accueil : aujourd'hui, la leçon à reprendre, le mot du jour. */
@Composable
fun HomeScreen(
    todayLabel: String,
    streakDays: Int,
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    total: Int,
    next: Lesson,
    nextKnown: Int,
    wordOfDay: Word,
    speak: (String) -> Unit,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
    ) {
        // En-tête : marque, date, série
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.ic_logo_mark), contentDescription = null,
                    modifier = Modifier.size(44.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Zhúzi", style = MaterialTheme.typography.titleLarge)
                    Text(todayLabel, style = MaterialTheme.typography.bodySmall, color = InkSoft)
                }
                if (streakDays > 0) StreakBadge(streakDays)
            }
        }
        item {
            TodayPanel(dueWords, dueTomorrow, knownCount, masteredCount, total, next, onReview, onOpen)
        }
        if (dueWords.isNotEmpty() || knownCount > 0) {
            item { ContinueCard(next, nextKnown, onOpen) }
        }
        item { WordOfDayCard(wordOfDay, speak) }
    }
}

@Composable
private fun TodayPanel(
    dueWords: List<Word>,
    dueTomorrow: Int,
    knownCount: Int,
    masteredCount: Int,
    total: Int,
    next: Lesson,
    onReview: () -> Unit,
    onOpen: (Lesson) -> Unit,
) {
    Column(
        Modifier.padding(top = 20.dp).fillMaxWidth().clip(PanelShape).background(Mist).padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("AUJOURD'HUI", style = Kicker)
                when {
                    dueWords.isNotEmpty() -> {
                        Text("${dueWords.size}", style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.offset(x = (-2).dp))
                        Text(if (dueWords.size > 1) "mots à revoir" else "mot à revoir",
                            fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    knownCount == 0 -> {
                        Text("Tes premiers mots t'attendent", style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 6.dp))
                    }
                    else -> {
                        Text("Tout est révisé", style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 6.dp))
                        if (dueTomorrow > 0) {
                            Text("Demain : $dueTomorrow mot${if (dueTomorrow > 1) "s" else ""} à revoir",
                                color = InkSoft, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
            Box(contentAlignment = Alignment.Center) {
                ProgressRing(knownCount.toFloat() / total, 96.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$knownCount", fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text("sur $total", fontSize = 11.sp, color = InkSoft)
                }
            }
        }

        if (dueWords.isNotEmpty()) {
            Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                val shown = dueWords.take(6)
                shown.forEachIndexed { i, w ->
                    CharGrid(w.hanzi.take(1), 40.dp, Modifier.offset(x = -(1.5.dp * i)))
                }
                if (dueWords.size > shown.size) {
                    Text("+${dueWords.size - shown.size}", color = InkSoft, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        if (knownCount > 0) {
            Text("$knownCount découverts · $masteredCount bien retenus",
                style = MaterialTheme.typography.bodySmall, color = InkSoft,
                modifier = Modifier.padding(top = 14.dp))
        }
        Spacer(Modifier.height(16.dp))
        if (dueWords.isNotEmpty()) {
            InkButton("Réviser maintenant", onReview, Modifier.fillMaxWidth())
        } else {
            InkButton(
                if (knownCount == 0) "Commencer la leçon 1" else "Continuer : ${next.short}",
                { onOpen(next) }, Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ContinueCard(lesson: Lesson, known: Int, onOpen: (Lesson) -> Unit) {
    val interaction = remember { MutableInteractionSource() }
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
            Text("CONTINUER · LEÇON ${lesson.id}", style = Kicker)
            Text(lesson.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
            ProgressBar(known.toFloat() / lesson.words.size, height = 6.dp)
        }
        Spacer(Modifier.width(12.dp))
        Text("›", fontSize = 28.sp, color = InkSoft)
    }
}

/** Un mot différent chaque jour, à découvrir en passant. */
@Composable
private fun WordOfDayCard(word: Word, speak: (String) -> Unit) {
    Column(Modifier.padding(top = 28.dp)) {
        Text("MOT DU JOUR", style = Kicker)
        Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(150.dp)) { WordGrids(word.hanzi, maxSize = 72.dp) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(word.pinyin, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(word.french, color = InkSoft, fontSize = 16.sp)
                ListenLink { speak(word.hanzi) }
            }
        }
    }
}
