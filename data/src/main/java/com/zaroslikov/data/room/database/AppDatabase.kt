package com.zaroslikov.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zaroslikov.data.room.converter.AnimalCountVersionConverter
import com.zaroslikov.data.room.converter.CategoryConverter
import com.zaroslikov.data.room.converter.FinanceCategoryConverter
import com.zaroslikov.data.room.converter.SuffixConverter
import com.zaroslikov.data.room.dao.AddDao
import com.zaroslikov.data.room.dao.AnimalCountDao
import com.zaroslikov.data.room.dao.AnimalDao
import com.zaroslikov.data.room.dao.AnimalSizeDao
import com.zaroslikov.data.room.dao.AnimalVaccinationDao
import com.zaroslikov.data.room.dao.AnimalWeightDao
import com.zaroslikov.data.room.dao.BookmarkDao
import com.zaroslikov.data.room.dao.ExpensesAnimalDao
import com.zaroslikov.data.room.dao.ExpensesDao
import com.zaroslikov.data.room.dao.FinanceDao
import com.zaroslikov.data.room.dao.IncubatorDao
import com.zaroslikov.data.room.dao.IncubatorTableDao
import com.zaroslikov.data.room.dao.NoteDao
import com.zaroslikov.data.room.dao.ProjectDao
import com.zaroslikov.data.room.dao.SaleDao
import com.zaroslikov.data.room.dao.SettingsDao
import com.zaroslikov.data.room.dao.WarehouseDao
import com.zaroslikov.data.room.dao.WriteOffDao
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.incubator.Incubator
import com.zaroslikov.data.room.table.ferma.NoteTable
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.table.incubator.BookmarkTable
import com.zaroslikov.data.room.table.incubator.IncubatorTable
import com.zaroslikov.data.room.table.project.SettingsTable

@Database(
    entities = [
        AddTable::class,
        SaleTable::class,
        ExpensesTable::class,
        WriteOffTable::class,
        ProjectTable::class,
        Incubator::class,
        AnimalTable::class,
        AnimalCountTable::class,
        AnimalSizeTable::class,
        AnimalVaccinationTable::class,
        AnimalWeightTable::class,
        NoteTable::class,
        ExpensesAnimalTable::class,
        SettingsTable::class,
        IncubatorTable::class,
        BookmarkTable::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    CategoryConverter::class,
    FinanceCategoryConverter::class,
    SuffixConverter::class,
    AnimalCountVersionConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun addDao(): AddDao
    abstract fun animalCountDao(): AnimalCountDao
    abstract fun animalDao(): AnimalDao
    abstract fun animalSizeDao(): AnimalSizeDao
    abstract fun animalVaccinationDao(): AnimalVaccinationDao
    abstract fun animalWeightDao(): AnimalWeightDao
    abstract fun expensesAnimalDao(): ExpensesAnimalDao
    abstract fun expensesDao(): ExpensesDao
    abstract fun financeDao(): FinanceDao
    abstract fun incubatorDao(): IncubatorDao
    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun saleDao(): SaleDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun writeOffDao(): WriteOffDao
    abstract fun settingsDao(): SettingsDao
    abstract fun incubatorTableDao(): IncubatorTableDao
    abstract fun bookmarkTableDao(): BookmarkDao

    companion object {
        @Volatile
        var Instance: AppDatabase? = null
    }
}