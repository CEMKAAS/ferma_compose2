package com.zaroslikov.fermacompose2.ui.elements

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.black_3
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_5
import com.zaroslikov.fermacompose2.blue_6
import com.zaroslikov.fermacompose2.blue_7
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_3
import com.zaroslikov.fermacompose2.green_4
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_7
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_1
import com.zaroslikov.fermacompose2.red_2
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.HeadingAnimalCard
import com.zaroslikov.fermacompose2.ui.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.SliderGradient
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen.Food
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.white


@Deprecated(
    message = "Используй CardFieldNew()",
    replaceWith = ReplaceWith("CardFielNew()"),
    level = DeprecationLevel.WARNING
)
@Composable
fun CardField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isNecessarily: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    row: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit,
) {
    val border = when {
        isError -> BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.error)
        isNecessarily -> BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        else -> null
    }
    Card(
        modifier = modifier.padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(),
        border = border
    ) {
        if (row) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment
            ) {
                content()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement
            ) {
                content()
            }
        }
    }
}


@Composable
fun CardFieldNew(
    modifier: Modifier = Modifier,
    colors: List<Color>? = null,
    padding: PaddingValues = PaddingValues(20.dp),
    onClick: () -> Unit = { },
    contentColumn: @Composable (ColumnScope.() -> Unit),
) {
    val shape = RoundedCornerShape(14.dp)

    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        shape = shape,
        modifier = modifier,
        elevation = CardDefaults.cardElevation( // Добавляем тень
            defaultElevation = 5.dp
        )
    ) {
        Box(
            Modifier.then(
                colors?.let { colors ->
                    val gradient = Brush.linearGradient(colors)
                    Modifier.background(gradient, shape = shape)
                } ?: Modifier
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                contentColumn()
            }
        }
    }
}

@Composable
fun CardFieldNew(
    modifier: Modifier = Modifier,
    colors: List<Color>? = null,
    padding: PaddingValues = PaddingValues(20.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    contentRow: @Composable (RowScope.() -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentColumn: @Composable (ColumnScope.() -> Unit)? = null
) {
    val modifierCard = Modifier
        .fillMaxWidth()
        .padding(padding)

    val shape = RoundedCornerShape(14.dp)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        shape = shape,
        modifier = modifier,
        elevation = CardDefaults.cardElevation( // Добавляем тень
            defaultElevation = 5.dp
        )
    ) {
        Box(
            Modifier.then(
                colors?.let { colors ->
                    val gradient = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to colors[0],
                            0.7f to colors[0],
                            1.0f to colors[1]
                        )
                    )
                    Modifier.background(gradient, shape = shape)
                } ?: Modifier
            )
        ) {
            when {
                contentRow != null -> Row(
                    modifier = modifierCard,
                    horizontalArrangement = horizontalArrangement,
                    verticalAlignment = verticalAlignment
                ) {
                    contentRow()
                }

                contentColumn != null -> Column(
                    modifier = modifierCard,
                    horizontalAlignment = horizontalAlignment,
                    verticalArrangement = verticalArrangement
                ) {
                    contentColumn()
                }
            }
        }
    }
}


//@RequiresApi(Build.VERSION_CODES.S)
//@Composable
//fun CardField(
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    row: Boolean = true,
//    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
//    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
//    content: @Composable () -> Unit,
//) {
//    val blurEffect = RenderEffect.createBlurEffect(
//        20f, 20f, Shader.TileMode.CLAMP
//    ).asComposeRenderEffect()
//
//    Box(
//        modifier = modifier
//            .clip(
//                RoundedCornerShape(16.dp)
//            )
//            .background(Color.Transparent)
//    ) {
//        Box(
////            modifier = Modifier
////                .graphicsLayer {
////                    renderEffect = blurEffect
////                }
////                .background(Color.White.copy(alpha = 0.75f)) // зелёный с прозрачностью
//            modifier = Modifier
//                .matchParentSize()
//                .clip(RoundedCornerShape(16.dp))
//                .background(Color.White.copy(alpha = 0.15f))
//                .blur(radius = 16.dp)
//        )
//        Box(
//            modifier = Modifier
//
//                .padding(
//                    16.dp
//                )
//        ) {
//            if (row) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = horizontalArrangement,
//                    verticalAlignment = verticalAlignment
//                ) {
//                    content()
//                }
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalAlignment = horizontalAlignment,
//                    verticalArrangement = verticalArrangement
//                ) {
//                    content()
//                }
//            }
//        }
//    }
//}

