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
import com.zaroslikov.fermacompose2.MainActivity
import com.zaroslikov.fermacompose2.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class IncubatorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.i("work_incubator", "STARTED")

        val time = inputData.getString("time") ?: return Result.success()
        val note = inputData.getString("note")
        val nameBookmark = inputData.getString("name_bookmark") ?: "Инкубатор"
        val bookmarkId = inputData.getLong("bookmarkId", -1)
        val projectId = inputData.getLong("projectId", -1)
        if (bookmarkId == -1L) return Result.success()

        showNotification(applicationContext, nameBookmark, note, projectId)
        val re = WorkManagerRepositoryImpl(applicationContext)

        re.scheduleReminderIncubator(
            name = nameBookmark,
            time = time,
            bookmarkId = bookmarkId,
            note = note,
            projectId = projectId
        )
        return Result.success()
    }

    private fun showNotification(
        context: Context,
        nameBookmark: String,
        note: String?,
        projectId: Long
    ) {
        val channelId = "incubator_channel"
        val text = "Уведомления инкубатора"
        Log.i("work_incubator", "Notification channel created: $channelId")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                text,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Напоминания по активным закладкам" }

            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(nameBookmark)
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
            action = "OPEN_BOOKMARK_DETAIL"
            putExtra("itemIdPT", projectId) // Передаем projectId из вашей ViewModel
        }

        // Flag to detect unsafe launches of intents for Android 12 and higher
        // to improve platform security
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