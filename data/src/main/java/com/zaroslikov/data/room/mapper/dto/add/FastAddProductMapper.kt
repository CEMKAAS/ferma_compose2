package com.zaroslikov.data.room.mapper.dto.add

import com.zaroslikov.data.room.dto.add.FastAddProductDto
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct

fun FastAddProductDto.toDomainFastAddProduct(): DomainFastAddProduct {
    return DomainFastAddProduct(
        title = this.title,
        count = this.count,
        suffix = this.suffix,
        category = this.category,
        idAnimal = this.idAnimal,
        animalName = this.animalName,
        countRow = this.countRow,
    )
}