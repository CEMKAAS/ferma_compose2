package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyBirth
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import kotlinx.coroutines.flow.Flow

interface AnimalPregnancyRepository {
    /** Все беременности самки: новые сверху (mating_date DESC, id DESC). */
    fun getPregnancies(animalId: Long): Flow<List<DomainAnimalPregnancy>>

    fun getActivePregnancy(animalId: Long): Flow<DomainAnimalPregnancy?>

    /** Активные беременности проекта — бейдж в списке животных. */
    fun getActivePregnanciesByProject(idPT: Long): Flow<List<DomainAnimalPregnancy>>

    suspend fun getPregnancy(id: Long): DomainAnimalPregnancy?

    /** Активные беременности у неархивных самок в неархивных проектах — для перепланирования. */
    suspend fun getActiveForReminders(): List<DomainAnimalPregnancy>

    /** Беременность всё ещё ACTIVE с той же датой случки, самка и проект не в архиве. */
    suspend fun isReminderValid(pregnancyId: Long, matingDate: String): Boolean

    suspend fun getActivePregnancyId(animalId: Long): Long?

    /** Самцы того же проекта и вида (включая архивных), кроме [excludeId]. */
    fun getSireCandidates(idPT: Long, species: AnimalSpecies, excludeId: Long): Flow<List<DomainAnimalTable>>

    /** Потомство: животные, у которых [animalId] — мать или отец. */
    fun getChildren(animalId: Long): Flow<List<DomainAnimalTable>>

    /** Мать/отец для родословной; `null`, если животного нет. */
    fun getAnimalOrNull(id: Long): Flow<DomainAnimalTable?>

    /** Нарушение правил → IllegalStateException, транзакция откатывается. Возвращает id. */
    suspend fun startPregnancy(p: DomainAnimalPregnancy): Long

    /** ACTIVE: дата случки, самец, заметка; завершённые: только заметка. */
    suspend fun updatePregnancy(p: DomainAnimalPregnancy)

    suspend fun interrupt(id: Long, date: String, reason: PregnancyInterruptReason, note: String)

    /** Прерывает беременность и отправляет самку [animalId] в архив одной транзакцией. */
    suspend fun interruptAndArchive(
        pregnancyId: Long,
        reason: PregnancyInterruptReason,
        date: String,
        note: String,
        animalId: Long
    )

    /** Возвращает id созданных животных (или id группы). */
    suspend fun registerBirth(birth: PregnancyBirth): List<Long>

    suspend fun deletePregnancy(id: Long)

    fun getAllForExport(): Flow<List<DomainAnimalPregnancy>>

    suspend fun clearAndInsertForImport(list: List<DomainAnimalPregnancy>)

    // Заметки по дням

    /** Заметки всех беременностей самки: новые сверху. */
    fun getNotesByAnimal(animalId: Long): Flow<List<DomainAnimalPregnancyNote>>

    /** Дата — от случки до сегодня и не позже окончания; иначе IllegalStateException. Возвращает id. */
    suspend fun insertNote(note: DomainAnimalPregnancyNote): Long

    /** Меняет дату и текст заметки. */
    suspend fun updateNote(note: DomainAnimalPregnancyNote)

    suspend fun deleteNote(id: Long)

    fun getAllNotesForExport(): Flow<List<DomainAnimalPregnancyNote>>

    /** Только после [clearAndInsertForImport]: её очистка каскадно удаляет заметки. */
    suspend fun clearAndInsertNotesForImport(list: List<DomainAnimalPregnancyNote>)
}
