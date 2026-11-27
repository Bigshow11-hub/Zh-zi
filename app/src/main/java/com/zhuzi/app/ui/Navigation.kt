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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import com.zhuzi.app.data.Lang
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.BgBottom
import com.zhuzi.app.ui.theme.BgTop
import com.zhuzi.app.ui.theme.Manrope
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule

/**
 * Les onglets du bas. Icônes dessinées au trait (maison, livre, case d'écriture 田字格, barres) ;
 * le caractère reste en `glyph` (fiches, notifications).
 */
enum class Tab(private val fr: String, private val en: String, private val ru: String, val glyph: String, val icon: Glyph) {
    HOME("Accueil", "Home", "Главная", "家", Glyph.HOME),
    LESSONS("Leçons", "Lessons", "Уроки", "学", Glyph.BOOK),
    CHARS("Caractères", "Characters", "Иероглифы", "字", Glyph.GRID),
    PROGRESS("Progrès", "Progress", "Успехи", "记", Glyph.CHART);

    fun label(lang: Lang) = tr(lang, fr, en, ru)
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
            Brush.verticalGradient(
                listOf(BgTop, BgBottom),
            ),
        ),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            // Couleur du texte par défaut : l'encre du thème (sinon noir, illisible la nuit)
            contentColor = Ink,
            bottomBar = { ZhuziBottomBar(tab, onTab, onCenter) },
            // Sur tablette ou en paysage, le contenu de l'onglet reste centré (≈ 600 dp)
            content = { padding -> ReadingColumn { content(padding) } },
        )
    }
}

/**
 * Barre du bas flottante, en « matière » translucide (fond de carte à 90 %, filet clair, ombre douce),
 * en pilule comme la barre d'onglets d'iOS : deux onglets, le panda (séance du jour), deux onglets.
 * Le rond du panda (60 dp) tient DANS la barre (68 dp).
 */
@Composable
fun ZhuziBottomBar(selected: Tab, onSelect: (Tab) -> Unit, onCenter: () -> Unit = {}) {
    val haptic = LocalHapticFeedback.current
    val bg = BgBottom
    val material = Paper.copy(alpha = 0.90f)
    val edge = Rule.copy(alpha = 0.7f)
    val barShape = PillShape
    Box(
        Modifier.fillMaxWidth()
            // Fond sous la barre : le contenu ne défile plus jusqu'aux boutons du téléphone
            .background(Brush.verticalGradient(
                0f to Color.Transparent, 0.5f to bg.copy(alpha = 0.85f), 1f to bg,
            ))
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            // Barre limitée à la largeur de lecture (tablette, paysage), centrée
            Modifier.widthIn(max = ReadingWidth).fillMaxWidth().height(68.dp)
                .shadow(14.dp, barShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.12f))
                .clip(barShape).background(material).border(1.dp, edge, barShape)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val tabs = Tab.entries
            tabs.forEachIndexed { i, t ->
                if (i == 2) Spacer(Modifier.width(68.dp)) // place du bouton central
                TabItem(t, t == selected, Modifier.weight(1f)) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelect(t)
                }
            }
        }
        CenterButton(Modifier.align(Alignment.Center)) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCenter()
        }
    }
}

@Composable
private fun TabItem(t: Tab, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val accent = Accent
    val pillBg by animateColorAsState(if (on) Mist else Color.Transparent, label = "tabBg")
    val tint by animateColorAsState(if (on) accent else InkSoft, label = "tabTint")
    val lang = currentLang()
    Column(
        modifier.height(56.dp).clip(PillShape).background(pillBg).selectable(
            selected = on,
            role = Role.Tab,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { if (!on) onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ZIcon(t.icon, tint, size = 24.dp, weight = if (on) 1.25f else 1f, filled = on)
        Spacer(Modifier.height(3.dp))
        // 11 sp partout. Un libellé qui ne tient pas (« Иероглифы » sur un petit téléphone, très grande
        // police) passe à 10,5 sp un peu resserré, puis se termine par « … » en dernier recours.
        val label = t.label(lang)
        var tight by remember(label) { mutableStateOf(false) }
        Text(
            label,
            fontSize = if (tight) 10.5.sp else 11.sp,
            letterSpacing = if (tight) (-0.3).sp else 0.sp,
            fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = Manrope,
            onTextLayout = { r -> if (!tight && r.hasVisualOverflow) tight = true },
        )
    }
}

/**
 * Bouton central : le panda 3D, bien rangé dans un rond de 60 dp à l'intérieur de la barre
 * (rien ne dépasse). Il cligne des yeux, s'écrase un peu à l'appui ; une petite pastille
 * « lecture » rappelle que c'est lui qui lance la séance du jour.
 */
@Composable
private fun CenterButton(modifier: Modifier, onClick: () -> Unit) {
    val a = Accent
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(if (pressed) 0.9f else 1f,
        spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "squash")
    val desc = tr("Lancer la séance du jour", "Start today's session", "Начать занятие дня")
    Box(
        modifier.size(60.dp)
            .graphicsLayer { scaleX = squash; scaleY = squash }
            .clip(CircleShape)
            .background(a.copy(alpha = 0.12f))
            .border(1.dp, a.copy(alpha = 0.30f), CircleShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center,
    ) {
        PandaHead(Modifier.size(44.dp).offset(y = (-1).dp), float = false)
    }
}
