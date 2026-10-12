package com.zaroslikov.crm.domain.model

import kotlinx.serialization.Serializable

/** Задача проекта, при необходимости привязанная к клиенту; [dueDate] в формате `yyyy-MM-dd`. */
@Serializable
data class DomainCrmTask(
    val id: Long = 0,
    val idPT: Long,
    val clientId: Long? = null,
    val title: String,
    val dueDate: String,
    val isDone: Boolean = false,
    val note: String = ""
)
