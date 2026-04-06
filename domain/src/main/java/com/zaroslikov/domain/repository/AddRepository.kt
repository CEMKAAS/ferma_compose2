package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffixDate
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import kotlinx.coroutines.flow.Flow

interface AddRepository {
    fun getAllAddTableForExport(): Flow<List<DomainAddTable>>

    suspend fun clearAndInsertAddTableForImport(domainAddTable: List<DomainAddTable>)
    fun getItem(id: Long): Flow<DomainAddTable>
    fun getAllItems(id: Long): Flow<List<DomainAddItemDto>>
    fun getItemAdd(id: Long): Flow<DomainAddTable>
    fun getBrieflyItemAdd(id: Long): Flow<List<BrieflyAddDomain>>
    fun getBrieflyDetailsItemAdd(id: Long, name: String): Flow<List<DomainAddItemDto>>
    fun getItemsTitleAddList(id: Long): Flow<List<TitleAndSuffixDomain>>
    fun getItemsCategoryAddList(id: Long): Flow<List<String>>
    fun getAnimalById(id: Long): Flow<String>

    suspend fun insertAdd(item: DomainAddTable)
    suspend fun updateAdd(item: DomainAddTable)
    suspend fun deleteAddById(id: Long)

    fun getFastAddProduct(id: Long): Flow<List<DomainFastAddProduct>>
    fun getAnalysisAddAllTime(id: Long, name: String): Flow<DomainCountSuffix?>
    fun getAnalysisAddAverageValueAllTime(id: Long, name: String): Flow<DomainCountSuffix?>
    fun getAnalysisAddAnimalAllTime(
        id: Long,
        name: String
    ): Flow<List<DomainAnimalCountSuffix>>

    fun getAnalysisAddRangeList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCountSuffixPriceDate>>

    fun getAnalysisAddAverageValueAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix?>

    fun getAnalysisAddAnimalRangeList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainAnimalCountSuffix>>

    fun getProductAnimal(idAnimal: Long): Flow<List<DomainAnimalCountSuffix>>
    fun getProductKillList(id: Long): Flow<List<DomainAddTable>>
    /*  fun getAnalysisAddProductNewYearProject(
          id: Long,
          dateBegin: String,
          dateEnd: String
      ): Flow<List<AnalysisSaleBuyerAllTime>> //TODO Buyer -> Title

      fun getAnalysisAddProductNewYear(
          dateBegin: String,
          dateEnd: String
      ): Flow<List<AnalysisSaleBuyerAllTime>> //TODO Buyer -> Title
  */
}