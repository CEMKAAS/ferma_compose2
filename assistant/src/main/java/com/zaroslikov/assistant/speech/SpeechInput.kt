package com.zaroslikov.assistant.speech

import kotlinx.coroutines.flow.StateFlow

/** Состояние голосового ввода. */
sealed interface SpeechState {
    data object Idle : SpeechState

    /**
     * Слушаем; [partial] — промежуточный текст, [rms] 0..1 — громкость для анимации,
     * [onDevice] — распознаёт движок на устройстве (без сети); иначе системный сервис, который
     * без офлайн-пакета может использовать сеть — пользователя об этом предупреждают.
     */
    data class Listening(val partial: String = "", val rms: Float = 0f, val onDevice: Boolean = true) : SpeechState

    /** Распознано; текст уходит в чат, затем состояние снова Idle. */
    data class Result(val text: String) : SpeechState

    data class Error(val kind: SpeechErrorKind) : SpeechState
}

enum class SpeechErrorKind {
    /** На устройстве нет сервиса распознавания речи. */
    UNAVAILABLE,
    /** Нет разрешения на микрофон. */
    NO_PERMISSION,
    /** Ничего не услышали / не распознали. */
    NO_MATCH,
    /** Офлайн-движок недоступен, а сети нет. */
    NETWORK,
    /** Движок на устройстве есть, но без русского пакета; предложена его загрузка. */
    LANGUAGE_PACK,
    /** Микрофон занят или сервис занят. */
    BUSY,
    OTHER
}

/**
 * Голосовой ввод. Реализация по умолчанию — системный `SpeechRecognizer` (офлайн при наличии
 * языкового пакета); абстракция оставлена, чтобы подключить другой движок (например, Vosk).
 */
interface SpeechInput {
    val state: StateFlow<SpeechState>

    /** Есть ли на устройстве хоть какой-то движок распознавания. */
    fun isAvailable(): Boolean

    fun start()
    fun stop()
    fun cancel()

    /** Сбросить [SpeechState.Result]/[SpeechState.Error] в Idle после обработки. */
    fun consume()

    fun release()
}
