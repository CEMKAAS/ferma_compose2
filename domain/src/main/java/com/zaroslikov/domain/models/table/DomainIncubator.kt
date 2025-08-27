package com.zaroslikov.domain.models.table

data class DomainIncubator(
    var id: Long,
    val day: Int,
    var temp: String,
    var damp: String,
    var over: String,
    var airing: String,
    var idPT: Long
)
