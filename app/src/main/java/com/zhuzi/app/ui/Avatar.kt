package com.zhuzi.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.runtime.Composable
import com.zhuzi.app.data.Accessory
import com.zhuzi.app.data.AvatarLook
import com.zhuzi.app.data.backgrounds
import com.zhuzi.app.data.outfitColors

private val FurDark = Color(0xFF1C1D28)
private val FurDarkLight = Color(0xFF4B4C60)
private val Bamboo = Color(0xFF3FA65A)
private val BambooDark = Color(0xFF26743B)

/**
 * La mascotte : un petit panda en pull à capuche, dessiné avec des dégradés, des reflets et une ombre
 * pour un rendu « pâte à modeler 3D ». Il tient une tige de bambou qui grandit avec le niveau (1 à 5).
 */
@Composable
fun PandaAvatar(look: AvatarLook, level: Int, modifier: Modifier = Modifier, withBackground: Boolean = true) {
    Canvas(modifier) {
        val u = size.minDimension / 100f
        fun o(x: Float, y: Float) = Offset(x * u, y * u)
        fun s(w: Float, h: Float) = Size(w * u, h * u)
        val outfit = Color(outfitColors[look.outfit])
        val outfitLight = lerp(outfit, Color.White, 0.35f)
        val outfitDark = lerp(outfit, Color.Black, 0.28f)

        // Fond rond en dégradé
        if (withBackground) {
            val (a, b) = backgrounds[look.background]
            drawCircle(Brush.linearGradient(listOf(Color(a), Color(b)), start = o(15f, 0f), end = o(85f, 100f)),
                radius = 50f * u, center = o(50f, 50f))
        }
        // Ombre au sol
        drawOval(Color.Black.copy(alpha = 0.13f), topLeft = o(24f, 89f), size = s(52f, 7f))


        // Corps (pull à capuche)
        drawOval(
            Brush.radialGradient(listOf(outfitLight, outfit, outfitDark), center = o(42f, 70f), radius = 36f * u),
            topLeft = o(24f, 62f), size = s(52f, 34f),
        )
        // Poche et cordons du pull
        drawRoundRect(outfitDark.copy(alpha = 0.35f), topLeft = o(39f, 80f), size = s(22f, 8f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * u))
        drawLine(Color.White.copy(alpha = 0.8f), o(46f, 66f), o(45f, 75f), 1.2f * u, cap = StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.8f), o(54f, 66f), o(55f, 75f), 1.2f * u, cap = StrokeCap.Round)
        // Bras
        drawOval(Brush.radialGradient(listOf(outfit, outfitDark), center = o(24f, 70f), radius = 14f * u),
            topLeft = o(18f, 66f), size = s(13f, 19f))
        drawOval(Brush.radialGradient(listOf(outfit, outfitDark), center = o(72f, 70f), radius = 14f * u),
            topLeft = o(69f, 66f), size = s(13f, 19f))
        // Tige de bambou tenue à droite (devant le bras) : un segment de plus à chaque niveau
        drawBamboo(u, segments = level.coerceIn(1, 7) + 1)
        // Patte qui tient le bambou
        drawCircle(Brush.radialGradient(listOf(FurDarkLight, FurDark), center = o(86f, 79f), radius = 6f * u),
            radius = 4.6f * u, center = o(87f, 81f))

        // Oreilles
        for (x in listOf(29f, 71f)) {
            drawCircle(Brush.radialGradient(listOf(FurDarkLight, FurDark), center = o(x - 3f, 18f), radius = 12f * u),
                radius = 9.5f * u, center = o(x, 22f))
        }
        // Tête : blanc nacré, plus sombre sur les bords
        drawCircle(
            Brush.radialGradient(listOf(Color.White, Color(0xFFF2F1F8), Color(0xFFD6D5E5)), center = o(43f, 34f), radius = 36f * u),
            radius = 27f * u, center = o(50f, 44f),
        )
        // Taches des yeux
        rotate(-20f, pivot = o(39f, 46f)) {
            drawOval(Brush.radialGradient(listOf(FurDarkLight, FurDark), center = o(37f, 42f), radius = 10f * u),
                topLeft = o(33f, 38.5f), size = s(12f, 16f))
        }
        rotate(20f, pivot = o(61f, 46f)) {
            drawOval(Brush.radialGradient(listOf(FurDarkLight, FurDark), center = o(59f, 42f), radius = 10f * u),
                topLeft = o(55f, 38.5f), size = s(12f, 16f))
        }
        // Yeux brillants
        for (x in listOf(40f, 60f)) {
            drawCircle(Color.White, 3.7f * u, o(x, 45f))
            drawCircle(Color(0xFF101018), 2.6f * u, o(x + 0.4f, 45.6f))
            drawCircle(Color.White, 1f * u, o(x + 1.2f, 44.3f))
        }
        // Joues roses
        drawOval(Color(0xFFFF9EB0).copy(alpha = 0.55f), topLeft = o(28.5f, 52f), size = s(8f, 5f))
        drawOval(Color(0xFFFF9EB0).copy(alpha = 0.55f), topLeft = o(63.5f, 52f), size = s(8f, 5f))
        // Nez et sourire
        drawOval(FurDark, topLeft = o(46.5f, 51f), size = s(7f, 4.6f))
        drawOval(Color.White.copy(alpha = 0.5f), topLeft = o(48.2f, 51.6f), size = s(2.4f, 1.2f))
        drawArc(FurDark, startAngle = 25f, sweepAngle = 130f, useCenter = false, topLeft = o(45f, 51.5f),
            size = s(10f, 6.5f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        // Reflet de lumière sur la tête
        rotate(-25f, pivot = o(39f, 26f)) {
            drawOval(Color.White.copy(alpha = 0.75f), topLeft = o(33f, 23.5f), size = s(12f, 5.5f))
        }

        drawAccessory(look.accessory, u, outfit)
    }
}

private fun DrawScope.drawBamboo(u: Float, segments: Int) {
    val x = 88f * u
    val w = 4.6f * u
    val seg = 8.6f * u
    var y = 88f * u
    repeat(segments) {
        drawRoundRect(
            Brush.horizontalGradient(listOf(lerp(Bamboo, Color.White, 0.25f), Bamboo, BambooDark), startX = x - w / 2, endX = x + w / 2),
            topLeft = Offset(x - w / 2, y - seg + 0.8f * u), size = Size(w, seg - 1.6f * u),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w / 2),
        )
        // Nœud
        drawLine(BambooDark, Offset(x - w * 0.65f, y - seg), Offset(x + w * 0.65f, y - seg), 1.1f * u, cap = StrokeCap.Round)
        y -= seg
    }
    // Deux feuilles au sommet
    fun leaf(angle: Float, len: Float) {
        val p = Path().apply {
            moveTo(0f, 0f)
            quadraticTo(len * 0.5f, -len * 0.2f, len, 0f)
            quadraticTo(len * 0.5f, len * 0.2f, 0f, 0f)
            close()
        }
        val top = Offset(x, y + 1f * u)
        rotate(angle, pivot = top) {
            translate(top.x, top.y) { drawPath(p, Brush.linearGradient(listOf(Bamboo, BambooDark), start = Offset.Zero, end = Offset(len, 0f))) }
        }
    }
    leaf(-35f, 13f * u)
    leaf(-150f, 11f * u)
}

