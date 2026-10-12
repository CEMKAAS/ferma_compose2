package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalPregnancyDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyBirth
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalPregnancyRepositoryImpl @Inject constructor(
    private val pregnancyDao: AnimalPregnancyDao
) : AnimalPregnancyRepository {

    override fun getPregnancies(animalId: Long): Flow<List<DomainAnimalPregnancy>> =
        pregnancyDao.getPregnancies(animalId).map { list -> list.map { it.toDomainMap() } }

    override fun getActivePregnancy(animalId: Long): Flow<DomainAnimalPregnancy?> =
        pregnancyDao.getActivePregnancy(animalId).map { it?.toDomainMap() }

    override fun getActivePregnanciesByProject(idPT: Long): Flow<List<DomainAnimalPregnancy>> =
        pregnancyDao.getActivePregnanciesByProject(idPT).map { list -> list.map { it.toDomainMap() } }

    override suspend fun getPregnancy(id: Long): DomainAnimalPregnancy? =
        pregnancyDao.getPregnancy(id)?.toDomainMap()

    override suspend fun getActiveForReminders(): List<DomainAnimalPregnancy> =
        pregnancyDao.getActiveForReminders().map { it.toDomainMap() }

    override suspend fun isReminderValid(pregnancyId: Long, matingDate: String): Boolean =
        pregnancyDao.isReminderValid(pregnancyId, matingDate)

    override suspend fun getActivePregnancyId(animalId: Long): Long? =
        pregnancyDao.getActivePregnancyId(animalId)

    override fun getSireCandidates(
        idPT: Long,
        species: AnimalSpecies,
        excludeId: Long
    ): Flow<List<DomainAnimalTable>> =
        pregnancyDao.getSireCandidates(idPT, species, excludeId).map { list -> list.map { it.toDomainMap() } }

    override fun getChildren(animalId: Long): Flow<List<DomainAnimalTable>> =
        pregnancyDao.getChildren(animalId).map { list -> list.map { it.toDomainMap() } }

    override fun getAnimalOrNull(id: Long): Flow<DomainAnimalTable?> =
        pregnancyDao.getAnimalOrNull(id).map { it?.toDomainMap() }

    override suspend fun startPregnancy(p: DomainAnimalPregnancy): Long =
        pregnancyDao.startPregnancy(p.toRoomMap())

    override suspend fun updatePregnancy(p: DomainAnimalPregnancy) =
        pregnancyDao.updatePregnancy(p.toRoomMap())

    override suspend fun interrupt(id: Long, date: String, reason: PregnancyInterruptReason, note: String) =
        pregnancyDao.interrupt(id, date, reason, note)

    override suspend fun interruptAndArchive(
        pregnancyId: Long,
        reason: PregnancyInterruptReason,
        date: String,
        note: String,
        animalId: Long
    ) = pregnancyDao.interruptAndArchive(pregnancyId, reason, date, note, animalId)

    override suspend fun registerBirth(birth: PregnancyBirth): List<Long> =
        pregnancyDao.registerBirth(birth)

    override suspend fun deletePregnancy(id: Long) =
        pregnancyDao.deletePregnancy(id)

    override fun getAllForExport(): Flow<List<DomainAnimalPregnancy>> =
        pregnancyDao.getAllForExport().map { list -> list.map { it.toDomainMap() } }

    override suspend fun clearAndInsertForImport(list: List<DomainAnimalPregnancy>) =
        pregnancyDao.clearAndInsertForImport(list.map { it.toRoomMap() })

    override fun getNotesByAnimal(animalId: Long): Flow<List<DomainAnimalPregnancyNote>> =
        pregnancyDao.getNotesByAnimal(animalId).map { list -> list.map { it.toDomainMap() } }

    override suspend fun insertNote(note: DomainAnimalPregnancyNote): Long =
        pregnancyDao.insertDayNote(note.toRoomMap())

    override suspend fun updateNote(note: DomainAnimalPregnancyNote) =
        pregnancyDao.updateDayNote(note.toRoomMap())

    override suspend fun deleteNote(id: Long) =
        pregnancyDao.deleteNote(id)

    override fun getAllNotesForExport(): Flow<List<DomainAnimalPregnancyNote>> =
        pregnancyDao.getAllNotesForExport().map { list -> list.map { it.toDomainMap() } }

    override suspend fun clearAndInsertNotesForImport(list: List<DomainAnimalPregnancyNote>) =
        pregnancyDao.clearAndInsertNotesForImport(list.map { it.toRoomMap() })
}
