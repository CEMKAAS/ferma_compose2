package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix

data class DomainAddItemDto(
    val id: Long = 0,
    override val title: String = "",
    override val count: Double = 0.0,
    override val countSuffix: Suffix = Suffix.KILOGRAM,
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    override val price: Double = 0.0,
    override val priceAll: Double? = null,
    val category: String = "",
    val animalId: Long? = null,
    val nameAnimal: String? = null,
    val note: String = "",
    val idPT: Long = 0,
    val animalCountId: Long? = null
) : BaseProductSection
