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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
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
    week: List<Pair<java.time.LocalDate, Int>> = emptyList(),
    themeMode: Int = 0,
    onThemeChange: (Int) -> Unit = {},
    reminderTime: Pair<Int, Int> = Reminders.HOUR to 0,
    onReminderTime: (Int, Int) -> Unit = { _, _ -> },
    wordOn: Boolean = true,
    onWordChange: (Boolean) -> Unit = {},
    wordTime: Pair<Int, Int> = 9 to 0,
    onWordTime: (Int, Int) -> Unit = { _, _ -> },
    updatesOn: Boolean = true,
    onUpdatesChange: (Boolean) -> Unit = {},
    updateName: String? = null,
    onUpdate: () -> Unit = {},
    exactOk: Boolean = true,
    batteryOk: Boolean = true,
    onExact: () -> Unit = {},
    onBattery: () -> Unit = {},
    onTest: () -> Unit = {},
    onExport: () -> Unit = {},
    onImport: () -> Unit = {},
    contentPadding: PaddingValues,
) {
    var pickTime by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var pickWordTime by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val timeLabel = "%02d:%02d".format(reminderTime.first, reminderTime.second)
    val wordLabel = "%02d:%02d".format(wordTime.first, wordTime.second)
    if (pickTime) ReminderTimeDialog(reminderTime, { h, m -> onReminderTime(h, m); pickTime = false }) { pickTime = false }
    if (pickWordTime) ReminderTimeDialog(wordTime, { h, m -> onWordTime(h, m); pickWordTime = false },
        title = tr("Heure du mot du jour", "Word of the day time", "Время слова дня")) { pickWordTime = false }
    val scroll = rememberScrollState()
    val density = androidx.compose.ui.platform.LocalDensity.current
    val compact by remember { androidx.compose.runtime.derivedStateOf { scroll.value > with(density) { 56.dp.toPx() } } }
    Box(Modifier.fillMaxSize()) {
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll)
            .padding(start = 16.dp, end = 16.dp,
                top = contentPadding.calculateTopPadding() + 12.dp,
                bottom = contentPadding.calculateBottomPadding() + 32.dp),
    ) {
        LargeTitle(tr("Progrès", "Progress", "Прогресс"),
            subtitle = tr("Ta régularité et ce que tu as retenu", "Your consistency and what you've learned", "Твоя регулярность и что ты выучил"))

        // Ta mascotte et son niveau
        Row(
            Modifier.padding(top = 4.dp).fillMaxWidth().softCard()
                .clickable(role = Role.Button, onClick = onAvatar).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PandaAvatar(look, com.zhuzi.app.data.levelFor(knownCount).number, Modifier.size(88.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                LevelInfo(knownCount)
                Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("Personnaliser", "Customize", "Настроить"), color = com.zhuzi.app.ui.theme.Accent,
                        style = MaterialTheme.typography.titleSmall)
                    ZIcon(Glyph.CHEVRON_RIGHT, com.zhuzi.app.ui.theme.Accent, size = 14.dp, weight = 1.6f,
                        modifier = Modifier.padding(start = 2.dp))
                }
            }
        }

        // Série de jours : un grand sceau rouge avec le nombre
        Row(
            Modifier.padding(top = 12.dp).fillMaxWidth().softCard().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(72.dp).graphicsLayer { rotationZ = -6f }
                    .clip(RoundedCornerShape(6.dp)).background(Seal),
                contentAlignment = Alignment.Center,
            ) { Text("$streakDays", color = OnSeal, fontWeight = FontWeight.Black, fontSize = 30.sp) }
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
                    color = InkSoft, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // Ta semaine : une barre par jour
        if (week.isNotEmpty()) WeekChart(week, Modifier.padding(top = 12.dp))

        // Chiffres clés
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val ofWords = tr("sur ", "of ", "из ") + wordsCount(lang, total)
            StatTile(tr("Découverts", "Discovered", "Открыто"), "$knownCount", ofWords, knownCount.toFloat() / total, Modifier.weight(1f))
            StatTile(tr("Bien retenus", "Well learned", "Выучено"), "$masteredCount", ofWords, masteredCount.toFloat() / total, Modifier.weight(1f))
        }
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(tr("Leçons maîtrisées", "Lessons mastered", "Уроков освоено"), "$lessonsMastered",
                tr("sur ", "of ", "из ") + lessonsTotal, lessonsMastered.toFloat() / lessonsTotal,
                Modifier.weight(1f))
            StatTile(tr("À revoir demain", "Due tomorrow", "Повторить завтра"), "$dueTomorrow",
                wordsCount(lang, dueTomorrow).substringAfter(' '), null, Modifier.weight(1f))
        }

        // Réglages : listes groupées, comme l'appli Réglages
        // Apparence : automatique, clair, sombre
        GroupedSection(
            Modifier.padding(top = 32.dp),
            header = tr("APPARENCE", "APPEARANCE", "ОФОРМЛЕНИЕ"),
            footer = tr("« Automatique » suit le réglage de ton téléphone.", "\"Automatic\" follows your phone's setting.",
                "«Авто» следует настройке телефона."),
        ) {
            Box(Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
                Segmented(
                    listOf(tr("Automatique", "Automatic", "Авто"), tr("Clair", "Light", "Светлая"), tr("Sombre", "Dark", "Тёмная")),
                    themeMode, onThemeChange,
                )
            }
        }

        // Langue : liste à choix unique, coche en accent
        GroupedSection(
            Modifier.padding(top = 28.dp),
            header = tr("LANGUE", "LANGUAGE", "ЯЗЫК"),
            footer = tr("Traductions des mots et textes de l'appli.", "Word translations and app texts.",
                "Переводы слов и тексты приложения."),
        ) {
            Lang.entries.forEachIndexed { i, l ->
                val on = l == lang
                if (i > 0) RowDivider(RowIconInset)
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .selectable(selected = on, role = Role.RadioButton) { onLangChange(l) }
                        .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(30.dp), contentAlignment = Alignment.Center) { Flag(l) }
                    Spacer(Modifier.width(14.dp))
                    Text(l.nativeName, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    if (on) ZIcon(Glyph.CHECK, com.zhuzi.app.ui.theme.Accent, size = 20.dp, weight = 1.5f)
                }
            }
        }

        // Notifications : rappel du soir, mot du jour, nouvelles versions
        GroupedSection(
            Modifier.padding(top = 28.dp),
            header = tr("NOTIFICATIONS", "NOTIFICATIONS", "УВЕДОМЛЕНИЯ"),
            footer = tr("Rappel à $timeLabel seulement si tu n'as pas encore étudié. Mot du jour à $wordLabel : un mot nouveau chaque matin, à ajouter à tes révisions d'un geste.",
                "Reminder at $timeLabel only if you haven't studied yet. Word of the day at $wordLabel: a new word every morning, added to your reviews in one tap.",
                "Напоминание в $timeLabel, только если ты ещё не занимался. Слово дня в $wordLabel: новое слово каждое утро."),
        ) {
            GroupedRow(
                title = tr("Rappel quotidien", "Daily reminder", "Ежедневное напоминание"),
                icon = { IconTile(Glyph.BELL) },
                modifier = Modifier.toggleable(value = reminderOn, role = Role.Switch) { onReminderChange(it) },
                trailing = { ZSwitch(reminderOn) },
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Heure du rappel", "Reminder time", "Время напоминания"),
                icon = { IconTile(Glyph.CLOCK) },
                value = timeLabel,
                chevron = true,
                titleColor = if (reminderOn) androidx.compose.ui.graphics.Color.Unspecified else InkSoft,
                onClick = if (reminderOn) ({ pickTime = true }) else null,
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Mot du jour", "Word of the day", "Слово дня"),
                icon = { IconTile(Glyph.BOOK) },
                modifier = Modifier.toggleable(value = wordOn, role = Role.Switch) { onWordChange(it) },
                trailing = { ZSwitch(wordOn) },
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Heure du mot du jour", "Word of the day time", "Время слова дня"),
                icon = { IconTile(Glyph.CLOCK) },
                value = wordLabel,
                chevron = true,
                titleColor = if (wordOn) androidx.compose.ui.graphics.Color.Unspecified else InkSoft,
                onClick = if (wordOn) ({ pickWordTime = true }) else null,
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Nouvelles versions", "New versions", "Новые версии"),
                icon = { IconTile(Glyph.INFO) },
                modifier = Modifier.toggleable(value = updatesOn, role = Role.Switch) { onUpdatesChange(it) },
                trailing = { ZSwitch(updatesOn) },
            )
        }

        // Sauvegarde : garder ses progrès en changeant de téléphone
        GroupedSection(
            Modifier.padding(top = 28.dp),
            header = tr("SAUVEGARDE", "BACKUP", "РЕЗЕРВНАЯ КОПИЯ"),
            footer = tr("Un fichier avec tous tes progrès (révisions, série, scores, mes mots, réglages). Garde-le (Drive, e-mail…) pour le restaurer sur un autre téléphone. Ils sont aussi copiés par la sauvegarde Google du téléphone.",
                "A file with all your progress (reviews, streak, scores, my words, settings). Keep it (Drive, email…) to restore it on another phone. It is also included in the phone's Google backup.",
                "Файл со всем прогрессом (повторения, серия, результаты, мои слова, настройки). Сохрани его (Drive, почта…), чтобы восстановить на другом телефоне."),
        ) {
            GroupedRow(
                title = tr("Sauvegarder mes progrès", "Back up my progress", "Сохранить прогресс"),
                icon = { IconTile(Glyph.CHECK) },
                chevron = true,
                onClick = onExport,
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Restaurer une sauvegarde", "Restore a backup", "Восстановить из копии"),
                icon = { IconTile(Glyph.CLOCK) },
                chevron = true,
                onClick = onImport,
            )
        }

        // Appli fermée : ce qu'Android doit autoriser pour que les notifications arrivent à l'heure
        GroupedSection(
            Modifier.padding(top = 28.dp),
            header = tr("APPLI FERMÉE", "APP CLOSED", "ПРИЛОЖЕНИЕ ЗАКРЫТО"),
            footer = tr("Les notifications arrivent même quand Zhúzi est fermée. Pour qu'elles soient à l'heure, autorise ces deux réglages (surtout sur Samsung).",
                "Notifications arrive even when Zhúzi is closed. To get them on time, allow these two settings (especially on Samsung).",
                "Уведомления приходят, даже когда Zhúzi закрыта. Чтобы они приходили вовремя, разреши эти два пункта (особенно на Samsung)."),
        ) {
            val ok = tr("Autorisé", "Allowed", "Разрешено")
            val todo = tr("Autoriser", "Allow", "Разрешить")
            GroupedRow(
                title = tr("Heure exacte", "Exact time", "Точное время"),
                subtitle = tr("Alarmes et rappels", "Alarms & reminders", "Будильники и напоминания"),
                icon = { IconTile(Glyph.CLOCK) },
                value = if (exactOk) ok else todo,
                chevron = !exactOk,
                onClick = if (exactOk) null else onExact,
                role = if (exactOk) null else Role.Button,
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Batterie sans restriction", "Unrestricted battery", "Батарея без ограничений"),
                subtitle = tr("Pour ne pas être mise en veille", "So it isn't put to sleep", "Чтобы не засыпало"),
                icon = { IconTile(Glyph.CONTRAST) },
                value = if (batteryOk) ok else todo,
                chevron = !batteryOk,
                onClick = if (batteryOk) null else onBattery,
                role = if (batteryOk) null else Role.Button,
            )
            RowDivider(RowIconInset)
            GroupedRow(
                title = tr("Envoyer une notification d'essai", "Send a test notification", "Отправить тестовое уведомление"),
                icon = { IconTile(Glyph.BELL) },
                chevron = true,
                onClick = onTest,
            )
        }

        // À propos
        GroupedSection(
            Modifier.padding(top = 28.dp),
            header = tr("À PROPOS", "ABOUT", "О ПРИЛОЖЕНИИ"),
            footer = tr("Caractères au pinceau : police LXGW WenKai. Logo : police Ma Shan Zheng (licence SIL OFL). " +
                "Ordre des traits : Make Me a Hanzi (licence Arphic).",
                "Brush characters: LXGW WenKai font. Logo: Ma Shan Zheng font (SIL OFL license). " +
                    "Stroke order: Make Me a Hanzi (Arphic license).",
                "Иероглифы: шрифт LXGW WenKai. Логотип: шрифт Ma Shan Zheng (лицензия SIL OFL). " +
                    "Порядок черт: Make Me a Hanzi (лицензия Arphic)."),
        ) {
            GroupedRow(
                title = "Zhúzi",
                icon = { IconTile(Glyph.INFO) },
                value = versionName,
                role = null,
            )
            if (updateName != null) {
                RowDivider(RowIconInset)
                GroupedRow(
                    title = tr("Nouvelle version $updateName", "New version $updateName", "Новая версия $updateName"),
                    subtitle = tr("Télécharge-la et installe-la par-dessus : tes progrès sont gardés.",
                        "Download it and install it over this one: your progress is kept.",
                        "Скачай и установи поверх: прогресс сохранится."),
                    icon = { IconTile(Glyph.CHECK) },
                    value = tr("Télécharger", "Download", "Скачать"),
                    chevron = true,
                    onClick = onUpdate,
                )
            }
        }
    }
    CompactTitleBar(tr("Progrès", "Progress", "Прогресс"), compact, contentPadding.calculateTopPadding())
    }
}

