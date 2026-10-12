package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi

/**
 * Сборная группа для экранов: своя запись (название, фото, примечание) и неархивные члены —
 * индивидуальные животные и обезличенные группы. Поголовье, виды и корм считаются по членам.
 */
data class AnimalHerdUi(
    val id: Long = 0,
    val name: String = "",
    val note: String = "",
    val imagePath: String? = null,
    val currentIcon: AppIcon? = null,
    val idPT: Long = 0,
    val members: List<AnimalListUi> = emptyList()
) {
    /** Виды членов через запятую: «Корова, Коза». */
    val typesLabel: String
        get() = members.map { it.type.trim() }.filter { it.isNotEmpty() }.distinct().joinToString(", ")

    /** Итог по членам; суффикс — общий у всех членов, иначе штуки. */
    val totalCount: Double get() = members.sumOf { it.totalCount }
    val totalSuffix: Suffix get() = members.map { it.suffix }.distinct().singleOrNull() ?: Suffix.PIECES

    private val individuals: List<AnimalListUi> get() = members.filter { it.sex != null }
    val hasIndividuals: Boolean get() = individuals.isNotEmpty()
    /** Самки и самцы: индивидуальные животные плюс заданные в обезличенных группах. */
    val females: Int get() = individuals.count { it.sex == false } + members.sumOf { it.femalesCount }
    val males: Int get() = individuals.count { it.sex == true } + members.sumOf { it.malesCount }

    /** Показывать ♀ и ♂: есть индивидуальные животные или пол задан в обезличенных группах. */
    val hasSexSplit: Boolean get() = hasIndividuals || females + males > 0

    /** Самки с активной беременностью. */
    val pregnant: Int get() = members.count { it.pregnancyProgress != null }

    /** Головы, пол которых не известен: в обезличенных группах — сверх заданных самок и самцов. */
    val unknownSex: Double get() = members.sumOf { it.unknownSexCount }

    /**
     * Суммарный расход: корм на голову × поголовье по каждому члену. Граммы и килограммы
     * не смешиваем — по суффиксу отдельно; нулевые не показываем.
     */
    val totalFood: Map<Suffix, Double>
        get() = members.map { it.foodDaySuffix to it.foodDay * it.totalCount }
            .filter { it.second > 0 }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.sum() }
}

fun DomainAnimalHerd.toUi(members: List<AnimalListUi>) = AnimalHerdUi(
    id = id,
    name = name,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    idPT = idPT,
    members = members
)

fun AnimalHerdUi.toDomain() = DomainAnimalHerd(
    id = id,
    name = name,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    idPT = idPT
)
