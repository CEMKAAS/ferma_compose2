package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.add.AnimalCountSuffixDto
import com.zaroslikov.data.room.dto.add.BrieflyAddDto
import com.zaroslikov.data.room.dto.add.FastAddProductDto
import com.zaroslikov.data.room.dto.add.TitleAndSuffixDto
import com.zaroslikov.data.room.dto.shared.CountSuffixDto
import com.zaroslikov.data.room.table.ferma.AddTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AddDao {
    @Query("SELECT * FROM add_table WHERE _id = :id")
    fun getItem(id: Long): Flow<AddTable>

    @Query(
        "SELECT * FROM add_table" +
                " WHERE idPT=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " DESC, _id DESC"
    )
    fun getAllItems(id: Long): Flow<List<AddTable>>

    @Query("SELECT * FROM add_table WHERE _id=:id")
    fun getItemAdd(id: Long): Flow<AddTable>

    @Query(
        "SELECT title," +
                " SUM(count) as count," +
                " count_suffix AS suffix" +
                " FROM add_table " +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY count DESC"
    )
    fun getBrieflyItemAdd(id: Long): Flow<List<BrieflyAddDto>>

    @Query(
        "SELECT * FROM add_table" +
                " WHERE idPT=:id AND title =:name" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC"
    )
    fun getBrieflyDetailsItemAdd(id: Long, name: String): Flow<List<AddTable>>

    @Query(
        "SELECT title," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY MAX(_id) DESC"
    )
    fun getItemsTitleAddList(id: Long): Flow<List<TitleAndSuffixDto>>

    @Query(
        "SELECT category FROM add_table" +
                " WHERE idPT=:id" +
                " GROUP BY category "
    )
    fun getItemsCategoryAddList(id: Long): Flow<List<String>>

    @Query("SELECT name FROM animal_table WHERE id=:id")
    fun getAnimalById(id: Long): Flow<String>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(item: AddTable)

    @Update
    suspend fun update(item: AddTable)

    @Query("DELETE FROM add_table WHERE _id = :id")
    suspend fun deleteAddById(id: Long)

    @Query(
        "SELECT title," +
                " count," +
                " count_suffix AS suffix," +
                " category," +
                " animal_id AS idAnimal," +
                " (SELECT name FROM animal_table WHERE id = animal_id) as animalName" +
                " FROM add_table" +
                " WHERE idPT=:id" +
                " GROUP BY title, count, idAnimal, category" +
                " ORDER BY Count(*) DESC LIMIT 5"
    )
    fun getFastAddProduct(id: Long): Flow<List<FastAddProductDto>>

    @Query(
        "SELECT SUM(count) AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id AND title=:name" +
                " GROUP BY count_suffix" +
                " HAVING SUM(count) IS NOT NULL"
    )
    fun getAnalysisAddAllTime(id: Long, name: String): Flow<CountSuffixDto?>

    @Query(
        "SELECT" +
                " CASE" +
                " WHEN COALESCE(SUM(count), 0) = 0" +
                " THEN 0 ELSE COALESCE(SUM(count), 0) / 365 END AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id AND title=:name"
    )
    fun getAnalysisAddAverageValueAllTime(id: Long, name: String): Flow<CountSuffixDto?>

    @Query(
        "SELECT" +
                " (SELECT name FROM animal_table WHERE id = animal_id) as animalName," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id AND title=:name" +
                " GROUP BY title" +
                " ORDER BY count DESC"
    )
    fun getAnalysisAddAnimalAllTime(
        id: Long,
        name: String
    ): Flow<List<AnimalCountSuffixDto>>

    @Query(
        "SELECT COALESCE(SUM(count), 0) AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id AND title=:name" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisAddAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<CountSuffixDto>

    @Query(
        "SELECT" +
                " CASE" +
                "     WHEN COALESCE(SUM(count), 0) = 0" +
                "     THEN 0 ELSE COALESCE(SUM(count), 0) / 365 END AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id AND title=:name" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisAddAverageValueAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<CountSuffixDto>

    @Query(
        "SELECT" +
                " (SELECT name FROM animal_table WHERE id = animal_id) as animalName," +
                " COALESCE(SUM(count),0) AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id AND title=:name" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title" +
                " ORDER BY count DESC "
    )
    fun getAnalysisAddAnimalAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnimalCountSuffixDto>>

    @Query(
        "SELECT" +
                " (SELECT name FROM animal_table WHERE id = animal_id) as animalName," +
                " COALESCE(SUM(count), 0.0) AS count," +
                " count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE title=:name" +
                " GROUP BY title" +
                " ORDER BY count DESC"
    )
    fun getProductAnimal(name: String): Flow<List<AnimalCountSuffixDto>>


    @Query("SELECT * FROM add_table WHERE animal_count_id =:id")
    fun getProductKillList(id: Long): Flow<List<AddTable>>

    /*    @Query(
            "SELECT title AS buyer," +
                    " COALESCE(SUM(count),0) AS resultPrice," +
                    " 0 AS resultCount," +
                    " count_suffix AS suffix" +
                    " FROM add_table" +
                    " WHERE idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                    " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                    " GROUP BY title" +
                    " ORDER BY resultPrice DESC Limit 3"
        )
        fun getAnalysisAddProductNewYearProject(
            id: Long,
            dateBegin: String,
            dateEnd: String
        ): Flow<List<AnalysisSaleBuyerAllTime>> //TODO Buyer -> Title

        @Query(
            "SELECT title AS buyer," +
                    " COALESCE(SUM(count),0) AS resultPrice," +
                    " 0 AS resultCount," +
                    " count_suffix AS suffix" +
                    " FROM add_table" +
                    " WHERE DATE(printf('%04d-%02d-%02d', year, month, day))" +
                    " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                    " GROUP BY title" +
                    " ORDER BY resultPrice DESC Limit 3"
        )
        fun getAnalysisAddProductNewYear(
            dateBegin: String,
            dateEnd: String
        ): Flow<List<AnalysisSaleBuyerAllTime>> //TODO Buyer -> Title*/
}