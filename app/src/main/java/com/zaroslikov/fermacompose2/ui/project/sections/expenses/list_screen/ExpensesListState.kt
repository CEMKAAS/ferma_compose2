package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.compose.ui.graphics.Color
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.BasePickList
import com.zaroslikov.fermacompose2.base.state.BaseProductState
import com.zaroslikov.fermacompose2.base.state.DetailNomenclatura
import com.zaroslikov.fermacompose2.base.state.MainList
import com.zaroslikov.fermacompose2.base.state.Product
import com.zaroslikov.fermacompose2.base.state.ProductError
import com.zaroslikov.fermacompose2.base.state.SearchState
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.base.state.UiState
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.BottomSheetState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplatesState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeWarning


data class ExpensesListState(
    val idPT: Long = 0,
    val productDetail: ExpensesTableUi? = null,

    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isArchive: Boolean = false,
    override val ui: ExpensesUiState = ExpensesUiState(),
    override val mainList: ExpensesMainListState = ExpensesMainListState(),

    override val detailNomenclatura: ExpensesDetailNomenclaturaState = ExpensesDetailNomenclaturaState(),
    override val currentProduct: ExpensesProductState = ExpensesProductState(),
    override val searchState: ExpensesSearchState = ExpensesSearchState(),

    override val settings: DomainSettings = DomainSettings(),
    override val bottomSheetState: BottomSheetState = BottomSheetState(),
    override val templatesState: TemplatesState = TemplatesState(),
    override val qrCodeState: QrCodeData? = null,
    override val qrCodeWarning: QrCodeWarning = QrCodeWarning()
) : SectionState


data class ExpensesUiState(
    override val colors: List<Color> = listOf(orang_1, orang_2),
    override val iconRes: Int = R.drawable.icon_expenses
) : UiState

data class ExpensesSearchState(
    override val searchQuery: String = "",
    val searchResults: List<ExpensesTableUi> = emptyList(),
    val searchBrieflyResults: List<BrieflyItem> = emptyList(),
) : SearchState

data class ExpensesMainListState(
    override val isGroupMode: Boolean = true,
    val items: List<ExpensesTableUi> = emptyList(),
    val brieflyItems: List<BrieflyItem> = emptyList()
) : MainList

data class ExpensesDetailNomenclaturaState(
    val detail: BrieflyItem? = null,
    val productItems: List<ExpensesTableUi> = emptyList(),
) : DetailNomenclatura


data class ExpensesProductState(
    override val product: ExpensesProduct = ExpensesProduct(),
    override val errors: ExpensesError = ExpensesError(),
    override val pickList: ExpensesPickList = ExpensesPickList(),
    override val template: TemplateState = TemplateState()
) : BaseProductState


data class ExpensesProduct(
    val isFood: Boolean = false,
    override val itemId: Long = 0,
    override val title: String = "",

    override val count: String = "",
    override val countSuffix: Suffix = Suffix.PIECES,
    val suffixList: List<Suffix> = suffixAllList,

    val weight: String = "",
    val weightSuffix: Suffix = Suffix.KILOGRAM,
    val weightAll: String = "",
    val weightAllSuffix: Suffix = Suffix.KILOGRAM,
    val isShowAutoWeightCheckbox: Boolean = false,
    val isAutoWeight: Boolean = false,
    val weightSuffixList: List<Suffix> = suffixWeightList,

    override val price: String = "",
    override val priceAll: String = "",
    override val isAutoPrice: Boolean = false,
    val priceSuffix: Suffix = Suffix.RUBLE,

    override val category: String = "",
    override val date: String = dateToday(),
    override val note: String = "",

    val isShowFood: Boolean = false,
    val feedFood: String = "",
    val feedFoodSuffix: Suffix = Suffix.GRAM,
    val countAnimalFood: String = "",
    val daysFood: Int = 0,
    val dateEndFood: String = "",

    val countInWarehouse: DomainCountSuffix = DomainCountSuffix(0.0, Suffix.PIECES),

    val isPercent: Boolean = true,

    override val isEntry: Boolean = true,
    val hasIndicators: Boolean = false,

    val suffixSet: Set<Suffix> = setOf(Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS),
    val animalId: Long? = null,

    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,

    override val projectId: Long = 0,
) : Product


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
    val category: String? = null,
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

data class ExpensesPickList(
    val titles: List<TitleAndSuffixDomain> = emptyList(),
    override val categories: List<String> = emptyList(),
    val animalList2: List<AnimalExpensesUi> = emptyList(),
    override val warehouseList: List<DomainCountSuffix> = emptyList(),
) : BasePickList

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

data class ExpensesError(
    val isErrorNameTemplate: Boolean = false,
    override val isErrorTitle: Boolean = false,
    override val isErrorSlash: Boolean = false,
    override val isErrorCount: Boolean = false,
    val isErrorPrice: Boolean = false,
    override val hasAnyError: Boolean = false
) : ProductError


