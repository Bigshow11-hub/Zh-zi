package com.zhuzi.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lang
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper

/**
 * Premier lancement, en 3 étapes courtes : la langue, le niveau de départ, le rappel.
 * `onDone(niveau, rappel, heure)` : niveau 0 = débutant, 1 = connaît le HSK 1, 2 = connaît les HSK 1 et 2.
 */
@Composable
fun OnboardingScreen(
    lang: Lang,
    onLang: (Lang) -> Unit,
    onDone: (level: Int, reminder: Boolean, hour: Int) -> Unit,
    contentPadding: PaddingValues,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var level by rememberSaveable { mutableIntStateOf(0) }
    var hour by rememberSaveable { mutableIntStateOf(19) }
    if (step > 0) BackHandler { step-- }

    Column(
        Modifier.fillMaxSize().padding(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ).padding(horizontal = 20.dp),
    ) {
        // Avancement : 3 petits traits
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val accent = Accent
            val mist = Mist
            repeat(3) { i ->
                Box(Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (i <= step) accent else mist))
            }
        }
        AnimatedContent(
            step,
            transitionSpec = {
                val dir = if (targetState > initialState) 1 else -1
                (fadeIn(tween(260)) + slideInHorizontally(tween(320)) { it / 5 * dir }) togetherWith
                    (fadeOut(tween(160)) + slideOutHorizontally(tween(280)) { -it / 5 * dir })
            },
            label = "onboarding",
            modifier = Modifier.weight(1f),
        ) { s ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 28.dp)) {
                when (s) {
                    0 -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            PandaHead(Modifier.size(110.dp))
                        }
                        StepTitle(tr("Bienvenue sur Zhúzi", "Welcome to Zhúzi", "Добро пожаловать в Zhúzi"),
                            tr("Le chinois, 5 minutes par jour. Choisis la langue de l'appli.",
                                "Chinese, 5 minutes a day. Choose the app language.",
                                "Китайский по 5 минут в день. Выбери язык приложения."))
                        Lang.entries.forEach { l ->
                            Choice(l == lang, { onLang(l) }) {
                                Flag(l)
                                Spacer(Modifier.width(14.dp))
                                Text(l.nativeName, fontSize = 17.sp, color = Ink, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    1 -> {
                        StepTitle(tr("Ton niveau", "Your level", "Твой уровень"),
                            tr("Les mots que tu connais déjà sont rangés dans tes révisions, étalés sur les semaines à venir.",
                                "Words you already know go into your reviews, spread over the coming weeks.",
                                "Слова, которые ты уже знаешь, попадут в повторения, распределённые на ближайшие недели."))
                        listOf(
                            Triple("你好", tr("Je débute", "I'm a beginner", "Я начинаю"),
                                tr("On commence par « bonjour ».", "We start with \"hello\".", "Начнём с «привет».")),
                            Triple("HSK 1", tr("Je connais le HSK 1", "I know HSK 1", "Я знаю HSK 1"),
                                tr("150 mots : on reprend au HSK 2.", "150 words: we pick up at HSK 2.", "150 слов: продолжим с HSK 2.")),
                            Triple("HSK 2", tr("Je connais les HSK 1 et 2", "I know HSK 1 and 2", "Я знаю HSK 1 и 2"),
                                tr("300 mots : on reprend au HSK 3.", "300 words: we pick up at HSK 3.", "300 слов: продолжим с HSK 3.")),
                        ).forEachIndexed { i, (badge, title, sub) ->
                            Choice(level == i, { level = i }) {
                                Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Mist),
                                    contentAlignment = Alignment.Center) {
                                    Text(badge, fontFamily = if (i == 0) Kai else null, fontWeight = FontWeight.Bold,
                                        fontSize = if (i == 0) 18.sp else 12.sp, color = Ink)
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(title, fontSize = 17.sp, color = Ink, fontWeight = FontWeight.SemiBold)
                                    Text(sub, fontSize = 13.sp, color = InkSoft)
                                }
                            }
                        }
                    }
                    else -> {
                        StepTitle(tr("Un petit rappel ?", "A gentle reminder?", "Напоминание?"),
                            tr("Chaque jour à l'heure choisie, seulement si tu n'as pas encore étudié. Et chaque matin, un mot nouveau. Android va te demander l'autorisation d'afficher les notifications.",
                                "Every day at the time you choose, only if you haven't studied yet. And every morning, a new word. Android will ask permission to show notifications.",
                                "Каждый день в выбранное время, только если ты ещё не занимался. А по утрам — новое слово. Android попросит разрешение на уведомления."))
                        Text(tr("HEURE DU RAPPEL", "REMINDER TIME", "ВРЕМЯ НАПОМИНАНИЯ"), fontSize = 13.sp, color = InkSoft,
                            fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(8, 12, 19, 21).forEach { h ->
                                val on = h == hour
                                val accent = Accent
                                Box(
                                    Modifier.weight(1f).heightIn(min = 52.dp).clip(RoundedCornerShape(14.dp))
                                        .background(if (on) accent.copy(alpha = 0.12f) else Paper)
                                        .border(if (on) 1.5.dp else 0.dp, if (on) accent else Color.Transparent, RoundedCornerShape(14.dp))
                                        .selectable(on, role = Role.RadioButton) { hour = h },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("%02d:00".format(h), fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                        color = if (on) accent else Ink)
                                }
                            }
                        }
                        Text(tr("Modifiable à tout moment dans Progrès → Notifications.",
                            "You can change it anytime in Progress → Notifications.",
                            "Можно изменить в любой момент: Прогресс → Уведомления."),
                            fontSize = 13.sp, color = InkSoft, modifier = Modifier.padding(start = 4.dp, top = 10.dp))
                    }
                }
            }
        }
        // Boutons, sous le pouce
        when (step) {
            0, 1 -> InkButton(tr("Continuer", "Continue", "Далее"), { step++ }, Modifier.fillMaxWidth())
            else -> {
                InkButton(tr("Activer les rappels", "Turn on reminders", "Включить напоминания"),
                    { onDone(level, true, hour) }, Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                InkOutlinedButton(tr("Plus tard", "Later", "Позже"), { onDone(level, false, hour) }, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ColumnScope.StepTitle(title: String, sub: String) {
    Text(title, style = MaterialTheme.typography.displaySmall, color = Ink, textAlign = TextAlign.Start,
        modifier = Modifier.padding(top = 18.dp).semantics { heading() })
    Text(sub, fontSize = 16.sp, color = InkSoft, modifier = Modifier.padding(top = 8.dp, bottom = 22.dp))
}

/** Une ligne à choisir (choix unique), avec contour en accent quand elle est choisie. */
@Composable
private fun Choice(selected: Boolean, onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    val accent = Accent
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().padding(bottom = 10.dp).heightIn(min = 64.dp).clip(shape)
            .background(if (selected) accent.copy(alpha = 0.08f) else Paper)
            .border(if (selected) 1.5.dp else 0.dp, if (selected) accent else Color.Transparent, shape)
            .selectable(selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        if (selected) ZIcon(Glyph.CHECK, accent, size = 20.dp, weight = 1.5f)
    }
}
