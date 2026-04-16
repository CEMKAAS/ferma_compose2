package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.compose.ui.graphics.Color
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem


data class ExpensesListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val isOpenGroupBottomSheet: Boolean = false,
    val isOpenEntryBottomSheet: Boolean = false,
    val isOpenBottomSheetDetail: Boolean = false,
    val isOpenBottomSheetDelete: Boolean = false,
    val isSaveStateForEntry: Boolean = false,

    val currentDetail: ExpensesTableUi? = null,
    val currentBriefly: BrieflyItem? = null,

    val list: List<ExpensesTableUi> = emptyList(),
    val briefly: List<BrieflyItem> = emptyList(),
    val brieflyList: List<ExpensesTableUi> = emptyList(),
    val searchList: List<ExpensesTableUi> = emptyList(),
    val searchBrieflyList: List<BrieflyItem> = emptyList(),

    val settings: DomainSettings = DomainSettings(),
    override val isEntry: Boolean = false,
    override val currentProduct: ExpensesEntryState2 = ExpensesEntryState2(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false
) : EntryNewState()


data class ExpensesEntryState2(
    val isFood: Boolean = false,
    val itemId: Long = 0,
    val title: String = "",

    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val suffixList: List<Suffix> = suffixAllList,

    val weight: String = "",
    val weightSuffix: Suffix = Suffix.KILOGRAM,
    val weightAll: String = "",
    val weightAllSuffix: Suffix = Suffix.KILOGRAM,
    val isShowAutoWeightCheckbox: Boolean = false,
    val isAutoWeight: Boolean = false,
    val weightSuffixList: List<Suffix> = suffixWeightList,

    val price: String = "",
    val isAutoPrice: Boolean = false,
    val priceAll: String = "",

    val category: String = "",
    val date: String = dateToday(),
    val note: String = "",

    val isShowFood: Boolean = false,
    val feedFood: String = "",
    val feedFoodSuffix: Suffix = Suffix.GRAM,
    val countAnimalFood: String = "",
    val daysFood: Int = 0,
    val dateEndFood: String = "",

    val countInWarehouse: DomainCountSuffix = DomainCountSuffix(0.0, Suffix.PIECES),

    val isPercent: Boolean = true,

    val isEntry: Boolean = true,
    val isIndicatorsValue: Boolean = false,

    val itemIdPT: Long = 0,
    val suffixSet: Set<Suffix> = setOf(Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS),
    val pickList: PickExpensesList = PickExpensesList(),
    val error: ErrorExpenses = ErrorExpenses(),

    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
    override val hasAnyError: Boolean = false
) : BaseProduct()


data class ExpensesTableUi(
    val id: Long = 0,
    val title: String = "", // название
    val count: Double = 0.0, // Кол-во
    val day: Int = 0,  // день
    val month: Int = 0, // месяц
    val year: Int = 0, // время
    val price: Double = 0.0,
    val priceAll: Double? = null,
    val countSuffix: Suffix = Suffix.PIECES,
    val category: String = "",
    val note: String = "",
    val isFood: Boolean = false,
    val isShowFood: Boolean = false, // Показывать на складе еду
    val feedFood: Double? = null, // Ежедневный расход еды
    val feedFoodSuffix: Suffix? = null, // Суффикс ежедневного расхода
    val countAnimal: Int? = null, // Кол-во животных
    val foodDesignedDay: Int? = null, // Кол-во дней
    val lastDayFood: String? = null, //Последний день еды
    val weight: Double? = null,
    val weightSuffix: Suffix? = null,
    val idPT: Long = 0,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
    val food: Food? = null,
    val typeProduct: TypeProduct? = null
)

data class Food(
    val feedFood: Double,
    val feedFoodSuffix: Suffix,
    val daysEnd: Int,
    val weightAll: Double,
    val weightSuffix: Suffix,
    val percentFloat: Float,
    val animalList: List<AnimalExpensesDomain>,
    val remainingFood: Double
)

data class PickExpensesList(
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val animalList2: List<AnimalExpensesUi> = emptyList(),
    val warehouseList: List<DomainCountSuffix> = emptyList(),
)

data class AnimalExpensesUi(
    val id: Long,
    val name: String,
    val type: String,
    val foodDay: Double,
    val foodDaySuffix: Suffix,
    val countAnimal: Int,
    val idExpensesAnimal: Long,
    val ps: Boolean = false,
    val presentException: Double = 0.0,
    val price: Double,
    val error: ErrorAnimalExpenses = ErrorAnimalExpenses()
)

data class ErrorAnimalExpenses(
    val isErrorPrice: Boolean = false
)

data class ErrorExpenses(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorPrice: Boolean = false
) : BaseError


