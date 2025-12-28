package com.zaroslikov.data.di

import android.content.Context
import androidx.room.Room
import com.zaroslikov.data.room.dao.AddDao
import com.zaroslikov.data.room.dao.AnimalCountDao
import com.zaroslikov.data.room.dao.AnimalDao
import com.zaroslikov.data.room.dao.AnimalSizeDao
import com.zaroslikov.data.room.dao.AnimalVaccinationDao
import com.zaroslikov.data.room.dao.AnimalWeightDao
import com.zaroslikov.data.room.dao.ExpensesAnimalDao
import com.zaroslikov.data.room.dao.ExpensesDao
import com.zaroslikov.data.room.dao.FinanceDao
import com.zaroslikov.data.room.dao.IncubatorDao
import com.zaroslikov.data.room.dao.IncubatorTableDao
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.database.AppDatabase.Companion.Instance
import com.zaroslikov.data.room.dao.NoteDao
import com.zaroslikov.data.room.dao.ProjectDao
import com.zaroslikov.data.room.dao.SaleDao
import com.zaroslikov.data.room.dao.SettingsDao
import com.zaroslikov.data.room.dao.WarehouseDao
import com.zaroslikov.data.room.dao.WriteOffDao
import com.zaroslikov.data.room.database.migration.MIGRATION_1_2
import com.zaroslikov.data.room.database.migration.MIGRATION_2_3
import com.zaroslikov.data.room.database.migration.MIGRATION_3_4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Instance ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "item_database")
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .addMigrations(MIGRATION_3_4)
                .build()
                .also { Instance = it }
        }
    }

    @Provides
    @Singleton
    fun provideAddDao(database: AppDatabase): AddDao {
        return database.addDao()
    }

    @Provides
    @Singleton
    fun provideAnimalCountDao(database: AppDatabase): AnimalCountDao {
        return database.animalCountDao()
    }

    @Provides
    @Singleton
    fun provideAnimalDao(database: AppDatabase): AnimalDao {
        return database.animalDao()
    }

    @Provides
    @Singleton
    fun provideAnimalSizeDao(database: AppDatabase): AnimalSizeDao {
        return database.animalSizeDao()
    }

    @Provides
    @Singleton
    fun provideAnimalVaccinationDao(database: AppDatabase): AnimalVaccinationDao {
        return database.animalVaccinationDao()
    }

    @Provides
    @Singleton
    fun provideAnimalWeightDao(database: AppDatabase): AnimalWeightDao {
        return database.animalWeightDao()
    }

    @Provides
    @Singleton
    fun provideExpensesAnimalDao(database: AppDatabase): ExpensesAnimalDao {
        return database.expensesAnimalDao()
    }

    @Provides
    @Singleton
    fun provideExpensesDao(database: AppDatabase): ExpensesDao {
        return database.expensesDao()
    }

    @Provides
    @Singleton
    fun provideFinanceDao(database: AppDatabase): FinanceDao {
        return database.financeDao()
    }

    @Provides
    @Singleton
    fun provideIncubatorDao(database: AppDatabase): IncubatorDao {
        return database.incubatorDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideSaleDao(database: AppDatabase): SaleDao {
        return database.saleDao()
    }

    @Provides
    @Singleton
    fun provideWarehouseDao(database: AppDatabase): WarehouseDao {
        return database.warehouseDao()
    }

    @Provides
    @Singleton
    fun provideWriteOffDao(database: AppDatabase): WriteOffDao {
        return database.writeOffDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    @Singleton
    fun provideIncubatorTableDao(database: AppDatabase): IncubatorTableDao {
        return database.incubatorTableDao()
    }

}