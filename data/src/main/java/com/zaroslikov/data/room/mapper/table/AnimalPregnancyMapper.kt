package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalPregnancyTable
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy

fun AnimalPregnancyTable.toDomainMap(): DomainAnimalPregnancy = DomainAnimalPregnancy(
    id = id,
    animalId = animalId,
    species = species,
    sireId = sireId,
    sireName = sireName,
    matingDate = matingDate,
    status = status,
    endDate = endDate,
    interruptReason = interruptReason,
    bornFemale = bornFemale,
    bornMale = bornMale,
    stillborn = stillborn,
    note = note,
    idPT = idPT
)

fun DomainAnimalPregnancy.toRoomMap(): AnimalPregnancyTable = AnimalPregnancyTable(
    id = id,
    animalId = animalId,
    species = species,
    sireId = sireId,
    sireName = sireName,
    matingDate = matingDate,
    status = status,
    endDate = endDate,
    interruptReason = interruptReason,
    bornFemale = bornFemale,
    bornMale = bornMale,
    stillborn = stillborn,
    note = note,
    idPT = idPT
)
