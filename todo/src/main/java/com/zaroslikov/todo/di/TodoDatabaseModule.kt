package com.zaroslikov.todo.di

import android.content.Context
import androidx.room.Room
import com.zaroslikov.todo.data.room.dao.TodoAssigneeDao
import com.zaroslikov.todo.data.room.dao.TodoCommentDao
import com.zaroslikov.todo.data.room.dao.TodoLinkDao
import com.zaroslikov.todo.data.room.dao.TodoListDao
import com.zaroslikov.todo.data.room.dao.TodoSectionDao
import com.zaroslikov.todo.data.room.dao.TodoStatusDao
import com.zaroslikov.todo.data.room.dao.TodoTagDao
import com.zaroslikov.todo.data.room.dao.TodoTaskDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_1_2
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_2_3
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_3_4
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_4_5
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_5_6
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_6_7
import com.zaroslikov.todo.data.room.database.migration.MIGRATION_7_8
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodoDatabaseModule {

    // Без fallbackToDestructiveMigration: пропущенная миграция должна падать, а не стирать задачи
    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database")
            .addMigrations(
                MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7,
                MIGRATION_7_8
            )
            .build()
    }

    @Provides
    fun provideTodoListDao(database: TodoDatabase): TodoListDao = database.todoListDao()

    @Provides
    fun provideTodoAssigneeDao(database: TodoDatabase): TodoAssigneeDao = database.todoAssigneeDao()

    @Provides
    fun provideTodoStatusDao(database: TodoDatabase): TodoStatusDao = database.todoStatusDao()

    @Provides
    fun provideTodoTaskDao(database: TodoDatabase): TodoTaskDao = database.todoTaskDao()

    @Provides
    fun provideTodoTagDao(database: TodoDatabase): TodoTagDao = database.todoTagDao()

    @Provides
    fun provideTodoLinkDao(database: TodoDatabase): TodoLinkDao = database.todoLinkDao()

    @Provides
    fun provideTodoCommentDao(database: TodoDatabase): TodoCommentDao = database.todoCommentDao()

    @Provides
    fun provideTodoSectionDao(database: TodoDatabase): TodoSectionDao = database.todoSectionDao()
}
