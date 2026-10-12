package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalPregnancyNoteTable
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote

fun AnimalPregnancyNoteTable.toDomainMap(): DomainAnimalPregnancyNote = DomainAnimalPregnancyNote(
    id = id,
    pregnancyId = pregnancyId,
    date = date,
    time = time,
    note = note
)

fun DomainAnimalPregnancyNote.toRoomMap(): AnimalPregnancyNoteTable = AnimalPregnancyNoteTable(
    id = id,
    pregnancyId = pregnancyId,
    date = date,
    time = time,
    note = note
)
