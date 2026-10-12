package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.dto.animal.AnimalInfoUpdate
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.AnimalSpecies

fun AnimalTable.toDomainMap(): DomainAnimalTable = DomainAnimalTable(
    id = id,
    name = name,
    type = type,
    species = species,
    breed = breed,
    date = date,
    dateFactory = dateFactory,
    group = isGroup,
    sex = sex,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    idPT = idPT,
    herdId = herdId,
    motherId = motherId,
    motherName = motherName,
    fatherId = fatherId,
    fatherName = fatherName,
    femalesCount = femalesCount,
    malesCount = malesCount
)

fun DomainAnimalTable.toRoomMap(): AnimalTable = AnimalTable(
    id = id,
    name = name,
    type = type,
    species = storedSpecies(),
    breed = storedBreed(),
    date = date,
    dateFactory = dateFactory,
    isGroup = group,
    sex = sex,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    idPT = idPT,
    herdId = herdId,
    motherId = motherId,
    motherName = motherName,
    fatherId = fatherId,
    fatherName = fatherName,
    femalesCount = femalesCount,
    malesCount = malesCount
)

/** Для частичного update: herd_id меняется только операциями сборных групп. */
fun DomainAnimalTable.toInfoUpdate(): AnimalInfoUpdate = AnimalInfoUpdate(
    id = id,
    name = name,
    type = type,
    species = storedSpecies(),
    breed = storedBreed(),
    date = date,
    dateFactory = dateFactory,
    isGroup = group,
    sex = sex,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    idPT = idPT,
    femalesCount = femalesCount,
    malesCount = malesCount
)

/**
 * Вид для записи в БД всегда согласован с [DomainAnimalTable.type]: «Бык» → COW, свой тип → UNKNOWN.
 * Так species не устареет ни после `copy(type = …)`, ни в старом бэкапе без поля species.
 */
private fun DomainAnimalTable.storedSpecies(): AnimalSpecies = AnimalSpecies.resolve(type)

/** Пустая порода хранится как NULL — карточка показывает её только если она указана. */
private fun DomainAnimalTable.storedBreed(): String? = breed?.trim()?.takeIf { it.isNotEmpty() }
