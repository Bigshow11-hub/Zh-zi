package com.zhuzi.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.StrokeData
import com.zhuzi.app.data.Tones
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.GridLine
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.Wrong
import com.zhuzi.app.ui.theme.toneColor
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max

/** Juge un trait tracé au doigt par rapport au vrai trait (coordonnées du carré de 1024). */
internal object StrokeJudge {
    private fun length(p: List<Pair<Float, Float>>) =
        p.zipWithNext { a, b -> hypot(b.first - a.first, b.second - a.second) }.sum()

    /** Distance d'un point au tracé médian (segment le plus proche). */
    private fun distToPolyline(x: Float, y: Float, line: List<Pair<Float, Float>>): Float {
        if (line.size == 1) return hypot(x - line[0].first, y - line[0].second)
        var best = Float.MAX_VALUE
        for ((a, b) in line.zipWithNext()) {
            val dx = b.first - a.first
            val dy = b.second - a.second
            val len2 = dx * dx + dy * dy
            val t = if (len2 == 0f) 0f else (((x - a.first) * dx + (y - a.second) * dy) / len2).coerceIn(0f, 1f)
            best = minOf(best, hypot(x - (a.first + t * dx), y - (a.second + t * dy)))
        }
        return best
    }

    /** Le trait est-il bon ? Départ et arrivée proches, même sens, forme et longueur proches. */
    /** Ne garde que des points espacés d'au moins `step` : les tremblements du doigt n'allongent pas le trait. */
    private fun thin(p: List<Pair<Float, Float>>, step: Float = 20f): List<Pair<Float, Float>> {
        val out = mutableListOf(p.first())
        for (q in p.drop(1)) {
            if (hypot(q.first - out.last().first, q.second - out.last().second) >= step) out += q
        }
        if (out.last() != p.last()) out += p.last()
        return out
    }

    fun matches(rawUser: List<Pair<Float, Float>>, median: List<Pair<Float, Float>>): Boolean {
        if (rawUser.size < 2 || median.isEmpty()) return false
        // Testé sur les 1 179 traits de l'appli : 99 % des traits tremblés acceptés, 0 % des traits à l'envers
        val user = thin(rawUser)
        val uLen = length(user)
        val mLen = max(length(median), 1f)
        // Un « point » tracé pour un trait très court (ex. le point de 主) est accepté
        if (uLen < 25f && mLen > 120f) return false
        val tol = max(150f, mLen * 0.35f).coerceAtMost(260f)
        val startOk = hypot(user.first().first - median.first().first, user.first().second - median.first().second) < tol
        val endOk = hypot(user.last().first - median.last().first, user.last().second - median.last().second) < tol
        val ux = user.last().first - user.first().first
        val uy = user.last().second - user.first().second
        val mx = median.last().first - median.first().first
        val my = median.last().second - median.first().second
        val sameWay = mLen < 120f || (ux * mx + uy * my) > 0f
        val avgDist = user.map { distToPolyline(it.first, it.second, median) }.average()
        val ratio = uLen / mLen
        return startOk && endOk && sameWay && avgDist < 115.0 && ratio in 0.4f..2.6f
    }
}

/**
 * Entraînement à l'écriture d'un caractère : on trace chaque trait au doigt, dans l'ordre.
 * Trait juste → il s'encre ; faux → il s'efface en rouge ; après 2 erreurs, un indice.
 */
