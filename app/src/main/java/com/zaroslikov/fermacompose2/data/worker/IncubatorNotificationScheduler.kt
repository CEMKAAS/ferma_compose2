package com.zaroslikov.fermacompose2.data.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.zaroslikov.data.room.table.incubator.BookmarkTable
import java.time.Duration
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

        val (hour, minute) = time.split(":").map { it.toInt() }

        val now = LocalDateTime.now()
        var trigger = now.withHour(hour).withMinute(minute).withSecond(0)

        if (trigger.isBefore(now)) {
            trigger = trigger.plusDays(1)
        }

        val delay = Duration.between(now, trigger).toMillis()

        val work = OneTimeWorkRequestBuilder<IncubatorWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "bookmarkId" to bookmarkId,
                    "time" to time
                )
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "incubator_${bookmarkId}_$time",
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    fun cancelAll(context: Context, bookmark: BookmarkTable) {
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