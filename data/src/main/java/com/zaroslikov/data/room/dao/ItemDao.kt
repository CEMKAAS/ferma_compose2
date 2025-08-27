package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.Incubator
import com.zaroslikov.data.room.table.ferma.NoteTable
import com.zaroslikov.data.room.table.ferma.ProjectTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    //Finance
    @Query(
        "SELECT COALESCE((SELECT SUM(PRICE) FROM sale_table WHERE idPT =:id), 0) " +
                "- COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT=:id), 0) " +
                "- COALESCE((SELECT SUM(0) FROM animal_table WHERE idPT=:id), 0) AS PriceDifference;"
    )//0 = price
    fun getCurrentBalance(id: Int): Flow<Double> //Finance



    @Query(
        "SELECT COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT=:id), 0) " +
                "+ COALESCE((SELECT SUM(0) FROM animal_table WHERE idPT=:id), 0) AS ResultCount"
    ) //0 = price

    fun getExpenses(id: Int): Flow<Double> //Finance

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM write_off_table WHERE write_off_table.idPT =:id and status=0")
    fun getOwnNeed(id: Int): Flow<Double> //Write off Maybe

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM write_off_table WHERE write_off_table.idPT =:id and status=1")
    fun getScrap(id: Int): Flow<Double> // WriteOff Maybe



