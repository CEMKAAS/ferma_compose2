package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.ferma.Incubator
import kotlinx.coroutines.flow.Flow

@Dao
interface IncubatorDao {
    @Query("SELECT * from MyIncubator Where idPT =:idPT")
    suspend fun getIncubatorListArh4(idPT: Long): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubatorList(id: Long): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    suspend fun getIncubatorList2(id: Long): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubator(id: Long): Flow<Incubator>

    @Query("SELECT * from MyIncubator Where idPT=:id and day=:day")
    fun getIncubatorEditDay(id: Long, day: Int): Flow<Incubator>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertIncubator(incubator: Incubator)

    @Update
    suspend fun updateIncubator(incubator: Incubator)

}
