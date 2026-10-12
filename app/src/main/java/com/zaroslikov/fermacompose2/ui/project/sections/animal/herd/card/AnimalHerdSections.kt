package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.zaroslikov.fermacompose2.animal_2
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_13
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.ExpandableRows
import com.zaroslikov.fermacompose2.ui.elements.ProductKillInfoCard
import com.zaroslikov.fermacompose2.ui.elements.SecondAnimalCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseExposedDropdownMenu2
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.GroupButton
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.PregnancyHeartCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.PregnancyRingIcon
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem

/** Сколько иконок членов показываем в шапке; остальные — кругляш «+N». */
private const val HEADER_VISIBLE_MEMBERS = 3
private val headerIconSize = 56.dp
/** Иконки почти целиком под соседней: виден край 14dp с белой обводкой — стопка занимает мало места. */
private val headerIconOverlap = 42.dp

/**
 * Шапка сборной группы: её фото (если задано) или стопка иконок членов, название, виды членов,
 * количество голов (с разбивкой по полу индивидуальных животных) и суммарный расход корма.
 * Поголовье — только итог по членам, не редактируется.
 */
@Composable
fun AnimalHerdHeader(herd: AnimalHerdUi, weight: HerdWeight?) {
    CardFieldNew {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Своя иконка или фото группы — если пользователь их задал, иначе стопка иконок членов
                if (herd.imagePath != null || herd.currentIcon != null)
                    IconAnimal(
                        sizeIcon = headerIconSize,
                        sex = null,
                        imagePath = herd.imagePath,
                        currentIcon = herd.currentIcon ?: AppIcon.PETS
                    )
                else MembersIconStack(members = herd.members)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = herd.name,
                        style = textBold_20,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (herd.typesLabel.isNotEmpty())
                        Text(
                            text = herd.typesLabel,
                            style = text_14,
                            color = marengo,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AnimalParameter(
                    titleParameter = R.string.animal_composite_heads,
                    parameter = "${herd.totalCount.formatNumber(isGroupingUsed = false)} " +
                            stringResource(herd.totalSuffix.toResId()),
                    icon = R.drawable.baseline_spoke_24,
                    iconColor = animal_1,
                    iconColorSecond = green_15,
                    trailingValue = if (herd.hasHeadsChips) {
                        { HerdHeadsChips(herd) }
                    } else null
                )
                AnimalParameter(
                    titleParameter = R.string.animal_herd_weight,
                    parameter = weight?.let { it.text() } ?: stringResource(R.string.animal_herd_weight_empty),
                    icon = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                    iconColor = blue_1,
                    iconColorSecond = blue_3
                )
                AnimalParameter(
                    titleParameter = R.string.animal_composite_food,
                    parameter = herd.totalFoodText(),
                    icon = R.drawable.outline_restaurant_24,
                    iconColor = Color(0xFFD08700),
                    iconColorSecond = orang_13
                )
            }
        }
    }
}

/** «средний 450 кг · общий 1 350 кг», если взвешены не все — «(взвешено 3 из 5)». */
@Composable
private fun HerdWeight.text(): String {
    val unit = stringResource(suffix.toResId())
    val value = stringResource(
        R.string.animal_herd_weight_value,
        "${average.formatNumber()} $unit",
        "${total.formatNumber()} $unit"
    )
    return if (weighed < members) "$value " + stringResource(R.string.animal_herd_weight_partial, weighed, members)
    else value
}

/** Граммы и килограммы через «·»; корм нигде не указан — «Не указан». */
@Composable
private fun AnimalHerdUi.totalFoodText(): String {
    if (totalFood.isEmpty()) return stringResource(R.string.animal_composite_food_empty)
    val context = LocalContext.current
    return totalFood.entries.joinToString(" · ") { (suffix, value) ->
        "${value.formatNumber()} ${context.getString(suffix.toResId())}"
    }
}

/** Иконки членов внахлёст (левая сверху); больше трёх — серый кругляш «+N». */
@Composable
private fun MembersIconStack(members: List<AnimalListUi>, size: Dp = headerIconSize) {
    val visible = members.take(HEADER_VISIBLE_MEMBERS)
    val rest = members.size - visible.size
    Row(horizontalArrangement = Arrangement.spacedBy(-headerIconOverlap)) {
        visible.forEachIndexed { index, member ->
            IconAnimal(
                modifier = Modifier.zIndex((visible.size - index + 1).toFloat()),
                sizeIcon = size,
                sex = member.sex,
                imagePath = member.imagePath,
                currentIcon = member.currentIcon
            )
        }
        if (rest > 0)
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(gray_6),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+$rest", style = text_14, fontWeight = FontWeight.SemiBold, color = marengo)
            }
    }
}

