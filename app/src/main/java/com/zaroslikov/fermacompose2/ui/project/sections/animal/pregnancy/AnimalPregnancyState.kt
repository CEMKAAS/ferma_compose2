package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.group.AnimalAutoName
import com.zaroslikov.domain.models.animal.pregnancy.LitterMode
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyBlock
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyPhase
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.domain.models.animal.pregnancy.SpeciesPregnancyNorms
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Откуда отец: самец из хозяйства, чужой производитель (имя) или не указан. */
enum class SireMode { FARM, EXTERNAL, NONE }

data class AnimalPregnancyState(
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val itemIdPT: Long = 0,
    val itemId: Long = 0,
    val today: LocalDate = LocalDate.now(),
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val norms: SpeciesPregnancyNorms? = null,
    val active: DomainAnimalPregnancy? = null,
    val progress: PregnancyProgress? = null,
    /** Все беременности, кроме [active]; новые сверху. */
    val history: List<DomainAnimalPregnancy> = emptyList(),
    val sireCandidates: List<DomainAnimalTable> = emptyList(),
    val projectAnimalNames: List<String> = emptyList(),
    /** Только просмотр: проект в архиве. Архив самки ограничивает лишь случку и роды. */
    val isReadOnly: Boolean = false,
    val startBlock: PregnancyBlock? = null,
    /** Шаблон имени группы-помёта «%1$s (%2$s, %3$s)» — подставляется ViewModel из ресурсов. */
    val groupNameTemplate: String = "",
    val start: StartForm = StartForm(),
    val birth: BirthForm = BirthForm(),
    val interrupt: InterruptForm = InterruptForm(),
    val deleteId: Long? = null,
    /** Правка заметки записи из истории: id → текст. */
    val editNote: Pair<Long, String>? = null,
    /** Заметки по дням: id беременности → заметки, новые сверху. */
    val dayNotes: Map<Long, List<DomainAnimalPregnancyNote>> = emptyMap(),
    val dayNote: DayNoteForm = DayNoteForm(),
    val isSaving: Boolean = false,
    /** Лист «Отец»: подробности о самце активной беременности. */
    val isSireSheetOpen: Boolean = false,
    val sireDetails: DomainAnimalTable? = null,
    val sireChildrenCount: Int = 0,
    val isSireDetailsLoaded: Boolean = false
) : BaseState {

    /** Сколько случек с этим же отцом было у самки (по id самца или имени чужого производителя). */
    val sireMatingsCount: Int
        get() {
            val sire = active ?: return 0
            return pregnancyById.values.count {
                if (sire.sireId != null) it.sireId == sire.sireId
                else !sire.sireName.isNullOrBlank() && it.sireId == null &&
                        it.sireName.equals(sire.sireName, ignoreCase = true)
            }
        }

    val isFemaleArchived: Boolean get() = animal.archive

    /** Случку можно отметить: правила вида/пола/архива/активной + проект не в архиве. */
    val canStart: Boolean get() = !isReadOnly && startBlock == null && norms != null

    /** Роды — только у неархивной самки. */
    val canBirth: Boolean get() = !isReadOnly && active != null && !isFemaleArchived && norms != null

    /** Прервать и удалить можно и у архивной самки. */
    val canInterrupt: Boolean get() = !isReadOnly && active != null

    val canEdit: Boolean get() = !isReadOnly

    /** Шестерёнка в заголовке: правка/удаление активной беременности. */
    val canEditActive: Boolean get() = canEdit && active != null

    /** Единственная кнопка действия: роды, когда срок уже допускает их, иначе — прервать. */
    val isBirthAction: Boolean
        get() = canBirth && progress != null && progress.phase != PregnancyPhase.IN_PROGRESS

    val pregnancyById: Map<Long, DomainAnimalPregnancy>
        get() = (listOfNotNull(active) + history).associateBy { it.id }

    // ---------- Случка / правка ----------

    val matingDateError: PregnancyRules.DateError?
        get() = start.matingDateError(animal, today)

    /** Для завершённой записи меняется только заметка. */
    val isEditingFinished: Boolean
        get() = start.editingId != null && pregnancyById[start.editingId]?.status != PregnancyStatus.ACTIVE

    val isSireError: Boolean
        get() = start.isSireError(sireCandidates, editingSireId)

    /**
     * Самец редактируемой записи. Считается допустимым, даже если больше не подходит
     * (перевели в группу, сменили пол/вид) — DAO оставляет его вместе со снимком имени.
     */
    val editingSireId: Long?
        get() = start.editingId?.let { pregnancyById[it]?.sireId }

    /** Снимок имени самца редактируемой записи — если он выбран, но уже не в списке кандидатов. */
    val editingSireName: String?
        get() = start.editingId
            ?.let { pregnancyById[it] }
            ?.takeIf { it.sireId != null && it.sireId == start.sireId }
            ?.sireName

    val selectedSire: DomainAnimalTable?
        get() = start.sireId?.let { id -> sireCandidates.firstOrNull { it.id == id } }

    val canConfirmStart: Boolean
        get() = !isSaving && when {
            isEditingFinished -> true
            else -> matingDateError == null && !isSireError
        }

    // ---------- Роды ----------

    val birthDate: LocalDate? get() = PregnancyDates.parseOrNull(birth.date)

    val activeMatingDate: LocalDate? get() = active?.let { PregnancyDates.parseOrNull(it.matingDate) }

    val birthDateError: PregnancyRules.DateError?
        get() = endDateError(birthDate)

    val bornFemale: Int? get() = birth.female.toCountOrNull()
    val bornMale: Int? get() = birth.male.toCountOrNull()
    val bornStillborn: Int? get() = birth.stillborn.toCountOrNull()

    val isFemaleCountError: Boolean get() = bornFemale == null
    val isMaleCountError: Boolean get() = bornMale == null
    val isStillbornCountError: Boolean get() = bornStillborn == null

    val bornAlive: Int get() = (bornFemale ?: 0) + (bornMale ?: 0)

    val isBirthEmpty: Boolean get() = bornAlive + (bornStillborn ?: 0) < 1

    val isLitterTooBig: Boolean get() = bornAlive > PregnancyRules.MAX_LITTER

    /** Имена отдельных особей: сначала самки, затем самцы. */
    val birthNames: List<String>
        get() {
            if (birth.mode != LitterMode.INDIVIDUALS || isLitterTooBig) return emptyList()
            val prefix = birth.prefix.trim()
            if (prefix.isEmpty() || bornAlive < 1) return emptyList()
            return AnimalAutoName.generate(prefix, projectAnimalNames, bornAlive)
        }

    /** День беременности, на который пришлись роды, если раньше нормы (предупреждение, не блокирует). */
    val pretermDays: Int?
        get() {
            val mating = activeMatingDate ?: return null
            val date = birthDate ?: return null
            val min = norms?.minDays ?: return null
            val days = ChronoUnit.DAYS.between(mating, date).toInt() + 1
            return days.takeIf { it in 1 until min }
        }

    val isPreterm: Boolean get() = pretermDays != null

    val canConfirmBirth: Boolean
        get() = !isSaving && canBirth && birthDateError == null &&
                !isFemaleCountError && !isMaleCountError && !isStillbornCountError &&
                !isBirthEmpty && !isLitterTooBig &&
                (bornAlive == 0 || when (birth.mode) {
                    LitterMode.INDIVIDUALS -> birthNames.size == bornAlive
                    LitterMode.GROUP -> birth.groupName.isNotBlank()
                })

    // ---------- Заметка за день ----------

    /** FAB «Заметка»: заметку за сегодня пишем к активной беременности. */
    val canAddDayNote: Boolean get() = canEdit && active != null

    val dayNotePregnancy: DomainAnimalPregnancy? get() = pregnancyById[dayNote.pregnancyId]

    val dayNoteDateError: PregnancyRules.DateError?
        get() {
            val p = dayNotePregnancy ?: return null
            val date = PregnancyDates.parseOrNull(dayNote.date) ?: return PregnancyRules.DateError.INVALID
            return PregnancyRules.noteDateError(
                date = date,
                mating = PregnancyDates.parseOrNull(p.matingDate) ?: date,
                end = p.endDate?.let { PregnancyDates.parseOrNull(it) },
                today = today
            )
        }

    /** День беременности для даты в форме; null — дата не читается или вне срока. */
    val dayNoteDay: Int?
        get() {
            if (dayNoteDateError != null) return null
            val mating = dayNotePregnancy?.let { PregnancyDates.parseOrNull(it.matingDate) } ?: return null
            return PregnancyDates.parseOrNull(dayNote.date)?.let { PregnancyCalculator.dayOf(mating, it) }
        }

    val isDayNoteEmpty: Boolean get() = dayNote.text.isBlank()

    val canConfirmDayNote: Boolean
        get() = !isSaving && canEdit && dayNotePregnancy != null &&
                dayNoteDateError == null && !isDayNoteEmpty

    // ---------- Прерывание ----------

    val interruptDateError: PregnancyRules.DateError?
        get() = endDateError(PregnancyDates.parseOrNull(interrupt.date))

    val isInterruptNoteRequired: Boolean
        get() = interrupt.reason == PregnancyInterruptReason.OTHER && interrupt.note.isBlank()

    val canConfirmInterrupt: Boolean
        get() = !isSaving && canInterrupt && interrupt.reason != null &&
                interruptDateError == null && !isInterruptNoteRequired

    private fun endDateError(date: LocalDate?): PregnancyRules.DateError? {
        val mating = activeMatingDate ?: return null
        val end = date ?: return PregnancyRules.DateError.INVALID
        return PregnancyRules.endDateError(end, mating, today)
    }

    private fun String.toCountOrNull(): Int? {
        val value = trim()
        if (value.isEmpty()) return 0
        return value.toIntOrNull()?.takeIf { it >= 0 }
    }
}

