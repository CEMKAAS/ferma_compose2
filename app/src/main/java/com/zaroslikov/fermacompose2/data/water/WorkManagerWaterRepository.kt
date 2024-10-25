package com.zaroslikov.fermacompose2.data.water

import android.content.Context
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zaroslikov.fermacompose2.data.water.work.FIVE_SECONDS
import com.zaroslikov.fermacompose2.data.water.work.WaterReminderWorker
import java.sql.Time
import java.util.Calendar
import java.util.concurrent.TimeUnit


class WorkManagerWaterRepository(context: Context) : WaterRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleReminder(string: String) {

//        for (day in 0 until durationDays) {
//            for (time in times) {
        val sd = string.split(":")


        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, sd[0].toInt())
            set(Calendar.MINUTE, sd[1].toInt())
            set(Calendar.SECOND, 0)
//            set(
//                Calendar.DAY_OF_YEAR,
//                Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + day
//            )
        }

        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(21, TimeUnit.DAYS)
            .setInitialDelay(
                calendar.timeInMillis - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
            )
            .addTag("time1")
            .build()


//        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>()
//            .setInitialDelay(
//                calendar.timeInMillis - System.currentTimeMillis(),
//                TimeUnit.MILLISECONDS
//            )
//            .addTag()
//            .build()

        workManager.enqueue(workRequest)
    }

//    workManager.enqueue(workRequestBuilder)
}

//        val workRequestBuilder = OneTimeWorkRequestBuilder<WaterReminderWorker>()
//            .setInitialDelay(duration, unit)
//            .setInputData(data.build())
//            .build()


//это не совсем верно
//        val workRequestBuilder =
//            PeriodicWorkRequest.Builder(WaterReminderWorker::class.java, 1, TimeUnit.DAYS)
//                .setInitialDelay(cal(string),TimeUnit.MILLISECONDS)
//                .build()


//        workManager.enqueueUniqueWork(
//            plantName + duration,
//            ExistingWorkPolicy.REPLACE,
//            workRequestBuilder
//        )
//}
//
//private fun cal(string: String): Long {
//    val sd = string.split(":")
//    val calendar = Calendar.getInstance().apply {
//        set(Calendar.HOUR_OF_DAY, sd[0].toInt())
//        set(Calendar.MINUTE, sd[1].toInt())
//        set(Calendar.SECOND, 0)
//    }
//
//    var initialDelay = calendar.timeInMillis - System.currentTimeMillis()
//    if (initialDelay < 0) {
//        initialDelay += TimeUnit.DAYS.toMillis(1)
//    }
//    return initialDelay
//}
//}

