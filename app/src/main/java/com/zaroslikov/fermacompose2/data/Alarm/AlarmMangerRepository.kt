package com.zaroslikov.fermacompose2.data.Alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import com.zaroslikov.fermacompose2.AlarmReceiver
import java.util.Calendar

class AlarmMangerRepository(private val context: Context) : AlarmRepository {

    override fun setDailyAlarm(string: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,
            0, intent, FLAG_IMMUTABLE)

        val split = string.split(":")
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, split[0].toInt())
        calendar.set(Calendar.MINUTE, split[1].toInt())
        calendar.set(Calendar.SECOND, 0)

//        alarmManager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] =
//            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        // Устанавливаем время для уведомления (20:00)
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, split[0].toInt())
//            set(Calendar.MINUTE, split[1].toInt())
//            set(Calendar.SECOND, 0)
//
//            // Если текущее время уже позже 20:00, устанавливаем на следующий день
//            if (timeInMillis < System.currentTimeMillis()) {
//                add(Calendar.DAY_OF_YEAR, 1)
//            }
//        }


//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis + 0 * AlarmManager.INTERVAL_DAY,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )


        // Установка повторяющегося будильника
        for (i in 0 until 21) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis + i * AlarmManager.INTERVAL_DAY,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }



    }
}