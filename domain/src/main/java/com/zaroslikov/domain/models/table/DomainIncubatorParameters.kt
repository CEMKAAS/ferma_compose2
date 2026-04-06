package com.zaroslikov.domain.models.table

import kotlinx.serialization.Serializable

@Serializable
data class DomainIncubatorParameters(
    val id: Long = 0,
    val day: Int = 0,
    val temp: String = "",
    val damp: String = "",
    val over: String = "",
    val airing: String = "",
    val tempFact: String = "",
    val dampFact: String = "",
    val overFact: String = "",
    val airingFact: String = "",
    val note: String = "",
    val idPT: Long = 0
)
