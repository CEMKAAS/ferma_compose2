package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import com.zaroslikov.domain.models.table.DomainAnimalWeight

fun AnimalWeightTable.toDomainAnimalWeight(): DomainAnimalWeight {
    return DomainAnimalWeight(
        id = this.id,
        weight = this.weight,
        suffix = this.suffix,
        date = this.date,
        idAnimal = this.idAnimal,
        note = this.note,
    )
}

fun DomainAnimalWeight.toAnimalWeightTable(): AnimalWeightTable {
    return AnimalWeightTable(
        idAnimal = this.idAnimal,
        id = this.id,
        note = this.note,
        suffix = this.suffix,
        date = this.date,
        weight = this.weight,
    )
}