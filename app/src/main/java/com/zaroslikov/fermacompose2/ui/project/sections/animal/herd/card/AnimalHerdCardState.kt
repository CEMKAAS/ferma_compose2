package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdUi
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem

/**
 * Карточка сборной группы: итоги, продукция, примечание и действия с группой (обезличить,
 * расформировать). Состав и поголовье меняются на экране корректировки
 * ([com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.count.AnimalHerdCountScreen]).
 */
data class AnimalHerdCardState(
    val herd: AnimalHerdUi = AnimalHerdUi(),
    /** null — никто из членов не взвешен. */
    val weight: HerdWeight? = null,
    /** Итог по каждому виду продукции. */
    val products: List<BrieflyItem> = emptyList(),
    /** Вид продукции → члены, от которых она получена. */
    val productsByMember: Map<String, List<HerdMemberProduct>> = emptyMap(),
    /** Вид продукции, выбранный для разбивки по животным. */
    val selectedProduct: String? = null,
    /** Хозяйство в архиве — только просмотр. */
    val isArchive: Boolean = false,
    val isOpenDepersonalizeDialog: Boolean = false,
    val isOpenDisbandDialog: Boolean = false,
    val isOperationRunning: Boolean = false,
    /**
     * Группа расформирована или опустела — экран надо закрыть. Флаг в состоянии, а не событие:
     * событие потерялось бы, пока сверху открыта карточка члена.
     */
    val isClosed: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState {
    /** Индивидуальные члены — их обезличивание переводит в счётчики обезличенных групп. */
    val individualsCount: Int get() = herd.members.count { it.sex != null }
}
