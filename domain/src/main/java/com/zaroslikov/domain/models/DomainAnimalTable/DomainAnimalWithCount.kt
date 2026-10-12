package com.zaroslikov.domain.models.DomainAnimalTable

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalWithCount(
    val id: Long,
    val name: String,
    val type: String,
    val breed: String?,
    val date: String,
    val dateFactory: String?,
    val group: Boolean,
    val sex: Boolean,
    val foodDay: Double,
    val foodDaySuffix: Suffix,
    val isArchive: Boolean,
    val imagePath: String?,
    val currentIcon: AppIcon?,
    /** Обезличенная группа: сколько самок и самцов; остальное — пол не известен. */
    val femalesCount: Int = 0,
    val malesCount: Int = 0,
    /** Сборная группа, в которой состоит животное; null — самостоятельное. */
    val herdId: Long?,
    val herdName: String?,
    val ownCount: Double,
    /** Суффикс собственного счётчика (первой записи); по умолчанию PIECES. */
    val countSuffix: Suffix
) {
    /** Поголовье: у индивидуального животного и обезличенной группы — свой счётчик, не меньше 0. */
    val totalCount: Double get() = maxOf(ownCount, 0.0)
}
