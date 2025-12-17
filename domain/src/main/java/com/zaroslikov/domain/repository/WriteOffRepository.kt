package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import kotlinx.coroutines.flow.Flow

interface WriteOffRepository {
    fun getAllWriteOffItems(id: Long): Flow<List<DomainWriteOffTable>>
    fun getItemWriteOff(id: Long): Flow<DomainWriteOffTable>
    fun getItemWriteOffIdAnimalCount(id: Long): Flow<DomainWriteOffTable>
    fun getBrieflyItemWriteOff(id: Long): Flow<List<BrieflyWriteOffDomain>>
    fun getBrieflyDetailsItemWriteOff(id: Long, name: String): Flow<List<DomainWriteOffTable>>
    fun getBrieflyDetailsItemWriteOffOwnNeed(
        id: Long,
        name: String
    ): Flow<List<DomainWriteOffTable>>

    fun getBrieflyDetailsItemWriteOffScrap(id: Long, name: String): Flow<List<DomainWriteOffTable>>
    fun getItemsWriteOffList(id: Long): Flow<List<DomainTitleSuffixCategory>>
    suspend fun insertWriteOff(item: DomainWriteOffTable)
    suspend fun updateWriteOff(item: DomainWriteOffTable)
    suspend fun deleteWriteOff(id: Long)

    fun getOwnNeedAllList(id: Long): Flow<List<DomainTitleSuffixPrice>> //maybe
    fun getScrapAllList(id: Long): Flow<List<DomainTitleSuffixPrice>> //maybe
    fun getOwnNeedAllCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>>
    fun getScrapAllCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>>

    fun getOwnNeedProductPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainTitleSuffixPrice>>

    fun getScrapProductPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainTitleSuffixPrice>>

    fun getOwnNeedCategoryPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCategoryPrice>>

    fun getScrapCategoryPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCategoryPrice>>

    fun getOwnNeed(id: Long): Flow<Double>
    fun getScrap(id: Long): Flow<Double>
    fun getOwnNeedMonth(id: Long, dateBegin: String, dateEnd: String): Flow<Double>
    fun getScrapMonth(id: Long, dateBegin: String, dateEnd: String): Flow<Double>
    fun getAnalysisWriteOffAllTime(id: Long, name: String): Flow<DomainCountSuffix>
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Long, name: String): Flow<DomainCountSuffix>
    fun getAnalysisWriteOffScrapAllTime(id: Long, name: String): Flow<DomainCountSuffix>
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Long, name: String): Flow<Double>
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Long, name: String): Flow<Double>
    fun getAnalysisWriteOffAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix>

    fun getAnalysisOwnNeedsScrapRangeList(
        id: Long,
        name: String,
        status: Boolean,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCountSuffixPriceDate>>

    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix>

    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>
}