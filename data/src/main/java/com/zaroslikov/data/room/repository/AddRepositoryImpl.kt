package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AddDao
import com.zaroslikov.data.room.mapper.dto.add.toDomainAddItemDto
import com.zaroslikov.data.room.mapper.dto.add.toDomainFastAddProduct
import com.zaroslikov.data.room.mapper.dto.finance.toDomainAnimalWitchCountAndImage
import com.zaroslikov.data.room.mapper.dto.sale.toDomainCountSuffixPriceDate
import com.zaroslikov.data.room.mapper.dto.shared.toDomainAnimalCountSuffix
import com.zaroslikov.data.room.mapper.dto.shared.toDomainCountSuffix
import com.zaroslikov.data.room.mapper.dto.toBrieflyAddDomain
import com.zaroslikov.data.room.mapper.dto.toTitleAndSuffixDomain
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.toAddDomainMap
import com.zaroslikov.data.room.mapper.toAddRoomMap
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.finance.DomainAnimalWitchCountAndImage
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.repository.AddRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddRepositoryImpl @Inject constructor(private val addDao: AddDao) : AddRepository {
    override fun getAllAddTableForExport(): Flow<List<DomainAddTable>> {
        return addDao.getAllAddTableForExport().map { it -> it.map { it.toAddDomainMap() } }
    }

    override suspend fun clearAndInsertAddTableForImport(domainAddTable: List<DomainAddTable>) {
        return addDao.clearAndInsertAddTableForImport(domainAddTable.map { it.toAddRoomMap() })
    }

    override fun getItem(id: Long): Flow<DomainAddTable> {
        return addDao.getItem(id).map { it.toAddDomainMap() }
    }

    override fun getAllItems(id: Long): Flow<List<DomainAddItemDto>> {
        return addDao.getAllItems(id).map { it -> it.map { it.toDomainAddItemDto() } }
    }

    override fun getItemAdd(id: Long): Flow<DomainAddTable> {
        return addDao.getItemAdd(id).map { it.toAddDomainMap() }
    }

    override fun getBrieflyItemAdd(id: Long): Flow<List<BrieflyAddDomain>> {
        return addDao.getBrieflyItemAdd(id).map { it -> it.map { it.toBrieflyAddDomain() } }
    }

    override fun getBrieflyDetailsItemAdd(
        id: Long,
        name: String
    ): Flow<List<DomainAddItemDto>> {
        return addDao.getBrieflyDetailsItemAdd(id, name)
            .map { it -> it.map { it.toDomainAddItemDto() } }
    }

    override fun getItemsTitleAddList(id: Long): Flow<List<TitleAndSuffixDomain>> {
        return addDao.getItemsTitleAddList(id).map { it -> it.map { it.toTitleAndSuffixDomain() } }
    }

    override fun getItemsCategoryAddList(id: Long): Flow<List<String>> {
        return addDao.getItemsCategoryAddList(id)
    }

    override fun getAnimalById(id: Long): Flow<String> {
        return addDao.getAnimalById(id)
    }

    override suspend fun insertAdd(item: DomainAddTable) {
        return addDao.insert(item.toAddRoomMap())
    }

    override suspend fun updateAdd(item: DomainAddTable) {
        return addDao.update(item.toAddRoomMap())
    }

    override suspend fun deleteAddById(id: Long) {
        return addDao.deleteAddById(id)
    }

    override fun getFastAddProduct(id: Long): Flow<List<DomainFastAddProduct>> {
        return addDao.getFastAddProduct(id).map { it -> it.map { it.toDomainFastAddProduct() } }
    }

    override fun getAnalysisAddAllTime(
        id: Long,
        name: String
    ): Flow<DomainCountSuffix?> {
        return addDao.getAnalysisAddAllTime(id, name).map { it?.toDomainCountSuffix() }
    }

    override fun getAnalysisAddAverageValueAllTime(
        id: Long,
        name: String
    ): Flow<DomainCountSuffix?> {
        return addDao.getAnalysisAddAverageValueAllTime(id, name).map { it?.toDomainCountSuffix() }
    }

   /* override fun getAnalysisAddAnimalAllTime(
        id: Long,
        name: String
    ): Flow<List<DomainAnimalCountSuffix>> {
        return addDao.getAnalysisAddAnimalAllTime(id, name)
            .map { it -> it.map { it.toDomainAnimalCountSuffix() } }
    }*/

    override fun getAnalysisAddRangeList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCountSuffixPriceDate>> {
        return addDao.getAnalysisAddRangeList(id, name, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainCountSuffixPriceDate() } }
    }

    override fun getAnalysisAddAverageValueAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix> {
        return addDao.getAnalysisAddAverageValueAllTimeRange(id, name, dateBegin, dateEnd)
            .map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisAddAnimalRangeList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainAnimalWitchCountAndImage>> {
        return addDao.getAnalysisAddAnimalRangeList(id, name, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainAnimalWitchCountAndImage() } }
    }

    override fun getProductAnimal(idAnimal: Long): Flow<List<DomainAnimalCountSuffix>> {
        return addDao.getProductAnimal(idAnimal)
            .map { it -> it.map { it.toDomainAnimalCountSuffix() } }
    }

    override fun getProductKillList(id: Long): Flow<List<DomainAddTable>> {
        return addDao.getProductKillList(id).map { it -> it.map { it.toAddDomainMap() } }
    }

    /* override fun getAnalysisAddProductNewYearProject(
         id: Long,
         dateBegin: String,
         dateEnd: String
     ): Flow<List<AnalysisSaleBuyerAllTime>> {
         return addDao.getAnalysisAddProductNewYearProject(id, dateBegin, dateEnd)
     }

     override fun getAnalysisAddProductNewYear(
         dateBegin: String,
         dateEnd: String
     ): Flow<List<AnalysisSaleBuyerAllTime>> {
         return addDao.getAnalysisAddProductNewYear(dateBegin, dateEnd)
     }*/
}