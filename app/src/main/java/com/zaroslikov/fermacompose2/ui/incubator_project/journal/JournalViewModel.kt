package com.zaroslikov.fermacompose2.ui.incubator_project.journal

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.incubator.DomainCountRejectedCount
import com.zaroslikov.domain.models.dto.incubator.DomainTitleCount
import com.zaroslikov.domain.models.dto.incubator.DomainTypeEggCount
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainIncubatorTable
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val incubatorTableRepository: IncubatorTableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository
) : BaseViewModel<JournalState, JournalIntent>(
    JournalState()
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[JournalDestination.itemIdPT])
    private var confirmDelete = CompletableDeferred<Boolean?>()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val incubator = incubatorTableRepository.getIncubatorByIdPT(itemIdPT).first()
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            combine(
                incubatorTableRepository.getIncubatorByIdPT(itemIdPT),
                bookmarkRepository.getBookmarkListByIdPT(incubator.id),
                bookmarkRepository.getTypeStatisticList(incubator.id),
                bookmarkRepository.getCountAndRejectedCountAll(incubator.id)
            ) { values: Array<Any?> ->
                val domainIncubatorTable = values[0] as DomainIncubatorTable
                val domainBookmarkList = values[1] as List<DomainBookmark>
                val typeList = values[2] as List<DomainTypeEggCount>
                val countEgg = values[3] as DomainCountRejectedCount
                ss(
                    domainIncubator = domainIncubatorTable,
                    domainBookmarkList = domainBookmarkList,
                    typeList = typeList,
                    countEgg = countEgg
                )
            }.collect { combine ->
                updateState { state ->
                    state.copy(
                        domainIncubator = combine.domainIncubator,
                        domainBookmarkList = combine.domainBookmarkList,
                        typeList = combine.typeList,
                        idPT = incubator.id,
                        projectId = itemIdPT,
                        isLoading = false,
                        isArchive = isArchive
                    )
                }
            }
        }
    }

    private fun ss(
        domainIncubator: DomainIncubatorTable,
        domainBookmarkList: List<DomainBookmark>,
        typeList: List<DomainTypeEggCount>,
        countEgg: DomainCountRejectedCount
    ): JournalCombined {
        val bookmarkUi = domainBookmarkList.map { domainBookmark -> domainBookmark.toBookmarkUi() }
        val incubatorUi = domainIncubator.toIncubatorUi(
            count = countEgg.count,
            rejectedCount = countEgg.rejectedCount
        )
        val typeSumEgg = typeList.sumOf { type -> type.count }
        val typeListUi = typeList.map { it.toTypeList(typeSumEgg) }
        return JournalCombined(
            domainBookmarkList = bookmarkUi,
            typeList = typeListUi,
            domainIncubator = incubatorUi
        )
    }

    fun onIntent(intent: JournalIntent) {
        return when (intent) {
            is JournalIntent.ChoiceTypeClicked -> updateChoiceType(intent.typeEgg)
            is JournalIntent.ResetTypeClicked -> updateResetType()
            is JournalIntent.ActiveBookmarkClicked -> updateActiveBookmark(
                intent.value,
                intent.title
            )

            is JournalIntent.DeleteBookmarkClicked -> updateDeleteBookmark(
                intent.value,
                intent.title
            )

            is JournalIntent.ShowActiveBookmarkClicked -> updateIsShowActiveBookmark(intent.value)
            is JournalIntent.ShowDeleteBookmarkClicked -> updateIsShowDeleteBookmark(intent.value)
        }
    }

    private fun updateChoiceType(typeEgg: TypeEgg) {
        viewModelScope.launch {
            val selectedItem = getState().typeList.firstOrNull { it.typeEgg == typeEgg }
            val wasSelected = selectedItem?.isChoice == true
            val breedUi = if (!wasSelected) {
                val domainBreedList =
                    bookmarkRepository.getBreedStatisticList(typeEgg, getState().idPT).first()
                val breedSumEgg = domainBreedList.sumOf { type -> type.count }
                domainBreedList.map { it.toBreedList(breedSumEgg) }
            } else emptyList()

            updateState { state ->
                val updatedList = state.typeList
                    .map { item ->
                        when {
                            item.typeEgg == typeEgg && wasSelected -> item.copy(isChoice = false)
                            item.typeEgg == typeEgg && !wasSelected -> item.copy(isChoice = true)
                            else -> item.copy(isChoice = false)
                        }
                    }
                state.copy(
                    typeList = updatedList,
                    breedList = breedUi
                )
            }
        }
    }

    private fun updateResetType() {
        updateState { state ->
            state.copy(
                typeList = state.typeList.map { it.copy(isChoice = false) },
                breedList = emptyList()
            )
        }
    }

    private fun updateActiveBookmark(currentIdBookmark: Long, title: String) {
        viewModelScope.launch {
            updateIsShowActiveBookmark(true)
            updateChoiceNameBookmark(title = title)
            val confirmed = awaitConfirmationIfNeeded()
            if (confirmed) {
                val domain = bookmarkRepository.getBookmark(currentIdBookmark).first()
                val domainActive = bookmarkRepository.getActivityBookmark(getState().idPT).first()
                bookmarkRepository.update(
                    domain.copy(
                        startDate = dateToday(),
                        isActivityBookmark = true
                    )
                )
                domainActive?.let {
                    bookmarkRepository.update(
                        it.copy(
                            endDate = dateToday(),
                            isActivityBookmark = false
                        )
                    )
                }
            }
        }
    }

    private fun updateDeleteBookmark(currentIdBookmark: Long, title: String) {
        viewModelScope.launch {
            updateIsShowDeleteBookmark(true)
            updateChoiceNameBookmark(title = title)
            val confirmed = awaitConfirmationIfNeeded()
            if (confirmed) bookmarkRepository.deleteBookmarkById(id = currentIdBookmark)
        }
    }

    private fun updateIsShowActiveBookmark(isShowAlertDialog: Boolean) {
        confirmDelete.complete(isShowAlertDialog)
        updateState { state ->
            state.copy(
                isShowActiveAlertDialog = if (isShowAlertDialog == state.isShowActiveAlertDialog) false
                else isShowAlertDialog
            )
        }
    }

    private fun updateIsShowDeleteBookmark(isShowDeleteAlertDialog: Boolean) {
        confirmDelete.complete(isShowDeleteAlertDialog)
        updateState { state ->
            state.copy(
                isShowDeleteAlertDialog = if (isShowDeleteAlertDialog == state.isShowDeleteAlertDialog) false
                else isShowDeleteAlertDialog
            )
        }
    }

    private fun updateChoiceNameBookmark(title: String) {
        updateState { state -> state.copy(choiceNameBookmark = title) }
    }

    private suspend fun awaitConfirmationIfNeeded(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            confirmDelete = CompletableDeferred()
            viewModelScope.launch {
                val confirmed = confirmDelete.await()
                continuation.resume(confirmed == true) { cause, _, _ -> }
            }
        }
    }

    private fun DomainBookmark.toBookmarkUi(): BookmarkUi {
        val textSup = textBuilder(resourceProvider.getString(type.toResId()), breed)
        val (percent, percentFloat) = percentCalculate(count - rejectedCount, count)
        return BookmarkUi(
            id = id,
            title = title,
            textSup = textSup,
            isEarlyCompletionStatus = isEarlyCompletionStatus,
            percent = percent,
            percentFloat = percentFloat,
            startDate = startDate,
            endDate = endDate,
            egg = count - rejectedCount,
            rejectedEgg = rejectedCount
        )
    }

    private fun DomainIncubatorTable.toIncubatorUi(
        count: Int,
        rejectedCount: Int
    ): IncubatorUi {
        val (percent, percentFloat) = percentCalculate(count - rejectedCount, count)
        return IncubatorUi(
            title = title,
            brand = brand,
            model = model,
            capacity = capacity,
            note = note,
            percent = percent,
            percentFloat = percentFloat,
            egg = count - rejectedCount,
            rejectedEgg = rejectedCount
        )
    }

    private fun DomainTypeEggCount.toTypeList(countAll: Int): TypeUi {
        val (percent, percentFloat) = percentCalculate(count, countAll)
        return TypeUi(
            typeEgg = typeEgg,
            count = count,
            isChoice = false,
            percent = percent,
            percentFloat = percentFloat,
        )
    }

    private fun DomainTitleCount.toBreedList(countAll: Int): BreedUi {
        val (percent, percentFloat) = percentCalculate(count, countAll)
        return BreedUi(
            breed = title,
            count = count,
            percent = percent,
            percentFloat = percentFloat,
        )
    }

    private fun percentCalculate(
        count: Int,
        countAll: Int,
    ): Pair<Double, Float> {
        val fraction = ((count.toDouble()) / countAll.toDouble())
            .coerceIn(0.0, 1.0)
        return fraction * 100.0 to fraction.toFloat()
    }

    private fun textBuilder(
        value: String,
        valueTwo: String?
    ): String {
        return if (valueTwo != null) value else "$value • $valueTwo"
    }
}

sealed class JournalIntent() : BaseIntent {
    data class ChoiceTypeClicked(val typeEgg: TypeEgg) : JournalIntent()
    data object ResetTypeClicked : JournalIntent()
    data class ShowActiveBookmarkClicked(val value: Boolean) : JournalIntent()
    data class ShowDeleteBookmarkClicked(val value: Boolean) : JournalIntent()
    data class ActiveBookmarkClicked(val value: Long, val title: String) : JournalIntent()
    data class DeleteBookmarkClicked(val value: Long, val title: String) : JournalIntent()
}