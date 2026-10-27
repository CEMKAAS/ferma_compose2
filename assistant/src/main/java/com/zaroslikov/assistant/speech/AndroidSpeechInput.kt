package com.zaroslikov.assistant.speech

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Голосовой ввод через системный `SpeechRecognizer`: русский язык, предпочтение офлайн-движку
 * (`EXTRA_PREFER_OFFLINE`), промежуточные результаты. На Android 12+ при наличии офлайн-пакета
 * используется распознавание на устройстве без сети. Все вызовы — из главного потока.
 */
class AndroidSpeechInput(private val context: Context) : SpeechInput {

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    override val state: StateFlow<SpeechState> = _state

    private var recognizer: SpeechRecognizer? = null
    private var listening = false

    /** Текущий распознаватель — «на устройстве» (Android 12+); при отсутствии русского пакета переключаемся на обычный. */
    private var usingOnDevice = false

    override fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    override fun start() {
        if (listening) return
        if (!isAvailable()) {
            _state.value = SpeechState.Error(SpeechErrorKind.UNAVAILABLE)
            return
        }
        val recognizer = recognizer ?: createRecognizer().also { recognizer = it }
        listening = true
        _state.value = SpeechState.Listening(onDevice = usingOnDevice)
        recognizer.startListening(recognitionIntent())
    }

    override fun stop() {
        if (!listening) return
        recognizer?.stopListening()
    }

    override fun cancel() {
        listening = false
        recognizer?.cancel()
        _state.value = SpeechState.Idle
    }

    override fun consume() {
        if (_state.value is SpeechState.Result || _state.value is SpeechState.Error) _state.value = SpeechState.Idle
    }

    override fun release() {
        listening = false
        recognizer?.destroy()
        recognizer = null
        _state.value = SpeechState.Idle
    }

    private fun createRecognizer(): SpeechRecognizer {
        // Android 12+: распознавание на устройстве — без сети. На облачный сервис молча не переключаемся:
        // надиктованные данные хозяйства не должны уходить с телефона без ведома пользователя.
        val onDevice = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
        usingOnDevice = onDevice
        val recognizer = if (onDevice) SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
        else SpeechRecognizer.createSpeechRecognizer(context)
        recognizer.setRecognitionListener(listener)
        return recognizer
    }

    /** В on-device движке нет русского пакета: на Android 13+ просим систему его загрузить. */
    private fun requestLanguagePack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                recognizer?.triggerModelDownload(recognitionIntent())
            } catch (e: Exception) {
                // Сервис может не поддерживать загрузку — остаётся подсказка пользователю
            }
        }
    }

    private fun recognitionIntent(): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LANGUAGE)
        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LANGUAGE)
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _state.value = SpeechState.Listening(onDevice = usingOnDevice)
        }

        override fun onBeginningOfSpeech() = Unit

        override fun onRmsChanged(rmsdB: Float) {
            val current = _state.value as? SpeechState.Listening ?: return
            // Типичный диапазон -2..10 дБ → 0..1
            val level = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            _state.value = current.copy(rms = level)
        }

        override fun onBufferReceived(buffer: ByteArray?) = Unit

        override fun onEndOfSpeech() = Unit

        override fun onError(error: Int) {
            listening = false
            if (usingOnDevice && (error == SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED || error == SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE)) {
                requestLanguagePack()
                _state.value = SpeechState.Error(SpeechErrorKind.LANGUAGE_PACK)
                return
            }
            val kind = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> SpeechErrorKind.NO_MATCH
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> SpeechErrorKind.NO_PERMISSION
                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT, SpeechRecognizer.ERROR_SERVER,
                SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> SpeechErrorKind.NETWORK
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY, SpeechRecognizer.ERROR_AUDIO -> SpeechErrorKind.BUSY
                SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED, SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> SpeechErrorKind.UNAVAILABLE
                else -> SpeechErrorKind.OTHER
            }
            _state.value = SpeechState.Error(kind)
        }

        override fun onResults(results: Bundle?) {
            listening = false
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.trim()
            _state.value = if (text.isNullOrEmpty()) SpeechState.Error(SpeechErrorKind.NO_MATCH) else SpeechState.Result(text)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: return
            val current = _state.value as? SpeechState.Listening ?: return
            _state.value = current.copy(partial = text)
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    private companion object {
        const val LANGUAGE = "ru-RU"
    }
}
