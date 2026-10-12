package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun DomainAnimalWithCount.toAnimalListUi(resourceProvider: ResourceProvider): AnimalListUi {
    return AnimalListUi(
        id = id,
        name = name,
        type = type,
        breed = breed?.takeIf { it.isNotBlank() },
        date = resourceProvider.getAgeFromDate(dateFactory ?: date),
        isGroup = group,
        sex = if (group) null else sex,
        count = totalCount.takeIf { it > 0 }?.formatNumber(isGroupingUsed = false),
        suffix = countSuffix,
        herdId = herdId,
        herdName = herdName,
        ownCount = ownCount,
        totalCount = totalCount,
        foodDay = foodDay,
        foodDaySuffix = foodDaySuffix,
        isArchive = isArchive,
        imagePath = imagePath,
        currentIcon = currentIcon ?: AppIcon.PETS,
        femalesCount = if (group) femalesCount else 0,
        malesCount = if (group) malesCount else 0
    )
}

private fun ResourceProvider.getAgeFromDate(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val birthDate = LocalDate.parse(dateString, formatter)
    val currentDate = LocalDate.now()

    if (birthDate.isAfter(currentDate)) {
        return getString(R.string.date_in_future)
    }

    val period = Period.between(birthDate, currentDate)
    val years = period.years
    val months = period.months

    val yeatsString = getString(R.string.date_years)
    val monthsString = getString(R.string.date_months)

    return when {
        years >= 1 -> "$years $yeatsString $months $monthsString"
        else -> {
            val totalMonths = ChronoUnit.MONTHS.between(birthDate, currentDate)
            "$totalMonths $monthsString"
        }
    }
}

/** id самки → прогресс срока активной беременности. */
fun List<DomainAnimalPregnancy>.toPregnancyProgress(): Map<Long, PregnancyProgress> {
    val today = LocalDate.now()
    return mapNotNull { p ->
        val norms = PregnancyNorms.of(p.species) ?: return@mapNotNull null
        val mating = PregnancyDates.parseOrNull(p.matingDate) ?: return@mapNotNull null
        p.animalId to PregnancyCalculator.progress(norms, mating, today)
    }.toMap()
}

/**
 * Животное из формы добавления. [herdId] — сборная группа, если животное добавляют прямо в неё.
 */
internal fun AnimalEntryState2.toDomainAnimal(herdId: Long? = null): DomainAnimalTable = DomainAnimalTable(
    id = itemId,
    name = title.trim(),
    type = type.trim(),
    breed = breed.trim().ifEmpty { null },
    species = AnimalSpecies.resolve(type),
    date = dateBorn,
    dateFactory = if (isDateFactory) null else dateFactory,
    group = isAnimalGroup,
    sex = sex,
    note = note.trim(),
    archive = archive,
    foodDay = if (foodDay.isBlank()) 0.0 else foodDay.toConvertDbDouble(),
    foodDaySuffix = foodDaySuffix,
    idPT = itemIdPT,
    herdId = herdId,
    femalesCount = if (isAnimalGroup) femalesCount.toIntOrNull() ?: 0 else 0,
    malesCount = if (isAnimalGroup) malesCount.toIntOrNull() ?: 0 else 0
)

/**
 * Первая запись счётчика нового животного: указана цена — покупка (EXPENSES и расход в финансах),
 * нет — просто приход (ADD). animalCountId расхода проставляет вызывающий.
 */
internal fun AnimalEntryState2.toFirstCount(
    idAnimal: Long,
    itemIdPT: Long,
    currency: Suffix,
    expensesCategory: String
): Pair<DomainAnimalCount, DomainExpensesTable?> {
    val dateFactory2 = if (isDateFactory) dateBorn else dateFactory
    val dateList = dateFactory2.split(".")
    val countAnimal = count.ifBlank { "1" }
    val isPurchase = price.isNotBlank()
    return DomainAnimalCount(
        count = countAnimal,
        suffix = countSuffix,
        date = if (!isDateFactory) dateFactory else dateBorn,
        idAnimal = idAnimal,
        note = "",
        version = if (isPurchase) AnimalCountVersion.EXPENSES else AnimalCountVersion.ADD
    ) to if (isPurchase) DomainExpensesTable(
        title = title.trim(),
        count = countAnimal.toConvertZeroDouble(),
        day = dateList[0].toInt(),
        month = dateList[1].toInt(),
        year = dateList[2].toInt(),
        price = price.toConvertDbDouble(),
        priceAll = if (isAutoPrice && isAnimalGroup) priceAll.toConvertDbDouble() else null,
        priceSuffix = currency,
        countSuffix = countSuffix,
        category = expensesCategory,
        note = "",
        isShowFood = false,
        idPT = itemIdPT,
        animalId = idAnimal,
        animalVaccinationId = null,
        animalCountId = null,
        isFood = false,
    ) else null
}
