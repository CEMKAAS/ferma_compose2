package com.zaroslikov.domain.models

data class DomainNoteTable(
    val id: Long,
    val title: String,
    val note: String,
    val date: String,
    val idPT: Long
)