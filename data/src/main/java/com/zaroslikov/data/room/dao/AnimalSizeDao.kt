package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import kotlinx.coroutines.flow.Flow


@Dao
interface AnimalSizeDao {
    @Insert
    suspend fun insertAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Update
    suspend fun updateAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Delete
    suspend fun deleteAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Query(
        "SELECT * FROM AnimalSizeTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getSizeAnimalLimit(id: Long): Flow<AnimalSizeTable?>

    @Query(
        "SELECT id, size, suffix, date, idAnimal, note" +
                " FROM AnimalSizeTable" +
                " WHERE idAnimal=:id" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', substr(date, 7, 4), substr(date, 4, 2), substr(date, 1, 2))) DESC, id DESC"
    )
    fun getSizeAnimal(id: Long): Flow<List<AnimalSizeTable>>
}