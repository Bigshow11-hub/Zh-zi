package com.zhuzi.app.ui

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/** État de la voix chinoise du téléphone. */
enum class Voice { CHECKING, OK, MISSING }

/**
 * Lit les mots à voix haute avec la voix chinoise du téléphone.
 * Si cette voix manque, `onMissing` est appelé (l'appli explique comment l'installer) au lieu de rester muette.
 */
class Speaker(context: Context) : TextToSpeech.OnInitListener {
    var voice by mutableStateOf(Voice.CHECKING)
        private set
    var onMissing: (() -> Unit)? = null
    private var engineOk = false
    private val tts = TextToSpeech(context.applicationContext, this)

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) { voice = Voice.MISSING; return }
        engineOk = true
        check()
    }

    /** Revérifie la voix (par exemple au retour des réglages, après l'avoir installée). */
    fun check() {
        if (!engineOk) return
        val result = runCatching { tts.setLanguage(Locale.SIMPLIFIED_CHINESE) }.getOrDefault(TextToSpeech.LANG_NOT_SUPPORTED)
        voice = if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) Voice.MISSING else Voice.OK
        tts.setSpeechRate(0.8f)
    }

    fun speak(text: String) {
        when (voice) {
            Voice.OK -> tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
            Voice.MISSING -> onMissing?.invoke()
            Voice.CHECKING -> {}
        }
    }

    fun shutdown() = tts.shutdown()

    companion object {
        /** Écran d'Android pour télécharger une voix (puis réglages de la synthèse vocale en secours). */
        fun installIntents(): List<Intent> = listOf(
            Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA),
            Intent("com.android.settings.TTS_SETTINGS"),
            Intent(android.provider.Settings.ACTION_SETTINGS),
        ).map { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    }
}
