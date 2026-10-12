package com.zaroslikov.crm.di

import com.zaroslikov.crm.data.room.repository.CrmBackupRepositoryImpl
import com.zaroslikov.crm.data.room.repository.CrmClientRepositoryImpl
import com.zaroslikov.crm.data.room.repository.CrmContactRepositoryImpl
import com.zaroslikov.crm.data.room.repository.CrmInteractionRepositoryImpl
import com.zaroslikov.crm.data.room.repository.CrmTaskRepositoryImpl
import com.zaroslikov.crm.domain.repository.CrmBackupRepository
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmContactRepository
import com.zaroslikov.crm.domain.repository.CrmInteractionRepository
import com.zaroslikov.crm.domain.repository.CrmTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CrmRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCrmClientRepository(impl: CrmClientRepositoryImpl): CrmClientRepository

    @Binds
    @Singleton
    abstract fun bindCrmContactRepository(impl: CrmContactRepositoryImpl): CrmContactRepository

    @Binds
    @Singleton
    abstract fun bindCrmInteractionRepository(impl: CrmInteractionRepositoryImpl): CrmInteractionRepository

    @Binds
    @Singleton
    abstract fun bindCrmTaskRepository(impl: CrmTaskRepositoryImpl): CrmTaskRepository

    @Binds
    @Singleton
    abstract fun bindCrmBackupRepository(impl: CrmBackupRepositoryImpl): CrmBackupRepository
}
