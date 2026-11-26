package com.zhuzi.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.StrokeData
import com.zhuzi.app.data.Tones
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.GridLine
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.toneColor
import kotlinx.coroutines.delay
import kotlin.math.hypot

/** Le début d'une ligne médiane, jusqu'à la fraction donnée de sa longueur. */
private fun partialPath(points: List<Pair<Float, Float>>, fraction: Float): Path {
    val path = Path()
    if (points.isEmpty()) return path
    val segs = points.zipWithNext { a, b -> hypot(b.first - a.first, b.second - a.second) }
    var left = segs.sum() * fraction
    path.moveTo(points[0].first, points[0].second)
    for (i in segs.indices) {
        val (ax, ay) = points[i]
        val (bx, by) = points[i + 1]
        if (left >= segs[i]) {
            path.lineTo(bx, by); left -= segs[i]
        } else {
            val t = if (segs[i] == 0f) 0f else left / segs[i]
            path.lineTo(ax + (bx - ax) * t, ay + (by - ay) * t)
            break
        }
    }
    return path
}

/**
 * Animation de l'ordre des traits d'un caractère : chaque trait se dessine dans son sens,
 * le trait en cours en rouge, les traits finis à l'encre, ceux à venir en gris clair.
 * Le point rouge montre où poser le pinceau.
 */
@Composable
fun StrokeOrderView(char: String, size: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val data = remember(char) { StrokeData.get(context, char) }
    if (data == null) {
        CharGrid(char, size, modifier, diagonals = true)
        return
    }
    val outlines = remember(data) { data.outlines.map { PathParser().parsePathString(it).toPath() } }
    val medians = data.medians
    val lengths = remember(data) {
        medians.map { pts -> pts.zipWithNext { a, b -> hypot(b.first - a.first, b.second - a.second) }.sum() }
    }
    val n = outlines.size

    // done = nombre de traits finis ; progress = avancée du trait suivant ; run relance l'animation
    var done by remember(char) { mutableIntStateOf(0) }
    var run by remember(char) { mutableIntStateOf(1) }
    val progress = remember(char) { Animatable(0f) }
    LaunchedEffect(char, run) {
        if (run <= 0) { // pas à pas : on arrête l'animation automatique
            progress.snapTo(0f)
            return@LaunchedEffect
        }
        done = 0
        delay(250)
        for (i in 0 until n) {
            done = i
            progress.snapTo(0f)
            progress.animateTo(1f, tween((lengths[i] * 0.9f).toInt().coerceIn(260, 800), easing = FastOutSlowInEasing))
            delay(140)
        }
        done = n
        progress.snapTo(0f)
    }

    val ink = Ink
    val ghost = GridLine
    val guide = GridLine
    val paper = Paper
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(size)) {
            val w = this.size.width
            val r = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            val thin = 1.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))
            // Case 米字格
            drawRoundRect(paper, cornerRadius = r)
            drawLine(guide.copy(alpha = 0.6f), Offset(0f, 0f), Offset(w, w), thin, pathEffect = dash)
            drawLine(guide.copy(alpha = 0.6f), Offset(w, 0f), Offset(0f, w), thin, pathEffect = dash)
            drawLine(guide, Offset(w / 2, 0f), Offset(w / 2, w), thin, pathEffect = dash)
            drawLine(guide, Offset(0f, w / 2), Offset(w, w / 2), thin, pathEffect = dash)
            drawRoundRect(ink.copy(alpha = 0.28f), topLeft = Offset(0.8.dp.toPx(), 0.8.dp.toPx()),
                size = Size(w - 1.6.dp.toPx(), w - 1.6.dp.toPx()), cornerRadius = r, style = Stroke(1.6.dp.toPx()))

            val k = w / 1024f
            withTransform({
                translate(top = 900f * k)
                scale(k, -k, pivot = Offset.Zero)
            }) {
                outlines.forEachIndexed { i, p ->
                    when {
                        i < done -> drawPath(p, ink)
                        i == done && progress.value > 0f -> {
                            drawPath(p, ghost)
                            clipPath(p) {
                                drawPath(
                                    partialPath(medians[i], progress.value), Seal,
                                    style = Stroke(width = 190f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                                )
                            }
                        }
                        else -> drawPath(p, ghost)
                    }
                }
                // Point de départ du trait suivant
                if (done < n) {
                    val (sx, sy) = medians[done].first()
                    drawCircle(Seal, radius = 34f, center = Offset(sx, sy))
                    drawCircle(paper, radius = 14f, center = Offset(sx, sy))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (done >= n) countOf(currentLang(), n, "trait" to "traits", "stroke" to "strokes", Triple("черта", "черты", "черт"))
            else tr("Trait ${done + 1} sur $n", "Stroke ${done + 1} of $n", "Черта ${done + 1} из $n"),
            fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkSoft,
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallPill("‹ " + tr("Trait", "Stroke", "Черта")) {
                run = 0; done = (done - 1).coerceAtLeast(0)
            }
            SmallPill(tr("Rejouer", "Replay", "Повтор"), filled = true) { run = if (run > 0) run + 1 else 1 }
            SmallPill(tr("Trait", "Stroke", "Черта") + " ›") {
                run = 0; done = (done + 1).coerceAtMost(n)
            }
        }
    }
}

