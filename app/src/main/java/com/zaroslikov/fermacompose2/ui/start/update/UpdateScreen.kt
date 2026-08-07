package com.zaroslikov.fermacompose2.ui.start.update


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_21
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_16
import com.zaroslikov.fermacompose2.green_17
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_5
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_22
import com.zaroslikov.fermacompose2.orang_23
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeType
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.TemplateCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_30
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.CountOfWarehouse
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.ProductCard
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseItem
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseSection
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_13
import com.zaroslikov.fermacompose2.violet_15
import com.zaroslikov.fermacompose2.violet_16
import com.zaroslikov.fermacompose2.violet_4
import com.zaroslikov.fermacompose2.white

@Composable
fun UpdateScreen(
    onSkipClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })

    // Анимируем каждый цвет отдельно
    val color1 by animateColorAsState(
        targetValue = when (pagerState.currentPage) {
            0 -> green_5
            1 -> blue_4
            2 -> violet_13
            else -> orang_22
        },
        animationSpec = tween(durationMillis = 500)
    )

    val color2 by animateColorAsState(
        targetValue = when (pagerState.currentPage) {
            0 -> green_6
            1 -> blue_21
            2 -> violet_4
            else -> orang_9
        },
        animationSpec = tween(durationMillis = 500)
    )

    val color3 by animateColorAsState(
        targetValue = when (pagerState.currentPage) {
            0 -> green_16
            1 -> green_17
            2 -> violet_16
            else -> orang_23
        },
        animationSpec = tween(durationMillis = 500)
    )

    BaseUpdateScreen(
        pagerState = pagerState,
        colors = listOf(color1, color2, color3),
        onSkipClick = onSkipClick
    ) { (page, innerPadding) ->
        when (page) {
            0 -> FirstUpdateScreen(innerPadding)
            1 -> SecondUpdateScreen(innerPadding)
            2 -> ThirdUpdateScreen(innerPadding)
            3 -> FourUpdateScreen(innerPadding)
        }
    }
}

@Composable
private fun FirstUpdateScreen(
    innerPadding: PaddingValues,
) {
    BaseAboutScreen(
        innerPadding = innerPadding,
        tittleRes = R.string.update_screen_first_title,
        supportTitleRes = R.string.update_screen_first_support_title,
        list = listOf(
            Triple(R.drawable.outline_description_24, R.string.update_screen_first_list_1, null),
            Triple(R.drawable.outline_qr_code_24, R.string.update_screen_first_list_2, null),
            Triple(R.drawable.baseline_warehouse_24, R.string.update_screen_first_list_3, null),
            Triple(R.drawable.outline_build_24, R.string.update_screen_first_list_4, null),
        )
    ) {

    }
}

