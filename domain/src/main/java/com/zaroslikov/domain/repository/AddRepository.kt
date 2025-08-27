package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import kotlinx.coroutines.flow.Flow

interface AddRepository {
    fun getItem(id: Int): Flow<DomainAddTable>
    fun getAllItems(id: Int): Flow<List<DomainAddTable>>
    fun getItemAdd(id: Long): Flow<DomainAddTable>
    fun getBrieflyItemAdd(id: Int): Flow<List<BrieflyAddDomain>>
    fun getBrieflyDetailsItemAdd(id: Long, name: String): Flow<List<DomainAddTable>>
    fun getItemsTitleAddList(id: Long): Flow<List<TitleAndSuffixDomain>>
    fun getItemsCategoryAddList(id: Long): Flow<List<String>>
    fun getAnimalById(id: Long): Flow<String>
    suspend fun insert(item: DomainAddTable)
    suspend fun update(item: DomainAddTable)
    suspend fun deleteAddById(id: Long)
}