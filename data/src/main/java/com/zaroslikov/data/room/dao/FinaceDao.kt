package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.zaroslikov.data.room.dto.finance.IncomeExpensesDto
import com.zaroslikov.data.room.dto.finance.TransactionDto
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

    @Query(
        "SELECT count, count_suffix AS suffix, price, price_all, category, buyer, animal, date, category_finance " +
                " FROM (  " +
                "      SELECT count, count_suffix, price, price_all, category, buyer, animal, DATE(printf('%04d-%02d-%02d', year, month, day)) AS date, category_finance" +
                " FROM ( " +
                " SELECT count, count_suffix, price, price_all, category, buyer, NULL AS animal, day, month, year, 0 AS category_finance" +
                " FROM sale_table" +
                " WHERE idPT=:id AND title=:name AND product_origin = 0" +

                " UNION All " +
                " SELECT count, count_suffix, NULL AS price, NULL AS price_all, category, NULL AS buyer, (SELECT a.name From animal_table a where a.id = animal_id) as animal, day, month, year, 5 AS category_finance" +
                " FROM add_table" +
                " WHERE idPT=:id  and title=:name " +

                " UNION All" +
                " SELECT count, count_suffix, price, price_all, category, NULL AS buyer, NULL AS animal, day, month, year, 3 AS category_finance" +
                " FROM write_off_table" +
                " WHERE idPT=:id  and title=:name  and status = 0 AND product_origin = 0" +

                " UNION All" +
                " SELECT count, count_suffix, price, price_all, category, NULL AS buyer, NULL AS animal, day, month, year, 2 AS category_finance" +
                " FROM write_off_table" +
                " WHERE idPT=:id and title=:name and status = 1 AND product_origin = 0" +
                ") " +
                " WHERE DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                ")" +
                " ORDER BY date DESC"
    )
    fun getAnalysisTransactionList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<TransactionDto>>

}