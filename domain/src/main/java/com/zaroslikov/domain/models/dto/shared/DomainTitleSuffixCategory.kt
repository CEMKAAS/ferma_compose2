package com.zaroslikov.domain.models.dto.shared

import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix

data class DomainTitleSuffixCategory(
    val title : String,
    val suffix: Suffix,
    val productOrigin: ProductOrigin
)