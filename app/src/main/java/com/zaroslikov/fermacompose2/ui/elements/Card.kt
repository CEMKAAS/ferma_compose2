package com.zaroslikov.fermacompose2.ui.elements

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.AddViewModel
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.start.monthToResString
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
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    contentRow: @Composable (RowScope.() -> Unit)? = null,

    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentColumn: @Composable (ColumnScope.() -> Unit)? = null,
) {
    val modifierCard = Modifier
        .fillMaxWidth()
        .padding(20.dp)

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
    title: String,
    count: Double,
    suffix: Suffix,
    category: String?,
    note: String,
    animal: String?,
    color: Color,
    day: Int,
    month: Int,
    year: Int,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val monthText = stringResource(id = monthToResString(month))
    val date = dateBuilder(day, monthText, year)

    CardFieldNew(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
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
                Text(
                    text = title,
                    style = textBold_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                CountColorCard(count, suffix, green_shamrock)
            }
            DropdownMenuEdit(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
        IconAndTextNew(
            iconRes = R.drawable.baseline_calendar_month_24,
            valueString = date
        )
        category.takeUnless { it == "Без категории" /*|| it.isEmpty()*/ }
            ?.let { category ->
                IconAndTextNew(
                    iconRes = R.drawable.baseline_format_list_bulleted_24,
                    valueString = category
                )
            }
//                if (addProduct.animal != "")
//                    IconAndText(
//                        iconRes = R.drawable.baseline_pets_24,
//                        valueString = addProduct.animal
//                    )
        if (note != "") NoteColorCard(note)
    }
}

@Composable
fun DetailProductCardNew2(
    modifier: Modifier = Modifier,
    count: Double,
    suffix: Suffix,
    category: String?,
    note: String,
    animal: String?,
    color: Color,
    day: Int,
    month: Int,
    year: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val monthText = stringResource(id = monthToResString(month))
    val date = dateBuilder(day, monthText, year)
    BorderCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CountColorCard(count, suffix, green_shamrock)
            DropdownMenuEdit(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
        IconAndTextNew(
            iconRes = R.drawable.baseline_calendar_month_24,
            valueString = date
        )
        category.takeUnless { it == "Без категории" /*|| it.isEmpty()*/ }
            ?.let { category ->
                IconAndTextNew(
                    iconRes = R.drawable.baseline_format_list_bulleted_24,
                    valueString = category
                )
            }
//                if (addProduct.animal != "")
//                    IconAndText(
//                        iconRes = R.drawable.baseline_pets_24,
//                        valueString = addProduct.animal
//                    )
        if (note != "") NoteColorCard(note)
    }
}


@Composable
fun BorderCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        border = BorderStroke(
            width = 1.dp,
            color = grey_2
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
    color: Color,
    colorSecondary: Color,
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
                    painterResource(R.drawable.icon_add_product),
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
fun NoteColorCard(
    note: String
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
        )
    }
}

