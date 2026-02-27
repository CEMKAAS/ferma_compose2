package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.incubator.IncubatorParameters
import kotlinx.coroutines.flow.Flow

@Dao
interface IncubatorParametersDao {
    @Query("SELECT * from incubator_parameters Where idPT =:idPT")
    fun getIncubatorListArh4(idPT: Long): Flow<List<IncubatorParameters>>

    @Query("SELECT * from incubator_parameters Where idPT=:id")
    fun getIncubatorList(id: Long): Flow<List<IncubatorParameters>>

    @Query("SELECT * from incubator_parameters Where idPT=:id")
    fun getIncubatorList2(id: Long): Flow<List<IncubatorParameters>>

    @Query("SELECT * from incubator_parameters Where idPT=:id")
    fun getIncubator(id: Long): Flow<IncubatorParameters>

    @Query("SELECT * from incubator_parameters Where idPT=:id and day=:day")
    fun getIncubatorEditDay(id: Long, day: Int): Flow<IncubatorParameters>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertIncubator(incubator: IncubatorParameters)

    @Update
    suspend fun updateIncubator(incubator: IncubatorParameters)

    /*@Query("SELECT COUNT(*)" +
            " FROM МyINCUBATOR" +
            " WHERE mode = 0" +
            " AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2)))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COALESCE(SUM(EGGALL), 0) AS resultPrice" +
            " FROM МyINCUBATOR" +
            " WHERE mode = 0" +
            " AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2)))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COALESCE(SUM(EGGALLEND), 0) AS resultPrice" +
            " FROM МyINCUBATOR" +
            " WHERE mode = 0" +
            " AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2)))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getChickenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT count(DISTINCT TYPE)" +
            " FROM МyINCUBATOR" +
            " WHERE mode = 0" +
            " AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2)))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String>
*/
}
