package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularProgress(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp)
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MessageNoData(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(paddingValues)
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Добро пожаловать в раздел \"Мои Товары!\"",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            fontSize = 20.sp,
        )
        Text(
            text = "В этом разделе Вы можете добавлять товары, которые поступают с вашей фермы! Каждому товару можно назначить кол-во, категорию и животное, если оно занесено в разделе \"Мои Животные\"",
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            fontSize = 20.sp,
        )
        Text(
            text = "Сейчас нет товаров:(\nНажмите + чтобы добавить\nили",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
        )
        Button(
            onClick = onClick, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)

        ) {
            Text(text = "Добавить Продукцию!")
        }
    }

}