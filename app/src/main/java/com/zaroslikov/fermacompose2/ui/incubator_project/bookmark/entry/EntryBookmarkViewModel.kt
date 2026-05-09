package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainIncubatorParameters
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubator
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.IncubatorParametersRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.domain.repository.TimeNotificationIncubatorRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepository
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDbInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryBookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: ResourceProvider,
    private val bookmarkRepository: BookmarkRepository,
    private val incubatorTableRepository: IncubatorTableRepository,
    private val incubatorParametersRepository: IncubatorParametersRepository,
    private val timeNotificationIncubatorRepository: TimeNotificationIncubatorRepository,
    private val workManagerRepository: WorkManagerRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel<EntryBookmarkState, EntryBookmarkIntent>(EntryBookmarkState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[EntryBookmarkDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[EntryBookmarkDestination.itemId])

    init {
        Log.i("bookmark", "entryBookmark: $itemIdPT")
        /*  updateState { it.copy(isInsertProject = itemId == -1L) }
          if (itemId != -1L) */ loadData(/*itemId*/)
    }

    override fun onIntent(intent: EntryBookmarkIntent) {
        return when (intent) {
            is EntryBookmarkIntent.TitleChanged -> updateTitle(intent.value)
            is EntryBookmarkIntent.TypeChanged -> updateType(intent.value)
            is EntryBookmarkIntent.BreedChanged -> updateBreed(intent.value)
            is EntryBookmarkIntent.CountChanged -> updateCount(intent.value)
            is EntryBookmarkIntent.RejectedCountChanged -> updateRejectedCount(intent.value)
            is EntryBookmarkIntent.PriceChanged -> updatePrice(intent.value)
            is EntryBookmarkIntent.AutoPriceClicked -> updateAutoPrice(intent.value)
            is EntryBookmarkIntent.DateClicked -> updateDate(intent.value)
            is EntryBookmarkIntent.TimeClicked -> updateTime(intent.value)
            is EntryBookmarkIntent.NoteChanged -> updateNote(intent.value)
            is EntryBookmarkIntent.AutoRotationClicked -> updateAutoRotation(intent.value)
            is EntryBookmarkIntent.AutoVentilationClicked -> updateAutoVentilation(intent.value)
            is EntryBookmarkIntent.TempChanged -> updateTemp(intent.index, intent.value)
            is EntryBookmarkIntent.DampChanged -> updateDamp(intent.index, intent.value)
            is EntryBookmarkIntent.AiringChanged -> updateAiring(intent.index, intent.value)
            is EntryBookmarkIntent.OverChanged -> updateOver(intent.index, intent.value)
            EntryBookmarkIntent.Insert -> insert()
            EntryBookmarkIntent.Update -> update()
            //Notification
            is EntryBookmarkIntent.TimeNotificationChanged -> updateTimeNotification(intent.value)
            is EntryBookmarkIntent.NoteNotificationChanged -> updateNoteNotification(intent.value)
            EntryBookmarkIntent.AddNotificationClicked -> updateAddNotification()
            EntryBookmarkIntent.EditNotificationClicked -> updateEditNotification()
            is EntryBookmarkIntent.RemoveNotificationClicked -> updateRemoveNotification(intent.index)
            is EntryBookmarkIntent.ChoiceNotificationClicked -> updateChoiceNotification(intent.index)
            EntryBookmarkIntent.CancelNotificationClicked -> updateCancelNotification()
            //
            is EntryBookmarkIntent.IndexChoiceBookmarkClicked -> updateIndexChoiceBookmark(intent.value)
            is EntryBookmarkIntent.ByDefaultTemplatesClicked -> updateByDefaultTemplates()
            EntryBookmarkIntent.ChoiceTemplatesBookmarkClicked -> updateChoiceTemplates()

        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isEntry = itemId == -1L
            val breedList =
                bookmarkRepository.getBreedBookmark(getState().currentProduct.type).first()
            val incubatorTable = incubatorTableRepository.getIncubatorById(itemIdPT).first()
            if (isEntry) {
                updateState { state ->
                    state.copy(
                        isEntry = true,
                        currentProduct = state.currentProduct.copy(
                            parameterDayList = setIncubator(state.currentProduct.type),
                            breed = resourceProvider.getString(R.string.entry_bookmark_not_specified),
                            autoRotation = incubatorTable.isAutoRotation,
                            autoVentilation = incubatorTable.isAutoVentilation
                        )
                    )
                }
            } else {
                val bookmark = bookmarkRepository.getBookmark(itemId).first()
                val parameter = incubatorParametersRepository.getIncubatorList(itemId).first()
                val timeNotification =
                    timeNotificationIncubatorRepository.getTimeNotificationByBookmarkId(itemId)
                        .first()
                val parameterDayList =
                    parameter.map { domainIncubator -> domainIncubator.toUiBookmark() }
                updateState { state ->
                    state.copy(
                        isEntry = false,
                        currentProduct = bookmark.toUiBookmark(
                            parameterDayList = parameterDayList,
                            timeNotification
                        ),
                    )
                }
            }

            val templatesBookmarkList =
                bookmarkRepository.getBookmarkList(getState().currentProduct.type, itemIdPT).first()
            updateState { state ->
                state.copy(
                    isLoading = false,
                    currentProduct = state.currentProduct.copy(
                        breedList = breedList,
                        templatesBookmarkList = templatesBookmarkList,
                        incubatorCount = incubatorTable.capacity,
                        currencySuffix = incubatorTable.currencySuffix
                    )
                )
            }
        }
    }

    private fun updateTitle(title: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    title = title
                )
            )
        }
    }

    private fun updateType(type: TypeEgg) {
        viewModelScope.launch {
            val templatesBookmarkList = bookmarkRepository.getBookmarkList(type, itemIdPT).first()
            updateState { state ->
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        type = type,
                        parameterDayList = setIncubator(type),
                        breed = resourceProvider.getString(R.string.entry_bookmark_not_specified),
                        indexBookmark = 0,
                        isTemplatesPlan = true,
                        templatesBookmarkList = templatesBookmarkList
                    )
                )
            }
        }
    }

    private fun updateBreed(breed: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    breed = breed
                )
            )
        }
    }

    private fun updateCount(count: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    count = count,
                    error = state.currentProduct.error.copy(
                        isErrorLargeCount = count.toConvertZeroDbInt() > state.currentProduct.incubatorCount
                    )
                )
            )
        }
        updatePriceAll()
    }

    private fun updateRejectedCount(rejectedCount: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    rejectedCount = rejectedCount,
                    error = state.currentProduct.error.copy(
                        isErrorRejectedCount = rejectedCount.toConvertZeroDbInt() > state.currentProduct.count.toConvertZeroDbInt()
                    )
                )
            )
        }
    }

    private fun updatePrice(price: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    price = price
                )
            )
        }
        updatePriceAll()
    }

    private fun updateAutoPrice(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    priceAll = if (it.currentProduct.isAutoPrice) (it.currentProduct.price.toConvertZeroDouble() * it.currentProduct.count.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    startDate = date
                )
            )
        }
    }

    private fun updateTime(time: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    time = time
                )
            )
        }
    }

    private fun updateAutoRotation(autoRotation: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    autoRotation = autoRotation
                )
            )
        }
    }

    private fun updateAutoVentilation(autoVentilation: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    autoVentilation = autoVentilation,
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    note = note
                )
            )
        }
    }

    private fun updateTemp(index: Int, temp: String) {
        updateState { state ->
            val updatedList = state.currentProduct.parameterDayList
                .mapIndexed { i, item ->
                    if (i == index) {
                        if (state.currentProduct.isTemplatesPlan)
                            item.copy(temp = temp)
                        else item.copy(tempFact = temp)
                    } else item
                }
            state.copy(
                currentProduct = state.currentProduct.copy(
                    parameterDayList = updatedList
                )
            )
        }
    }

    private fun updateDamp(index: Int, damp: String) {
        updateState { state ->
            val updatedList = state.currentProduct.parameterDayList
                .mapIndexed { i, item ->
                    if (i == index) if (state.currentProduct.isTemplatesPlan)
                        item.copy(damp = damp)
                    else item.copy(dampFact = damp)
                    else item
                }
            state.copy(
                currentProduct = state.currentProduct.copy(
                    parameterDayList = updatedList
                )
            )
        }
    }

    private fun updateOver(index: Int, over: String) {
        updateState { state ->
            val updatedList = state.currentProduct.parameterDayList
                .mapIndexed { i, item ->
                    if (i == index) if (state.currentProduct.isTemplatesPlan)
                        item.copy(over = over)
                    else item.copy(overFact = over)
                    else item
                }
            state.copy(
                currentProduct = state.currentProduct.copy(
                    parameterDayList = updatedList
                )
            )
        }
    }

    private fun updateAiring(index: Int, airing: String) {
        updateState { state ->
            val updatedList = state.currentProduct.parameterDayList
                .mapIndexed { i, item ->
                    if (i == index) {
                        if (state.currentProduct.isTemplatesPlan)
                            item.copy(airing = airing)
                        else item.copy(airingFact = airing)
                    } else item
                }
            state.copy(
                currentProduct = state.currentProduct.copy(
                    parameterDayList = updatedList
                )
            )
        }
    }

    private fun updateTimeNotification(time: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    currentNotification = state.currentProduct.currentNotification.copy(
                        time = time
                    )
                )
            )
        }
    }

    private fun updateNoteNotification(note: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    currentNotification = state.currentProduct.currentNotification.copy(
                        note = note
                    )
                )
            )
        }
    }

    private fun updateAddNotification() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList + state.currentProduct.currentNotification.copy(
                        isEntry = false
                    ),
                    currentNotification = NotificationParameters()
                )
            )
        }
    }

    private fun updateEditNotification() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList.map { item ->
                        if (item.localId == state.currentProduct.indexNotification) state.currentProduct.currentNotification
                        else item
                    },
                    currentNotification = NotificationParameters()
                )
            )
        }
    }

    private fun updateRemoveNotification(index: Long) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList.map { notification ->
                        if (notification.localId == index) notification.copy(isVisibility = false) else notification.copy(
                            isEntry = false
                        )
                    },
                    currentNotification = NotificationParameters()
                )
            )
        }
    }

    private fun updateChoiceNotification(index: Long) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList.map { item ->
                        if (item.localId == index) {
                            if (item.isEntry) item.copy(isEntry = false) else item.copy(isEntry = true)
                        } else item.copy(isEntry = false)
                    },
                    currentNotification = if (state.currentProduct.notificationList.find { it.localId == index }?.isEntry
                            ?: false
                    )
                        NotificationParameters() else state.currentProduct.notificationList.find { it.localId == index }
                        ?: NotificationParameters(),
                    indexNotification = index
                )
            )
        }
    }

    private fun updateCancelNotification() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList.map { item ->
                        if (item.localId == state.currentProduct.indexNotification)
                            item.copy(isEntry = false)
                        else item
                    },
                    currentNotification = NotificationParameters()
                )
            )
        }
    }

    private fun updateIndexChoiceBookmark(indexBookmark: Long) {
        viewModelScope.launch {
            val parameterDayList =
                incubatorParametersRepository.getIncubatorList(indexBookmark).first()
            val updatedList =
                getState().currentProduct.parameterDayList.mapIndexed { index, itemB ->
                    val source = parameterDayList[index]
                    itemB.copy(
                        temp = source.temp,
                        damp = source.damp,
                        over = source.over,
                        airing = source.airing,
                        tempFact = source.tempFact,
                        dampFact = source.dampFact,
                        overFact = source.overFact,
                        airingFact = source.airingFact,
                        note = source.note,
                        idPT = source.idPT
                    )
                }
            updateState { state ->
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        indexBookmark = indexBookmark,
                        parameterDayList = updatedList
                    )
                )
            }
        }
    }

    private fun updateByDefaultTemplates() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    indexBookmark = 0,
                    parameterDayList = setIncubator(getState().currentProduct.type),
                    isTemplatesPlan = true
                )
            )
        }
    }

    private fun updateChoiceTemplates() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isTemplatesPlan = !state.currentProduct.isTemplatesPlan
                )
            )
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                val bookmark = getState().currentProduct
                Log.i("bookmark", "insert: yes")
                Log.i(
                    "bookmark",
                    "insert bookmark: ${bookmark.toDomainBookmark(itemIdPT = itemIdPT)}"
                )
                val id = bookmarkRepository.insert(bookmark.toDomainBookmark(itemIdPT = itemIdPT))
                bookmark.notificationList.filter { it.isVisibility }.forEach {
                    timeNotificationIncubatorRepository.insertTimeNotification(it.toDomain(id))
                }
                yandexMetricRepository.metricalBookmark(bookmark)
                updateNotifications()
                getState().currentProduct.parameterDayList.forEach {
                    val domain = it.toDomainParameter(
                        itemId = 0,
                        isTemplatesPlan = bookmark.isTemplatesPlan,
                        isEntry = getState().isEntry,
                        itemIdPT = id
                    )
                    Log.i("bookmark", "insert: $domain")
                    incubatorParametersRepository.insertIncubator(domain)
                }
                Log.i(
                    "bookmark",
                    "insert bookmark: ${bookmark.toDomainBookmark(itemIdPT = itemIdPT)}"
                )
                navigateTo(UiEvent.NavigateBack)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                val bookmark = getState().currentProduct
                bookmarkRepository.update(bookmark.toDomainBookmark(itemIdPT = itemIdPT))
                bookmark.notificationList.forEach {
                    when {
                        it.id == 0L && it.isVisibility ->
                            timeNotificationIncubatorRepository.insertTimeNotification(
                                it.toDomain(
                                    newBookmarkId = bookmark.id
                                )
                            )

                        it.id != 0L && it.isVisibility ->
                            timeNotificationIncubatorRepository.updateTimeNotification(it.toDomain())

                        it.id != 0L && !it.isVisibility ->
                            timeNotificationIncubatorRepository.deleteTimeNotificationById(it.id)

                        else -> Unit
                    }
                }
                updateNotifications()
                bookmark.parameterDayList.forEach {
                    val domain = it.toDomainParameter(
                        itemIdPT = itemId,
                        isTemplatesPlan = bookmark.isTemplatesPlan,
                        isEntry = getState().isEntry
                    )
                    incubatorParametersRepository.updateIncubator(domain)
                    Log.i("bookmark", "insert: $domain")
                }
                navigateTo(UiEvent.NavigateBack)
            }
        }
    }

    suspend fun updateNotifications() {
        timeNotificationIncubatorRepository.getTimeNotificationInAllActiveBookmark()// Переделать список!
            .first()
            .let { list ->
                workManagerRepository.cancelIncubatorNotification()
                list.forEach { item ->
                    workManagerRepository.scheduleReminderIncubator(
                        name = item.nameBookmark.trim(),
                        time = item.time,
                        bookmarkId = item.bookmarkId,
                        note = item.note?.trim(),
                        projectId = item.projectId
                    )
                }
            }
    }


    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank(),
                        isErrorCount = state.currentProduct.count.isBlank(),
                    )
                )
            )
        }
    }

    private fun ParameterDay.toDomainParameter(
        itemId: Long? = null,
        isTemplatesPlan: Boolean,
        itemIdPT: Long,
        isEntry: Boolean,
    ): DomainIncubatorParameters {
        return DomainIncubatorParameters(
            id = itemId ?: id,
            day = day,
            temp = if (isTemplatesPlan) temp.trim() else tempFact.trim(),
            damp = if (isTemplatesPlan) damp.trim() else dampFact.trim(),
            over = if (isTemplatesPlan) over.trim() else overFact.trim(),
            airing = if (isTemplatesPlan) airing.trim() else airingFact.trim(),
            tempFact = if (isEntry) if (isTemplatesPlan) temp.trim() else tempFact.trim() else tempFact.trim(),
            dampFact = if (isEntry) if (isTemplatesPlan) damp.trim() else dampFact.trim() else dampFact.trim(),
            overFact = if (isEntry) if (isTemplatesPlan) over.trim() else overFact.trim() else overFact.trim(),
            airingFact = if (isEntry) if (isTemplatesPlan) airing.trim() else airingFact.trim() else airingFact.trim(),
            note = note.trim(),
            idPT = itemIdPT,
        )
    }

    private fun DomainIncubatorParameters.toUiBookmark(): ParameterDay {
        return ParameterDay(
            id = id,
            day = day,
            temp = temp,
            damp = damp,
            over = over,
            airing = airing,
            tempFact = tempFact,
            dampFact = dampFact,
            overFact = overFact,
            airingFact = airingFact,
            note = note,
            idPT = itemIdPT,
        )
    }

    private fun EntryBookmark.toDomainBookmark(
        itemIdPT: Long
    ): DomainBookmark {
        return DomainBookmark(
            id = id,
            title = title.trim(),
            type = type,
            breed = breed.trim().ifEmpty { null },
            count = count.toConvertDbInt(),
            rejectedCount = rejectedCount.toConvertZeroDbInt(),
            startDate = startDate,
            endDate = endDate,
            time = time,
            price = if (price.isEmpty()) null else price.toConvertDbDouble(),
            priceAll = if (price.isEmpty()) null else if (isAutoPrice) priceAll.toConvertDbDouble() else null,
            priceSuffix = if (price.isEmpty()) null else currencySuffix,
            note = note.trim(),
            isAutoRotation = autoRotation,
            isAutoVentilation = autoVentilation,
            isActivityBookmark = isActivityBookmark,
            idPT = itemIdPT,
        )
    }

    private fun DomainBookmark.toUiBookmark(
        parameterDayList: List<ParameterDay>,
        domainTimeNotificationIncubator: List<DomainTimeNotificationIncubator>
    ): EntryBookmark {
        return EntryBookmark(
            id = id,
            title = title.trim(),
            type = type,
            breed = breed?.trim()
                ?: resourceProvider.getString(R.string.entry_bookmark_not_specified),
            count = count.formatNumber(false),
            rejectedCount = rejectedCount.formatNumber(false),
            startDate = startDate,
            endDate = endDate,
            time = time,
            price = price?.formatNumber(false) ?: "",
            priceAll = priceAll?.formatNumber(false) ?: "",
            isAutoPrice = priceAll != null,
            note = note.trim(),
            autoRotation = isAutoRotation,
            autoVentilation = isAutoVentilation,
            parameterDayList = parameterDayList,
            notificationList = domainTimeNotificationIncubator.map { it.toUi() },
            isActivityBookmark = isActivityBookmark,
            idPT = idPT,
        )
    }

    private fun DomainTimeNotificationIncubator.toUi(): NotificationParameters {
        return NotificationParameters(
            id = id,
            time = time,
            note = note ?: "",
            isEntry = false,
            isVisibility = true,
            bookmarkId = bookmarkId
        )
    }

    private fun NotificationParameters.toDomain(newBookmarkId: Long? = null): DomainTimeNotificationIncubator {
        return DomainTimeNotificationIncubator(
            id = id,
            time = time,
            note = note.trim().ifBlank { null },
            bookmarkId = newBookmarkId ?: bookmarkId
        )
    }

    private fun setIncubator(typeIncubator: TypeEgg): List<ParameterDay> {
        when (typeIncubator) {
            TypeEgg.CHICKENS -> return listOf(
                ParameterDay(1, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(2, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(3, "37.0", "70", "2 - 3", "2 x 5"),
                ParameterDay(4, "37.0", "70", "2 - 3", "2 x 5"),
                ParameterDay(5, "37.0", "70", "2 - 3", "2 x 5"),
                ParameterDay(6, "37.9", "66", "2 - 3", "-"),
                ParameterDay(7, "37.9", "66", "2 - 3", "-"),
                ParameterDay(8, "37.9", "66", "2 - 3", "-"),
                ParameterDay(9, "37.9", "66", "2 - 3", "-"),
                ParameterDay(10, "37.9", "66", "2 - 3", "-"),
                ParameterDay(11, "37.5", "60", "2 - 3", "-"),
                ParameterDay(12, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(13, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(14, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(15, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(16, "37.5", "60", "2 - 3", "2 x 5"),
                ParameterDay(17, "37.3", "60", "2 - 3", "2 x 5"),
                ParameterDay(18, "37.3", "60", "2 - 3", "2 x 20"),
                ParameterDay(19, "37.0", "70", "-", "2 x 20"),
                ParameterDay(20, "37.0", "70", "-", "2 x 5"),
                ParameterDay(21, "37.0", "70", "-", "2 x 5")
            )


            TypeEgg.GEESE -> return listOf(
                ParameterDay(1, "38.0", "65", "3 - 4", "-"),
                ParameterDay(2, "37.8", "65", "6", "1 x 20"),
                ParameterDay(3, "37.8", "65", "6", "1 x 20"),
                ParameterDay(4, "37.6", "70", "6", "1 x 20"),
                ParameterDay(5, "37.6", "70", "6", "1 x 20"),
                ParameterDay(6, "37.6", "70", "6", "2 x 20"),
                ParameterDay(7, "37.6", "70", "6", "2 x 20"),
                ParameterDay(8, "37.6", "70", "6", "2 x 20"),
                ParameterDay(9, "37.6", "70", "10", "2 x 20"),
                ParameterDay(10, "37.3", "75", "10", "3 x 45"),
                ParameterDay(11, "37.3", "75", "10", "3 x 45"),
                ParameterDay(12, "37.3", "75", "10", "3 x 45"),
                ParameterDay(13, "37.3", "75", "10", "3 x 45"),
                ParameterDay(14, "37.3", "75", "10", "3 x 45"),
                ParameterDay(15, "37.3", "75", "10", "3 x 45"),
                ParameterDay(16, "37.3", "75", "10", "3 x 45"),
                ParameterDay(17, "37.3", "75", "10", "3 x 45"),
                ParameterDay(18, "37.3", "75", "10", "3 x 45"),
                ParameterDay(19, "37.3", "75", "10", "3 x 45"),
                ParameterDay(20, "37.3", "75", "10", "3 x 45"),
                ParameterDay(21, "37.3", "75", "10", "3 x 45"),
                ParameterDay(22, "37.3", "75", "10", "3 x 45"),
                ParameterDay(23, "37.3", "75", "10", "3 x 45"),
                ParameterDay(24, "37.3", "75", "10", "3 x 45"),
                ParameterDay(25, "37.3", "75", "10", "3 x 45"),
                ParameterDay(26, "37.3", "75", "10", "3 x 45"),
                ParameterDay(27, "37.3", "75", "10", "3 x 45"),
                ParameterDay(28, "37.3", "75", "-", "3 x 45"),
                ParameterDay(29, "37.3", "75", "-", "3 x 45"),
                ParameterDay(30, "37.3", "75", "-", "3 x 45")
            )

            TypeEgg.QUAILS -> return listOf(
                ParameterDay(1, "38.0", "55", "3 - 6", "-"),
                ParameterDay(2, "38.0", "55", "3 - 6", "-"),
                ParameterDay(3, "37.7", "55", "3 - 6", "-"),
                ParameterDay(4, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(5, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(6, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(7, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(8, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(9, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(10, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(11, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(12, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(13, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(14, "37.7", "55", "3 - 6", "1 x 5"),
                ParameterDay(15, "37.5", "37.5", "3 - 6", "-"),
                ParameterDay(16, "37.5", "37.5", "нет", "-"),
                ParameterDay(17, "37.5", "37.5", "нет", "-")
            )


            TypeEgg.TURKEYS -> return listOf(
                ParameterDay(1, "38.0", "60", "6", "-"),
                ParameterDay(2, "38.0", "60", "6", "-"),
                ParameterDay(3, "38.0", "60", "6", "-"),
                ParameterDay(4, "38.0", "60", "6", "-"),
                ParameterDay(5, "38.0", "60", "6", "-"),
                ParameterDay(6, "38.0", "60", "6", "-"),
                ParameterDay(7, "38.0", "60", "6", "-"),
                ParameterDay(8, "37.7", "45", "6", "2 x 5"),
                ParameterDay(9, "37.7", "45", "6", "2 x 5"),
                ParameterDay(10, "37.7", "45", "6", "2 x 5"),
                ParameterDay(11, "37.7", "45", "6", "2 x 5"),
                ParameterDay(12, "37.7", "45", "6", "2 x 5"),
                ParameterDay(13, "37.7", "45", "6", "2 x 5"),
                ParameterDay(14, "37.7", "65", "6", "2 x 5"),
                ParameterDay(15, "37.5", "65", "4", "4 x 10"),
                ParameterDay(16, "37.5", "65", "4", "4 x 10"),
                ParameterDay(17, "37.5", "65", "4", "4 x 10"),
                ParameterDay(18, "37.5", "65", "4", "4 x 10"),
                ParameterDay(19, "37.5", "65", "4", "4 x 10"),
                ParameterDay(20, "37.5", "65", "4", "4 x 10"),
                ParameterDay(21, "37.5", "65", "4", "4 x 10"),
                ParameterDay(22, "37.5", "65", "4", "4 x 10"),
                ParameterDay(23, "37.5", "65", "4", "4 x 10"),
                ParameterDay(24, "37.5", "65", "4", "4 x 10"),
                ParameterDay(25, "37.5", "65", "4", "4 x 10"),
                ParameterDay(26, "37.5", "65", "-", "-"),
                ParameterDay(27, "37.5", "65", "", "-"),
                ParameterDay(28, "37.5", "65", "-", "-")
            )


            TypeEgg.DUCKS -> return listOf(
                ParameterDay(1, "38.0", "75", "4", "-"),
                ParameterDay(2, "38.0", "75", "4", "-"),
                ParameterDay(3, "38.0", "75", "4", "-"),
                ParameterDay(4, "38.0", "75", "4", "-"),
                ParameterDay(5, "38.0", "75", "4", "-"),
                ParameterDay(6, "38.0", "75", "4", "-"),
                ParameterDay(7, "37.8", "60", "4", "-"),
                ParameterDay(8, "37.8", "60", "4 - 6", "-"),
                ParameterDay(9, "37.8", "60", "4 - 6", "-"),
                ParameterDay(10, "37.8", "60", "4 - 6", "-"),
                ParameterDay(11, "37.8", "60", "4 - 6", "-"),
                ParameterDay(12, "37.8", "60", "4 - 6", "-"),
                ParameterDay(13, "37.8", "60", "4 - 6", "-"),
                ParameterDay(14, "37.8", "60", "4 - 6", "-"),
                ParameterDay(15, "37.8", "60", "6", "2 x 15"),
                ParameterDay(16, "37.8", "60", "6", "2 x 15"),
                ParameterDay(17, "37.8", "60", "6", "2 x 15"),
                ParameterDay(18, "37.8", "60", "6", "2 x 15"),
                ParameterDay(19, "37.8", "60", "6", "2 x 15"),
                ParameterDay(20, "37.8", "60", "6", "2 x 15"),
                ParameterDay(21, "37.8", "60", "6", "2 x 15"),
                ParameterDay(22, "37.8", "60", "6", "2 x 15"),
                ParameterDay(23, "37.8", "60", "6", "2 x 15"),
                ParameterDay(24, "37.8", "60", "6", "2 x 15"),
                ParameterDay(25, "37.5", "90", "6", "2 x 15 "),
                ParameterDay(26, "37.5", "90", "-", "-"),
                ParameterDay(27, "37.5", "90", "-", "-"),
                ParameterDay(28, "37.5", "90", "-", "-")
            )
        }
    }
}

sealed class EntryBookmarkIntent() : BaseIntent {
    data class TitleChanged(val value: String) : EntryBookmarkIntent()
    data class TypeChanged(val value: TypeEgg) : EntryBookmarkIntent()
    data class BreedChanged(val value: String) : EntryBookmarkIntent()
    data class CountChanged(val value: String) : EntryBookmarkIntent()
    data class RejectedCountChanged(val value: String) : EntryBookmarkIntent()
    data class PriceChanged(val value: String) : EntryBookmarkIntent()
    data class AutoPriceClicked(val value: Boolean) : EntryBookmarkIntent()
    data class DateClicked(val value: String) : EntryBookmarkIntent()
    data class TimeClicked(val value: String) : EntryBookmarkIntent()
    data class NoteChanged(val value: String) : EntryBookmarkIntent()
    data class TempChanged(val index: Int, val value: String) : EntryBookmarkIntent()
    data class DampChanged(val index: Int, val value: String) : EntryBookmarkIntent()
    data class OverChanged(val index: Int, val value: String) : EntryBookmarkIntent()
    data class AiringChanged(val index: Int, val value: String) : EntryBookmarkIntent()
    data class AutoVentilationClicked(val value: Boolean) : EntryBookmarkIntent()
    data class AutoRotationClicked(val value: Boolean) : EntryBookmarkIntent()

    //Notification
    data class TimeNotificationChanged(val value: String) : EntryBookmarkIntent()
    data class NoteNotificationChanged(val value: String) : EntryBookmarkIntent()
    data class ChoiceNotificationClicked(val index: Long) : EntryBookmarkIntent()
    data class RemoveNotificationClicked(val index: Long) : EntryBookmarkIntent()
    data object CancelNotificationClicked : EntryBookmarkIntent()
    data object AddNotificationClicked : EntryBookmarkIntent()
    data object EditNotificationClicked : EntryBookmarkIntent()

    data object Insert : EntryBookmarkIntent()
    data object Update : EntryBookmarkIntent()

    //
    data class IndexChoiceBookmarkClicked(val value: Long) : EntryBookmarkIntent()
    data object ByDefaultTemplatesClicked : EntryBookmarkIntent()
    data object ChoiceTemplatesBookmarkClicked : EntryBookmarkIntent()
}