private fun DrawScope.drawAccessory(acc: Accessory, u: Float, outfit: Color) {
    fun o(x: Float, y: Float) = Offset(x * u, y * u)
    fun s(w: Float, h: Float) = Size(w * u, h * u)
    when (acc) {
        Accessory.NONE -> Unit
        Accessory.SCARF -> {
            val red = Color(0xFFD7263D)
            drawRoundRect(Brush.verticalGradient(listOf(lerp(red, Color.White, 0.2f), lerp(red, Color.Black, 0.2f)), startY = 62f * u, endY = 70f * u),
                topLeft = o(30f, 62f), size = s(40f, 8f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * u))
            drawRoundRect(lerp(red, Color.Black, 0.15f), topLeft = o(56f, 66f), size = s(7f, 15f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * u))
        }
        Accessory.STRAW_HAT -> {
            val straw = Color(0xFFE9C46A)
            val hat = Path().apply {
                moveTo(12f * u, 27f * u); lineTo(50f * u, 5f * u); lineTo(88f * u, 27f * u)
                quadraticTo(50f * u, 33f * u, 12f * u, 27f * u); close()
            }
            drawPath(hat, Brush.linearGradient(listOf(lerp(straw, Color.White, 0.3f), straw, lerp(straw, Color.Black, 0.25f)),
                start = o(30f, 5f), end = o(70f, 32f)))
            drawLine(lerp(straw, Color.Black, 0.35f), o(22f, 25f), o(78f, 25f), 1.2f * u, cap = StrokeCap.Round)
        }
        Accessory.HEADPHONES -> {
            val band = Color(0xFF2B2D42)
            drawArc(band, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = o(20f, 14f), size = s(60f, 60f),
                style = Stroke(4f * u, cap = StrokeCap.Round))
            for (x in listOf(17f, 74f)) {
                drawRoundRect(Brush.verticalGradient(listOf(lerp(outfit, Color.White, 0.3f), lerp(outfit, Color.Black, 0.3f)),
                    startY = 37f * u, endY = 53f * u), topLeft = o(x, 37f), size = s(9f, 16f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * u))
            }
        }
        Accessory.SCHOLAR_CAP -> {
            val black = Color(0xFF1E1F2B)
            drawRoundRect(black, topLeft = o(33f, 17f), size = s(34f, 10f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * u))
            val top = Path().apply {
                moveTo(50f * u, 6f * u); lineTo(82f * u, 15f * u); lineTo(50f * u, 24f * u); lineTo(18f * u, 15f * u); close()
            }
            drawPath(top, Brush.linearGradient(listOf(Color(0xFF3A3C52), black), start = o(30f, 6f), end = o(70f, 24f)))
            drawLine(Color(0xFFD7263D), o(50f, 15f), o(78f, 22f), 1.2f * u)
            drawCircle(Color(0xFFD7263D), 2.2f * u, o(78f, 24f))
        }
        Accessory.JADE_CROWN -> {
            val jade = Color(0xFF34C08A)
            drawRoundRect(Color(0xFFE6B740), topLeft = o(34f, 20f), size = s(32f, 5f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * u))
            val crown = Path().apply {
                moveTo(34f * u, 21f * u); lineTo(36f * u, 9f * u); lineTo(43f * u, 16f * u); lineTo(50f * u, 6f * u)
                lineTo(57f * u, 16f * u); lineTo(64f * u, 9f * u); lineTo(66f * u, 21f * u); close()
            }
            drawPath(crown, Brush.linearGradient(listOf(lerp(jade, Color.White, 0.35f), jade, lerp(jade, Color.Black, 0.25f)),
                start = o(36f, 6f), end = o(64f, 21f)))
            drawCircle(Color.White.copy(alpha = 0.8f), 1.3f * u, o(50f, 12f))
        }
    }
}
