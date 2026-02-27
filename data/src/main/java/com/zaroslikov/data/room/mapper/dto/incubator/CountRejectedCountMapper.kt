package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.CountRejectedCountDto
import com.zaroslikov.domain.models.dto.incubator.DomainCountRejectedCount

fun CountRejectedCountDto.toDomainCountRejectedCount(): DomainCountRejectedCount {
    return DomainCountRejectedCount(count = this.count, rejectedCount = this.rejectedCount,)
}