package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.shared.TitleSuffixCategoryDto
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory

fun TitleSuffixCategoryDto.toDomainTitleSuffixCategory(): DomainTitleSuffixCategory {
    return DomainTitleSuffixCategory(
        title = this.title,
        suffix = this.suffix,
        category = this.category,
    )
}