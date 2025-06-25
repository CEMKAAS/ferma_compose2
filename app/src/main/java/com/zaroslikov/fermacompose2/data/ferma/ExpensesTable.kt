package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData

@Entity(
    tableName = "MyFermaEXPENSES",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
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
    @ColumnInfo(name = "titleEXPENSES")
    val title: String, // название
    @ColumnInfo(name = "discEXPENSES")
    val count: Double, // Кол-во
    @ColumnInfo(name = "DAY")
    val day: Int,  // день
    @ColumnInfo(name = "MOUNT")
    val mount: Int, // месяц
    @ColumnInfo(name = "YEAR")
    val year: Int, // время
    @ColumnInfo(name = "countEXPENSES")
    val priceAll: Double, // кол-во товара
    var suffix: String,
    var category: String,
    val note: String,
    val showFood: Boolean, // Показывать на складе иду
    val showWarehouse: Boolean, // Показывать на складе
    val showAnimals: Boolean, // Связывает животных
    val dailyExpensesFoodAndCount: Boolean, // указать вручную
    val dailyExpensesFood: Double, // Ежедневный расход еды
    val countAnimal: Int, // Кол-во животных
    val foodDesignedDay: Int, // Кол-во дней
    val lastDayFood: String, //Последний день еды
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "animalId")
    val animalId: Long? = null,
    @ColumnInfo(name = "animal_vaccination_id")
    val animalVaccinationId: Long? = null,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)