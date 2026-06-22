package com.nitha.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Text-to-Speech Manager for NITHA with 3 voice personas
 */
class TextToSpeechManager(context: Context) {

    private var tts: TextToSpeech? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var onCompleteCallback: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _isReady.value = true
                setupTTS()
            }
        }
    }

    private fun setupTTS() {
        tts?.apply {
            language = Locale.US
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onCompleteCallback?.invoke()
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
        }
    }

    /**
     * Configure TTS based on voice persona
     */
    fun configureVoice(persona: String, speed: Float = 1.0f, pitch: Float = 1.0f) {
        tts?.let { ttsInstance ->
            when (persona) {
                "LUNA" -> {
                    // Soft, calm, slightly slower
                    ttsInstance.setSpeechRate(speed * 0.85f)
                    ttsInstance.setPitch(pitch * 0.95f)
                }
                "MIRA" -> {
                    // Clear, professional, normal speed
                    ttsInstance.setSpeechRate(speed * 1.0f)
                    ttsInstance.setPitch(pitch * 1.0f)
                }
                "NOVA" -> {
                    // Futuristic, slightly faster, higher pitch
                    ttsInstance.setSpeechRate(speed * 1.05f)
                    ttsInstance.setPitch(pitch * 1.05f)
                }
                else -> {
                    ttsInstance.setSpeechRate(speed)
                    ttsInstance.setPitch(pitch)
                }
            }
        }
    }

    /**
     * Speak text with optional callback
     */
    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (text.isBlank()) return

        onCompleteCallback = onComplete

        // Clean text for speech (remove markdown, code blocks, etc.)
        val cleanText = text
            .replace(Regex("""```[\s\S]*?```"""), "")
            .replace(Regex("""`[^`]*`"""), "")
            .replace(Regex("""\*\*|__|\*|_"""), "")
            .replace(Regex("""#+\s*"""), "")
            .replace(Regex("""\[([^\]]*)\]\([^\)]*\)"""), "$1")
            .replace(Regex("""
+"""), " ")
            .trim()

        if (cleanText.isBlank()) return

        val utteranceId = "nitha_${System.currentTimeMillis()}"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            @Suppress("DEPRECATION")
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
            @Suppress("DEPRECATION")
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params)
        }
    }

    /**
     * Stop speaking
     */
    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    /**
     * Check if TTS is speaking
     */
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking == true
    }

    /**
     * Shutdown TTS
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
