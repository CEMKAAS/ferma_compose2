package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card.HeadsChip
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card.HerdHeadsChips
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal

/** Размер главной иконки в карточках списка — как у [IconAnimal] по умолчанию. */
private val LIST_ICON_SIZE = 64.dp

/**
 * Сборная группа в списке животных: название и поголовье с разбивкой по полу.
 * Слева — своя иконка/фото группы, если пользователь её задал, иначе стопка иконок членов
 * на месте одной иконки.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimalHerdListCard(herd: AnimalHerdUi, onClick: () -> Unit) {
    CardFieldNew(onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (herd.imagePath != null || herd.currentIcon != null)
                IconAnimal(
                    sex = null,
                    imagePath = herd.imagePath,
                    currentIcon = herd.currentIcon ?: AppIcon.PETS
                )
            else MembersIconDeck(members = herd.members)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = herd.name,
                    style = textBold_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Поголовье с иконкой, за ним — пол, пол не известен и беременные
                FlowRow(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HeadsChip(
                        colors = listOf(animal_1, animal_2),
                        icon = R.drawable.baseline_spoke_24,
                        text = "${herd.totalCount.formatNumber(isGroupingUsed = false)} " +
                                stringResource(herd.totalSuffix.toResId())
                    )
                    HerdHeadsChips(herd)
                }
            }
        }
    }
}

/**
 * Иконки членов «колодой» на месте одной иконки [size]: первая — спереди слева сверху,
 * следующие выглядывают снизу справа. Больше трёх — серый кружок «+N» в углу.
 */
@Composable
private fun MembersIconDeck(members: List<AnimalListUi>, size: Dp = LIST_ICON_SIZE) {
    val visible = members.take(DECK_VISIBLE)
    val rest = members.size - visible.size
    // Один член — обычная иконка; иначе иконки меньше на сдвиг колоды
    val step = if (visible.size > 1) DECK_SPREAD / (visible.size - 1) else 0.dp
    val iconSize = if (visible.size > 1) size - DECK_SPREAD else size
    Box(modifier = Modifier.size(size)) {
        visible.forEachIndexed { index, member ->
            IconAnimal(
                modifier = Modifier
                    .offset(x = step * index, y = step * index)
                    .zIndex((visible.size - index).toFloat()),
                sizeIcon = iconSize,
                sex = member.sex,
                imagePath = member.imagePath,
                currentIcon = member.currentIcon
            )
        }
        if (rest > 0)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .zIndex(visible.size + 1f)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(gray_6),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+$rest", style = text_12, fontWeight = FontWeight.SemiBold, color = marengo, maxLines = 1)
            }
    }
}

/** Сколько иконок членов в колоде и на сколько сдвинута последняя относительно первой. */
private const val DECK_VISIBLE = 3
private val DECK_SPREAD = 16.dp
