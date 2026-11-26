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
 * - gridLine : les guides gris discrets des cases d'écriture.
 * - tones : la couleur de chaque ton (caractères et pinyin) : 1 rouge, 2 ambre, 3 sarcelle, 4 bleu, neutre gris.
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
    /** Lignes des cases d'écriture : gris doux, pour laisser la vedette au caractère. */
    val gridLine: Color,
    /** Couleurs des tons (1 à 4, puis neutre) : elles diffèrent aussi par la clarté (daltonisme). */
    val tones: List<Color>,
    /** Accent indigo → violet : boutons, sélection, progression, carte objectif. */
    val accent: Color,
    val accent2: Color,
    /** Fond de l'appli : dégradé lavande, du haut vers le bas. */
    val bgTop: Color,
    val bgBottom: Color,
)

val DayColors = ZhuziColors(
    gridLine = Color(0xFFCBCBE3),
    accent = Color(0xFF5B5CF0),
    accent2 = Color(0xFF8B7BF7),
    bgTop = Color(0xFFECEAFF),
    bgBottom = Color(0xFFF8F8FC),
    tones = listOf(
        Color(0xFFC62828), // 1er ton : rouge
        Color(0xFFB45309), // 2e ton : ambre
        Color(0xFF0F766E), // 3e ton : sarcelle
        Color(0xFF1D4ED8), // 4e ton : bleu
        Color(0xFF5B6472), // neutre : gris
    ),
    paper = Color(0xFFFFFFFF),
    mist = Color(0xFFF1F0FB),
    ink = Color(0xFF1C1D3B),
    inkSoft = Color(0xFF6A6C8E),
    rule = Color(0xFFE4E3F3),
    right = Color(0xFF2E7D4F),
    rightBg = Color(0xFFE7F3EC),
    wrong = Color(0xFFB3261E),
    wrongBg = Color(0xFFFBE9E7),
)

val NightColors = ZhuziColors(
    gridLine = Color(0xFF3F4166),
    accent = Color(0xFF7C7DFF),
    accent2 = Color(0xFFA497FF),
    bgTop = Color(0xFF17183D),
    bgBottom = Color(0xFF0E0F22),
    tones = listOf(
        Color(0xFFFF8A80),
        Color(0xFFFDBA74),
        Color(0xFF5EEAD4),
        Color(0xFF93C5FD),
        Color(0xFFA3ADBB),
    ),
    paper = Color(0xFF1A1B35),
    mist = Color(0xFF23244A),
    ink = Color(0xFFEDEDFF),
    inkSoft = Color(0xFFA6A8CC),
    rule = Color(0xFF2D2E52),
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
val GridLine: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.gridLine
val Accent: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.accent
val Accent2: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.accent2
val BgTop: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.bgTop
val BgBottom: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.bgBottom

/** Couleur d'un ton : 1 à 4, 5 = neutre. */
@Composable
@ReadOnlyComposable
fun toneColor(tone: Int): Color = LocalZhuziColors.current.tones[(tone.coerceIn(1, 5)) - 1]

/** Identiques jour et nuit. */
val Seal = Color(0xFFC8321E)
val OnSeal = Color(0xFFFFFFFF)

/** Carte premium « encre profonde » (panneau Aujourd'hui) : identique jour et nuit, texte clair. */
val DeepInk = Color(0xFF131B26)
val DeepInk2 = Color(0xFF22303F)
val OnDeep = Color(0xFFF3F5F8)
val OnDeepSoft = Color(0xFFAEB8C6)

/** Police pinceau (楷书) pour les caractères chinois : LXGW WenKai, réduite aux caractères de l'appli. */
val Kai = FontFamily(Font(R.font.wenkai))

/** Police de l'interface : Manrope (latin + cyrillique). Le pinyin reste en police du téléphone (tous les tons). */
val Manrope = FontFamily(
    Font(R.font.manrope_400, FontWeight.Normal),
    Font(R.font.manrope_500, FontWeight.Medium),
    Font(R.font.manrope_700, FontWeight.Bold),
    Font(R.font.manrope_800, FontWeight.ExtraBold),
    Font(R.font.manrope_800, FontWeight.Black),
)

/** Police du pinyin : Noto Sans (toutes les voyelles à tons : ǎ ǐ ǒ ǔ ǚ). Jamais la police du téléphone. */
val PinyinFont = FontFamily(
    Font(R.font.pinyin_400, FontWeight.Normal),
    Font(R.font.pinyin_500, FontWeight.Medium),
    Font(R.font.pinyin_700, FontWeight.Bold),
)

/** Formes : panneaux doux, boutons en pilule, cases d'écriture nettes. */
val PanelShape = RoundedCornerShape(24.dp)
val TileShape = RoundedCornerShape(16.dp)
val PillShape = RoundedCornerShape(50)

private val base = Typography()

val ZhuziTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-2).sp),
    displaySmall = base.displaySmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp),
    headlineLarge = base.headlineLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp),
    headlineMedium = base.headlineMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.6).sp),
    headlineSmall = base.headlineSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    titleLarge = base.titleLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp),
    titleMedium = base.titleMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    titleSmall = base.titleSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Manrope),
    bodyMedium = base.bodyMedium.copy(fontFamily = Manrope),
    bodySmall = base.bodySmall.copy(fontFamily = Manrope),
    labelLarge = base.labelLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    labelMedium = base.labelMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
    labelSmall = base.labelSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
)

/** Petites capitales espacées pour les étiquettes de section. */
val Kicker: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontFamily = Manrope,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.6.sp,
        color = InkSoft,
    )

@Composable
fun ZhuziTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val c = if (darkTheme) NightColors else DayColors
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = c.accent, onPrimary = Color.White, secondary = c.inkSoft, onSecondary = c.paper,
            background = c.bgBottom, onBackground = c.ink, surface = c.bgBottom, onSurface = c.ink,
            surfaceVariant = c.mist, onSurfaceVariant = c.inkSoft, outline = c.rule, error = c.wrong,
        )
    } else {
        lightColorScheme(
            primary = c.accent, onPrimary = Color.White, secondary = c.inkSoft, onSecondary = c.paper,
            background = c.bgBottom, onBackground = c.ink, surface = c.bgBottom, onSurface = c.ink,
            surfaceVariant = c.mist, onSurfaceVariant = c.inkSoft, outline = c.rule, error = c.wrong,
        )
    }
    CompositionLocalProvider(LocalZhuziColors provides c) {
        MaterialTheme(colorScheme = scheme, typography = ZhuziTypography, content = content)
    }
}
