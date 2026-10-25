package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

/**
 * Партия переработки: сырьё списывается (строки write_off с [id] в `recyclingId`),
 * а из него получается одна или несколько позиций продукции ([DomainRecyclingProduct]).
 */
@Serializable
data class DomainRecycling(
    val id: Long = 0,
    val day: Int,
    val month: Int,
    val year: Int,
    val note: String = "",
    val idPT: Long
)

/**
 * Продукция партии переработки [recyclingId]. Пока она не завершена ([isCompleted] = false),
 * её нет ни на складе, ни в учёте — строка add_table появляется только при готовности, её id — [addId].
 * Дата готовности ([readyDay]/[readyMonth]/[readyYear]) задана, только если продукция «зреет».
 */
@Serializable
data class DomainRecyclingProduct(
    val id: Long = 0,
    val recyclingId: Long = 0,
    val title: String,
    val count: Double,
    val countSuffix: Suffix,
    val category: String? = null,
    val readyDay: Int? = null,
    val readyMonth: Int? = null,
    val readyYear: Int? = null,
    val isCompleted: Boolean = false,
    val addId: Long? = null,
    val idPT: Long
)

/** Партия переработки целиком: продукция и списанное сырьё. */
data class DomainRecyclingWithProducts(
    val recycling: DomainRecycling,
    val products: List<DomainRecyclingProduct>,
    val ingredients: List<DomainWriteOffTable>
)
