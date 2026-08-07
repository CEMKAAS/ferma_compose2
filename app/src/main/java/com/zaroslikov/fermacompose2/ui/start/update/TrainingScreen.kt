package com.zaroslikov.fermacompose2.ui.start.update

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_3
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.green_10
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_20
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_18

@Composable
fun TrainingScreen(
    onSkipClick: () -> Unit
) {
    BaseUpdateScreen(
        pagerState = rememberPagerState(pageCount = { 5 }),
        onSkipClick = onSkipClick
    ) { (page, innerPadding) ->
        when (page) {
            0 -> TrainingScreen1(innerPadding)
            1 -> TrainingScreen2(innerPadding)
            2 -> TrainingScreen3(innerPadding)
            3 -> TrainingScreen4(innerPadding)
            4 -> TrainingScreen5(innerPadding)
        }
    }
}

@Composable
private fun TrainingScreen1(
    innerPadding: PaddingValues
) {
    val listRow = listOf(
        R.drawable.baseline_favorite_24 to R.string.training_screen_main_welcome_row_1,
        R.drawable.icon_add_product to R.string.training_screen_main_welcome_row_2,
        R.drawable.outline_analytics_24 to R.string.training_screen_main_welcome_row_3,
        R.drawable.outline_qr_code_24 to R.string.training_screen_main_welcome_row_4
    )
    Base(
        innerPadding = innerPadding,
        titleRes = R.string.training_screen_main_welcome_title,
        supportRes = R.string.training_screen_main_welcome_title_sup,
        list = listRow,
        iconRes = R.drawable.ic_log,
        iconColor = price_green,
        backgroundColor = green_8,
        iconSize = 96.dp,
        isIcon = false
    )
}

@Composable
private fun TrainingScreen2(
    innerPadding: PaddingValues
) {
    val listRow = listOf(
        R.drawable.icon_add_product to R.string.training_screen_main_ferma_row_1,
        R.drawable.baseline_delete_24 to R.string.training_screen_main_ferma_row_2,
        R.drawable.baseline_pets_24 to R.string.training_screen_main_ferma_row_3,
        R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24 to R.string.training_screen_main_ferma_row_4,
        R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24 to R.string.training_screen_main_ferma_row_5,
    )
    Base(
        innerPadding = innerPadding,
        titleRes = R.string.training_screen_main_ferma_title,
        supportRes = R.string.training_screen_main_ferma_title_sup,
        list = listRow,
        iconRes = R.drawable.outline_work_24,
        iconColor = green_10,
        backgroundColor = animal_3,
    )
}

@Composable
private fun TrainingScreen3(
    innerPadding: PaddingValues
) {
    val listRow = listOf(
        R.drawable.outline_analytics_24 to R.string.training_screen_main_incubator_row_1,
        R.drawable.outline_percent_24 to R.string.training_screen_main_incubator_row_2,
        R.drawable.baseline_notifications_none_24 to R.string.training_screen_main_incubator_row_3,
        R.drawable.icon_list to R.string.training_screen_main_incubator_row_4
    )
    Base(
        innerPadding = innerPadding,
        titleRes = R.string.training_screen_main_incubator_title,
        supportRes = R.string.training_screen_main_incubator_title_sup,
        list = listRow,
        iconRes = R.drawable.outline_egg_24,
        iconColor = orang_1,
        backgroundColor = orang_20,
    )
}

@Composable
private fun TrainingScreen4(
    innerPadding: PaddingValues
) {
    val listRow = listOf(
        R.drawable.outline_analytics_24 to R.string.training_screen_main_finance_row_1,
        R.drawable.baseline_currency_ruble_24 to R.string.training_screen_main_finance_row_2,
        R.drawable.outline_savings_24 to R.string.training_screen_main_finance_row_3,
        R.drawable.baseline_sticky_note_2_24 to R.string.training_screen_main_finance_row_4
    )
    Base(
        innerPadding = innerPadding,
        titleRes = R.string.training_screen_main_finance_title,
        supportRes = R.string.training_screen_main_finance_title_sup,
        list = listRow,
        iconRes = R.drawable.icon_money,
        iconColor = blue_1,
        backgroundColor = blue_13,
    )
}

@Composable
private fun TrainingScreen5(
    innerPadding: PaddingValues
) {
    val listRow = listOf(
        R.drawable.icon_add to R.string.training_screen_main_end_row_1,
        R.drawable.baseline_notifications_none_24 to R.string.training_screen_main_end_row_2,
        R.drawable.baseline_archive_24 to R.string.training_screen_main_end_row_3,
        R.drawable.outline_download_24 to R.string.training_screen_main_end_row_4
    )
    Base(
        innerPadding = innerPadding,
        titleRes = R.string.training_screen_main_end_title,
        supportRes = R.string.training_screen_main_end_title_sup,
        list = listRow,
        iconRes = R.drawable.icon_check,
        iconColor = price_green,
        backgroundColor = green_8,
    )
}


@Composable
private fun Base(
    innerPadding: PaddingValues = PaddingValues(),
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    @StringRes supportRes: Int,
    list: List<Pair<Int, Int>>,
    iconColor: Color,
    backgroundColor: Color,
    isIcon: Boolean = true,
    boxSize: Dp = 96.dp,
    iconSize: Dp = 40.dp,
) {
    Column(
        modifier = Modifier.modifierScreen(innerPadding),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconTraining(
            iconRes = iconRes,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
            shape = RoundedCornerShape(24.dp),
            boxSize = boxSize,
            iconSize = iconSize,
            isShadow = true,
            isIcon = isIcon
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(titleRes),
                style = textBold_18,
                color = black_2,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(supportRes),
                style = text_18,
                color = marengo,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            list.forEach {
                Card(
                    iconRes = it.first,
                    textRes = it.second,
                    iconColor = iconColor,
                    backgroundColor = backgroundColor
                )
            }
        }
    }
}

@Composable
private fun ExampleCard() {
    CardFieldNew { }
}

@Composable
private fun Card(
    @DrawableRes iconRes: Int,
    @StringRes textRes: Int,
    iconColor: Color,
    backgroundColor: Color
) {
    BorderCard(
        padding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTraining(
                iconRes = iconRes,
                shape = RoundedCornerShape(10.dp),
                boxSize = 32.dp,
                iconSize = 16.dp,
                iconColor = iconColor,
                backgroundColor = backgroundColor,
                isShadow = false
            )
            Text(stringResource(textRes), style = text_14, color = marengo)
        }
    }
}


@Composable
fun IconTraining(
    @StringRes iconRes: Int,
    shape: RoundedCornerShape,
    isShadow: Boolean,
    boxSize: Dp,
    iconSize: Dp,
    iconColor: Color,
    backgroundColor: Color,
    isIcon: Boolean = true
) {
    Box(
        modifier = Modifier
            .then(
                if (isShadow) Modifier.shadow(6.dp, shape = shape) else Modifier
            )
            .background(color = backgroundColor, shape = shape)
            .size(boxSize)
    ) {
        if (isIcon)
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(iconSize),
                tint = iconColor
            )
        else
            Image(
                painter = painterResource(R.drawable.ic_log),
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
    }
}