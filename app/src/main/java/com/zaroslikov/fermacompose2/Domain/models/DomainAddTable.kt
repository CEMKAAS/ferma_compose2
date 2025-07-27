package com.zaroslikov.fermacompose2.Domain.models

import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable.Error
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray

data class DomainAddTable(
    val id: Long = 0,
    val title: String = "",
    val count: String = "",
    var day: Int = dateTodayArray()[0],
    val mount: Int = dateTodayArray()[1],
    val year: Int = dateTodayArray()[2],
    val priceAll: String = "",
    var suffix: String = "",
    var category: String = "",
    var idAnimal: Long = 0,
    var animal: String = "",
    val note: String = "",
    val idPT: Long = 0,
    val error:Error = Error()
) {
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount
    }

    fun validate(): DomainAddTable{
        val error = Error(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/"),
            isErrorCount = count.isBlank(),
        )
        return this.copy(error = error)
    }

    fun validateTitle(): DomainAddTable {
        return this.copy(
            error = DomainAddTable.Error(
                isErrorTitle = title.isBlank(),
                isErrorSlash = title.contains("/")
            )
        )
    }

    fun validateCount(): DomainAddTable {
        return this.copy(
            error = DomainAddTable.Error(
                isErrorCount = count.isBlank()
            )
        )
    }
}