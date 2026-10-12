package com.zaroslikov.crm.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** При удалении клиента задача остаётся в проекте — `client_id` обнуляется. */
@Entity(
    tableName = "crm_task_table",
    foreignKeys = [
        ForeignKey(
            entity = CrmClientTable::class,
            parentColumns = ["id"],
            childColumns = ["client_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["idPT"]),
        Index(value = ["client_id"])
    ]
)
data class CrmTaskTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "client_id")
    val clientId: Long? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "due_date")
    val dueDate: String,
    @ColumnInfo(name = "is_done", defaultValue = "0")
    val isDone: Boolean = false,
    @ColumnInfo(name = "note", defaultValue = "''")
    val note: String = ""
)
