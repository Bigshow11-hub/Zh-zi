package com.zhuzi.app.ui

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Vrai si le téléphone demande de supprimer les animations (Accessibilité → « Supprimer les animations »,
 * ou vitesse d'animation à 0 dans les options pour développeurs). Les boucles décoratives s'arrêtent alors.
 */
@Composable
fun reduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
    }
}
