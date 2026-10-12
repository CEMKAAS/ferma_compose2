package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import com.zaroslikov.domain.models.dto.add.DomainHerdProduct
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.mapperToBrieflyItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Вес сборной группы по последним взвешиваниям членов. Вес обезличенной группы — средний
 * на голову, поэтому он умножается на её поголовье; у индивидуального животного — одна голова.
 * [weighed] из [members] — сколько членов взвешено.
 */
data class HerdWeight(
    val average: Double,
    val total: Double,
    val suffix: Suffix,
    val weighed: Int,
    val members: Int
)

/** Продукция одного члена по выбранному виду продукции. */
data class HerdMemberProduct(val member: AnimalListUi, val item: BrieflyItem)

/** null — никто из членов не взвешен. */
fun herdWeight(members: List<AnimalListUi>, weights: List<DomainAnimalWeight>, target: Suffix): HerdWeight? {
    val latest = weights.groupBy { it.idAnimal }
        .mapValues { (_, list) -> list.maxWith(compareBy({ it.date.toLocalDateOrMin() }, { it.id })) }
    var total = 0.0
    var heads = 0.0
    var weighed = 0
    members.forEach { member ->
        val weight = latest[member.id] ?: return@forEach
        val value = weight.weight.replace(',', '.').toDoubleOrNull()
            ?.convertWeight(weight.suffix, target)
            ?.takeIf { it > 0 } ?: return@forEach
        val count = if (member.sex != null) 1.0 else member.totalCount
        if (count <= 0) return@forEach
        total += value * count
        heads += count
        weighed++
    }
    if (heads == 0.0) return null
    return HerdWeight(total / heads, total, target, weighed, members.size)
}

/** Итог по каждому виду продукции (по названию), в единицах из настроек хозяйства. */
fun herdProducts(products: List<DomainHerdProduct>, settings: DomainSettings): List<BrieflyItem> =
    products.groupBy { it.title }
        .map { (title, items) -> mapperToBrieflyItem(title, items, settings) }
        .sortedBy { it.title }

/** Вид продукции → члены, от которых она получена, по убыванию количества. */
fun herdProductsByMember(
    members: List<AnimalListUi>,
    products: List<DomainHerdProduct>,
    settings: DomainSettings
): Map<String, List<HerdMemberProduct>> {
    val byId = members.associateBy { it.id }
    return products.groupBy { it.title }.mapValues { (title, items) ->
        items.groupBy { it.animalId }
            .mapNotNull { (id, list) ->
                byId[id]?.let { HerdMemberProduct(it, mapperToBrieflyItem(title, list, settings)) }
            }
            .sortedByDescending { it.item.mainValue }
    }
}

/** Значение для сортировки: вес, иначе объём, длина, штуки. */
private val BrieflyItem.mainValue: Double
    get() = (weight ?: volume ?: linear ?: pieces)?.value?.first ?: 0.0

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun String.toLocalDateOrMin(): LocalDate =
    try {
        LocalDate.parse(this, DATE_FORMATTER)
    } catch (_: DateTimeParseException) {
        LocalDate.MIN
    }
