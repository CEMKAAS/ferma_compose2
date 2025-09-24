package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.zaroslikov.data.room.dto.finance.IncomeExpensesDto
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query(
        "SELECT COALESCE((SELECT SUM(PRICE) FROM sale_table WHERE idPT =:id), 0) " +
                "- COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT=:id), 0) " +
                " AS PriceDifference;"
    )//0 = price
    fun getCurrentBalance(id: Long): Flow<Double>

    @Query(
        "SELECT title, count, count_suffix as suffix, price, printf('%02d.%02d.%04d', day, month, year) as date " +
                "from (SELECT title, count, count_suffix, price, day, month, year From sale_table" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT title,count, count_suffix, -price AS minusPriceAll, day, month, year from expenses_table" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT name, (Select Count From animal_count_table Where animal_id = a.id ORDER BY count DESC LIMIT 1 ), 'Шт.', -0 AS minusPriceAll,substr(date, 1, 2) AS day,  substr(date, 4, 2) AS month,  substr(date, 7, 4) AS year from animal_table a " +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) " +
                " combined_table ORDER BY date DESC"
    )//0 = price
    fun getIncomeExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDto>>
}