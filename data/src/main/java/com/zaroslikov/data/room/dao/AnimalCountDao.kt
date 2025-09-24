package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.animal.AnimalCountPriceDto
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalCountDao {
    @Insert
    suspend fun insertAnimalCountTable(animalCountTable: AnimalCountTable): Long

    @Update
    suspend fun updateAnimalCountTable(animalCountTable: AnimalCountTable)

    @Delete
    suspend fun deleteAnimalCountTable(animalCountTable: AnimalCountTable)

    @Query(
        """
    WITH FirstRow AS (
        SELECT *
        FROM animal_count_table
        WHERE animal_id = :id
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
                    ) FROM animal_count_table WHERE animal_id = :id)
        END AS calculatedCount
    )
    SELECT 
        id,
        (SELECT calculatedCount FROM Calculation) AS count,
        suffix,
        date,
        animal_id,
        note,
        version
    FROM FirstRow
"""
    )
    fun getCountAnimalLimit(id: Long): Flow<AnimalCountTable>

    @Query(
        "SELECT id, count, suffix, date, animal_id, note, version," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT PRICE FROM sale_table WHERE animal_count_id = id)" +
                "        WHEN version = 1 THEN (SELECT price FROM expenses_table WHERE animal_count_id = id)" +
                "        WHEN version IN (2, 3) THEN (SELECT price FROM write_off_table WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS price," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT buyer FROM sale_table WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS buyer," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT _id FROM sale_table WHERE animal_count_id = id)" +
                "        WHEN version = 1 THEN (SELECT _id FROM expenses_table WHERE animal_count_id = id)" +
                "        WHEN version IN (2, 3) THEN (SELECT _id FROM write_off_table WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS table_id," +
                "  CASE" +
                "        WHEN version = 0 THEN (SELECT idPT FROM sale_table WHERE animal_count_id = id)" +
                "        WHEN version = 1 THEN (SELECT idPT FROM expenses_table WHERE animal_count_id = id)" +
                "        WHEN version IN (2, 3) THEN (SELECT idPT FROM write_off_table WHERE animal_count_id = id)" +
                "        ELSE NULL" +
                "    END AS idPT" +
                " FROM animal_count_table" +
                " WHERE animal_id=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getCountAnimal(id: Long): Flow<List<AnimalCountPriceDto>>

}