package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.zaroslikov.data.room.dto.shared.CountSuffixDto
import com.zaroslikov.data.room.dto.shared.TitleCountSuffixDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseDao {
    @Query(
        "SELECT title, count, suffix " +
                " FROM (  " +
                "      SELECT title, count , suffix" +
                " FROM ( " +
                " SELECT title, count as count, count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id" +

                " UNION All " +
                " SELECT title, -count as count, count_suffix AS suffix " +
                " FROM sale_table" +
                " WHERE idPT=:id  and animal_count_id IS NULL and animal_id IS NULL AND product_origin = 0" +

                " UNION All" +
                " SELECT title, -count as count, count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id and animal_count_id IS NULL AND product_origin = 0" +
                ") " +
                ")"
    )
    fun getCurrentBalanceWarehouse(id: Long): Flow<List<TitleCountSuffixDto>>


    @Query(
        "SELECT title, count, suffix " +
                " FROM (  " +
                "      SELECT title, count , suffix" +
                " FROM ( " +
                " SELECT title, count as count, count_suffix AS suffix" +
                " FROM expenses_table" +
                " WHERE idPT=:id and is_food = 0 and animalId IS NULL and animal_vaccination_id IS NULL and animal_count_id IS NULL" +

                " UNION All " +
                " SELECT title, -count as count, count_suffix AS suffix " +
                " FROM sale_table" +
                " WHERE idPT=:id and animal_count_id IS NULL and animal_id IS NULL AND product_origin = 1" +

                " UNION All" +
                " SELECT title, -count as count, count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id and animal_count_id IS NULL AND product_origin = 1" +
                ") " +
                ")"
    )
    fun getCurrentExpensesWarehouse(id: Long): Flow<List<TitleCountSuffixDto>>

    @Query(
        "SELECT " +
                " base.count AS count," +
                " base.suffix AS suffix " +
                "FROM (" +
                "    SELECT count AS count, count_suffix AS suffix " +
                "    FROM add_table " +
                "    WHERE idPT = :id AND title = :name " +

                "    UNION ALL " +

                "    SELECT -count AS count, count_suffix as suffix " +
                "    FROM sale_table " +
                "    WHERE idPT = :id AND title = :name and animal_count_id IS NULL and animal_id IS NULL AND product_origin = 0 " +

                "    UNION ALL " +

                "    SELECT -count AS count, count_suffix as suffix " +
                "    FROM write_off_table " +
                "    WHERE idPT = :id AND title = :name  and animal_count_id IS NULL AND product_origin = 0" +
                ") AS base "
    )
    fun getCurrentBalanceProductList(name: String, id: Long): Flow<List<CountSuffixDto>>

    @Query(
        "SELECT " +
                " base.count AS count," +
                " base.suffix AS suffix " +
                "FROM (" +
                "    SELECT count AS count, count_suffix AS suffix " +
                "    FROM expenses_table " +
                "    WHERE idPT = :id AND title = :name AND is_food = 0 AND animalId IS NULL and animal_vaccination_id IS NULL AND animal_count_id IS NULL" +

                "    UNION ALL " +

                "    SELECT -count AS count, count_suffix AS suffix " +
                "    FROM sale_table " +
                "    WHERE idPT = :id AND title = :name AND animal_count_id IS NULL AND animal_id IS NULL AND product_origin = 1" +

                "    UNION ALL " +

                "    SELECT  -count AS count, count_suffix AS suffix " +
                "    FROM write_off_table " +
                "    WHERE idPT = :id AND title = :name AND animal_count_id IS NULL AND product_origin= 1" +
                ") AS base "
    )
    fun getCurrentExpensesProductList(name: String, id: Long): Flow<List<CountSuffixDto>>

    /*
        @Query(
            "SELECT at.name As count, COALESCE(SUM((e.price*ea.percentExpenses)/100)/(SUM(a.title)),0) AS suffix from add_table a" +
                    " left Join animal_table at On at.name = a.title" +
                    " left Join ExpensesAnimalTable ea On ea.idAnimal = at.id" +
                    " left Join expenses_table e on ea.idExpenses = e._id" +
                    " Where a.title =:name and a.idPT=:id and at.name IS NOT NULL GROUP BY a.title ORDER BY count DESC"
        )
        fun getAnalysisCostPriceAllTime(
            id: Int,
            name: String
        ): Flow<List<CountSuffixDto>> //todo title заменить на animal

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


        // NewYear Project


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
    */

    /*    Лишнее если не проголится, то можно удалить

        @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount =:mount and year =:year")
        fun getExpensesMountFin(id: Int, mount: Int, year: Int): Flow<Double>

        @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id and mount=:mount and year=:year")
        fun getIncomeMount(id: Int, mount: Int, year: Int): Flow<Double>

        @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount=:mount and year=:year")
        fun getExpensesMount(id: Int, mount: Int, year: Int): Flow<Double>

        @Query("SELECT MyFermaSale.category as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year group by MyFermaSale.category ORDER BY MyFermaSale.PRICE DESC")
        fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>

        @Query("SELECT MyFermaEXPENSES.category as title, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year group by MyFermaEXPENSES.category ORDER BY MyFermaEXPENSES.countEXPENSES DESC")
        fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>




        @Query(
            "SELECT (" +
                    "SELECT SUM(AddCount) - COALESCE(SUM(SaleCount), 0) - COALESCE(SUM(WriteOffCount), 0)" +
                    " FROM (" +
                    "    SELECT SUM(count) AS AddCount, 0 AS SaleCount, 0 AS WriteOffCount" +
                    "    FROM add_table" +
                    "    WHERE title = :name and idPT = :id" +
                    "    GROUP BY title" +
                    "    UNION ALL" +
                    "    SELECT 0 AS AddCount, SUM(discSale) AS SaleCount, 0 AS WriteOffCount" +
                    "    FROM MyFermaSale" +
                    "    WHERE titleSale = :name and idPT = :id" +
                    "    GROUP BY titleSale" +
                    "    UNION ALL" +
                    "    SELECT 0 AS AddCount, 0 AS SaleCount, SUM(count) AS WriteOffCount" +
                    "    FROM write_off_table" +
                    "    WHERE title = :name and idPT = :id" +
                    "    GROUP BY title )" +
                    " ) AS first," +
                    " (" +
                    "        SELECT count_suffix" +
                    "        FROM add_table" +
                    "        WHERE Title = :name AND idPT = :id" +
                    "        ORDER BY _id " +
                    "        LIMIT 1" +
                    "    ) AS second"
        )
        fun getCurrentBalanceProduct(name: String, id: Long): Flow<PairDataDoubleSting>




        @Query(
            "SELECT SUM(ExpensesCount) - COALESCE(SUM(WriteOffCount), 0) - COALESCE(SUM(SaleCount), 0) AS ResultCount" +
                    " FROM (SELECT title,price, SUM(count) AS ExpensesCount, 0 AS WriteOffCount, 0 AS SaleCount" +
                    "    FROM expenses_table" +
                    "    WHERE title =:name and idPT = :id and is_show_warehouse = 1 and is_show_food != 1" +
                    "    GROUP BY title" +
                    "    UNION ALL" +
                    "    SELECT title, suffix, 0 AS ExpensesCoun, SUM(count) AS WriteOffCount, 0 AS SaleCount" +
                    "    FROM write_off_table" +
                    "    WHERE idPT = :id and title =:name" +
                    "    GROUP BY title" +
                    "    UNION ALL" +
                    "    SELECT title, count_suffix, 0 AS ExpensesCoun, 0 AS WriteOffCount, SUM(count) AS SaleCount" +
                    "    FROM sale_table" +
                    "    WHERE idPT = :id and title =:name" +
                    "    GROUP BY title" +
                    ")"
        )
        fun getCurrentExpensesProduct(name: String, id: Long): Flow<Double>




        @Query("SELECT i.name as title, COALESCE(SUM(pd1.price), 0) - COALESCE(SUM(pd_sub.price), 0) AS priceAll " +
                " FROM МyINCUBATOR i" +
                " LEFT JOIN" +
                " (SELECT idPT, SUM(PRICE) as price FROM MyFermaSale WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION ALL" +
                " SELECT idPT, SUM(priceAll) as price  FROM write_off_table WHERE status = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) pd1" +
                " ON i._id = pd1.idPT" +
                " LEFT JOIN" +
                " (SELECT idPT, SUM(countEXPENSES) as price  FROM MyFermaEXPENSES WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " UNION ALL" +
                " SELECT idPT, SUM(price) as price  FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " UNION ALL" +
                " SELECT idPT, SUM(priceAll) as price  FROM write_off_table WHERE status = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                ") pd_sub ON i._id = pd_sub.idPT" +
                " Where i.mode = 1 and DATE(printf('%04d-%02d-%02d', substr(i.data, 7, 4), substr(i.data, 4, 2), substr(i.data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY i._id,i.NAME ORDER BY priceAll DESC LIMIT 1 ")
        fun getBestProjectNewYear(
            dateBegin: String,
            dateEnd: String
        ): Flow<Fin>

        @Query(
            "SELECT idPT, COALESCE(SUM(SaleCount), 0) + COALESCE(SUM(WriteOffCount0), 0) - COALESCE(SUM(ExpensesCount), 0) - COALESCE(SUM(WriteOffCount0), 0) - COALESCE(SUM(AnimalCount), 0)  AS ResultCount" +
                    " FROM (" +
                    "    SELECT idPT, SUM(PRICE) AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount0, 0 AS WriteOffCount1,  0 AS AnimalCount" +
                    "    FROM MyFermaSale s" +
                    "    Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                    "    GROUP BY idPT" +
                    "    Join  +
                    "    SELECT  idPT, 0 AS SaleCount, SUM(countEXPENSES) AS ExpensesCount, 0 AS WriteOffCount0, 0 AS WriteOffCount1, 0 AS AnimalCount" +
                    "    FROM MyFermaEXPENSES e" +
                    "    Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                    "    GROUP BY idPT" +
                    "    UNION ALL" +
                    "    SELECT  idPT, 0 AS SaleCount, 0 AS ExpensesCount, SUM(priceAll) AS WriteOffCount0, 0 AS WriteOffCount1, 0 AS AnimalCount" +
                    "    FROM write_off_table w0" +
                    "    where status = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                    "    GROUP BY idPT " +
                    "    UNION ALL" +
                    "    SELECT  idPT,  0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, SUM(priceAll) AS WriteOffCount1, 0 AS AnimalCount" +
                    "    FROM write_off_table w1" +
                    "    where status = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                    "    GROUP BY idPT " +
                    "    UNION ALL" +
                    "    SELECT  idPT, 0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, 0 AS WriteOffCount1, SUM(price) AS AnimalCount" +
                    "    FROM AnimalTable a" +
                    "    where DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                    "    GROUP BY idPT " +
                    ")" +
                    " ORDER BY ResultCount DESC ")
        fun getBestProjectNewYear(
            dateBegin: String,
            dateEnd: String
        ): Flow<Fin>*/
}