package com.zaroslikov.fermacompose2.data.water

import java.util.concurrent.TimeUnit

interface WaterRepository {
    fun scheduleReminder(string: String)
}