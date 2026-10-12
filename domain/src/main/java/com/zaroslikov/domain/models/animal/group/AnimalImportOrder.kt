package com.zaroslikov.domain.models.animal.group

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalHerd

/**
 * Приводит ссылки животных к валидным значениям перед импортом: убирает дубликаты id
 * (остаётся первая строка), обнуляет herdId, если такой сборной группы нет в [herds]
 * или она из другого проекта.
 * Родословная: висячие и самоссылочные motherId/fatherId обнуляются, fatherName сохраняется
 * (сами ссылки на родителей ставятся второй фазой импорта, порядок для них неважен).
 */
fun List<DomainAnimalTable>.normalizedForImport(herds: List<DomainAnimalHerd>): List<DomainAnimalTable> {
    val unique = distinctBy { it.id }
    val byId = unique.associateBy { it.id }
    val herdProject = herds.associate { it.id to it.idPT }
    return unique.map { a ->
        a.copy(
            motherId = a.motherId?.takeIf { it != a.id && it in byId },
            fatherId = a.fatherId?.takeIf { it != a.id && it in byId },
            herdId = a.herdId?.takeIf { herdProject[it] == a.idPT }
        )
    }
}

/**
 * Сборные группы для импорта: без дубликатов id (остаётся первая) и только проектов из бэкапа —
 * иначе FK на project_table уронит импорт на полпути.
 */
fun List<DomainAnimalHerd>.normalizedHerdsForImport(projectIds: Set<Long>): List<DomainAnimalHerd> =
    distinctBy { it.id }.filter { it.idPT in projectIds }