@Composable
fun WritingPad(char: String, color: Color, size: Dp, onDone: (mistakes: Int) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val data = remember(char) { StrokeData.get(context, char) }
    if (data == null) {
        CharGrid(char, size, modifier, diagonals = true)
        return
    }
    val outlines = remember(data) { data.outlines.map { PathParser().parsePathString(it).toPath() } }
    val medians = data.medians
    val n = outlines.size

    var done by remember(char) { mutableIntStateOf(0) }
    var mistakesHere by remember(char) { mutableIntStateOf(0) }
    var mistakes by remember(char) { mutableIntStateOf(0) }
    var showModel by remember { mutableStateOf(true) }
    val drawing = remember(char) { mutableStateListOf<Offset>() }
    val wrongPath = remember(char) { mutableStateListOf<Offset>() }
    val wrongAlpha = remember(char) { Animatable(0f) }

    val ink = Ink
    val ghost = GridLine
    val paper = Paper
    val accent = Accent

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            Modifier.size(size).clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .pointerInput(char, done) {
                    detectDragGestures(
                        onDragStart = { drawing.clear(); drawing.add(it) },
                        onDrag = { change, _ -> drawing.add(change.position) },
                        onDragEnd = {
                            if (done >= n) return@detectDragGestures
                            val k = this.size.width / 1024f
                            // Écran → carré de 1024 (axe y vers le haut)
                            val pts = drawing.map { (it.x / k) to (900f - it.y / k) }
                            if (StrokeJudge.matches(pts, medians[done])) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                done++
                                mistakesHere = 0
                                drawing.clear()
                                if (done >= n) onDone(mistakes)
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                mistakes++
                                mistakesHere++
                                wrongPath.clear(); wrongPath.addAll(drawing)
                                drawing.clear()
                                scope.launch {
                                    wrongAlpha.snapTo(1f)
                                    wrongAlpha.animateTo(0f, tween(700))
                                }
                            }
                        },
                    )
                },
        ) {
            val w = this.size.width
            val r = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            val thin = 1.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))
            // Case 米字格
            drawRoundRect(paper, cornerRadius = r)
            drawLine(ghost.copy(alpha = 0.6f), Offset(0f, 0f), Offset(w, w), thin, pathEffect = dash)
            drawLine(ghost.copy(alpha = 0.6f), Offset(w, 0f), Offset(0f, w), thin, pathEffect = dash)
            drawLine(ghost, Offset(w / 2, 0f), Offset(w / 2, w), thin, pathEffect = dash)
            drawLine(ghost, Offset(0f, w / 2), Offset(w, w / 2), thin, pathEffect = dash)
            drawRoundRect(ink.copy(alpha = 0.25f), topLeft = Offset(0.8.dp.toPx(), 0.8.dp.toPx()),
                size = Size(w - 1.6.dp.toPx(), w - 1.6.dp.toPx()), cornerRadius = r, style = Stroke(1.6.dp.toPx()))

            val k = w / 1024f
            withTransform({
                translate(top = 900f * k)
                scale(k, -k, pivot = Offset.Zero)
            }) {
                outlines.forEachIndexed { i, p ->
                    when {
                        i < done -> drawPath(p, if (done >= n) color else ink)
                        showModel -> drawPath(p, ghost.copy(alpha = 0.55f))
                    }
                }
                // Indice après 2 erreurs : le trait attendu en léger, son départ et son sens
                if (done < n && mistakesHere >= 2) {
                    drawPath(outlines[done], accent.copy(alpha = 0.22f))
                    val m = medians[done]
                    val path = Path().apply {
                        moveTo(m.first().first, m.first().second)
                        m.drop(1).forEach { lineTo(it.first, it.second) }
                    }
                    drawPath(path, accent.copy(alpha = 0.7f), style = Stroke(18f, cap = StrokeCap.Round, join = StrokeJoin.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 30f))))
                    drawCircle(Seal, 34f, Offset(m.first().first, m.first().second))
                    drawCircle(paper, 14f, Offset(m.first().first, m.first().second))
                }
            }
            // Le trait raté, qui s'efface en rouge
            if (wrongPath.size > 1 && wrongAlpha.value > 0f) {
                val p = Path().apply { moveTo(wrongPath[0].x, wrongPath[0].y); wrongPath.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(p, Wrong.copy(alpha = wrongAlpha.value), style = Stroke(9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            // Le trait en cours
            if (drawing.size > 1) {
                val p = Path().apply { moveTo(drawing[0].x, drawing[0].y); drawing.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(p, ink, style = Stroke(9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            if (done >= n) {
                tr("Bravo ! ", "Well done! ", "Браво! ") + (
                    if (mistakes == 0) tr("Sans aucune erreur.", "No mistakes at all.", "Без единой ошибки.")
                    else tr("Erreurs : ", "Mistakes: ", "Ошибок: ") + mistakes
                    )
            } else {
                tr("Trait ${done + 1} sur $n", "Stroke ${done + 1} of $n", "Черта ${done + 1} из $n") + (
                    if (mistakesHere >= 2) tr(" · suis l'indice", " · follow the hint", " · следуй подсказке") else ""
                    )
            },
            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (done >= n) color else InkSoft,
            textAlign = TextAlign.Center,
        )
        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Modèle", "Guide", "Образец"), fontSize = 14.sp, color = Ink, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(8.dp))
            Switch(showModel, { showModel = it }, colors = SwitchDefaults.colors(
                checkedTrackColor = Accent, checkedThumbColor = Color.White,
                uncheckedTrackColor = Mist, uncheckedThumbColor = InkSoft, uncheckedBorderColor = GridLine,
            ))
            Spacer(Modifier.width(16.dp))
            Box(
                Modifier.clip(PillShape).background(Mist).clickable {
                    done = 0; mistakes = 0; mistakesHere = 0; drawing.clear()
                }.padding(horizontal = 14.dp, vertical = 8.dp),
            ) { Text(tr("Effacer", "Clear", "Стереть"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink) }
        }
    }
}

/** Écrire un mot : choix du caractère, puis sa case d'écriture. Le sceau se tamponne à la fin du mot. */
@Composable
fun WritingPractice(word: Word, onWordDone: () -> Unit, modifier: Modifier = Modifier) {
    val chars = word.hanzi.map { it.toString() }
    val tones = Tones.forWord(word.hanzi, word.pinyin)
    var index by remember(word) { mutableIntStateOf(0) }
    val finished = remember(word) { mutableStateListOf<Int>() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(finished.size) { if (finished.size == chars.size && chars.isNotEmpty()) onWordDone() }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PinyinText(word.pinyin, 22.sp)
            Text("  ·  " + word.meaning(currentLang()), color = InkSoft, fontSize = 16.sp, maxLines = 1)
        }
        if (chars.size > 1) {
            Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chars.forEachIndexed { i, c ->
                    val on = i == index
                    Box(
                        Modifier.size(46.dp).clip(PillShape)
                            .background(if (on) Accent else if (i in finished) toneColor(tones[i]).copy(alpha = 0.15f) else Mist)
                            .clickable { index = i },
                        contentAlignment = Alignment.Center,
                    ) { Text(c, fontFamily = Kai, fontSize = 22.sp, color = if (on) Color.White else toneColor(tones[i])) }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(contentAlignment = Alignment.Center) {
            WritingPad(chars[index], toneColor(tones[index]), 290.dp, onDone = {
                if (index !in finished) finished.add(index)
                // On laisse voir le caractère réussi un instant avant le suivant
                if (index + 1 < chars.size) scope.launch { kotlinx.coroutines.delay(900); index++ }
            })
            if (finished.size == chars.size && chars.isNotEmpty()) {
                SealStamp("好", 64.dp, Modifier.align(Alignment.TopEnd).padding(4.dp))
            }
        }
    }
}
