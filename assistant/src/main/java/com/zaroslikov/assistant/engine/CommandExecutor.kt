package com.zaroslikov.assistant.engine

import com.zaroslikov.assistant.model.SavedRecord
import com.zaroslikov.assistant.nlu.ParsedCommand
import com.zaroslikov.assistant.nlu.RuDates
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Категория расхода при покупке животного — как в карточке животного приложения. */
private const val ANIMAL_PURCHASE_CATEGORY = "Покупка животных"

/** Записывает разобранные команды в domain-репозитории и умеет откатить только что сделанную запись. */
class CommandExecutor @Inject constructor(
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val animalRepository: AnimalRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalHerdRepository: AnimalHerdRepository
) {

    suspend fun execute(command: ParsedCommand.Write, idPT: Long, currency: Suffix): SavedRecord = when (command) {
        is ParsedCommand.AddProduction -> insertProduction(command, idPT, currency)
        is ParsedCommand.AddSale -> insertSale(command, idPT, currency)
        is ParsedCommand.AddExpense -> insertExpense(command, idPT, currency)
        is ParsedCommand.AddAnimal -> insertAnimal(command, idPT, currency)
    }

    suspend fun undo(saved: SavedRecord) {
        saved.addId?.let { addRepository.deleteAddById(it) }
        saved.saleId?.let { saleRepository.deleteSaleById(it) }
        saved.expenseId?.let { expensesRepository.deleteExpensesById(it) }
        // Счётчик поголовья удалится каскадом вместе с животным
        saved.animalId?.let { animalHerdRepository.deleteAnimal(it) }
    }

    private suspend fun insertProduction(c: ParsedCommand.AddProduction, idPT: Long, currency: Suffix): SavedRecord {
        val title = requireNotNull(c.title).trim()
        val count = requireNotNull(c.count)
        val item = DomainAddTable(
            title = title,
            count = count,
            day = c.date.dayOfMonth,
            month = c.date.monthValue,
            year = c.date.year,
            price = 0.0,
            countSuffix = c.suffix ?: Suffix.PIECES,
            priceSuffix = currency,
            category = null,
            animalId = c.animal?.id,
            note = c.note.orEmpty(),
            idPT = idPT
        )
        addRepository.insertAdd(item)
        // insertAdd не возвращает id — берём самую свежую запись с теми же полями
        val id = addRepository.getAllItems(idPT).first()
            .filter { it.title == title && it.count == count && it.day == item.day && it.month == item.month && it.year == item.year }
            .maxOfOrNull { it.id }
        return SavedRecord(addId = id)
    }

    private suspend fun insertSale(c: ParsedCommand.AddSale, idPT: Long, currency: Suffix): SavedRecord {
        val title = requireNotNull(c.title).trim()
        val count = requireNotNull(c.count)
        val (price, priceAll) = prices(count, c.pricePerUnit, c.priceTotal)
        val item = DomainSaleTable(
            title = title,
            count = count,
            countSuffix = c.suffix ?: Suffix.PIECES,
            price = price,
            priceAll = priceAll,
            priceSuffix = currency,
            // Своя продукция (ADD) и купленный товар (EXPENSES) должны списаться со склада — как в экране продаж
            productOrigin = c.productOrigin ?: ProductOrigin.SALE,
            day = c.date.dayOfMonth,
            month = c.date.monthValue,
            year = c.date.year,
            category = null,
            buyer = c.buyer?.trim()?.ifEmpty { null },
            note = c.note.orEmpty(),
            idPT = idPT
        )
        saleRepository.insertSale(item)
        val id = saleRepository.getAllSaleItems(idPT).first()
            .filter { it.title == title && it.count == count && it.day == item.day && it.month == item.month && it.year == item.year }
            .maxOfOrNull { it.id }
        return SavedRecord(saleId = id)
    }

    private suspend fun insertExpense(c: ParsedCommand.AddExpense, idPT: Long, currency: Suffix): SavedRecord {
        val title = requireNotNull(c.title).trim()
        val count = c.count ?: 1.0
        val (price, priceAll) = prices(count, c.pricePerUnit, c.priceTotal)
        val item = DomainExpensesTable(
            title = title,
            count = count,
            countSuffix = c.suffix ?: Suffix.PIECES,
            day = c.date.dayOfMonth,
            month = c.date.monthValue,
            year = c.date.year,
            price = price,
            priceAll = priceAll,
            priceSuffix = currency,
            category = c.category?.trim()?.ifEmpty { null },
            note = c.note.orEmpty(),
            idPT = idPT
        )
        val id = expensesRepository.insertExpenses(item)
        return SavedRecord(expenseId = id)
    }

    private suspend fun insertAnimal(c: ParsedCommand.AddAnimal, idPT: Long, currency: Suffix): SavedRecord {
        val type = requireNotNull(c.type).trim()
        val isGroup = c.isGroup
        val sex = c.sex ?: false
        val name = c.name?.trim()?.ifEmpty { null } ?: type
        val date = RuDates.display(c.date)
        val isPurchase = c.priceTotal != null && c.priceTotal > 0
        val animal = DomainAnimalTable(
            name = name,
            type = type,
            species = c.species,
            date = date,
            // У купленного животного названная дата — день поступления, а не рождения
            dateFactory = if (isPurchase) date else null,
            group = isGroup,
            sex = sex,
            note = c.note.orEmpty(),
            idPT = idPT,
            femalesCount = if (isGroup && c.sex == false) c.count else 0,
            malesCount = if (isGroup && c.sex == true) c.count else 0
        )
        val animalId = animalRepository.insertAnimalTable(animal)
        // Три вставки без транзакции: если счётчик или расход не записались, убираем животное,
        // иначе повторное «Записать» создаст дубль
        try {
            return insertAnimalDetails(c, idPT, currency, animalId, date)
        } catch (e: Exception) {
            runCatching { animalHerdRepository.deleteAnimal(animalId) }
            throw e
        }
    }

    private suspend fun insertAnimalDetails(
        c: ParsedCommand.AddAnimal,
        idPT: Long,
        currency: Suffix,
        animalId: Long,
        date: String
    ): SavedRecord {
        val isPurchase = c.priceTotal != null && c.priceTotal > 0
        val name = c.name?.trim()?.ifEmpty { null } ?: requireNotNull(c.type).trim()
        val isGroup = c.isGroup
        val countId = animalCountRepository.insertAnimalCountTable(
            DomainAnimalCount(
                count = c.count.toString(),
                suffix = Suffix.PIECES,
                date = date,
                note = "",
                version = if (isPurchase) AnimalCountVersion.EXPENSES else AnimalCountVersion.ADD,
                idAnimal = animalId,
                femalesCount = if (isGroup && c.sex == false) c.count else 0,
                malesCount = if (isGroup && c.sex == true) c.count else 0
            )
        )
        var expenseId: Long? = null
        if (isPurchase) {
            val total = c.priceTotal!!
            expenseId = expensesRepository.insertExpenses(
                DomainExpensesTable(
                    title = name,
                    count = c.count.toDouble(),
                    countSuffix = Suffix.PIECES,
                    day = c.date.dayOfMonth,
                    month = c.date.monthValue,
                    year = c.date.year,
                    // Как в форме животного: у одиночки price — вся сумма; у группы price — за голову, priceAll — итого
                    price = if (c.count > 1) total / c.count else total,
                    priceAll = if (c.count > 1) total else null,
                    priceSuffix = currency,
                    category = ANIMAL_PURCHASE_CATEGORY,
                    note = "",
                    idPT = idPT,
                    animalId = animalId,
                    animalCountId = countId
                )
            )
        }
        return SavedRecord(animalId = animalId, expenseId = expenseId)
    }

    /**
     * Цена как её хранит приложение: `price` — за единицу, `priceAll` — итого. Если названа только
     * сумма — за единицу считаем сумму / количество; если только цена за единицу — итого = цена × количество.
     */
    private fun prices(count: Double, perUnit: Double?, total: Double?): Pair<Double, Double?> = when {
        perUnit != null && total != null -> perUnit to total
        perUnit != null -> perUnit to (if (count > 0) perUnit * count else null)
        total != null -> (if (count > 0) total / count else total) to total
        else -> 0.0 to null
    }
}
