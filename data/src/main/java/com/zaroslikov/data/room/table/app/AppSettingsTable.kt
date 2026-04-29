package com.zaroslikov.data.room.table.app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_settings_table",
)
data class AppSettingsTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "last_version_app")
    val lastVersionApp: String? = null,
    @ColumnInfo(name = "current_version_app")
    val currentVersionApp: String,
    @ColumnInfo(name = "is_first_launch")
    val isFirstLaunch: Boolean
)
