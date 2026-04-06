package com.zaroslikov.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainNoteTable(
    val id: Long = 0,
    val title: String ="",
    val note: String = "",
    val date: String = "",
    val idPT: Long = 0
)