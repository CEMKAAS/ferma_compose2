package com.zaroslikov.fermacompose2.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zaroslikov.data.room.table.incubator.BookmarkTable
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.MainActivity
import com.zaroslikov.fermacompose2.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProjectWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.i("project_work", "STARTED")

        val time = inputData.getString("time") ?: return Result.success()
        val note = inputData.getString("note")
        val nameProject = inputData.getString("name_project") ?: "Мое хозяйство"
        val projectId = inputData.getLong("projectId", -1)
        if (projectId == -1L) return Result.success()

        showNotification(applicationContext, nameProject, note, projectId)
        val re = WorkManagerRepositoryImpl(applicationContext)

        re.scheduleReminderProject(
            name = nameProject,
            time = time,
            note = note,
            projectId = projectId
        )
        return Result.success()
    }

    private fun showNotification(
        context: Context,
        nameProject: String,
        note: String?,
        projectId: Long
    ) {
        val channelId = "project_channel"
        val text = "Проверьте инкубатор"
        Log.i("work_incubator", "Notification channel created: $channelId")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                text,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Напоминания по активным проектам" }

            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_new_logo)
            .setContentTitle(nameProject)
            .setContentText(note ?: text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(createPendingIntent(context, projectId))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    fun createPendingIntent(appContext: Context, projectId: Long): PendingIntent {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = "OPEN_PROJECT_DETAIL"
            putExtra("itemIdPT", projectId) // Передаем projectId из вашей ViewModel
        }

        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }

        return PendingIntent.getActivity(
            appContext,
            projectId.toInt(),
            intent,
            flags
        )
    }
}