/** Пол индивидуальных животных с количеством: «♀ 3», «♂ 0». */
@Composable
internal fun SexCountChip(sex: Boolean, count: Int) {
    val (colors, icon) = if (sex)
        listOf(Color(0xFF2B7FFF), Color(0xFF615FFF)) to R.drawable.baseline_male_24
    else listOf(Color(0xFFF6339A), Color(0xFFFF2056)) to R.drawable.baseline_female_24
    HeadsChip(colors, icon, count.toString())
}

internal val PREGNANT_COLORS = listOf(Color(0xFFFF2056), Color(0xFFE7000B))
internal val UNKNOWN_COLORS = listOf(Color(0xFF6A7282), Color(0xFF99A1AF))

/**
 * Разбивка поголовья плашками: ♀ и ♂ — если есть индивидуальные животные или пол задан
 * в обезличенных группах; пол не известен и беременные — только если их больше нуля.
 */
@Composable
internal fun HerdHeadsChips(herd: AnimalHerdUi) {
    if (herd.hasSexSplit) {
        SexCountChip(sex = false, count = herd.females)
        SexCountChip(sex = true, count = herd.males)
    }
    if (herd.unknownSex > 0)
        HeadsChip(UNKNOWN_COLORS, R.drawable.icon_unknow, herd.unknownSex.formatNumber(isGroupingUsed = false))
    if (herd.pregnant > 0)
        HeadsChip(PREGNANT_COLORS, R.drawable.baseline_favorite_24, herd.pregnant.toString())
}

/**
 * Обезличенная группа в списке: поголовье с иконкой, за ним — самки, самцы и пол не известен
 * (каждая плашка — только если больше нуля).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GroupHeadsChips(animal: AnimalListUi) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HeadsChip(
            colors = listOf(animal_1, animal_2),
            icon = R.drawable.baseline_spoke_24,
            text = "${animal.totalCount.formatNumber(isGroupingUsed = false)} ${stringResource(animal.suffix.toResId())}"
        )
        if (animal.femalesCount > 0) SexCountChip(sex = false, count = animal.femalesCount)
        if (animal.malesCount > 0) SexCountChip(sex = true, count = animal.malesCount)
        if (animal.unknownSexCount > 0)
            HeadsChip(UNKNOWN_COLORS, R.drawable.icon_unknow, animal.unknownSexCount.formatNumber(isGroupingUsed = false))
    }
}

/** Есть что показать в [HerdHeadsChips]. */
internal val AnimalHerdUi.hasHeadsChips: Boolean
    get() = hasSexSplit || unknownSex > 0 || pregnant > 0

/** Плашка рядом с поголовьем: градиент, белая иконка и число. */
@Composable
internal fun HeadsChip(colors: List<Color>, @DrawableRes icon: Int, text: String) {
    Row(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(colors, Offset(0f, 0f), Offset(Float.POSITIVE_INFINITY, 0f)),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
        Text(text = text, style = text_12, color = Color.White)
    }
}

/** «Состав группы»: заголовок с числом членов и «Корректировать», ниже — карточка со списком. */
@Composable
fun AnimalHerdMembers(
    herd: AnimalHerdUi,
    canAdjust: Boolean,
    onAdjustClick: () -> Unit,
    onMemberClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.animal_composite_members_title), style = textBold_18)
                Text(herd.members.size.toString(), style = textBold_18, color = grey)
            }
            if (canAdjust)
                TextButton(onClick = onAdjustClick) {
                    Text(
                        stringResource(R.string.animal_herd_button_adjust),
                        style = text_14,
                        fontWeight = FontWeight.SemiBold,
                        color = animal_1
                    )
                }
        }
        CardFieldNew(padding = PaddingValues(vertical = 4.dp)) {
            // Первые три члена, остальные — по кнопке «Показать все»
            ExpandableRows(items = herd.members, spacing = 0.dp) { index, member ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = gray_4)
                HerdMemberRow(member = member, onClick = { onMemberClick(member.id) })
            }
        }
    }
}

/**
 * Строка члена: наша иконка, имя, пол (индивидуальное животное) или поголовье (обезличенная
 * группа), вид и порода. Возраст не показываем.
 */
@Composable
private fun HerdMemberRow(member: AnimalListUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Кольцо вплотную к иконке: размер иконки + две толщины линии
        PregnancyRingIcon(
            progress = member.pregnancyProgress,
            ringSize = MEMBER_ICON_SIZE + MEMBER_RING_STROKE * 2,
            strokeWidth = MEMBER_RING_STROKE
        ) {
            IconAnimal(
                sizeIcon = MEMBER_ICON_SIZE,
                sex = member.sex,
                imagePath = member.imagePath,
                currentIcon = member.currentIcon
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = member.name,
                    style = textBold_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (member.sex != null) {
                    CountColorGradientCard(sex = member.sex)
                    member.pregnancyProgress?.let { PregnancyHeartCard(it) }
                } else member.count?.let {
                    Text("$it ${stringResource(member.suffix.toResId())}", style = text_14, color = marengo)
                }
            }
            Text(
                text = listOfNotNull(member.type, member.breed).joinToString(" · "),
                style = text_12,
                color = marengo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painterResource(R.drawable.outline_keyboard_arrow_right_24),
            contentDescription = null,
            tint = grey
        )
    }
}