//    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount =:mount and year =:year")
//    fun getExpensesMountFin(id: Int, mount: Int, year: Int): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT =:id and month =:mount and year =:year), 0)" +
                "+ COALESCE((SELECT SUM(0) FROM animal_table WHERE idPT=:id and strftime('%Y-%m'," +
                "                substr(date, 7, 4) || '-' ||" +
                "                substr(date, 4, 2) || '-' ||" +
                "                substr(date, 1, 2)) =:yearMonth), 0) AS ResultCount"
    )//0 = price
    fun getExpensesMountFin(id: Int, mount: Int, year: Int, yearMonth: String): Flow<Double> // Expeneses maybe

    @Query(
        "SELECT COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0.0)" +
                " +  COALESCE((SELECT SUM(0) FROM animal_table WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d',  substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0.0) AS ResultCount "
    )//0 = price
    fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM write_off_table WHERE write_off_table.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and status=0")
    fun getOwnNeedMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM write_off_table WHERE write_off_table.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and status=1")
    fun getScrapMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double> //maybe


    @Query(
        "SELECT category as title, COALESCE(SUM(price), 0.0) AS priceAll FROM expenses_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by category " +
                " UNION All " +
                "SELECT ' Мои Животные ' as title, COALESCE(SUM(0), 0.0) AS priceAll FROM animal_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )//0 = price
//    ORDER BY countEXPENSES DESC
    fun getCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>> //maybe


//    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id and mount=:mount and year=:year")
//    fun getIncomeMount(id: Int, mount: Int, year: Int): Flow<Double>
//
//    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount=:mount and year=:year")
//    fun getExpensesMount(id: Int, mount: Int, year: Int): Flow<Double>
//
//    @Query("SELECT MyFermaSale.category as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year group by MyFermaSale.category ORDER BY MyFermaSale.PRICE DESC")
//    fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>
//
//    @Query("SELECT MyFermaEXPENSES.category as title, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year group by MyFermaEXPENSES.category ORDER BY MyFermaEXPENSES.countEXPENSES DESC")
//    fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>

    @Query(
        "SELECT title as title, count, count_suffix as suffix, price as priceAll, printf('%02d.%02d.%04d', day, month, year) as date " +
                "from (SELECT title, count, count_suffix, PRICE, day, month, year From sale_table" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT title,count, count_suffix, -price AS minusPriceAll, day, month as month, year from expenses_table" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT name, (Select Count From AnimalCountTable Where idAnimal = a.id ORDER BY count DESC LIMIT 1 ), 'Шт.', -0 AS minusPriceAll,substr(date, 1, 2) AS day,  substr(date, 4, 2) AS month,  substr(date, 7, 4) AS year from animal_table a " +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) " +
                " combined_table ORDER BY date DESC"
    )//0 = price
    fun getIncomeExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDetails>> //finance



    @Query("SELECT title as title, COALESCE(SUM(price), 0.0) AS priceAll FROM expenses_table WHERE idPT =:id group by title")
    fun getExpensesAllList(id: Int): Flow<List<Fin>> //maybe

    @Query("SELECT name as title, COALESCE(SUM(0), 0.0) AS priceAll FROM animal_table WHERE idPT =:id group by name")
    fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>>//0 = price  //maybe



    @Query(
        "SELECT category as title, COALESCE(SUM(price), 0.0) AS priceAll FROM expenses_table WHERE idPT =:id group by category" +
                " UNION ALL " +
                "SELECT 'Мои Животные' as title, COALESCE(SUM(0), 0.0) AS priceAll FROM animal_table WHERE idPT =:id"
    )//0 = price
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>  //maybe



    @Query("SELECT title as title, COALESCE(SUM(count), 0.0) AS priceAll FROM expenses_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and category=:category group by title ORDER BY count DESC")
    fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>> //maybe

    //0 = COALESCE(SUM(0.0), 0.0)
    @Query("SELECT name as title, 0.0 AS priceAll FROM animal_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by name")
    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>  //maybe


    @Query(
        "SELECT title, count_suffix AS suffix, " +
                "       SUM(AddCount) - COALESCE(SUM(SaleCount), 0) - COALESCE(SUM(WriteOffCount), 0) AS ResultCount" +
                " FROM (" +
                "    SELECT Title,count_suffix, SUM(count) AS AddCount, 0 AS SaleCount, 0 AS WriteOffCount" +
                "    FROM add_table" +
                "    WHERE idPT = :id" +
                "    GROUP BY Title" +
                "    UNION ALL" +
                "    SELECT title, count_suffix, 0 AS AddCount, SUM(count) AS SaleCount, 0 AS WriteOffCount" +
                "    FROM sale_table" +
                "    WHERE idPT = :id" +
                "    GROUP BY title" +
                "    UNION ALL" +
                "    SELECT title, count_suffix as suffix, 0 AS AddCount, 0 AS SaleCount, SUM(count) AS WriteOffCount" +
                "    FROM write_off_table" +
                "    WHERE idPT = :id" +
                "    GROUP BY title" +
                ")" +
                "  GROUP BY Title HAVING ResultCount > 0 ORDER BY ResultCount DESC "
    )
    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>


    @Query(
        "SELECT * From expenses_table Where idPT =:id and is_show_food = 1"
    )
    fun getCurrentFoodWarehouse(id: Int): Flow<List<ExpensesTable>>

    @Query(
        "SELECT title as Title, suffix, " +
                "      SUM(ExpensesCount) - COALESCE(SUM(WriteOffCount) , 0) - COALESCE(SUM(SaleCount), 0) AS ResultCount" +
                " FROM (" +
                "    SELECT title, count_suffix as suffix, SUM(count) AS ExpensesCount, 0 AS WriteOffCount, 0 AS SaleCount" +
                "    FROM expenses_table" +
                "    WHERE idPT = :id and is_show_warehouse = 1 and is_show_food != 1" +
                "    GROUP BY title" +
                "    UNION ALL" +
                "    SELECT title, count_suffix as suffix, 0 AS ExpensesCoun, SUM(count) AS WriteOffCount, 0 AS SaleCount" +
                "    FROM write_off_table" +
                "    WHERE idPT = :id" +
                "    GROUP BY title" +
                "    UNION ALL" +
                "    SELECT title, count_suffix, 0 AS ExpensesCoun, 0 AS WriteOffCount, SUM(count) AS SaleCount" +
                "    FROM sale_table" +
                "    WHERE idPT = :id" +
                "    GROUP BY title" +
                " ) " +
                " GROUP BY title HAVING ResultCount > 0 ORDER BY ResultCount DESC"
    )
    fun getCurrentExpensesWarehouse(id: Int): Flow<List<WarehouseData>>


