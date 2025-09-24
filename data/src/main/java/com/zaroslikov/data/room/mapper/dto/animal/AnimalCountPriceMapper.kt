package com.zaroslikov.data.room.mapper.dto.animal

import com.zaroslikov.data.room.dto.animal.AnimalCountPriceDto
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice

fun AnimalCountPriceDto.toDomainAnimalCountPrice(): DomainAnimalCountPrice {
    return DomainAnimalCountPrice(
        id = this.id,
        count = this.count,
        suffix = this.suffix,
        date = this.date,
        animalId = this.animalId,
        note = this.note,
        version = this.version,
        price = this.price,
        buyer = this.buyer,
        tableId = this.tableId,
        idPT = this.idPT,
    )
}