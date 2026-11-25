package com.zhuzi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Bamboo = Color(0xFF3E8E41)
val BambooDark = Color(0xFF8BC98E)
val Lacquer = Color(0xFFC0392B)

@Composable
fun ZhuziTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = BambooDark, secondary = Lacquer)
    } else {
        lightColorScheme(primary = Bamboo, secondary = Lacquer)
    }
    MaterialTheme(colorScheme = colors, content = content)
}