//    @Query(
//        "SELECT (" +
//                "SELECT SUM(AddCount) - COALESCE(SUM(SaleCount), 0) - COALESCE(SUM(WriteOffCount), 0)" +
//                " FROM (" +
//                "    SELECT SUM(count) AS AddCount, 0 AS SaleCount, 0 AS WriteOffCount" +
//                "    FROM add_table" +
//                "    WHERE title = :name and idPT = :id" +
//                "    GROUP BY title" +
//                "    UNION ALL" +
//                "    SELECT 0 AS AddCount, SUM(discSale) AS SaleCount, 0 AS WriteOffCount" +
//                "    FROM MyFermaSale" +
//                "    WHERE titleSale = :name and idPT = :id" +
//                "    GROUP BY titleSale" +
//                "    UNION ALL" +
//                "    SELECT 0 AS AddCount, 0 AS SaleCount, SUM(count) AS WriteOffCount" +
//                "    FROM write_off_table" +
//                "    WHERE title = :name and idPT = :id" +
//                "    GROUP BY title )" +
//                " ) AS first," +
//                " (" +
//                "        SELECT count_suffix" +
//                "        FROM add_table" +
//                "        WHERE Title = :name AND idPT = :id" +
//                "        ORDER BY _id " +
//                "        LIMIT 1" +
//                "    ) AS second"
//    )
//    fun getCurrentBalanceProduct(name: String, id: Long): Flow<PairDataDoubleSting>


    @Query(
        "SELECT " +
                "    grouped.total_count AS first, " +
                "    grouped.suffix AS second " +
                "FROM (" +
                "    SELECT " +
                "        base.suffix, " +
                "        SUM(base.count) - " +
                "        COALESCE((SELECT SUM(s.count) FROM sale_table s WHERE s.title = base.title AND s.count_suffix = base.suffix AND s.idPT = :id), 0) - " +
                "        COALESCE((SELECT SUM(w.count) FROM write_off_table w WHERE w.title = base.title AND w.count_suffix = base.suffix AND w.idPT = :id), 0) " +
                "        AS total_count " +
                "    FROM (" +
                "        SELECT title, count, count_suffix AS suffix FROM add_table WHERE idPT = :id AND title = :name " +
                "        UNION ALL " +
                "        SELECT title, -count, count_suffix FROM sale_table WHERE idPT = :id AND title = :name " +
                "        UNION ALL " +
                "        SELECT title, -count, count_suffix as suffix FROM write_off_table WHERE idPT = :id AND title = :name " +
                "    ) AS base " +
                "    GROUP BY base.suffix " +
                "    HAVING total_count > 0 " +
                ") AS grouped"
    )
    fun getCurrentBalanceProduct(name: String, id: Long): Flow<PairDataDoubleSting>


    @Query(
        "SELECT " +
                "    base.suffix AS second, " +
                "    SUM(base.count) AS first " +
                "FROM (" +
                "    SELECT title, count, count_suffix AS suffix " +
                "    FROM add_table " +
                "    WHERE idPT = :id AND title = :name " +
                "    UNION ALL " +
                "    SELECT title, -count, count_suffix " +
                "    FROM sale_table " +
                "    WHERE idPT = :id AND title = :name " +
                "    UNION ALL " +
                "    SELECT title, -count, count_suffix as suffix " +
                "    FROM write_off_table " +
                "    WHERE idPT = :id AND title = :name " +
                ") AS base " +
                "GROUP BY base.suffix"
    )
    fun getCurrentBalanceProductList(name: String, id: Long): Flow<List<PairDataDoubleSting>>

