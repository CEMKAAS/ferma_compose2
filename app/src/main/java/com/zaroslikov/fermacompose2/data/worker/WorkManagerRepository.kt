package com.zaroslikov.fermacompose2.data.worker

interface WorkManagerRepository {
    fun scheduleReminderIncubator(
        name: String,
        time: String,
        bookmarkId: Long,
        projectId: Long,
        note: String?
    )

    fun scheduleReminderProject(
        name: String,
        time: String,
        projectId: Long,
        note: String?
    )

    fun cancelAllNotifications()
    fun cancelIncubatorNotification()
    fun cancelProjectNotification()
    fun checkAndStartWorkers(isIncubatorWorkers: Boolean): Boolean
}