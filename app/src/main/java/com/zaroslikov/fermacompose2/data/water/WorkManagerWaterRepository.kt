package com.zaroslikov.fermacompose2.data.water

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zaroslikov.fermacompose2.data.water.work.CHANNEL_ID
import com.zaroslikov.fermacompose2.data.water.work.CHANNEL_ID2
import com.zaroslikov.fermacompose2.data.water.work.NOTIFICATION_ID
import com.zaroslikov.fermacompose2.data.water.work.NOTIFICATION_ID2
import com.zaroslikov.fermacompose2.data.water.work.NOTIFICATION_TITLE
import com.zaroslikov.fermacompose2.data.water.work.NOTIFICATION_TITLE2
import com.zaroslikov.fermacompose2.data.water.work.WaterReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit


class WorkManagerWaterRepository(private var context: Context) : WaterRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleReminder(list: List<String>, name: String) {

        list.forEach {
            if (it != "") {
                val sd = it.split(":")

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, sd[0].toInt())
                    set(Calendar.MINUTE, sd[1].toInt())
                    set(Calendar.SECOND, 0)
                }

                val data = Data.Builder()
                    .putString("name", name)
                    .putString("CHANNEL_ID", CHANNEL_ID)
                    .putString("NOTIFICATION_TITLE", NOTIFICATION_TITLE.toString())
                    .putInt("NOTIFICATION_ID", NOTIFICATION_ID)
                    .build()


                val workRequest =
                    PeriodicWorkRequestBuilder<WaterReminderWorker>(
                        1,
                        TimeUnit.DAYS
                    ).setInitialDelay(
                        calendar.timeInMillis - System.currentTimeMillis(),
                        TimeUnit.MILLISECONDS
                    ).setInputData(data).addTag(name).build()

                workManager.enqueueUniquePeriodicWork(
                    it,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    workRequest
                )
            }
        }
    }

    override fun cancelAllNotifications(name: String) {
        workManager.cancelAllWorkByTag(name)
    }

    override fun setupDailyReminder() {
        val isFirstLaunch = getTimeReminder()
        val sd = isFirstLaunch.split(":")

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, sd[0].toInt())
            set(Calendar.MINUTE, sd[1].toInt())
            set(Calendar.SECOND, 0)
        }

        val data = Data.Builder()
            .putString("name", "Мое Хозяйство")
            .putString("CHANNEL_ID", CHANNEL_ID2)
            .putString("NOTIFICATION_TITLE", NOTIFICATION_TITLE2.toString())
            .putInt("NOTIFICATION_ID", NOTIFICATION_ID2)
            .build()


        val workRequest =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(
                1,
                TimeUnit.DAYS
            ).setInitialDelay(
                calendar.timeInMillis - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
            ).setInputData(data).addTag("7bc20e66-fc56-4002-ac33-4cc15dd28213").build()

        workManager.enqueueUniquePeriodicWork(
            "7bc20e66-fc56-4002-ac33-4cc15dd28213",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    override fun getTimeReminder(): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("app_notifications", null) ?: "20:00"
    }

    override fun setTimeReminder(time: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("app_notifications", time).apply()
    }


}

