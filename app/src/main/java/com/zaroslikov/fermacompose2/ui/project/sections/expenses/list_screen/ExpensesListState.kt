package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.compose.ui.graphics.Color
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


data class ExpensesListState(
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val isOpenGroupBottomSheet: Boolean = false,
    val isOpenEntryBottomSheet: Boolean = false,
    val currentBriefly: BrieflyExpensesDomain = BrieflyExpensesDomain(),
    val list: List<ExpensesTableUi> = emptyList(),
    val briefly: List<BrieflyExpensesDomain> = emptyList(),
    val brieflyList: List<ExpensesTableUi> = emptyList(),
    val textSearch: String = "",
    val searchList: List<ExpensesTableUi> = emptyList(),
    val searchBrieflyList: List<BrieflyExpensesDomain> = emptyList(),
    val isSaveStateForEntry: Boolean = false,
    val priceSuffix: Suffix = Suffix.RUBLE,
    override val isEntry: Boolean = false,
    override val currentProduct: ExpensesEntryState2 = ExpensesEntryState2(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
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
    override val hasAnyError: Boolean = false
) : BaseProduct()


data class ExpensesTableUi(
    val id: Long = 0,
    val title: String, // название
    val count: Double, // Кол-во
    val day: Int,  // день
    val month: Int, // месяц
    val year: Int, // время
    val price: Double,
    val priceAll: Double? = null,
    val countSuffix: Suffix,
    val category: String,
    val note: String,
    val isShowFood: Boolean, // Показывать на складе еду
    val feedFood: Double? = null, // Ежедневный расход еды
    val feedFoodSuffix: Suffix? = null, // Суффикс ежедневного расхода
    val countAnimal: Int? = null, // Кол-во животных
    val foodDesignedDay: Int? = null, // Кол-во дней
    val lastDayFood: String? = null, //Последний день еды
    val weight: Double? = null,
    val weightSuffix: Suffix? = null,
    val idPT: Long,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
    val food: Food? = null,
    val colors: List<Color>? = null
)

data class Food(
    val feedFood: Double,
    val feedFoodSuffix: Suffix,
    val daysEnd: Int,
    val weightAll: Double,
    val weightSuffix: Suffix,
    val percentFloat: Float,
    val animalList: List<String>
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


