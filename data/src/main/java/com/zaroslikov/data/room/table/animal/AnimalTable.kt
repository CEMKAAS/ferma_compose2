package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix


@Entity(
    tableName = "animal_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalHerdTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("herd_id"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("mother_id"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("father_id"),
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("idPT"), Index("herd_id"), Index("mother_id"), Index("father_id")]
)
data class AnimalTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    /**
     * Название для показа: у известного вида — имя по полу/группе или синоним («Несушка»),
     * у UNKNOWN — свой тип. [species] при записи вычисляется из этого поля.
     */
    val type: String,
    /** Код [AnimalSpecies]; DEFAULT совпадает с MIGRATION_8_9. */
    @ColumnInfo(name = "species", defaultValue = "0")
    val species: AnimalSpecies = AnimalSpecies.UNKNOWN,
    /** Порода — необязательна, NULL если не указана (MIGRATION_10_11). */
    val breed: String? = null,
    val date: String,
    @ColumnInfo(name = "date_factory")
    val dateFactory: String?,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,//groop = true, one = false
    val sex: Boolean,
    val note : String,
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,
    @ColumnInfo(name = "icon")
    val currentIcon: AppIcon? = null,
    val archive: Boolean,
    @ColumnInfo(name = "food_day")
    val foodDay:Double,
    @ColumnInfo(name = "food_day_suffix")
    val foodDaySuffix : Suffix,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    /**
     * Сборная группа, в которую входит животное или обезличенная группа (MIGRATION_7_8);
     * SET NULL при удалении сборной группы.
     */
    @ColumnInfo(name = "herd_id")
    val herdId: Long? = null,
    /** Мать (самка того же хозяйства). Ставится при родах; SET NULL при её удалении. */
    @ColumnInfo(name = "mother_id")
    val motherId: Long? = null,
    /** Имя матери: снимок при родах. Остаётся, даже если mother_id обнулится. */
    @ColumnInfo(name = "mother_name")
    val motherName: String? = null,
    /** Отец из хозяйства; SET NULL при его удалении. */
    @ColumnInfo(name = "father_id")
    val fatherId: Long? = null,
    /** Имя отца: снимок при родах или чужой производитель / ИО. Остаётся, даже если father_id обнулится. */
    @ColumnInfo(name = "father_name")
    val fatherName: String? = null,
    /**
     * Обезличенная группа: сколько в ней самок и самцов (MIGRATION_13_14). Остальное поголовье —
     * пол не известен. У индивидуального животного — 0.
     */
    @ColumnInfo(name = "females_count", defaultValue = "0")
    val femalesCount: Int = 0,
    @ColumnInfo(name = "males_count", defaultValue = "0")
    val malesCount: Int = 0,
)