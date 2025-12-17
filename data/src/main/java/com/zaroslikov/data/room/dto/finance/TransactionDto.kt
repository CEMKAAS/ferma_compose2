package com.zaroslikov.data.room.dto.finance

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix

data class TransactionDto(
    val count: Double,
    val suffix: Suffix,
    val price: Double?,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
    val category: String?,
    val buyer: String? = null,
    val animal: String? = null,
    val date: String,
    @ColumnInfo(name = "category_finance")
    val categoryFinance: FinanceCategory
)