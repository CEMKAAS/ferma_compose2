package com.zaroslikov.todo.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zaroslikov.todo.data.room.dao.TodoAssigneeDao
import com.zaroslikov.todo.data.room.table.TodoAssigneeTable
import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.data.room.dao.TodoCommentDao
import com.zaroslikov.todo.data.room.dao.TodoLinkDao
import com.zaroslikov.todo.data.room.dao.TodoListDao
import com.zaroslikov.todo.data.room.dao.TodoSectionDao
import com.zaroslikov.todo.data.room.dao.TodoStatusDao
import com.zaroslikov.todo.data.room.dao.TodoTagDao
import com.zaroslikov.todo.data.room.dao.TodoTaskDao
import com.zaroslikov.todo.data.room.table.TodoCommentTable
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoSectionTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTagTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.data.room.table.TodoTaskTagTable

/**
 * Отдельная от `AppDatabase` база задач (`todo_database`). Связь с проектом — колонка `idPT`
 * у списков, статусов и тегов без внешнего ключа между базами.
 * Миграции — `data/room/database/migration`, регистрируются в `TodoDatabaseModule`.
 */
@Database(
    entities = [
        TodoAssigneeTable::class,
        TodoListTable::class,
        TodoSectionTable::class,
        TodoStatusTable::class,
        TodoTaskTable::class,
        TodoTagTable::class,
        TodoTaskTagTable::class,
        TodoTaskLinkTable::class,
        TodoCommentTable::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(TodoConverters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoAssigneeDao(): TodoAssigneeDao
    abstract fun todoListDao(): TodoListDao
    abstract fun todoStatusDao(): TodoStatusDao
    abstract fun todoTaskDao(): TodoTaskDao
    abstract fun todoTagDao(): TodoTagDao
    abstract fun todoLinkDao(): TodoLinkDao
    abstract fun todoCommentDao(): TodoCommentDao
    abstract fun todoSectionDao(): TodoSectionDao
}
