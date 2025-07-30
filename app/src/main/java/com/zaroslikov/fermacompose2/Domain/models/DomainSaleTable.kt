package com.zaroslikov.fermacompose2.Domain.models

import com.zaroslikov.fermacompose2.supportFun.dateTodayArray

data class DomainSaleTable (
    val id: Long = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = dateTodayArray()[0],
    val mount: Int = dateTodayArray()[1],
    val year: Int = dateTodayArray()[2],
    val priceAll: String = "",
    var suffix: String = "",
    var category: String = "",
    var buyer: String = "",
    var note: String = "",
    val idPT: Long = 0,
    val animalCountId : Long? = null,
    val error: Error = Error()
){
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
    }

    fun validate(): DomainSaleTable{
        val error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/"),
            isErrorCount = count.isBlank(),
            isErrorPrice = priceAll.isBlank()
        )
        return this.copy(error = error)
    }

    fun validateTitle(): DomainSaleTable {
        return this.copy(
            error = error.copy(
                isErrorTitle = title.isBlank(),
                isErrorSlash = title.contains("/")
            )
        )
    }

    fun validateCount(): DomainSaleTable {
        return this.copy(
            error = error.copy(
                isErrorCount = count.isBlank()
            )
        )
    }
    fun validatePrice(): DomainSaleTable {
        return this.copy(
            error = error.copy(
                isErrorPrice = priceAll.isBlank()
            )
        )
    }
}