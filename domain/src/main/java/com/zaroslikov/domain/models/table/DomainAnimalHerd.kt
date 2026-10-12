package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.AppIconAsCodeSerializer
import kotlinx.serialization.Serializable

/**
 * Сборная группа: объединяет индивидуальных животных и обезличенные группы одного хозяйства.
 * Своего поголовья, вида и дат у неё нет — всё считается по членам (animal_table.herd_id).
 * Меняются только название, фото/иконка и примечание.
 */
@Serializable
data class DomainAnimalHerd(
    val id: Long = 0,
    val name: String = "",
    val note: String = "",
    val imagePath: String? = null,
    @Serializable(with = AppIconAsCodeSerializer::class)
    val currentIcon: AppIcon? = null,
    val idPT: Long = 0
)
