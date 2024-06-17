package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.TopAppBarEdit
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination


object IncubatorEditDayScreenDestination : NavigationDestination {
    override val route = "IncubatorEditDayScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}
@Composable
fun IncubatorEditDayScreen(
    navigateBack: () -> Unit,
    viewModel: IncubatorEditDayViewModel  = viewModel(factory = AppViewModelProvider.Factory)
) {

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = "Инкубатор",
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorEditDayContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        )
    }

}

@Composable
fun IncubatorEditDayContainer(
    modifier: Modifier = Modifier
)
{
    var text by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier.padding(5.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var text by rememberSaveable { mutableStateOf("") }

        Text(
            text = "День 1",
            fontSize = 25.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Температура") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите температуру")
            },
            suffix = { Text(text = "°C") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            //            isError = () TODO
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Влажность") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите влажность")
            },
            suffix = { Text(text = "%") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            isError = () TODO
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Переворот") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите кол-во переворачиваний")
            },
            suffix = { TODO() },
//            isError = () TODO
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Проветривание") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите кол-во проветриваний")
            },
//            isError = () TODO
        )

        Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(vertical = 10.dp)) {
            Text(text = "Обновить")
            //TODO Изображение
        }
    }
}