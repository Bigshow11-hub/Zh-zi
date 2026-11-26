package com.zhuzi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R

/*
 * Zhúzi : papier, encre et sceau. Deux papiers : jour (blanc) et nuit (encre bleutée).
 * - paper / mist : le papier et les panneaux de l'appli.
 * - ink : texte et boutons principaux (s'inverse la nuit).
 * - Grid : SEULEMENT les lignes des cases d'écriture (décor), identique jour et nuit.
 * - Seal : SEULEMENT ce qui est « validé » (sceau de fin, série, leçon maîtrisée), texte blanc.
 * - right / wrong : SEULEMENT le retour des réponses.
 * Les cases d'écriture restent à angles nets (le papier) ; l'interface autour est arrondie.
 */
@Immutable
data class ZhuziColors(
    val paper: Color,
    val mist: Color,
    val ink: Color,
    val inkSoft: Color,
    val rule: Color,
    val right: Color,
    val rightBg: Color,
    val wrong: Color,
    val wrongBg: Color,
)

val DayColors = ZhuziColors(
    paper = Color(0xFFFFFFFF),
    mist = Color(0xFFF4F5F7),
    ink = Color(0xFF16202C),
    inkSoft = Color(0xFF4B5768),
    rule = Color(0xFFDCE1E8),
    right = Color(0xFF2E7D4F),
    rightBg = Color(0xFFE7F3EC),
    wrong = Color(0xFFB3261E),
    wrongBg = Color(0xFFFBE9E7),
)

val NightColors = ZhuziColors(
    paper = Color(0xFF11161D),
    mist = Color(0xFF1B222C),
    ink = Color(0xFFECEFF3),
    inkSoft = Color(0xFFA3ADBB),
    rule = Color(0xFF2E3744),
    right = Color(0xFF7BD49F),
    rightBg = Color(0xFF16301F),
    wrong = Color(0xFFFF9A8F),
    wrongBg = Color(0xFF3A1614),
)

val LocalZhuziColors = staticCompositionLocalOf { DayColors }

val Paper: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.paper
val Mist: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.mist
val Ink: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.ink
val InkSoft: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.inkSoft
val Rule: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.rule
val Right: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.right
val RightBg: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.rightBg
val Wrong: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.wrong
val WrongBg: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.wrongBg

/** Identiques jour et nuit. */
val Grid = Color(0xFFE04A3A)
val Seal = Color(0xFFC8321E)
val OnSeal = Color(0xFFFFFFFF)

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
val Kicker: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.4.sp,
        color = InkSoft,
    )

@Composable
fun ZhuziTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val c = if (darkTheme) NightColors else DayColors
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = c.ink, onPrimary = c.paper, secondary = c.inkSoft, onSecondary = c.paper,
            background = c.paper, onBackground = c.ink, surface = c.paper, onSurface = c.ink,
            surfaceVariant = c.mist, onSurfaceVariant = c.inkSoft, outline = c.rule, error = c.wrong,
        )
    } else {
        lightColorScheme(
            primary = c.ink, onPrimary = c.paper, secondary = c.inkSoft, onSecondary = c.paper,
            background = c.paper, onBackground = c.ink, surface = c.paper, onSurface = c.ink,
            surfaceVariant = c.mist, onSurfaceVariant = c.inkSoft, outline = c.rule, error = c.wrong,
        )
    }
    CompositionLocalProvider(LocalZhuziColors provides c) {
        MaterialTheme(colorScheme = scheme, typography = ZhuziTypography, content = content)
    }
}
