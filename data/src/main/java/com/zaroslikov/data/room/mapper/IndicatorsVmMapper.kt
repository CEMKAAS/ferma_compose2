package com.zaroslikov.data.room.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.data.room.table.animal.AnimalWeightTable

//==================== IndicatorToDomain ====================
fun AnimalWeightTable.toDomainMap(): DomainIndicatorsVM = DomainIndicatorsVM(
    id = id,
    weight = weight,
    suffix = suffix,
    date = date,
    idAnimal = idAnimal,
    note = note,
)

fun AnimalSizeTable.toDomainMap(): DomainIndicatorsVM = DomainIndicatorsVM(
    id = id, weight = size, suffix = suffix, date = date, idAnimal = idAnimal,
    note = note,
)

//fun AnimalCountTable.toDomainMap(): DomainIndicatorsVM = DomainIndicatorsVM(
//    id = id, weight = count, suffix = suffix, date = date, idAnimal = idAnimal,
//    note = note, version = version
//)

fun AnimalVaccinationTable.toDomainMap(): DomainIndicatorsVM = DomainIndicatorsVM(
    id = id, weight = vaccination, suffix = nextVaccination, date = date, idAnimal = idAnimal,
    note = note
)


//==================== DomainToIndicator ====================
fun DomainIndicatorsVM.toWeightRoomMap(): AnimalWeightTable = AnimalWeightTable(
    id = id, weight = weight, suffix = suffix, date = date, idAnimal = idAnimal,
    note = note,
)

fun DomainIndicatorsVM.toSizeRoomMap(): AnimalSizeTable = AnimalSizeTable(
    id = id, size = weight, suffix = suffix, date = date, idAnimal = idAnimal,
    note = note,
)

//fun DomainIndicatorsVM.toCountRoomMap(): AnimalCountTable = AnimalCountTable(
//    id = id, count = weight, suffix = suffix, date = date, idAnimal = idAnimal,
//    note = note, version = version
//)

fun DomainIndicatorsVM.toVaccinationRoomMap(): AnimalVaccinationTable = AnimalVaccinationTable(
    id = id, vaccination = weight, date = date, nextVaccination = suffix, idAnimal = idAnimal,
    note = note,
)
