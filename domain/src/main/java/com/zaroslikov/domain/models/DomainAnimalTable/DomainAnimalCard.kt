package com.zaroslikov.domain.models.DomainAnimalTable

data class DomainAnimalCard(
    val name: String,
    val type: String,
    val date: String,
    val dateFactory: String?,
    val group: Boolean,
    val sex: Boolean,
    val note : String,
    val archive: Boolean,
    val foodDay:Double,
    val foodDaySuffix : String,
    val price : Double?,
    val countAnimal : String?,
    val countAnimalSuffix : String?,
    val size: String?,
    val sizeSuffix: String?,
    val weight :String?,
    val weightSuffix: String?,
    val vaccination: String?,
    val vaccinationDate: String?
)