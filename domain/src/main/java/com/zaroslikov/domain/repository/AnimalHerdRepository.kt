package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.animal.group.DomainHerdCountRecord
import com.zaroslikov.domain.models.animal.group.DomainHerdOperation
import com.zaroslikov.domain.models.dto.add.DomainHerdProduct
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import kotlinx.coroutines.flow.Flow

/**
 * Сборные группы. Операции, меняющие состав, — одна транзакция; нарушение инварианта
 * бросает IllegalStateException.
 */
interface AnimalHerdRepository {
    fun getHerd(id: Long): Flow<DomainAnimalHerd?>

    /** Сборные группы проекта, в которых есть хотя бы один неархивный член. */
    fun getActiveHerds(idPT: Long): Flow<List<DomainAnimalHerd>>

    /** Все члены сборной группы (в том числе архивные). */
    fun getHerdMembers(herdId: Long): Flow<List<DomainAnimalWithCount>>

    /** Все взвешивания неархивных членов (последнее по каждому выбирает вызывающий). */
    fun getHerdWeights(herdId: Long): Flow<List<DomainAnimalWeight>>

    /** Продукция, записанная на членов, включая архивных. */
    fun getHerdProducts(herdId: Long): Flow<List<DomainHerdProduct>>

    /** Самостоятельные неархивные животные и обезличенные группы — кандидаты в сборную группу. */
    fun getStandaloneCandidates(idPT: Long): Flow<List<DomainAnimalWithCount>>

    /** Название, фото/иконка, примечание. Состав не меняется. */
    suspend fun updateHerd(herd: DomainAnimalHerd)

    /** Создаёт сборную группу и добавляет в неё животных. Возвращает её id. */
    suspend fun createHerdWithMembers(herd: DomainAnimalHerd, memberIds: List<Long>): Long

    suspend fun addMembers(herdId: Long, memberIds: List<Long>)

    /** Животное выходит из сборной группы; опустевшая сборная группа удаляется. */
    suspend fun removeMember(memberId: Long)

    /** Несколько членов выходят из группы одной транзакцией. */
    suspend fun removeMembers(memberIds: List<Long>)

    /** Удаляет животное; опустевшая сборная группа удаляется вместе с ним. */
    suspend fun deleteAnimal(id: Long)

    /** Возвращает животное из архива; true — оно вышло из сборной группы (своего поголовья нет). */
    suspend fun unarchiveAnimal(id: Long): Boolean

    /** Все члены становятся самостоятельными, сборная группа удаляется. */
    suspend fun disbandHerd(herdId: Long)

    /**
     * Индивидуальные члены переводятся в счётчик обезличенной группы своего вида внутри этой
     * сборной группы (существующей или новой), их карточки архивируются.
     * Возвращает число обезличенных.
     */
    suspend fun depersonalizeHerd(
        herdId: Long,
        date: String,
        groupNote: String,
        memberNote: String
    ): Int

    /**
     * Выделяет из счётчика обезличенной группы особей с заданными именами. Новые особи попадают
     * в сборную группу исходной группы; если её нет — она создаётся. Возвращает id новых особей.
     */
    suspend fun splitFromCounter(
        groupId: Long,
        names: List<String>,
        sex: Boolean,
        date: String,
        groupNote: String,
        memberNote: String
    ): List<Long>

    /** История поголовья группы: записи счётчиков членов с её меткой, новые первыми. */
    fun getHerdHistory(herdId: Long): Flow<List<DomainHerdCountRecord>>

    /**
     * Продажа, списание, забой, приход или покупка над несколькими членами — одна транзакция.
     * Возвращает id индивидуальных животных, ушедших в архив.
     */
    suspend fun applyHerdOperation(operation: DomainHerdOperation): List<Long>

    /** Новое животное прямо в группе: животное, приход или покупка (с расходом) — одна транзакция. */
    suspend fun insertAnimalIntoHerd(
        herdId: Long,
        animal: DomainAnimalTable,
        count: DomainAnimalCount,
        expenses: DomainExpensesTable?
    ): Long

    fun getAllForExport(): Flow<List<DomainAnimalHerd>>

    suspend fun clearAndInsertForImport(herds: List<DomainAnimalHerd>)
}
