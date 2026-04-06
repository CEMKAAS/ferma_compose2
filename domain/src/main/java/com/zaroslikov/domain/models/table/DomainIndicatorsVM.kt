package com.zaroslikov.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainIndicatorsVM(
    val id: Int = 0,
    val weight: String = "",
    val suffix: String = "",
    val date: String = "0",
    val idAnimal: Int = 0,
    val note: String = "",
    val version: Int? = null,
    val price: Double? = null,
    val buyer: String? = null,
    val _id: Long? = null,
    val idPT: Long? = null,
)