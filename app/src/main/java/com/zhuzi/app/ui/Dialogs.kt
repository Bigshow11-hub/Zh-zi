package com.zhuzi.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Wrong

/** Le téléphone n'a pas de voix chinoise : on explique, et on ouvre l'écran d'Android pour l'installer. */
@Composable
fun VoiceSheet(onInstall: () -> Unit, onDismiss: () -> Unit) {
    ZhuziSheet(onDismiss) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val accent = Accent
            Box(Modifier.size(64.dp).clip(CircleShape).background(accent.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                ZIcon(Glyph.SPEAKER, accent, size = 30.dp, weight = 1.2f)
            }
            Text(tr("Pas de voix chinoise", "No Chinese voice", "Нет китайского голоса"),
                style = MaterialTheme.typography.titleLarge, color = Ink, modifier = Modifier.padding(top = 14.dp))
            Text(
                tr("Pour entendre les mots, ton téléphone a besoin de la voix « Chinois (Chine) » de la synthèse vocale. " +
                    "Touche « Installer », puis télécharge « Chinois » ou « 中文 » dans la liste. Reviens ensuite dans Zhúzi : le son marchera tout de suite.",
                    "To hear the words, your phone needs the \"Chinese (China)\" text-to-speech voice. " +
                        "Tap Install, then download \"Chinese\" or \"中文\" from the list. Come back to Zhúzi and sound will work right away.",
                    "Чтобы слышать слова, телефону нужен голос «Китайский (Китай)» синтеза речи. " +
                        "Нажми «Установить» и скачай «Китайский» или «中文». Вернись в Zhúzi — звук сразу заработает."),
                fontSize = 15.sp, color = InkSoft, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp),
            )
            Spacer(Modifier.height(20.dp))
            InkButton(tr("Installer la voix chinoise", "Install the Chinese voice", "Установить китайский голос"), onInstall,
                Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            InkOutlinedButton(tr("Plus tard", "Later", "Позже"), onDismiss, Modifier.fillMaxWidth())
        }
    }
}

/** Confirmation avant de remplacer les progrès par ceux d'une sauvegarde. */
@Composable
fun RestoreDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Paper,
        title = { Text(tr("Restaurer cette sauvegarde ?", "Restore this backup?", "Восстановить копию?")) },
        text = {
            Text(tr("Tes progrès actuels seront remplacés par ceux du fichier (révisions, série, scores, mes mots, réglages).",
                "Your current progress will be replaced by the file's (reviews, streak, scores, my words, settings).",
                "Текущий прогресс будет заменён данными из файла (повторения, серия, результаты, мои слова, настройки)."),
                color = InkSoft)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(tr("Restaurer", "Restore", "Восстановить"), color = Wrong, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(tr("Annuler", "Cancel", "Отмена"), color = InkSoft) }
        },
    )
}
