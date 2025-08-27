package com.zaroslikov.data.room.mapper.table


import com.zaroslikov.data.room.table.ferma.NoteTable
import com.zaroslikov.domain.models.DomainNoteTable

fun NoteTable.toDomainMap(): DomainNoteTable = DomainNoteTable(
    id = id, title = title, note = note, date = date, idPT = idPT
)

fun DomainNoteTable.toNoteMap(): NoteTable = NoteTable(
    id = id, title = title, note = note, date = date, idPT = idPT
)