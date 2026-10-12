package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.group.AnimalAutoName
import com.zaroslikov.domain.models.animal.group.MAX_SPLIT
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.domain.models.animal.pregnancy.SpeciesPregnancyNorms
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.StartForm
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.isSireError
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.matingDateError
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import java.time.LocalDate
import kotlin.math.floor

data class AnimalCardState(
    val isOpenArchiveDialog: Boolean = false,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val weight: DomainAnimalWeight? = DomainAnimalWeight(),
    val size: DomainAnimalSize? = DomainAnimalSize(),
    val countAnimal: DomainAnimalCount? = DomainAnimalCount(),
    val vaccination: DomainAnimalVaccination? = DomainAnimalVaccination(),
    val productList: List<BrieflyItem> = emptyList(),
    val age: String = "",
    val price: Double? = 0.0,
    val buyerList: List<String> = emptyList(),
    val itemIdPT: Long = 0,
    val itemId: Long = 0,
    val settings: DomainSettings = DomainSettings(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false,
    /** Сводка по этому животному (счётчик, сборная группа) из getAnimalWithGroup. */
    val summary: AnimalListUi? = null,
    /** Имена всех животных проекта — для автоимён при выделении. */
    val projectAnimalNames: List<String> = emptyList(),
    val split: SplitDialogState = SplitDialogState(),
    val isOpenLeaveDialog: Boolean = false,
    val isGroupOperationRunning: Boolean = false,
    /** Беременности уже прочитаны — до этого кнопку «Отметить случку» не показываем. */
    val isPregnancyLoaded: Boolean = false,
    /** Активная беременность самки и её прогресс на сегодня. */
    val activePregnancy: DomainAnimalPregnancy? = null,
    val pregnancyProgress: PregnancyProgress? = null,
    /** Нормы вида активной беременности (снимок вида на момент случки). */
    val pregnancyNorms: SpeciesPregnancyNorms? = null,
    /** Завершённые (не активные) беременности. */
    val pregnancyHistoryCount: Int = 0,
    val mother: DomainAnimalTable? = null,
    val father: DomainAnimalTable? = null,
    val children: List<DomainAnimalTable> = emptyList(),
    /** Диалог «Прервать беременность?» при архивации беременной самки. */
    val isOpenArchivePregnancyDialog: Boolean = false,
    /** Лист «Случка» прямо в карточке. */
    val pregnancyStart: StartForm = StartForm(),
    val sireCandidates: List<DomainAnimalTable> = emptyList(),
    val isPregnancySaving: Boolean = false,
    /** Лист «Мать»/«Отец»/«Потомок» из родословной: подробности без перехода в карточку; null — закрыт. */
    val pedigreeSheet: PedigreeSheet? = null,
    val pedigreeSheetAnimal: DomainAnimalTable? = null,
    val pedigreeSheetChildrenCount: Int = 0,
    val isPedigreeSheetLoaded: Boolean = false
) : BaseState {

    /** Отец не из хозяйства: только имя. */
    val externalFatherName: String?
        get() = animal.fatherName?.takeIf { animal.fatherId == null && it.isNotBlank() }

    /** «Не указан» — листа нет. */
    val isFatherKnown: Boolean get() = animal.fatherId != null || externalFatherName != null

    /** Мать в хозяйстве или удалена (остался снимок имени); «Не указана» — листа нет. */
    val isMotherKnown: Boolean get() = animal.motherId != null || !animal.motherName.isNullOrBlank()

    fun canOpen(sheet: PedigreeSheet): Boolean = when (sheet) {
        PedigreeSheet.Mother -> isMotherKnown
        PedigreeSheet.Father -> isFatherKnown
        is PedigreeSheet.Child -> children.any { it.id == sheet.id }
    }

    /** id животного открытого листа в хозяйстве; null — читать нечего (чужой отец, удалённая мать). */
    fun pedigreeSheetAnimalId(sheet: PedigreeSheet): Long? = when (sheet) {
        PedigreeSheet.Mother -> animal.motherId
        PedigreeSheet.Father -> animal.fatherId
        is PedigreeSheet.Child -> sheet.id
    }
    val isGroup: Boolean get() = animal.group

    /** Секция «Беременность» — только самкам-млекопитающим (не группам). */
    val pregnancyApplicable: Boolean get() = PregnancyRules.isApplicable(animal)

    /** Можно отметить случку: нет активной, ни животное, ни проект не в архиве. */
    val canStartPregnancy: Boolean
        get() = isPregnancyLoaded && !isArchive &&
                PregnancyRules.startBlock(animal, activePregnancy != null) == null

    /** Пустую секцию (нечего показать и случку отметить нельзя) не выводим. */
    val isPregnancySectionVisible: Boolean
        get() = pregnancyApplicable &&
                (activePregnancy != null || canStartPregnancy || pregnancyHistoryCount > 0)

    val matingDateError: PregnancyRules.DateError?
        get() = pregnancyStart.matingDateError(animal, LocalDate.now())

    val isSireError: Boolean get() = pregnancyStart.isSireError(sireCandidates)

    val canConfirmPregnancyStart: Boolean
        get() = !isPregnancySaving && canStartPregnancy && matingDateError == null && !isSireError

    val hasPedigree: Boolean
        get() = animal.motherId != null || animal.motherName != null || animal.fatherId != null ||
                animal.fatherName != null || children.isNotEmpty()
    /** Животное или обезличенная группа состоит в сборной группе. */
    val isMember: Boolean get() = animal.herdId != null

    /** Сколько особей можно выделить из счётчика обезличенной группы (не больше [MAX_SPLIT] за раз). */
    val splitMax: Int get() = minOf(floor(maxOf(summary?.ownCount ?: 0.0, 0.0)).toInt(), MAX_SPLIT)

    val splitCount: Int? get() = split.count.toIntOrNull()

    val isSplitCountError: Boolean
        get() {
            if (split.count.isBlank()) return false
            val n = splitCount ?: return true
            return n < 1 || n > splitMax
        }

    val splitNames: List<String>
        get() {
            val n = splitCount ?: return emptyList()
            if (isSplitCountError || split.prefix.isBlank()) return emptyList()
            return AnimalAutoName.generate(split.prefix.trim(), projectAnimalNames, n)
        }

    val canConfirmSplit: Boolean
        get() = !isGroupOperationRunning && splitNames.isNotEmpty()
}

/** Чей лист открыт в родословной. */
sealed interface PedigreeSheet {
    data object Mother : PedigreeSheet
    data object Father : PedigreeSheet
    data class Child(val id: Long) : PedigreeSheet
}

data class SplitDialogState(
    val isOpen: Boolean = false,
    val count: String = "",
    val prefix: String = "",
    val sex: Boolean = true
)

data class PregnancyCardData(
    val active: DomainAnimalPregnancy?,
    val historyCount: Int,
    val children: List<DomainAnimalTable>,
    val mother: DomainAnimalTable?,
    val father: DomainAnimalTable?
)

data class LoadDataAnimalCard(
    val animal: DomainAnimalTable,
    val count: DomainAnimalCount?,
    val size: DomainAnimalSize?,
    val vacc: DomainAnimalVaccination?,
    val weight: DomainAnimalWeight?,
    val productList: List<BrieflyItem>,
    val settings: DomainSettings
)
