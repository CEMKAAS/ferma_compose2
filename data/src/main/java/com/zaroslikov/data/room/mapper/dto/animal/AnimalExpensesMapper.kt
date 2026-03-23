package com.zaroslikov.data.room.mapper.dto.animal

import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDto

fun AnimalExpensesDto.toAnimalExpensesDomain(): AnimalExpensesDomain {
    return AnimalExpensesDomain(
        id = this.id,
        name = this.name,
        type = this.type,
        foodDay = this.foodDay,
        foodDaySuffix = this.foodDaySuffix,
        countAnimal = this.countAnimal,
        idExpensesAnimal = this.idExpensesAnimal,
        ps = this.ps,
        presentException = this.presentException,
    )
}