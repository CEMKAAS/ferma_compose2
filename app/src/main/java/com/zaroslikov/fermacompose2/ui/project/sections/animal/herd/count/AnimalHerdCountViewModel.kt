package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.count

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.animal.group.DomainHerdCountRecord
import com.zaroslikov.domain.models.animal.group.DomainHerdOperation
import com.zaroslikov.domain.models.animal.group.DomainHerdOperationItem
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.toMergeCandidate
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.toUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.CountItem
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.DomainAnimalCountPriceUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.ProductKill
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toDomainAnimal
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toFirstCount
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toAnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toPregnancyProgress
import com.zaroslikov.fermacompose2.utils.PregnancyReminderScheduler
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class AnimalHerdCountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalHerdRepository: AnimalHerdRepository,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,
    private val pregnancyRepository: AnimalPregnancyRepository,
    private val pregnancyReminderScheduler: PregnancyReminderScheduler,
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val animalRepository: AnimalRepository,
    private val yandexMetricRepository: YandexMetricRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel2<AnimalHerdCountState, AnimalHerdCountIntent, AnimalHerdCountReduce>(
    AnimalHerdCountState(), AnimalHerdCountReduce(resourceProvider)
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalHerdCountDestination.itemIdPT])
    private val herdId: Long = checkNotNull(savedStateHandle[AnimalHerdCountDestination.itemId])

    init {
        loadData()
    }

    fun onIntent(intent: AnimalHerdCountIntent) {
        when (intent) {
            is AnimalHerdCountIntent.OpenAddMembersSheet ->
                if (intent.value) openAddMembersSheet() else closeAddMembersSheet(intent)

            AnimalHerdCountIntent.MergeConfirmed -> confirmAddMembers()
            AnimalHerdCountIntent.LeaveConfirmed -> leaveSelected()
            is AnimalHerdCountIntent.PickRemoved -> {
                sendIntent(intent)
                // Продажа, списание, забой: убрали последнее животное — лист больше не нужен
                val state = getState()
                if (state.version.sign < 0 && state.pickable.isEmpty())
                    sendIntent(AnimalHerdCountIntent.EntryClosed)
            }
            is AnimalHerdCountIntent.SelectionActionClicked -> openSelectionEntry(intent.version)
            else -> sendIntent(intent)
        }
    }

    /** Интенты листа продажи, списания, забоя — те же, что у счётчика животного. */
    fun onEntryIntent(intent: AnimalCountIntent) {
        when (intent) {
            // Лист открывается только из панели отмеченных членов
            is AnimalCountIntent.DialogClicked ->
                if (!intent.isOpen) sendIntent(AnimalHerdCountIntent.EntryClosed)

            AnimalCountIntent.InsertSalePressed,
            AnimalCountIntent.InsertWriteOffPressed,
            AnimalCountIntent.InsertKillChanged -> submit()

            else -> sendIntent(AnimalHerdCountIntent.Entry(intent))
        }
    }

    /** Интенты листа «Добавить» — та же форма, что при добавлении животного в список. */
    fun onAnimalEntryIntent(intent: AnimalListIntent) {
        when (intent) {
            is AnimalListIntent.OpenBottomSheetEntry ->
                if (intent.value) openAnimalEntry() else closeAnimalEntry()

            AnimalListIntent.Insert -> insertAnimal()
            else -> sendIntent(AnimalHerdCountIntent.AnimalEntry(intent))
        }
    }

    private fun openAnimalEntry() {
        if (getState().animalEntry.openBottomSheetEntry) return
        viewModelScope.launch {
            val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
            val breedList = animalRepository.getBreedAnimal(itemIdPT).first()
            sendIntent(
                AnimalHerdCountIntent.AnimalEntry(
                    AnimalListIntent.RefreshEntryBottomSheetState(
                        true,
                        AnimalEntryState2(itemIdPT = itemIdPT, typeList = typeList, breedList = breedList)
                    )
                )
            )
        }
    }

    private fun closeAnimalEntry() = sendIntent(
        AnimalHerdCountIntent.AnimalEntry(AnimalListIntent.RefreshEntryBottomSheetState(false, AnimalEntryState2()))
    )

    /** Новое животное — сразу в группе; с ценой — покупка (расход в финансах), без — приход. */
    private fun insertAnimal() {
        val form = getState().animalEntry.currentProduct
        if (!form.hasAnyError) return
        runOperation {
            val animal = form.toDomainAnimal(herdId = herdId)
            val (count, expenses) = form.toFirstCount(
                idAnimal = 0,
                itemIdPT = itemIdPT,
                currency = getState().settings.currencySuffix,
                expensesCategory = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses)
            )
            animalHerdRepository.insertAnimalIntoHerd(herdId, animal, count, expenses)
            yandexMetricRepository.metricalAnimal(form)
            closeAnimalEntry()
            showMessage(
                resourceProvider.getString(
                    if (form.isAnimalGroup) R.string.snackbar_animal_add_multiple else R.string.snackbar_animal_add_single
                ).format(form.title.trim())
            )
        }
    }

    /** Группа, её неархивные члены и история поголовья — реактивно. */
    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            combine(
                animalHerdRepository.getHerd(herdId),
                animalHerdRepository.getHerdMembers(herdId),
                pregnancyRepository.getActivePregnanciesByProject(itemIdPT),
                animalHerdRepository.getHerdHistory(herdId),
                settingsRepository.getSettings(itemIdPT)
            ) { herd, members, pregnancies, history, settings ->
                val badges = pregnancies.toPregnancyProgress()
                val active = members.filter { !it.isArchive }
                    .map { it.toAnimalListUi(resourceProvider).copy(pregnancyProgress = badges[it.id]) }
                // Расформирована или не осталось неархивных членов — экран закрываем
                if (herd == null || active.isEmpty()) null
                else AnimalHerdCountIntent.Loaded(
                    herd = herd.toUi(active),
                    history = history.map { it.toHistoryItem() },
                    settings = settings,
                    isArchive = isArchive
                )
            }.collectLatest { loaded ->
                if (loaded == null) leave() else sendIntent(loaded)
            }
        }
    }

    private suspend fun DomainHerdCountRecord.toHistoryItem() = HerdHistoryItem(
        record = DomainAnimalCountPriceUi(
            id = id,
            count = count,
            suffix = suffix,
            date = date,
            animalId = animalId,
            note = note,
            version = version,
            price = price,
            priceAll = priceAll,
            buyer = buyer,
            productKill = if (version == AnimalCountVersion.KILL) productKill(id) else emptyList(),
            femalesCount = femalesCount,
            malesCount = malesCount
        ),
        animalName = animalName
    )

    private suspend fun productKill(countId: Long): List<ProductKill> =
        addRepository.getProductKillList(countId).first().map { add ->
            ProductKill(
                idProduct = add.id,
                title = add.title,
                isEntry = false,
                countProduct = add.count.toString(),
                suffixProduct = add.countSuffix
            )
        }

    private fun leave() = sendIntent(AnimalHerdCountIntent.Closed)

    /**
     * Операция над группой: блокирует повтор, ловит ошибки (нарушение инварианта, сбой БД)
     * и показывает снекбар. Отмену корутины не глотает.
     */
    private fun runOperation(operation: suspend () -> Unit) {
        if (getState().isOperationRunning) return
        sendIntent(AnimalHerdCountIntent.OperationRunningChanged(true))
        viewModelScope.launch {
            try {
                operation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_error))
            } finally {
                sendIntent(AnimalHerdCountIntent.OperationRunningChanged(false))
            }
        }
    }

    // region Продажа, списание, забой, приход

    private var entryJob: Job? = null

    /**
     * Продажа, списание, забой отмеченных: в листе — только они, по умолчанию всё их поголовье
     * (у обезличенной группы количество можно уменьшить).
     */
    private fun openSelectionEntry(version: AnimalCountVersion) {
        val picks = getState().selectedMembers
            .filter { it.totalCount > 0 }
            .associate { it.id to it.totalCount.toCountText() }
        if (picks.isEmpty()) {
            showMessage(resourceProvider.getString(R.string.animal_herd_pick_empty))
            return
        }
        openEntry(version, picks)
    }

    private fun openEntry(version: AnimalCountVersion, picks: Map<Long, String> = emptyMap()) {
        if (getState().entry.isOpenEntryBottomSheet || entryJob?.isActive == true) return
        entryJob = viewModelScope.launch {
            val titles = async { addRepository.getItemsTitleAddList(itemIdPT, false).first() }
            val buyers = async { saleRepository.getItemsBuyerSaleList(itemIdPT).first() }
            sendIntent(
                AnimalHerdCountIntent.EntryOpened(
                    CountItem(
                        version = version,
                        suffix = getState().herd.totalSuffix,
                        itemIdPT = itemIdPT,
                        buyerList = buyers.await(),
                        titleList = titles.await()
                    ),
                    picks
                )
            )
        }
    }

    private fun submit() {
        val state = getState()
        val item = state.entry.currentProduct
        if (!state.isPicksValid || !item.hasAnyError) return
        val version = item.version
        val picks = state.pickable.mapNotNull { member ->
            state.picks[member.id]?.toCountOrZero()?.takeIf { it > 0 }?.let { member to it }
        }
        val rows = picks.map { (member, count) ->
            val (females, males) = state.pickSexCounts(member.id)
            OperationRow(member.id, member.name, member.suffix, count, females, males)
        }
        runOperation {
            // Индивидуальные животные при убыли уходят в архив, DAO прерывает их беременности —
            // напоминания снимаем после успешной операции
            val pregnancyIds = if (version.sign < 0)
                picks.filter { it.first.sex == false }
                    .mapNotNull { (member, _) -> pregnancyRepository.getActivePregnancyId(member.id)?.let { member.id to it } }
                    .toMap()
            else emptyMap()
            val archived = animalHerdRepository.applyHerdOperation(
                DomainHerdOperation(
                    herdId = herdId,
                    version = version,
                    date = item.date,
                    note = item.note.trim(),
                    items = operationItems(item, rows)
                )
            )
            archived.mapNotNull { pregnancyIds[it] }.forEach(pregnancyReminderScheduler::cancel)
            val total = rows.sumOf { it.count }
            yandexMetricRepository.metricalAnimalCount(
                DomainAnimalCount(
                    count = total.toCountText(),
                    suffix = state.herd.totalSuffix,
                    date = item.date,
                    note = item.note,
                    version = version,
                    herdId = herdId
                )
            )
            sendIntent(AnimalHerdCountIntent.EntryClosed)
            if (version.sign < 0) sendIntent(AnimalHerdCountIntent.SelectionCleared)
            showMessage(
                resourceProvider.getString(R.string.snackbar_count_add)
                    .format(total.toCountText(), resourceProvider.getString(state.herd.totalSuffix.toResId()))
            )
        }
    }

    /**
     * Записи по членам. Общая сумма делится пропорционально поголовью (копейки — последнему),
     * цена за голову («автоподсчёт») у каждого своя; продукция забоя — тоже пропорционально.
     */
    private fun operationItems(item: CountItem, rows: List<OperationRow>): List<DomainHerdOperationItem> {
        val total = rows.sumOf { it.count }
        val (day, month, year) = item.date.split(".").map { it.toInt() }
        val price = item.price.toPriceOrNull()
        val priceShares = price?.let { split(it, rows.map { r -> r.count }, total, PRICE_SCALE) }
        val products = item.productKillList.filter { it.isVisibility }
        val productShares = products.map { product ->
            // Штуки не дробим: «1 шкура» не делится на 0,33
            val scale = if (product.suffixProduct == Suffix.PIECES) 0 else PRODUCT_SCALE
            split(product.countProduct.toPriceOrNull() ?: 0.0, rows.map { it.count }, total, scale)
        }
        val currency = getState().settings.currencySuffix
        return rows.mapIndexed { index, row ->
            val count = row.count
            // Автоподсчёт: в поле цена за голову, у члена сумма — за его поголовье
            val memberPrice = if (item.isAutoCalculate) price else priceShares?.get(index)
            val memberPriceAll = if (item.isAutoCalculate) price?.let { round2(it * count) } else null
            val memberProducts = products.mapIndexed { p, product -> product to productShares[p][index] }
            DomainHerdOperationItem(
                animalId = row.animalId,
                femalesCount = row.femalesCount,
                malesCount = row.malesCount,
                count = count,
                sale = if (item.version == AnimalCountVersion.SALE) DomainSaleTable(
                    title = row.name.trim(),
                    count = count,
                    countSuffix = row.suffix,
                    price = memberPrice ?: 0.0,
                    priceAll = memberPriceAll,
                    priceSuffix = currency,
                    productOrigin = null,
                    day = day,
                    month = month,
                    year = year,
                    category = resourceProvider.getString(R.string.animal_card_screen_category_sale),
                    buyer = item.buyer.trim(),
                    note = resourceProvider.getString(R.string.animal_card_screen_note_sale),
                    idPT = itemIdPT
                ) else null,
                writeOff = if (item.version == AnimalCountVersion.WRITE_OFF || item.version == AnimalCountVersion.KILL)
                    DomainWriteOffTable(
                        title = row.name.trim(),
                        count = count,
                        countSuffix = row.suffix,
                        price = memberPrice,
                        priceAll = memberPriceAll,
                        priceSuffix = memberPrice?.let { currency },
                        productOrigin = null,
                        day = day,
                        month = month,
                        year = year,
                        status = item.writeOffStatus,
                        note = if (item.version == AnimalCountVersion.KILL) reasonNote(memberProducts)
                        else resourceProvider.getString(R.string.animal_card_screen_note_write_off),
                        idPT = itemIdPT
                    ) else null,
                expenses = if (item.version == AnimalCountVersion.EXPENSES) DomainExpensesTable(
                    title = row.name.trim(),
                    count = count,
                    countSuffix = row.suffix,
                    day = day,
                    month = month,
                    year = year,
                    price = memberPrice ?: 0.0,
                    priceAll = memberPriceAll,
                    priceSuffix = currency,
                    category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses),
                    note = resourceProvider.getString(R.string.animal_card_screen_note_expenses),
                    idPT = itemIdPT
                ) else null,
                products = if (item.version == AnimalCountVersion.KILL) memberProducts
                    .filter { it.second > 0 }
                    .map { (product, amount) ->
                        DomainAddTable(
                            title = product.title.trim(),
                            count = amount,
                            countSuffix = product.suffixProduct,
                            priceSuffix = currency,
                            day = day,
                            month = month,
                            year = year,
                            category = resourceProvider.getString(R.string.animal_card_screen_category_kill),
                            note = resourceProvider.getString(R.string.animal_card_screen_note_kill),
                            price = 0.0,
                            idPT = itemIdPT
                        )
                    } else emptyList()
            )
        }
    }

    private fun reasonNote(products: List<Pair<ProductKill, Double>>): String =
        resourceProvider.getString(R.string.animal_card_screen_kill_add_product) +
                products.mapIndexed { index, (product, amount) ->
                    "${index + 1}. ${product.title} - ${amount.toCountText()} " +
                            resourceProvider.getString(product.suffixProduct.toResId())
                }.joinToString("\n")

    // endregion

    // region Состав группы

    /** Загрузка кандидатов для листа «Добавить в группу»; отменяется при закрытии листа. */
    private var addMembersJob: Job? = null

    private fun openAddMembersSheet() {
        if (getState().merge.isOpen || addMembersJob?.isActive == true) return
        addMembersJob = viewModelScope.launch {
            val candidates = animalHerdRepository.getStandaloneCandidates(itemIdPT).first()
                .map { it.toMergeCandidate() }
            sendIntent(
                AnimalHerdCountIntent.RefreshMergeState(
                    MergeGroupState(isOpen = true, fixedGroupId = herdId, candidates = candidates)
                )
            )
        }
    }

    private fun closeAddMembersSheet(intent: AnimalHerdCountIntent.OpenAddMembersSheet) {
        addMembersJob?.cancel()
        addMembersJob = null
        sendIntent(intent)
    }

    private fun confirmAddMembers() {
        val merge = getState().merge
        if (!merge.canConfirm) return
        runOperation {
            sendIntent(AnimalHerdCountIntent.RefreshMergeState(getState().merge.copy(isSaving = true)))
            try {
                animalHerdRepository.addMembers(herdId, merge.selectedIds.toList())
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_added))
                sendIntent(AnimalHerdCountIntent.OpenAddMembersSheet(false))
            } catch (e: Exception) {
                sendIntent(AnimalHerdCountIntent.RefreshMergeState(getState().merge.copy(isSaving = false)))
                throw e
            }
        }
    }

    /** Отмеченные члены выходят из группы одной транзакцией. */
    private fun leaveSelected() {
        val members = getState().selectedMembers
        if (members.isEmpty()) return
        runOperation {
            animalHerdRepository.removeMembers(members.map { it.id })
            sendIntent(AnimalHerdCountIntent.OpenLeaveDialog(false))
            sendIntent(AnimalHerdCountIntent.SelectionCleared)
            showMessage(
                resourceProvider.getString(R.string.snackbar_animal_group_left)
                    .format(members.joinToString { it.name })
            )
        }
    }


    // endregion
}

