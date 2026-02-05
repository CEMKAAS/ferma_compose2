package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.TypeEgg

data class DomainBookmark(
    val id: Long = 0,
    val title: String = "",
    val type: TypeEgg = TypeEgg.CHICKENS,
    val breed: String? = null,
    val count: Int = 0,
    val date: String = "",
    val dateEnd: String = "",
    val time: String = "",
    val price: Double? = 0.0,
    val autoPrice: Boolean = false,
    val note: String = "",
    val isAutoRotation: Boolean = false,
    val isAutoVentilation: Boolean = false,
    val isActivityBookmark: Boolean = false,
    val idPT: Long = 0
)