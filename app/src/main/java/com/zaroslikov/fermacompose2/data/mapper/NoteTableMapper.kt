package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.data.ferma.NoteTable

fun NoteTable.toDomainMap(): DomainNoteTable = DomainNoteTable(
    id = id, title = title, note = note, date = date, idPT = idPT
)

fun DomainNoteTable.toRoomMap(): NoteTable = NoteTable(
    id = id, title = title, note = note, date = date, idPT = idPT
)