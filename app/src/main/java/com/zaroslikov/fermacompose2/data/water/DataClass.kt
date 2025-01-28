package com.zaroslikov.fermacompose2.data.water

import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable

data class BrieflyItemPrice(
    var title: String,
    var count :Double,
    var price :Double,
    var suffix: String
)

data class BrieflyItemCount(
    var title: String,
    var count :Double,
    var suffix: String
)

data class BrieflyUiState(val itemList: List<BrieflyItemCount> = listOf())
data class BrieflyPriceUiState(val itemList: List<BrieflyItemPrice> = listOf())

data class HomeUiState(val itemList: List<AddTable> = listOf())
data class SaleUiState(val itemList: List<SaleTable> = listOf())
data class ExpensesUiState(val itemList: List<ExpensesTable> = listOf())
data class WriteOffUiState(val itemList: List<WriteOffTable> = listOf())