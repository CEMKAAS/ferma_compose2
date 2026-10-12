package com.zaroslikov.crm.di

import android.content.Context
import androidx.room.Room
import com.zaroslikov.crm.data.room.dao.CrmClientDao
import com.zaroslikov.crm.data.room.dao.CrmContactDao
import com.zaroslikov.crm.data.room.dao.CrmInteractionDao
import com.zaroslikov.crm.data.room.dao.CrmTaskDao
import com.zaroslikov.crm.data.room.database.CrmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CrmDatabaseModule {

    // Без fallbackToDestructiveMigration: пропущенная миграция должна падать, а не стирать клиентов
    @Provides
    @Singleton
    fun provideCrmDatabase(@ApplicationContext context: Context): CrmDatabase {
        return Room.databaseBuilder(context, CrmDatabase::class.java, "crm_database").build()
    }

    @Provides
    fun provideCrmClientDao(database: CrmDatabase): CrmClientDao = database.crmClientDao()

    @Provides
    fun provideCrmContactDao(database: CrmDatabase): CrmContactDao = database.crmContactDao()

    @Provides
    fun provideCrmInteractionDao(database: CrmDatabase): CrmInteractionDao =
        database.crmInteractionDao()

    @Provides
    fun provideCrmTaskDao(database: CrmDatabase): CrmTaskDao = database.crmTaskDao()
}
