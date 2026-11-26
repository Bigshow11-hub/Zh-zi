package com.zhuzi.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Tones
import com.zhuzi.app.ui.theme.GridLine
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.toneColor
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.OnSeal
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal

/** Coins presque droits, comme une feuille de cahier (cases d'écriture seulement). */
val SquareShape = RoundedCornerShape(2.dp)

/** Ressort commun : vif, à peine rebondi. */
fun <T> zhuziSpring() = spring<T>(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)

/**
 * Cases d'écriture d'un mot : un seul cadre aux coins doux, une case par caractère,
 * guides 田字格 (croix) ou 米字格 (croix + diagonales) en tirets gris discrets.
 * Le caractère, au pinceau, porte la couleur de son ton.
 */
@Composable
private fun GridRow(
    chars: List<String>,
    cell: Dp,
    colors: List<Color>,
    diagonals: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val guide = GridLine
    val frame = Ink.copy(alpha = 0.28f)
    val paper = Paper
    val n = chars.size
    Box(modifier.size(cell * n, cell)) {
        Canvas(Modifier.fillMaxSize()) {
            val c = this.size.height
            val border = (if (cell >= 100.dp) 1.6.dp else 1.2.dp).toPx()
            val thin = 0.9.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(5f, 4f))
            val r = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            drawRoundRect(paper, cornerRadius = r)
            for (i in 0 until n) {
                val x0 = i * c
                if (diagonals) {
                    drawLine(guide.copy(alpha = 0.6f), Offset(x0, 0f), Offset(x0 + c, c), thin, pathEffect = dash)
                    drawLine(guide.copy(alpha = 0.6f), Offset(x0 + c, 0f), Offset(x0, c), thin, pathEffect = dash)
                }
                drawLine(guide, Offset(x0 + c / 2, 0f), Offset(x0 + c / 2, c), thin, pathEffect = dash)
                drawLine(guide, Offset(x0, c / 2), Offset(x0 + c, c / 2), thin, pathEffect = dash)
                if (i > 0) drawLine(frame, Offset(x0, 0f), Offset(x0, c), border * 0.8f)
            }
            drawRoundRect(
                frame, topLeft = Offset(border / 2, border / 2),
                size = Size(this.size.width - border, this.size.height - border),
                cornerRadius = r, style = Stroke(border),
            )
        }
        Row {
            chars.forEachIndexed { i, ch ->
                Box(Modifier.size(cell), contentAlignment = Alignment.Center) {
                    Text(
                        ch,
                        fontFamily = Kai,
                        color = colors[i],
                        fontSize = with(density) { (cell * 0.76f).toSp() },
                        // La police pinceau est un peu basse : on remonte légèrement le caractère
                        modifier = Modifier.offset(y = -cell * 0.02f),
                    )
                }
            }
        }
    }
}

/** Une seule case d'écriture (symbole de leçon, aperçu). */
@Composable
fun CharGrid(
    char: String,
    size: Dp,
    modifier: Modifier = Modifier,
    diagonals: Boolean = false,
    color: Color = Ink,
) = GridRow(listOf(char), size, listOf(color), diagonals, modifier)

/** Un mot entier dans ses cases ; avec le pinyin, chaque caractère prend la couleur de son ton. */
@Composable
fun WordGrids(
    word: String,
    maxSize: Dp,
    modifier: Modifier = Modifier,
    diagonals: Boolean = false,
    pinyin: String? = null,
) {
    val chars = word.map { it.toString() }
    val colors = if (pinyin != null) {
        Tones.forWord(word, pinyin).map { toneColor(it) }
    } else {
        chars.map { Ink }
    }
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val cell = min(maxSize, maxWidth / chars.size)
        GridRow(chars, cell, colors, diagonals)
    }
}

/** Le pinyin, chaque syllabe à la couleur de son ton. */
@Composable
fun PinyinText(
    pinyin: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign? = null,
) {
    val pieces = Tones.pieces(pinyin)
    val colors = pieces.map { (_, t) -> if (t == null) Ink else toneColor(t) }
    val text = buildAnnotatedString {
        pieces.forEachIndexed { i, (s, _) -> withStyle(SpanStyle(color = colors[i])) { append(s) } }
    }
    Text(text, modifier = modifier, fontSize = fontSize, fontWeight = fontWeight, textAlign = textAlign,
        maxLines = 1, overflow = TextOverflow.Ellipsis)
}

