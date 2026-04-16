package com.zaroslikov.fermacompose2.app.di.uiModule

import android.content.Context
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepository
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepositoryImpl
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepositoryImpl
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiModule {

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider {
        return ResourceProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideWorkManagerRepository(
        @ApplicationContext context: Context
    ): WorkManagerRepository {
        return WorkManagerRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideYandexMetricRepository(
        resourceProvider: ResourceProvider
    ): YandexMetricRepository {
        return YandexMetricRepositoryImpl(resourceProvider)
    }
}
