package com.zhuzi.app.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule

/** Les onglets du bas. Chaque icône est un caractère : 家 maison, 学 apprendre, 字 caractère, 记 retenir. */
enum class Tab(private val fr: String, private val en: String, private val ru: String, val glyph: String) {
    HOME("Accueil", "Home", "Главная", "家"),
    LESSONS("Leçons", "Lessons", "Уроки", "学"),
    CHARS("Caractères", "Characters", "Иероглифы", "字"),
    PROGRESS("Progrès", "Progress", "Успехи", "记");

    fun label(lang: com.zhuzi.app.data.Lang) = tr(lang, fr, en, ru)
}

/** Cadre principal : fond lavande en dégradé, contenu de l'onglet, barre flottante en bas. */
@Composable
fun MainFrame(
    tab: Tab,
    onTab: (Tab) -> Unit,
    onCenter: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        Modifier.fillMaxSize().background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(com.zhuzi.app.ui.theme.BgTop, com.zhuzi.app.ui.theme.BgBottom),
            ),
        ),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            // Couleur du texte par défaut : l'encre du thème (sinon noir, illisible la nuit)
            contentColor = com.zhuzi.app.ui.theme.Ink,
            bottomBar = { ZhuziBottomBar(tab, onTab, onCenter) },
            content = content,
        )
    }
}

/** Barre du bas flottante : deux onglets, le bouton central « séance du jour », deux onglets. */
@Composable
fun ZhuziBottomBar(selected: Tab, onSelect: (Tab) -> Unit, onCenter: () -> Unit = {}) {
    val haptic = LocalHapticFeedback.current
    val bg = com.zhuzi.app.ui.theme.BgBottom
    Box(
        Modifier.fillMaxWidth()
            // Fond sous la barre : le contenu ne défile plus jusqu'aux boutons du téléphone
            .background(androidx.compose.ui.graphics.Brush.verticalGradient(
                0f to Color.Transparent, 0.45f to bg.copy(alpha = 0.92f), 1f to bg,
            ))
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 10.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            Modifier.fillMaxWidth().height(72.dp).softCard(androidx.compose.foundation.shape.RoundedCornerShape(30.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val tabs = Tab.entries
            tabs.forEachIndexed { i, t ->
                if (i == 2) Spacer(Modifier.width(76.dp)) // place du bouton central
                TabItem(t, t == selected, Modifier.weight(1f)) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelect(t)
                }
            }
        }
        CenterButton(Modifier.align(Alignment.TopCenter).offset(y = (-26).dp)) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCenter()
        }
    }
}

@Composable
private fun TabItem(t: Tab, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val accent = com.zhuzi.app.ui.theme.Accent
    val pillBg by animateColorAsState(if (on) accent.copy(alpha = 0.13f) else Color.Transparent, label = "tabBg")
    val glyphColor by animateColorAsState(if (on) accent else InkSoft, label = "tabGlyph")
    val pillWidth by animateDpAsState(if (on) 52.dp else 40.dp, zhuziSpring(), label = "tabPill")
    Column(
        modifier.fillMaxHeight().selectable(
            selected = on,
            role = Role.Tab,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { if (!on) onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Box(
            Modifier.width(pillWidth).height(30.dp).clip(PillShape).background(pillBg),
            contentAlignment = Alignment.Center,
        ) { Text(t.glyph, fontFamily = Kai, fontSize = 20.sp, color = glyphColor) }
        Spacer(Modifier.height(3.dp))
        Text(
            t.label(currentLang()),
            fontSize = if (currentLang() == com.zhuzi.app.data.Lang.RU) 10.sp else 11.sp,
            letterSpacing = if (currentLang() == com.zhuzi.app.data.Lang.RU) (-0.2).sp else 0.sp,
            softWrap = false,
            fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
            color = if (on) accent else InkSoft,
            maxLines = 1,
        )
    }
}

/**
 * Bouton central : le panda 3D qui lance la séance du jour. Il flotte, cligne des yeux,
 * s'écrase un peu quand on appuie, et une pastille « lecture » rappelle que c'est un bouton.
 */
@Composable
private fun CenterButton(modifier: Modifier, onClick: () -> Unit) {
    val a = com.zhuzi.app.ui.theme.Accent
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(if (pressed) 0.88f else 1f,
        androidx.compose.animation.core.spring(dampingRatio = 0.45f, stiffness = androidx.compose.animation.core.Spring.StiffnessMedium),
        label = "squash")
    val desc = tr("Lancer la séance du jour", "Start today's session", "Начать занятие дня")
    val bob = androidx.compose.animation.core.rememberInfiniteTransition(label = "fab")
    val lift by bob.animateFloat(0f, 1f, androidx.compose.animation.core.infiniteRepeatable(
        androidx.compose.animation.core.tween(1600, easing = EaseInOutSine), androidx.compose.animation.core.RepeatMode.Reverse), label = "lift")
    Box(
        modifier.size(80.dp)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center,
    ) {
        // ombre douce au sol, plus petite quand le panda monte
        androidx.compose.foundation.Canvas(Modifier.align(Alignment.BottomCenter).size(56.dp, 10.dp)) {
            drawOval(Color(0xFF14152B).copy(alpha = 0.16f - 0.06f * lift),
                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.1f * lift, 0f),
                size = androidx.compose.ui.geometry.Size(size.width * (1f - 0.2f * lift), size.height))
        }
        // halo d'accent derrière la tête : le panda blanc ressort sur la barre claire
        Box(
            Modifier.size(66.dp).graphicsLayer { translationY = -lift * 3f * density + 4f * density }
                .clip(CircleShape)
                .background(androidx.compose.ui.graphics.Brush.radialGradient(listOf(a.copy(alpha = 0.32f), a.copy(alpha = 0.12f)))),
        )
        PandaHead(
            Modifier.size(76.dp).graphicsLayer {
                translationY = -lift * 3f * density
                scaleX = squash * (1f + (1f - squash) * 0.6f); scaleY = squash
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
            },
            float = false,
        )
        // pastille « lecture »
        Box(
            Modifier.align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-6).dp).size(26.dp)
                .shadow(4.dp, CircleShape, spotColor = a.copy(alpha = 0.4f)).clip(CircleShape).background(a)
                .border(2.dp, com.zhuzi.app.ui.theme.Paper, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.foundation.Canvas(Modifier.size(10.dp)) {
                val p = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.08f)
                    lineTo(size.width * 0.95f, size.height * 0.50f)
                    lineTo(size.width * 0.18f, size.height * 0.92f)
                    close()
                }
                drawPath(p, Color.White)
            }
        }
    }
}
