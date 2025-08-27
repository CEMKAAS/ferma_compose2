package com.zaroslikov.domain.models.DomainAnimalTable

data class DomainAnimalTable(
    val id: Long,
    val name: String,
    val type: String,
    val date: String,
    val dateFactory: String?,
    val group: Boolean,
    val sex: Boolean,
    val note : String,
    val image:String?,
    val archive: Boolean,
    val foodDay:Double,
    val foodDaySuffix : String,
    val idPT: Long,
)
