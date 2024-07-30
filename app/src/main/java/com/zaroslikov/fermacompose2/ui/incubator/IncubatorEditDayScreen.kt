package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.TopAppBarEdit
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object IncubatorEditDayScreenDestination : NavigationDestination {
    override val route = "IncubatorEditDayScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun IncubatorEditDayScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: IncubatorEditDayViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val day = viewModel.day + 1
    val incubator = viewModel.incubatorState

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = "День $day",
                navigateUp = navigateBack,
            )
        }
    ) { innerPadding ->

        IncubatorEditDayContainer(
            incubator,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            saveDay = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    onNavigateUp()
                }
            },
            onValueChange = viewModel::updateUiState,
        )
    }

}

@Composable
fun IncubatorEditDayContainer(
    incubator: IncubatorUiState,
    modifier: Modifier = Modifier,
    saveDay: () -> Unit,
    onValueChange: (IncubatorUiState) -> Unit = {}
) {

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.padding(5.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = incubator.temp,
            onValueChange = {
                onValueChange(
                    incubator.copy(temp = it.replace(Regex("[^\\d.]"), "").replace(",", "."))
                )
            },
            label = { Text("Температура") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            supportingText = {
                Text("Укажите температуру")
            },
            suffix = { Text(text = "°C") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
            )
        )
        OutlinedTextField(
            value = incubator.damp.replace(Regex("[^\\d.]"), "").replace(",", "."),
            onValueChange = {
                onValueChange(
                    incubator.copy(damp = it)
                )
            },
            label = { Text("Влажность") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            supportingText = {
                Text("Укажите влажность")
            },
            suffix = { Text(text = "%") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                },
//            isError = () TODO
            )
        )
        OutlinedTextField(
            value = incubator.over,
            onValueChange = {
                onValueChange(
                    incubator.copy(over = it)
                )
            },
            label = { Text("Переворот") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            supportingText = {
                Text("Укажите кол-во переворачиваний")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
            )
        )
        OutlinedTextField(
            value = incubator.airing,
            onValueChange = {
                onValueChange(
                    incubator.copy(airing = it)
                )
            },
            label = { Text("Проветривание") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            supportingText = {
                Text("Укажите кол-во проветриваний")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
            )
        )

        Button(
            onClick = { saveDay() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_create_24),
                contentDescription = " Обновить "
            )
            Text(text = "Обновить")
        }
    }
}
