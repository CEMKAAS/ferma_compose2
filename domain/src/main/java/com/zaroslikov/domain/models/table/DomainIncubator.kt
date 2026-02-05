package com.zaroslikov.domain.models.table

data class DomainIncubator(
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
