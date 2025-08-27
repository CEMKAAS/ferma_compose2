package com.zaroslikov.data.room.mapper

import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.domain.models.DomainExpensesTable


fun DomainExpensesTable.toExpensesRoomMap(): ExpensesTable = ExpensesTable(
    id = id,
    title = title,
    count = count,
    day = day,
    mount = month,
    year = year,
    price = price,
    countSuffix = countSuffix,
    category = category,
    note = note,
    isShowFood = isShowFood,
    isShowWarehouse = isShowWarehouse,
    isShowAnimals = isShowAnimals,
    isShowFoodHand = isShowFoodHand,
    feedFood = feedFood,
    feedFoodSuffix = feedFoodSuffix,
    countAnimal = countAnimal,
    foodDesignedDay = foodDesignedDay,
    lastDayFood = lastDayFood,
    idPT = idPT,
    animalId = animalId,
    animalVaccinationId = animalVaccinationId,
    animalCountId = animalCountId,
    priceAll = priceAll,
    weight = weight,
    weightSuffix = weightSuffix
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
    isShowFood = isShowFood,
    isShowWarehouse = isShowWarehouse,
    isShowAnimals = isShowAnimals,
    isShowFoodHand = isShowFoodHand,
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