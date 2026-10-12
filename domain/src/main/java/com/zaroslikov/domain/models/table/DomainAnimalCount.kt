package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainAnimalCount(
    val id: Long = 0,
    val count: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = "",
    val note: String = "",
    val version: AnimalCountVersion? = null,
    val idAnimal: Long = 0,
    /** Сборная группа, в составе которой сделана запись; null — вне группы. */
    val herdId: Long? = null,
    /** Обезличенная группа: сколько из [count] самок и самцов; остальное — пол не известен. */
    val femalesCount: Int = 0,
    val malesCount: Int = 0
)