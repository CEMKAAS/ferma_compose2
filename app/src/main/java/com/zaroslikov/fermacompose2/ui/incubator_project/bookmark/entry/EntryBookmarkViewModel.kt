package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainIncubator
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.IncubatorRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbInt
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryBookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val incubatorRepository: IncubatorRepository,
    private val incubatorTableRepository: IncubatorTableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val resourceProvider: ResourceProvider
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
            is EntryBookmarkIntent.PriceChanged -> updatePrice(intent.value)
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
                val parameter = incubatorRepository.getIncubatorList(itemId).first()
                val parameterDayList =
                    parameter.map { domainIncubator -> domainIncubator.toUiBookmark() }
                updateState { state ->
                    state.copy(
                        isEntry = false,
                        currentProduct = bookmark.toUiBookmark(parameterDayList = parameterDayList),
                    )
                }
            }

            val templatesBookmarkList =
                bookmarkRepository.getBookmarkList(getState().currentProduct.type).first()
            updateState { state ->
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        breedList = breedList,
                        templatesBookmarkList = templatesBookmarkList
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
            val templatesBookmarkList = bookmarkRepository.getBookmarkList(type).first()
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
                    count = count
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
    }

    private fun updateDate(date: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    date = date
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
                    notificationList = state.currentProduct.notificationList.mapIndexed { i, item ->
                        if (i == state.currentProduct.indexNotification) state.currentProduct.currentNotification
                        else item
                    },
                    currentNotification = NotificationParameters()
                )
            )
        }
    }

    private fun updateRemoveNotification(index: Int) {
        updateState { state ->
            val list = state.currentProduct.notificationList.toMutableList()
            list.removeAt(index)

            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = list,
                    currentNotification = NotificationParameters()
                )
            )
        }
    }

    private fun updateChoiceNotification(index: Int) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList.mapIndexed { i, item ->
                        if (i == index) {
                            if (item.isEntry) item.copy(isEntry = false) else item.copy(isEntry = true)
                        } else item.copy(isEntry = false)
                    },
                    currentNotification = if (state.currentProduct.notificationList[index].isEntry)
                        NotificationParameters() else state.currentProduct.notificationList[index],
                    indexNotification = index
                )
            )
        }
    }

    private fun updateCancelNotification() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    notificationList = state.currentProduct.notificationList.mapIndexed { i, item ->
                        if (i == state.currentProduct.indexNotification) item.copy(isEntry = false)
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
                incubatorRepository.getIncubatorList(indexBookmark).first()

            updateState { state ->
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        indexBookmark = indexBookmark,
                        parameterDayList = parameterDayList.map { it.toUiBookmark() }
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
                getState().currentProduct.parameterDayList.forEach {
                    val domain = it.toDomainParameter(
                        itemId = 0,
                        isTemplatesPlan = bookmark.isTemplatesPlan,
                        isEntry = getState().isEntry,
                        itemIdPT = id
                    )
                    Log.i("bookmark", "insert: $domain")
                    incubatorRepository.insertIncubator(domain)
                }
                navigateTo(UiEvent.NavigateBack)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                val bookmark = getState().currentProduct
                bookmarkRepository.update(bookmark.toDomainBookmark(itemIdPT = itemIdPT))
                bookmark.parameterDayList.forEach {
                    incubatorRepository.updateIncubator(
                        it.toDomainParameter(
                            itemIdPT = itemId,
                            isTemplatesPlan = bookmark.isTemplatesPlan,
                            isEntry = getState().isEntry
                        )
                    )
                }
                navigateTo(UiEvent.NavigateBack)
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
    ): DomainIncubator {
        return DomainIncubator(
            id = itemId ?: id,
            day = day,
            temp = if (isTemplatesPlan) temp else tempFact,
            damp = if (isTemplatesPlan) damp else dampFact,
            over = if (isTemplatesPlan) over else overFact,
            airing = if (isTemplatesPlan) airing else airingFact,
            tempFact = if (isEntry) if (isTemplatesPlan) temp else tempFact else tempFact,
            dampFact = if (isEntry) if (isTemplatesPlan) damp else dampFact else dampFact,
            overFact = if (isEntry) if (isTemplatesPlan) over else overFact else overFact,
            airingFact = if (isEntry) if (isTemplatesPlan) airing else airingFact else airingFact,
            note = "",
            idPT = itemIdPT,
        )
    }

    private fun DomainIncubator.toUiBookmark(): ParameterDay {
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
            title = title,
            type = type,
            breed = breed.ifEmpty { null },
            count = count.toConvertDbInt(),
            date = date,
            time = time,
            price = if (price.isEmpty()) null else price.toConvertDbDouble(),
            autoPrice = false,
            note = note,
            isAutoRotation = autoRotation,
            isAutoVentilation = autoVentilation,
            isActivityBookmark = isActivityBookmark,
            idPT = itemIdPT,
        )
    }

    private fun DomainBookmark.toUiBookmark(
        parameterDayList: List<ParameterDay>
    ): EntryBookmark {
        return EntryBookmark(
            id = id,
            title = title,
            type = type,
            breed = breed ?: resourceProvider.getString(R.string.entry_bookmark_not_specified),
            count = count.formatNumber(false),
            date = date,
            time = time,
            price = price?.formatNumber(false) ?: "",
            parameterDayList = parameterDayList,
            autoPrice = autoPrice,
            note = note,
            autoRotation = isAutoRotation,
            autoVentilation = isAutoVentilation,
            isActivityBookmark = isActivityBookmark,
            idPT = idPT
        )
    }


    private fun setIncubator(typeIncubator: TypeEgg): List<ParameterDay> {
        when (typeIncubator) {
            TypeEgg.CHICKENS -> return listOf(
                ParameterDay(1, "37.5", "60", "2-3", "2 раза по 5 минут"),
                ParameterDay(2, "37.5", "60", "2-3", "2 раза по 5 минут"),
                ParameterDay(3, "37.0", "70", "2-3", "2 раза по 5 минут"),
                ParameterDay(4, "37.0", "70", "2-3", "2 раза по 5 минут"),
                ParameterDay(5, "37.0", "70", "2-3", "2 раза по 5 минут"),
                ParameterDay(6, "37.9", "66", "2-3", "нет"),
                ParameterDay(7, "37.9", "66", "2-3", "нет"),
                ParameterDay(8, "37.9", "66", "2-3", "нет"),
                ParameterDay(9, "37.9", "66", "2-3", "нет"),
                ParameterDay(10, "37.9", "66", "2-3", "нет"),
                ParameterDay(11, "37.5", "60", "2-3", "нет"),
                ParameterDay(12, "37.5", "60", "2-3", "2 раза по 5 мин"),
                ParameterDay(13, "37.5", "60", "2-3", "2 раза по 5 мин"),
                ParameterDay(14, "37.5", "60", "2-3", "2 раза по 5 мин"),
                ParameterDay(15, "37.5", "60", "2-3", "2 раза по 5 мин"),
                ParameterDay(16, "37.5", "60", "2-3", "2 раза по 5 мин"),
                ParameterDay(17, "37.3", "60", "2-3", "2 раза по 5 мин"),
                ParameterDay(18, "37.3", "60", "2-3", "2 раза по 20 мин"),
                ParameterDay(19, "37.0", "70", "нет", "2 раза по 20 мин"),
                ParameterDay(20, "37.0", "70", "нет", "2 раза по 5 мин"),
                ParameterDay(21, "37.0", "70", "нет", "2 раза по 5 мин")
            )


            TypeEgg.GEESE -> return listOf(
                ParameterDay(1, "38.0", "65", "3-4", "нет"),
                ParameterDay(2, "37.8", "65", "6", "1 раз по 20 мин"),
                ParameterDay(3, "37.8", "65", "6", "1 раз по 20 мин"),
                ParameterDay(4, "37.6", "70", "6", "1 раз по 20 мин"),
                ParameterDay(5, "37.6", "70", "6", "1 раз по 20 мин"),
                ParameterDay(6, "37.6", "70", "6", "2 раза по 20 мин"),
                ParameterDay(7, "37.6", "70", "6", "2 раза по 20 мин"),
                ParameterDay(8, "37.6", "70", "6", "2 раза по 20 мин"),
                ParameterDay(9, "37.6", "70", "10", "2 раз по 20 мин"),
                ParameterDay(10, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(11, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(12, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(13, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(14, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(15, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(16, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(17, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(18, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(19, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(20, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(21, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(22, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(23, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(24, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(25, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(26, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(27, "37.3", "75", "10", "3 раза по 45 мин"),
                ParameterDay(28, "37.3", "75", "нет", "3 раза по 45 мин"),
                ParameterDay(29, "37.3", "75", "нет", "3 раза по 45 мин"),
                ParameterDay(30, "37.3", "75", "нет", "3 раза по 45 мин")
            )

            TypeEgg.QUAILS -> return listOf(
                ParameterDay(1, "38.0", "55", "3-6", "нет"),
                ParameterDay(2, "38.0", "55", "3-6", "нет"),
                ParameterDay(3, "37.7", "55", "3-6", "нет"),
                ParameterDay(4, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(5, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(6, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(7, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(8, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(9, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(10, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(11, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(12, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(13, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(14, "37.7", "55", "3-6", "1 раз по 5 мин"),
                ParameterDay(15, "37.5", "37,5", "3-6", "нет"),
                ParameterDay(16, "37.5", "37,5", "нет", "нет"),
                ParameterDay(17, "37.5", "37,5", "нет", "нет")
            )


            TypeEgg.TURKEYS -> return listOf(
                ParameterDay(1, "38.0", "60", "6", "нет"),
                ParameterDay(2, "38.0", "60", "6", "нет"),
                ParameterDay(3, "38.0", "60", "6", "нет"),
                ParameterDay(4, "38.0", "60", "6", "нет"),
                ParameterDay(5, "38.0", "60", "6", "нет"),
                ParameterDay(6, "38.0", "60", "6", "нет"),
                ParameterDay(7, "38.0", "60", "6", "нет"),
                ParameterDay(8, "37.7", "45", "6", "2 раза по 5 мин"),
                ParameterDay(9, "37.7", "45", "6", "2 раза по 5 мин"),
                ParameterDay(10, "37.7", "45", "6", "2 раза по 5 мин"),
                ParameterDay(11, "37.7", "45", "6", "2 раза по 5 мин"),
                ParameterDay(12, "37.7", "45", "6", "2 раза по 5 мин"),
                ParameterDay(13, "37.7", "45", "6", "2 раза по 5 мин"),
                ParameterDay(14, "37.7", "65", "6", "2 раза по 5 мин"),
                ParameterDay(15, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(16, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(17, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(18, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(19, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(20, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(21, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(22, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(23, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(24, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(25, "37.5", "65", "4", "4 раза по 10 мин"),
                ParameterDay(26, "37.5", "65", "нет", "нет"),
                ParameterDay(27, "37.5", "65", "нет", "нет"),
                ParameterDay(28, "37.5", "65", "нет", "нет")
            )


            TypeEgg.DUCKS -> return listOf(
                ParameterDay(1, "38.0", "75", "4", "нет"),
                ParameterDay(2, "38.0", "75", "4", "нет"),
                ParameterDay(3, "38.0", "75", "4", "нет"),
                ParameterDay(4, "38.0", "75", "4", "нет"),
                ParameterDay(5, "38.0", "75", "4", "нет"),
                ParameterDay(6, "38.0", "75", "4", "нет"),
                ParameterDay(7, "37.8", "60", "4", "нет"),
                ParameterDay(8, "37.8", "60", "4-6", "нет"),
                ParameterDay(9, "37.8", "60", "4-6", "нет"),
                ParameterDay(10, "37.8", "60", "4-6", "нет"),
                ParameterDay(11, "37.8", "60", "4-6", "нет"),
                ParameterDay(12, "37.8", "60", "4-6", "нет"),
                ParameterDay(13, "37.8", "60", "4-6", "нет"),
                ParameterDay(14, "37.8", "60", "4-6", "нет"),
                ParameterDay(15, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(16, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(17, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(18, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(19, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(20, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(21, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(22, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(23, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(24, "37.8", "60", "6", "2 раза по 15 мин"),
                ParameterDay(25, "37.5", "90", "6", "2 раза по 15 мин"),
                ParameterDay(26, "37.5", "90", "нет", "нет"),
                ParameterDay(27, "37.5", "90", "нет", "нет"),
                ParameterDay(28, "37.5", "90", "нет", "нет")
            )
        }
    }
}

sealed class EntryBookmarkIntent() : BaseIntent {
    data class TitleChanged(val value: String) : EntryBookmarkIntent()
    data class TypeChanged(val value: TypeEgg) : EntryBookmarkIntent()
    data class BreedChanged(val value: String) : EntryBookmarkIntent()
    data class CountChanged(val value: String) : EntryBookmarkIntent()
    data class PriceChanged(val value: String) : EntryBookmarkIntent()
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
    data class ChoiceNotificationClicked(val index: Int) : EntryBookmarkIntent()
    data class RemoveNotificationClicked(val index: Int) : EntryBookmarkIntent()
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