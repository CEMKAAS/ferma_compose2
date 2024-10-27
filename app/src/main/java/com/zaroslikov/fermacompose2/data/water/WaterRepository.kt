package com.zaroslikov.fermacompose2.data.water

import java.util.concurrent.TimeUnit

interface WaterRepository {
    fun scheduleReminder(list: List<String>, name: String)
    fun cancelAllNotifications(name: String)
    fun setupDailyReminder()
    fun getTimeReminder(): String
    fun setTimeReminder(time: String)
}