package com.zaroslikov.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DomainPairDataDoubleSting(
    val first: Double = 0.0, val second: String = ""
)