package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.zaroslikov.data.room.dto.crm.CrmBuyerProductDto
import com.zaroslikov.data.room.dto.crm.CrmBuyerStatsDto
import com.zaroslikov.data.room.dto.crm.CrmMonthRevenueDto
import com.zaroslikov.data.room.table.ferma.SaleTable
import kotlinx.coroutines.flow.Flow

/**
 * Агрегаты по sale_table для CRM. Сумма продажи — price_all, если он задан, иначе price.
 * Покупатель везде нормализуется через TRIM.
 */
@Dao
interface CrmSaleDao {

    @Query(
        "SELECT DISTINCT TRIM(buyer) AS buyer FROM sale_table" +
                " WHERE idPT=:idPT AND buyer IS NOT NULL AND TRIM(buyer) <> ''" +
                " ORDER BY TRIM(buyer)"
    )
    fun getBuyers(idPT: Long): Flow<List<String>>

    @Query(
        "SELECT TRIM(buyer) AS buyer," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS total," +
                " COUNT(*) AS orders," +
                " MIN(printf('%04d-%02d-%02d', year, month, day)) AS firstDate," +
                " MAX(printf('%04d-%02d-%02d', year, month, day)) AS lastDate" +
                " FROM sale_table" +
                " WHERE idPT=:idPT AND buyer IS NOT NULL AND TRIM(buyer) <> ''" +
                " GROUP BY TRIM(buyer) ORDER BY total DESC"
    )
    fun getBuyerStats(idPT: Long): Flow<List<CrmBuyerStatsDto>>

    @Query(
        "SELECT * FROM sale_table WHERE idPT=:idPT AND TRIM(buyer) = :buyer" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC, _id DESC"
    )
    fun getBuyerSales(idPT: Long, buyer: String): Flow<List<SaleTable>>

    @Query(
        "SELECT title," +
                " COALESCE(SUM(count), 0.0) AS count," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS total" +
                " FROM sale_table WHERE idPT=:idPT AND TRIM(buyer) = :buyer" +
                " GROUP BY title, count_suffix ORDER BY total DESC"
    )
    fun getBuyerProducts(idPT: Long, buyer: String): Flow<List<CrmBuyerProductDto>>

    @Query(
        "SELECT year, month," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS total," +
                " COUNT(*) AS orders" +
                " FROM sale_table WHERE idPT=:idPT" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY year, month ORDER BY year ASC, month ASC"
    )
    fun getMonthlyRevenue(idPT: Long, dateBegin: String, dateEnd: String): Flow<List<CrmMonthRevenueDto>>

    @Query(
        "SELECT year, month," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS total," +
                " COUNT(*) AS orders" +
                " FROM sale_table WHERE idPT=:idPT AND TRIM(buyer) = :buyer" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY year, month ORDER BY year ASC, month ASC"
    )
    fun getMonthlyRevenueByBuyer(
        idPT: Long,
        buyer: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CrmMonthRevenueDto>>

    @Query(
        "SELECT COUNT(*) FROM sale_table" +
                " WHERE idPT=:idPT AND (buyer IS NULL OR TRIM(buyer) = '')"
    )
    fun getSalesWithoutBuyerCount(idPT: Long): Flow<Int>
}
