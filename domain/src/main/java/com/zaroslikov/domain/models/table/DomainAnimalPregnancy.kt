package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AnimalSpeciesAsCodeSerializer
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyInterruptReasonAsCodeSerializer
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.enums.PregnancyStatusAsCodeSerializer
import kotlinx.serialization.Serializable

/**
 * Беременность самки. Ожидаемая дата не хранится — считается от [matingDate] по нормам [species].
 * Даты — строки `dd.MM.yyyy`.
 */
@Serializable
data class DomainAnimalPregnancy(
    val id: Long = 0,
    val animalId: Long = 0,
    /** Снимок вида самки на момент случки: нормы не «поплывут», если позже сменят тип. */
    @Serializable(with = AnimalSpeciesAsCodeSerializer::class)
    val species: AnimalSpecies = AnimalSpecies.UNKNOWN,
    val sireId: Long? = null,
    /** Снимок имени самца или имя чужого производителя / ИО. */
    val sireName: String? = null,
    val matingDate: String = "",
    @Serializable(with = PregnancyStatusAsCodeSerializer::class)
    val status: PregnancyStatus = PregnancyStatus.ACTIVE,
    val endDate: String? = null,
    @Serializable(with = PregnancyInterruptReasonAsCodeSerializer::class)
    val interruptReason: PregnancyInterruptReason? = null,
    val bornFemale: Int = 0,
    val bornMale: Int = 0,
    val stillborn: Int = 0,
    val note: String = "",
    val idPT: Long = 0
) {
    val bornAlive: Int get() = bornFemale + bornMale
}
