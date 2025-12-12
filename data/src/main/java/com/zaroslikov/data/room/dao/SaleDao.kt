package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.sale.BrieflySaleDto
import com.zaroslikov.data.room.dto.sale.BuyerPriceDto
import com.zaroslikov.data.room.dto.sale.CountSuffixPriceDto
import com.zaroslikov.data.room.dto.shared.CategoryPriceDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixCategoryDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixPriceDto
import com.zaroslikov.data.room.table.ferma.SaleTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query(
        "SELECT * from sale_table Where idPT=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC"
    )
    fun getAllSaleItems(id: Long): Flow<List<SaleTable>>

    @Query("SELECT * from sale_table Where _id=:id")
    fun getItemSale(id: Long): Flow<SaleTable>

    @Query("SELECT * from sale_table Where animal_count_id=:id")
    fun getItemSaleIdCountAnimal(id: Long): Flow<SaleTable>

    @Query(
        "SELECT title," +
                " SUM(count) as count," +
                " count_suffix as suffix," +
                " SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END) AS price," +
                " COUNT(*) AS row_count" +
                " FROM sale_table WHERE idPT=:id GROUP BY title ORDER BY price DESC"
    )
    fun getBrieflyItemSale(id: Long): Flow<List<BrieflySaleDto>>

    @Query("SELECT * from sale_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemSale(id: Long, name: String): Flow<List<SaleTable>>


    @Query(
        "SELECT title, count_suffix AS suffix, 0 AS category FROM add_table WHERE idPT=:id" +
                " UNION " +
                " SELECT title, count_suffix AS suffix, 1 AS catefory FROM expenses_table WHERE idPT=:id AND is_show_warehouse = 1 AND is_show_food != 1 GROUP BY title" +
                " UNION " +
                " SELECT title, count_suffix AS suffix, 2 AS catefory FROM sale_table WHERE idPT=:id" +
                " AND title NOT IN (SELECT title from add_table Where idPT=:id" +
                " UNION " +
                " SELECT title FROM expenses_table WHERE idPT=:id AND is_show_warehouse = 1 AND is_show_food != 1 GROUP BY title)"
    )
    fun getItemsTitleSaleList(id: Long): Flow<List<TitleSuffixCategoryDto>>


    @Query("SELECT category from sale_table Where idPT=:id group by category")
    fun getItemsCategorySaleList(id: Long): Flow<List<String>>

    @Query("SELECT buyer from sale_table Where idPT=:id AND buyer IS NOT NULL group by buyer")
    fun getItemsBuyerSaleList(id: Long): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertSale(item: SaleTable)

    @Update
    suspend fun updateSale(item: SaleTable)

    @Query("DELETE FROM sale_table WHERE _id = :id")
    suspend fun deleteSaleById(id: Long)

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM sale_table WHERE idPT =:id"
    )
    fun getIncome(id: Long): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM sale_table" +
                " WHERE idPT =:id and month =:month and year =:year"
    )
    fun getIncomeMountFin(id: Long, month: Int, year: Int): Flow<Double>

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM sale_table" +
                " WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getIncomeMount(id: Long, dateBegin: String, dateEnd: String): Flow<Double>

    @Query(
        "SELECT title," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 0 AS category" +
                " FROM sale_table" +
                " WHERE idPT =:id GROUP BY title ORDER BY price DESC"
    )
    fun getIncomeAllList(id: Long): Flow<List<TitleSuffixPriceDto>>

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM sale_table" +
                " WHERE idPT =:id GROUP BY category ORDER BY price DESC"
    )
    fun getIncomeCategoryAllList(id: Long): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT title," +
                " count_suffix as suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 0 AS category" +
                " FROM sale_table" +
                " WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title ORDER BY price DESC"
    )
    fun getProductListCategoryIncomeCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<TitleSuffixPriceDto>>

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM sale_table" +
                " WHERE idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY category ORDER BY price DESC"
    )
    fun getCategoryIncomeCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT title," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(count), 0) AS price," +
                " 0 AS category" +
                " from sale_table" +
                " Where idPT=:id and title=:name"
    )
    fun getAnalysisSaleAllTime(id: Long, name: String): Flow<TitleSuffixPriceDto>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS priceAll from sale_table" +
                " Where idPT=:id and title=:name"
    )
    fun getAnalysisSaleSoldAllTime(id: Long, name: String): Flow<Double>

    @Query(
        "SELECT buyer, COALESCE(SUM(PRICE),0) AS price," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix as suffix" +
                " FROM sale_table" +
                " Where idPT=:id  and title =:name " +
                " GROUP BY buyer ORDER BY price DESC"
    )
    fun getAnalysisSaleBuyerAllTime(id: Long, name: String): Flow<List<BuyerPriceDto>>

    @Query(
        "SELECT count," +
                " count_suffix as suffix," +
                " CASE WHEN price_all IS NULL THEN price ELSE price_all END AS price" +
                " FROM sale_table" +
                " Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisSaleRangeList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CountSuffixPriceDto>>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS priceAll from sale_table" +
                " Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisSaleSoldAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT" +
                " buyer," +
                " CASE WHEN price_all IS NULL THEN price ELSE price_all END AS price," +
                " count," +
                " count_suffix as suffix" +
                " FROM sale_table" +
                " Where idPT=:id  and title =:name AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisSaleBuyerRangeList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<BuyerPriceDto>>

    @Query(
        "SELECT COALESCE(SUM(PRICE), 0) AS priceAll from sale_table" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisSaleNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT buyer," +
                " COALESCE(SUM(PRICE),0) AS price," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix as suffix" +
                " FROM sale_table" +
                " Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY buyer ORDER BY count DESC Limit 3"
    )
    fun getAnalysisSaleBuyerNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<BuyerPriceDto>>

    @Query(
        "SELECT buyer," +
                " COALESCE(SUM(PRICE),0) AS price," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix AS suffix" +
                " FROM sale_table" +
                " Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title ORDER BY price DESC Limit 3"
    )
    fun getAnalysisSaleProductNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<BuyerPriceDto>>

    @Query(
        "SELECT COALESCE(SUM(PRICE), 0) AS priceAll" +
                " FROM sale_table" +
                " Where DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisSaleNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT buyer," +
                " COALESCE(SUM(PRICE),0) AS price," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix as suffix" +
                " FROM sale_table" +
                " Where DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY buyer ORDER BY count DESC Limit 3"
    )
    fun getAnalysisSaleBuyerNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<BuyerPriceDto>>

    @Query(
        "SELECT title As buyer," +
                " COALESCE(SUM(PRICE),0) AS price," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix AS suffix" +
                " FROM sale_table" +
                " Where DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title ORDER BY price DESC Limit 3"
    )
    fun getAnalysisSaleProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<BuyerPriceDto>>
}