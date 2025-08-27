package com.zaroslikov.data.room.mapper.dto.write_off

import com.zaroslikov.data.room.dto.write_off.BrieflyWriteOffDto
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain

fun BrieflyWriteOffDto.toBrieflyWriteOffDomain(): BrieflyWriteOffDomain {
    return BrieflyWriteOffDomain(title = this.title, count = this.count, suffix = this.suffix,)
}