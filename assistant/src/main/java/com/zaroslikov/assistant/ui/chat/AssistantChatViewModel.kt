package com.zaroslikov.assistant.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.assistant.engine.AssistantContextLoader
import com.zaroslikov.assistant.engine.AssistantEngine
import com.zaroslikov.assistant.engine.AssistantReply
import com.zaroslikov.assistant.engine.CommandExecutor
import com.zaroslikov.assistant.engine.ProjectContext
import com.zaroslikov.assistant.model.ActionCard
import com.zaroslikov.assistant.model.ActionStatus
import com.zaroslikov.assistant.model.ChatAuthor
import com.zaroslikov.assistant.model.ChatMessage
import com.zaroslikov.assistant.nlu.NluContext
import com.zaroslikov.assistant.nlu.ParsedCommand
import com.zaroslikov.assistant.nlu.RuDates
import com.zaroslikov.assistant.nlu.Units
import com.zaroslikov.assistant.settings.AssistantPrefs
import com.zaroslikov.assistant.speech.SpeechErrorKind
import com.zaroslikov.assistant.speech.SpeechInput
import com.zaroslikov.assistant.speech.SpeechState
import com.zaroslikov.assistant.ui.base.AssistantBaseViewModel
import com.zaroslikov.assistant.ui.base.AssistantUiEvent
import com.zaroslikov.assistant.ui.navigation.AssistantDestination
import com.zaroslikov.assistant.util.assistantFormat
import com.zaroslikov.assistant.util.toInputDouble
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AssistantChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val engine: AssistantEngine,
    private val executor: CommandExecutor,
    private val contextLoader: AssistantContextLoader,
    private val speech: SpeechInput,
    private val prefs: AssistantPrefs
) : AssistantBaseViewModel<AssistantChatState, AssistantChatIntent>(AssistantChatState(), ::assistantChatReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[AssistantDestination.itemIdPT])

    private val messages = ArrayList<ChatMessage>()
    private var nextId = 1L
    private var ctx: ProjectContext = ProjectContext(idPT, "", Suffix.RUBLE, NluContext())

    /** Команда, по которой задан уточняющий вопрос. */
    private var pending: ParsedCommand.Write? = null

    init {
        viewModelScope.launch {
            ctx = loadContext()
            sendIntent(AssistantChatIntent.Loaded(ctx.projectTitle, prefs.autoConfirm, speech.isAvailable()))
            addAssistant(engine.greeting(ctx))
        }
        viewModelScope.launch {
            speech.state.collect { state ->
                when (state) {
                    is SpeechState.Result -> {
                        speech.consume()
                        sendIntent(AssistantChatIntent.SpeechChanged(SpeechState.Idle))
                        send(state.text)
                    }

                    is SpeechState.Error -> {
                        speech.consume()
                        sendIntent(AssistantChatIntent.SpeechChanged(SpeechState.Idle))
                        addError(speechErrorText(state.kind))
                    }

                    is SpeechState.Listening -> {
                        // Честно предупреждаем один раз, если распознаёт не движок на устройстве
                        if (!state.onDevice && !cloudNoticeShown) {
                            cloudNoticeShown = true
                            addError(CLOUD_NOTICE)
                        }
                        sendIntent(AssistantChatIntent.SpeechChanged(state))
                    }

                    else -> sendIntent(AssistantChatIntent.SpeechChanged(state))
                }
            }
        }
    }

    private var cloudNoticeShown = false

    override fun onIntent(intent: AssistantChatIntent) {
        val before = getState()
        sendIntent(intent)
        when (intent) {
            is AssistantChatIntent.Send -> {
                val text = (intent.text ?: before.input).trim()
                if (text.isNotEmpty()) send(text)
            }

            AssistantChatIntent.MicClicked -> {
                if (before.speech is SpeechState.Listening) speech.stop() else speech.start()
            }

            AssistantChatIntent.StopListening -> if (before.speech is SpeechState.Listening) speech.cancel()

            AssistantChatIntent.MicPermissionDenied -> addError(MIC_DENIED)

            is AssistantChatIntent.ConfirmCard -> confirmCard(intent.messageId)
            is AssistantChatIntent.CancelCard -> updateCard(intent.messageId) { it.copy(status = ActionStatus.CANCELLED) }
            is AssistantChatIntent.UndoCard -> undoCard(intent.messageId)
            is AssistantChatIntent.EditCard -> openEdit(intent.messageId)
            AssistantChatIntent.EditFormSaved -> before.editForm?.let { saveEdit(it) }

            is AssistantChatIntent.AutoConfirmChanged -> prefs.autoConfirm = intent.value
            AssistantChatIntent.ClearChat -> clearChat()
            else -> Unit
        }
    }

    override fun onCleared() {
        speech.release()
        super.onCleared()
    }

    // ---------- Диалог ----------

    private fun send(rawText: String) {
        val text = rawText.take(MAX_INPUT_LENGTH)
        addMessage(ChatMessage(nextId++, ChatAuthor.USER, text))
        val hasPendingCard = getState().pendingMessageId != null
        val currentPending = pending
        sendIntent(AssistantChatIntent.Thinking(true))
        viewModelScope.launch {
            // Сообщения обрабатываем строго по очереди: иначе второе разберётся со старым уточнением,
            // а «да» подтвердит не ту карточку
            sendMutex.withLock {
                sendIntent(AssistantChatIntent.Thinking(true))
                handle(text, currentPending, hasPendingCard)
            }
        }
    }

    private val sendMutex = Mutex()

    private suspend fun handle(text: String, currentPending: ParsedCommand.Write?, hasPendingCard: Boolean) {
        run {
            try {
                // Разбор и запросы к БД — вне главного потока: длинная фраза не подвесит UI
                val reply = withContext(Dispatchers.Default) { engine.handle(text, ctx, currentPending, hasPendingCard) }
                pending = reply.pending
                when {
                    reply.confirmsPending -> getState().pendingMessageId?.let { confirmCard(it) }
                    reply.cancelsPending -> getState().pendingMessageId?.let { id -> updateCard(id) { it.copy(status = ActionStatus.CANCELLED) } }
                }
                if (reply.action != null && getState().autoConfirm) {
                    // Без подтверждения: пишем сразу, карточка показывается как «Записано»
                    val saved = executor.execute(reply.action.command, idPT, ctx.currency)
                    addAssistant(reply.copy(text = savedText(reply.action), action = reply.action.copy(status = ActionStatus.SAVED, saved = saved.takeIf { !it.isEmpty })))
                    refreshContext()
                } else {
                    // Новая карточка на подтверждение заменяет прежнюю неподтверждённую
                    if (reply.action != null) getState().pendingMessageId?.let { id -> updateCard(id) { it.copy(status = ActionStatus.CANCELLED) } }
                    addAssistant(reply)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                addError(DB_ERROR)
            } finally {
                sendIntent(AssistantChatIntent.Thinking(false))
            }
        }
    }

    private fun confirmCard(messageId: Long) {
        val card = messages.firstOrNull { it.id == messageId }?.action ?: return
        if (card.status != ActionStatus.PENDING) return
        // Статус меняем синхронно до запуска корутины — второе нажатие или голосовое «да» не запишут повторно
        updateCard(messageId) { it.copy(status = ActionStatus.SAVING) }
        launchSafe(onError = { updateCard(messageId) { it.copy(status = ActionStatus.PENDING) } }) {
            val saved = executor.execute(card.command, idPT, ctx.currency)
            // Без известного id отменить нечего — кнопка «Отменить запись» не показывается
            updateCard(messageId) { it.copy(status = ActionStatus.SAVED, saved = saved.takeIf { !it.isEmpty }) }
            addAssistant(AssistantReply(savedText(card)))
            refreshContext()
        }
    }

    private fun undoCard(messageId: Long) {
        val card = messages.firstOrNull { it.id == messageId }?.action ?: return
        val saved = card.saved ?: return
        if (card.status != ActionStatus.SAVED) return
        updateCard(messageId) { it.copy(status = ActionStatus.UNDOING) }
        launchSafe(onError = { updateCard(messageId) { it.copy(status = ActionStatus.SAVED) } }) {
            executor.undo(saved)
            updateCard(messageId) { it.copy(status = ActionStatus.UNDONE) }
            addAssistant(AssistantReply("Запись удалена."))
            refreshContext()
        }
    }

    private fun clearChat() {
        messages.clear()
        pending = null
        speech.cancel()
        publish()
        addAssistant(engine.greeting(ctx))
    }

    private fun savedText(card: ActionCard): String = when (card.command) {
        is ParsedCommand.AddProduction -> "Записал продукцию: ${card.title.substringAfter(": ")}."
        is ParsedCommand.AddSale -> "Записал продажу."
        is ParsedCommand.AddExpense -> "Записал расход."
        is ParsedCommand.AddAnimal -> "Добавил в хозяйство."
    }

    // ---------- Правка карточки ----------

    private fun openEdit(messageId: Long) {
        val card = messages.firstOrNull { it.id == messageId }?.action ?: return
        if (card.status != ActionStatus.PENDING) return
        val c = card.command
        val form = when (c) {
            is ParsedCommand.AddProduction -> ActionEditForm(
                messageId, c,
                title = c.title.orEmpty(), count = c.count?.assistantFormat().orEmpty(),
                unitSuffix = c.suffix ?: Suffix.PIECES, unitLabel = Units.label(c.suffix ?: Suffix.PIECES),
                animalName = c.animal?.name.orEmpty(), date = RuDates.display(c.date), note = c.note.orEmpty()
            )

            is ParsedCommand.AddSale -> ActionEditForm(
                messageId, c,
                title = c.title.orEmpty(), count = c.count?.assistantFormat().orEmpty(),
                unitSuffix = c.suffix ?: Suffix.PIECES, unitLabel = Units.label(c.suffix ?: Suffix.PIECES),
                price = (c.pricePerUnit ?: c.priceTotal)?.assistantFormat().orEmpty(), priceIsPerUnit = c.pricePerUnit != null,
                buyer = c.buyer.orEmpty(), date = RuDates.display(c.date), note = c.note.orEmpty()
            )

            is ParsedCommand.AddExpense -> ActionEditForm(
                messageId, c,
                title = c.title.orEmpty(), count = (c.count ?: 1.0).assistantFormat(),
                unitSuffix = c.suffix ?: Suffix.PIECES, unitLabel = Units.label(c.suffix ?: Suffix.PIECES),
                price = (c.priceTotal ?: c.pricePerUnit)?.assistantFormat().orEmpty(), priceIsPerUnit = c.priceTotal == null && c.pricePerUnit != null,
                category = c.category.orEmpty(), date = RuDates.display(c.date), note = c.note.orEmpty()
            )

            is ParsedCommand.AddAnimal -> ActionEditForm(
                messageId, c,
                animalName = c.name.orEmpty(), animalType = c.type.orEmpty(), count = c.count.toString(), unitLabel = "гол",
                price = c.priceTotal?.assistantFormat().orEmpty(), priceIsPerUnit = false,
                date = RuDates.display(c.date), note = c.note.orEmpty()
            )
        }
        sendIntent(AssistantChatIntent.EditFormChanged(form))
    }

    private fun saveEdit(form: ActionEditForm) {
        val date = parseDate(form.date) ?: form.command.date
        val note = form.note.trim().ifEmpty { null }
        val count = form.count.toInputDouble()
        val price = form.price.toInputDouble()
        val command: ParsedCommand.Write = when (val c = form.command) {
            is ParsedCommand.AddProduction -> c.copy(
                title = form.title.trim(), count = count ?: c.count, suffix = form.unitSuffix, date = date, note = note,
                animal = ctx.nlu.animals.firstOrNull { it.name.equals(form.animalName.trim(), ignoreCase = true) } ?: c.animal.takeIf { form.animalName.isNotBlank() },
                titleResolved = true
            )

            is ParsedCommand.AddSale -> c.copy(
                title = form.title.trim(), count = count ?: c.count, suffix = form.unitSuffix, date = date, note = note,
                pricePerUnit = if (form.priceIsPerUnit) price else null,
                priceTotal = if (form.priceIsPerUnit) null else price,
                buyer = form.buyer.trim().ifEmpty { null },
                titleResolved = true
            )

            is ParsedCommand.AddExpense -> c.copy(
                title = form.title.trim(), count = count ?: c.count, suffix = form.unitSuffix, date = date, note = note,
                pricePerUnit = if (form.priceIsPerUnit) price else null,
                priceTotal = if (form.priceIsPerUnit) null else price,
                category = form.category.trim().ifEmpty { null },
                titleResolved = true
            )

            is ParsedCommand.AddAnimal -> {
                val type = form.animalType.trim()
                c.copy(
                    name = form.animalName.trim().ifEmpty { null },
                    type = type,
                    species = AnimalSpecies.resolve(type),
                    // «Корова» → «Бык»: пол берём из нового названия, если оно его задаёт
                    sex = AnimalSpecies.sexOf(type) ?: c.sex,
                    count = count?.toInt()?.coerceAtLeast(1) ?: c.count,
                    date = date,
                    priceTotal = price,
                    note = note
                )
            }
        }
        val missing = command.missingSlot()
        if (missing != null) {
            emitEvent(AssistantUiEvent.Message(com.zaroslikov.assistant.R.string.assistant_edit_error_empty))
            return
        }
        sendIntent(AssistantChatIntent.EditFormDismissed)
        // Пока шторка была открыта, карточку могли подтвердить голосом или отменить — её не воскрешаем
        val current = messages.firstOrNull { it.id == form.messageId }?.action ?: return
        if (current.status != ActionStatus.PENDING) return
        val card = engine.describe(command, ctx)
        updateCard(form.messageId) { card.copy(status = ActionStatus.PENDING) }
    }

    private fun parseDate(text: String): LocalDate? =
        try {
            LocalDate.parse(text.trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            null
        }

    // ---------- Сообщения ----------

    private fun addAssistant(reply: AssistantReply) {
        addMessage(
            ChatMessage(
                id = nextId++,
                author = ChatAuthor.ASSISTANT,
                text = reply.text,
                action = reply.action,
                analysis = reply.analysis,
                suggestions = reply.suggestions
            )
        )
    }

    private fun addError(text: String) {
        addMessage(ChatMessage(nextId++, ChatAuthor.ASSISTANT, text, isError = true))
    }

    private fun addMessage(message: ChatMessage) {
        messages += message
        publish()
        emitEvent(AssistantUiEvent.ScrollToBottom)
    }

    private fun updateCard(messageId: Long, transform: (ActionCard) -> ActionCard) {
        val index = messages.indexOfFirst { it.id == messageId }
        if (index < 0) return
        val message = messages[index]
        val card = message.action ?: return
        messages[index] = message.copy(action = transform(card))
        publish()
    }

    private fun publish() = sendIntent(AssistantChatIntent.MessagesChanged(messages.toList()))

    private suspend fun loadContext(): ProjectContext =
        try {
            contextLoader.load(idPT)
        } catch (e: Exception) {
            ProjectContext(idPT, "", Suffix.RUBLE, NluContext())
        }

    /** После записи обновляем известные названия/животных/покупателей. */
    private fun refreshContext() {
        viewModelScope.launch { ctx = loadContext() }
    }

    private fun speechErrorText(kind: SpeechErrorKind): String = when (kind) {
        SpeechErrorKind.UNAVAILABLE -> "На этом устройстве нет распознавания речи для русского языка. Напишите команду текстом."
        SpeechErrorKind.NO_PERMISSION -> MIC_DENIED
        SpeechErrorKind.NO_MATCH -> "Не расслышал. Нажмите микрофон и повторите."
        SpeechErrorKind.NETWORK -> "Офлайн-распознавание недоступно: скачайте русский языковой пакет в настройках голосового ввода Google или напишите текстом."
        SpeechErrorKind.LANGUAGE_PACK -> "В распознавании на устройстве нет русского языка. Я запросил загрузку пакета — проверьте уведомления или настройки голосового ввода, а пока напишите текстом."
        SpeechErrorKind.BUSY -> "Микрофон занят другим приложением. Попробуйте ещё раз."
        SpeechErrorKind.OTHER -> "Не удалось распознать речь. Попробуйте ещё раз или напишите текстом."
    }

    companion object {
        /** Ограничение длины одной реплики: разбор нечётких совпадений квадратичен по длине. */
        const val MAX_INPUT_LENGTH = 500
        private const val MIC_DENIED = "Без доступа к микрофону голосовой ввод не работает. Разрешите его в настройках приложения или пишите текстом."
        private const val DB_ERROR = "Не получилось выполнить: ошибка базы данных. Попробуйте ещё раз."
        private const val CLOUD_NOTICE = "На этом устройстве нет распознавания речи на самом устройстве: голос обрабатывает системный сервис распознавания, без офлайн-пакета русского языка он может использовать сеть. Текстовый ввод всегда работает офлайн."
    }
}
