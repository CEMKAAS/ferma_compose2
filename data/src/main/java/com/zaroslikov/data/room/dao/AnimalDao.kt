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
                "an.date_factory, " +
                "an.`group`, " +
                "an.sex, " +
                "ac.count AS count, " +
                "ac.suffix AS suffix " +
                "FROM animal_table an " +
                "LEFT JOIN AnimalCountTable ac ON ac.id = ( " +
                "   SELECT MAX(id) " +
                "   FROM AnimalCountTable " +
                "   WHERE idAnimal = an.id " +
                ") " +
                "WHERE an.idPT = :id AND an.archive = 0 " +
                "ORDER BY an.id DESC"
    )
    fun getAllAnimal(id: Long): Flow<List<AnimalWithCountDto>>


    @Query("SELECT * from animal_table Where id=:id")
    fun getAnimal(id: Long): Flow<AnimalTable>

    @Query("SELECT type from animal_table Where idPT=:id GROUP BY type")
    fun getTypeAnimal(id: Long): Flow<List<String>>

    @Query("SELECT id, name, type from animal_table Where idPT=:id")
    fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDto>>

    @Insert
    suspend fun insertAnimalTable(animalTable: AnimalTable): Long

    @Update
    suspend fun updateAnimalTable(animalTable: AnimalTable)

    @Query("DELETE FROM animal_table WHERE id =:id")
    suspend fun deleteAnimalTable(id: Long)

}