package com.zaroslikov.fermacompose2.utils

import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

/**
 * Планирует напоминания об этапах беременности через WorkManager.
 *
 * Работы: `pregnancy_<id>_<stageKey>`, теги `tag_pregnancy`, `tag_pregnancy_<id>`;
 * время показа — 09:00 дня этапа.
 *
 * Уже идущий (начавшийся раньше сегодняшнего дня) этап ставится только при новой случке или
 * смене даты случки ([schedule]). Перепланирование ([rescheduleAll]) берёт лишь этапы с исходной
 * датой не раньше сегодня — иначе многодневный этап напоминал бы при каждом запуске.
 */
class PregnancyReminderScheduler @Inject constructor(
    private val workManagerRepository: WorkManagerRepository,
    private val pregnancyRepository: AnimalPregnancyRepository
) {

    /**
     * Только после новой случки или смены даты случки: отмена по тегу беременности и постановка
     * заново с REPLACE, включая уже начавшийся этап (переносится на сегодня). Правка самца или
     * заметки напоминаний не касается — [schedule] не вызывать. Не ACTIVE или нет норм — только отмена.
     */
    suspend fun schedule(pregnancyId: Long) = withContext(Dispatchers.IO) {
        val pregnancy = pregnancyRepository.getPregnancy(pregnancyId)
        if (pregnancy == null || !enqueue(pregnancy, keepExisting = false)) {
            workManagerRepository.cancelPregnancyReminders(pregnancyId)
        }
    }

    /** После родов, прерывания, удаления записи. */
    fun cancel(pregnancyId: Long) = workManagerRepository.cancelPregnancyReminders(pregnancyId)

    /** Снимает напоминания активной беременности самки (перед удалением животного). */
    suspend fun cancelForAnimal(animalId: Long) {
        pregnancyRepository.getActivePregnancyId(animalId)?.let(::cancel)
    }

    /**
     * Снимает напоминания активных беременностей проекта (архивация, перед удалением проекта).
     * Воркер всё равно не покажет их, но работы не будут висеть в очереди.
     */
    suspend fun cancelForProject(idPT: Long) {
        pregnancyRepository.getActivePregnanciesByProject(idPT).first().forEach { cancel(it.id) }
    }

    /**
     * Старт приложения, (раз)архивация и удаление проекта: БЕЗ глобальной отмены, работы ставятся
     * с KEEP; только этапы с исходной датой не раньше сегодня.
     */
    suspend fun rescheduleAll() = withContext(Dispatchers.IO) {
        pregnancyRepository.getActiveForReminders().forEach { enqueue(it, keepExisting = true) }
    }

    /** Только после импорта бэкапа: глобальная отмена всех напоминаний, затем [rescheduleAll]. */
    suspend fun resetAfterImport() {
        workManagerRepository.cancelAllPregnancyReminders()
        rescheduleAll()
    }

    /** `false` — ставить нечего: беременность не активна, нет норм вида или дата не читается. */
    private fun enqueue(pregnancy: DomainAnimalPregnancy, keepExisting: Boolean): Boolean {
        if (pregnancy.status != PregnancyStatus.ACTIVE) return false
        val norms = PregnancyNorms.of(pregnancy.species) ?: return false
        val matingDate = PregnancyDates.parseOrNull(pregnancy.matingDate) ?: return false
        workManagerRepository.schedulePregnancyReminders(
            pregnancyId = pregnancy.id,
            matingDate = pregnancy.matingDate,
            reminders = PregnancyCalculator.upcomingReminders(
                norms, matingDate, LocalDate.now(), includeStarted = !keepExisting
            ),
            keepExisting = keepExisting
        )
        return true
    }
}
