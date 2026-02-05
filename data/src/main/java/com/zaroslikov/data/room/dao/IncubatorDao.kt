package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.incubator.Incubator
import kotlinx.coroutines.flow.Flow

@Dao
interface IncubatorDao {
    @Query("SELECT * from MyIncubator Where idPT =:idPT")
    fun getIncubatorListArh4(idPT: Long): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubatorList(id: Long): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubatorList2(id: Long): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubator(id: Long): Flow<Incubator>

    @Query("SELECT * from MyIncubator Where idPT=:id and day=:day")
    fun getIncubatorEditDay(id: Long, day: Int): Flow<Incubator>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertIncubator(incubator: Incubator)

    @Update
    suspend fun updateIncubator(incubator: Incubator)

    @Query("SELECT COUNT(*)" +
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

}
