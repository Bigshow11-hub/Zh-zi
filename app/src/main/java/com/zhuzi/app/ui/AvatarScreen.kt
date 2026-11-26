package com.zhuzi.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Accessory
import com.zhuzi.app.data.AvatarLook
import com.zhuzi.app.data.AvatarStats
import com.zhuzi.app.data.backgrounds
import com.zhuzi.app.data.levelFor
import com.zhuzi.app.data.levels
import com.zhuzi.app.data.nextLevel
import com.zhuzi.app.data.outfitColors
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist

/** Carte de niveau : nom du niveau, barre vers le suivant. */
@Composable
fun LevelInfo(known: Int, modifier: Modifier = Modifier) {
    val lang = currentLang()
    val level = levelFor(known)
    val next = nextLevel(known)
    Column(modifier) {
        Text(tr("NIVEAU ", "LEVEL ", "УРОВЕНЬ ") + "${level.number} / ${levels.size}", style = Kicker)
        Text(level.name.get(lang), style = MaterialTheme.typography.headlineMedium)
        if (next != null) {
            val span = (next.from - level.from).toFloat()
            ProgressBar((known - level.from) / span, Modifier.padding(top = 10.dp), height = 8.dp)
            Text(
                tr("Encore ", "", "Ещё ") + wordsCount(lang, next.from - known) +
                    tr(" pour : ", " to go for: ", " до: ") + next.name.get(lang),
                fontSize = 13.sp, color = InkSoft, modifier = Modifier.padding(top = 6.dp),
            )
        } else {
            Text(tr("Niveau maximal atteint. Bravo !", "Top level reached. Well done!", "Высший уровень. Браво!"),
                fontSize = 13.sp, color = InkSoft, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

/** Écran « Mon avatar » : la mascotte, son niveau, et sa personnalisation. */
@Composable
fun AvatarScreen(
    look: AvatarLook,
    stats: AvatarStats,
    onChange: (AvatarLook) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
) {
    val lang = currentLang()
    val level = levelFor(stats.known)
    Column(
        Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())
            .verticalScroll(rememberScrollState())
            .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        TopBar(tr("Retour", "Back", "Назад"), tr("Mon avatar", "My avatar", "Мой аватар"), onBack)
        // La mascotte en grand
        Box(Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            PandaAvatar(look, level.number, Modifier.size(240.dp)
                .semantics { contentDescription = tr(lang, "Ta mascotte panda", "Your panda mascot", "Твой талисман-панда") })
        }
        LevelInfo(stats.known, Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp).fillMaxWidth().softCard().padding(20.dp))

        // Tenue
        Section(tr("TENUE", "OUTFIT", "ОДЕЖДА"))
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            outfitColors.forEachIndexed { i, c ->
                Swatch(Brush.linearGradient(listOf(Color(c), Color(c).copy(alpha = 0.75f))), i == look.outfit) {
                    onChange(look.copy(outfit = i))
                }
            }
        }

        // Accessoires
        Section(tr("ACCESSOIRE", "ACCESSORY", "АКСЕССУАР"))
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Accessory.entries.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { acc ->
                        val open = acc.unlocked(stats)
                        val on = acc == look.accessory
                        Row(
                            Modifier.weight(1f).softCard(RoundedCornerShape(20.dp))
                                .then(if (on) Modifier.border(2.dp, Accent, RoundedCornerShape(20.dp)) else Modifier)
                                .clickable(enabled = open) { onChange(look.copy(accessory = acc)) }
                                .padding(10.dp)
                                .alpha(if (open) 1f else 0.5f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PandaAvatar(look.copy(accessory = acc), level.number, Modifier.size(48.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(acc.label.get(lang), fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2)
                                if (!open) {
                                    Text(tr("À débloquer : ", "Unlock: ", "Открыть: ") + acc.condition.get(lang), fontSize = 11.sp,
                                        color = InkSoft, maxLines = 2)
                                } else if (on) {
                                    Text(tr("Porté", "Wearing", "Надето"), fontSize = 11.sp, color = Accent,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        // Fond
        Section(tr("FOND", "BACKGROUND", "ФОН"))
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            backgrounds.forEachIndexed { i, (a, b) ->
                Swatch(Brush.linearGradient(listOf(Color(a), Color(b))), i == look.background) {
                    onChange(look.copy(background = i))
                }
            }
        }
        Text(
            tr("Ta mascotte grandit avec toi : chaque niveau allonge sa tige de bambou, et chaque étape débloque un accessoire.",
                "Your mascot grows with you: each level lengthens its bamboo, and each milestone unlocks an accessory.",
                "Твой талисман растёт вместе с тобой: каждый уровень удлиняет бамбук, а каждый этап открывает аксессуар."),
            fontSize = 13.sp, color = InkSoft, textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 24.dp).fillMaxWidth(),
        )
    }
}

@Composable
private fun Section(title: String) {
    Text(title, style = Kicker, modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 12.dp))
}

/** Pastille de couleur ronde, cerclée d'indigo quand elle est choisie. */
@Composable
private fun Swatch(brush: Brush, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(44.dp).clip(CircleShape)
            .then(if (selected) Modifier.border(3.dp, Accent, CircleShape) else Modifier.border(1.dp, Mist, CircleShape))
            .clickable(onClick = onClick)
            .padding(5.dp).clip(CircleShape).background(brush),
    )
}