@Composable
private fun StatTile(label: String, value: String, sub: String, progress: Float?, modifier: Modifier) {
    Column(modifier.softCard().padding(16.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = InkSoft, maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        val n = value.toIntOrNull()
        if (n != null) {
            CountUpText(n, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.displaySmall)
        } else {
            Text(value, style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 4.dp))
        }
        Text(sub, color = InkSoft, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        Spacer(Modifier.height(12.dp))
        if (progress != null) ProgressBar(progress, height = 4.dp) else Spacer(Modifier.height(4.dp))
    }
}

/** « Ta semaine » : une barre par jour (actions faites), qui pousse à l'ouverture ; aujourd'hui en dégradé. */
@Composable
private fun WeekChart(days: List<Pair<java.time.LocalDate, Int>>, modifier: Modifier = Modifier) {
    val lang = currentLang()
    val locale = when (lang) {
        com.zhuzi.app.data.Lang.FR -> java.util.Locale.FRENCH
        com.zhuzi.app.data.Lang.EN -> java.util.Locale.ENGLISH
        com.zhuzi.app.data.Lang.RU -> java.util.Locale.forLanguageTag("ru")
    }
    val top = maxOf(days.maxOf { it.second }, 10)
    val total = days.sumOf { it.second }
    val activeDays = days.count { it.second > 0 }
    val a = com.zhuzi.app.ui.theme.Accent
    val b = com.zhuzi.app.ui.theme.Accent2
    Column(modifier.fillMaxWidth().softCard().padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Ta semaine", "Your week", "Твоя неделя"), style = MaterialTheme.typography.titleSmall, color = InkSoft,
                modifier = Modifier.weight(1f))
            Text(tr("$activeDays jour${if (activeDays > 1) "s" else ""} sur 7", "$activeDays of 7 days", "$activeDays из 7 дней"),
                style = MaterialTheme.typography.labelMedium, color = InkSoft)
        }
        Text(
            "$total " + tr(if (total > 1) "actions cette semaine" else "action cette semaine", "actions this week",
                ruPlural(total, "действие за неделю", "действия за неделю", "действий за неделю")),
            style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 4.dp),
        )
        Row(
            Modifier.fillMaxWidth().height(150.dp).padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEachIndexed { i, (day, n) ->
                val isToday = i == days.lastIndex
                val target = n.toFloat() / top
                val anim = remember { androidx.compose.animation.core.Animatable(0f) }
                LaunchedEffect(target) {
                    kotlinx.coroutines.delay(i * 70L)
                    anim.animateTo(target, androidx.compose.animation.core.spring(dampingRatio = 0.7f,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessLow))
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Place toujours réservée au chiffre, pour que toutes les barres partent du même niveau
                    Text(if (n > 0) "$n" else " ", fontSize = 11.sp, color = InkSoft, fontWeight = FontWeight.Bold)
                    Box(
                        Modifier.padding(top = 4.dp).width(20.dp)
                            .height(androidx.compose.ui.unit.max(6.dp, 72.dp * anim.value))
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isToday) androidx.compose.ui.graphics.Brush.verticalGradient(listOf(b, a))
                                else androidx.compose.ui.graphics.SolidColor(if (n > 0) a.copy(alpha = 0.35f) else Mist),
                            ),
                    )
                    Text(
                        day.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, locale).take(3)
                            .replaceFirstChar { it.titlecase(locale) },
                        fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp),
                        color = if (isToday) a else InkSoft,
                        fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

