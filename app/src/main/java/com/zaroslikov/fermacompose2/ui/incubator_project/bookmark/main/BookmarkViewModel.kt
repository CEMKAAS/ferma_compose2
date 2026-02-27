package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainIncubatorParameters
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.IncubatorParametersRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.supportFun.currentTime
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDbInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val incubatorTableRepository: IncubatorTableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val incubatorParametersRepository: IncubatorParametersRepository,
    private val resourceProvider: ResourceProvider,
    private val settingsRepository: SettingsRepository,
    private val animalRepository: AnimalRepository,
    private val expensesRepository: ExpensesRepository,
    private val animalCountRepository: AnimalCountRepository,
) : BaseViewModel<BookmarkState, BookmarkIntent>(BookmarkState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[BookmarkDestination.itemIdPT])

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val incubator = incubatorTableRepository.getIncubatorByIdPT(itemIdPT).first()
            updateState { state ->
                state.copy(
                    itemIdPT = itemIdPT,
                    incubatorId = incubator.id
                )
            }
            bookmarkRepository.getActivityBookmark(incubator.id)
                .flatMapLatest { bookmark ->
                    if (bookmark == null) flowOf(null to emptyList())
                    else incubatorParametersRepository.getIncubatorList(bookmark.id)
                        .map { parameters ->
                            bookmark to parameters
                        }
                }
                .collect { (bookmark, parameters) ->
                    if (bookmark == null) {
                        updateState {
                            it.copy(
                                isActivityBookmark = false,
                                isLoading = false
                            )
                        }
                        return@collect
                    }
                    val numberDays = parameters.size
                    val (currentDay, startDay, endDay) = dayNumberWithTime(
                        startDate = bookmark.startDate,
                        startTime = bookmark.time,
                        numberDays = numberDays
                    )
                    val currentParameter =
                        searchCurrentParameter(
                            currentDay = currentDay,
                            parametersList = parameters
                        ).toUi(typeEgg = bookmark.type)
                    val tomorrowParameter = searchCurrentParameter(
                        currentDay = currentDay + 1,
                        parametersList = parameters
                    ).toUi(typeEgg = bookmark.type)
                    val percent = percentWithTime(
                        startDate = bookmark.startDate, startTime = bookmark.time,
                        numberDays = numberDays
                    )
                    val isBookmarkCompleted = currentDay > numberDays
                    updateState { state ->
                        state.copy(
                            domainBookmark = bookmark,
                            idBookmark = bookmark.id,
                            isActivityBookmark = bookmark.isActivityBookmark,
                            isBookmarkCompleted = isBookmarkCompleted,
                            numberDays = numberDays,
                            currentDay = currentDay,
                            startDay = startDay,
                            endDay = endDay,
                            currentEgg = bookmark.count - bookmark.rejectedCount,
                            daysToEnd = if (isBookmarkCompleted) 0 else numberDays - currentDay,
                            percent = percent.first,
                            percentFloat = percent.second,
                            parameterDayList = parameters.map { it.toUi(bookmark.type) },
                            currentParameterDay = currentParameter,
                            tomorrowParameterDay = tomorrowParameter,
                            isLoading = false,
                            isCompleteModeEnd = currentDay > numberDays - 1
                        )
                    }
                    if (isBookmarkCompleted) updateCompleteIncubationBottomSheet(true)
                }
        }
    }

    fun onIntent(intent: BookmarkIntent) {
        when (intent) {
            //CurrentParameter
            is BookmarkIntent.TempChanged -> updateTemp(intent.value)
            is BookmarkIntent.DampChanged -> updateDamp(intent.value)
            is BookmarkIntent.OverChanged -> updateOver(intent.value)
            is BookmarkIntent.AiringChanged -> updateAiring(intent.value)
            is BookmarkIntent.NoteChanged -> updateNote(intent.value)
            is BookmarkIntent.OpenBottomSheetClick -> updateOpenBottomSheet(intent.value)

            //Ovoscopy
            is BookmarkIntent.OpenOvoscopBottomSheetClick -> updateOpenOvoscopBottomSheet(intent.value)
            is BookmarkIntent.CompleteOvoscopClick -> updateComplete(intent.value)
            BookmarkIntent.LanternClick -> updateLantern()
            is BookmarkIntent.RejectedEggChanged -> updateRejectedEgg(intent.value)
            BookmarkIntent.SaveEggClick -> saveEgg()

            //CompleteParameter
            is BookmarkIntent.OpenCompleteIncubationBottomSheetClick ->
                updateCompleteIncubationBottomSheet(intent.value)

            is BookmarkIntent.IndexChoiceProjectClick -> updateIndexChoiceProject(intent.value)
            is BookmarkIntent.ChoiceProjectModeClick -> updateChoiceProjectMode(intent.value)
            BookmarkIntent.CompleteIncubatorClick -> completeIncubatorClick()
            is BookmarkIntent.NameProjectChanged -> updateNewNameProject(intent.value)
            is BookmarkIntent.ChicksBredChanged -> updateChicksBred(intent.value)
            is BookmarkIntent.ChicksPriceChanged -> updateChicksPrice(intent.value)

            //EarlyCompleteParameter
            BookmarkIntent.EarlyCompleteIncubatorClick -> earlyCompleteIncubator()
            is BookmarkIntent.ReasonNoteChanged -> updateReasonNote(intent.value)

            //EditCurrentParameter
            is BookmarkIntent.OpenEditBottomSheetClick -> updateOpenEditBottomSheet(
                intent.value,
                intent.parameterDay
            )

            BookmarkIntent.SaveParameterClick -> updateEditCurrentParameterDay()
            is BookmarkIntent.TempFactChanged -> updateTempFact(intent.value)
            is BookmarkIntent.DampFactChanged -> updateDampFact(intent.value)
            is BookmarkIntent.OverFactChanged -> updateOverFact(intent.value)
            is BookmarkIntent.AiringFactChanged -> updateAiringFact(intent.value)
            is BookmarkIntent.NoteFactChanged -> updateNoteFact(intent.value)
        }
    }

    private fun updateOpenEditBottomSheet(
        isOpenEditBottomSheet: Boolean,
        editParameterDay: ParametersIncubatorUi
    ) {
        updateState { state ->
            state.copy(
                isOpenEditBottomSheet = isOpenEditBottomSheet,
                editParameterDay = editParameterDay
            )
        }
    }

    private fun dayNumberWithTime(
        startDate: String,
        startTime: String,
        numberDays: Int
    ): Triple<Int, String, String> {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

        val start = LocalDateTime.parse("$startDate $startTime", dateTimeFormatter)
        val current = LocalDateTime.parse("${dateToday()} ${currentTime()}", dateTimeFormatter)
        val currentDay =
            ChronoUnit.DAYS.between(start, current).toInt() + 1

        val endDate = start
            .plusDays(numberDays.toLong())
            .format(dateFormatter)
        val startDateNew = start.format(dateFormatter)
        return Triple(currentDay, startDateNew, endDate)
    }

    private fun percentWithTime(
        startDate: String,
        startTime: String,
        numberDays: Int
    ): Pair<Double, Float> {

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val start = LocalDateTime.parse("$startDate $startTime", formatter)
        val end = start.plusDays(numberDays.toLong())
        val now = LocalDateTime.parse("${dateToday()} ${currentTime()}", formatter)

        val totalMillis = ChronoUnit.MILLIS.between(start, end).toDouble()
        val passedMillis = ChronoUnit.MILLIS.between(start, now).toDouble()

        val fraction = (passedMillis / totalMillis)
            .coerceIn(0.0, 1.0)

        return fraction * 100.0 to fraction.toFloat()
    }

    private fun setOvoskop(day: Int, typeEgg: TypeEgg): OvoskopyState {
        val firstOvoskopySup =
            resourceProvider.getString(R.string.bookmark_screen_first_ovoscopy)
        val secondOvoskopySup =
            resourceProvider.getString(R.string.bookmark_screen_second_ovoscopy)
        val thirdOvoskopySup =
            resourceProvider.getString(R.string.bookmark_screen_third_ovoscopy)

        val firstOvoskopyTitle =
            resourceProvider.getString(R.string.bookmark_screen_first_ovoscopy_title).format(day)
        val secondOvoskopyTitle =
            resourceProvider.getString(R.string.bookmark_screen_second_ovoscopy_tilte).format(day)
        val thirdOvoskopyTitle =
            resourceProvider.getString(R.string.bookmark_screen_third_ovoscopy_title).format(day)

        val firstOvoskopyTitleSup =
            resourceProvider.getString(R.string.bookmark_screen_first_ovoscopy_title_sup)
        val secondOvoskopyTitleSup =
            resourceProvider.getString(R.string.bookmark_screen_second_ovoscopy_tilte_sup)
        val thirdOvoskopyTitleSup =
            resourceProvider.getString(R.string.bookmark_screen_third_ovoscopy_title_sup)

        return when (typeEgg) {
            TypeEgg.CHICKENS -> when (day) {
                7 -> OvoskopyState(
                    true,
                    firstOvoskopySup,
                    R.drawable.chiken1,
                    firstOvoskopyTitle,
                    firstOvoskopyTitleSup
                )

                11 -> OvoskopyState(
                    true,
                    secondOvoskopySup,
                    R.drawable.chiken2,
                    secondOvoskopyTitle,
                    secondOvoskopyTitleSup
                )

                16 -> OvoskopyState(
                    true,
                    thirdOvoskopySup,
                    R.drawable.chiken3,
                    thirdOvoskopyTitle,
                    thirdOvoskopyTitleSup
                )

                else -> OvoskopyState(false, "", R.drawable.chiken1, thirdOvoskopyTitle)
            }

            TypeEgg.TURKEYS, TypeEgg.DUCKS -> when (day) {
                8 -> OvoskopyState(
                    true,
                    firstOvoskopySup,
                    R.drawable.turkeys1,
                    firstOvoskopyTitle,
                    firstOvoskopyTitleSup
                )

                14 -> OvoskopyState(
                    true,
                    secondOvoskopySup,
                    R.drawable.turkeys2,
                    secondOvoskopyTitle,
                    secondOvoskopyTitleSup
                )

                25 -> OvoskopyState(
                    true,
                    thirdOvoskopySup,
                    R.drawable.turkeys3,
                    thirdOvoskopyTitle,
                    thirdOvoskopyTitleSup
                )

                else -> OvoskopyState(false, "", R.drawable.turkeys1, thirdOvoskopyTitle)
            }

            TypeEgg.GEESE -> when (day) {
                9 -> OvoskopyState(
                    true,
                    firstOvoskopySup,
                    R.drawable.goose1,
                    firstOvoskopyTitle,
                    firstOvoskopyTitleSup
                )

                15 -> OvoskopyState(
                    true,
                    secondOvoskopySup,
                    R.drawable.goose2,
                    secondOvoskopyTitle,
                    secondOvoskopyTitleSup
                )

                21 -> OvoskopyState(
                    true,
                    thirdOvoskopySup,
                    R.drawable.goose3,
                    thirdOvoskopyTitle,
                    thirdOvoskopyTitleSup
                )

                else -> OvoskopyState(false, "", R.drawable.goose1, thirdOvoskopyTitle)
            }

            TypeEgg.QUAILS -> when (day) {
                6 -> OvoskopyState(
                    true,
                    firstOvoskopySup,
                    R.drawable.quail1,
                    firstOvoskopyTitle,
                    firstOvoskopyTitleSup
                )

                13 -> OvoskopyState(
                    true,
                    secondOvoskopySup,
                    R.drawable.quail2,
                    secondOvoskopyTitle,
                    secondOvoskopyTitleSup
                )

                else -> OvoskopyState(false, "", R.drawable.quail2, thirdOvoskopyTitle)
            }
        }
    }

    private fun searchCurrentParameter(
        currentDay: Int,
        parametersList: List<DomainIncubatorParameters>
    ): DomainIncubatorParameters {
        return parametersList.firstOrNull { it.day == currentDay } ?: DomainIncubatorParameters()
    }

    private fun updateTemp(temp: String) {
        updateState { state ->
            state.copy(currentParameterDay = state.currentParameterDay.copy(tempFact = temp))
        }
        updateCurrentParameterDay()
    }

    private fun updateDamp(damp: String) {
        updateState { state ->
            state.copy(currentParameterDay = state.currentParameterDay.copy(dampFact = damp))
        }
        updateCurrentParameterDay()
    }

    private fun updateOver(over: String) {
        updateState { state ->
            state.copy(currentParameterDay = state.currentParameterDay.copy(overFact = over))
        }
        updateCurrentParameterDay()
    }

    private fun updateAiring(airing: String) {
        updateState { state ->
            state.copy(currentParameterDay = state.currentParameterDay.copy(airingFact = airing))
        }
        updateCurrentParameterDay()
    }

    private fun updateNote(note: String) {
        updateState { state ->
            state.copy(currentParameterDay = state.currentParameterDay.copy(note = note))
        }
        updateCurrentParameterDay()
    }

    private fun updateTempFact(temp: String) {
        updateState { state ->
            state.copy(editParameterDay = state.editParameterDay.copy(tempFact = temp))
        }
    }

    private fun updateDampFact(damp: String) {
        updateState { state ->
            state.copy(editParameterDay = state.editParameterDay.copy(dampFact = damp))
        }
    }

    private fun updateOverFact(over: String) {
        updateState { state ->
            state.copy(editParameterDay = state.editParameterDay.copy(overFact = over))
        }
    }

    private fun updateAiringFact(airing: String) {
        updateState { state ->
            state.copy(editParameterDay = state.editParameterDay.copy(airingFact = airing))
        }
    }

    private fun updateNoteFact(note: String) {
        updateState { state ->
            state.copy(editParameterDay = state.editParameterDay.copy(note = note))
        }
    }

    private fun updateRejectedEgg(rejectedEgg: String) {
        updateState { state ->
            state.copy(
                rejectedEgg = rejectedEgg
            )
        }
    }

    private fun earlyCompleteIncubator() {
        viewModelScope.launch {
            bookmarkToArchive(getState().reasonNote, isEarlyCompletionStatus = true)
            updateCompleteIncubationBottomSheet(false)
        }
    }

    private fun updateCompleteIncubationBottomSheet(isOpenCompleteIncubationBottomSheet: Boolean) {
        viewModelScope.launch {
            if (!getState().isCompleteModeEnd)
                updateState { state ->
                    state.copy(
                        isOpenEarlyCompleteIncubationBottomSheet = isOpenCompleteIncubationBottomSheet,
                    )
                }
            else {
                val projectList =
                    if (isOpenCompleteIncubationBottomSheet)
                        projectRepository.getProjectListAct().first()
                    else emptyList()

                updateState { state ->
                    state.copy(
                        isOpenCompleteIncubationBottomSheet = isOpenCompleteIncubationBottomSheet,
                        completeState = state.completeState.copy(
                            projectList = projectList
                        )
                    )
                }
            }
        }
    }

    private fun updateChoiceProjectMode(isChoiceProjectMode: Boolean) {
        val state = getState().completeState.isChoiceProjectMode
        val value = when (isChoiceProjectMode) {
            true -> if (state == true) null else true
            false -> if (state == false) null else false
        }
        val isEnabledCompleteButtonTwo = if (value == null) true else {
            if (value) getState().completeState.newNameProject.isNotBlank() else getState().completeState.indexProject != 0L
        }

        updateState { state ->
            state.copy(
                completeState = state.completeState.copy(
                    isChoiceProjectMode = value,
                    isEnabledCompleteButtonTwo = isEnabledCompleteButtonTwo
                )
            )
        }
    }

    private fun updateNewNameProject(newNameProject: String) {
        updateState { state ->
            state.copy(
                completeState = state.completeState.copy(
                    newNameProject = newNameProject,
                    isEnabledCompleteButtonTwo = newNameProject.isNotBlank()
                )
            )
        }
    }

    private fun updateChicksBred(chicksBred: String) {
        val allEgg = getState().domainBookmark.count
        val currentEgg = getState().currentEgg
        val rejectedEgg = getState().domainBookmark.rejectedCount
        val chicksBredInt = chicksBred.toConvertZeroDbInt()

        val fraction = (chicksBred.toConvertZeroDouble() / allEgg.toDouble())
            .coerceIn(0.0, 1.0)

        val isErrorCompleted = chicksBredInt > currentEgg
        val rejectedEggCompleted =
            if (isErrorCompleted) rejectedEgg
            else currentEgg - chicksBredInt + rejectedEgg

        val isEnabledCompleteButton =
            (!isErrorCompleted && chicksBred.isNotBlank() && chicksBredInt > 0)


        updateState { state ->
            state.copy(
                completeState = state.completeState.copy(
                    chicksBred = chicksBred,
                    precentCompleted = fraction * 100.0,
                    precentFloatCompleted = fraction.toFloat(),
                    isErrorCompleted = isErrorCompleted,
                    rejectedEggCompleted = rejectedEggCompleted,
                    isEnabledCompleteButton = isEnabledCompleteButton
                )
            )
        }
    }

    private fun updateChicksPrice(chicksPrice: String) {
        updateState { state ->
            state.copy(
                completeState = state.completeState.copy(
                    chicksPrice = chicksPrice,
                )
            )
        }
    }

    private fun updateIndexChoiceProject(index: Long) {
        updateState { state ->
            state.copy(
                completeState = state.completeState.copy(
                    indexProject = index,
                    isEnabledCompleteButtonTwo = index != 0L
                )
            )
        }
    }

    private fun saveEgg() {
        viewModelScope.launch {
            if (getState().rejectedEgg.isNotBlank()) {
                updateState { state ->
                    state.copy(
                        domainBookmark = state.domainBookmark.copy(
                            rejectedCount = state.domainBookmark.rejectedCount + state.rejectedEgg.toConvertDbInt()
                        ),
                        rejectedEgg = ""
                    )
                }
                bookmarkRepository.update(getState().domainBookmark)
            }
            updateComplete(false)
            updateOpenOvoscopBottomSheet(false)
        }
    }

    private fun updateOpenBottomSheet(isOpenBottomSheet: Boolean) {
        updateState { state ->
            state.copy(isOpenBottomSheet = isOpenBottomSheet)
        }
    }

    private fun updateOpenOvoscopBottomSheet(isOpenOvoscopBottomSheet: Boolean) {
        viewModelScope.launch {
            if (!isOpenOvoscopBottomSheet && getState().isLantern) {
                updateLantern()
                delay(300)
            }
            updateState { state ->
                state.copy(isOpenOvoscopBottomSheet = isOpenOvoscopBottomSheet)
            }
        }
    }

    private fun updateCurrentParameterDay() {
        viewModelScope.launch {
            incubatorParametersRepository.updateIncubator(getState().currentParameterDay.toDomain())
        }
    }

    private fun updateEditCurrentParameterDay() {
        viewModelScope.launch {
            incubatorParametersRepository.updateIncubator(getState().editParameterDay.toDomain())
        }
        updateOpenEditBottomSheet(false, ParametersIncubatorUi())
    }

    private fun updateReasonNote(reasonNote: String) {
        updateState { state ->
            state.copy(reasonNote = reasonNote)
        }
    }

    private fun updateLantern() {
        updateState { state ->
            state.copy(isLantern = !state.isLantern)
        }
    }

    private fun updateComplete(isOpenOvoscopEndBottomSheet: Boolean) {
        if (isOpenOvoscopEndBottomSheet && getState().isLantern) updateLantern()
        updateState { state ->
            state.copy(
                isOpenOvoscopEndBottomSheet = isOpenOvoscopEndBottomSheet
            )
        }
        if (!isOpenOvoscopEndBottomSheet) updateOpenOvoscopBottomSheet(false)
    }

    private fun completeIncubatorClick() {
        viewModelScope.launch {
            when (getState().completeState.isChoiceProjectMode) {
                true -> insertIntoNewProject()
                false -> insertIntoProjectAnimal(getState().completeState.indexProject)
                null -> bookmarkToArchive()
            }
            updateCompleteIncubationBottomSheet(false)
        }
    }

    private suspend fun insertIntoNewProject() {
        val id = projectRepository.insertProjectLong(insertDomainProject())
        settingsRepository.insertSettings(insertDomainSettings(id))
        insertAnimalToProject(id)
    }

    private suspend fun insertIntoProjectAnimal(idPT: Long) {
        insertAnimalToProject(itemIdPT = idPT)
        bookmarkToArchive()
    }

    private suspend fun insertAnimalToProject(itemIdPT: Long) {
        val idAnimal = animalRepository.insertAnimalTable(
            getState().domainBookmark.toDomainAnimalTable(itemIdPT = itemIdPT)
        )
        val pair = getState().updateForSave(idAnimal, itemIdPT)
        animalCountRepository.insertAnimalCountTable(pair.first)
        pair.second?.let { expensesRepository.insertExpenses(it) }
    }

    private suspend fun bookmarkToArchive(
        note: String? = null,
        isEarlyCompletionStatus: Boolean = false
    ) {
        val chicksPrice = if (getState().completeState.chicksPrice.isBlank()) null
        else getState().completeState.chicksPrice.toConvertDbDouble()

        val rejectedCount = if (isEarlyCompletionStatus) getState().domainBookmark.count
        else getState().completeState.rejectedEggCompleted

        bookmarkRepository.update(
            getState().domainBookmark.copy(
                isActivityBookmark = false,
                isEarlyCompletionStatus = isEarlyCompletionStatus,
                endDate = dateToday(),
                chickPrice = chicksPrice,
                rejectedCount = rejectedCount,
                note = note ?: getState().domainBookmark.note
            )
        )
    }

    private fun insertDomainProject(): DomainProjectTable {
        return DomainProjectTable(
            title = getState().completeState.newNameProject,
            date = dateToday(),
            mode = false
        )
    }

    private fun insertDomainSettings(idPT: Long): DomainSettings {
        return DomainSettings(
            /* currencySuffix = incubatorRepository,*/ //TODO Рубли или тенге
            idPT = idPT
        )
    }

    private fun BookmarkState.updateForSave(
        idAnimal: Long,
        itemIdPT: Long
    ): Pair<DomainAnimalCount, DomainExpensesTable?> {
        val dateFactory2 = dateToday()
        val dateList = dateFactory2.split(".")

        return DomainAnimalCount(
            count = domainBookmark.count.toString(),
            suffix = Suffix.PIECES,
            date = dateToday(),
            note = "",// Добавлено через инкубатор
            version = AnimalCountVersion.ADD,
            idAnimal = idAnimal
        ) to
                if (domainBookmark.price != null)
                    DomainExpensesTable(
                        title = domainBookmark.title, // TODO
                        count = domainBookmark.count.toDouble(),
                        day = dateList[0].toInt(),
                        month = dateList[0].toInt(),
                        year = dateList[0].toInt(),
                        price = domainBookmark.price!!, //TODO
                        priceAll = null, //TODO
                        countSuffix = Suffix.PIECES,
                        category = "", // //TODO Затраты на инкубатор
                        note = "", // //TODO Затраты на инкубатор
                        isShowFood = false,
                        isShowFoodHand = false,
                        isShowWarehouse = false,
                        isShowAnimals = false,
                        idPT = itemIdPT,
                        animalId = idAnimal,
                    ) else null
    }


    private fun DomainBookmark.toDomainAnimalTable(
        itemIdPT: Long
    ): DomainAnimalTable {
        return DomainAnimalTable(
            name = title,
            type = resourceProvider.getString(type.toResId()),
            date = dateToday(),
            dateFactory = null,
            group = true,
            sex = false,
            note = note,
            image = null,
            archive = false,
            foodDay = 0.0,
            foodDaySuffix = Suffix.GRAM_DAY,
            idPT = itemIdPT
        )
    }

    private fun DomainIncubatorParameters.toUi(typeEgg: TypeEgg): ParametersIncubatorUi {
        val ovoskop = setOvoskop(day = day, typeEgg = typeEgg)
        return ParametersIncubatorUi(
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
            ovoscopyState = ovoskop,
            note = note,
            idPT = idPT
        )
    }

    private fun ParametersIncubatorUi.toDomain(): DomainIncubatorParameters {
        return DomainIncubatorParameters(
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
            idPT = idPT
        )
    }
}

