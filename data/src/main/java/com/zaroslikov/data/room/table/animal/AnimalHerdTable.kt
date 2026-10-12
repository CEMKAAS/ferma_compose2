package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.AppIcon

/**
 * Сборная группа (MIGRATION_7_8). Члены — строки animal_table с herd_id = id: индивидуальные
 * животные и обезличенные группы. Своего поголовья нет, оно считается по членам.
 */
@Entity(
    tableName = "animal_herd_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idPT")]
)
data class AnimalHerdTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val note: String,
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,
    @ColumnInfo(name = "icon")
    val currentIcon: AppIcon? = null,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