/** Choisir l'heure du rappel : le cadran d'Android (Material 3). */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimeDialog(
    initial: Pair<Int, Int>,
    onPick: (Int, Int) -> Unit,
    title: String = tr("Heure du rappel", "Reminder time", "Время напоминания"),
    onDismiss: () -> Unit,
) {
    val state = androidx.compose.material3.rememberTimePickerState(initial.first, initial.second, is24Hour = true)
    val accent = com.zhuzi.app.ui.theme.Accent
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.zhuzi.app.ui.theme.Paper,
        title = { Text(title) },
        text = {
            androidx.compose.material3.TimePicker(
                state,
                colors = androidx.compose.material3.TimePickerDefaults.colors(
                    clockDialColor = Mist, selectorColor = accent,
                    timeSelectorSelectedContainerColor = accent.copy(alpha = 0.14f),
                    timeSelectorSelectedContentColor = accent,
                    timeSelectorUnselectedContainerColor = Mist,
                ),
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = { onPick(state.hour, state.minute) }) {
                Text(tr("Valider", "Save", "Сохранить"), color = accent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(tr("Annuler", "Cancel", "Отмена"), color = InkSoft)
            }
        },
    )
}

/** Interrupteur des réglages (la ligne entière est cliquable). */
@Composable
private fun ZSwitch(checked: Boolean) {
    Switch(
        checked = checked,
        onCheckedChange = null,
        colors = SwitchDefaults.colors(
            checkedTrackColor = com.zhuzi.app.ui.theme.AccentFill, checkedThumbColor = androidx.compose.ui.graphics.Color.White,
            checkedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            uncheckedTrackColor = Mist, uncheckedThumbColor = androidx.compose.ui.graphics.Color.White,
            uncheckedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
        ),
    )
}
