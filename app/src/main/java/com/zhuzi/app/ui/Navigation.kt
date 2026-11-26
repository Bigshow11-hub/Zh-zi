package com.zhuzi.app.ui

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
    PROGRESS("Progrès", "Progress", "Прогресс", "记");

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
        CenterButton(Modifier.align(Alignment.TopCenter).offset(y = (-18).dp)) {
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

/** Bouton rond central en dégradé : lance la séance du jour (révision, sinon la leçon à reprendre). */
@Composable
private fun CenterButton(modifier: Modifier, onClick: () -> Unit) {
    val a = com.zhuzi.app.ui.theme.Accent
    val b = com.zhuzi.app.ui.theme.Accent2
    val interaction = remember { MutableInteractionSource() }
    val desc = tr("Lancer la séance du jour", "Start today's session", "Начать занятие дня")
    Box(
        modifier.size(64.dp).pressScale(interaction)
            .shadow(16.dp, CircleShape, ambientColor = a.copy(alpha = 0.4f), spotColor = a.copy(alpha = 0.55f))
            .clip(CircleShape)
            .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(a, b)))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center,
    ) {
        // Triangle « lecture », dessiné
        androidx.compose.foundation.Canvas(Modifier.size(22.dp)) {
            val p = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.22f, size.height * 0.10f)
                lineTo(size.width * 0.92f, size.height * 0.50f)
                lineTo(size.width * 0.22f, size.height * 0.90f)
                close()
            }
            drawPath(p, Color.White)
        }
    }
}
