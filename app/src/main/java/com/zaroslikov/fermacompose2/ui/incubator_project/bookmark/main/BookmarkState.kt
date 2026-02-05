package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class BookmarkState(
    val isOpenBottomSheet: Boolean = false,
    val isOpenEditBottomSheet: Boolean = false,
    val isOpenOvoscopBottomSheet: Boolean = false,
    val isOpenOvoscopEndBottomSheet: Boolean = false,
    val isOpenCompleteIncubationBottomSheet: Boolean = false,
    val isOpenEarlyCompleteIncubationBottomSheet: Boolean = false,
    val domainBookmark: DomainBookmark = DomainBookmark(),
    val isChoiceProjectMode: Boolean? = null,
    val chicksBred: String = "",
    val newNameProject: String = "",
    val numberDays: Int = 0,
    val percent: Double = 0.0,
    val percentFloat: Float = 0f,
    val idBookmark: Long? = null,
    val daysToEnd: Int = 0,
    val endDay: String = "",
    val itemIdPT: Long = 0,
    val incubatorId: Long = 0,
    val isActivityBookmark: Boolean = false,
    val currentDay: Int = 0,
    val reasonNote: String = "",
    val parameterDayList: List<ParametersIncubatorUi> = emptyList(),
    val editParameterDay: ParametersIncubatorUi = ParametersIncubatorUi(),
    val currentParameterDay: ParametersIncubatorUi = ParametersIncubatorUi(),
    val tomorrowParameterDay: ParametersIncubatorUi = ParametersIncubatorUi(),
    val projectList: List<DomainProjectTable> = emptyList(),
    val isLantern: Boolean = false,
    val rejectedEgg: String = "",
    val indexProject: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState {
    fun enabledOvoscopyButton(): Boolean {
        val isEnabled = (domainBookmark.count - rejectedEgg.toConvertZero()) < 0
        return !isEnabled
    }

    fun enabledCompleteButton(): Boolean {
        val isEnabled =
            ((chicksBred.toConvertZero()) <= domainBookmark.count) && chicksBred.isNotBlank() &&
                    chicksBred.toConvertZero() > 0
        return !isEnabled
    }

    fun enabledS(): Boolean {
        return when (isChoiceProjectMode) {
            true -> newNameProject.isNotBlank()
            false -> true
            else -> true
        }
    }
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