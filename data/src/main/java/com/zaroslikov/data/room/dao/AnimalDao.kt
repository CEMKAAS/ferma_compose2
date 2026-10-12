package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.AnimalWithCountDto
import com.zaroslikov.data.room.dto.animal.AnimalBreedDto
import com.zaroslikov.data.room.dto.animal.AnimalForAddDto
import com.zaroslikov.data.room.dto.animal.AnimalInfoUpdate
import com.zaroslikov.data.room.table.animal.AnimalTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * from animal_table")
    fun getAllAnimalTableForExport(): Flow<List<AnimalTable>>

    @Upsert
    suspend fun insertAllAnimalTable(data: List<AnimalTable>)

    @Query("DELETE FROM animal_table")
    suspend fun deleteAllAnimalTable()

    @Query("UPDATE animal_table SET mother_id = :motherId, father_id = :fatherId WHERE id = :id")
    suspend fun updatePedigree(id: Long, motherId: Long?, fatherId: Long?)

    /**
     * Две фазы: сначала все строки без ссылок на родителей, затем ссылки. Помёт может ссылаться
     * на мать, которая идёт позже (строки бэкапа идут в произвольном порядке), — FK не даст вставить сразу.
     */
    @Transaction
    suspend fun clearAndInsertAnimalTableForImport(animalTable: List<AnimalTable>) {
        deleteAllAnimalTable()
        insertAllAnimalTable(animalTable.map { it.copy(motherId = null, fatherId = null) })
        animalTable
            .filter { it.motherId != null || it.fatherId != null }
            .forEach { updatePedigree(it.id, it.motherId, it.fatherId) }
    }

    @Query("$ANIMAL_WITH_HERD_SELECT_BY_PROJECT WHERE an.idPT = :idPT ORDER BY an.id DESC")
    fun getAllAnimalWithGroups(idPT: Long): Flow<List<AnimalWithCountDto>>

    @Query("$ANIMAL_WITH_HERD_SELECT_BY_ANIMAL WHERE an.id = :id")
    fun getAnimalWithGroup(id: Long): Flow<AnimalWithCountDto?>

    /** Имена животных проекта — для автонумерации новых особей. */
    @Query("SELECT name FROM animal_table WHERE idPT = :idPT")
    fun getAnimalNames(idPT: Long): Flow<List<String>>

    @Query("SELECT * FROM animal_table WHERE id=:id")
    fun getAnimal(id: Long): Flow<AnimalTable>

    @Query("SELECT type FROM animal_table WHERE idPT=:id GROUP BY type")
    fun getTypeAnimal(id: Long): Flow<List<String>>

    /** Породы неархивных животных проекта; порода, оставшаяся только в архиве, не предлагается. */
    @Query(
        "SELECT DISTINCT breed, species, type FROM animal_table " +
                "WHERE idPT = :id AND archive = 0 AND breed IS NOT NULL AND TRIM(breed) != ''"
    )
    fun getBreedAnimal(id: Long): Flow<List<AnimalBreedDto>>

    @Query("SELECT id, name, type from animal_table Where idPT=:id")
    fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDto>>

    @Insert
    suspend fun insertAnimalTable(animalTable: AnimalTable): Long

    /** Обновляет всё, кроме herd_id (членство меняется только через [AnimalHerdDao]). */
    @Update(entity = AnimalTable::class)
    suspend fun updateAnimalInfo(info: AnimalInfoUpdate)

    @Query("DELETE FROM animal_table WHERE id =:id")
    suspend fun deleteAnimalTable(id: Long)

    @Query("UPDATE animal_table SET archive =:isArchive WHERE id = :id")
    suspend fun updateArchiveAnimalTableById(isArchive: Boolean, id: Long)

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