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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.AccentFill
import kotlinx.coroutines.launch
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

/** Le début d'une ligne médiane, jusqu'à la fraction donnée de sa longueur (et le bout atteint). */
private fun partialPath(points: List<Pair<Float, Float>>, fraction: Float): Pair<Path, Offset> {
    val path = Path()
    if (points.isEmpty()) return path to Offset.Zero
    val segs = points.zipWithNext { a, b -> hypot(b.first - a.first, b.second - a.second) }
    var left = segs.sum() * fraction
    path.moveTo(points[0].first, points[0].second)
    var tip = Offset(points[0].first, points[0].second)
    for (i in segs.indices) {
        val (ax, ay) = points[i]
        val (bx, by) = points[i + 1]
        if (left >= segs[i]) {
            path.lineTo(bx, by); left -= segs[i]; tip = Offset(bx, by)
        } else {
            val t = if (segs[i] == 0f) 0f else left / segs[i]
            tip = Offset(ax + (bx - ax) * t, ay + (by - ay) * t)
            path.lineTo(tip.x, tip.y)
            break
        }
    }
    return path to tip
}

private enum class StrokeCmd { PLAY, PAUSE, NEXT, PREV }

/**
 * Animation de l'ordre des traits d'un caractère, comme un pinceau :
 * le trait en cours se dessine en rouge dans son sens (la pointe du pinceau le suit),
 * les traits finis passent à l'encre, ceux à venir restent en filigrane.
 * Commandes : trait précédent, lecture / pause / rejouer, trait suivant, vitesse.
 */
