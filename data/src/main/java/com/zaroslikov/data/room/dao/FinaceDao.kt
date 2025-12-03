package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.zaroslikov.data.room.dto.finance.IncomeExpensesDto
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query(
        "SELECT COALESCE(" +
                "(SELECT SUM(Case WHEN price_all IS NULL THEN price ELSE price_all END) FROM sale_table WHERE idPT =:id), 0) " +
                "- COALESCE(" +
                "         (SELECT SUM(Case WHEN price_all IS NULL THEN price ELSE price_all END) FROM expenses_table WHERE idPT=:id), 0) " +
                " AS PriceDifference;"
    )
    fun getCurrentBalance(id: Long): Flow<Double>

    @Query(
        "SELECT title, count, count_suffix as suffix, price, printf('%02d.%02d.%04d', day, month, year) as date, category " +
                " FROM (" +
                " SELECT title, count, count_suffix, Case WHEN price_all IS NULL THEN price ELSE price_all END as price, day, month, year, 0 AS category" +
                " FROM sale_table" +
                " WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT title,count, count_suffix, -(Case WHEN price_all IS NULL THEN price ELSE price_all END) AS minusPriceAll, day, month, year,  1 AS category" +
                " FROM expenses_table" +
                " WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) " +
                " combined_table ORDER BY date DESC"
    )
    fun getIncomeExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDto>>
}