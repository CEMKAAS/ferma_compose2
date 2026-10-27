package com.zaroslikov.assistant.engine

import com.zaroslikov.assistant.model.ActionCard
import com.zaroslikov.assistant.model.AnalysisCard
import com.zaroslikov.assistant.nlu.ParsedCommand

/** Ответ помощника на одну реплику пользователя. */
data class AssistantReply(
    val text: String,
    /** Карточка команды записи (ждёт подтверждения или уже записана). */
    val action: ActionCard? = null,
    val analysis: AnalysisCard? = null,
    val suggestions: List<String> = emptyList(),
    /** Незавершённая команда, по которой задан уточняющий вопрос. */
    val pending: ParsedCommand.Write? = null,
    /** Пользователь подтвердил/отменил последнюю карточку словами — экран применит это к ней. */
    val confirmsPending: Boolean = false,
    val cancelsPending: Boolean = false
)

/**
 * Движок помощника: текст пользователя + контекст проекта → ответ. Реализация по умолчанию —
 * правила и словари ([RuleBasedAssistantEngine]); интерфейс позволяет подключить локальную LLM.
 */
interface AssistantEngine {
    suspend fun handle(text: String, ctx: ProjectContext, pending: ParsedCommand.Write?, hasPendingCard: Boolean): AssistantReply

    /** Карточка для команды — например, после правки полей пользователем. */
    fun describe(command: ParsedCommand.Write, ctx: ProjectContext): ActionCard

    /** Приветствие при открытии чата. */
    fun greeting(ctx: ProjectContext): AssistantReply
}
