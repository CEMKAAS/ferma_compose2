package com.zaroslikov.fermacompose2.utils

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toAnimalIcon
import com.zaroslikov.fermacompose2.supportFun.toResId
import javax.inject.Inject

/**
 * Записывает птенцов из приложения «Инкубатор» в хозяйство — сразу, без формы: всё, что
 * нужно для группы, пришло в ссылке, и человек уже сказал «добавить», нажав кнопку там.
 *
 * Путь тот же, что у завершения закладки встроенного инкубатора (`BookmarkViewModel`):
 * новый проект — `DomainProjectTable(mode = true)` со своей строкой настроек, группа
 * животных с иконкой вида и первая запись численности с версией
 * [AnimalCountVersion.INCUBATOR] и примечанием «Добавлено из инкубатора — …». Так птенцы
 * из другого приложения ничем не отличаются в учёте от выведенных здесь же.
 */
class IncubatorChicksImporter @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,
    private val animalRepository: AnimalRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val resourceProvider: ResourceProvider,
) {

    /** Новый проект с названием закладки; возвращает его id. */
    suspend fun toNewProject(chicks: IncubatorChicks): Long {
        val idPT = projectRepository.insertProjectLong(
            DomainProjectTable(title = chicks.name, date = dateToday(), mode = true)
        )
        settingsRepository.insertSettings(DomainSettings(idPT = idPT))
        toProject(chicks, idPT)
        return idPT
    }

    /** В имеющийся проект [idPT]. */
    suspend fun toProject(chicks: IncubatorChicks, idPT: Long) {
        // Встроенные виды «Инкубатора» совпадают с нашими type_egg_* дословно; сверяем без
        // учёта регистра и пишем своё написание, а незнакомый вид (свой вид пользователя)
        // берём как есть — тип группы у нас свободный текст.
        val typeEgg = TypeEgg.entries.firstOrNull {
            resourceProvider.getString(it.toResId()).equals(chicks.type, ignoreCase = true)
        }
        val idAnimal = animalRepository.insertAnimalTable(
            DomainAnimalTable(
                name = chicks.name,
                type = typeEgg?.let { resourceProvider.getString(it.toResId()) } ?: chicks.type,
                date = chicks.date,
                dateFactory = null,
                group = true,
                sex = false,
                note = if (chicks.breed.isBlank()) ""
                else resourceProvider.getString(R.string.incubator_chicks_breed_note).format(chicks.breed),
                currentIcon = typeEgg?.toAnimalIcon(),
                archive = false,
                foodDay = 0.0,
                foodDaySuffix = Suffix.GRAM_DAY,
                idPT = idPT,
            )
        )
        animalCountRepository.insertAnimalCountTable(
            DomainAnimalCount(
                count = chicks.count.toString(),
                suffix = Suffix.PIECES,
                date = chicks.date,
                note = resourceProvider.getString(R.string.bookmark_screen_expenses_animal_note)
                    .format(chicks.name),
                version = AnimalCountVersion.INCUBATOR,
                idAnimal = idAnimal,
            )
        )
    }
}
