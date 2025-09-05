package com.zaroslikov.domain.models.DomainAnimalTable

data class DomainAnimalTable(
    val id: Long = 0,
    val name: String = "",
    val type: String = "",
    val date: String = "",
    val dateFactory: String? = null,
    val group: Boolean = false,
    val sex: Boolean = false,
    val note: String = "",
    val image: String? = null,
    val archive: Boolean = false,
    val foodDay: Double = 0.0,
    val foodDaySuffix: String = "",
    val idPT: Long = 0
)
