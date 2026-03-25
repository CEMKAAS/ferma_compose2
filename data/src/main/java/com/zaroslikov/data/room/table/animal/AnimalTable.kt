package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix


@Entity(
    tableName = "animal_table",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idPT")]
)
data class AnimalTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val date: String,
    @ColumnInfo(name = "date_factory")
    val dateFactory: String?,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,//groop = true, one = false
    val sex: Boolean,
    val note : String,
    val image:String?,
    val archive: Boolean,
    @ColumnInfo(name = "food_day")
    val foodDay:Double,
    @ColumnInfo(name = "food_day_suffix")
    val foodDaySuffix : Suffix,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    )