/** Знаков после запятой: сумма — копейки, продукция — граммы при килограммах. */
private const val PRICE_SCALE = 2
private const val PRODUCT_SCALE = 3

private fun String.toPriceOrNull(): Double? =
    replace(',', '.').replace(Regex("[^\\d.]"), "").trim().takeIf { it.isNotEmpty() }?.toDoubleOrNull()

private fun round2(value: Double) = roundTo(value, PRICE_SCALE)

private fun roundTo(value: Double, scale: Int): Double {
    var factor = 1.0
    repeat(scale) { factor *= 10 }
    return round(value * factor) / factor
}

/**
 * Делит [amount] пропорционально [counts] (их сумма — [total]), округляя до [scale] знаков;
 * остаток от округления — последнему, так что сумма долей равна [amount].
 */
private fun split(amount: Double, counts: List<Double>, total: Double, scale: Int): List<Double> {
    if (counts.isEmpty() || total <= 0) return counts.map { 0.0 }
    val shares = counts.dropLast(1).map { roundTo(amount * it / total, scale) }
    return shares + roundTo(amount - shares.sum(), scale)
}

/** Строка операции: член группы и сколько голов уходит. */
private data class OperationRow(
    val animalId: Long,
    val name: String,
    val suffix: Suffix,
    val count: Double,
    /** Обезличенная группа: сколько из [count] самок и самцов. */
    val femalesCount: Int = 0,
    val malesCount: Int = 0
)
