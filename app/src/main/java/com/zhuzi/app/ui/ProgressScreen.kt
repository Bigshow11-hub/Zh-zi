package com.zhuzi.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.reminder.Reminders
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.OnSeal
import com.zhuzi.app.ui.theme.PanelShape
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.TileShape

/** Onglet Progrès : série de jours, chiffres clés, rappel quotidien, à propos. */
@Composable
fun ProgressScreen(
    streakDays: Int,
    knownCount: Int,
    masteredCount: Int,
    total: Int,
    dueTomorrow: Int,
    lessonsMastered: Int,
    lessonsTotal: Int,
    reminderOn: Boolean,
    onReminderChange: (Boolean) -> Unit,
    versionName: String,
    contentPadding: PaddingValues,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        ScreenTitle("Progrès", "Ta régularité et ce que tu as retenu")

        // Série de jours : un grand sceau rouge avec le nombre
        Row(
            Modifier.padding(top = 12.dp).fillMaxWidth().clip(PanelShape).background(Mist).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(76.dp).graphicsLayer { rotationZ = -6f }
                    .clip(RoundedCornerShape(6.dp)).background(Seal),
                contentAlignment = Alignment.Center,
            ) { Text("$streakDays", color = OnSeal, fontWeight = FontWeight.Black, fontSize = 32.sp) }
            Spacer(Modifier.width(18.dp))
            Column {
                Text(
                    when {
                        streakDays == 0 -> "Pas encore de série"
                        streakDays == 1 -> "1 jour d'affilée"
                        else -> "$streakDays jours d'affilée"
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    if (streakDays == 0) "Étudie un mot aujourd'hui pour la commencer."
                    else "Un peu chaque jour : c'est ainsi que le bambou pousse.",
                    color = InkSoft, modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // Chiffres clés
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("DÉCOUVERTS", "$knownCount", "sur $total mots", knownCount.toFloat() / total, Modifier.weight(1f))
            StatTile("BIEN RETENUS", "$masteredCount", "sur $total mots", masteredCount.toFloat() / total, Modifier.weight(1f))
        }
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("LEÇONS MAÎTRISÉES", "$lessonsMastered", "sur $lessonsTotal", lessonsMastered.toFloat() / lessonsTotal,
                Modifier.weight(1f))
            StatTile("À REVOIR DEMAIN", "$dueTomorrow", if (dueTomorrow > 1) "mots" else "mot", null, Modifier.weight(1f))
        }

        // Rappel quotidien
        SectionLabel("RAPPEL")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Rappel du soir", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Vers ${Reminders.HOUR} h, seulement si tu n'as pas encore étudié ce jour-là.",
                    color = InkSoft, fontSize = 13.sp)
            }
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = reminderOn,
                onCheckedChange = onReminderChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Ink, checkedThumbColor = Paper,
                    uncheckedTrackColor = Mist, uncheckedThumbColor = InkSoft, uncheckedBorderColor = Rule,
                ),
            )
        }

        // À propos
        SectionLabel("À PROPOS")
        Text("Zhúzi $versionName", fontWeight = FontWeight.Bold)
        Text("Caractères au pinceau : police LXGW WenKai. Logo : police Ma Shan Zheng. " +
            "Toutes deux sous licence libre SIL OFL.", color = InkSoft, fontSize = 13.sp,
            modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Column(Modifier.padding(top = 28.dp, bottom = 10.dp)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Rule))
        Text(text, style = Kicker, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun StatTile(label: String, value: String, sub: String, progress: Float?, modifier: Modifier) {
    Column(modifier.clip(TileShape).background(Mist).padding(16.dp)) {
        Text(label, style = Kicker)
        Text(value, style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 4.dp))
        Text(sub, color = InkSoft, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        if (progress != null) ProgressBar(progress, height = 5.dp) else Spacer(Modifier.height(5.dp))
    }
}
