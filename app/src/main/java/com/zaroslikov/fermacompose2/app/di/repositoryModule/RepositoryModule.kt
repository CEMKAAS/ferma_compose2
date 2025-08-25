package com.zaroslikov.fermacompose2.app.di.repositoryModule

import android.content.Context
import androidx.room.Room
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.database.AppDatabase.Companion.Instance
import com.zaroslikov.data.room.database.AppDatabase.Companion.MIGRATION_1_2
import com.zaroslikov.data.room.database.AppDatabase.Companion.MIGRATION_2_3
import com.zaroslikov.data.room.database.AppDatabase.Companion.MIGRATION_3_4
import com.zaroslikov.data.room.dao.ItemDao
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.data.room.repository.OfflineItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        provideItemsRepository: Provider<ItemDao>
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


}