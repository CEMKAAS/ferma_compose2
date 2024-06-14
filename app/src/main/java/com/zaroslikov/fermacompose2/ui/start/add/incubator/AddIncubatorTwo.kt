package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.zaroslikov.fermacompose2.TopAppBarStart
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun AddIncubatorTwo(navigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBarStart(title = "Мое Хозяйство", true, navigateUp = navigateBack)
        },
    ) { innerPadding ->
        AddIncubatorTwoContainer(
            modifier = Modifier
                .padding(innerPadding)
        )
    }
}

@Composable
fun AddIncubatorTwoContainer(modifier: Modifier) {

    var massTemp = mutableListOf<String>()
    var massDamp = mutableListOf<String>()
    var massOver = mutableListOf<String>()
    var massAiring = mutableListOf<String>()

    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .background(color = Color(red = 238, green = 243, blue = 220))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "День",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "°C",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "%",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "Поворот",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "Проветривание",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            }
        }
        item { Divider(color = Color.DarkGray, thickness = 1.dp) }
        item { Divider(color = Color.DarkGray, thickness = 1.dp) }
        items(30) {
//            MyRowIncubatorAdd()
        }
        item {
            Button(
                onClick = { },
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Text(text = "Запустить")
            }
        }
    }
}


@Composable
fun MyRowIncubatorAdd(
    temp: String,
    damp: String,
    over: String,
    airing: String
) {
    val text by rememberSaveable { mutableStateOf("День 1") }
    val text2 by rememberSaveable { mutableStateOf(temp) }
    val text3 by rememberSaveable { mutableStateOf(damp) }
    val text4 by rememberSaveable { mutableStateOf(over) }
    val text5 by rememberSaveable { mutableStateOf(airing) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text, onValueChange = {  },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .padding(6.dp),
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        BasicTextField(
            value = text2, onValueChange = { text2 },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .padding(6.dp),
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        BasicTextField(
            value = text3, onValueChange = { text3 },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .padding(6.dp),
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        BasicTextField(
            value = text4, onValueChange = { text4 },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(6.dp),
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        BasicTextField(
            value = text5, onValueChange = { text5 },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
        )

    }
    Divider(color = Color.DarkGray, thickness = 1.dp)
}

//private fun setIncubator(
//) {
//    when (typeIncubator) {
//        "Курицы" -> {
//            massTemp = mutableListOf(
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.9",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.3",
//                "37.3",
//                "37.0",
//                "37.0",
//                "37.0"
//            )
//            massDamp = mutableListOf(
//                "66",
//                "66",
//                "66",
//                "66",
//                "66",
//                "66",
//                "66",
//                "66",
//                "66",
//                "66",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "47",
//                "47",
//                "70",
//                "70",
//                "70"
//            )
//            massAiring = mutableListOf(
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 20 мин",
//                "2 раза по 20 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин"
//            )
//            massOver = mutableListOf(
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "2-3",
//                "0"
//            )
//        }
//
//        "Гуси" -> {
//            massTemp = mutableListOf(
//                "38.0",
//                "37.8",
//                "37.8",
//                "37.6",
//                "37.6",
//                "37.6",
//                "37.6",
//                "37.6",
//                "37.6",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3",
//                "37.3"
//            )
//            massDamp = mutableListOf(
//                "65",
//                "65",
//                "65",
//                "70",
//                "70",
//                "70",
//                "70",
//                "70",
//                "70",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75"
//            )
//            massAiring = mutableListOf(
//                "нет",
//                "1 раз по 20 мин",
//                "1 раз по 20 мин",
//                "1 раз по 20 мин",
//                "1 раз по 20 мин",
//                "2 раз по 20 мин",
//                "2 раз по 20 мин",
//                "2 раз по 20 мин",
//                "2 раз по 20 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин",
//                "3 раза по 45 мин"
//            )
//            massOver = mutableListOf(
//                "3-4",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "10",
//                "0",
//                "0",
//                "0"
//            )
//        }
//
//        "Перепела" -> {
//            massTemp = mutableListOf(
//                "38.0",
//                "38.0",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.5",
//                "37.5",
//                "37.5"
//            )
//            massDamp = mutableListOf(
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "55",
//                "37.5",
//                "37.5",
//                "37.5"
//            )
//            massAiring = mutableListOf(
//                "нет",
//                "нет",
//                "нет",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "1 раз по 5 мин",
//                "нет",
//                "нет",
//                "нет"
//            )
//            massOver = mutableListOf(
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "3-6",
//                "нет",
//                "нет"
//            )
//        }
//
//        "Индюки" -> {
//            massTemp = mutableListOf(
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.7",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5"
//            )
//            massDamp = mutableListOf(
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "45",
//                "45",
//                "45",
//                "45",
//                "45",
//                "45",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65",
//                "65"
//            )
//            massAiring = mutableListOf(
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "2 раза по 5 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "4 раза по 10 мин",
//                "нет",
//                "нет",
//                "нет"
//            )
//            massOver = mutableListOf(
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "нет",
//                "нет",
//                "нет"
//            )
//        }
//
//        "Утки" -> {
//            massTemp = mutableListOf(
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "38.0",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.8",
//                "37.5",
//                "37.5",
//                "37.5",
//                "37.5"
//            )
//            massDamp = mutableListOf(
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "75",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "60",
//                "90",
//                "90",
//                "90",
//                "90"
//            )
//            massAiring = mutableListOf(
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "нет",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "2 раза по 20 мин",
//                "2 раза по 20 мин",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "2 раза по 15 мин",
//                "нет",
//                "нет",
//                "нет"
//            )
//            massOver = mutableListOf(
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4",
//                "4-6",
//                "4-6",
//                "4-6",
//                "4-6",
//                "4-6",
//                "4-6",
//                "4-6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "6",
//                "нет",
//                "нет",
//                "нет"
//            )
//        }
//    }
//}