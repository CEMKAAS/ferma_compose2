package com.zaroslikov.assistant.engine

import com.zaroslikov.assistant.nlu.KnownAnimal
import com.zaroslikov.assistant.nlu.KnownProduct
import com.zaroslikov.assistant.nlu.NluContext
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/** Что помощник знает о проекте перед разбором фразы. */
data class ProjectContext(
    val idPT: Long,
    val projectTitle: String,
    val currency: Suffix,
    val nlu: NluContext
)

/** Собирает [ProjectContext] из domain-репозиториев: названия продукции, животные, покупатели, категории. */
class AssistantContextLoader @Inject constructor(
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val animalRepository: AnimalRepository,
    private val settingsRepository: SettingsRepository,
    private val projectRepository: ProjectRepository
) {

    suspend fun load(idPT: Long): ProjectContext {
        val production = addRepository.getItemsTitleAddList(idPT, false).firstOr(emptyList())
            .map { KnownProduct(it.title, it.suffix, isExpense = false) }
        val expenses = expensesRepository.getItemsTitleExpensesList(idPT).firstOr(emptyList())
            .map { KnownProduct(it.title, it.suffix, isExpense = true) }
        val sales = saleRepository.getItemsTitleSaleList(idPT).firstOr(emptyList())
            .map { KnownProduct(it.title, it.suffix, isExpense = it.productOrigin == ProductOrigin.EXPENSES, origin = it.productOrigin) }
        val titles = (production + expenses + sales).distinctBy { it.title.trim().lowercase() to it.isExpense }

        val animals = animalRepository.getAllAnimalWithGroups(idPT).firstOr(emptyList())
            .filter { !it.isArchive }
            .map { KnownAnimal(it.id, it.name, it.type, it.group) }
        val buyers = saleRepository.getItemsBuyerSaleList(idPT).firstOr(emptyList()).map { it.trim() }.filter { it.isNotEmpty() }
        val categories = expensesRepository.getItemsCategoryExpensesList(idPT).firstOr(emptyList()).map { it.trim() }.filter { it.isNotEmpty() }
        val currency = try {
            settingsRepository.getSettings(idPT).first().currencySuffix
        } catch (e: Exception) {
            Suffix.RUBLE
        }
        val title = try {
            projectRepository.getProject(idPT).first().title
        } catch (e: Exception) {
            ""
        }
        return ProjectContext(
            idPT = idPT,
            projectTitle = title,
            currency = currency,
            nlu = NluContext(
                productTitles = titles,
                animals = animals,
                buyers = buyers.distinct(),
                expenseCategories = categories.distinct(),
                today = LocalDate.now()
            )
        )
    }

    private suspend fun <T> Flow<T>.firstOr(default: T): T =
        try {
            catch { emit(default) }.first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            default
        }
}
