package com.zaroslikov.domain.models.table

data class DomainProjectTable(
    val id: Long = 0,
    val titleProject: String = "",
    val type: String = "",
    val data: String = "",
    val eggAll: String = "",
    val eggAllEND: String = "",
    val airing: String = "",
    val over: String = "",
    var arhive: String = "0",
    val dateEnd: String = "",
    var time1: String = "",
    var time2: String = "",
    var time3: String = "",
    val mode: Int = 0,
    val imageData: ByteArray? = null // Изображение
)
