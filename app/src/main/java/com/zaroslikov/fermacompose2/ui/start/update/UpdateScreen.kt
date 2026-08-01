package com.zaroslikov.fermacompose2.ui.start.update

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.text_18

@Composable
fun UpdateScreen(
    onSkipClick: () -> Unit
) {
    BaseUpdateScreen(
        countPage = 5,
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
private fun BaseAboutScreen(
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
        Text()
        Text()
        content()
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
private fun CardNew() {
    Card(

    ) {

    }
}

@Composable
private fun IconCard(){
    Card() { }
}