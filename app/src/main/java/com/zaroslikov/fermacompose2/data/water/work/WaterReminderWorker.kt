package com.zaroslikov.fermacompose2.data.water.work

import android.app.Notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zaroslikov.fermacompose2.MainActivity
import com.zaroslikov.fermacompose2.R

class WaterReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val name = inputData.getString("name")
        val id = inputData.getString("CHANNEL_ID")
        val title = inputData.getString("NOTIFICATION_TITLE")
        val notification = inputData.getInt("NOTIFICATION_ID", 2)


        makePlantReminderNotification(
            name ?: "Инкубатор",
            id ?: "Мое Хозяйство",
            title?: "Пора внести товар и расходы за сегодня!",
            notification,
            applicationContext,
        )

        return Result.success()
    }
}

fun makePlantReminderNotification(
    name: String,
    id: String,
    title: String,
    notification: Int,
    context: Context
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            id,
            VERBOSE_NOTIFICATION_CHANNEL_NAME,
            importance
        )
        channel.description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    val pendingIntent: PendingIntent = createPendingIntent(context)

    val builder = NotificationCompat.Builder(context, id)
        .setSmallIcon(R.drawable.ic_stat_name)
        .setContentTitle(name)
        .setContentText(title)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(notification, builder.build())
}

fun createPendingIntent(appContext: Context): PendingIntent {
    val intent = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    // Flag to detect unsafe launches of intents for Android 12 and higher
    // to improve platform security
    var flags = PendingIntent.FLAG_UPDATE_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }

    return PendingIntent.getActivity(
        appContext,
        REQUEST_CODE,
        intent,
        flags
    )
}