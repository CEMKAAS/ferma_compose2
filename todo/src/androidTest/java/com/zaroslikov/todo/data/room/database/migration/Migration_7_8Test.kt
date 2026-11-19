package com.zaroslikov.todo.data.room.database.migration

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class Migration_7_8Test {
    @get:Rule val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), TodoDatabase::class.java)

    @Test fun migrationPreservesProjectDirectoryAndTaskAssignments() = runBlocking {
        val name = "todo-assignee-profile-migration"
        helper.createDatabase(name, 7).use { db ->
            db.execSQL("INSERT INTO todo_assignee_table (idPT, name_key, name) VALUES (7, 'иван иванов', 'Иван Иванов')")
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, created_at) VALUES (1, 7, 'Куры', '2026-11-14T10:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title) VALUES (1, 7, 'В планах')")
            db.execSQL(
                "INSERT INTO todo_task_table (id, list_id, title, status_id, created_at, assignees) VALUES (?, ?, ?, ?, ?, ?)",
                arrayOf<Any?>(5, 1, "Корм", 1, "2026-11-14T10:00", """["Иван Иванов"]""")
            )
        }
        helper.runMigrationsAndValidate(name, 8, true, MIGRATION_7_8).close()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.databaseBuilder(context, TodoDatabase::class.java, name).build()
        try {
            val assignee = database.todoAssigneeDao().getProfiles(7).first().single().toDomain()
            assertEquals(TodoAssigneeProfile(firstName = "Иван Иванов"), assignee.profile)
            assertEquals(listOf("Иван Иванов"), database.todoTaskDao().getTask(5)?.assignees)
        } finally {
            database.close()
        }
    }
}
