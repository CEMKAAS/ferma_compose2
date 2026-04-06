package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalWeightDao {

    @Query("SELECT * from animal_weight_table")
    fun getAllAnimalWeightTableForExport(): Flow<List<AnimalWeightTable>>


    @Upsert
    suspend fun insertAllAnimalWeightTable(data: List<AnimalWeightTable>)

    @Query("DELETE FROM animal_weight_table")
    suspend fun deleteAllAnimalWeightTable()

    @Transaction
    suspend fun clearAndInsertAnimalWeightTableForImport(animalWeightTable: List<AnimalWeightTable>) {
        deleteAllAnimalWeightTable()
        insertAllAnimalWeightTable(animalWeightTable)
    }

    @Insert
    suspend fun insertAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Update
    suspend fun updateAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Delete
    suspend fun deleteAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Query("DELETE FROM animal_weight_table WHERE id = :id")
    suspend fun deleteAnimalWeightTableById(id: Long)

    @Query(
        "SELECT * FROM animal_weight_table" +
                " WHERE animal_id=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getWeightAnimalLimit(id: Long): Flow<AnimalWeightTable?>

    @Query(
        "SELECT * FROM animal_weight_table" +
                " WHERE animal_id=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getWeightAnimal(id: Long): Flow<List<AnimalWeightTable>>
}