package com.zaroslikov.fermacompose2.data.water


import androidx.annotation.StringRes
import java.util.concurrent.TimeUnit

data class Reminder(
    @StringRes val durationRes: Int,
    val duration: Long,
    val unit: TimeUnit,
    val plantName: String
)