package com.zhuzi.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.zhuzi.app.data.Word

/** Révision du jour : la carte, on se souvient, on la retourne, on dit si on savait. */
@Composable
fun ReviewScreen(
    dueWords: List<Word>,
    speak: (String) -> Unit,
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
    val haptic = LocalHapticFeedback.current

    Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
        TopBar("Accueil", "Révision du jour", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp,
                    bottom = contentPadding.calculateBottomPadding() + 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (queue.isEmpty()) {
                Spacer(Modifier.height(80.dp))
                DoneBlock(
                    "记", "Révision terminée",
                    "$total mot${if (total > 1) "s" else ""} revu${if (total > 1) "s" else ""}. " +
                        "Les prochains reviendront au bon moment.",
                )
                Spacer(Modifier.height(32.dp))
                InkButton("Retour à l'accueil", onBack, Modifier.fillMaxWidth())
                return@Column
            }

            StepProgress(done, total)
            Spacer(Modifier.height(18.dp))
            val word = queue.first()
            // Changement de carte en fondu
            AnimatedContent(word, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "card") { w ->
                FlipCard(w, revealed, {
                    if (!revealed) { revealed = true; speak(w.hanzi) }
                }, frontHint = "Tu t'en souviens ? Touche pour vérifier")
            }
            ListenLink { speak(word.hanzi) }

            if (!revealed) {
                InkButton("Voir la réponse", { revealed = true; speak(word.hanzi) }, Modifier.fillMaxWidth())
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InkOutlinedButton("Oublié", {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        failed.add(word)
                        onAnswer(word, false)
                        queue.removeAt(0); queue.add(word)
                        revealed = false
                    }, Modifier.weight(1f))
                    InkButton("Je savais", {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
