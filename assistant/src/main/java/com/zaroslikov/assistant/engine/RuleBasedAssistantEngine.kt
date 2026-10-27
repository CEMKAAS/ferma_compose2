package com.zaroslikov.assistant.engine

import com.zaroslikov.assistant.model.ActionCard
import com.zaroslikov.assistant.model.CardRow
import com.zaroslikov.assistant.nlu.AssistantNlu
import com.zaroslikov.assistant.nlu.MissingSlot
import com.zaroslikov.assistant.nlu.ParsedCommand
import com.zaroslikov.assistant.nlu.RuDates
import com.zaroslikov.assistant.nlu.Units
import com.zaroslikov.assistant.util.moneyLabel
import com.zaroslikov.assistant.util.quantityLabel
import com.zaroslikov.domain.models.enums.Suffix
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Помощник на правилах: компактная локальная модель понимания команд ([AssistantNlu]) +
 * исполнение через domain-репозитории. Полностью офлайн.
 */
@Singleton
class RuleBasedAssistantEngine @Inject constructor(
    private val analytics: AnalyticsService
) : AssistantEngine {

    private val nlu = AssistantNlu()

    override fun greeting(ctx: ProjectContext): AssistantReply = AssistantReply(
        text = "Здравствуйте! Я помощник хозяйства" + (if (ctx.projectTitle.isNotBlank()) " «${ctx.projectTitle}»" else "") +
                ". Работаю без интернета: скажите или напишите, что записать, или спросите о продукции.",
        suggestions = EXAMPLES
    )

    override suspend fun handle(text: String, ctx: ProjectContext, pending: ParsedCommand.Write?, hasPendingCard: Boolean): AssistantReply {
        val command = nlu.parse(text, ctx.nlu, pending)
        return when (command) {
            ParsedCommand.Confirm -> if (hasPendingCard) AssistantReply("Записываю.", confirmsPending = true)
            else AssistantReply("Пока нечего подтверждать. Скажите, что записать — например:", suggestions = EXAMPLES.take(3))

            ParsedCommand.Cancel -> when {
                hasPendingCard -> AssistantReply("Отменил.", cancelsPending = true)
                pending != null -> AssistantReply("Хорошо, не записываю.")
                else -> AssistantReply("Хорошо. Чем ещё помочь?")
            }

            ParsedCommand.Help -> AssistantReply(HELP_TEXT, suggestions = EXAMPLES)

            is ParsedCommand.Unsupported -> AssistantReply(command.reason)

            is ParsedCommand.Unknown -> AssistantReply(
                "Не понял. Я умею записывать продукцию, продажи, покупки и животных, а также отвечать на вопросы о продукции. Например:",
                suggestions = EXAMPLES.shuffled().take(3)
            )

            is ParsedCommand.Analysis -> {
                val reply = analytics.answer(command, ctx)
                AssistantReply(reply.text, analysis = reply.card)
            }

            is ParsedCommand.Write -> writeReply(command, ctx)
        }
    }

    private fun writeReply(command: ParsedCommand.Write, ctx: ProjectContext): AssistantReply {
        val missing = command.missingSlot()
        if (missing != null) {
            return AssistantReply(question(command, missing), pending = command)
        }
        val card = describe(command, ctx)
        return AssistantReply(text = intro(command), action = card)
    }

    private fun question(command: ParsedCommand.Write, missing: MissingSlot): String {
        val what = (command as? ParsedCommand.AddProduction)?.title
            ?: (command as? ParsedCommand.AddSale)?.title
            ?: (command as? ParsedCommand.AddExpense)?.title
        return when (missing) {
            MissingSlot.TITLE -> when (command) {
                is ParsedCommand.AddSale -> "Что продали? Назовите продукт."
                is ParsedCommand.AddExpense -> "На что потратили? Назовите покупку."
                else -> "Что записать? Назовите продукт."
            }

            MissingSlot.COUNT -> if (what != null) "Сколько ${genitiveHint(what)} записать? Например, «10 литров»." else "Сколько записать?"
            MissingSlot.PRICE -> when (command) {
                is ParsedCommand.AddSale -> "По какой цене" + (if (what != null) " продали ${genitiveHint(what)}" else "") + "? Скажите «по 80» за единицу или «за 800» всего."
                else -> "Сколько заплатили" + (if (what != null) " за ${genitiveHint(what)}" else "") + "?"
            }

            MissingSlot.ANIMAL_TYPE -> "Какое животное добавить? Например, «корову Зорьку» или «20 кур»."
            MissingSlot.ANIMAL_NAME -> "Как назовём животное?"
        }
    }

    /** Название в кавычках — не склоняем, чтобы не портить слово пользователя. */
    private fun genitiveHint(title: String): String = "«$title»"

    private fun intro(command: ParsedCommand.Write): String = when (command) {
        is ParsedCommand.AddProduction -> "Записать продукцию?"
        is ParsedCommand.AddSale -> "Записать продажу?"
        is ParsedCommand.AddExpense -> "Записать расход?"
        is ParsedCommand.AddAnimal -> if (command.isGroup) "Добавить группу животных?" else "Добавить животное?"
    }

    override fun describe(command: ParsedCommand.Write, ctx: ProjectContext): ActionCard {
        val today = ctx.nlu.today
        val currency = ctx.currency
        return when (command) {
            is ParsedCommand.AddProduction -> ActionCard(
                command = command,
                title = "Продукция: ${command.title}",
                rows = buildList {
                    add(CardRow("Количество", quantityLabel(command.count ?: 0.0, command.suffix ?: Suffix.PIECES)))
                    command.animal?.let { add(CardRow("Животное", it.name)) }
                    add(CardRow("Дата", RuDates.dayLabel(command.date, today)))
                    command.note?.let { add(CardRow("Примечание", it)) }
                    if (!command.titleResolved) add(CardRow("Название", "новое — проверьте написание"))
                }
            )

            is ParsedCommand.AddSale -> {
                val count = command.count ?: 0.0
                val perUnit = command.pricePerUnit ?: command.priceTotal?.let { if (count > 0) it / count else it } ?: 0.0
                val total = command.priceTotal ?: (perUnit * count)
                ActionCard(
                    command = command,
                    title = "Продажа: ${command.title}",
                    rows = buildList {
                        add(CardRow("Количество", quantityLabel(count, command.suffix ?: Suffix.PIECES)))
                        add(CardRow("Цена за ${Units.label(command.suffix ?: Suffix.PIECES).ifEmpty { "ед" }}", moneyLabel(perUnit, currency)))
                        add(CardRow("Сумма", moneyLabel(total, currency)))
                        command.buyer?.let { add(CardRow("Покупатель", it)) }
                        add(CardRow("Дата", RuDates.dayLabel(command.date, today)))
                        command.note?.let { add(CardRow("Примечание", it)) }
                    }
                )
            }

            is ParsedCommand.AddExpense -> {
                val count = command.count ?: 1.0
                val total = command.priceTotal ?: ((command.pricePerUnit ?: 0.0) * count)
                ActionCard(
                    command = command,
                    title = "Расход: ${command.title}",
                    rows = buildList {
                        if (count != 1.0 || command.suffix != Suffix.PIECES) add(CardRow("Количество", quantityLabel(count, command.suffix ?: Suffix.PIECES)))
                        if (command.pricePerUnit != null && count != 1.0) add(CardRow("Цена за ${Units.label(command.suffix ?: Suffix.PIECES)}", moneyLabel(command.pricePerUnit, currency)))
                        add(CardRow("Сумма", moneyLabel(total, currency)))
                        command.category?.let { add(CardRow("Категория", it)) }
                        add(CardRow("Дата", RuDates.dayLabel(command.date, today)))
                        command.note?.let { add(CardRow("Примечание", it)) }
                    }
                )
            }

            is ParsedCommand.AddAnimal -> ActionCard(
                command = command,
                title = if (command.isGroup) "Группа: ${command.type}" else "Животное: ${command.name}",
                rows = buildList {
                    add(CardRow("Вид", command.type ?: ""))
                    if (command.isGroup) add(CardRow("Поголовье", "${command.count} гол"))
                    else add(CardRow("Пол", if (command.sex == true) "самец" else "самка"))
                    add(CardRow(if (command.isGroup) "Дата" else "Дата рождения", RuDates.dayLabel(command.date, today)))
                    command.priceTotal?.let { add(CardRow("Покупка", moneyLabel(it, currency))) }
                    command.note?.let { add(CardRow("Примечание", it)) }
                }
            )
        }
    }

    companion object {
        val EXAMPLES = listOf(
            "Добавь 10 литров молока",
            "Продал 30 яиц по 10 рублей",
            "Купил комбикорм 40 кг за 1200",
            "Добавь корову Зорьку",
            "Сколько молока за месяц?",
            "Выручка за прошлый месяц"
        )

        val HELP_TEXT = """
            Я записываю данные в хозяйство и отвечаю на вопросы — всё без интернета.

            • Продукция: «добавь 10 литров молока», «собрал два десятка яиц вчера», «надоила 12 л от Зорьки».
            • Продажи: «продал 10 л молока по 80 рублей Иванову», «продала 30 яиц за 300».
            • Покупки и расходы: «купил комбикорм 40 кг за 1200», «потратил 500 на ветеринара».
            • Животные: «добавь корову Зорьку», «купил 20 кур за 5000».
            • Вопросы: «сколько молока за неделю», «сколько яиц продал в сентябре», «выручка за месяц», «остаток на складе», «сколько коров», «анализ по молоку за год».

            Перед записью я показываю карточку — проверьте и нажмите «Записать» или скажите «да».
        """.trimIndent()
    }
}
