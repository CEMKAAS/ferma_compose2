package com.zaroslikov.calendar.data

import android.content.Context
import androidx.room.Room
import com.zaroslikov.calendar.domain.CalendarBackupRepository
import com.zaroslikov.calendar.domain.CalendarRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CalendarDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CalendarDatabase =
        Room.databaseBuilder(context, CalendarDatabase::class.java, "calendar_database").build()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface CalendarRepositoryModule {
    @Binds
    fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository

    @Binds
    fun bindCalendarBackupRepository(impl: CalendarBackupRepositoryImpl): CalendarBackupRepository
}
