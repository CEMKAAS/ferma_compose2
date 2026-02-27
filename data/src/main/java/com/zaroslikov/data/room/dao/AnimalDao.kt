package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.AnimalWithCountDto
import com.zaroslikov.data.room.dto.animal.AnimalForAddDto
import com.zaroslikov.data.room.table.animal.AnimalTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query(
        "SELECT an.id, " +
                "an.name, " +
                "an.type, " +
                "an.date, " +
                "an.date_factory as dateFactory, " +
                "an.`group`, " +
                "an.sex, " +
                "ac.count AS count, " +
                "ac.suffix AS suffix " +
                "FROM animal_table an " +
                "LEFT JOIN ANIMAL_COUNT_TABLE ac ON ac.id = ( " +
                "   SELECT MAX(id) " +
                "   FROM ANIMAL_COUNT_TABLE " +
                "   WHERE animal_id = an.id " +
                ") " +
                "WHERE an.idPT = :id AND an.archive = 0 " +
                "ORDER BY an.id DESC"
    )
    fun getAllAnimal(id: Long): Flow<List<AnimalWithCountDto>>


    @Query("SELECT * FROM animal_table WHERE id=:id")
    fun getAnimal(id: Long): Flow<AnimalTable>

    @Query("SELECT type FROM animal_table WHERE idPT=:id GROUP BY type")
    fun getTypeAnimal(id: Long): Flow<List<String>>

    @Query("SELECT id, name, type from animal_table Where idPT=:id")
    fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDto>>

    @Insert
    suspend fun insertAnimalTable(animalTable: AnimalTable): Long

    @Update
    suspend fun updateAnimalTable(animalTable: AnimalTable)

    @Query("DELETE FROM animal_table WHERE id =:id")
    suspend fun deleteAnimalTable(id: Long)

    /*@Query("SELECT name as title," +
            " COALESCE(SUM(0), 0.0) AS priceAll" +
            " FROM animal_table" +
            " WHERE idPT =:id" +
            " GROUP BY name")
    fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>>//0 = price  //maybe

    //0 = COALESCE(SUM(0.0), 0.0)
    @Query("SELECT name as title," +
            " 0.0 AS priceAll" +
            " FROM animal_table" +
            " Where idPT=:id" +
            " AND DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2)))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
            " GROUP BY name")
    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>  //maybe*/


}