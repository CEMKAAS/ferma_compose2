package com.zaroslikov.data.room.table.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.domain.models.enums.Suffix

@Entity(
    tableName = "settings_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.CASCADE
        )], indices = [Index("idPT")]
)
data class SettingsTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "currency_suffix")
    val currencySuffix: Suffix,
    @ColumnInfo(name = "weight_suffix")
    val weightSuffix: Suffix,
    @ColumnInfo(name = "volume_suffix")
    val volumeSuffix: Suffix,
    @ColumnInfo(name = "linear_suffix")
    val linearSuffix: Suffix,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
)