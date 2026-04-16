package com.zaroslikov.fermacompose2.data.worker

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerRepositoryImpl @Inject constructor(
    private val context: Context
) :
    WorkManagerRepository {
    override fun scheduleReminderIncubator(
        name: String,
        time: String,
        bookmarkId: Long,
        projectId: Long,
        note: String?
    ) {
        val delay = timeCalculate(time)
        val work = OneTimeWorkRequestBuilder<IncubatorWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "time" to time,
                    "name_bookmark" to name,
                    "note" to note,
                    "bookmarkId" to bookmarkId,
                    "projectId" to projectId
                )
            ).addTag(TAG_INCUBATOR)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            "incubator_${bookmarkId}_$time",
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    override fun scheduleReminderProject(
        name: String,
        time: String,
        projectId: Long,
        note: String?
    ) {
        Log.i("project_work", "start")
        val delay = timeCalculate(time)
        Log.i("project_work", "$delay")

        val work = OneTimeWorkRequestBuilder<ProjectWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "time" to time,
                    "name_project" to name,
                    "note" to note,
                    "projectId" to projectId
                )
            ).addTag(TAG_PROJECT)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            "project_${projectId}_$time",
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    override fun cancelAllNotifications() {
        WorkManager.getInstance(context.applicationContext).cancelAllWork()
    }

    override fun cancelIncubatorNotification() {
        WorkManager.getInstance(context.applicationContext).cancelAllWorkByTag(TAG_INCUBATOR)
    }

    override fun cancelProjectNotification() {
        WorkManager.getInstance(context.applicationContext).cancelAllWorkByTag(TAG_PROJECT)
    }

    override fun checkAndStartWorkers(isIncubatorWorkers: Boolean): Boolean {
        var isLaunch = false

        val currentTag = if (isIncubatorWorkers) TAG_INCUBATOR else TAG_PROJECT

        val workManager = WorkManager.getInstance(context.applicationContext)
        workManager.getWorkInfosForUniqueWorkLiveData(currentTag)
            .observeForever { workInfos ->
                isLaunch = workInfos.isNullOrEmpty() || workInfos.any { it.state.isFinished }
            }
        return isLaunch //Если тру, значит нет запущенных воркеров
    }

    private fun timeCalculate(time: String): Long {
        val (hour, minute) = time.split(":").map { it.toInt() }

        val now = LocalDateTime.now()
        var trigger = now.withHour(hour).withMinute(minute).withSecond(0)

        if (trigger.isBefore(now)) {
            trigger = trigger.plusDays(1)
        }
        return Duration.between(now, trigger).toMillis()
    }


    companion object {
        const val TAG_INCUBATOR = "tag_incubator"
        const val TAG_PROJECT = "tag_project"
    }
}