package com.zaroslikov.fermacompose2.ui.incubator

import android.database.Cursor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination


object IncubatorScreenDestination : NavigationDestination {
    override val route = "IncubatorScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun IncubatorScreen(
    navigateBack: () -> Unit,
    viewModel: IncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val temp = viewModel.tempState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarStart(
                title = "Инкубатор",
                true,
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorContainer(
            modifier = Modifier.padding(innerPadding),
            incubatorTemp = temp.value.itemList
        )
    }
}

@Composable
fun IncubatorContainer(
    modifier: Modifier,
    incubatorTemp: List<IncubatorTemp>
) {

    val mass = mutableListOf<String>()
    for (i in 0..20) {
        mass.add(incubatorTemp[0].day1)
    }


    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        items(incubatorTemp[0].id) {
            MyRowIncubatorSettting(
                it,
                incubatorTemp[it]
            )
        }
    }
}

private fun setCursor(cursor: Cursor, sizeBegin: Int, size: Int): MutableList<String> {
    cursor.moveToNext()
    val mass = mutableListOf<String>()
    for (i in sizeBegin..size) {
        mass.add(cursor.getString(i).toString())
    }
    cursor.close()
    return mass
}


@Composable
fun MyRowIncubatorSettting(
    n: Int,
    temp: IncubatorTemp
//    ovos: Boolean,
//    ovosShowBottom: MutableState<Boolean>
) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .clickable {
            },
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {

        //clikable
        Text(
            text = "День ${n + 1}",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Температура ${temp}°C",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Влажность 60%",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Переворачивать 2-3 раза",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Проветривать 2 раза по 5 минут",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
        }
//        if (ovos) {
//            TextButton(
//                onClick = { ovosShowBottom.value = true },
//                modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    text = "Овоскопирование",
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                )
//            }
//        }
    }
}
