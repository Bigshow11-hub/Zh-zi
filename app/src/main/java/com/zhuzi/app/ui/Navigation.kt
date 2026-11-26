package com.zhuzi.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
enum class Tab(val label: String, val glyph: String) {
    HOME("Accueil", "家"),
    LESSONS("Leçons", "学"),
    CHARS("Caractères", "字"),
    PROGRESS("Progrès", "记"),
}

/** Cadre principal : le contenu de l'onglet, et la barre de navigation en bas. */
@Composable
fun MainFrame(tab: Tab, onTab: (Tab) -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        containerColor = Paper,
        bottomBar = { ZhuziBottomBar(tab, onTab) },
        content = content,
    )
}

@Composable
fun ZhuziBottomBar(selected: Tab, onSelect: (Tab) -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth().background(Paper)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Rule))
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(72.dp)) {
            Tab.entries.forEach { t ->
                val on = t == selected
                val pillBg by animateColorAsState(if (on) Ink else Color.Transparent, label = "tabBg")
                val glyphColor by animateColorAsState(if (on) Paper else InkSoft, label = "tabGlyph")
                val pillWidth by animateDpAsState(if (on) 60.dp else 40.dp, zhuziSpring(), label = "tabPill")
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .selectable(
                            selected = on,
                            role = Role.Tab,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            if (!on) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelect(t)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                ) {
                    Box(
                        Modifier.width(pillWidth).height(32.dp).clip(PillShape).background(pillBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(t.glyph, fontFamily = Kai, fontSize = 21.sp, color = glyphColor)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        t.label,
                        fontSize = 12.sp,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = if (on) Ink else InkSoft,
                    )
                }
            }
        }
    }
}