data class StartForm(
    val isOpen: Boolean = false,
    /** null — новая случка; иначе id редактируемой записи. */
    val editingId: Long? = null,
    val matingDate: String = "",
    val sireMode: SireMode = SireMode.NONE,
    val sireId: Long? = null,
    val sireName: String = "",
    val note: String = ""
)

data class BirthForm(
    val isOpen: Boolean = false,
    val date: String = "",
    val female: String = "",
    val male: String = "",
    val stillborn: String = "",
    val mode: LitterMode = LitterMode.INDIVIDUALS,
    val prefix: String = "",
    val groupName: String = "",
    /** Имя группы правили руками — при смене даты не перегенерировать. */
    val isGroupNameEdited: Boolean = false,
    val note: String = ""
)

data class DayNoteForm(
    val isOpen: Boolean = false,
    /** null — новая заметка; иначе id редактируемой. */
    val editingId: Long? = null,
    val pregnancyId: Long = 0,
    val date: String = "",
    /** `HH:mm` или пусто — время не указано. */
    val time: String = "",
    val text: String = "",
    /** Открыто подтверждение удаления редактируемой заметки. */
    val isDeleteConfirm: Boolean = false
)

data class InterruptForm(
    val isOpen: Boolean = false,
    val date: String = "",
    val reason: PregnancyInterruptReason? = null,
    val note: String = ""
)
