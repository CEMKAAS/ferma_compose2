package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

/**
 * Переработка: сырьё списывается (строки write_off с [id] в `recyclingId`), а из него получается
 * продукция [title]. Пока переработка не завершена ([isCompleted] = false), продукции нет
 * ни на складе, ни в учёте — строка add_table появляется только при готовности, её id — [addId].
 * Дата готовности ([readyDay]/[readyMonth]/[readyYear]) задана, только если продукция «зреет».
 */
@Serializable
data class DomainRecycling(
    val id: Long = 0,
    val title: String,
    val count: Double,
    val countSuffix: Suffix,
    val category: String? = null,
    val day: Int,
    val month: Int,
    val year: Int,
    val readyDay: Int? = null,
    val readyMonth: Int? = null,
    val readyYear: Int? = null,
    val isCompleted: Boolean = false,
    val addId: Long? = null,
    val note: String = "",
    val idPT: Long
)
