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
 * Zhúzi : un système inspiré d'iOS (listes groupées, matières translucides), en restant une appli Android.
 * - bgTop / bgBottom : le fond « groupé » gris clair (noir la nuit) sur lequel posent les cartes.
 * - paper : les cartes et sections groupées (blanc ; gris très sombre la nuit).
 * - mist : un remplissage translucide (contrôles, pastilles, blocs dans une carte) ; il se voit
 *   aussi bien sur le fond que sur une carte.
 * - ink / inkSoft : texte principal et secondaire (contraste ≥ 4,5:1 sur fond et cartes).
 * - rule : filets de séparation et rails des barres de progression.
 * - accent : la seule couleur d'action (texte, icônes, sélection) ; accentFill : le même indigo
 *   en aplat, sous du texte blanc (plus foncé la nuit pour garder le contraste).
 * - gridLine : les guides gris discrets des cases d'écriture.
 * - tones : la couleur de chaque ton (caractères et pinyin) : 1 rouge, 2 ambre, 3 sarcelle, 4 bleu, neutre gris.
 * - Seal : SEULEMENT ce qui est « validé » (sceau de fin, série, leçon maîtrisée), texte blanc.
 * - right / wrong : SEULEMENT le retour des réponses.
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
    /** Accent indigo : texte, icônes, sélection, progression. */
    val accent: Color,
    /** Accent en aplat (boutons, pastilles pleines) : toujours sous du texte blanc. */
    val accentFill: Color,
    /** Fond de l'appli (fond « groupé »), du haut vers le bas. */
    val bgTop: Color,
    val bgBottom: Color,
    /** Pastille sélectionnée du sélecteur segmenté. */
    val thumb: Color,
)

val DayColors = ZhuziColors(
    gridLine = Color(0xFFC9C9D6),
    accent = Color(0xFF5856D6),
    accentFill = Color(0xFF5856D6),
    bgTop = Color(0xFFF2F2F7),
    bgBottom = Color(0xFFF2F2F7),
    thumb = Color(0xFFFFFFFF),
    tones = listOf(
        Color(0xFFC62828), // 1er ton : rouge
        Color(0xFFB45309), // 2e ton : ambre
        Color(0xFF0F766E), // 3e ton : sarcelle
        Color(0xFF1D4ED8), // 4e ton : bleu
        Color(0xFF5B6472), // neutre : gris
    ),
    paper = Color(0xFFFFFFFF),
    // 12 % de gris bleuté : se voit sur le fond gris comme sur une carte blanche
    mist = Color(0x1F767680),
    ink = Color(0xFF111114),
    inkSoft = Color(0xFF6C6C72),
    rule = Color(0xFFD8D8DD),
    right = Color(0xFF2E7D4F),
    rightBg = Color(0xFFE7F3EC),
    wrong = Color(0xFFB3261E),
    wrongBg = Color(0xFFFBE9E7),
)

val NightColors = ZhuziColors(
    gridLine = Color(0xFF48484F),
    accent = Color(0xFF7D7AFF),
    accentFill = Color(0xFF5E5CE6),
    bgTop = Color(0xFF000000),
    bgBottom = Color(0xFF000000),
    thumb = Color(0xFF636366),
    tones = listOf(
        Color(0xFFFF8A80),
        Color(0xFFFDBA74),
        Color(0xFF5EEAD4),
        Color(0xFF93C5FD),
        Color(0xFFA3ADBB),
    ),
    paper = Color(0xFF1C1C1E),
    mist = Color(0x3D767680),
    ink = Color(0xFFF5F5F7),
    inkSoft = Color(0xFF98989F),
    rule = Color(0xFF38383A),
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
/** Ancien second accent du dégradé : désormais le même indigo, en aplat. */
val Accent2: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.accentFill
val AccentFill: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.accentFill
val Thumb: Color @Composable @ReadOnlyComposable get() = LocalZhuziColors.current.thumb
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

/**
 * Formes : coins « continus » à la manière d'iOS (la courbe s'adoucit avant l'arrondi, pas de cassure
 * nette entre le bord droit et le quart de cercle), boutons en pilule.
 */
val PanelShape: androidx.compose.ui.graphics.Shape = SmoothShape(16.dp)
val TileShape: androidx.compose.ui.graphics.Shape = SmoothShape(12.dp)
/** Sections groupées (réglages, listes) : 12 dp, comme les listes « inset grouped » d'iOS. */
val GroupShape: androidx.compose.ui.graphics.Shape = SmoothShape(12.dp)
val PillShape = RoundedCornerShape(50)

/**
 * Rectangle aux coins continus (« squircle » d'iOS). Les constantes reprennent le tracé des coins
 * d'UIKit : chaque coin est fait de trois courbes de Bézier qui commencent à 1,53 × le rayon.
 * Si la forme est trop petite pour ce tracé, on retombe sur un arrondi classique.
 */
class SmoothShape(private val radius: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density,
    ): androidx.compose.ui.graphics.Outline {
        val r = with(density) { radius.toPx() }
        val w = size.width
        val h = size.height
        if (r <= 0f || w <= 0f || h <= 0f) {
            return androidx.compose.ui.graphics.Outline.Rectangle(androidx.compose.ui.geometry.Rect(0f, 0f, w, h))
        }
        if (r * 1.528665f > minOf(w, h) / 2f) {
            return RoundedCornerShape(radius).createOutline(size, layoutDirection, density)
        }
        val a = 1.528665f * r
        val b = 1.088493f * r
        val c = 0.868407f * r
        val d = 0.669934f * r
        val e = 0.065496f * r
        val f = 0.422829f * r
        val g = 0.158803f * r
        val path = androidx.compose.ui.graphics.Path()
        // Coins dans le sens des aiguilles : point du coin, direction d'arrivée, direction de départ
        val corners = listOf(
            floatArrayOf(w, 0f, 1f, 0f, 0f, 1f),
            floatArrayOf(w, h, 0f, 1f, -1f, 0f),
            floatArrayOf(0f, h, -1f, 0f, 0f, -1f),
            floatArrayOf(0f, 0f, 0f, -1f, 1f, 0f),
        )
        corners.forEachIndexed { i, k ->
            // p : recul depuis le coin le long du bord d'arrivée ; q : avance le long du bord de départ
            fun x(p: Float, q: Float) = k[0] - p * k[2] + q * k[4]
            fun y(p: Float, q: Float) = k[1] - p * k[3] + q * k[5]
            if (i == 0) path.moveTo(x(a, 0f), y(a, 0f)) else path.lineTo(x(a, 0f), y(a, 0f))
            path.cubicTo(x(b, 0f), y(b, 0f), x(c, 0f), y(c, 0f), x(d, e), y(d, e))
            path.cubicTo(x(f, g), y(f, g), x(g, f), y(g, f), x(e, d), y(e, d))
            path.cubicTo(x(0f, c), y(0f, c), x(0f, b), y(0f, b), x(0f, a), y(0f, a))
        }
        path.close()
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }

    override fun equals(other: Any?) = other is SmoothShape && other.radius == radius
    override fun hashCode() = radius.hashCode()
}

