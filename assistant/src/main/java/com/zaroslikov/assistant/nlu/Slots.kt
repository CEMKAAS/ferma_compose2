package com.zaroslikov.assistant.nlu

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import java.time.LocalDate

/**
 * Известный продукт проекта — для сопоставления названий и единицы по умолчанию.
 * [origin] — откуда продукт: ADD (своя продукция), EXPENSES (купленный товар), SALE (только в продажах);
 * продажа наследует его, иначе склад и аналитика продаж (они смотрят `product_origin = 0`) её не увидят.
 */
data class KnownProduct(
    val title: String,
    val suffix: Suffix,
    val isExpense: Boolean = false,
    val origin: ProductOrigin = if (isExpense) ProductOrigin.EXPENSES else ProductOrigin.ADD
)

/** Животное проекта — для «надоила 10 л от Зорьки». */
data class KnownAnimal(val id: Long, val name: String, val type: String, val isGroup: Boolean)

/** Контекст проекта, который помощник знает перед разбором фразы. */
data class NluContext(
    val productTitles: List<KnownProduct> = emptyList(),
    val animals: List<KnownAnimal> = emptyList(),
    val buyers: List<String> = emptyList(),
    val expenseCategories: List<String> = emptyList(),
    val today: LocalDate = LocalDate.now()
)

/** Что спрашивают в аналитике. */
enum class AnalysisKind {
    /** Сколько произведено продукта (или всех продуктов). */
    PRODUCTION,
    /** Сколько продано и на какую сумму. */
    SALES,
    /** Расходы за период. */
    EXPENSES,
    /** Выручка, расходы, прибыль, баланс. */
    FINANCE,
    /** Остаток на складе. */
    STOCK,
    /** Поголовье. */
    ANIMALS,
    /** Сводка по продукту: произведено, продано, выручка, остаток. */
    PRODUCT_SUMMARY
}

/** Разобранная команда. Слоты nullable — чего не хватает, помощник уточнит. */
sealed interface ParsedCommand {

    /** Команды, которые пишут в БД и требуют подтверждения. */
    sealed interface Write : ParsedCommand {
        val date: LocalDate
        val note: String?

        /** Какого обязательного слота не хватает; null — команда полная. */
        fun missingSlot(): MissingSlot?
    }

    data class AddProduction(
        val title: String?,
        val count: Double?,
        val suffix: Suffix?,
        override val date: LocalDate,
        val animal: KnownAnimal? = null,
        override val note: String? = null,
        /** Название взято из лексикона/БД (true) или это сырое слово пользователя. */
        val titleResolved: Boolean = false
    ) : Write {
        override fun missingSlot(): MissingSlot? = when {
            title.isNullOrBlank() -> MissingSlot.TITLE
            count == null || count <= 0.0 -> MissingSlot.COUNT
            else -> null
        }
    }

    data class AddSale(
        val title: String?,
        val count: Double?,
        val suffix: Suffix?,
        val pricePerUnit: Double?,
        val priceTotal: Double?,
        val buyer: String?,
        override val date: LocalDate,
        override val note: String? = null,
        val titleResolved: Boolean = false,
        /** Происхождение проданного продукта; null — неизвестный продукт (запишется как SALE). */
        val productOrigin: ProductOrigin? = null
    ) : Write {
        override fun missingSlot(): MissingSlot? = when {
            title.isNullOrBlank() -> MissingSlot.TITLE
            count == null || count <= 0.0 -> MissingSlot.COUNT
            pricePerUnit == null && priceTotal == null -> MissingSlot.PRICE
            else -> null
        }
    }

    data class AddExpense(
        val title: String?,
        val count: Double?,
        val suffix: Suffix?,
        val pricePerUnit: Double?,
        val priceTotal: Double?,
        val category: String?,
        override val date: LocalDate,
        override val note: String? = null,
        val titleResolved: Boolean = false
    ) : Write {
        override fun missingSlot(): MissingSlot? = when {
            title.isNullOrBlank() -> MissingSlot.TITLE
            pricePerUnit == null && priceTotal == null -> MissingSlot.PRICE
            else -> null
        }
    }

    data class AddAnimal(
        val name: String?,
        /** Тип для `animal_table.type`: «Корова», «Бык», «Куры», «Цыплята» или слово пользователя. */
        val type: String?,
        val species: AnimalSpecies,
        /** `true` — самец; null — не определён (станет самкой, как в форме приложения). */
        val sex: Boolean?,
        /** Поголовье; > 1 — обезличенная группа. */
        val count: Int,
        override val date: LocalDate,
        /** Цена покупки — тогда создаётся и расход. */
        val priceTotal: Double? = null,
        override val note: String? = null
    ) : Write {
        val isGroup: Boolean get() = count > 1

        override fun missingSlot(): MissingSlot? = when {
            type.isNullOrBlank() -> MissingSlot.ANIMAL_TYPE
            !isGroup && name.isNullOrBlank() -> MissingSlot.ANIMAL_NAME
            else -> null
        }
    }

    data class Analysis(
        val kind: AnalysisKind,
        /** Продукт, о котором спрашивают; null — по всем. */
        val product: String?,
        val period: Period,
        /** Вид животного для «сколько коров». */
        val species: AnimalSpecies? = null,
        val periodExplicit: Boolean = true
    ) : ParsedCommand

    data object Help : ParsedCommand
    data object Confirm : ParsedCommand
    data object Cancel : ParsedCommand
    data class Unknown(val text: String) : ParsedCommand

    /** Понятная, но не поддерживаемая операция (продажа/падёж животного): [reason] — что ответить. */
    data class Unsupported(val reason: String) : ParsedCommand
}

enum class MissingSlot { TITLE, COUNT, PRICE, ANIMAL_TYPE, ANIMAL_NAME }
