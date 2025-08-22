package com.zaroslikov.fermacompose2.ui.animal.animalCard

import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalCard
import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalTable

data class AnimalCardState(
    val id: Long = 0,
    val name: String = "",
    val type: String = "",
    val date: String = "",
    val dateFactory: String? = null,
    val age: String = "",
    val group: Boolean = false,
    val sex: Boolean = false,
    val note: String = "",
    val image: String? = null,
    val archive: Boolean = false,
    val foodDay: Double = 0.0,
    val foodDaySuffix: String = "",
    val idPT: Long = 0,
    val price : Double=0.0,
    val countAnimal : String? ="",
    val countAnimalSuffix : String? ="",
    val size: String? ="",
    val sizeSuffix: String? ="",
    val weight :String? = "",
    val weightSuffix: String? ="",
    val vaccination: String? ="",
    val vaccinationDate: String? ="",
)

fun AnimalCardState.updateFromDomain(domain: DomainAnimalCard): AnimalCardState {
    return this.copy(
        name = domain.name,
        type = domain.type,
        date = domain.date,
        dateFactory = domain.dateFactory,
        group = domain.group,
        sex = domain.sex,
        note = domain.note,
        archive = domain.archive,
        foodDay = domain.foodDay,
        foodDaySuffix = domain.foodDaySuffix,
        price = price,
        countAnimal = domain.countAnimal,
        countAnimalSuffix = domain.countAnimalSuffix,
        size = domain.size,
        sizeSuffix = domain.sizeSuffix,
        weight = domain.weight,
        weightSuffix = domain.weightSuffix,
        vaccination = domain.vaccination,
        vaccinationDate = domain.vaccinationDate
    )
}
