package com.zaroslikov.fermacompose2.data.worker

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.zaroslikov.data.room.table.incubator.BookmarkTable
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object IncubatorNotificationScheduler {

    fun scheduleAll(context: Context, bookmark: BookmarkTable) {

        /* if (!bookmark.isActive) return

         val times = listOf(
             bookmark.notificationTime1,
             bookmark.notificationTime2,
             bookmark.notificationTime3
         ).filterNotNull()

         times.forEach { time ->
             schedule(context, bookmark.id, time)
         }*/
    }

    fun schedule(context: Context, bookmarkId: Long, time: String) {
        Log.i("work_incubator", "schedule: $time")
        val (hour, minute) = time.split(":").map { it.toInt() }

        val now = LocalDateTime.now()
        var trigger = now.withHour(hour).withMinute(minute).withSecond(0)

        if (trigger.isBefore(now)) {
            trigger = trigger.plusDays(1)
        }

        val delay = 10000L//*Duration.between(now, trigger).toMillis()*/
        Log.i("work_incubator", "delay: $delay")
        val work = OneTimeWorkRequestBuilder<IncubatorWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "bookmarkId" to bookmarkId,
                    "time" to time
                )
            )
            .build()
        Log.i("work_incubator", "work: $work")

        val work2 = WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            "incubator_${bookmarkId}_$time",
            ExistingWorkPolicy.REPLACE,
            work
        )
        Log.i("work_incubator", "work2: $work2")
    }

    fun schedule2(context: Context) {
        Log.i("work_incubator", "Start")

        val work = OneTimeWorkRequestBuilder<TestWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        Log.i("work_incubator", "work: $work")

        WorkManager.getInstance(context.applicationContext).enqueue(work)
//        Log.i("work_incubator", "work2: $work2")
    }

    fun cancelAll(context: Context) {
        /*listOf(
            bookmark.notificationTime1,
            bookmark.notificationTime2,
            bookmark.notificationTime3
        ).filterNotNull().forEach { time ->
            WorkManager.getInstance(context)
                .cancelUniqueWork("incubator_${bookmark.id}_$time")
        }*/
    }
}