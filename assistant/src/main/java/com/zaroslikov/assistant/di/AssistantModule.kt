package com.zaroslikov.assistant.di

import android.content.Context
import com.zaroslikov.assistant.engine.AssistantEngine
import com.zaroslikov.assistant.engine.RuleBasedAssistantEngine
import com.zaroslikov.assistant.speech.AndroidSpeechInput
import com.zaroslikov.assistant.speech.SpeechInput
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AssistantEngineModule {

    /** Точка расширения: вместо правил сюда можно подставить движок на локальной LLM. */
    @Binds
    @Singleton
    abstract fun bindAssistantEngine(impl: RuleBasedAssistantEngine): AssistantEngine
}

@Module
@InstallIn(ViewModelComponent::class)
object AssistantSpeechModule {

    /** Свой распознаватель на каждый экран чата: освобождается вместе с ViewModel. */
    @Provides
    @ViewModelScoped
    fun provideSpeechInput(@ApplicationContext context: Context): SpeechInput = AndroidSpeechInput(context)
}
