package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.ui.start.formatNumber

fun DomainExpensesTable.toRoomMap(): ExpensesTable = ExpensesTable(
    id = id,
    title = title.trim(),
    count = count.toConvertDbDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.toConvertDbDouble(),
    suffix = suffix,
    category = category.trim(),
    note = note.trim(),
    showFood = showFood,
    showWarehouse = showWarehouse,
    showAnimals = showAnimals,
    dailyExpensesFoodAndCount = dailyExpensesFoodAndCount,
    dailyExpensesFood = dailyExpensesFood.toConvertZeroString().toConvertDbDouble(),
    countAnimal = countAnimal.toConvertZeroString().toConvertDbOnlyInt(),
    foodDesignedDay = foodDesignedDay.toConvertZeroString().toConvertDbOnlyInt(),
    lastDayFood = lastDayFood,
    idPT = idPT,
    animalId = animalId,
    animalVaccinationId = animalVaccinationId,
    animalCountId = animalCountId
)

fun ExpensesTable.toDomainMap(): DomainExpensesTable = DomainExpensesTable(
    id = id,
    title = title,
    count = count.formatNumber(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.formatNumber(),
    suffix = suffix,
    category = category,
    note = note,
    showFood = showFood,
    showWarehouse = showWarehouse,
    showAnimals = showAnimals,
    dailyExpensesFoodAndCount = dailyExpensesFoodAndCount,
    dailyExpensesFood = dailyExpensesFood.formatNumber(),
    countAnimal = countAnimal.toString(),
    foodDesignedDay = foodDesignedDay.toString(),
    lastDayFood = lastDayFood,
    idPT = idPT,
    animalId = animalId,
    animalVaccinationId = animalVaccinationId,
    animalCountId = animalCountId
)