@Composable
fun CardOutlined(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier.padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(3.dp, CardDefaults.cardColors().containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}


@Composable
fun CardFinance(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes titleRes: Int,
    value: Double
) {
    CardField(
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceNew(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    colors: List<Color>,
    value: Double,
    suffix: Suffix = Suffix.RUBLE,
    @StringRes titleRes2: Int? = null
) {
    BigColorCard(
        modifier = modifier,
        glowColor = colors.first(),
        colors = colors,
        padding = PaddingValues(16.dp),
        onDetailClick = onClick,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            IconFinance(
                icon = icon,
                color = Color(0x20FFFFFF)
            )
            Text(
                text = stringResource(titleRes),
                style = text_12,
                color = Color(0x80FFFFFF)
            )
            Text(
                text = "${value.formatNumber()} ${stringResource(suffix.toResId())}",
                style = text_20,
                color = white
            )
            titleRes2?.let {
                Text(
                    text = stringResource(it),
                    style = text_12,
                    color = Color(0x70FFFFFF)
                )
            }
        }
    )
}

@Composable
fun CardFinance(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    titleRes: String,
    value: Double
) {
    CardField(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceOutlined(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    value: Double
) {
    CardOutlined(
        modifier = modifier
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceOutlinedRow(
    modifier: Modifier = Modifier,
    @StringRes oneIntRes: Int,
    @StringRes twoIntRes: Int,
    oneValue: Double,
    twoValue: Double,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CardFinanceOutlined(
            modifier = modifier.weight(1f),
            titleRes = oneIntRes,
            value = oneValue,
        )
        CardFinanceOutlined(
            modifier = modifier.weight(1f),
            titleRes = twoIntRes,
            value = twoValue,
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun CardFinanceRow(
    modifier: Modifier = Modifier,
    title: String,
    value: Double
) {
    CardField(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth(0.7f),
            style = textBold_16
        )
        Text(
            text = stringResource(R.string.card_ruble_s, value.formatNumber()),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f),
            style = textBold_16
        )
    }
}

@Composable
fun DetailProductCardNew(
    modifier: Modifier = Modifier,
    title: String? = null,
    count: Double,
    suffix: Suffix,
    price: Double? = null,
    priceSuffix: Suffix = Suffix.PIECES,
    category: String? = null,
    statusWriteOff: Boolean? = null,
    note: String,
    animal: String? = null,
    color: Color,
    day: Int,
    month: Int,
    year: Int,
    buyer: String? = null,
    food: Food? = null,
    colors: List<Color>? = null,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isCardField: Boolean = true
) {
    val monthText = stringResource(id = monthToResString(month))
    val date = dateBuilder(day, monthText, year)

    val cardField: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = textBold_16,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                CountColorCard(count, suffix, color)
                price?.let {
                    CountColorCard(it, priceSuffix, price_green)
                }
            }
            DropdownMenuEdit(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
        statusWriteOff?.let { status ->
            val (iconRes, valueString) = if (status)
                R.drawable.baseline_cottage_24 to R.string.ration_button_own_needs
            else
                R.drawable.baseline_delete_24 to R.string.ration_button_disposal
            IconAndTextNew(
                iconRes = iconRes,
                valueString = stringResource(valueString),
                iconColor = if (status) violet_1 else error_base,
            )
        }
        category.takeUnless { it == "Без категории" /*|| it.isEmpty()*/ }
            ?.let { category ->
                IconAndTextNew(
                    iconRes = R.drawable.baseline_format_list_bulleted_24,
                    valueString = category,
                    iconColor = color
                )
            }
        buyer?.let {
            IconAndTextNew(
                iconRes = R.drawable.baseline_person_24,
                valueString = it,
                iconColor = color
            )
        }
        IconAndTextNew(
            iconRes = R.drawable.baseline_calendar_month_24,
            valueString = date,
            iconColor = color
        )
//                if (addProduct.animal != "")
//                    IconAndText(
//                        iconRes = R.drawable.baseline_pets_24,
//                        valueString = addProduct.animal
//                    )
        food?.let { food ->
            SliderFood(
                feedFood = food.feedFood,
                feedFoodSuffix = food.feedFoodSuffix,
                daysEnd = food.daysEnd,
                weightAll = food.weightAll,
                weightSuffix = food.weightSuffix,
                percentFloat = food.percentFloat,
                animalList = food.animalList
            )
        }
        if (note != "") NoteColorCard(note, color)
    }

    if (isCardField) CardFieldNew(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        colors = colors
    ) { cardField() } else BorderCard { cardField() }
}

@Composable
private fun SliderFood(
    feedFood: Double,
    feedFoodSuffix: Suffix,
    daysEnd: Int,
    weightAll: Double,
    weightSuffix: Suffix,
    percentFloat: Float,
    animalList: List<String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            animalList.forEach {
                AnimalFoodClips(it)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconAndTextNew(
                iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                valueString = "${weightAll.formatNumber()} ${stringResource(weightSuffix.toResId())}",
                iconColor = blue_1,
                iconSize = 14.dp,
                textColor = black_2,
                textStyle = text_12
            )
            IconAndTextNew(
                iconRes = R.drawable.baseline_shopping_basket_24,
                valueString = "${feedFood.formatNumber()} ${stringResource(feedFoodSuffix.toResId())}",
                iconColor = green_shamrock,
                iconSize = 14.dp,
                textColor = black_2,
                textStyle = text_12
            )
            IconAndTextNew(
                iconRes = R.drawable.baseline_access_time_24,
                valueString = "${daysEnd.formatNumber()} ${stringResource(R.string.expenses_screen_days)}",
                iconColor = violet_1,
                iconSize = 14.dp,
                textColor = black_2,
                textStyle = text_12
            )
        }
        BaseSlider(
            modifier = Modifier,
            percentFloat = percentFloat,
            color = black_3,
        )
    }
}


@Composable
fun BorderCard(
    modifier: Modifier = Modifier,
    containerColor: Color = white,
    padding: PaddingValues = PaddingValues(20.dp),
    shape: Shape = RoundedCornerShape(14.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = grey_2,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ), onClick = onClick
    ) {
        Column(
            Modifier
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun BorderCard(
    modifier: Modifier = Modifier,
    containerColor: Color = white,
    padding: PaddingValues = PaddingValues(20.dp),
    shape: Shape = RoundedCornerShape(14.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = grey_2,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
    ) {
        Column(
            Modifier
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ColorCard(
    modifier: Modifier = Modifier,
    containerColor: Color = white,
    padding: PaddingValues = PaddingValues(20.dp),
    shape: Shape = RoundedCornerShape(14.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            Modifier
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun BrieflyCountCardNew(
    modifier: Modifier = Modifier,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    price: Double? = null,
    priceSuffix: Suffix = Suffix.PIECES,
    color: Color,
    colorSecondary: Color,
    icon: Int,
    onClick: () -> Unit
) {
    CardFieldNew(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentRow = {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorSecondary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(8.dp),
                )
            }
            TextLine(
                modifier = Modifier.weight(1f),
                valueString = titleProduct,
                textStyle = textBold_20
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    CountColorCard(count, suffix, color)
                    price?.let {
                        CountColorCard(it, priceSuffix, price_green)
                    }
                    Text(text = "$countEntry Записи", style = text_12)
                }
                IconButton(onClick = onClick) {
                    Icon(
                        painterResource(R.drawable.outline_keyboard_arrow_right_24),
                        contentDescription = null,
                        tint = grey
                    )
                }
            }
        }
    )
}

@Composable
private fun AnimalFoodClips(
    nameAnimal: String
) {
    BorderCard(
        borderColor = green_3,
        containerColor = green_g_1,
        shape = RoundedCornerShape(99.dp),
        padding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_shopping_basket_24),
                contentDescription = null,
                tint = green_shamrock,
                modifier = Modifier.size(12.dp)
            )
            Text(nameAnimal, style = text_12, color = green_2)
        }
    }
}

@Composable
fun CountColorCard(
    count: Double,
    suffix: Suffix,
    colorCard: Color
) {
    val countString = count.formatNumber()
    val suffixString = stringResource(suffix.toResId())
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorCard
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "$countString $suffixString",
            style = text_12,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
            color = white
        )
    }
}

@Composable
fun CategoryBorderCard(
    text: String
) {
    BorderCard(
        modifier = Modifier,
        padding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, style = text_12, color = black)
    }
}

@Composable
fun CountColorGradientCard(
    modifier: Modifier = Modifier,
    sex: Boolean
) {
    val (colorGradient, icon) = if (sex)
        listOf(Color(0xFF2B7FFF), Color(0xFF615FFF)) to R.drawable.baseline_male_24
    else listOf(Color(0xFFF6339A), Color(0xFFFF2056)) to R.drawable.baseline_female_24

    val gradient = Brush.linearGradient(
        colors = colorGradient,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    Box(
        modifier = modifier
            .background(brush = gradient, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = "Пол",
            tint = Color.White,
            modifier = Modifier
                .size(12.dp),
            /*.padding(8.dp)*/
        )
    }
}


@Composable
fun NoteColorCard(
    note: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ghostly_white
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        IconAndTextNew(
            iconRes = R.drawable.baseline_sticky_note_2_24,
            valueString = note,
            modifier = Modifier.padding(12.dp),
            iconColor = color
        )
    }
}

@Composable
fun WarehouseCountCard(
    title: String,
    warehouseList: List<DomainCountSuffix>
) {
    if (title.isNotBlank() && warehouseList.isNotEmpty())
        BorderCard {
            Text(
                text = stringResource(R.string.support_text_count_warehouse_s)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                warehouseList.forEachIndexed { index, item ->
                    CountColorCard(item.count, item.suffix, item.suffix.toColorList())
                }
            }
        }
}

@Composable
fun CardClips(
    modifier: Modifier = Modifier,
    colorBackground: Color,
    colorBorder: Color,
    colorText: Color,
    colorIcon: Color? = null,
    @DrawableRes icon: Int? = null,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colorBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.let {
                colorIcon?.let { contentDescription ->
                    Icon(
                        painterResource(it),
                        modifier = Modifier.size(18.dp),
                        tint = contentDescription,
                        contentDescription = null
                    )
                }
            }
            Text(
                text = value,
                color = colorText,
                style = text_12
            )
        }
    }
}




