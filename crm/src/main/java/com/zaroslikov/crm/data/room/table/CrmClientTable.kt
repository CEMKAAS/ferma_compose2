package com.zaroslikov.crm.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.crm.domain.model.CrmClientSource

/**
 * `sales_key` — неизменяемый TRIM-ключ связи с `sale_table.buyer`. Уникален в рамках проекта:
 * синхронизация с продажами делает INSERT OR IGNORE и не плодит дублей, в том числе
 * не воскрешает архивных клиентов.
 */
@Entity(
    tableName = "crm_client_table",
    indices = [
        Index(value = ["idPT"]),
        Index(value = ["idPT", "sales_key"], unique = true)
    ]
)
data class CrmClientTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "sales_key")
    val salesKey: String,
    @ColumnInfo(name = "source")
    val source: CrmClientSource,
    @ColumnInfo(name = "is_vip", defaultValue = "0")
    val isVip: Boolean = false,
    @ColumnInfo(name = "note", defaultValue = "''")
    val note: String = "",
    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "created_date")
    val createdDate: String,
    @ColumnInfo(name = "is_archived", defaultValue = "0")
    val isArchived: Boolean = false
)
