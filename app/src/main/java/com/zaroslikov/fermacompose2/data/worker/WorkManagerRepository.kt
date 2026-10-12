package com.zaroslikov.fermacompose2.data.worker

import com.zaroslikov.domain.models.animal.pregnancy.PregnancyReminder

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

    /**
     * Ставит напоминания беременности: каждое — отдельная уникальная работа `pregnancy_<id>_<stageKey>`.
     * [keepExisting] = false — действие пользователя: сначала отмена по тегу `tag_pregnancy_<id>`,
     * затем `ExistingWorkPolicy.REPLACE`. true — перепланирование при старте: без отмены, `KEEP`.
     */
    fun schedulePregnancyReminders(
        pregnancyId: Long,
        matingDate: String,
        reminders: List<PregnancyReminder>,
        keepExisting: Boolean
    )

    fun cancelPregnancyReminders(pregnancyId: Long)
    fun cancelAllPregnancyReminders()
}