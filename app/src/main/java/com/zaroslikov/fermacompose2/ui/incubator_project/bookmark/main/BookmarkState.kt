package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent

data class BookmarkState(
    val isOpenBottomSheet: Boolean = false,
    val isOpenEditBottomSheet: Boolean = false,
    val isOpenOvoscopBottomSheet: Boolean = false,
    val isOpenOvoscopEndBottomSheet: Boolean = false,
    val isOpenCompleteIncubationBottomSheet: Boolean = false,
    val isOpenEarlyCompleteIncubationBottomSheet: Boolean = false,
    val isBookmarkCompleted: Boolean = false,
    val domainBookmark: DomainBookmark = DomainBookmark(),
    val numberDays: Int = 0,
    val percent: Double = 0.0,
    val percentFloat: Float = 0f,
    val idBookmark: Long? = null,
    val daysToEnd: Int = 0,
    val startDay: String = "",
    val endDay: String = "",
    val itemIdPT: Long = 0,
    val incubatorId: Long = 0,
    val incubatorName: String = "",
    val isActivityBookmark: Boolean = false,
    val currentDay: Int = 0,
    val currentEgg: Int = 0,
    val reasonNote: String = "",
    val currencySuffix: Suffix = Suffix.RUBLE,
    val parameterDayList: List<ParametersIncubatorUi> = emptyList(),
    val editParameterDay: ParametersIncubatorUi = ParametersIncubatorUi(),
    val currentParameterDay: ParametersIncubatorUi = ParametersIncubatorUi(),
    val tomorrowParameterDay: ParametersIncubatorUi = ParametersIncubatorUi(),
    val isLantern: Boolean = false,
    val rejectedEgg: String = "",
    val isCompleteModeEnd: Boolean = false,
    val completeState: CompleteState = CompleteState(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false,
) : BaseState {
    fun enabledOvoscopyButton(): Boolean {
        val isEnabled = (currentEgg - rejectedEgg.toConvertZero()) > 0
        return isEnabled
    }
}


data class CompleteState(
    val chicksBred: String = "",
    val chicksPrice: String = "",
    val precentCompleted: Double = 0.0,
    val precentFloatCompleted: Float = 0f,
    val rejectedEggCompleted: Int = 0,

    val newNameProject: String = "",
    val indexProject: Long = 0,
    val projectList: List<DomainProjectTable> = emptyList(),
    val isChoiceProjectMode: Boolean? = null,
    val isErrorCompleted: Boolean = false,
    val isEnabledCompleteButton: Boolean = false,
    val isEnabledCompleteButtonTwo: Boolean = true
)

data class OvoscopyState(
    val isLantern: Boolean = false,
) {

}

data class ParametersIncubatorUi(
    val id: Long = 0,
    val day: Int = 0,
    val temp: String = "",
    val damp: String = "",
    val over: String = "",
    val airing: String = "",
    val tempFact: String = "",
    val dampFact: String = "",
    val overFact: String = "",
    val airingFact: String = "",
    val ovoscopyState: OvoskopyState = OvoskopyState(),
    val note: String = "",
    val idPT: Long = 0
)

data class OvoskopyState(
    val isOvoscopyDay: Boolean = false,
    val supportText: String = "",
    val image: Int = 0,
    val titleText: String = "",
    val titleSupText: String = ""
)