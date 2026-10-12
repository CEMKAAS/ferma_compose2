package com.zaroslikov.domain.models.DomainAnimalTable

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AnimalSpeciesAsCodeSerializer
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.AppIconAsCodeSerializer
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainAnimalTable(
    val id: Long = 0,
    val name: String = "",
    val type: String = "",
    /** Вид; [AnimalSpecies.UNKNOWN] — свой тип из [type]. В старых бэкапах поля нет. */
    @Serializable(with = AnimalSpeciesAsCodeSerializer::class)
    val species: AnimalSpecies = AnimalSpecies.UNKNOWN,
    /** Порода; null — не указана. В старых бэкапах поля нет. */
    val breed: String? = null,
    val date: String = "",
    val dateFactory: String? = null,
    val group: Boolean = false,
    val sex: Boolean = false,
    val note: String = "",
    val archive: Boolean = false,
    val foodDay: Double = 0.0,
    val foodDaySuffix: Suffix = Suffix.GRAM,
    val imagePath: String? = null,
    @Serializable(with = AppIconAsCodeSerializer::class)
    val currentIcon: AppIcon? = null,
    val idPT: Long = 0,
    /** Сборная группа, в которой состоит животное; null — самостоятельное. В старых бэкапах поля нет. */
    val herdId: Long? = null,
    /** Мать (самка того же хозяйства). Ставится при родах. В старых бэкапах поля нет. */
    val motherId: Long? = null,
    /** Имя матери: снимок при родах. Остаётся, даже если motherId обнулится. */
    val motherName: String? = null,
    /** Отец из хозяйства; обнуляется при его удалении. */
    val fatherId: Long? = null,
    /** Имя отца: снимок при родах или чужой производитель / ИО. */
    val fatherName: String? = null,
    /** Обезличенная группа: сколько самок и самцов; остальное — пол не известен. В старых бэкапах полей нет. */
    val femalesCount: Int = 0,
    val malesCount: Int = 0
)
