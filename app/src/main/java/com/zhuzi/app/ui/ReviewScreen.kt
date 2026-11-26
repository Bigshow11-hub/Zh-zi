package com.zhuzi.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.Word
import com.zhuzi.app.ui.theme.InkSoft

/** Révision du jour : le mot dans ses cases, on réfléchit, on vérifie, on dit si on savait. */
@Composable
fun ReviewScreen(
    dueWords: List<Word>,
    speaker: Speaker,
    onAnswer: (Word, Boolean) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
) {
    // File d'attente : un mot oublié revient en fin de séance jusqu'à ce qu'il soit su.
    val queue = remember { dueWords.shuffled().toMutableStateList() }
    val failed = remember { mutableSetOf<Word>() }
    var revealed by remember { mutableStateOf(false) }
    var done by remember { mutableIntStateOf(0) }
    val total = dueWords.size

    Column(Modifier.fillMaxSize().padding(contentPadding)) {
        TopBar("← Révision", if (queue.isEmpty()) "" else "${done + 1} / $total", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (queue.isEmpty()) {
                Spacer(Modifier.height(60.dp))
                CharGrid("记", 120.dp)
                Spacer(Modifier.height(20.dp))
                Text("Révision terminée", style = MaterialTheme.typography.headlineMedium)
                Text("$total mot${if (total > 1) "s" else ""} revu${if (total > 1) "s" else ""}. " +
                    "Reviens demain pour la suite.", color = InkSoft, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                InkButton("Retour aux leçons", onBack, Modifier.fillMaxWidth())
                return@Column
            }

            val word = queue.first()
            val reveal = {
                if (!revealed) { revealed = true; speaker.speak(word.hanzi) }
            }
            Spacer(Modifier.height(12.dp))
            WordGrids(
                word.hanzi, maxSize = 240.dp, diagonals = true,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = reveal,
                ),
            )
            Spacer(Modifier.height(20.dp))
            Column(Modifier.heightIn(min = 72.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (revealed) {
                    Text(word.pinyin, fontSize = 28.sp)
                    Text("${word.hanzi} · ${word.french}", fontSize = 18.sp, color = InkSoft,
                        textAlign = TextAlign.Center)
                } else {
                    Text("Tu t'en souviens ? Touche la case pour vérifier.", color = InkSoft,
                        textAlign = TextAlign.Center)
                }
            }
            ListenLink { speaker.speak(word.hanzi) }
            Spacer(Modifier.height(16.dp))

            if (!revealed) {
                InkButton("Voir la réponse", reveal, Modifier.fillMaxWidth())
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InkOutlinedButton("Oublié", {
                        failed.add(word)
                        onAnswer(word, false)
                        queue.removeAt(0); queue.add(word)
                        revealed = false
                    }, Modifier.weight(1f))
                    InkButton("Je savais", {
                        // Un mot raté pendant la séance reste en boîte 1 (à revoir demain).
                        if (word !in failed) onAnswer(word, true)
                        queue.removeAt(0)
                        done++
                        revealed = false
                    }, Modifier.weight(1f))
                }
            }
        }
    }
}
