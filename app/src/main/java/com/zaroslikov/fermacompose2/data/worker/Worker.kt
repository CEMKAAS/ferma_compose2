package com.zaroslikov.fermacompose2.data.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.repository.BookmarkRepositoryImpl
import com.zaroslikov.domain.repository.BookmarkRepository

import com.zaroslikov.fermacompose2.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


class IncubatorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: BookmarkRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val bookmarkId = inputData.getLong("bookmarkId", -1)
        val time = inputData.getString("time") ?: return Result.success()

        val repository = BookmarkRepositoryImpl(applicationContext)

        val bookmark = repository.getBookmarkById(bookmarkId)

        if (bookmark?.isActive == true) {
            showNotification(applicationContext)
        }

        val bookmark = dao.getById(bookmarkId)

        if (bookmark?.isActive == true) {
            showNotification(applicationContext)
        }

        // 👉 перепланируем на следующий день
        IncubatorNotificationScheduler.schedule(
            applicationContext,
            bookmarkId,
            time
        )
        return Result.success()
    }

    private fun showNotification(context: Context) {

        val manager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, "incubator_channel")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Инкубатор")
            .setContentText("Пора проверить закладку")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}