package com.zaroslikov.domain.models.table

import kotlinx.serialization.Serializable

/** Заметка к беременности за конкретный день. Дата — строка `dd.MM.yyyy`, время необязательно. */
@Serializable
data class DomainAnimalPregnancyNote(
    val id: Long = 0,
    val pregnancyId: Long = 0,
    val date: String = "",
    /** `HH:mm`; null — время не указано. */
    val time: String? = null,
    val note: String = ""
)
