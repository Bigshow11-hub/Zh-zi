package com.zhuzi.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
    var strokesFor by remember { mutableStateOf<Word?>(null) }
    StrokeSheet(strokesFor) { strokesFor = null }

    Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
        TopBar(tr("Accueil", "Home", "Главная"), tr("Révision du jour", "Today's review", "Повторение дня"), onBack)
        if (queue.isEmpty()) {
            Column(
                Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                DoneBlock(
                    "记", tr("Révision terminée", "Review complete", "Повторение завершено"),
                    tr("Mots revus : ", "Words reviewed: ", "Повторено слов: ") + total + ". " +
                        tr("Les prochains reviendront au bon moment.", "The next ones will come back at the right time.",
                            "Следующие вернутся в нужный момент."),
                )
            }
            BottomActions(contentPadding) {
                InkButton(tr("Retour à l'accueil", "Back to home", "На главную"), onBack, Modifier.weight(1f))
            }
            return@Column
        }

        val word = queue.first()
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StepProgress(done, total)
            Spacer(Modifier.height(18.dp))
            // Changement de carte en fondu
            AnimatedContent(word, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "card") { w ->
                FlipCard(w, revealed, {
                    if (!revealed) { revealed = true; speak(w.hanzi) }
                }, frontHint = tr("Tu t'en souviens ? Touche pour vérifier", "Do you remember? Tap to check", "Помнишь? Нажми, чтобы проверить"))
            }
            Row {
                ListenLink { speak(word.hanzi) }
                StrokeLink { strokesFor = word }
            }
            AnimatedVisibility(
                revealed,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                ExampleBlock(word, speak, Modifier.padding(bottom = 12.dp))
            }
        }

        // Actions en bas, sous le pouce
        BottomActions(contentPadding) {
            if (!revealed) {
                InkButton(tr("Voir la réponse", "Show answer", "Показать ответ"), { revealed = true; speak(word.hanzi) }, Modifier.weight(1f))
            } else {
                run {
                    InkOutlinedButton(tr("Oublié", "Forgot", "Забыл"), {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        failed.add(word)
                        onAnswer(word, false)
                        queue.removeAt(0); queue.add(word)
                        revealed = false
                    }, Modifier.weight(1f))
                    InkButton(tr("Je savais", "I knew it", "Знал"), {
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