@Composable
fun CardIndicationChange(
    value: String,
    suffix: Suffix,
) {
    CardClips(
        colorBackground = green_g_2,
        colorBorder = green_1,
        colorText = green_2,
        value = "$value ${stringResource(suffix.toResId())}",
    )
}



@Composable
fun DateFactoryCardNew(
    isDateFactory: Boolean,
    @StringRes intTitle: Int,
    @StringRes intTooltip: Int,
    @StringRes intRes: Int,
    @StringRes intResSup: Int,
    dateBoring: String,
    dateFactory: String,
    dateFactoryClicked: (Boolean) -> Unit,
    dateFactoryChanged: (String) -> Unit
) {
    BorderCard {
        CheckboxTextIcon(

            checked = isDateFactory,
            onCheckedChange = {
                dateFactoryClicked(it)
            },
            intTitle = intTitle,
            isTooltipShow = true,
            intTooltip = intTooltip
        )
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = !isDateFactory,
        ) {
            OutlinedTextDateNew(
                value = dateFactory,
                intRes = intRes,
                intResSup = intResSup,
                drawableRes = R.drawable.baseline_event_24,
                onValueChange = { dateFactoryChanged(it) },
                isBorderCard = false,
                minDate = dateBoring
            )
        }
    }
}

@Composable
fun GreenCard(
    padding: PaddingValues = PaddingValues(),
    onClick: () -> Unit = {},
    containerColor: Color = green_g_2,
    borderColor: Color = green_1,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        content()
    }
}