@Composable
private fun ThirdUpdateScreen(
    innerPadding: PaddingValues,
) {
    BaseAboutScreen(
        innerPadding = innerPadding,
        tittleRes = R.string.update_screen_second_title,
        supportTitleRes = R.string.update_screen_second_support_title,
        list = listOf(
            Triple(
                R.drawable.outline_qr_code_24,
                R.string.update_screen_second_list_1,
                R.string.update_screen_second_list_support_1
            ),
            Triple(
                R.drawable.outline_download_24,
                R.string.update_screen_second_list_2,
                R.string.update_screen_second_list_support_2
            ),
            Triple(
                R.drawable.outline_qr_code_scanner_24,
                R.string.update_screen_second_list_3,
                R.string.update_screen_second_list_support_3
            )
        )
    ) {
        TransparentCard(
            shape = RoundedCornerShape(16.dp),
            paddingValues = PaddingValues(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QrCodeCard(
                    iconRes = R.drawable.outline_qr_code_24
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QrCodeType.entries.forEach { qrCodeType ->
                        val isChoice = QrCodeType.STANDARD == qrCodeType
                        TransparentCard(
                            shape = RoundedCornerShape(99f),
                            paddingValues = PaddingValues(vertical = 4.dp, horizontal = 10.dp),
                            transparent = if (isChoice) 1f else 0.2f,
                            isBorder = false
                        ) {
                            Text(
                                text = stringResource(qrCodeType.toResId()),
                                style = text_12,
                                color = if (isChoice) violet_15 else white,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondUpdateScreen(
    innerPadding: PaddingValues,
) {
    BaseAboutScreen(
        innerPadding = innerPadding,
        tittleRes = R.string.update_screen_third_title,
        supportTitleRes = R.string.update_screen_third_support_title,
        list = listOf(
            Triple(
                R.drawable.outline_language_24,
                R.string.update_screen_third_list_1,
                R.string.update_screen_third_list_support_1
            ),
            Triple(
                R.drawable.outline_keep_24,
                R.string.update_screen_third_list_2,
                R.string.update_screen_third_list_support_2
            ),
        )
    ) {
        CardFieldNew {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    TemplateItem(
                        id = 0,
                        name = "Сбор яиц",
                        description = "Яйцо · шт · Яичная продукция · Курицы",
                        isMultiProject = false,
                        isPinned = true
                    ),
                    TemplateItem(
                        id = 1,
                        name = "Списание молока",
                        description = "Молоко · л · 100₽ · Молочная продукция · На собственые нужды",
                        isMultiProject = false,
                        isPinned = false
                    ),
                    TemplateItem(
                        id = 2,
                        name = "Продажа яиц",
                        description = "Яйцо · 10 · шт · Яичная продукция · ",
                        isMultiProject = false,
                        isPinned = false
                    ),
                    TemplateItem(
                        id = 3,
                        name = "Покупка комбикорма",
                        description = "Комбикомр · шт · 500₽ · Корм",
                        isMultiProject = true,
                        isPinned = false
                    )
                ).forEach {
                    val (icon, iconColor) = when (it.id) {
                        0L -> R.drawable.icon_add_product to price_green
                        1L -> R.drawable.baseline_edit_note_24 to violet_1
                        2L -> R.drawable.icon_sale to blue_1
                        else -> R.drawable.icon_expenses to orang_1
                    }
                    TemplateCard(
                        iconRes = icon,
                        title = it.name,
                        value = it.description.ifBlank { null },
                        pin = it.isPinned,
                        isMultiProject = it.isMultiProject,
                        iconColor = iconColor,
                        onClick = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun FourUpdateScreen(
    innerPadding: PaddingValues,
) {
    BaseAboutScreen(
        innerPadding = innerPadding,
        tittleRes = R.string.update_screen_found_title,
        supportTitleRes = R.string.update_screen_found_support_title,
        list = listOf(
            Triple(
                R.drawable.ic_visibility,
                R.string.update_screen_found_list_1,
                R.string.update_screen_found_list_support_1
            ),
            Triple(
                R.drawable.baseline_format_list_bulleted_24,
                R.string.update_screen_found_list_2,
                R.string.update_screen_found_list_support_2
            ),
        )
    ) {
        listOf(
            WarehouseItem(
                title = "Яйцо",
                count = 200.0,
                suffix = Suffix.PIECES,
                countOfWarehouse = CountOfWarehouse.Have
            ),
            WarehouseItem(
                title = "Молоко",
                count = 0.0,
                suffix = Suffix.LITERS,
                countOfWarehouse = CountOfWarehouse.Zero
            ),
            WarehouseItem(
                title = "Мясо",
                count = -5.0,
                suffix = Suffix.KILOGRAM,
                countOfWarehouse = CountOfWarehouse.Minus
            ),
        ).let { list ->
            WarehouseSection(
                titleRes = R.string.add_screen_title2,
                iconRes = R.drawable.icon_add_product,
                list = list,
                textColor = green_2,
                borderColor = green_1,
                iconColor = green_shamrock,
                backgroundMiniColor = green_g_1,
                isShowAllProduct = true,
                onClick = { }
            ) { product ->
                ProductCard(
                    title = product.title,
                    value = product.count,
                    suffix = product.suffix,
                    countOfWarehouse = product.countOfWarehouse
                )
            }
        }
    }
}


@Composable
private fun BaseAboutScreen(
    innerPadding: PaddingValues,
    @StringRes tittleRes: Int,
    @StringRes supportTitleRes: Int,
    list: List<Triple<Int, Int, Int?>>,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .modifierScreen(innerPadding),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(tittleRes),
                style = text_30,
                fontWeight = FontWeight.Bold,
                lineHeight = 37.sp,
                color = white
            )
            Text(
                stringResource(supportTitleRes),
                style = text_14,
                lineHeight = 22.sp,
                color = white.copy(alpha = 0.8f)
            )
        }
        content()
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            list.forEach { (icon, text, supportText) ->
                Card2(icon, text, supportText)
            }
        }
    }
}


@Composable
private fun Card2(
    @DrawableRes iconRes: Int,
    @StringRes stringRes: Int,
    @StringRes supportText: Int?
) {
    CardFieldNew(
        shape = RoundedCornerShape(14.dp),
        containerColor = white.copy(alpha = 0.1f),
        elevation = 0.dp,
        padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCard(iconRes)
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    stringResource(stringRes),
                    style = text_14,
                    color = white,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (supportText != null)
                    Text(
                        stringResource(supportText),
                        style = text_12,
                        color = white.copy(0.6f),
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
            }
        }
    }
}

@Composable
private fun IconCard(
    @DrawableRes iconRes: Int
) {
    BorderCard(
        shape = RoundedCornerShape(10.dp),
        containerColor = white.copy(alpha = 0.2f),
        borderColor = white.copy(alpha = 0.2f),
        elevation = 0.dp,
        padding = PaddingValues(6.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = white,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun QrCodeCard(
    @DrawableRes iconRes: Int
) {
    BorderCard(
        shape = RoundedCornerShape(14.dp),
        elevation = 1.dp,
        borderColor = white,
        padding = PaddingValues(16.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = violet_15,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
private fun TransparentCard(
    modifier: Modifier = Modifier,
    shape: Shape,
    paddingValues: PaddingValues,
    transparent: Float = 0.15f,
    isBorder: Boolean = true,
    content: @Composable () -> Unit
) {
    BorderCard(
        modifier = modifier,
        shape = shape,
        containerColor = white.copy(transparent),
        borderColor = white.copy(alpha = if (isBorder) 0.2f else transparent),
        borderWidth = if (isBorder) 1.dp else 0.dp,
        padding = paddingValues
    ) { content() }
}

