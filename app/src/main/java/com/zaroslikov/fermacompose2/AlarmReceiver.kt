package com.zaroslikov.fermacompose2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {
    private var notifManager: NotificationManager? = null

    // Установка уведомления, делал не я, поэтому хз, что и как тут
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show()
        createNotification(context)
    }

    private fun createNotification(context: Context) {
        val NOTIFY_ID: Int = 1002
        val name: String = "my_package_channel"
        val id: String = "my_package_channel_1" // The user-visible name of the channel.
        val description: String =
            "my_package_first_channel" // The user-visible description of the channel.
        val intent: Intent
        val pendingIntent: PendingIntent
        val builder: NotificationCompat.Builder
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            var mChannel: NotificationChannel? = notifManager.getNotificationChannel(id)
            if (mChannel == null) {
                mChannel = NotificationChannel(id, name, importance)
                mChannel.description = description
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(
                    100,
                    200,
                    300,
                    400,
                    500,
                    400,
                    300,
                    200,
                    400
                )
                notifManager.createNotificationChannel(mChannel)
            }
            builder = NotificationCompat.Builder(context, id)
            intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            builder.setContentTitle("Время пришло!") // required
                .setSmallIcon(R.drawable.ic_launcher_foreground) // required
                .setContentText("Пора внести товар и расходы за сегодня!") // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker("ABCD")
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        } else {
            builder = NotificationCompat.Builder(context)
            intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE)
            builder.setContentTitle("Время пришло!") // required
                .setSmallIcon(R.drawable.ic_launcher_foreground) // required
                .setContentText("Пора внести товар и расходы за сегодня!") // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker("ABCD")
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)).priority =
                Notification.PRIORITY_HIGH
        }
        val notification: Notification = builder.build()
        notifManager.notify(NOTIFY_ID, notification)
    }
}