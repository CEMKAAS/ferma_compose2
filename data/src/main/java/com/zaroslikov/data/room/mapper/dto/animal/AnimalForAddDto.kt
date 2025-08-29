package com.zaroslikov.data.room.mapper.dto.animal

import com.zaroslikov.data.room.dto.animal.AnimalForAddDto
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain

fun AnimalForAddDto.toAnimalForAddDomain(): AnimalForAddDomain {
    return AnimalForAddDomain(first = this.id, second = this.name, third = this.type)
}