private val MEMBER_ICON_SIZE = 44.dp
private val MEMBER_RING_STROKE = 3.dp

/** Операции над сборной группой — внизу карточки группы. */
@Composable
fun AnimalHerdActions(
    canDepersonalize: Boolean,
    enabled: Boolean,
    onDepersonalizeClick: () -> Unit,
    onDisbandClick: () -> Unit
) {
    SecondAnimalCard(
        icon = R.drawable.ic_herd,
        intRes = R.string.animal_composite_actions
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (canDepersonalize)
                GroupButton(
                    R.string.animal_group_button_depersonalize,
                    enabled = enabled,
                    iconRes = R.drawable.ic_herd_depersonalize,
                    onClick = onDepersonalizeClick
                )
            GroupButton(
                R.string.animal_group_button_disband,
                enabled = enabled,
                iconRes = R.drawable.ic_herd_disband,
                onClick = onDisbandClick
            )
        }
    }
}


/** «Продукция группы»: итог по каждому виду продукции, первые три — остальные по кнопке. */
@Composable
fun AnimalHerdProducts(products: List<BrieflyItem>) {
    if (products.isEmpty()) return
    SecondAnimalCard(
        icon = R.drawable.baseline_analytics_24,
        intRes = R.string.animal_herd_products
    ) {
        ExpandableRows(items = products) { index, item ->
            ProductKillInfoCard(
                number = index + 1,
                name = item.title,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                pieces = item.pieces
            )
        }
    }
}

/**
 * «Продукция по животным»: кто сколько дал выбранной продукции. Если видов несколько,
 * вид выбирается выпадающим списком.
 */
@Composable
fun AnimalHerdProductsByMember(
    products: List<BrieflyItem>,
    productsByMember: Map<String, List<HerdMemberProduct>>,
    selectedProduct: String?,
    onProductSelected: (String) -> Unit,
    onMemberClick: (Long) -> Unit
) {
    if (products.isEmpty()) return
    SecondAnimalCard(
        icon = R.drawable.baseline_pets_24,
        intRes = R.string.animal_herd_products_by_animal
    ) {
        if (products.size > 1)
            BaseExposedDropdownMenu2(
                title = selectedProduct.orEmpty(),
                labelSelector = { it.title },
                list = products,
                content = {
                    BaseOutlinedTextNew(
                        modifier = it.first,
                        value = selectedProduct.orEmpty(),
                        onValueChange = {},
                        leadingIconRes = R.drawable.baseline_analytics_24,
                        labelIntRes = R.string.animal_herd_product_choice,
                        intResSup = R.string.animal_herd_product_choice_support,
                        keyboardOptions = keyboardOptionsNext(),
                        readOnly = true,
                        isMore = true
                    )
                }
            ) { _, item, closeMenu ->
                DropdownMenuItem(
                    text = { Text(text = item.title) },
                    trailingIcon = if (item.title == selectedProduct) {
                        { Icon(Icons.Default.Done, contentDescription = null) }
                    } else null,
                    onClick = {
                        onProductSelected(item.title)
                        closeMenu()
                    }
                )
            }
        // Свой счётчик раскрытия у каждой продукции: при смене вида список снова свёрнут
        ExpandableRows(items = productsByMember[selectedProduct].orEmpty(), key = selectedProduct, spacing = 0.dp) { index, row ->
            if (index > 0) HorizontalDivider(thickness = 1.dp, color = gray_4)
            MemberProductRow(row = row, onClick = { onMemberClick(row.member.id) })
        }
    }
}

@Composable
private fun MemberProductRow(row: HerdMemberProduct, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconAnimal(
            sizeIcon = 36.dp,
            sex = row.member.sex,
            imagePath = row.member.imagePath,
            currentIcon = row.member.currentIcon,
            // Архивный член — серая иконка: его продукция учитывается, но в составе его нет
            isArchive = row.member.isArchive
        )
        Text(
            text = row.member.name,
            style = text_14,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(text = row.item.amountText(), style = text_14, fontWeight = FontWeight.SemiBold)
    }
}

/** Количество продукции в единицах из настроек: «12 кг · 30 шт.». */
@Composable
private fun BrieflyItem.amountText(): String {
    val context = LocalContext.current
    return listOfNotNull(weight, volume, linear, pieces).joinToString(" · ") { item ->
        "${item.value.first.formatNumber()} ${context.getString(item.value.second.toResId())}"
    }
}
