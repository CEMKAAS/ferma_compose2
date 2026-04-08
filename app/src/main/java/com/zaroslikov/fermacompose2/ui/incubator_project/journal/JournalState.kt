package com.zaroslikov.fermacompose2.ui.incubator_project.journal

import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class JournalState(
    override val idPT: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val projectId: Long = 0,
    val domainIncubator: IncubatorUi = IncubatorUi(),
    val domainBookmarkList: List<BookmarkUi> = emptyList(),
    val typeList: List<TypeUi> = emptyList(),
    val breedList: List<BreedUi> = emptyList(),
    val isShowActiveAlertDialog: Boolean = false,
    val isShowDeleteAlertDialog: Boolean = false,
    val choiceNameBookmark: String = "",
    val isArchive: Boolean = false
) : ListState()

data class JournalCombined(
    val domainIncubator: IncubatorUi = IncubatorUi(),
    val domainBookmarkList: List<BookmarkUi>,
    val typeList: List<TypeUi>,
    val breedList: List<BreedUi> = emptyList(),
)

data class BookmarkUi(
    val id: Long,
    val title: String,
    val textSup: String,
    val isEarlyCompletionStatus: Boolean,
    val percent: Double,
    val percentFloat: Float,
    val startDate: String,
    val endDate: String,
    val egg: Int,
    val rejectedEgg: Int
)

data class IncubatorUi(
    val title: String = "",
    val brand: String? = null,
    val model: String? = null,
    val capacity: Int = 0,
    val percent: Double = 0.0,
    val percentFloat: Float = 0f,
    val egg: Int = 0,
    val rejectedEgg: Int = 0,
    val note: String = ""
)

data class TypeUi(
    val typeEgg: TypeEgg,
    val count: Int,
    val percent: Double,
    val percentFloat: Float,
    val isChoice: Boolean
)

data class BreedUi(
    val breed: String,
    val count: Int,
    val percent: Double,
    val percentFloat: Float
)
