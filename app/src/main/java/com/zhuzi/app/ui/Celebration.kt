package com.zhuzi.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.AvatarLook
import com.zhuzi.app.data.Milestone
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Seal
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/** Un confetti : départ, vitesse, couleur, rotation. */
private class Bit(val x: Float, val delay: Float, val speed: Float, val drift: Float, val spin: Float, val color: Color, val w: Float, val h: Float)

/**
 * La fête d'une étape : le panda saute de joie, des confettis tombent, le repère se tamponne.
 * Plein écran par-dessus l'appli ; « Continuer » (ou le retour) la ferme.
 */
@Composable
fun CelebrationOverlay(milestone: Milestone, look: AvatarLook, level: Int, onDone: () -> Unit, animate: Boolean = true) {
    val lang = currentLang()
    val haptic = LocalHapticFeedback.current
    BackHandler(onBack = onDone)
    val scrim = remember { Animatable(if (animate) 0f else 1f) }
    val pop = remember { Animatable(if (animate) 0.6f else 1f) }
    val hop = remember { Animatable(0f) }
    val fall = remember { Animatable(if (animate) 0f else 0.45f) }
    LaunchedEffect(Unit) {
        if (!animate) return@LaunchedEffect
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        scrim.animateTo(1f, tween(220))
        pop.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow))
    }
    LaunchedEffect(Unit) {
        if (!animate) return@LaunchedEffect
        // deux petits sauts de joie
        repeat(2) {
            hop.animateTo(1f, tween(220))
            hop.animateTo(0f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
        }
    }
    LaunchedEffect(Unit) { if (animate) fall.animateTo(1f, tween(2600, easing = LinearEasing)) }

    // Confettis : 4 couleurs de l'appli seulement (accent, sceau, bambou, or)
    val palette = listOf(Accent, Seal, Color(0xFF1E9E5A), Color(0xFFE0A21B))
    val bits = remember {
        val r = Random(7)
        List(70) {
            Bit(r.nextFloat(), r.nextFloat() * 0.35f, 0.7f + r.nextFloat() * 0.6f, (r.nextFloat() - 0.5f) * 0.12f,
                r.nextFloat() * 720f, palette[it % palette.size], 6f + r.nextFloat() * 6f, 10f + r.nextFloat() * 8f)
        }
    }

    Box(
        Modifier.fillMaxSize()
            .background(Color(0xFF0E0F1E).copy(alpha = 0.62f * scrim.value))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.Center,
    ) {
        // pluie de confettis
        Canvas(Modifier.fillMaxSize()) {
            val t = fall.value
            bits.forEach { b ->
                val p = ((t - b.delay) / (1f - b.delay)).coerceIn(0f, 1f) * b.speed
                if (p <= 0f || p > 1.15f) return@forEach
                val x = size.width * (b.x + b.drift * sin(p * 2 * PI.toFloat() * 1.5f))
                val y = -40f + (size.height + 80f) * p
                rotate(b.spin * p, Offset(x, y)) {
                    drawRect(b.color.copy(alpha = (1.2f - p).coerceIn(0f, 1f)), Offset(x - b.w / 2, y - b.h / 2),
                        Size(b.w * density / 2, b.h * density / 2))
                }
            }
        }
        Column(
            Modifier.padding(24.dp).widthIn(max = 360.dp).fillMaxWidth()
                .graphicsLayer { scaleX = pop.value; scaleY = pop.value; alpha = scrim.value }
                .clip(RoundedCornerShape(20.dp)).background(Paper)
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                PandaAvatar(look, level, Modifier.size(150.dp).offset { IntOffset(0, (-22 * hop.value * density).toInt()) })
                // le repère, tamponné comme un sceau
                Box(
                    Modifier.offset(x = 6.dp, y = 2.dp).size(52.dp).graphicsLayer { rotationZ = -8f }
                        .clip(RoundedCornerShape(10.dp)).background(Seal),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(milestone.badge, color = Color.White, fontWeight = FontWeight.ExtraBold,
                        fontSize = if (milestone.badge.length > 2) 17.sp else 24.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(milestone.title.get(lang), color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp,
                textAlign = TextAlign.Center, lineHeight = 28.sp)
            Text(milestone.body.get(lang), color = InkSoft, fontSize = 15.sp, textAlign = TextAlign.Center,
                lineHeight = 21.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(22.dp))
            InkButton(tr("Continuer", "Continue", "Продолжить"), onDone, Modifier.fillMaxWidth())
        }
    }
}
