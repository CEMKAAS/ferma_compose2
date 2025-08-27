package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import com.zaroslikov.domain.models.table.DomainAnimalSize

fun AnimalSizeTable.toDomainAnimalSize(): DomainAnimalSize {
    return DomainAnimalSize(
        id = this.id,
        size = this.size,
        suffix = this.suffix,
        date = this.date,
        idAnimal = this.idAnimal,
        note = this.note,
    )
}

fun DomainAnimalSize.toAnimalSizeTable(): AnimalSizeTable {
    return AnimalSizeTable(
        idAnimal = this.idAnimal,
        id = this.id,
        note = this.note,
        suffix = this.suffix,
        size = this.size,
        date = this.date,
    )
}