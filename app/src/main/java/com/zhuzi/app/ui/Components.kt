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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.zhuzi.app.ui.theme.Grid
import com.zhuzi.app.ui.theme.Ink
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
 * Une case d'écriture : 田字格 (croix au centre) ou 米字格 (croix + diagonales),
 * avec un caractère au pinceau dedans.
 */
@Composable
fun CharGrid(char: String, size: Dp, modifier: Modifier = Modifier, diagonals: Boolean = false) {
    val density = LocalDensity.current
    val borderDp = if (size >= 120.dp) 2.dp else 1.5.dp
    Box(modifier.size(size).background(Paper), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val border = borderDp.toPx()
            val thin = 1.dp.toPx()
            val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))
            val w = this.size.width
            val h = this.size.height
            if (diagonals) {
                drawLine(Grid.copy(alpha = 0.3f), Offset(0f, 0f), Offset(w, h), thin, pathEffect = dash)
                drawLine(Grid.copy(alpha = 0.3f), Offset(w, 0f), Offset(0f, h), thin, pathEffect = dash)
            }
            drawLine(Grid.copy(alpha = 0.55f), Offset(w / 2, 0f), Offset(w / 2, h), thin, pathEffect = dash)
            drawLine(Grid.copy(alpha = 0.55f), Offset(0f, h / 2), Offset(w, h / 2), thin, pathEffect = dash)
            drawRect(Grid, style = Stroke(border), topLeft = Offset(border / 2, border / 2),
                size = this.size.copy(w - border, h - border))
        }
        Text(
            char,
            fontFamily = Kai,
            color = Ink,
            fontSize = with(density) { (size * 0.78f).toSp() },
            // La police pinceau est un peu basse : on remonte légèrement le caractère
            modifier = Modifier.offset(y = -size * 0.02f),
        )
    }
}

/** Un mot entier : une case par caractère, côte à côte (cases jointives). */
@Composable
fun WordGrids(word: String, maxSize: Dp, modifier: Modifier = Modifier, diagonals: Boolean = false) {
    val chars = word.map { it.toString() }
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val cell = min(maxSize, maxWidth / chars.size)
        Row {
            chars.forEachIndexed { i, c ->
                CharGrid(c, cell, Modifier.offset(x = -(1.5.dp * i)), diagonals)
            }
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