sealed class BookmarkIntent() : BaseIntent {
    //CurrentParameter
    data class TempChanged(val value: String) : BookmarkIntent()
    data class DampChanged(val value: String) : BookmarkIntent()
    data class OverChanged(val value: String) : BookmarkIntent()
    data class AiringChanged(val value: String) : BookmarkIntent()
    data class NoteChanged(val value: String) : BookmarkIntent()
    data class OpenBottomSheetClick(val value: Boolean) : BookmarkIntent()

    //EditCurrentParameter
    data class OpenEditBottomSheetClick(
        val value: Boolean,
        val parameterDay: ParametersIncubatorUi
    ) : BookmarkIntent()

    data class TempFactChanged(val value: String) : BookmarkIntent()
    data class DampFactChanged(val value: String) : BookmarkIntent()
    data class OverFactChanged(val value: String) : BookmarkIntent()
    data class AiringFactChanged(val value: String) : BookmarkIntent()
    data class NoteFactChanged(val value: String) : BookmarkIntent()
    data object SaveParameterClick : BookmarkIntent()

    //Ovoscopy
    data class OpenOvoscopBottomSheetClick(val value: Boolean) : BookmarkIntent()
    data object LanternClick : BookmarkIntent()
    data class CompleteOvoscopClick(val value: Boolean) : BookmarkIntent()
    data class RejectedEggChanged(val value: String) : BookmarkIntent()
    data object SaveEggClick : BookmarkIntent()

    //CompleteIncubation
    data class OpenCompleteIncubationBottomSheetClick(val value: Boolean) : BookmarkIntent()
    data class ChicksBredChanged(val value: String) : BookmarkIntent()
    data class ChicksPriceChanged(val value: String) : BookmarkIntent()
    data class ChoiceProjectModeClick(val value: Boolean) : BookmarkIntent()
    data class IndexChoiceProjectClick(val value: Long) : BookmarkIntent()
    data class NameProjectChanged(val value: String) : BookmarkIntent()
    data object CompleteIncubatorClick : BookmarkIntent()

    //EarlyCompleteIncubator
    data class ReasonNoteChanged(val value: String) : BookmarkIntent()
    data object EarlyCompleteIncubatorClick : BookmarkIntent()
}