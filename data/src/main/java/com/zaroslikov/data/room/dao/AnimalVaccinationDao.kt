package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.animal.AnimalVaccinationExpensesDto
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalVaccinationDao {
    @Query("SELECT * from animal_vaccination_table")
    fun getAllAnimalVaccinationTableForExport(): Flow<List<AnimalVaccinationTable>>

    @Upsert
    suspend fun insertAllAnimalVaccinationTable(data: List<AnimalVaccinationTable>)

    @Query("DELETE FROM animal_vaccination_table")
    suspend fun deleteAllAnimalVaccinationTable()

    @Transaction
    suspend fun clearAndInsertAnimalVaccinationForImport(animalVaccinationTable: List<AnimalVaccinationTable>) {
        deleteAllAnimalVaccinationTable()
        insertAllAnimalVaccinationTable(animalVaccinationTable)
    }

    @Insert
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable): Long

    @Update
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Delete
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Query(
        "DELETE FROM animal_vaccination_table WHERE id = :id"
    )
    suspend fun deleteAnimalVaccinationTableById(id: Long)

    @Query(
        "SELECT * FROM animal_vaccination_table" +
                " WHERE animal_id=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationAnimalLimit(id: Long): Flow<AnimalVaccinationTable?>


    @Query(
        "SELECT * FROM animal_vaccination_table" +
                " WHERE animal_id=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationAnimal(id: Long): Flow<List<AnimalVaccinationTable>>

    @Query(
        "SELECT v.id, v.vaccination, v.count_vaccination, v.date, v.next_vaccination, v.note, v.animal_id," +
                " e.price," +
                " e.price_all " +
                " FROM animal_vaccination_table AS v" +
                " LEFT JOIN expenses_table AS e ON e.animal_vaccination_id = v.id " +
                " WHERE animal_id=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationExpensesAnimal(id: Long): Flow<List<AnimalVaccinationExpensesDto>>

    @Query(
        "SELECT vaccination FROM animal_vaccination_table" +
                " WHERE animal_id=:id" +
                " GROUP BY vaccination"
    )
    fun getTitleVaccinationAnimalList(id: Long): Flow<List<String>>


}