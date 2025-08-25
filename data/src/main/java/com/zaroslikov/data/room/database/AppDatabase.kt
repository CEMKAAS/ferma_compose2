package com.zaroslikov.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaroslikov.data.room.dao.ItemDao
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.Incubator
import com.zaroslikov.data.room.table.ferma.NoteTable
import com.zaroslikov.data.room.table.ferma.ProjectTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable

@Database(
    entities = [AddTable::class, SaleTable::class, ExpensesTable::class, WriteOffTable::class,
        ProjectTable::class, Incubator::class, AnimalTable::class, AnimalCountTable::class,
        AnimalSizeTable::class, AnimalVaccinationTable::class, AnimalWeightTable::class, NoteTable::class,
        ExpensesAnimalTable::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(CategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        var Instance: AppDatabase? = null
    }
}