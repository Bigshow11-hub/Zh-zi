package com.zhuzi.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.zhuzi.app.ui.theme.Grid
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Paper

/** Coins presque droits, comme une feuille de cahier. */
val SquareShape = RoundedCornerShape(2.dp)

/**
 * Une case d'écriture : 田字格 (croix au centre) ou 米字格 (croix + diagonales),
 * avec un caractère au pinceau dedans.
 */
@Composable
fun CharGrid(char: String, size: Dp, modifier: Modifier = Modifier, diagonals: Boolean = false) {
    val density = LocalDensity.current
    val borderDp = if (size >= 120.dp) 2.dp else 1.5.dp
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
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
        // Le caractère occupe environ 78 % de la case
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
                // Cases jointives : on superpose les bords
                CharGrid(c, cell, Modifier.offset(x = -(1.5.dp * i)), diagonals)
            }
        }
    }
}

/** Bouton principal : encre pleine. */
@Composable
fun InkButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = SquareShape,
        colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Paper),
        modifier = modifier.height(52.dp),
    ) { Text(text) }
}

/** Bouton secondaire : contour encre. */
@Composable
fun InkOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = SquareShape,
        border = BorderStroke(1.5.dp, Ink),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink),
        modifier = modifier.height(52.dp),
    ) { Text(text) }
}
