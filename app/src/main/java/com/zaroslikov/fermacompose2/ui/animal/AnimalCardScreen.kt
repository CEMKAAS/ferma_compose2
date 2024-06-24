package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object AnimalCardDestination : NavigationDestination {
    override val route = "animalCard"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AnimalCardProduct(
    navigateBack: () -> Unit,
    onNavigateSetting: (Int) -> Unit,
    onNavigateIndicators: (AnimalIndicators) -> Unit,
    viewModel: AnimalCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val animalTable = viewModel.animalState.collectAsState()
    val size = viewModel.sizeState.collectAsState()
    val count = viewModel.countState.collectAsState()
    val weight = viewModel.weightState.collectAsState()
    val vaccination = viewModel.vaccinationState.collectAsState()
    val modifierCardClicable = Modifier
        .fillMaxWidth()
        .padding(8.dp)

    Scaffold(topBar = {
        TopAppBarStart(
            title = animalTable.value.animalTable.name,
            true,
            navigateUp = navigateBack,
            settingUp = {  onNavigateSetting(animalTable.value.animalTable.id) }
        )
    }) { innerPadding ->
        AnimalCardContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            animalTable = animalTable.value.animalTable,
            animalWeightTable = weight.value.itemList,
            animalSizeTable = size.value.itemList,
            animalCountTable = count.value.itemList,
            animalVaccinationTable = vaccination.value.itemList,
            onNavigateIndicators = onNavigateIndicators
        )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    animalTable: AnimalTable,
    animalWeightTable: List<AnimalWeightTable>,
    animalSizeTable: List<AnimalSizeTable>,
    animalCountTable: List<AnimalCountTable>,
    animalVaccinationTable: List<AnimalVaccinationTable>,
    onNavigateIndicators: (AnimalIndicators) -> Unit,
    ) {

    val modifierCard = Modifier
        .fillMaxWidth()
        .padding(8.dp)

    val modifierHeading = Modifier
        .wrapContentSize()
        .padding(6.dp)

    val modifierText = Modifier
        .wrapContentSize()
        .padding(vertical = 3.dp, horizontal = 6.dp)

    Column(modifier = modifier) {
        Card(
            modifier = modifierCard
        ) {
            Text(text = "Имя: ${animalTable.name}", modifier = modifierText)
            Text(text = "Тип: ${animalTable.type}", modifier = modifierText)
            if (!animalTable.groop) {
                Text(text = "Пол: ${animalTable.sex}", modifier = modifierText)
            }
            Text(text = "Дата добавления: ${animalTable.data}", modifier = modifierText)
        }

        if (!animalTable.groop) {
            Card(
                modifier =  modifierCard.clickable {  onNavigateIndicators(AnimalIndicators(id = animalTable.id, table = "weight"))}
            ) {
                var i = 1
                Text(
                    text = "Вес", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                animalWeightTable.forEach {
                    Text(text = "${i++})  ${it.weight} кг. на ${it.date}", modifier = modifierText)
                }
            }

            Card(
                modifier = modifierCard.clickable {  onNavigateIndicators(AnimalIndicators(id = animalTable.id, table = "size"))}
            ) {
                var i = 1
                Text(
                    text = "Рост", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                animalSizeTable.forEach {
                    Text(text = "${i++}) ${it.size} М на ${it.date}", modifier = modifierText)
                }
            }
        } else {

            Card(
                modifier = modifierCard.clickable {  onNavigateIndicators(AnimalIndicators(id = animalTable.id, table = "count"))}
            ) {
                var i = 1
                Text(
                    text = "Количество", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                animalCountTable.forEach {
                    Text(text = "${i++}) ${it.count} шт. на ${it.date}", modifier = modifierText)
                }
            }
        }

        Card(
            modifier = modifierCard.clickable {  onNavigateIndicators(AnimalIndicators(id = animalTable.id, table = "vaccination"))}
        ) {
            var i = 1
            Text(
                text = "Прививки:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (animalVaccinationTable.isNotEmpty()) {
                animalVaccinationTable.forEach {
                    Text(text = "${i++}) ${it.vaccination} ${it.date}", modifier = modifierText)
                }
            } else {
                Text(text = "Нет добавленных прививок", modifier = modifierText)
            }
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Продукции получено:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = "600 кг тврога", modifier = modifierText)
            Text(text = "200 л молока", modifier = modifierText)
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Выручки получено:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = "220  рубля за 800 кг тврога", modifier = modifierText)
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Примечание:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = animalTable.note, modifier = modifierText)
        }

    }

}

data class AnimalIndicators(
    val id: Int,
    val table: String
)

//@Preview(showBackground = true)
//@Composable
//fun AnimalCardPrewie() {
//    AnimalCardContainer(modifier = Modifier.padding(5.dp))
//}
