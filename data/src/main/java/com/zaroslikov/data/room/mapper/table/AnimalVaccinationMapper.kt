package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.domain.models.table.DomainAnimalVaccination

fun AnimalVaccinationTable.toDomainAnimalVaccination(): DomainAnimalVaccination {
    return DomainAnimalVaccination(
        id = this.id,
        countVaccination = this.countVaccination,
        vaccination = this.vaccination,
        date = this.date,
        nextVaccination = this.nextVaccination,
        idAnimal = this.animalId,
        note = this.note,
    )
}

fun DomainAnimalVaccination.toAnimalVaccinationTable(): AnimalVaccinationTable {
    return AnimalVaccinationTable(
        id = this.id,
        vaccination = this.vaccination,
        countVaccination = this.countVaccination,
        date = this.date,
        nextVaccination = this.nextVaccination,
        note = this.note,
        animalId = this.idAnimal,
    )
}