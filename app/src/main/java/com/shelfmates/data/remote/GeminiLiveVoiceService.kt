package com.shelfmates.data.remote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.shelfmates.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * State of the Gemini Live Voice session
 */
enum class LiveVoiceStatus {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

data class LiveVoiceTurn(
    val id: String = java.util.UUID.randomUUID().toString(),
    val speaker: String, // "You" or "Gemini Live"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LiveVoiceSessionState(
    val status: LiveVoiceStatus = LiveVoiceStatus.IDLE,
    val currentInputText: String = "",
    val currentResponseText: String = "",
    val errorMessage: String? = null,
    val audioLevel: Float = 0f,
    val turns: List<LiveVoiceTurn> = emptyList(),
    val isTtsEnabled: Boolean = true,
    val selectedBookContext: String = "Pride and Prejudice & Modern ARC Review",
    val modelName: String = "gemini-3.1-flash-live-preview"
)

/**
 * Service managing real-time voice conversations with model `gemini-3.1-flash-live-preview` (Live API).
 * Handles voice recording, live API processing, and spoken audio responses via TTS.
 */
class GeminiLiveVoiceService(
    private val context: Context,
    private val scope: CoroutineScope
) {

    private val _sessionState = MutableStateFlow(LiveVoiceSessionState())
    val sessionState = _sessionState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()
    }

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                textToSpeech?.setSpeechRate(1.05f)
                textToSpeech?.setPitch(1.0f)
                isTtsReady = true
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _sessionState.value = _sessionState.value.copy(status = LiveVoiceStatus.SPEAKING)
                    }

                    override fun onDone(utteranceId: String?) {
                        _sessionState.value = _sessionState.value.copy(status = LiveVoiceStatus.IDLE)
                    }

                    override fun onError(utteranceId: String?) {
                        _sessionState.value = _sessionState.value.copy(status = LiveVoiceStatus.IDLE)
                    }
                })
            } else {
                Log.w(TAG, "TextToSpeech init failed")
            }
        }
    }

    /**
     * Start listening to the user's voice input
     */
    fun startListening() {
        stopSpeaking()
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _sessionState.value = _sessionState.value.copy(
                status = LiveVoiceStatus.ERROR,
                errorMessage = "Speech recognition is not available on this device"
            )
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _sessionState.value = _sessionState.value.copy(
                            status = LiveVoiceStatus.LISTENING,
                            errorMessage = null,
                            currentInputText = "Listening..."
                        )
                    }

                    override fun onBeginningOfSpeech() {
                        _sessionState.value = _sessionState.value.copy(
                            status = LiveVoiceStatus.LISTENING,
                            currentInputText = "Hearing voice..."
                        )
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize RMS dB (typical range -2 to 10) to 0.0 .. 1.0 for audio wave visualizer
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
                        _sessionState.value = _sessionState.value.copy(audioLevel = normalized)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        _sessionState.value = _sessionState.value.copy(
                            status = LiveVoiceStatus.PROCESSING,
                            audioLevel = 0f
                        )
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Tap mic to speak again."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            else -> "Voice recognition stopped."
                        }
                        Log.w(TAG, "SpeechRecognizer error: $error ($message)")
                        _sessionState.value = _sessionState.value.copy(
                            status = LiveVoiceStatus.IDLE,
                            audioLevel = 0f,
                            errorMessage = if (error == SpeechRecognizer.ERROR_NO_MATCH) null else message
                        )
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _sessionState.value = _sessionState.value.copy(
                                currentInputText = text,
                                audioLevel = 0f
                            )
                            sendVoicePromptToGeminiLive(text)
                        } else {
                            _sessionState.value = _sessionState.value.copy(status = LiveVoiceStatus.IDLE)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { partial ->
                            _sessionState.value = _sessionState.value.copy(currentInputText = partial)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognizer: ${e.message}", e)
            _sessionState.value = _sessionState.value.copy(
                status = LiveVoiceStatus.ERROR,
                errorMessage = "Failed to start microphone: ${e.message}"
            )
        }
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recognizer: ${e.message}")
        }
    }

    /**
     * Send speech input to model `gemini-3.1-flash-live-preview` (Live API)
     */
    fun sendVoicePromptToGeminiLive(userText: String) {
        scope.launch {
            _sessionState.value = _sessionState.value.copy(
                status = LiveVoiceStatus.PROCESSING,
                currentInputText = userText
            )

            // Add user turn to session history
            val userTurn = LiveVoiceTurn(speaker = "You", text = userText)
            val updatedTurns = _sessionState.value.turns + userTurn
            _sessionState.value = _sessionState.value.copy(turns = updatedTurns)

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val responseText = callGeminiLiveApi(apiKey, userText, _sessionState.value.selectedBookContext)

                val aiTurn = LiveVoiceTurn(speaker = "Gemini Live", text = responseText)
                _sessionState.value = _sessionState.value.copy(
                    status = LiveVoiceStatus.SPEAKING,
                    currentResponseText = responseText,
                    turns = _sessionState.value.turns + aiTurn
                )

                // Speak back using TTS
                if (_sessionState.value.isTtsEnabled && isTtsReady) {
                    textToSpeech?.speak(
                        responseText,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "gemini_live_${System.currentTimeMillis()}"
                    )
                } else {
                    _sessionState.value = _sessionState.value.copy(status = LiveVoiceStatus.IDLE)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error from Gemini Live API: ${e.message}", e)
                _sessionState.value = _sessionState.value.copy(
                    status = LiveVoiceStatus.ERROR,
                    errorMessage = "Gemini Live response failed: ${e.message}"
                )
            }
        }
    }

    /**
     * HTTP call to Gemini Live API model: gemini-3.1-flash-live-preview
     */
    private suspend fun callGeminiLiveApi(apiKey: String, prompt: String, bookContext: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "I am ready to discuss $bookContext with you via Gemini Live! Please configure your GEMINI_API_KEY in the AI Studio Secrets panel to enable real-time Live API responses."
        }

        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-live-preview:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            // Live voice system instruction: concise, engaging, spoken-audio friendly
            val systemInstruction = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", "You are the Gemini Live Voice Book Companion for Shelfmates. You are participating in a live audio voice conversation with the reader. Keep responses concise, conversational, and direct (2-4 sentences max per spoken turn) so it feels natural to listen to. Current book discussion topic: $bookContext"))
                })
            }
            put("systemInstruction", systemInstruction)

            val contents = JSONArray().apply {
                val turn = JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                }
                put(turn)
            }
            put("contents", contents)

            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
            }
            put("generationConfig", genConfig)
        }

        val body = requestJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder().url(endpoint).post(body).build()

        val response = okHttpClient.newCall(request).execute()
        val responseBodyString = response.body?.string()

        if (!response.isSuccessful || responseBodyString == null) {
            throw Exception("HTTP ${response.code}: $responseBodyString")
        }

        val respJson = JSONObject(responseBodyString)
        val candidates = respJson.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        text ?: "I heard you! Let's continue our live book club conversation."
    }

    fun toggleTts() {
        val newTts = !_sessionState.value.isTtsEnabled
        _sessionState.value = _sessionState.value.copy(isTtsEnabled = newTts)
        if (!newTts) {
            stopSpeaking()
        }
    }

    fun setBookContext(contextText: String) {
        _sessionState.value = _sessionState.value.copy(selectedBookContext = contextText)
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            if (_sessionState.value.status == LiveVoiceStatus.SPEAKING) {
                _sessionState.value = _sessionState.value.copy(status = LiveVoiceStatus.IDLE)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS: ${e.message}")
        }
    }

    fun clearHistory() {
        stopSpeaking()
        _sessionState.value = _sessionState.value.copy(
            turns = emptyList(),
            currentInputText = "",
            currentResponseText = "",
            errorMessage = null,
            status = LiveVoiceStatus.IDLE
        )
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying service: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "GeminiLiveVoiceService"
    }
}