@Composable
fun ProductKillInfoCard(
    number: Int,
    name: String,
    value: String,
    suffix: Suffix
) {
    BorderCard(
        padding = PaddingValues(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconText(number = number.toString(), colorBackground = green_4, colorText = green_2)
                Text(name, style = text_14, color = black_1)
            }
            CountColorCard(
                count = value.toDouble(),
                suffix = suffix,
                colorCard = suffix.toColorList()
            )
        }
    }
}

@Composable
fun SecondAnimalCard(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes intRes: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    CardFieldNew(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeadingAnimalCard(icon, intRes)
            content()
        }
    }
}

@Composable
fun BigColorCard(
    modifier: Modifier = Modifier,
    glowColor: Color,
    colors: List<Color>,
    padding: PaddingValues = PaddingValues(24.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    borderStroke: BorderStroke? = null,
    onDetailClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape2 = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 20f
                shape = shape2
                clip = false
                ambientShadowColor = glowColor.copy(alpha = 0.8f)
                spotShadowColor = glowColor.copy(alpha = 0.8f)
            }
            .then(
                if (borderStroke != null) Modifier.border(borderStroke, shape2) else Modifier
            )
            .drawBehind {
                val gradient = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height) // диагональ
                )
                drawRoundRect(
                    brush = gradient,
                    cornerRadius = CornerRadius(14.dp.toPx()) // скругление здесь
                )
            }
            .then(
                if (onDetailClick != null)
                    Modifier
                        .clip(shape2)
                        .clickable { onDetailClick() }
                else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = verticalArrangement,
            modifier = Modifier.padding(padding)
        ) {
            content()
        }
    }
}

