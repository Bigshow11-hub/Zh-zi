package com.zhuzi.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R

/*
 * Zhúzi : papier, encre et sceau.
 * - Paper / Mist : le papier et les zones de l'appli (panneaux).
 * - Ink : texte et boutons principaux.
 * - Grid : SEULEMENT les lignes des cases d'écriture (décor).
 * - Seal : SEULEMENT ce qui est « validé » : sceau de fin, série de jours, leçon maîtrisée.
 * - Right / Wrong : SEULEMENT le retour des réponses.
 * Les cases d'écriture restent à angles nets (le papier) ; l'interface autour est arrondie.
 */
val Paper = Color(0xFFFFFFFF)
val Mist = Color(0xFFF4F5F7)
val Ink = Color(0xFF16202C)
val InkSoft = Color(0xFF4B5768)
val Rule = Color(0xFFDCE1E8)
val Grid = Color(0xFFE04A3A)
val Seal = Color(0xFFC8321E)

val Right = Color(0xFF2E7D4F)
val RightBg = Color(0xFFE7F3EC)
val Wrong = Color(0xFFB3261E)
val WrongBg = Color(0xFFFBE9E7)

/** Police pinceau (楷书) pour les caractères chinois : LXGW WenKai, réduite aux mots de l'appli. */
val Kai = FontFamily(Font(R.font.wenkai))

/** Formes : panneaux doux, boutons en pilule, cases d'écriture nettes. */
val PanelShape = RoundedCornerShape(20.dp)
val TileShape = RoundedCornerShape(16.dp)
val PillShape = RoundedCornerShape(50)

private val base = Typography()

val ZhuziTypography = Typography(
    displayLarge = base.displayLarge.copy(fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp),
    displaySmall = base.displaySmall.copy(fontWeight = FontWeight.Black, letterSpacing = (-1).sp),
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.Bold),
    bodyLarge = base.bodyLarge,
    bodyMedium = base.bodyMedium,
    bodySmall = base.bodySmall,
    labelLarge = base.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
)

/** Petites capitales espacées pour les étiquettes de section. */
val Kicker = TextStyle(
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.4.sp,
    color = InkSoft,
)

@Composable
fun ZhuziTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Ink,
        onPrimary = Paper,
        secondary = InkSoft,
        onSecondary = Paper,
        background = Paper,
        onBackground = Ink,
        surface = Paper,
        onSurface = Ink,
        surfaceVariant = Mist,
        onSurfaceVariant = InkSoft,
        outline = Rule,
        error = Wrong,
    )
    MaterialTheme(colorScheme = colors, typography = ZhuziTypography, content = content)
}
