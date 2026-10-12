package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.count

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.DomainAnimalCountPriceUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi

/**
 * Корректировка поголовья сборной группы: продажа, списание и забой отмеченных членов,
 * добавление нового животного (с ценой — покупка) и существующих в группу, вывод из группы,
 * история поголовья. Обезличивание и расформирование — в карточке группы.
 */
data class AnimalHerdCountState(
    val herd: AnimalHerdUi = AnimalHerdUi(),
    /** Записи счётчиков членов с меткой группы, новые первыми. */
    val history: List<HerdHistoryItem> = emptyList(),
    val settings: DomainSettings = DomainSettings(),
    /** Хозяйство в архиве — только просмотр. */
    val isArchive: Boolean = false,
    /**
     * Лист продажи, списания, забоя. Поля (дата, цена, покупатель, продукция забоя) — те же,
     * что у счётчика животного, поэтому состояние листа — [AnimalCountState]; количество = сумма [picks].
     */
    val entry: AnimalCountState = AnimalCountState(isLoading = false),
    /** Член → сколько голов затрагивает операция (текст поля); у индивидуального животного — всё его поголовье. */
    val picks: Map<Long, String> = emptyMap(),
    /** Обезличенная группа в листе → сколько из её количества самок и самцов (тексты полей). */
    val pickSex: Map<Long, Pair<String, String>> = emptyMap(),
    /**
     * Лист «Добавить» — та же форма, что при добавлении животного в список, поэтому его состояние —
     * [AnimalListState] (используются openBottomSheetEntry и currentProduct).
     */
    val animalEntry: AnimalListState = AnimalListState(isLoading = false),
    /** Лист «Добавить в группу». */
    val merge: MergeGroupState = MergeGroupState(),
    /** Отмеченные в составе члены: для них — продажа, списание, забой, вывод из группы. */
    val selected: Set<Long> = emptySet(),
    /** Открыт диалог «Вывести из группы» для отмеченных. */
    val isOpenLeaveDialog: Boolean = false,
    val isOperationRunning: Boolean = false,
    /** Группа расформирована или опустела — экран надо закрыть (см. карточку группы). */
    val isClosed: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState {
    /** Самки с активной беременностью. */
    val pregnantCount: Int get() = herd.pregnant

    /**
     * Головы, пол которых не известен: пока это поголовье обезличенных групп
     * (пол у них общий на группу, а не по головам).
     */
    val unknownSexCount: Double get() = herd.unknownSex

    /** Отмеченные члены, в порядке состава. */
    val selectedMembers: List<AnimalListUi> get() = herd.members.filter { it.id in selected }

    val version: AnimalCountVersion get() = entry.currentProduct.version

    /** Члены в листе операции — отмеченные в составе: добавить в листе нельзя, можно лишь убрать. */
    val pickable: List<AnimalListUi> get() = herd.members.filter { it.id in picks && it.totalCount > 0 }

    /** Сумма по тем, кто остался в листе: её же и сохранит операция. */
    val pickedTotal: Double get() = pickable.sumOf { picks[it.id]?.toCountOrZero() ?: 0.0 }

    /** Ошибка количества у члена: пусто, не число, ноль или больше его поголовья. */
    fun isPickError(member: AnimalListUi): Boolean {
        val count = picks[member.id]?.toCountOrNull() ?: return member.id in picks
        return count <= 0 || count > member.totalCount
    }

    /**
     * Ошибка пола у обезличенной группы: самок или самцов больше, чем в группе, или вместе
     * больше выбранного количества.
     */
    fun isPickSexError(member: AnimalListUi): Boolean {
        val (females, males) = pickSexCounts(member.id)
        val count = picks[member.id]?.toCountOrNull() ?: 0.0
        return females + males > count || females > member.femalesCount || males > member.malesCount
    }

    fun pickSexCounts(id: Long): Pair<Int, Int> =
        pickSex[id]?.let { (f, m) -> (f.toIntOrNull() ?: 0) to (m.toIntOrNull() ?: 0) } ?: (0 to 0)

    /** Выбран хоть кто-то, количества и пол корректны. */
    val isPicksValid: Boolean
        get() = pickedTotal > 0 && pickable.none { isPickError(it) || isPickSexError(it) }
}

/** Запись истории группы: запись счётчика члена и его имя. */
data class HerdHistoryItem(
    val record: DomainAnimalCountPriceUi,
    val animalName: String
)

internal fun String.toCountOrNull(): Double? =
    replace(',', '.').trim().takeIf { it.isNotEmpty() }?.toDoubleOrNull()?.takeIf { it >= 0 }

internal fun String.toCountOrZero(): Double = toCountOrNull() ?: 0.0

/** Количество для поля и записи: целое — без «.0». */
internal fun Double.toCountText(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()
