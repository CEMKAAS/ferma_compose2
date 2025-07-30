package com.zaroslikov.fermacompose2.Domain.models

import com.zaroslikov.fermacompose2.supportFun.dateTodayArray

data class DomainWriteOffTable(
    val id: Int = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = dateTodayArray()[0],
    val mount: Int = dateTodayArray()[1],
    val year: Int = dateTodayArray()[2],
    val priceAll: String = "",
    val idPT: Long = 0,
    var suffix: String = "",
    val status: Boolean = false,
    val note: String = "",
    val animalCountId : Long? = null,
    val error: Error = Error()
){
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount
    }

    fun validate(): DomainWriteOffTable{
        val error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/"),
            isErrorCount = count.isBlank()
        )
        return this.copy(error = error)
    }

    fun validateTitle(): DomainWriteOffTable {
        return this.copy(
            error = error.copy(
                isErrorTitle = title.isBlank(),
                isErrorSlash = title.contains("/")
            )
        )
    }

    fun validateCount(): DomainWriteOffTable{
        return this.copy(
            error = error.copy(
                isErrorCount = count.isBlank()
            )
        )
    }
}
