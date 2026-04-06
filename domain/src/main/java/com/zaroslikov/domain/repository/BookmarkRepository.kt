package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.dto.incubator.DomainCountRejectedCount
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceAllIncubator
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorHistory
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorMain
import com.zaroslikov.domain.models.dto.incubator.DomainTitleCount
import com.zaroslikov.domain.models.dto.incubator.DomainTypeEggCount
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface BookmarkRepository {
    fun getAllBookmarkTableForExport(): Flow<List<DomainBookmark>>
    suspend fun clearAndInsertBookmarkTableForImport(domainBookmark: List<DomainBookmark>)
    fun getBookmark(id: Long): Flow<DomainBookmark>
    fun getAllBookmark(id: Long): Flow<List<DomainBookmark>>
    fun getBreedBookmark(type: TypeEgg): Flow<List<String>>
    fun getBreedStatisticList(typeEgg: TypeEgg, idPT: Long): Flow<List<DomainTitleCount>>
    fun getTypeStatisticList(idPT: Long): Flow<List<DomainTypeEggCount>>
    fun getCountAndRejectedCountAll(idPT: Long): Flow<DomainCountRejectedCount>
    fun getBookmarkList(type: TypeEgg, idPT: Long): Flow<List<DomainBookmark>>
    fun getBookmarkListByIdPT(idPT: Long): Flow<List<DomainBookmark>>
    fun getActivityBookmark(idPT: Long): Flow<DomainBookmark?>
    suspend fun insert(item: DomainBookmark): Long
    suspend fun update(item: DomainBookmark)
    suspend fun deleteBookmarkById(id: Long)
    suspend fun deleteBookmark(id: DomainBookmark)

    fun getFinanceIncubator(idPT: Long): Flow<DomainFinanceIncubatorMain?>

    fun getFinanceIncubatorAllProject(currencySuffix: Suffix): Flow<DomainFinanceAllIncubator>

    fun getFinanceIncubatorList(idPT: Long): Flow<List<DomainFinanceIncubatorHistory>>
}