package com.zaroslikov.crm.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.crm.domain.model.CrmContactType

@Entity(
    tableName = "crm_contact_table",
    foreignKeys = [
        ForeignKey(
            entity = CrmClientTable::class,
            parentColumns = ["id"],
            childColumns = ["client_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["client_id"])]
)
data class CrmContactTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "client_id")
    val clientId: Long,
    @ColumnInfo(name = "type")
    val type: CrmContactType,
    @ColumnInfo(name = "value")
    val value: String,
    @ColumnInfo(name = "is_primary", defaultValue = "0")
    val isPrimary: Boolean = false
)
