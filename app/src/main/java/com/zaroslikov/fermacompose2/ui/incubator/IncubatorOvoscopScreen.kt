package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object IncubatorOvoscopDestination : NavigationDestination {
    override val route = "IncubatorOvoscopScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemDay"
    const val itemIdArgTwo = "itemType"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun IncubatorOvoscopScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: IncubatorOvoscopViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = "Инкубатор",
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorEditDayContainer(
            day = viewModel.dayVM,
            typeBird = viewModel.typeBirds,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            onNavigateUp =  onNavigateUp
        )
    }

}

@Composable
fun IncubatorEditDayContainer(
    day: Int,
    typeBird :String,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit
) {

    val image = setOvoskopImage(day,typeBird)

    Column(
        modifier = modifier.padding(5.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "День ${day}",
            fontSize = 25.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )

        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "тут кaкой-то текст",
            fontSize = 25.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onNavigateUp ,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(text = "Обновить")
            //TODO Изображение
        }
    }
}

fun setOvoskopImage( day: Int, typeBird: String,): Int{
    //todo доделать овоскоп
    when (typeBird) {
        "Курицы" -> {
            return when (day) {
                7 -> R.drawable.chicken
                11 -> R.drawable.chicken
                16 -> R.drawable.chicken
                else -> {
                    R.drawable.chicken
                }
            }
        }

        "Индюки", "Утки" -> {
            return when (day) {
                8 -> R.drawable.chicken
                14 -> R.drawable.chicken
                25 -> R.drawable.chicken
                else -> {
                    R.drawable.chicken
                }
            }
        }

        "Гуси" -> {
            return when (day) {
                9 -> R.drawable.chicken
                15 -> R.drawable.chicken
                21 -> R.drawable.chicken
                else -> {
                    R.drawable.chicken
                }
            }
        }

        "Перепела" -> {
            return when (day) {
                6 -> R.drawable.chicken
                13 -> R.drawable.chicken
                else -> {
                    R.drawable.chicken
                }
            }
        }

        else -> {
            return R.drawable.chicken
        }
    }

}