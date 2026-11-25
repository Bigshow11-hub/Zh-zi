package com.zhuzi.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.zhuzi.app.ui.theme.Lacquer

/** Révision du jour : le mot, on réfléchit, on montre la réponse, on dit si on savait. */
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
        TextButton(onClick = onBack) { Text("← Leçons") }
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (queue.isEmpty()) {
                Text("🎉 Révision terminée !", style = MaterialTheme.typography.headlineMedium)
                Text("$total mots revus. Reviens demain pour la suite.",
                    textAlign = TextAlign.Center, fontSize = 18.sp)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onBack) { Text("Retour aux leçons") }
                return@Column
            }

            val word = queue.first()
            Text("Revus : $done / $total · reste ${queue.size}",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Card(Modifier.fillMaxWidth().height(280.dp).clickable {
                if (!revealed) { revealed = true; speaker.speak(word.hanzi) }
            }) {
                Column(
                    Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(word.hanzi, fontSize = 72.sp)
                    Spacer(Modifier.height(12.dp))
                    if (revealed) {
                        Text(word.pinyin, fontSize = 28.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(word.french, fontSize = 22.sp, textAlign = TextAlign.Center)
                    } else {
                        Text("Tu t'en souviens ? Touche pour vérifier",
                            style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { speaker.speak(word.hanzi) }) { Text("🔊 Écouter") }
            Spacer(Modifier.height(16.dp))

            if (!revealed) {
                Button(onClick = { revealed = true; speaker.speak(word.hanzi) }) {
                    Text("Voir la réponse")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            failed.add(word)
                            onAnswer(word, false)
                            queue.removeAt(0); queue.add(word)
                            revealed = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Lacquer),
                    ) { Text("✗ Oublié") }
                    Button(onClick = {
                        // Un mot raté pendant la séance reste en boîte 1 (à revoir demain).
                        if (word !in failed) onAnswer(word, true)
                        queue.removeAt(0)
                        done++
                        revealed = false
                    }) { Text("✓ Je savais") }
                }
            }
        }
    }
}
