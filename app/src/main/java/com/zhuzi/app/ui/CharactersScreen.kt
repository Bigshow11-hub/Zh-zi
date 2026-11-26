package com.zhuzi.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Lesson
import com.zhuzi.app.data.Word
import com.zhuzi.app.data.hsk1Lessons
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kicker
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Seal
import com.zhuzi.app.ui.theme.TileShape
import java.text.Normalizer

/** Enlève les accents et tons (nǐ → ni, é → e) pour chercher sans se soucier des signes. */
private fun plain(s: String): String =
    Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{M}"), "").lowercase().trim()

/** Onglet Caractères : tous les mots, avec recherche, filtres et fiche détaillée. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    boxOf: (Word) -> Int,
    speak: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    val entries = remember { hsk1Lessons.flatMap { l -> l.words.map { it to l } } }
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Pair<Word, Lesson>?>(null) }

    val q = plain(query)
    val shown = entries.filter { (w, _) ->
        val box = boxOf(w)
        val keep = when (filter) {
            1 -> box > 0
            2 -> box >= 4
            3 -> box == 0
            else -> true
        }
        keep && (q.isEmpty() || plain(w.pinyin).contains(q) || plain(w.french).contains(q) ||
            w.hanzi.contains(query.trim()))
    }
    val known = entries.count { boxOf(it.first) > 0 }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                ScreenTitle("Caractères", "$known découverts sur ${entries.size} · touche un mot pour sa fiche")
                Spacer(Modifier.height(8.dp))
                SearchField(query) { query = it }
                Spacer(Modifier.height(12.dp))
                Segmented(listOf("Tous", "Vus", "Retenus", "Nouveaux"), filter, { filter = it })
                Spacer(Modifier.height(6.dp))
            }
        }
        if (shown.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Aucun mot trouvé.", color = InkSoft, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp))
            }
        }
        items(shown, key = { it.first.hanzi }) { (w, l) ->
            CharCell(w, boxOf(w)) { selected = w to l }
        }
    }

    selected?.let { (w, l) ->
        ModalBottomSheet(onDismissRequest = { selected = null }, containerColor = Paper) {
            WordDetail(w, l, boxOf(w), speak)
        }
    }
}

@Composable
private fun SearchField(value: String, onChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = TextStyle(color = Ink, fontSize = 16.sp),
        cursorBrush = SolidColor(Ink),
        modifier = Modifier.fillMaxWidth().clip(PillShape).background(Mist)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) Text("Chercher : ni hao, merci, 你…", color = InkSoft, fontSize = 16.sp)
                inner()
            }
        },
    )
}

@Composable
private fun CharCell(word: Word, box: Int, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier.fillMaxWidth().pressScale(interaction)
            .clip(TileShape).border(1.dp, Rule, TileShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(10.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().height(52.dp), contentAlignment = Alignment.Center) {
                WordGrids(word.hanzi, maxSize = 44.dp)
            }
            Spacer(Modifier.height(8.dp))
            Text(word.pinyin, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text(word.french, fontSize = 11.sp, color = InkSoft, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        // Repère : point d'encre = découvert, petit sceau rouge = bien retenu
        when {
            box >= 4 -> Box(
                Modifier.align(Alignment.TopEnd).size(10.dp).graphicsLayer { rotationZ = -6f }
                    .clip(RoundedCornerShape(1.5.dp)).background(Seal),
            )
            box > 0 -> Box(Modifier.align(Alignment.TopEnd).size(7.dp).clip(CircleShape).background(Ink))
        }
    }
}

@Composable
private fun WordDetail(word: Word, lesson: Lesson, box: Int, speak: (String) -> Unit) {
    Column(
        Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WordGrids(word.hanzi, maxSize = 120.dp, diagonals = true)
        Spacer(Modifier.height(18.dp))
        Text(word.pinyin, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(word.french, fontSize = 18.sp, color = InkSoft, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Text("LEÇON ${lesson.id} · ${lesson.short.uppercase()}", style = Kicker)
        Text(
            when {
                box >= 4 -> "Bien retenu"
                box > 0 -> "Découvert · en révision"
                else -> "Pas encore découvert"
            },
            style = MaterialTheme.typography.bodyMedium, color = Ink,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(20.dp))
        InkButton("Écouter", { speak(word.hanzi) }, Modifier.fillMaxWidth())
    }
}