/** Légende des couleurs de tons. */
@Composable
fun ToneLegend(modifier: Modifier = Modifier) {
    val items = listOf("ā 1er" to 1, "á 2e" to 2, "ǎ 3e" to 3, "à 4e" to 4, "a neutre" to 5)
    Row(modifier, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text("Tons", fontSize = 12.sp, color = InkSoft, fontWeight = FontWeight.Bold)
        items.forEach { (label, t) ->
            Text(label, fontSize = 12.sp, color = toneColor(t), fontWeight = FontWeight.Bold)
        }
    }
}

/** Petit enfoncement à ressort quand on appuie (boutons, tuiles). */
@Composable
fun Modifier.pressScale(interaction: MutableInteractionSource): Modifier {
    val pressed by interaction.collectIsPressedAsState()
    val s by animateFloatAsState(if (pressed) 0.96f else 1f, zhuziSpring(), label = "press")
    return this.scale(s)
}

/** Bouton principal : encre pleine, en pilule. */
@Composable
fun InkButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val interaction = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        interactionSource = interaction,
        colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Paper),
        modifier = modifier.height(56.dp).pressScale(interaction),
    ) { Text(text, style = androidx.compose.material3.MaterialTheme.typography.labelLarge) }
}

/** Bouton secondaire : contour encre, en pilule. */
@Composable
fun InkOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = onClick,
        shape = PillShape,
        interactionSource = interaction,
        border = BorderStroke(1.5.dp, Ink),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink),
        modifier = modifier.height(56.dp).pressScale(interaction),
    ) { Text(text, style = androidx.compose.material3.MaterialTheme.typography.labelLarge) }
}

/** Barre de progression arrondie, qui glisse jusqu'à sa valeur. */
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Ink,
    track: Color = Rule,
    height: Dp = 8.dp,
) {
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), zhuziSpring(), label = "progress")
    Canvas(modifier.fillMaxWidth().height(height)) {
        val r = CornerRadius(size.height / 2, size.height / 2)
        drawRoundRect(track, cornerRadius = r)
        if (p > 0f) {
            drawRoundRect(color, size = size.copy(width = (size.width * p).coerceAtLeast(size.height)),
                cornerRadius = r)
        }
    }
}

/** Anneau de progression (mots découverts sur le total). */
@Composable
fun ProgressRing(progress: Float, size: Dp, modifier: Modifier = Modifier, stroke: Dp = 8.dp) {
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), tween(900), label = "ring")
    val track = Rule
    val ink = Ink
    Canvas(modifier.size(size)) {
        val w = stroke.toPx()
        val inset = w / 2
        val arcSize = this.size.copy(this.size.width - w, this.size.height - w)
        drawArc(track, -90f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(w))
        if (p > 0f) {
            drawArc(ink, -90f, 360f * p, false, Offset(inset, inset), arcSize,
                style = Stroke(w, cap = StrokeCap.Round))
        }
    }
}

/**
 * Le sceau rouge qui se tamponne : il arrive grand et transparent, puis s'écrase
 * en place avec un ressort, légèrement penché, comme un vrai tampon.
 */
@Composable
fun SealStamp(char: String, size: Dp, modifier: Modifier = Modifier, animate: Boolean = true) {
    val density = LocalDensity.current
    val stampScale = remember { Animatable(if (animate) 1.8f else 1f) }
    val stampAlpha = remember { Animatable(if (animate) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (animate) {
            stampAlpha.animateTo(1f, tween(120))
        }
    }
    LaunchedEffect(Unit) {
        if (animate) {
            stampScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
    }
    Box(
        modifier
            .size(size)
            .graphicsLayer {
                scaleX = stampScale.value; scaleY = stampScale.value
                alpha = stampAlpha.value
                rotationZ = -6f
            }
            .clip(RoundedCornerShape(size * 0.08f))
            .background(Seal)
            .border(size * 0.035f, OnSeal.copy(alpha = 0.85f), RoundedCornerShape(size * 0.06f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            char,
            fontFamily = Kai,
            color = OnSeal,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { (size * 0.62f).toSp() },
        )
    }
}

/** Pastille de série : un petit sceau rouge avec le nombre de jours. */
@Composable
fun StreakBadge(days: Int, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(30.dp).graphicsLayer { rotationZ = -6f }
                .clip(RoundedCornerShape(3.dp)).background(Seal),
            contentAlignment = Alignment.Center,
        ) { Text("$days", color = OnSeal, fontWeight = FontWeight.Black, fontSize = 14.sp) }
        Text(if (days > 1) "  jours d'affilée" else "  jour", color = Ink,
            fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
