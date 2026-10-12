package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import com.zaroslikov.domain.models.animal.breed.DomainAnimalBreed
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdUi

data class AnimalListState(
    val textSearch: String = "",
    val isArchive: Boolean = true,
    val isArchiveProject: Boolean = false,
    val idPT: Long = 0,
    val openBottomSheetEntry: Boolean = false,
    val isOpenBottomSheetDelete: Boolean = false,
    val currentAnimal: AnimalListUi = AnimalListUi(),
    /** Верхний уровень: сборные группы и самостоятельные животные. */
    val list: List<AnimalListItem> = emptyList(),
    /** Все активные животные, включая членов сборных групп (для поиска). */
    val allActive: List<AnimalListUi> = emptyList(),
    /** Сборные группы с неархивными членами (для поиска). */
    val herds: List<AnimalHerdUi> = emptyList(),
    val archiveList: List<AnimalListUi> = emptyList(),
    val searchList: List<AnimalListItem> = emptyList(),
    val searchArchiveList: List<AnimalListUi> = emptyList(),
    val isSaveStateForEntry: Boolean = false,
    val settings: DomainSettings = DomainSettings(),
    val merge: MergeGroupState = MergeGroupState(),
    /** Самка с активной беременностью, которую переносят в архив: диалог «Прервать беременность?». */
    val archivePregnancyAnimalId: Long? = null,
    /** У удаляемой самки есть записи о беременностях — они удалятся вместе с ней. */
    val deleteHasPregnancyHistory: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = true,
    override val currentProduct: AnimalEntryState2 = AnimalEntryState2(),
) : EntryNewState()

/** Строка списка животных: сборная группа или самостоятельное животное. */
sealed interface AnimalListItem {
    data class Herd(val herd: AnimalHerdUi) : AnimalListItem
    data class Animal(val animal: AnimalListUi) : AnimalListItem
}

data class AnimalEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val type: String = "",
    val breed: String = "",
    val sex: Boolean = true,

    val isAnimalGroup: Boolean = false, // true group
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    /** Обезличенная группа: сколько самок и самцов (необязательно). */
    val femalesCount: String = "",
    val malesCount: String = "",

    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",

    val isDateFactory: Boolean = true,
    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),

    val foodDay: String = "",
    val foodDaySuffix: Suffix = Suffix.GRAM_DAY,
    val note: String = "",

    val typeList: List<String> = emptyList(),
    /** Породы неархивных животных проекта; в списке — только породы вида из поля «Тип». */
    val breedList: List<DomainAnimalBreed> = emptyList(),
    val archive: Boolean = false,
    val itemIdPT: Long = 0,
    val error: ErrorEntryAnimal = ErrorEntryAnimal(),
    override val hasAnyError: Boolean = false
) : BaseProduct()


data class AnimalListUi(
    val id: Long = 0,
    val name: String = "",
    val type: String = "",
    /** Порода; null — не указана, строка в карточке не показывается. */
    val breed: String? = null,
    val date: String = "",
    val isGroup: Boolean = false,
    val sex: Boolean? = null,
    val count: String? = null,
    val suffix: Suffix = Suffix.PIECES,
    /** Сборная группа, в которой состоит животное; null — самостоятельное. */
    val herdId: Long? = null,
    val herdName: String? = null,
    val ownCount: Double = 0.0,
    val totalCount: Double = 0.0,
    val foodDay: Double = 0.0,
    val foodDaySuffix: Suffix = Suffix.KILOGRAM_DAY,
    val isArchive: Boolean = false,
    val imagePath: String? = null,
    val currentIcon: AppIcon = AppIcon.PETS,
    /** Срок активной беременности — кольцо вокруг иконки и значок сердца. */
    val pregnancyProgress: PregnancyProgress? = null,
    /** Обезличенная группа: сколько самок и самцов (остальное — пол не известен). */
    val femalesCount: Int = 0,
    val malesCount: Int = 0
) {
    /** Обезличенная группа: головы, пол которых не известен. */
    val unknownSexCount: Double
        get() = if (sex != null) 0.0 else maxOf(totalCount - femalesCount - malesCount, 0.0)
}


data class ErrorEntryAnimal(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorType: Boolean = false,
    val isErrorCount: Boolean = false,
    /** Самок и самцов вместе больше, чем голов в группе. */
    val isErrorSexCount: Boolean = false,
    override val hasAnyError: Boolean = false,
) : BaseError
