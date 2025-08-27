package com.zaroslikov.data.room.mapper

import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable
import com.zaroslikov.domain.models.DomainExpensesAnimal

fun ExpensesAnimalTable.toDomainExpensesAnimal(): DomainExpensesAnimal {
    return DomainExpensesAnimal(
        id = this.id,
        idExpenses = this.idExpenses,
        idAnimal = this.idAnimal,
        percentExpenses = this.percentExpenses,
        idPT = this.idPT,
    )
}

fun DomainExpensesAnimal.toExpensesAnimalTable(): ExpensesAnimalTable {
    return ExpensesAnimalTable(
        idAnimal = this.idAnimal,
        id = this.id,
        percentExpenses = this.percentExpenses,
        idPT = this.idPT,
        idExpenses = this.idExpenses,
    )
}