private val base = Typography()

/*
 * Échelle de texte calquée sur celle d'iOS, en Manrope (la police SF d'Apple ne peut pas être intégrée) :
 * displaySmall = grand titre 34, headlineMedium = titre 1 (28), headlineSmall = titre 2 (22),
 * titleLarge = titre 3 (20), titleMedium = en-tête 17, bodyLarge = corps 17, bodyMedium = 15,
 * bodySmall = note 13, labelLarge = bouton 17. Manrope est plus large que SF : interlettrage un peu serré.
 */
val ZhuziTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.5).sp),
    displayMedium = base.displayMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp),
    displaySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp,
        lineHeight = 41.sp, letterSpacing = (-0.8).sp),
    headlineLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp,
        lineHeight = 38.sp, letterSpacing = (-0.7).sp),
    headlineMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp,
        lineHeight = 34.sp, letterSpacing = (-0.6).sp),
    headlineSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 22.sp,
        lineHeight = 28.sp, letterSpacing = (-0.3).sp),
    titleLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
        lineHeight = 25.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 17.sp,
        lineHeight = 22.sp, letterSpacing = (-0.2).sp),
    titleSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 15.sp,
        lineHeight = 20.sp, letterSpacing = (-0.1).sp),
    bodyLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 17.sp,
        lineHeight = 24.sp, letterSpacing = (-0.2).sp),
    bodyMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 15.sp,
        lineHeight = 21.sp, letterSpacing = (-0.1).sp),
    bodySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 13.sp,
        lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 17.sp,
        lineHeight = 22.sp, letterSpacing = (-0.2).sp),
    labelMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 13.sp),
)

/** En-tête de section groupée (comme les listes d'iOS) : note 13, gris, légèrement espacée. */
val Kicker: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontFamily = Manrope,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.4.sp,
        color = InkSoft,
    )

@Composable
fun ZhuziTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val c = if (darkTheme) NightColors else DayColors
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = c.accentFill, onPrimary = Color.White, secondary = c.inkSoft, onSecondary = c.paper,
            background = c.bgBottom, onBackground = c.ink, surface = c.bgBottom, onSurface = c.ink,
            surfaceVariant = c.paper, onSurfaceVariant = c.inkSoft, outline = c.rule, error = c.wrong,
            surfaceContainerLow = c.paper, surfaceContainer = c.paper, surfaceContainerHigh = c.paper,
            surfaceContainerHighest = c.paper, outlineVariant = c.rule,
        )
    } else {
        lightColorScheme(
            primary = c.accentFill, onPrimary = Color.White, secondary = c.inkSoft, onSecondary = c.paper,
            background = c.bgBottom, onBackground = c.ink, surface = c.bgBottom, onSurface = c.ink,
            surfaceVariant = c.paper, onSurfaceVariant = c.inkSoft, outline = c.rule, error = c.wrong,
            surfaceContainerLow = c.paper, surfaceContainer = c.paper, surfaceContainerHigh = c.paper,
            surfaceContainerHighest = c.paper, outlineVariant = c.rule,
        )
    }
    CompositionLocalProvider(LocalZhuziColors provides c) {
        MaterialTheme(colorScheme = scheme, typography = ZhuziTypography, content = content)
    }
}