@Composable
fun WhiteTenCard(
    modifier: Modifier,
    @StringRes titleRes: Int,
    value: Double,
    suffix: Suffix
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0x10FFFFFF)
        ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(stringResource(titleRes), style = text_12, color = Color(0x70FFFFFF))
            Text(
                "${value.formatNumber()} ${stringResource(suffix.toResId())}",
                style = text_20,
                color = white
            )
        }
    }
}


@Composable
fun CardNewWithTitle(
    @DrawableRes iconRes: Int? = null,
    @StringRes titleRes: Int,
    textColor: Color = black_2,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    CardFieldNew {
        Column(
            verticalArrangement = verticalArrangement
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                iconRes?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = gray_7
                    )
                }
                Text(
                    stringResource(titleRes),
                    style = text_16,
                    color = textColor
                )
            }
            content()
        }
    }
}


@Composable
fun TextMiniCard(
    value: String,
    textColor: Color,
    backgroundColor: Color
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
    ) {
        Text(
            value,
            style = text_12,
            color = textColor,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
        )
    }
}

@Composable
fun InfoCard(
    colorBackground: Color,
    colorBorder: Color,
    colorIcon: Color,
    colorIconBackground: Color,
    colorTitle: Color,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colorBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconTransaction2(
                    icon = icon,
                    color = colorIconBackground,
                    colorIcon = colorIcon,
                    sizeCard = 32.dp
                )
                Text(stringResource(title), style = text_14, color = colorTitle)
            }
            Column {
                content()
            }
        }
    }
}