//    @Query(
//        "SELECT SUM(ExpensesCount) - COALESCE(SUM(WriteOffCount), 0) - COALESCE(SUM(SaleCount), 0) AS ResultCount" +
//                " FROM (SELECT title,price, SUM(count) AS ExpensesCount, 0 AS WriteOffCount, 0 AS SaleCount" +
//                "    FROM expenses_table" +
//                "    WHERE title =:name and idPT = :id and is_show_warehouse = 1 and is_show_food != 1" +
//                "    GROUP BY title" +
//                "    UNION ALL" +
//                "    SELECT title, suffix, 0 AS ExpensesCoun, SUM(count) AS WriteOffCount, 0 AS SaleCount" +
//                "    FROM write_off_table" +
//                "    WHERE idPT = :id and title =:name" +
//                "    GROUP BY title" +
//                "    UNION ALL" +
//                "    SELECT title, count_suffix, 0 AS ExpensesCoun, 0 AS WriteOffCount, SUM(count) AS SaleCount" +
//                "    FROM sale_table" +
//                "    WHERE idPT = :id and title =:name" +
//                "    GROUP BY title" +
//                ")"
//    )
//    fun getCurrentExpensesProduct(name: String, id: Long): Flow<Double>

    @Query(
        "SELECT SUM(ExpensesCount) - COALESCE(SUM(WriteOffCount), 0) - COALESCE(SUM(SaleCount), 0) AS first, suffix AS second " +
                "FROM (" +
                "   SELECT title, count_suffix AS suffix, SUM(count) AS ExpensesCount, 0 AS WriteOffCount, 0 AS SaleCount " +
                "   FROM expenses_table " +
                "   WHERE title = :name AND idPT = :id AND (is_show_warehouse = 1 OR is_show_food != 1) " +
                "   GROUP BY title, count_suffix " +
                "   UNION ALL " +
                "   SELECT title AS title, count_suffix as suffix, 0 AS ExpensesCount, SUM(count) AS WriteOffCount, 0 AS SaleCount " +
                "   FROM write_off_table " +
                "   WHERE idPT = :id AND title = :name " +
                "   GROUP BY title, suffix " +
                "   UNION ALL " +
                "   SELECT title, count_suffix AS suffix, 0 AS ExpensesCount, 0 AS WriteOffCount, SUM(count) AS SaleCount " +
                "   FROM sale_table " +
                "   WHERE idPT = :id AND title = :name " +
                "   GROUP BY title, count_suffix" +
                ") " +
                "GROUP BY suffix"
    )
    fun getCurrentExpensesProductList(name: String, id: Long): Flow<List<PairDataDoubleSting>>

    @Query("SELECT title, count as disc, count_suffix AS suffix, category, animal_id as idAnimal, title as animal, Count(*) as count from add_table Where idPT=:id GROUP BY title, count, idAnimal, category order by count desc limit 5")
    fun getFastAddProduct(id: Long): Flow<List<FastAdd>> //todo title заменить на animal

    // Analysis
    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from add_table Where idPT=:id and title=:name")
    fun getAnalysisAddAllTime(id: Int, name: String): Flow<Fin>



    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from write_off_table Where idPT=:id and title=:name")
    fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=0")
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=1")
    fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin>



    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=0")
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=1")
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT count_suffix as title, CASE WHEN COALESCE(SUM(count), 0) = 0 THEN 0 ELSE COALESCE(SUM(count), 0) / 365 END AS priceAll from add_table Where idPT=:id and title=:name")
    fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT title as title, COALESCE(SUM(count),0) AS priceAll, count_suffix AS suffix from add_table Where idPT=:id and title=:name GROUP BY title ORDER BY priceAll DESC")
    fun getAnalysisAddAnimalAllTime(
        id: Int,
        name: String
    ): Flow<List<AnimalTitSuff>> //todo title заменить на animal



    @Query(
        "SELECT at.name As title, COALESCE(SUM((e.price*ea.percentExpenses)/100)/(SUM(a.title)),0) AS priceAll from add_table a" +
                " left Join animal_table at On at.name = a.title" +
                " left Join ExpensesAnimalTable ea On ea.idAnimal = at.id" +
                " left Join expenses_table e on ea.idExpenses = e._id" +
                " Where a.title =:name and a.idPT=:id and at.name IS NOT NULL GROUP BY a.title ORDER BY priceAll DESC"
    )
    fun getAnalysisCostPriceAllTime(
        id: Int,
        name: String
    ): Flow<List<Fin>> //todo title заменить на animal

    //AnalisisRange
    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from add_table Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisAddAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>



    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from write_off_table Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=0 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=1 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>



    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=0 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table Where idPT=:id and title=:name and status=1 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT count_suffix as title, CASE WHEN COALESCE(SUM(count), 0) = 0 THEN 0 ELSE COALESCE(SUM(count), 0) / 365 END AS priceAll from add_table Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisAddAverageValueAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT title as title, COALESCE(SUM(count),0) AS priceAll, count_suffix AS suffix from add_table Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY priceAll DESC ")
    fun getAnalysisAddAnimalAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnimalTitSuff>>//todo title заменить на animal



    @Query(
        "SELECT at.name As title, COALESCE(SUM((e.price*ea.percentExpenses)/100)/(SUM(a.count)),0) AS priceAll, '₽' AS suffix from add_table a" +
                " left Join animal_table at On at.name = a.title" +
                " left Join ExpensesAnimalTable ea On ea.idAnimal = at.id" +
                " left Join expenses_table e on ea.idExpenses = e._id" +
                " Where a.idPT=:id  and a.title =:name AND DATE(printf('%04d-%02d-%02d', a.year, a.month, a.day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY a.title ORDER BY priceAll DESC"
    )
    fun getAnalysisCostPriceAllTimeRange(
        id: Int, name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>//todo title заменить на animal

    @Query(
        "SELECT title,COALESCE(SUM(count), 0.0) AS priceAll, count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE title=:name" +
                " GROUP BY Title ORDER BY priceAll DESC"
    )
    fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>> //todo title заменить на animal

    // NewYear Project


    @Query(
        "SELECT COALESCE((SELECT SUM(price) FROM expenses_table" +
                " WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) +" +
                " COALESCE((SELECT SUM(0) FROM animal_table WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) " +
                "AS PriceDifference"
    )//0 = price
    fun getAnalysisExpensesNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table" +
                " WHERE idPT=:id and status=0 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table" +
                " WHERE idPT=:id and status=1 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffScrapNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT  COALESCE(SUM(t.count), 0) as countAnimal" +
                " FROM animal_table a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where a.idPT=:id and DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisCountAnimalNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>



    @Query("SELECT title As buyer, COALESCE(SUM(count),0) AS resultPrice, 0 AS resultCount, count_suffix AS suffix from add_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisAddProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>



    @Query(
        "SELECT title As buyer, COALESCE(SUM(price), 0) AS resultPrice,  COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix" +
                " from expenses_table Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultPrice DESC Limit 3"
    )
    fun getAnalysisExpensesProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>


    // NewYear


    @Query(
        "SELECT COALESCE((SELECT SUM(price) FROM expenses_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) +" +
                " COALESCE((SELECT SUM(0) FROM animal_table WHERE DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) " +
                "AS PriceDifference"
    )//0 = price
    fun getAnalysisExpensesNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table Where status=0 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from write_off_table Where status=1 AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>



    @Query("SELECT title As buyer, COALESCE(SUM(count),0) AS resultPrice, 0 AS resultCount, count_suffix AS suffix from add_table Where DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisAddProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>



    @Query("SELECT title As buyer, COALESCE(SUM(price), 0) AS resultPrice,  COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from expenses_table Where DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisExpensesProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query(
        "SELECT  COALESCE(SUM(t.count), 0) as countAnimal" +
                " from animal_table a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisCountAnimalNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COUNT(*) from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COALESCE(SUM(EGGALL), 0) AS resultPrice from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COALESCE(SUM(EGGALLEND), 0) AS resultPrice from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getChikenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT count(DISTINCT TYPE) from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String>

    @Query(
        "SELECT name as title, COALESCE((SELECT SUM(PRICE) FROM sale_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT ), 0) +" +
                " COALESCE((SELECT SUM(price) FROM write_off_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0) -" +
                " COALESCE((SELECT SUM(price) FROM expenses_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY idPT), 0) - " +
                " COALESCE((SELECT SUM(0) FROM animal_table WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0) - " +
                " COALESCE((SELECT SUM(price) FROM write_off_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0)" +
                " AS priceAll FROM МyINCUBATOR Where mode = 1 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY name "
    )//0 = price
    fun getBestProjectNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

//    @Query("SELECT i.name as title, COALESCE(SUM(pd1.price), 0) - COALESCE(SUM(pd_sub.price), 0) AS priceAll " +
//            " FROM МyINCUBATOR i" +
//            " LEFT JOIN" +
//            " (SELECT idPT, SUM(PRICE) as price FROM MyFermaSale WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//            " UNION ALL" +
//            " SELECT idPT, SUM(priceAll) as price  FROM write_off_table WHERE status = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) pd1" +
//            " ON i._id = pd1.idPT" +
//            " LEFT JOIN" +
//            " (SELECT idPT, SUM(countEXPENSES) as price  FROM MyFermaEXPENSES WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            " UNION ALL" +
//            " SELECT idPT, SUM(price) as price  FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            " UNION ALL" +
//            " SELECT idPT, SUM(priceAll) as price  FROM write_off_table WHERE status = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            ") pd_sub ON i._id = pd_sub.idPT" +
//            " Where i.mode = 1 and DATE(printf('%04d-%02d-%02d', substr(i.data, 7, 4), substr(i.data, 4, 2), substr(i.data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY i._id,i.NAME ORDER BY priceAll DESC LIMIT 1 ")
//    fun getBestProjectNewYear(
//        dateBegin: String,
//        dateEnd: String
//    ): Flow<Fin>

//    @Query(
//        "SELECT idPT, COALESCE(SUM(SaleCount), 0) + COALESCE(SUM(WriteOffCount0), 0) - COALESCE(SUM(ExpensesCount), 0) - COALESCE(SUM(WriteOffCount0), 0) - COALESCE(SUM(AnimalCount), 0)  AS ResultCount" +
//                " FROM (" +
//                "    SELECT idPT, SUM(PRICE) AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount0, 0 AS WriteOffCount1,  0 AS AnimalCount" +
//                "    FROM MyFermaSale s" +
//                "    Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT" +
//                "    Join  +
//                "    SELECT  idPT, 0 AS SaleCount, SUM(countEXPENSES) AS ExpensesCount, 0 AS WriteOffCount0, 0 AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM MyFermaEXPENSES e" +
//                "    Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT" +
//                "    UNION ALL" +
//                "    SELECT  idPT, 0 AS SaleCount, 0 AS ExpensesCount, SUM(priceAll) AS WriteOffCount0, 0 AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM write_off_table w0" +
//                "    where status = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT " +
//                "    UNION ALL" +
//                "    SELECT  idPT,  0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, SUM(priceAll) AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM write_off_table w1" +
//                "    where status = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//                "    GROUP BY idPT " +
//                "    UNION ALL" +
//                "    SELECT  idPT, 0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, 0 AS WriteOffCount1, SUM(price) AS AnimalCount" +
//                "    FROM AnimalTable a" +
//                "    where DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//                "    GROUP BY idPT " +
//                ")" +
//                " ORDER BY ResultCount DESC ")
//    fun getBestProjectNewYear(
//        dateBegin: String,
//        dateEnd: String
//    ): Flow<Fin>


}