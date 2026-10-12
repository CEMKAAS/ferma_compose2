package com.zaroslikov.data.room.mapper.dto.animal

import com.zaroslikov.data.room.dto.animal.AnimalBreedDto
import com.zaroslikov.domain.models.animal.breed.DomainAnimalBreed

fun AnimalBreedDto.toDomainAnimalBreed(): DomainAnimalBreed {
    return DomainAnimalBreed(
        breed = this.breed,
        species = this.species,
        type = this.type
    )
}
