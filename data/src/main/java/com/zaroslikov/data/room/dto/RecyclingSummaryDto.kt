package com.zaroslikov.data.room.dto

data class RecyclingSummaryDto(
    val activeBatches: Int,
    val activePositions: Int,
    val completedBatches: Int,
    val interruptedBatches: Int
)
