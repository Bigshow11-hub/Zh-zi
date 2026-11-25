package com.zhuzi.app.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Lit les mots à voix haute avec la voix chinoise du téléphone. */
class Speaker(context: Context) : TextToSpeech.OnInitListener {
    private var ready = false
    private val tts = TextToSpeech(context.applicationContext, this)

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val result = tts.setLanguage(Locale.SIMPLIFIED_CHINESE)
        ready = result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED
        tts.setSpeechRate(0.8f)
    }

    fun speak(text: String) {
        if (ready) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    fun shutdown() = tts.shutdown()
}
