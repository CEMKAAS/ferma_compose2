package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalVaccinationDao {
    @Insert
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable): Long

    @Update
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Delete
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Query(
        "SELECT * FROM AnimalVaccinationTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationAnimalLimit(id: Long): Flow<AnimalVaccinationTable?>


    @Query(
        "SELECT * FROM AnimalVaccinationTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getVaccinationAnimal(id: Long): Flow<List<AnimalVaccinationTable>>

}