package com.zaroslikov.data.room.mapper.dto.finance

import com.zaroslikov.data.room.dto.finance.AnimalWitchCountAndImageDto
import com.zaroslikov.domain.models.dto.finance.DomainAnimalWitchCountAndImage

fun AnimalWitchCountAndImageDto.toDomainAnimalWitchCountAndImage(): DomainAnimalWitchCountAndImage {
    return DomainAnimalWitchCountAndImage(
        title = this.title,
        type = this.type,
        count = this.count,
        countSuffix = this.suffix,
        price = null,
        priceAll = null,
        imagePath = imagePath,
        currentIcon = currentIcon
    )
}