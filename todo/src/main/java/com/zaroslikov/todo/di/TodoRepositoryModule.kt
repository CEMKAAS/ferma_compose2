package com.zaroslikov.todo.di

import com.zaroslikov.todo.data.room.repository.TodoBackupRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoCommentRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoLinkRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoListRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoSectionRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoStatusRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoTagRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoTaskRepositoryImpl
import com.zaroslikov.todo.domain.repository.TodoBackupRepository
import com.zaroslikov.todo.domain.repository.TodoCommentRepository
import com.zaroslikov.todo.domain.repository.TodoLinkRepository
import com.zaroslikov.todo.domain.repository.TodoListRepository
import com.zaroslikov.todo.domain.repository.TodoSectionRepository
import com.zaroslikov.todo.domain.repository.TodoStatusRepository
import com.zaroslikov.todo.domain.repository.TodoTagRepository
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TodoRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTodoListRepository(impl: TodoListRepositoryImpl): TodoListRepository

    @Binds
    @Singleton
    abstract fun bindTodoStatusRepository(impl: TodoStatusRepositoryImpl): TodoStatusRepository

    @Binds
    @Singleton
    abstract fun bindTodoTaskRepository(impl: TodoTaskRepositoryImpl): TodoTaskRepository

    @Binds
    @Singleton
    abstract fun bindTodoTagRepository(impl: TodoTagRepositoryImpl): TodoTagRepository

    @Binds
    @Singleton
    abstract fun bindTodoLinkRepository(impl: TodoLinkRepositoryImpl): TodoLinkRepository

    @Binds
    @Singleton
    abstract fun bindTodoCommentRepository(impl: TodoCommentRepositoryImpl): TodoCommentRepository

    @Binds
    @Singleton
    abstract fun bindTodoSectionRepository(impl: TodoSectionRepositoryImpl): TodoSectionRepository

    @Binds
    @Singleton
    abstract fun bindTodoBackupRepository(impl: TodoBackupRepositoryImpl): TodoBackupRepository
}
