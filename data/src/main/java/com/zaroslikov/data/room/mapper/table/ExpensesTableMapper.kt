package com.zaroslikov.data.room.mapper

import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.domain.models.DomainExpensesTable


fun DomainExpensesTable.toExpensesRoomMap(): ExpensesTable = ExpensesTable(

    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    day = day,
    mount = month,
    year = year,
    price = price,
    priceAll = priceAll,
    category = category,
    note = note,
    isFood = isFood,
    isShowFood = isShowFood,
    feedFood = feedFood,
    feedFoodSuffix = feedFoodSuffix,
    countAnimal = countAnimal,
    foodDesignedDay = foodDesignedDay,
    lastDayFood = lastDayFood,
    weight = weight,
    weightSuffix = weightSuffix,
    idPT = idPT,
    animalId = animalId,
    animalVaccinationId = animalVaccinationId,
    animalCountId = animalCountId,
)

fun ExpensesTable.toDomainMap(): DomainExpensesTable = DomainExpensesTable(
    id = id,
    title = title,
    count = count,
    day = day,
    month = mount,
    year = year,
    price = price,
    countSuffix = countSuffix,
    category = category,
    note = note,
    isFood = isFood,
    isShowFood = isShowFood,
    feedFood = feedFood,
    countAnimal = countAnimal,
    foodDesignedDay = foodDesignedDay,
    lastDayFood = lastDayFood,
    idPT = idPT,
    animalId = animalId,
    animalVaccinationId = animalVaccinationId,
    animalCountId = animalCountId,
    priceAll = priceAll,
    feedFoodSuffix = feedFoodSuffix,
    weight = weight,
    weightSuffix = weightSuffix,
)