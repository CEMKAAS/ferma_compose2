package com.zaroslikov.fermacompose2.ui.project.sections.animal.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.ButtonForGroupButtons
import com.zaroslikov.fermacompose2.ui.elements.CustomCheckbox
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.AnimalNameOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseExposedDropdownMenu2
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal
import com.zaroslikov.fermacompose2.white

/** Животное или обезличенная группа, которых можно объединить в сборную группу. */
data class MergeCandidate(
    val id: Long,
    val name: String,
    val type: String,
    /** null — обезличенная группа (вместо пола показываем поголовье). */
    val sex: Boolean?,
    val count: Double = 0.0,
    val countSuffix: Suffix = Suffix.PIECES,
    val imagePath: String?,
    val currentIcon: AppIcon?
)

/** Существующая сборная группа для выбора. */
data class MergeGroupChoice(
    val id: Long,
    val name: String
)

fun DomainAnimalWithCount.toMergeCandidate() = MergeCandidate(
    id = id,
    name = name,
    type = type,
    sex = if (group) null else sex,
    count = totalCount,
    countSuffix = countSuffix,
    imagePath = imagePath,
    currentIcon = currentIcon
)

fun DomainAnimalHerd.toMergeGroupChoice() = MergeGroupChoice(id = id, name = name)

/**
 * Состояние листа объединения в сборную группу.
 * [fixedGroupId] != null — режим «только добавить» в заданную сборную группу (её карточка):
 * переключатель Новая/Существующая и выбор группы скрыты.
 */
data class MergeGroupState(
    val isOpen: Boolean = false,
    val candidates: List<MergeCandidate> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val isNewGroup: Boolean = true,
    val groups: List<MergeGroupChoice> = emptyList(),
    val chosenGroupId: Long? = null,
    val fixedGroupId: Long? = null,
    val name: String = "",
    val isSaving: Boolean = false
) {
    val isAddOnly: Boolean get() = fixedGroupId != null

    /** Сборная группа, в которую добавляем (для режима существующей группы). */
    val targetGroupId: Long? get() = fixedGroupId ?: chosenGroupId.takeIf { !isNewGroup }

    val canConfirm: Boolean
        get() = !isSaving && selectedIds.isNotEmpty() && when {
            isAddOnly -> true
            isNewGroup -> name.isNotBlank()
            else -> chosenGroupId != null
        }

    fun toggle(id: Long) = copy(
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    )

    fun changeMode(isNewGroup: Boolean) = copy(
        isNewGroup = if (groups.isEmpty()) true else isNewGroup
    )

    fun chooseGroup(id: Long) = copy(chosenGroupId = id)
    fun changeName(name: String) = copy(name = name)
}

@Composable
fun MergeAnimalsBottomSheet(
    state: MergeGroupState,
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onCandidateToggled: (Long) -> Unit,
    onModeChanged: (isNewGroup: Boolean) -> Unit,
    onGroupChosen: (Long) -> Unit,
    onNameChanged: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(
            if (state.isAddOnly) R.string.animal_group_button_add_members
            else R.string.animal_group_merge_title
        ),
        supText = stringResource(R.string.animal_group_merge_support),
        iconRes = R.drawable.ic_herd,
        colors = colors,
        onDismissRequest = onDismissRequest,
        contentBottom = if (state.candidates.isEmpty()) null else {
            {
                GradientButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = colors,
                    text = stringResource(
                        if (state.isAddOnly) R.string.animal_group_button_add_members
                        else R.string.animal_group_merge_button
                    ),
                    prefixIconRes = R.drawable.ic_herd,
                    enabled = state.canConfirm,
                    paddingValues = PaddingValues(16.dp),
                    onClick = onConfirm
                )
            }
        }
    ) {
        if (state.candidates.isEmpty()) {
            Text(
                text = stringResource(R.string.animal_group_merge_no_candidates),
                style = text_14,
                color = marengo,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            return@BaseBottomSheet
        }

        if (!state.isAddOnly && state.groups.isNotEmpty())
            GroupButton(modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val (newText, newBack) = if (state.isNewGroup) white to colors.last()
                    else marengo to gray_6
                    val (oldText, oldBack) = if (!state.isNewGroup) white to colors.last()
                    else marengo to gray_6
                    ButtonForGroupButtons(
                        modifier = Modifier.weight(1f),
                        text = R.string.animal_group_merge_new,
                        backgroundColor = newBack,
                        textColor = newText
                    ) { onModeChanged(true) }
                    ButtonForGroupButtons(
                        modifier = Modifier.weight(1f),
                        text = R.string.animal_group_merge_existing,
                        backgroundColor = oldBack,
                        textColor = oldText
                    ) { onModeChanged(false) }
                }
            }

        if (!state.isAddOnly) {
            if (state.isNewGroup || state.groups.isEmpty()) {
                AnimalNameOutlinedTextNew(
                    value = state.name,
                    onValueChange = onNameChanged,
                    isAnimalGroup = true,
                    isErrorTitle = false
                )
            } else {
                // Выбор по id: у разных групп могут совпадать имена
                val chosenName = state.groups.find { it.id == state.chosenGroupId }?.name ?: ""
                BorderCard {
                    BaseExposedDropdownMenu2(
                        title = chosenName,
                        labelSelector = { it.name },
                        list = state.groups,
                        content = {
                            BaseOutlinedTextNew(
                                modifier = it.first,
                                value = chosenName,
                                onValueChange = {},
                                leadingIconRes = R.drawable.ic_herd,
                                singleLine = false,
                                labelIntRes = R.string.animal_group_merge_existing,
                                intResSup = R.string.animal_group_merge_support,
                                keyboardOptions = keyboardOptionsNext(),
                                readOnly = true,
                                isMore = chosenName.isBlank()
                            )
                        }
                    ) { _, group, closeMenu ->
                        DropdownMenuItem(
                            text = { Text(text = group.name) },
                            trailingIcon = if (group.id == state.chosenGroupId) {
                                { Icon(Icons.Default.Done, contentDescription = null) }
                            } else null,
                            onClick = {
                                onGroupChosen(group.id)
                                closeMenu()
                            }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.candidates.forEach { candidate ->
                MergeCandidateRow(
                    candidate = candidate,
                    checked = candidate.id in state.selectedIds,
                    onToggle = { onCandidateToggled(candidate.id) }
                )
            }
        }
    }
}

@Composable
private fun MergeCandidateRow(
    candidate: MergeCandidate,
    checked: Boolean,
    onToggle: () -> Unit
) {
    BorderCard(padding = PaddingValues(12.dp), onClick = onToggle) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomCheckbox(checked = checked, onCheckedChange = { onToggle() })
            IconAnimal(
                sizeIcon = 40.dp,
                sex = candidate.sex,
                imagePath = candidate.imagePath,
                currentIcon = candidate.currentIcon ?: AppIcon.PETS
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = candidate.name,
                    style = text_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // У обезличенной группы рядом с типом — поголовье
                Text(
                    text = if (candidate.sex == null)
                        "${candidate.type} · ${candidate.count.formatNumber(isGroupingUsed = false)} " +
                                stringResource(candidate.countSuffix.toResId())
                    else candidate.type,
                    style = text_14,
                    color = marengo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
