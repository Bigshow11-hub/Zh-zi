package com.zhuzi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuzi.app.data.starterWords
import com.zhuzi.app.ui.theme.ZhuziTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ZhuziTheme { FlashcardScreen() } }
    }
}

/** Écran de cartes : on touche la carte pour voir le pinyin et la traduction. */
@Composable
fun FlashcardScreen() {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var revealed by rememberSaveable { mutableStateOf(false) }
    val word = starterWords[index]

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Zhúzi 竹子", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text("Carte ${index + 1} / ${starterWords.size}",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(260.dp)
                    .clickable { revealed = !revealed },
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(word.hanzi, fontSize = 72.sp)
                    Spacer(Modifier.height(12.dp))
                    if (revealed) {
                        Text(word.pinyin, fontSize = 28.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(word.french, fontSize = 22.sp)
                    } else {
                        Text("Touche pour voir la réponse", textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {
                    index = (index - 1 + starterWords.size) % starterWords.size; revealed = false
                }) { Text("Précédent") }
                Button(onClick = {
                    index = (index + 1) % starterWords.size; revealed = false
                }) { Text("Suivant") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardPreview() { ZhuziTheme { FlashcardScreen() } }
