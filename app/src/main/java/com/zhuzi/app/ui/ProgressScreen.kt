package com.zhuzi.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import com.zhuzi.app.data.Lang
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
    lang: Lang,
    onLangChange: (Lang) -> Unit,
    look: com.zhuzi.app.data.AvatarLook,
    onAvatar: () -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        ScreenTitle(tr("Progrès", "Progress", "Прогресс"),
            tr("Ta régularité et ce que tu as retenu", "Your consistency and what you've learned", "Твоя регулярность и что ты выучил"))

        // Ta mascotte et son niveau
        Row(
            Modifier.padding(top = 12.dp).fillMaxWidth().softCard().clickable(onClick = onAvatar).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PandaAvatar(look, com.zhuzi.app.data.levelFor(knownCount).number, Modifier.size(96.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                LevelInfo(knownCount)
                Text(tr("Personnaliser ›", "Customize ›", "Настроить ›"), color = com.zhuzi.app.ui.theme.Accent,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // Série de jours : un grand sceau rouge avec le nombre
        Row(
            Modifier.padding(top = 12.dp).fillMaxWidth().softCard().padding(20.dp),
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
                    if (streakDays == 0) tr("Pas encore de série", "No streak yet", "Серии пока нет")
                    else daysCount(lang, streakDays) + tr(" d'affilée", " in a row", " подряд"),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    if (streakDays == 0) tr("Étudie un mot aujourd'hui pour la commencer.",
                        "Study one word today to start it.", "Выучи сегодня одно слово, чтобы начать.")
                    else tr("Un peu chaque jour : c'est ainsi que le bambou pousse.",
                        "A little every day: that's how bamboo grows.", "Понемногу каждый день: так растёт бамбук."),
                    color = InkSoft, modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // Chiffres clés
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val ofWords = tr("sur ", "of ", "из ") + wordsCount(lang, total)
            StatTile(tr("DÉCOUVERTS", "DISCOVERED", "ОТКРЫТО"), "$knownCount", ofWords, knownCount.toFloat() / total, Modifier.weight(1f))
            StatTile(tr("BIEN RETENUS", "WELL LEARNED", "ВЫУЧЕНО"), "$masteredCount", ofWords, masteredCount.toFloat() / total, Modifier.weight(1f))
        }
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(tr("LEÇONS MAÎTRISÉES", "LESSONS MASTERED", "УРОКОВ ОСВОЕНО"), "$lessonsMastered",
                tr("sur ", "of ", "из ") + lessonsTotal, lessonsMastered.toFloat() / lessonsTotal,
                Modifier.weight(1f))
            StatTile(tr("À REVOIR DEMAIN", "DUE TOMORROW", "ПОВТОРИТЬ ЗАВТРА"), "$dueTomorrow",
                wordsCount(lang, dueTomorrow).substringAfter(' '), null, Modifier.weight(1f))
        }

        // Langue
        SectionLabel(tr("LANGUE", "LANGUAGE", "ЯЗЫК"))
        Lang.entries.forEach { l ->
            val on = l == lang
            Row(
                Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(TileShape)
                    .background(if (on) com.zhuzi.app.ui.theme.Accent.copy(alpha = 0.12f) else Paper)
                    .selectable(selected = on, role = Role.RadioButton) { onLangChange(l) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Flag(l)
                Spacer(Modifier.width(14.dp))
                Text(l.nativeName, fontWeight = if (on) FontWeight.Bold else FontWeight.Medium, fontSize = 16.sp,
                    modifier = Modifier.weight(1f))
                if (on) Text("✓", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Ink)
            }
        }
        Text(tr("Traductions des mots et textes de l'appli.", "Word translations and app texts.",
            "Переводы слов и тексты приложения."), color = InkSoft, fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp))

        // Rappel quotidien
        SectionLabel(tr("RAPPEL", "REMINDER", "НАПОМИНАНИЕ"))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(tr("Rappel du soir", "Evening reminder", "Вечернее напоминание"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(tr("Vers ${Reminders.HOUR} h, seulement si tu n'as pas encore étudié ce jour-là.",
                    "Around ${Reminders.HOUR}:00, only if you haven't studied yet that day.",
                    "Около ${Reminders.HOUR}:00, только если ты ещё не занимался в этот день."),
                    color = InkSoft, fontSize = 13.sp)
            }
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = reminderOn,
                onCheckedChange = onReminderChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = com.zhuzi.app.ui.theme.Accent, checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                    uncheckedTrackColor = Mist, uncheckedThumbColor = InkSoft, uncheckedBorderColor = Rule,
                ),
            )
        }

        // À propos
        SectionLabel(tr("À PROPOS", "ABOUT", "О ПРИЛОЖЕНИИ"))
        Text("Zhúzi $versionName", fontWeight = FontWeight.Bold)
        Text(tr("Caractères au pinceau : police LXGW WenKai. Logo : police Ma Shan Zheng (licence SIL OFL). " +
            "Ordre des traits : Make Me a Hanzi (licence Arphic).",
            "Brush characters: LXGW WenKai font. Logo: Ma Shan Zheng font (SIL OFL license). " +
            "Stroke order: Make Me a Hanzi (Arphic license).",
            "Иероглифы: шрифт LXGW WenKai. Логотип: шрифт Ma Shan Zheng (лицензия SIL OFL). " +
            "Порядок черт: Make Me a Hanzi (лицензия Arphic)."), color = InkSoft, fontSize = 13.sp,
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
    Column(modifier.softCard(androidx.compose.foundation.shape.RoundedCornerShape(22.dp)).padding(16.dp)) {
        Text(label, style = Kicker)
        Text(value, style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 4.dp))
        Text(sub, color = InkSoft, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        if (progress != null) ProgressBar(progress, height = 5.dp) else Spacer(Modifier.height(5.dp))
    }
}
