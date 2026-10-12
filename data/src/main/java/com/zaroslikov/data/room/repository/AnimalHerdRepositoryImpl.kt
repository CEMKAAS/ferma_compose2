package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalHerdDao
import com.zaroslikov.data.room.mapper.AnimaMapper.dto.toDomain
import com.zaroslikov.data.room.mapper.table.toDomain
import com.zaroslikov.data.room.mapper.table.toRoom
import com.zaroslikov.data.room.mapper.table.toDomainAnimalWeight
import com.zaroslikov.domain.models.animal.group.DomainHerdCountRecord
import com.zaroslikov.domain.models.animal.group.DomainHerdOperation
import com.zaroslikov.domain.models.dto.add.DomainHerdProduct
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.domain.repository.AnimalHerdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalHerdRepositoryImpl @Inject constructor(
    private val animalHerdDao: AnimalHerdDao
) : AnimalHerdRepository {

    override fun getHerd(id: Long): Flow<DomainAnimalHerd?> =
        animalHerdDao.getHerdFlow(id).map { it?.toDomain() }

    override fun getActiveHerds(idPT: Long): Flow<List<DomainAnimalHerd>> =
        animalHerdDao.getActiveHerds(idPT).map { list -> list.map { it.toDomain() } }

    override fun getHerdMembers(herdId: Long): Flow<List<DomainAnimalWithCount>> =
        animalHerdDao.getHerdMembers(herdId).map { list -> list.map { it.toDomain() } }

    override fun getHerdWeights(herdId: Long): Flow<List<DomainAnimalWeight>> =
        animalHerdDao.getHerdWeights(herdId).map { list -> list.map { it.toDomainAnimalWeight() } }

    override fun getHerdProducts(herdId: Long): Flow<List<DomainHerdProduct>> =
        animalHerdDao.getHerdProducts(herdId).map { list ->
            list.map { DomainHerdProduct(it.animalId, it.title, it.count, it.suffix, it.price) }
        }

    override fun getStandaloneCandidates(idPT: Long): Flow<List<DomainAnimalWithCount>> =
        animalHerdDao.getStandaloneCandidates(idPT).map { list -> list.map { it.toDomain() } }

    override suspend fun updateHerd(herd: DomainAnimalHerd) =
        animalHerdDao.updateHerd(herd.toRoom())

    override suspend fun createHerdWithMembers(herd: DomainAnimalHerd, memberIds: List<Long>): Long =
        animalHerdDao.createHerdWithMembers(herd.toRoom(), memberIds)

    override suspend fun addMembers(herdId: Long, memberIds: List<Long>) =
        animalHerdDao.addMembers(herdId, memberIds)

    override suspend fun removeMember(memberId: Long) =
        animalHerdDao.removeMember(memberId)

    override suspend fun removeMembers(memberIds: List<Long>) =
        animalHerdDao.removeMembers(memberIds)

    override suspend fun deleteAnimal(id: Long) =
        animalHerdDao.deleteAnimal(id)

    override suspend fun unarchiveAnimal(id: Long): Boolean =
        animalHerdDao.unarchiveAnimal(id)

    override suspend fun disbandHerd(herdId: Long) =
        animalHerdDao.disbandHerd(herdId)

    override suspend fun depersonalizeHerd(
        herdId: Long,
        date: String,
        groupNote: String,
        memberNote: String
    ): Int = animalHerdDao.depersonalizeHerd(herdId, date, groupNote, memberNote)

    override suspend fun splitFromCounter(
        groupId: Long,
        names: List<String>,
        sex: Boolean,
        date: String,
        groupNote: String,
        memberNote: String
    ): List<Long> = animalHerdDao.splitFromCounter(groupId, names, sex, date, groupNote, memberNote)

    override fun getHerdHistory(herdId: Long): Flow<List<DomainHerdCountRecord>> =
        animalHerdDao.getHerdHistory(herdId).map { list ->
            list.map {
                DomainHerdCountRecord(
                    id = it.id,
                    count = it.count,
                    suffix = it.suffix,
                    date = it.date,
                    note = it.note,
                    version = it.version,
                    animalId = it.animalId,
                    animalName = it.animalName,
                    isGroup = it.isGroup,
                    price = it.price,
                    priceAll = it.priceAll,
                    buyer = it.buyer,
                    femalesCount = it.femalesCount,
                    malesCount = it.malesCount
                )
            }
        }

    override suspend fun applyHerdOperation(operation: DomainHerdOperation): List<Long> =
        animalHerdDao.applyHerdOperation(operation)

    override suspend fun insertAnimalIntoHerd(
        herdId: Long,
        animal: DomainAnimalTable,
        count: DomainAnimalCount,
        expenses: DomainExpensesTable?
    ): Long = animalHerdDao.insertAnimalIntoHerd(herdId, animal, count, expenses)

    override fun getAllForExport(): Flow<List<DomainAnimalHerd>> =
        animalHerdDao.getAllForExport().map { list -> list.map { it.toDomain() } }

    override suspend fun clearAndInsertForImport(herds: List<DomainAnimalHerd>) =
        animalHerdDao.clearAndInsertForImport(herds.map { it.toRoom() })
}
