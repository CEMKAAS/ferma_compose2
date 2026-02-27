package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDbInt
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class EntryBookmarkState(
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val currentProduct: EntryBookmark = EntryBookmark()
) : EntryNewState()


data class EntryBookmark(
    val id: Long = 0,
    val title: String = "",
    val type: TypeEgg = TypeEgg.CHICKENS,
    val breed: String = "",
    val count: String = "",
    val rejectedCount: String = "",
    val startDate: String = dateToday(),
    val endDate: String = dateToday(),
    val time: String = "12:00",
    val price: String = "",
    val priceAll: String = "",
    val isAutoPrice: Boolean = false,
    val note: String = "",
    val autoRotation: Boolean = false,
    val autoVentilation: Boolean = false,
    val parameterDayList: List<ParameterDay> = emptyList(),
    val breedList: List<String> = emptyList(),
    val indexNotification: Int = 0,
    val notificationList: List<NotificationParameters> = emptyList(),
    val currentNotification: NotificationParameters = NotificationParameters(),
    val error: ErrorBookmark = ErrorBookmark(),
    val isActivityBookmark: Boolean = true,
    val idPT: Long = 0,
    val templatesBookmarkList: List<DomainBookmark> = emptyList(),
    val indexBookmark: Long = 0,
    val isTemplatesPlan: Boolean = true,
    val incubatorCount: Int = 0,

) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled =
            title.isNotBlank() && count.isNotBlank() && (count.toConvertZeroDbInt() <= incubatorCount) &&
                    (rejectedCount.toConvertZeroDbInt() < count.toConvertZeroDbInt())
                    && !hasAnyError
        return isEnabled
    }
}

data class NotificationParameters(
    val time: String = "12:00",
    val note: String = "",
    val isEntry: Boolean = true,
    val isVisibility: Boolean = true
)

data class ParameterDay(
    val day: Int,
    val temp: String,
    val damp: String,
    val over: String,
    val airing: String,
    val tempFact: String = "",
    val dampFact: String = "",
    val overFact: String = "",
    val airingFact: String = "",
    val note: String = "",
    val id: Long = 0,
    val idPT: Long = 0
//    val error: ErrorParameterDay()
)

/*data class ErrorParameterDay(
    val isErrorTemp: String,
    val isErrorDamp: String,
    val isErrorOver: String,
    val
)*/

data class ErrorBookmark(
    val isErrorTitle: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorLargeCount: Boolean = false,
    val isErrorRejectedCount: Boolean = false
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorTitle || isErrorCount || isErrorLargeCount || isErrorRejectedCount
}



