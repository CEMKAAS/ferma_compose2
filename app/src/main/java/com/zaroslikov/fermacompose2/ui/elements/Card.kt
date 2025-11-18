package com.zaroslikov.fermacompose2.ui.elements

import android.annotation.SuppressLint
import androidx.annotation.StringRes
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.supportFun.toColor
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.start.monthToResString
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.white


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
    padding: PaddingValues = PaddingValues(20.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    contentRow: @Composable (RowScope.() -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentColumn: @Composable (ColumnScope.() -> Unit)? = null,
) {
    val modifierCard = Modifier
        .fillMaxWidth()
        .padding(padding)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
        elevation = CardDefaults.cardElevation( // Добавляем тень
            defaultElevation = 5.dp
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
                color = if (status) violet_1 else error_base,
            )
        }
        category.takeUnless { it == "Без категории" /*|| it.isEmpty()*/ }
            ?.let { category ->
                IconAndTextNew(
                    iconRes = R.drawable.baseline_format_list_bulleted_24,
                    valueString = category,
                    color = color
                )
            }
        buyer?.let {
            IconAndTextNew(
                iconRes = R.drawable.baseline_person_24,
                valueString = it,
                color = color
            )
        }
        IconAndTextNew(
            iconRes = R.drawable.baseline_calendar_month_24,
            valueString = date,
            color = color
        )

//                if (addProduct.animal != "")
//                    IconAndText(
//                        iconRes = R.drawable.baseline_pets_24,
//                        valueString = addProduct.animal
//                    )
        if (note != "") NoteColorCard(note, color)
    }

    if (isCardField) CardFieldNew(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) { cardField() } else BorderCard { cardField() }
}


@Composable
fun BorderCard(
    modifier: Modifier = Modifier,
    padding: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = grey_2,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
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
            color = color
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
                    CountColorCard(item.count, item.suffix, item.suffix.toColor())
                }
            }
        }
}