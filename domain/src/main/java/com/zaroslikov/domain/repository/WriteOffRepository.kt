package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.dto.write_off.TitleWriteOffDomain
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import kotlinx.coroutines.flow.Flow

interface WriteOffRepository {
    fun getAllWriteOffItems(id: Long): Flow<List<DomainWriteOffTable>>
    fun getItemWriteOff(id: Long): Flow<DomainWriteOffTable>
    fun getItemWriteOffIdAnimalCount(id: Long): Flow<DomainWriteOffTable>
    fun getBrieflyItemWriteOff(id: Long): Flow<List<BrieflyWriteOffDomain>>
    fun getBrieflyDetailsItemWriteOff(id: Long, name: String): Flow<List<DomainWriteOffTable>>
    fun getItemsWriteOffList(id: Long): Flow<List<TitleWriteOffDomain>>
    suspend fun insertWriteOff(item: DomainWriteOffTable)
    suspend fun updateWriteOff(item: DomainWriteOffTable)
    suspend fun deleteWriteOff(id: Long)
}