@Composable
private fun SmallPill(label: String, filled: Boolean = false, onClick: () -> Unit) {
    Box(
        Modifier.clip(PillShape)
            .background(if (filled) Ink else Paper)
            .border(1.5.dp, Ink, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = if (filled) Paper else Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
}

/** Lien souligné « Ordre des traits ». */
@Composable
fun StrokeLink(onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text(tr("Ordre des traits", "Stroke order", "Порядок черт"), color = Ink, fontWeight = FontWeight.Bold,
            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
    }
}

/** Panneau du bas avec l'animation des traits d'un mot (rien si word est null). */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StrokeSheet(word: Word?, onDismiss: () -> Unit) {
    if (word == null) return
    androidx.compose.material3.ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Paper) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WordGrids(word.hanzi, maxSize = 56.dp, pinyin = word.pinyin)
            Spacer(Modifier.height(6.dp))
            PinyinText(word.pinyin, 18.sp)
            Spacer(Modifier.height(18.dp))
            StrokePanel(word, size = 240.dp)
        }
    }
}

/** Les caractères d'un mot au pinceau, chacun à la couleur de son ton. */
@Composable
fun HanziText(word: Word, fontSize: androidx.compose.ui.unit.TextUnit) {
    val tones = Tones.forWord(word.hanzi, word.pinyin)
    val colors = tones.map { toneColor(it) }
    Row {
        word.hanzi.forEachIndexed { i, c ->
            Text(c.toString(), fontFamily = Kai, fontSize = fontSize, color = colors[i])
        }
    }
}

/** Ordre des traits d'un mot : choix du caractère, puis son animation. */
@Composable
fun StrokePanel(word: Word, modifier: Modifier = Modifier, size: Dp = 220.dp) {
    var index by remember(word) { mutableIntStateOf(0) }
    val chars = word.hanzi.map { it.toString() }
    val tones = Tones.forWord(word.hanzi, word.pinyin)
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(tr("ORDRE DES TRAITS", "STROKE ORDER", "ПОРЯДОК ЧЕРТ"), style = Kicker)
        if (chars.size > 1) {
            Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chars.forEachIndexed { i, c ->
                    val on = i == index
                    Box(
                        Modifier.size(44.dp).clip(PillShape).background(if (on) Ink else Mist)
                            .clickable { index = i },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(c, fontFamily = Kai, fontSize = 22.sp, color = if (on) Paper else toneColor(tones[i]))
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        StrokeOrderView(chars[index.coerceIn(0, chars.size - 1)], size)
    }
}
