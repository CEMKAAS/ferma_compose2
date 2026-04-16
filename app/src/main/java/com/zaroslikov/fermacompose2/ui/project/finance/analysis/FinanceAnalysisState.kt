package com.zaroslikov.fermacompose2.ui.project.finance.analysis

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix
import com.zaroslikov.domain.models.dto.finance.DomainTransaction
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.supportFun.DomainChartPoint
import com.zaroslikov.fermacompose2.supportFun.firstDayOfMonth
import com.zaroslikov.fermacompose2.supportFun.todayOfMonth
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceAnalysisState(
    val baseSuffix: Suffix = Suffix.LITERS,
    val settingSuffix: Suffix = Suffix.LITERS,
    val settings: DomainSettings = DomainSettings(),
    val titleProduct: String = "",
    val totalPrice: Double = 0.0,
    val countProduct: Double = 0.0,
    val realizedPrice: Double = 0.0,
    val potentialBalance: Double = 0.0,
    val soldLost: Double = 0.0,
    val stock: Double = 0.0,
    val averagePrice: Double = 0.0,
    val charSelection: FinanceAnalysisEnum = FinanceAnalysisEnum.STOCK,
    val transactionList: List<DomainTransaction> = emptyList(),
    val charFilter: List<Pair<List<DomainChartPoint>, FinanceAnalysisEnum>> = emptyList(),
    val buyers: List<Buyer> = emptyList(),
    val animalProducer: List<AnimalProducer> = emptyList(),
    val financeAnalysis: List<FinanceAnalysis> = emptyList(),
    val dateFilter: DateFilterAnalysis = DateFilterAnalysis(),
    override val isLoading: Boolean = false,
    override val idPT: Long = 0,
    override val navigate: UiEvent? = null
) : ListState()

data class DateFilterAnalysis(
    val isOpenCalendarDialog: Boolean = false,
    val filterDate: FilterDate = FilterDate.ALL_TIME,
    val dateBegin: Pair<String, Long> = firstDayOfMonth(),
    val dateEnd: Pair<String, Long> = todayOfMonth(),
    val currentPeriod: String = ""
)

data class Buyer(
    val buyer: String,
    val count: Double,
    val suffix: Suffix,
    val price: Double,
    val priceSuffix: Suffix,
    val countTransaction: Int
)

data class AnimalProducer(
    val name: String,
    val type: String,
    val count: Double,
    val suffix: Suffix,
    val percentDouble: Double,
    val percentFloat: Float,
    val imagePath: String?,
    val currentIcon: Int,
)

data class FinanceAnalysis(
    val totalCount: Double,
    val suffixCount: Suffix,
    val totalPrice: Double,
    val suffixPrice: Suffix,
    val averagePrice: Double,
    val percentDouble: Double,
    val percentFloat: Float,
    val financeAnalysis: FinanceAnalysisEnum
)

data class AnalysisUiCalculatedState(
    val buyers: List<Buyer>,
    val animalProducer: List<AnimalProducer>,
    val financeAnalysis: List<FinanceAnalysis> = emptyList(),
    val totalPrice: Double = 0.0,
    val countProduct: Double = 0.0,
    val realizedPrice: Double = 0.0,
    val potentialBalance: Double = 0.0,
    val soldLost: Double = 0.0,
    val stock: Double = 0.0,
    val averagePrice: Double = 0.0,
    val transactionList: List<DomainTransaction>,
    val charFilter: List<Pair<List<DomainChartPoint>, FinanceAnalysisEnum>> = emptyList(),
)


enum class FinanceAnalysisEnum(
    val colorBackground: Color,
    val iconBackground: Color,
    val textColor: Color,
    @param:DrawableRes val icon: Int,
    @param:StringRes val title: Int,
    @param:StringRes val titleButton: Int,
    @param:StringRes val titleChar: Int,
    val sign: String,
    val colorTextPercent: Color
) {
    STOCK(
        colorBackground = blue_3,
        iconBackground = blue_4,
        textColor = blue_1,
        icon = R.drawable.icon_add_product,
        title = R.string.analysis_screen_in_stock,
        titleButton = R.string.analysis_screen_produced,
        titleChar = R.string.analysis_screen_char_produced,
        sign = "~",
        colorTextPercent = blue_8
    ),
    SALE(
        colorBackground = price_green_2,
        iconBackground = green_6,
        textColor = price_green,
        icon = R.drawable.icon_sale,
        title = R.string.analysis_screen_sold,
        titleButton = R.string.analysis_screen_sold,
        titleChar = R.string.analysis_screen_char_sale,
        sign = "",
        colorTextPercent = green_9
    ),
    OWN_NEED(
        colorBackground = orang_4,
        iconBackground = orang_9,
        textColor = orang_2,
        icon = R.drawable.outline_savings_24,
        title = R.string.analysis_screen_saved,
        titleButton = R.string.analysis_screen_saved,
        titleChar = R.string.analysis_screen_char_own_need,
        sign = "",
        colorTextPercent = orang_6
    ),
    SCRAP(
        colorBackground = red_11,
        iconBackground = red_13,
        textColor = error_base,
        icon = R.drawable.icon_trash,
        title = R.string.analysis_screen_lost,
        titleButton = R.string.analysis_screen_lost,
        titleChar = R.string.analysis_screen_char_scrap,
        sign = "-",
        colorTextPercent = red_14
    )
}