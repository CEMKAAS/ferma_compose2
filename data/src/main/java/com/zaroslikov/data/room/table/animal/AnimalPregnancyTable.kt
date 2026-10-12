package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus

/**
 * Беременность самки. Ожидаемая дата не хранится — считается от [matingDate] по нормам [species].
 * Схема совпадает с MIGRATION_9_10 (колонки, FK, индексы index_animal_pregnancy_table_*).
 */
@Entity(
    tableName = "animal_pregnancy_table",
    foreignKeys = [
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sire_id"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("animal_id"), Index("sire_id"), Index("idPT")]
)
data class AnimalPregnancyTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "animal_id")
    val animalId: Long,
    /** Снимок вида самки на момент случки: нормы не «поплывут», если позже сменят тип. */
    val species: AnimalSpecies,
    @ColumnInfo(name = "sire_id")
    val sireId: Long? = null,
    /** Снимок имени самца или имя чужого производителя / ИО. */
    @ColumnInfo(name = "sire_name")
    val sireName: String? = null,
    /** dd.MM.yyyy */
    @ColumnInfo(name = "mating_date")
    val matingDate: String,
    val status: PregnancyStatus,
    @ColumnInfo(name = "end_date")
    val endDate: String? = null,
    @ColumnInfo(name = "interrupt_reason")
    val interruptReason: PregnancyInterruptReason? = null,
    @ColumnInfo(name = "born_female")
    val bornFemale: Int = 0,
    @ColumnInfo(name = "born_male")
    val bornMale: Int = 0,
    val stillborn: Int = 0,
    val note: String = "",
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
