package com.zaroslikov.fermacompose2.data.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable


@Entity(
    tableName = "AnimalTable",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AnimalTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val data: String,
    val groop: Boolean,//groop = true, one = false
    val sex: String,
    val note : String,
    val image:String,
    val arhiv: Boolean,
    val price: Double,
    @ColumnInfo(name = "idPT")
    val idPT: Int,
    )