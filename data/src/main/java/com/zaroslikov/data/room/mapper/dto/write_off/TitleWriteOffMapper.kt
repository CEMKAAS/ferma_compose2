package com.zaroslikov.data.room.mapper.dto.write_off

import com.zaroslikov.data.room.dto.write_off.TitleWriteOffDto
import com.zaroslikov.domain.models.dto.write_off.TitleWriteOffDomain

fun TitleWriteOffDto.toTitleWriteOffDomain(): TitleWriteOffDomain {
    return TitleWriteOffDomain(title = this.title, suffix = this.suffix, category = this.category)
}