@Composable
fun StrokeOrderView(char: String, size: Dp, modifier: Modifier = Modifier, controls: Boolean = true) {
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

    var done by remember(char) { mutableIntStateOf(0) }          // traits finis
    var playing by remember(char) { mutableStateOf(false) }
    var slow by remember { mutableStateOf(false) }
    var cmd by remember(char) { mutableStateOf(StrokeCmd.PLAY) }
    var tick by remember(char) { mutableIntStateOf(0) }           // relance la commande
    val progress = remember(char) { Animatable(0f) }
    val appear = remember(char) { Animatable(0f) }
    val send: (StrokeCmd) -> Unit = { c -> cmd = c; tick++ }

    suspend fun drawStroke(i: Int) {
        val ms = ((lengths[i] * 0.95f).toInt().coerceIn(320, 900) * (if (slow) 1.9f else 1f)).toInt()
        progress.snapTo(0f)
        progress.animateTo(1f, tween(ms, easing = FastOutSlowInEasing))
        done = i + 1
        progress.snapTo(0f)
    }

    LaunchedEffect(char) { appear.animateTo(1f, tween(380, easing = FastOutSlowInEasing)) }
    LaunchedEffect(char, tick) {
        when (cmd) {
            StrokeCmd.PLAY -> {
                if (done >= n) done = 0
                playing = true
                delay(if (tick == 0) 450 else 150)
                while (done < n) {
                    drawStroke(done)
                    delay(if (slow) 260 else 130)
                }
                playing = false
            }
            StrokeCmd.PAUSE -> { playing = false; progress.snapTo(0f) }
            StrokeCmd.NEXT -> { playing = false; if (done < n) drawStroke(done) }
            StrokeCmd.PREV -> { playing = false; progress.snapTo(0f); done = (done - 1).coerceAtLeast(0) }
        }
    }

    val ink = Ink
    val ghost = GridLine
    val guide = GridLine
    val paper = Paper
    val accent = Accent
    val soft = Mist
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(size).graphicsLayer {
            alpha = appear.value
            val sc = 0.94f + 0.06f * appear.value
            scaleX = sc; scaleY = sc
        }) {
            val w = this.size.width
            val r = CornerRadius(14.dp.toPx(), 14.dp.toPx())
            val thin = 1.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(7f, 6f))
            // Case 米字格, douce
            drawRoundRect(paper, cornerRadius = r)
            drawLine(guide.copy(alpha = 0.55f), Offset(0f, 0f), Offset(w, w), thin, pathEffect = dash)
            drawLine(guide.copy(alpha = 0.55f), Offset(w, 0f), Offset(0f, w), thin, pathEffect = dash)
            drawLine(guide, Offset(w / 2, 0f), Offset(w / 2, w), thin, pathEffect = dash)
            drawLine(guide, Offset(0f, w / 2), Offset(w, w / 2), thin, pathEffect = dash)
            drawRoundRect(ink.copy(alpha = 0.16f), topLeft = Offset(0.75.dp.toPx(), 0.75.dp.toPx()),
                size = Size(w - 1.5.dp.toPx(), w - 1.5.dp.toPx()), cornerRadius = r, style = Stroke(1.5.dp.toPx()))

            val k = w * 0.9f / 1024f
            val pad = w * 0.05f
            withTransform({
                translate(left = pad, top = pad + 900f * k)
                scale(k, -k, pivot = Offset.Zero)
            }) {
                var tip: Offset? = null
                outlines.forEachIndexed { i, p ->
                    when {
                        i < done -> drawPath(p, ink)
                        i == done && progress.value > 0f -> {
                            drawPath(p, ghost.copy(alpha = 0.55f))
                            val (part, end) = partialPath(medians[i], progress.value)
                            tip = end
                            clipPath(p) {
                                drawPath(part, Seal, style = Stroke(width = 200f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                        }
                        else -> drawPath(p, ghost.copy(alpha = 0.55f))
                    }
                }
                // La pointe du pinceau pendant le tracé, sinon le point de départ du trait suivant
                tip?.let {
                    drawCircle(Seal.copy(alpha = 0.22f), radius = 58f, center = it)
                    drawCircle(Seal, radius = 26f, center = it)
                } ?: run {
                    if (done < n) {
                        val (sx, sy) = medians[done].first()
                        drawCircle(Seal.copy(alpha = 0.18f), radius = 52f, center = Offset(sx, sy))
                        drawCircle(Seal, radius = 26f, center = Offset(sx, sy))
                        drawCircle(paper, radius = 11f, center = Offset(sx, sy))
                    }
                }
            }
        }
        if (!controls) return@Column
        // Avancement : une petite barre par trait
        Spacer(Modifier.height(14.dp))
        Canvas(Modifier.width(size).height(5.dp)) {
            val gap = 3.dp.toPx()
            val seg = (this.size.width - gap * (n - 1)) / n
            val h = this.size.height
            for (i in 0 until n) {
                val x = i * (seg + gap)
                drawRoundRect(soft, Offset(x, 0f), Size(seg, h), CornerRadius(h / 2, h / 2))
                val f = when {
                    i < done -> 1f
                    i == done -> progress.value
                    else -> 0f
                }
                if (f > 0f) drawRoundRect(if (i < done) accent else Seal, Offset(x, 0f), Size(seg * f, h), CornerRadius(h / 2, h / 2))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            if (done >= n && !playing) countOf(currentLang(), n, "trait" to "traits", "stroke" to "strokes", Triple("черта", "черты", "черт"))
            else tr("Trait ${(done + 1).coerceAtMost(n)} sur $n", "Stroke ${(done + 1).coerceAtMost(n)} of $n",
                "Черта ${(done + 1).coerceAtMost(n)} из $n"),
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = InkSoft,
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            RoundIcon(tr("Trait précédent", "Previous stroke", "Предыдущая черта"), enabled = done > 0 || progress.value > 0f,
                onClick = { send(StrokeCmd.PREV) }) { tint -> ZIcon(Glyph.CHEVRON_LEFT, tint, size = 22.dp, weight = 1.4f) }
            PlayButton(playing = playing, finished = done >= n && !playing) {
                send(if (playing) StrokeCmd.PAUSE else StrokeCmd.PLAY)
            }
            RoundIcon(tr("Trait suivant", "Next stroke", "Следующая черта"), enabled = done < n,
                onClick = { send(StrokeCmd.NEXT) }) { tint -> ZIcon(Glyph.CHEVRON_RIGHT, tint, size = 22.dp, weight = 1.4f) }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            if (slow) tr("Vitesse : lente", "Speed: slow", "Скорость: медленно") else tr("Vitesse : normale", "Speed: normal", "Скорость: обычная"),
            fontSize = 13.sp, color = accent, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clip(PillShape).clickable { slow = !slow }.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

/** Bouton rond secondaire (précédent / suivant). */
@Composable
private fun RoundIcon(desc: String, enabled: Boolean, onClick: () -> Unit, icon: @Composable (Color) -> Unit) {
    val tint = if (enabled) Ink else InkSoft.copy(alpha = 0.4f)
    Box(
        Modifier.size(48.dp).clip(CircleShape).background(Mist)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center,
    ) { icon(tint) }
}

/** Gros bouton central : lecture, pause ou rejouer. */
@Composable
private fun PlayButton(playing: Boolean, finished: Boolean, onClick: () -> Unit) {
    val fill = AccentFill
    val desc = when {
        playing -> tr("Pause", "Pause", "Пауза")
        finished -> tr("Rejouer", "Replay", "Повтор")
        else -> tr("Lecture", "Play", "Воспроизвести")
    }
    val press = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Box(
        Modifier.size(60.dp).graphicsLayer { scaleX = press.value; scaleY = press.value }
            .clip(CircleShape).background(fill)
            .clickable {
                scope.launch { press.snapTo(0.9f); press.animateTo(1f, tween(220, easing = FastOutSlowInEasing)) }
                onClick()
            }
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(24.dp)) {
            val w = this.size.width
            val white = Color.White
            when {
                playing -> {
                    val bw = w * 0.22f
                    drawRoundRect(white, Offset(w * 0.2f, w * 0.12f), Size(bw, w * 0.76f), CornerRadius(bw / 3, bw / 3))
                    drawRoundRect(white, Offset(w * 0.58f, w * 0.12f), Size(bw, w * 0.76f), CornerRadius(bw / 3, bw / 3))
                }
                finished -> {
                    val st = Stroke(w * 0.13f, cap = StrokeCap.Round)
                    drawArc(white, -60f, 290f, false, Offset(w * 0.14f, w * 0.14f), Size(w * 0.72f, w * 0.72f), style = st)
                    val a = Path().apply {
                        moveTo(w * 0.62f, w * 0.02f); lineTo(w * 0.86f, w * 0.2f); lineTo(w * 0.58f, w * 0.32f); close()
                    }
                    drawPath(a, white)
                }
                else -> {
                    val t = Path().apply {
                        moveTo(w * 0.3f, w * 0.14f); lineTo(w * 0.86f, w * 0.5f); lineTo(w * 0.3f, w * 0.86f); close()
                    }
                    drawPath(t, white)
                }
            }
        }
    }
}

/** Lien « Ordre des traits » : pinceau + libellé en accent. */
@Composable
fun StrokeLink(onClick: () -> Unit) {
    ActionLink(tr("Ordre des traits", "Stroke order", "Порядок черт"), Glyph.PENCIL, onClick)
}

/** Panneau du bas avec l'animation des traits d'un mot (rien si word est null). */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun StrokeSheet(word: Word?, onDismiss: () -> Unit) {
    if (word == null) return
    val screen = androidx.compose.ui.platform.LocalConfiguration.current
    val grid = minOf(screen.screenWidthDp * 0.64f, screen.screenHeightDp * 0.34f, 280f).dp
    ZhuziSheet(onDismiss, sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // En-tête : le mot, sa prononciation et son sens
            Row(verticalAlignment = Alignment.CenterVertically) {
                HanziText(word, 30.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    PinyinText(word.pinyin, 17.sp)
                    Text(word.meaning(currentLang()), fontSize = 14.sp, color = InkSoft, maxLines = 1)
                }
            }
            Spacer(Modifier.height(18.dp))
            StrokePanel(word, size = grid)
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
        Text(tr("ORDRE DES TRAITS", "STROKE ORDER", "ПОРЯДОК ЧЕРТ"), style = Kicker, color = InkSoft)
        if (chars.size > 1) {
            Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chars.forEachIndexed { i, c ->
                    val on = i == index
                    val accent = Accent
                    Box(
                        Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                            .background(if (on) accent.copy(alpha = 0.12f) else Mist)
                            .border(if (on) 1.5.dp else 0.dp, if (on) accent else Color.Transparent, RoundedCornerShape(14.dp))
                            .clickable { index = i },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(c, fontFamily = Kai, fontSize = 24.sp, color = toneColor(tones[i]))
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        androidx.compose.animation.AnimatedContent(
            index.coerceIn(0, chars.size - 1),
            transitionSpec = {
                val dir = if (targetState > initialState) 1 else -1
                (androidx.compose.animation.fadeIn(tween(260)) + androidx.compose.animation.slideInHorizontally(tween(300)) { it / 6 * dir }) togetherWith
                    (androidx.compose.animation.fadeOut(tween(160)) + androidx.compose.animation.slideOutHorizontally(tween(260)) { -it / 6 * dir })
            },
            label = "strokeChar",
        ) { i -> StrokeOrderView(chars[i], size) }
    }
}
