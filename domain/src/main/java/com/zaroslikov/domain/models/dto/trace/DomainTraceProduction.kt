package com.zaroslikov.domain.models.dto.trace

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix

/**
 * Строка производства (`add_table`) для доски продукции вместе с данными животного-производителя.
 * Поля животного null, если запись сделана без животного (ручное добавление или результат переработки).
 */
data class DomainTraceProduction(
    val id: Long,
    val count: Double,
    val countSuffix: Suffix,
    val day: Int,
    val month: Int,
    val year: Int,
    val price: Double,
    val priceSuffix: Suffix,
    val animalId: Long?,
    val animalName: String?,
    val animalType: String?,
    val animalBreed: String? = null,
    val animalIsGroup: Boolean?,
    /** Пол индивидуального животного: true — самец, false — самка. */
    val animalSex: Boolean? = null,
    /** Сборная группа, в которую входит животное. */
    val animalHerdId: Long? = null,
    val animalIcon: AppIcon?,
    val animalImagePath: String?,
    val note: String
)
