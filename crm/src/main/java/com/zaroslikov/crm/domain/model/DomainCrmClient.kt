package com.zaroslikov.crm.domain.model

import kotlinx.serialization.Serializable

/**
 * Клиент проекта.
 *
 * [salesKey] — неизменяемый ключ связи с `sale_table.buyer` (TRIM имени на момент создания),
 * вся статистика матчится по нему. [name] — только отображение, его можно свободно править.
 */
@Serializable
data class DomainCrmClient(
    val id: Long = 0,
    val idPT: Long,
    val name: String,
    val salesKey: String,
    val source: CrmClientSource = CrmClientSource.MANUAL,
    val isVip: Boolean = false,
    val note: String = "",
    val isFavorite: Boolean = false,
    val createdDate: String,
    val isArchived: Boolean = false
)
