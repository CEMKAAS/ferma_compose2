package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix

@Entity(
    tableName = "expenses_table",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animalId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalVaccinationTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_vaccination_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalCountTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_count_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idPT"), Index("animalId"), Index("animal_vaccination_id"), Index("animal_count_id")]
)
data class ExpensesTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "count")
    val count: Double,
    @ColumnInfo(name = "day")
    val day: Int,
    @ColumnInfo(name = "month")
    val mount: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix,
    @ColumnInfo(name = "price_suffix")
    val priceSuffix: Suffix,
    val category: String,
    val note: String,
    @ColumnInfo(name = "is_food")
    val isFood: Boolean,
    @ColumnInfo(name = "is_show_food")
    val isShowFood: Boolean,
    @ColumnInfo(name = "feed_food")
    val feedFood: Double?,
    @ColumnInfo(name = "feed_food_suffix")
    val feedFoodSuffix: Suffix?,
    @ColumnInfo(name = "count_animal")
    val countAnimal: Int?,
    @ColumnInfo(name = "food_designed_day")
    val foodDesignedDay: Int?, // Кол-во дней
    @ColumnInfo(name = "last_day_food")
    val lastDayFood: String?, //Последний день еды
    val weight: Double?,
    @ColumnInfo(name = "weight_suffix")
    val weightSuffix: Suffix?,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "animalId")
    val animalId: Long? = null,
    @ColumnInfo(name = "animal_vaccination_id")
    val animalVaccinationId: Long? = null,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null,
)