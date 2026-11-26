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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.QuizMode
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.data.lessonIntros
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Accent2
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.PillShape

/** Une couleur par leçon, pour la pastille du caractère symbole. */
private val lessonTints = listOf(
    Color(0xFF5B5CF0), Color(0xFF2BB59A), Color(0xFFFF7A66), Color(0xFF4C8DF6),
    Color(0xFFE0A21B), Color(0xFF8B7BF7), Color(0xFFE0567A),
)

/** Onglet Leçons : synthèse du parcours, puis une carte par leçon. */
@Composable
fun LessonsScreen(
    bestScore: (Int, QuizMode) -> Int,
    lessonKnown: (Lesson) -> Int,
    onOpen: (Lesson) -> Unit,
    contentPadding: PaddingValues,
) {
    val lang = currentLang()
    val total = hsk1Lessons.sumOf { it.words.size }
    val known = hsk1Lessons.sumOf { lessonKnown(it) }
    val mastered: (Lesson) -> Boolean = { l -> QuizMode.entries.all { bestScore(l.id, it) >= 80 } }
    val masteredCount = hsk1Lessons.count(mastered)
    val current = hsk1Lessons.firstOrNull { lessonKnown(it) < it.words.size }
    val seen = remember { mutableSetOf<Any>() }
    LazyColumn(
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        staggerItem(0, seen) {
            ScreenTitle(tr("Leçons", "Lessons", "Уроки"),
                tr("Le parcours HSK 1, thème par thème", "The HSK 1 path, theme by theme", "Путь HSK 1, тема за темой"))
        }
        // Synthèse du parcours
        staggerItem(1, seen) {
            val a = Accent
            val b = Accent2
            Column(
                Modifier.fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(26.dp), ambientColor = a.copy(alpha = 0.3f), spotColor = a.copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Brush.linearGradient(listOf(a, b)))
                    .padding(20.dp),
            ) {
                Text(tr("PARCOURS HSK 1", "HSK 1 PATH", "ПУТЬ HSK 1"), color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, letterSpacing = 1.6.sp)
                Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.Bottom) {
                    Text("$known", color = Color.White, style = MaterialTheme.typography.displaySmall)
                    Text(" / $total " + tr("mots", "words", "слов"), color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                }
                ProgressBar(known.toFloat() / total, Modifier.padding(top = 10.dp), color = Color.White,
                    track = Color.White.copy(alpha = 0.28f), height = 8.dp)
                Text(
                    tr("$masteredCount leçon${if (masteredCount > 1) "s" else ""} maîtrisée${if (masteredCount > 1) "s" else ""} sur ${hsk1Lessons.size}",
                        "$masteredCount of ${hsk1Lessons.size} lessons mastered",
                        "Освоено уроков: $masteredCount из ${hsk1Lessons.size}"),
                    color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        hsk1Lessons.forEachIndexed { i, lesson ->
            staggerItem(i + 2, seen) {
                LessonCard(lesson, lessonKnown(lesson), mastered(lesson), lesson == current, bestScore, onOpen)
            }
        }
    }
}

/** Titre d'onglet : grand titre et sous-titre. */
@Composable
fun ScreenTitle(title: String, subtitle: String) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = InkSoft,
            modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun LessonCard(
    lesson: Lesson,
    known: Int,
    isMastered: Boolean,
    isCurrent: Boolean,
    bestScore: (Int, QuizMode) -> Int,
    onOpen: (Lesson) -> Unit,
) {
    val lang = currentLang()
    val interaction = remember { MutableInteractionSource() }
    val tint = lessonTints[(lesson.id - 1) % lessonTints.size]
    val shape = RoundedCornerShape(24.dp)
    val complete = known >= lesson.words.size
    Column(
        Modifier.fillMaxWidth()
            .pressScale(interaction)
            .softCard(shape)
            .then(if (isCurrent) Modifier.border(2.dp, Accent, shape) else Modifier)
            .clickable(interactionSource = interaction, indication = null) { onOpen(lesson) }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Grande pastille colorée avec le caractère symbole
            // Pastille du caractère, entourée d'un anneau de progression de la leçon
            Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
                ProgressRing(known.toFloat() / lesson.words.size, 68.dp, stroke = 5.dp, color = tint,
                    trackColor = tint.copy(alpha = 0.15f))
                Box(
                    Modifier.size(52.dp).clip(androidx.compose.foundation.shape.CircleShape).background(tint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) { Text(lesson.symbol, fontFamily = Kai, fontSize = 30.sp, color = tint) }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("LEÇON ", "LESSON ", "УРОК ") + lesson.id.toString().padStart(2, '0'),
                        fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp, color = tint,
                        modifier = Modifier.weight(1f))
                    when {
                        isMastered -> SealStamp("记", 24.dp, animate = false)
                        isCurrent -> Badge(tr("En cours", "In progress", "В процессе"), Accent)
                        complete -> Badge(tr("Découverte", "Discovered", "Пройден"), InkSoft)
                    }
                }
                Text(lesson.title(lang), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                lessonIntros[lesson.id]?.let {
                    Text(it.get(lang), fontSize = 13.sp, color = InkSoft, maxLines = 2, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
        Row(Modifier.padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            ProgressBar(known.toFloat() / lesson.words.size, Modifier.weight(1f), color = tint, height = 6.dp)
            Text("  $known/${lesson.words.size}", fontSize = 12.sp, color = InkSoft, fontWeight = FontWeight.Bold)
        }
        // Les trois quiz, en pilules : pleines quand le score atteint 80 %
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            QuizMode.entries.forEach { m ->
                val done = bestScore(lesson.id, m) >= 80
                Box(
                    Modifier.clip(PillShape).background(if (done) tint else Mist)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text((if (done) "✓ " else "") + m.label(lang), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = if (done) Color.White else InkSoft)
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Box(Modifier.clip(PillShape).background(color.copy(alpha = 0.13f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
