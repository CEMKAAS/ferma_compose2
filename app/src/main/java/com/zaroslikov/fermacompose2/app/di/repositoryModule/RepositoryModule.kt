package com.zaroslikov.fermacompose2.app.di.repositoryModule

import android.content.Context
import androidx.room.Room
import com.zaroslikov.fermacompose2.data.InventoryDatabase
import com.zaroslikov.fermacompose2.data.InventoryDatabase.Companion.Instance
import com.zaroslikov.fermacompose2.data.InventoryDatabase.Companion.MIGRATION_1_2
import com.zaroslikov.fermacompose2.data.InventoryDatabase.Companion.MIGRATION_2_3
import com.zaroslikov.fermacompose2.data.InventoryDatabase.Companion.MIGRATION_3_4
import com.zaroslikov.fermacompose2.data.ItemDao
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.OfflineItemsRepository
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
    ): InventoryDatabase {
        return Instance ?: synchronized(this) {
            Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
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
    fun provideItemDao(cabinetDatabase: InventoryDatabase) = cabinetDatabase.itemDao()

    @Provides
    @Singleton
    fun provideItemsRepository(itemDao: ItemDao): ItemsRepository = OfflineItemsRepository(itemDao)


}