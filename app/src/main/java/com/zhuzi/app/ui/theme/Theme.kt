package com.zhuzi.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zhuzi.app.R

/*
 * Look « Cahier d'écriture » : papier blanc, encre bleu-noir, lignes de case rouges.
 * Règle : le rouge Grid ne sert qu'à dessiner les cases d'écriture (décor), jamais pour
 * un bouton ou un texte. Les boutons et le texte sont à l'encre.
 */
val Paper = Color(0xFFFFFFFF)
val Ink = Color(0xFF1E2A3A)
val InkSoft = Color(0xFF4B5768)
val Rule = Color(0xFFDCE1E8)
val Grid = Color(0xFFE04A3A)

// Couleurs de sens (bonne / mauvaise réponse) : seulement pour les retours de quiz
val Right = Color(0xFF2E7D4F)
val RightBg = Color(0xFFE7F3EC)
val Wrong = Color(0xFFB3261E)
val WrongBg = Color(0xFFFBE9E7)

/** Police pinceau (楷书) pour les caractères chinois : LXGW WenKai, réduite aux mots de l'appli. */
val Kai = FontFamily(Font(R.font.wenkai))

private val base = Typography()

val ZhuziTypography = Typography(
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Ink),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.Bold),
    bodyLarge = base.bodyLarge,
    bodyMedium = base.bodyMedium,
    bodySmall = base.bodySmall,
    labelLarge = base.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
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
        surfaceVariant = Paper,
        onSurfaceVariant = InkSoft,
        outline = Rule,
        error = Wrong,
    )
    MaterialTheme(colorScheme = colors, typography = ZhuziTypography, content = content)
}
