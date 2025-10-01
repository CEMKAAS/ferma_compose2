package com.zaroslikov.fermacompose2.ui.finance.analysis

import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.supportFun.firstDayOfMonth
import com.zaroslikov.fermacompose2.supportFun.todayOfMonth
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceAnalysisState(
    val productAdd: DomainCountSuffix = DomainCountSuffix(),
    val productSale: DomainTitleSuffixPrice = DomainTitleSuffixPrice(),
    val productWriteOff: DomainCountSuffix = DomainCountSuffix(),
    val productOnwNeeds: DomainCountSuffix = DomainCountSuffix(),
    val productScrap: DomainCountSuffix = DomainCountSuffix(),
    val productSaleSold: Double = 0.0,
    val productWriteOffOnwNeedMoney: Double = 0.0,
    val productWriteOffScrapMoney: Double = 0.0,
    val productAverage: DomainCountSuffix = DomainCountSuffix(),
    val saleBuyerList: List<DomainBuyerPrice> = emptyList(),
    val currentPeriod: String = "",
    val isOpenCalendarDialog: Boolean = false,
    val dateBegin: Pair<String, Long> = firstDayOfMonth(),
    val dateEnd: Pair<String, Long> = todayOfMonth(),
    override val isLoading: Boolean = false,
    override val idPT: Long = 0,
    override val navigate: UiEvent? = null
) : ListState()