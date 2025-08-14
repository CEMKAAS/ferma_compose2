package com.zaroslikov.fermacompose2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.water.BrieflyItemCount
import com.zaroslikov.fermacompose2.data.water.BrieflyItemPrice
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.PairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.PairDataStringInt
import com.zaroslikov.fermacompose2.supportFun.SaleTitleData
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.AnimalExpensesList2
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTime
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.warehouse.FastAdd
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * from МyINCUBATOR ORDER BY ARHIVE ASC")
    fun getAllProject(): Flow<List<ProjectTable>>

    @Query("SELECT * from МyINCUBATOR Where _id=:id")
    fun getProject(id: Int): Flow<ProjectTable>

    @Query("SELECT * from MyIncubator Where idPT =:idPT")
    suspend fun getIncubatorListArh4(idPT: Int): List<Incubator>

    @Query("SELECT * from МyINCUBATOR Where TYPE =:type and mode = 0 and ARHIVE = 1")
    suspend fun getIncubatorListArh6(type: String): List<ProjectTable>

    @Query("SELECT * from МyINCUBATOR Where mode = 1 and ARHIVE = 0")
    fun getProjectListAct(): Flow<List<ProjectTable>>

    @Query("SELECT COUNT(*) AS row_count from МyINCUBATOR Where mode = 0")
    fun getCountRowIncubator(): Flow<Int>

    @Query("SELECT COUNT(*) AS row_count from МyINCUBATOR Where mode = 1")
    fun getCountRowProject(): Flow<Int>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(projectTable: ProjectTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProjectLong(projectTable: ProjectTable): Long

    @Update
    suspend fun updateProject(item: ProjectTable)

    @Delete
    suspend fun deleteProject(item: ProjectTable)

    @Query("SELECT _id from МyINCUBATOR ORDER BY _id DESC Limit 1")
    fun getLastProject(): Flow<Int>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubatorList(id: Int): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    suspend fun getIncubatorList2(id: Int): List<Incubator>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubator(id: Int): Flow<Incubator>

    @Query("SELECT * from MyIncubator Where idPT=:id and day=:day")
    fun getIncubatorEditDay(id: Int, day: Int): Flow<Incubator>


    //==================== Add ====================
    @Query("SELECT * from add_table WHERE _id = :id")
    fun getItem(id: Int): Flow<AddTable>

    @Query("SELECT * from add_table Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC, _id DESC")
    fun getAllItems(id: Int): Flow<List<AddTable>>

    @Query("SELECT * from add_table Where _id=:id")
    fun getItemAdd(id: Int): Flow<AddTable>

    @Query("SELECT title as title, SUM(count) as count, count_suffix AS suffix from add_table Where idPT=:id group by title ORDER BY count DESC")
    fun getBrieflyItemAdd(id: Int): Flow<List<BrieflyItemCount>>

    @Query("SELECT * from add_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemAdd(id: Long, name: String): Flow<List<AddTable>>

    @Query(
        "SELECT title AS first, count_suffix AS second" +
                " FROM add_table" +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY MAX(_id) DESC"
    )
    fun getItemsTitleAddList(id: Int): Flow<List<PairData>>


    @Query("SELECT category from add_table Where idPT=:id group by category ")
    fun getItemsCategoryAddList(id: Int): Flow<List<String>>

    @Query("SELECT id as first, name as second, type as third from AnimalTable Where idPT=:id")
    fun getItemsAnimalAddList(id: Int): Flow<List<TripleData>>

    @Query("SELECT name FROM AnimalTable WHERE id=:id")
    fun getAnimalById(id: Long): Flow<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AddTable)

    @Update
    suspend fun update(item: AddTable)

    @Query("DELETE FROM add_table WHERE _id = :id")
    suspend fun deleteAddById(id: Long)

    //==================== Sale ====================
    @Query("SELECT * from sale_table Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC")
    fun getAllSaleItems(id: Int): Flow<List<SaleTable>>

    @Query("SELECT * from sale_table Where _id=:id")
    fun getItemSale(id: Int): Flow<SaleTable>

    @Query("SELECT * from sale_table Where animal_count_id=:id")
    fun getItemSaleIdCountAnimal(id: Int): Flow<SaleTable>

    @Query("SELECT title as title, SUM(count) as count, SUM(price) as price, count_suffix as suffix from sale_table Where idPT=:id group by title ORDER BY price DESC")
    fun getBrieflyItemSale(id: Int): Flow<List<BrieflyItemPrice>>

    @Query("SELECT * from sale_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemSale(id: Long, name: String): Flow<List<SaleTable>>

//   @Query(
//        "SELECT title as name, 'Моя Продукция' as type from " +
//                "(SELECT MyFerma.Title from MyFerma Where idPT=:id" +
//                " UNION All " +
//                "SELECT MyFermaSale.titleSale From MyFermaSale Where idPT=:id " +
//                " UNION ALL" +
//                " SELECT titleEXPENSES from MyFermaEXPENSES Where idPT=:id and showWarehouse = 1 and showFood != 1 group by titleEXPENSES "+
//                ") combined_table group by name"
//    )
//    fun getItemsTitleSaleList(id: Int): Flow<List<PairString>>

    @Query(
        "SELECT title AS first, count_suffix AS second, 0 AS third FROM add_table WHERE idPT=:id" +
                " UNION " +
                " SELECT title AS first, count_suffix AS second, 1 AS third FROM expenses_table WHERE idPT=:id AND is_show_warehouse = 1 AND is_show_food != 1 GROUP BY title" +
                " UNION " +
                " SELECT title AS first, count_suffix AS second, 2 AS third FROM sale_table WHERE idPT=:id AND first" +
                " NOT IN (SELECT title from add_table Where idPT=:id" +
                " UNION " +
                " SELECT title FROM expenses_table WHERE idPT=:id AND is_show_warehouse = 1 AND is_show_food != 1 GROUP BY title)"
    )
    fun getItemsTitleSaleList(id: Int): Flow<List<SaleTitleData>>


    @Query("SELECT category from sale_table Where idPT=:id group by category")
    fun getItemsCategorySaleList(id: Int): Flow<List<String>>

    @Query("SELECT buyer from sale_table Where idPT=:id group by buyer")
    fun getItemsBuyerSaleList(id: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSale(item: SaleTable)

    @Update
    suspend fun updateSale(item: SaleTable)

    @Query("DELETE FROM sale_table WHERE _id = :id")
    suspend fun deleteSaleById(id: Long)

    //==================== Expenses ====================
    @Query("SELECT * from expenses_table Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC")
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>

    @Query("SELECT * from expenses_table Where _id=:id")
    fun getItemExpenses(id: Int): Flow<ExpensesTable>

    @Query("SELECT * from expenses_table Where animal_count_id=:id")
    fun getItemExpensesIdAnimalCount(id: Int): Flow<ExpensesTable>

    @Query("SELECT * from expenses_table Where animal_vaccination_id=:id")
    fun getItemExpensesForVaccination(id: Long): Flow<ExpensesTable>

    @Query(
        "SELECT title, SUM(count) AS count," +
                " SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END) AS price," +
                " count_suffix AS suffix" +
                " FROM expenses_table" +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY price DESC"
    )
    fun getBrieflyItemExpenses(id: Int): Flow<List<BrieflyItemPrice>>

    @Query("SELECT * from expenses_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemExpenses(id: Long, name: String): Flow<List<ExpensesTable>>

    @Query("SELECT idAnimal from ExpensesAnimalTable Where idExpenses=:id")
    suspend fun getItemExpensesAnimal(id: Int): List<Long>

    @Query(
        "SELECT title as first, count_suffix as second FROM expenses_table" +
                " WHERE idPT=:id AND animalId IS NULL AND animal_vaccination_id IS NULL AND animal_count_id IS NULL " +
                " GROUP BY title" +
                " ORDER BY MAX(_id) DESC"
    )
    fun getItemsTitleExpensesList(id: Int): Flow<List<PairData>>

    @Query("SELECT category FROM expenses_table WHERE idPT=:id GROUP BY category")
    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>


    @Query(
        "SELECT a.id, a.name as name, a.foodDay as foodDay, a.suffix_food_day as foodDaySuffix, t.count as countAnimal," +
                " case when e._id NOT NULL Then e._id  else 0 end as idExpensesAnimal," +
                " case when e.idAnimal NOT NULL  Then 1 else 0 end as ps," +
                " case when  e.percentExpenses NOT NULL Then e.percentExpenses else 0 end as presentException " +
                " from AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Left Join ExpensesAnimalTable e On e.idAnimal = a.id and e.idExpenses =:idExpenses Where a.idPT=:id ORDER By ps Desc"
    )
    suspend fun getItemsAnimalExpensesList2(id: Int, idExpenses: Long): List<AnimalExpensesList2>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenses(item: ExpensesTable): Long

    @Update
    suspend fun updateExpenses(item: ExpensesTable)

    @Delete
    suspend fun deleteExpenses(item: ExpensesTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpensesAnimal(item: ExpensesAnimalTable)

    @Update
    suspend fun updateExpensesAnimal(item: ExpensesAnimalTable)

    @Delete
    suspend fun deleteExpensesAnimal(item: ExpensesAnimalTable)

    //WriteOff
    @Query("SELECT * from MyFermaWRITEOFF Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, mount, day) ) DESC, _id DESC")
    fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>>

    @Query("SELECT * from MyFermaWRITEOFF Where _id=:id ")
    fun getItemWriteOff(id: Int): Flow<WriteOffTable>

    @Query("SELECT * from MyFermaWRITEOFF Where animal_count_id=:id ")
    fun getItemWriteOffIdAnimalCount(id: Int): Flow<WriteOffTable>

    @Query("SELECT titleWRITEOFF as title, SUM(discWRITEOFF) as count, suffix from MyFermaWRITEOFF Where idPT=:id group by title ORDER BY count DESC")
    fun getBrieflyItemWriteOff(id: Int): Flow<List<BrieflyItemCount>>

    @Query("SELECT * from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, mount, day)) DESC")
    fun getBrieflyDetailsItemWriteOff(id: Long, name: String): Flow<List<WriteOffTable>>

    @Query(
        "SELECT Title as first, 'Моя Продукция' as second from add_table Where idPT=:id group by title" +
                " UNION ALL" +
                " SELECT title as first, 'Купленый товар' as second from expenses_table Where idPT=:id and is_show_warehouse = 1 group by title"
    )
    fun getItemsWriteoffList(id: Int): Flow<List<PairData>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWriteOff(item: WriteOffTable)

    @Update
    suspend fun updateWriteOff(item: WriteOffTable)

    @Delete
    suspend fun deleteWriteOff(item: WriteOffTable)


    //Finance
    @Query(
        "SELECT COALESCE((SELECT SUM(PRICE) FROM sale_table WHERE idPT =:id), 0) " +
                "- COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT=:id), 0) " +
                "- COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id), 0) AS PriceDifference;"
    )
    fun getCurrentBalance(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM sale_table WHERE idPT =:id")
    fun getIncome(id: Int): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT=:id), 0) " +
                "+ COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id), 0) AS ResultCount"
    )
    fun getExpenses(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id and statusWRITEOFF=0")
    fun getOwnNeed(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id and statusWRITEOFF=1")
    fun getScrap(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM sale_table WHERE idPT =:id and month =:month and year =:year")
    fun getIncomeMountFin(id: Int, month: Int, year: Int): Flow<Double>

//    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount =:mount and year =:year")
//    fun getExpensesMountFin(id: Int, mount: Int, year: Int): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT =:id and month =:mount and year =:year), 0)" +
                "+ COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id and strftime('%Y-%m'," +
                "                substr(data, 7, 4) || '-' ||" +
                "                substr(data, 4, 2) || '-' ||" +
                "                substr(data, 1, 2)) =:yearMonth), 0) AS ResultCount"
    )
    fun getExpensesMountFin(id: Int, mount: Int, year: Int, yearMonth: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount FROM sale_table WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getIncomeMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(count) FROM expenses_table WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0.0)" +
                " +  COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d',  substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0.0) AS ResultCount "
    )
    fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and statusWRITEOFF=0")
    fun getOwnNeedMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and statusWRITEOFF=1")
    fun getScrapMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query("SELECT category as title, COALESCE(SUM(price), 0.0) AS priceAll FROM sale_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by category ORDER BY price DESC")
    fun getCategoryIncomeCurrentMonth(id: Int, dateBegin: String, dateEnd: String): Flow<List<Fin>>

    @Query(
        "SELECT category as title, COALESCE(SUM(price), 0.0) AS priceAll FROM expenses_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by category " +
                " UNION All " +
                "SELECT ' Мои Животные ' as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable Where idPT=:id AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
//    ORDER BY countEXPENSES DESC
    fun getCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>


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
                " SELECT name, (Select Count From AnimalCountTable Where idAnimal = a.id ORDER BY count DESC LIMIT 1 ), 'Шт.', -price AS minusPriceAll,substr(data, 1, 2) AS day,  substr(data, 4, 2) AS month,  substr(data, 7, 4) AS year from AnimalTable a " +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) " +
                " combined_table ORDER BY date DESC"
    )
    fun getIncomeExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDetails>>

    @Query("SELECT title as title, COALESCE(SUM(price), 0.0) AS priceAll FROM sale_table WHERE idPT =:id group by title")
    fun getIncomeAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT title as title, COALESCE(SUM(price), 0.0) AS priceAll FROM expenses_table WHERE idPT =:id group by title")
    fun getExpensesAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT name as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable WHERE idPT =:id group by name")
    fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT category as title, COALESCE(SUM(price), 0.0) AS priceAll FROM sale_table WHERE idPT =:id group by category")
    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>

    @Query(
        "SELECT category as title, COALESCE(SUM(price), 0.0) AS priceAll FROM expenses_table WHERE idPT =:id group by category" +
                " UNION ALL " +
                "SELECT 'Мои Животные' as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable WHERE idPT =:id"
    )
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT title as title, COALESCE(SUM(price), 0.0) AS priceAll FROM sale_table Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and category=:category group by title ORDER BY price DESC")
    fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    @Query("SELECT title as title, COALESCE(SUM(count), 0.0) AS priceAll FROM expenses_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and category=:category group by title ORDER BY count DESC")
    fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    @Query("SELECT name as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable Where idPT=:id AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by name ORDER BY price DESC")
    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>


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
                "    SELECT titleWRITEOFF, suffix, 0 AS AddCount, 0 AS SaleCount, SUM(discWRITEOFF) AS WriteOffCount" +
                "    FROM MyFermaWRITEOFF" +
                "    WHERE idPT = :id" +
                "    GROUP BY titleWRITEOFF" +
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
                "    SELECT titleWRITEOFF, suffix, 0 AS ExpensesCoun, SUM(discWRITEOFF) AS WriteOffCount, 0 AS SaleCount" +
                "    FROM MyFermaWRITEOFF" +
                "    WHERE idPT = :id" +
                "    GROUP BY titleWRITEOFF" +
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
//                "    SELECT 0 AS AddCount, 0 AS SaleCount, SUM(discWRITEOFF) AS WriteOffCount" +
//                "    FROM MyFermaWRITEOFF" +
//                "    WHERE titleWRITEOFF = :name and idPT = :id" +
//                "    GROUP BY titleWRITEOFF )" +
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
                "        COALESCE((SELECT SUM(w.discWRITEOFF) FROM MyFermaWRITEOFF w WHERE w.titleWRITEOFF = base.title AND w.suffix = base.suffix AND w.idPT = :id), 0) " +
                "        AS total_count " +
                "    FROM (" +
                "        SELECT title, count, count_suffix AS suffix FROM add_table WHERE idPT = :id AND title = :name " +
                "        UNION ALL " +
                "        SELECT title, -count, count_suffix FROM sale_table WHERE idPT = :id AND title = :name " +
                "        UNION ALL " +
                "        SELECT titleWRITEOFF, -discWRITEOFF, suffix FROM MyFermaWRITEOFF WHERE idPT = :id AND titleWRITEOFF = :name " +
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
                "    SELECT titleWRITEOFF, -discWRITEOFF, suffix " +
                "    FROM MyFermaWRITEOFF " +
                "    WHERE idPT = :id AND titleWRITEOFF = :name " +
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
//                "    SELECT titleWRITEOFF, suffix, 0 AS ExpensesCoun, SUM(discWRITEOFF) AS WriteOffCount, 0 AS SaleCount" +
//                "    FROM MyFermaWRITEOFF" +
//                "    WHERE idPT = :id and titleWRITEOFF =:name" +
//                "    GROUP BY titleWRITEOFF" +
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
                "   WHERE title = :name AND idPT = :id AND is_show_warehouse = 1 AND is_show_food != 1 " +
                "   GROUP BY title, count_suffix " +
                "   UNION ALL " +
                "   SELECT titleWRITEOFF AS title, suffix, 0 AS ExpensesCount, SUM(discWRITEOFF) AS WriteOffCount, 0 AS SaleCount " +
                "   FROM MyFermaWRITEOFF " +
                "   WHERE idPT = :id AND titleWRITEOFF = :name " +
                "   GROUP BY titleWRITEOFF, suffix " +
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

    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from sale_table Where idPT=:id and title=:name")
    fun getAnalysisSaleAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name")
    fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0")
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1")
    fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from sale_table Where idPT=:id and title=:name")
    fun getAnalysisSaleSoldAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0")
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1")
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT count_suffix as title, CASE WHEN COALESCE(SUM(count), 0) = 0 THEN 0 ELSE COALESCE(SUM(count), 0) / 365 END AS priceAll from add_table Where idPT=:id and title=:name")
    fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT title as title, COALESCE(SUM(count),0) AS priceAll, count_suffix AS suffix from add_table Where idPT=:id and title=:name GROUP BY title ORDER BY priceAll DESC")
    fun getAnalysisAddAnimalAllTime(
        id: Int,
        name: String
    ): Flow<List<AnimalTitSuff>> //todo title заменить на animal

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from sale_table Where idPT=:id  and title =:name  GROUP BY buyer ORDER BY ResultPrice DESC")
    fun getAnalysisSaleBuyerAllTime(id: Int, name: String): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query(
        "SELECT at.name As title, COALESCE(SUM((e.price*ea.percentExpenses)/100)/(SUM(a.title)),0) AS priceAll from add_table a" +
                " left Join AnimalTable at On at.name = a.title" +
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

    @Query("SELECT count_suffix as title, COALESCE(SUM(count), 0) AS priceAll from sale_table Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from sale_table Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleSoldAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
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

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from sale_table Where idPT=:id  and title =:name AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY ResultPrice DESC")
    fun getAnalysisSaleBuyerAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query(
        "SELECT at.name As title, COALESCE(SUM((e.price*ea.percentExpenses)/100)/(SUM(a.count)),0) AS priceAll, '₽' AS suffix from add_table a" +
                " left Join AnimalTable at On at.name = a.title" +
                " left Join ExpensesAnimalTable ea On ea.idAnimal = at.id" +
                " left Join expenses_table e on ea.idExpenses = e._id" +
                " Where a.idPT=:id  and a.title =:name AND DATE(printf('%04d-%02d-%02d', a.year, a.month, a.day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY a.title ORDER BY priceAll DESC"
    )
    fun getAnalysisCostPriceAllTimeRange(
        id: Int, name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>//todo title заменить на animal


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncubator(item: Incubator)

    @Update
    suspend fun updateIncubator(item: Incubator)

    //Animal
    @Query("SELECT * from AnimalTable Where idPT=:id and arhiv = 0 ORDER BY id DESC")
    fun getAllAnimal(id: Int): Flow<List<AnimalTable>>

    @Query("SELECT * from AnimalTable Where id=:id")
    fun getAnimal(id: Int): Flow<AnimalTable>

    @Query(
        "SELECT " +
                "AnimalTable.id, AnimalTable.name, AnimalTable.type, AnimalTable.data, AnimalTable.date_factory, " +
                "AnimalTable.groop, AnimalTable.sex, AnimalTable.note, AnimalTable.image, AnimalTable.arhiv, " +
                "(SELECT IFNULL(SUM(price), 0) " +
                "FROM expenses_table " +
                "WHERE animalId = AnimalTable.id ) AS price, " +
                "AnimalTable.foodDay, AnimalTable.suffix_food_day, AnimalTable.idPT " +
                "FROM AnimalTable " +
                "WHERE AnimalTable.id = :id"
    )
    fun getAnimalCard(id: Int): Flow<AnimalTable>

    @Query("SELECT type from AnimalTable Where idPT=:id GROUP BY type")
    fun getTypeAnimal(id: Long): Flow<List<String>>

    @Insert
    suspend fun insertAnimalTable(animalTable: AnimalTable): Long

    @Update
    suspend fun updateAnimalTable(animalTable: AnimalTable)

    @Delete
    suspend fun deleteAnimalTable(animalTable: AnimalTable)


    //==================== Animal Indicators ====================
    //==================== Insert ====================
    @Insert
    suspend fun insertAnimalCountTable(animalCountTable: AnimalCountTable): Long

    @Insert
    suspend fun insertAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Insert
    suspend fun insertAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Insert
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable): Long

    //==================== Update ====================
    @Update
    suspend fun updateAnimalCountTable(animalCountTable: AnimalCountTable)

    @Update
    suspend fun updateAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Update
    suspend fun updateAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Update
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    //==================== Delete ====================
    @Delete
    suspend fun deleteAnimalCountTable(animalCountTable: AnimalCountTable)

    @Delete
    suspend fun deleteAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Delete
    suspend fun deleteAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Delete
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)


    //==================== get One Indicators ====================
    @Query(
        """
    WITH FirstRow AS (
        SELECT *
        FROM AnimalCountTable
        WHERE idAnimal = :id
        ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC
        LIMIT 1
    ),
    Calculation AS (
        SELECT
            CASE
                WHEN (SELECT version FROM FirstRow) IS NULL THEN
                    (SELECT count FROM FirstRow)
                ELSE
                    (SELECT SUM(
                        CASE
                            WHEN version IN (1, 4) THEN count
                            WHEN version IN (0, 2, 3) THEN -count
                            ELSE 0
                        END
                    ) FROM AnimalCountTable WHERE idAnimal = :id)
        END AS calculatedCount
    )
    SELECT 
        id,
        (SELECT calculatedCount FROM Calculation) AS count,
        suffix,
        date,
        idAnimal,
        note,
        version
    FROM FirstRow
"""
    )
    fun getCountAnimalLimit(id: Int): Flow<AnimalCountTable>

    @Query(
        "SELECT * FROM AnimalSizeTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getSizeAnimalLimit(id: Int): Flow<AnimalSizeTable>

    @Query(
        "SELECT * FROM AnimalVaccinationTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationAnimalLimit(id: Int): Flow<AnimalVaccinationTable>

    @Query(
        "SELECT * FROM AnimalWeightTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getWeightAnimalLimit(id: Int): Flow<AnimalWeightTable>

    //==================== get List Indicators ====================
    @Query(
        "SELECT id, count as weight, suffix, date, idAnimal, note, version," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT PRICE FROM sale_table WHERE animal_count_id = id)" +
                "        WHEN version = 1 THEN (SELECT price FROM expenses_table WHERE animal_count_id = id)" +
                "        WHEN version IN (2, 3) THEN (SELECT priceAll FROM myfermawriteoff WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS price," +
                " CASE" +
                "        WHEN version = 0 THEN (SELECT buyer FROM sale_table WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS buyer," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT _id FROM sale_table WHERE animal_count_id = id)" +
                "        WHEN version = 1 THEN (SELECT _id FROM expenses_table WHERE animal_count_id = id)" +
                "        WHEN version IN (2, 3) THEN (SELECT _id FROM myfermawriteoff WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS _id," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT idPT FROM sale_table WHERE animal_count_id = id)" +
                "        WHEN version = 1 THEN (SELECT idPT FROM expenses_table WHERE animal_count_id = id)" +
                "        WHEN version IN (2, 3) THEN (SELECT idPT FROM myfermawriteoff WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS idPT" +
                " FROM AnimalCountTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getCountAnimal(id: Int): Flow<List<DomainIndicatorsVM>>

    @Query(
        "SELECT id, size as weight, suffix, date, idAnimal, note" +
                " FROM AnimalSizeTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getSizeAnimal(id: Int): Flow<List<DomainIndicatorsVM>>

    @Query(
        "SELECT * FROM AnimalVaccinationTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationAnimal(id: Int): Flow<List<AnimalVaccinationTable>>

    @Query(
        "SELECT  id, weight, suffix, date, idAnimal, note" +
                " FROM AnimalWeightTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getWeightAnimal(id: Int): Flow<List<DomainIndicatorsVM>>

    @Query(
        "SELECT title,COALESCE(SUM(count), 0.0) AS priceAll, count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE title=:name" +
                " GROUP BY Title ORDER BY priceAll DESC"
    )
    fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>> //todo title заменить на animal


    //==================== Note ====================
    @Query(
        "SELECT * from NoteFerma" +
                " Where idPT=:id" +
                " ORDER BY strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) DESC"
    )
    fun getAllNote(id: Int): Flow<List<NoteTable>>

    @Query(
        "SELECT * from NoteFerma" +
                " Where _id=:id"
    )
    fun getNote(id: Int): Flow<NoteTable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(item: NoteTable)

    @Update
    suspend fun updateNote(item: NoteTable)

    @Delete
    suspend fun deleteNote(item: NoteTable)

    // NewYear Project
    @Query(
        "SELECT COALESCE(SUM(PRICE), 0) AS priceAll from sale_table" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisSaleNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(price) FROM expenses_table" +
                " WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) +" +
                " COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) " +
                "AS PriceDifference"
    )
    fun getAnalysisExpensesNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF" +
                " WHERE idPT=:id and statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF" +
                " WHERE idPT=:id and statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffScrapNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT  COALESCE(SUM(t.count), 0) as countAnimal" +
                " FROM AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where a.idPT=:id and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisCountAnimalNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from sale_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultCount DESC Limit 3")
    fun getAnalysisSaleBuyerNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT title As buyer, COALESCE(SUM(count),0) AS resultPrice, 0 AS resultCount, count_suffix AS suffix from add_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisAddProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT title As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from sale_table Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisSaleProductNewYearProject(
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
    @Query("SELECT COALESCE(SUM(PRICE), 0) AS priceAll from sale_table Where DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(price) FROM expenses_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) +" +
                " COALESCE((SELECT SUM(price) FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) " +
                "AS PriceDifference"
    )
    fun getAnalysisExpensesNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from sale_table Where DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultCount DESC Limit 3")
    fun getAnalysisSaleBuyerNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT title As buyer, COALESCE(SUM(count),0) AS resultPrice, 0 AS resultCount, count_suffix AS suffix from add_table Where DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisAddProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT title As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix from sale_table Where DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisSaleProductNewYear(
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
                " from AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
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
                " COALESCE((SELECT SUM(priceAll) FROM MyFermaWRITEOFF WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0) -" +
                " COALESCE((SELECT SUM(price) FROM expenses_table WHERE DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY idPT), 0) - " +
                " COALESCE((SELECT SUM(price) FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0) - " +
                " COALESCE((SELECT SUM(priceAll) FROM MyFermaWRITEOFF WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0)" +
                " AS priceAll FROM МyINCUBATOR Where mode = 1 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY name "
    )
    fun getBestProjectNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

//    @Query("SELECT i.name as title, COALESCE(SUM(pd1.price), 0) - COALESCE(SUM(pd_sub.price), 0) AS priceAll " +
//            " FROM МyINCUBATOR i" +
//            " LEFT JOIN" +
//            " (SELECT idPT, SUM(PRICE) as price FROM MyFermaSale WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//            " UNION ALL" +
//            " SELECT idPT, SUM(priceAll) as price  FROM MyFermaWRITEOFF WHERE statusWRITEOFF = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) pd1" +
//            " ON i._id = pd1.idPT" +
//            " LEFT JOIN" +
//            " (SELECT idPT, SUM(countEXPENSES) as price  FROM MyFermaEXPENSES WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            " UNION ALL" +
//            " SELECT idPT, SUM(price) as price  FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            " UNION ALL" +
//            " SELECT idPT, SUM(priceAll) as price  FROM MyFermaWRITEOFF WHERE statusWRITEOFF = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
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
//                "    FROM MyFermaWRITEOFF w0" +
//                "    where statusWRITEOFF = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT " +
//                "    UNION ALL" +
//                "    SELECT  idPT,  0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, SUM(priceAll) AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM MyFermaWRITEOFF w1" +
//                "    where statusWRITEOFF = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
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
