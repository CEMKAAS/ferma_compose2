package com.zaroslikov.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainExpensesAnimal(
    val id: Long,
    val idExpenses: Long,
    val idAnimal: Long,
    val percentExpenses: Double,
    val idPT: Long
)
