package com.zaroslikov.data.room.dto.animal

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix

/**
 * Частичный update animal_table: все колонки, кроме herd_id.
 * Экраны собирают DomainAnimalTable с нуля, полный @Update затёр бы членство в сборной группе.
 */
data class AnimalInfoUpdate(
    val id: Long,
    val name: String,
    val type: String,
    val species: AnimalSpecies,
    val breed: String?,
    val date: String,
    @ColumnInfo(name = "date_factory")
    val dateFactory: String?,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,
    val sex: Boolean,
    val note: String,
    @ColumnInfo(name = "image_path")
    val imagePath: String?,
    @ColumnInfo(name = "icon")
    val currentIcon: AppIcon?,
    val archive: Boolean,
    @ColumnInfo(name = "food_day")
    val foodDay: Double,
    @ColumnInfo(name = "food_day_suffix")
    val foodDaySuffix: Suffix,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "females_count")
    val femalesCount: Int,
    @ColumnInfo(name = "males_count")
    val malesCount: Int
)
