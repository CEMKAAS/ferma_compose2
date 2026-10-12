package com.zaroslikov.fermacompose2.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.fermacompose2.MainActivity
import com.zaroslikov.fermacompose2.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Напоминание об одном этапе беременности. Каждый этап — отдельная работа, поэтому после
 * показа ничего не перепланируется. Перед показом запись перепроверяется в БД: роды,
 * прерывание, удаление, смена даты случки, архив самки или проекта гасят напоминание.
 */
@HiltWorker
class PregnancyWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val pregnancyRepository: AnimalPregnancyRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pregnancyId = inputData.getLong(KEY_PREGNANCY_ID, -1L)
        val stageKey = inputData.getString(KEY_STAGE_KEY) ?: return Result.success()
        val matingDate = inputData.getString(KEY_MATING_DATE) ?: return Result.success()
        if (pregnancyId == -1L) return Result.success()

        if (!pregnancyRepository.isReminderValid(pregnancyId, matingDate)) return Result.success()

        val pregnancy = pregnancyRepository.getPregnancy(pregnancyId) ?: return Result.success()
        val stage = PregnancyNorms.of(pregnancy.species)?.stages
            ?.firstOrNull { it.key == stageKey } ?: return Result.success()
        val animalName = pregnancyRepository.getAnimalOrNull(pregnancy.animalId).first()?.name
            ?: return Result.success()

        showNotification(
            context = applicationContext,
            title = applicationContext.getString(
                R.string.pregnancy_notification_title, animalName, stage.title
            ),
            text = stage.description,
            projectId = pregnancy.idPT,
            pregnancyId = pregnancyId,
            stageKey = stageKey
        )
        return Result.success()
    }

    private fun showNotification(
        context: Context,
        title: String,
        text: String,
        projectId: Long,
        pregnancyId: Long,
        stageKey: String
    ) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.pregnancy_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = context.getString(R.string.pregnancy_channel_description) }

        context.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)

        val requestCode = (pregnancyId * 31 + stageKey.hashCode()).toInt()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_new_logo)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                createPendingIntent(context, projectId, pregnancyId, stageKey, requestCode)
            )
            .setAutoCancel(true)
            .build()

        if (
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
            NotificationManagerCompat.from(context).notify(requestCode, notification)
    }

    /** Открывает проект, как [ProjectWorker]; уникальный data не даёт PendingIntent-ам слипнуться. */
    private fun createPendingIntent(
        appContext: Context,
        projectId: Long,
        pregnancyId: Long,
        stageKey: String,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = "OPEN_PROJECT_DETAIL"
            data = "myferma-pregnancy://$pregnancyId/$stageKey".toUri()
            putExtra("itemIdPT", projectId)
        }
        return PendingIntent.getActivity(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val CHANNEL_ID = "pregnancy_channel"
        const val KEY_PREGNANCY_ID = "pregnancyId"
        const val KEY_STAGE_KEY = "stageKey"
        const val KEY_MATING_DATE = "matingDate"
    }
}
