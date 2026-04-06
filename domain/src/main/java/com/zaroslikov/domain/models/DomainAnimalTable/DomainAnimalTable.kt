package com.zaroslikov.domain.models.DomainAnimalTable

import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
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
    val foodDaySuffix: Suffix = Suffix.GRAM,
    val idPT: Long = 0
)
