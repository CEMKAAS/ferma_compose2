package com.zaroslikov.data.room.mapper.dto.animal

import com.zaroslikov.data.room.dto.animal.AnimalVaccinationExpensesDto
import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain

fun AnimalVaccinationExpensesDto.toAnimalVaccinationExpensesDomain(): AnimalVaccinationExpensesDomain {
    return AnimalVaccinationExpensesDomain(
        id = this.id,
        vaccination = this.vaccination,
        countVaccination = this.countVaccination,
        date = this.date,
        nextDate = this.nextDate,
        note = this.note,
        idAnimal = this.idAnimal,
        price = this.price,
        priceAll = this.priceAll,
    )
}