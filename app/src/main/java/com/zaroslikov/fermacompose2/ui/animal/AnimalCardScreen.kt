package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.navigate.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatter
import io.appmetrica.analytics.AppMetrica


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
    val animalTable = viewModel.itemUiState
    val size = viewModel.sizeState.collectAsState()
    val count = viewModel.countState.collectAsState()
    val weight = viewModel.weightState.collectAsState()
    val vaccination = viewModel.vaccinationState.collectAsState()
    val product = viewModel.productState(animalTable.name).collectAsState()

    Scaffold(topBar = {
        TopAppBarStart(
            title = animalTable.name,
            true,
            navigateUp = navigateBack,
            settingUp = { onNavigateSetting(animalTable.id) }
        )
    }) { innerPadding ->
        AnimalCardContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            animalTable = animalTable,
            animalWeightTable = weight.value.itemList,
            animalSizeTable = size.value.itemList,
            animalCountTable = count.value.itemList,
            animalVaccinationTable = vaccination.value.itemList,
            animalProductTable = product.value.itemList,
            onNavigateIndicators = onNavigateIndicators
        )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    animalTable: AnimalEditUiState,
    animalWeightTable: List<AnimalWeightTable>,
    animalSizeTable: List<AnimalSizeTable>,
    animalCountTable: List<AnimalCountTable>,
    animalVaccinationTable: List<AnimalVaccinationTable>,
    animalProductTable: List<AnimalTitSuff>,
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
            Text(
                text = "Данные", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = "Тип: ${animalTable.type}", modifier = modifierText)
            if (!animalTable.groop) {
                Text(text = "Пол: ${animalTable.sex}", modifier = modifierText)
            }
            Text(text = "Дата добавления: ${animalTable.data}", modifier = modifierText)
            Text(text = "Стомость: ${animalTable.price} ₽", modifier = modifierText)
            Text(text = "Потребления корма: ${animalTable.foodDay} кг", modifier = modifierText)
        }

        if (!animalTable.groop) {
            Card(
                modifier = modifierCard.clickable {
                    onNavigateIndicators(
                        AnimalIndicators(
                            id = animalTable.id,
                            table = "Вес"
                        )
                    )
                    AppMetrica.reportEvent("Животные Вес")
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                        var i = 1
                        Text(
                            text = "Вес", modifier = modifierHeading,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        if (animalWeightTable.isNotEmpty()) {
                            animalWeightTable.forEach {
                                Text(
                                    text = "${i++}) ${it.weight} кг. на ${it.date}",
                                    modifier = modifierText
                                )
                            }
                        } else {
                            Text(
                                text = "Данных нет! Нажмите, чтобы добавить",
                                modifier = modifierText
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            onNavigateIndicators(
                                AnimalIndicators(
                                    id = animalTable.id,
                                    table = "Вес"
                                )
                            )
                            AppMetrica.reportEvent("Животные Вес")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_output_24),
                            contentDescription = "Показать меню"
                        )
                    }
                }
            }

            Card(
                modifier = modifierCard.clickable {
                    onNavigateIndicators(
                        AnimalIndicators(
                            id = animalTable.id,
                            table = "Размер"
                        )
                    )
                    AppMetrica.reportEvent("Животные Размер")
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                        var i = 1
                        Text(
                            text = "Размер", modifier = modifierHeading,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        if (animalSizeTable.isNotEmpty()) {
                            animalSizeTable.forEach {
                                Text(
                                    text = "${i++}) ${it.size} м. на ${it.date}",
                                    modifier = modifierText
                                )
                            }
                        } else {
                            Text(
                                text = "Данных нет! Нажмите, чтобы добавить",
                                modifier = modifierText
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onNavigateIndicators(
                                AnimalIndicators(
                                    id = animalTable.id,
                                    table = "Количество"
                                )
                            )
                            AppMetrica.reportEvent("Животные Ко-во")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_output_24),
                            contentDescription = "Показать меню"
                        )
                    }
                }
            }

        } else {

            Card(
                modifier = modifierCard.clickable {
                    onNavigateIndicators(
                        AnimalIndicators(
                            id = animalTable.id,
                            table = "Количество"
                        )
                    )
                    AppMetrica.reportEvent("Животные Размер")
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.fillMaxWidth(0.85f)) {

                        var i = 1
                        Text(
                            text = "Количество", modifier = modifierHeading,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )

                        if (animalCountTable.isNotEmpty()) {
                            animalCountTable.forEach {
                                Text(
                                    text = "${i++}) ${formatter(it.count.toDouble())} шт. на ${it.date}",
                                    modifier = modifierText
                                )
                            }
                        } else {
                            Text(
                                text = "Данных нет! Нажмите, чтобы добавить",
                                modifier = modifierText
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onNavigateIndicators(
                                AnimalIndicators(
                                    id = animalTable.id,
                                    table = "Количество"
                                )
                            )
                            AppMetrica.reportEvent("Животные Ко-во")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_output_24),
                            contentDescription = "Показать меню"
                        )
                    }

                }
            }
        }

        Card(
            modifier = modifierCard.clickable {
                onNavigateIndicators(
                    AnimalIndicators(
                        id = animalTable.id,
                        table = "Прививки"
                    )
                )
                AppMetrica.reportEvent("Животные Прививки")
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                    var i = 1
                    Text(
                        text = "Прививки:", modifier = modifierHeading,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    if (animalVaccinationTable.isNotEmpty()) {
                        animalVaccinationTable.forEach {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "${i++}) ${it.vaccination}",
                                    modifier = modifierText.fillMaxWidth(0.6f)
                                )
                                Text(
                                    text = "${it.date} - ${it.nextVaccination}",
                                    modifier = Modifier.padding(
                                        vertical = 3.dp,
                                        horizontal = 15.dp
                                    ),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    } else {
                        Text(text = "Нет добавленных прививок", modifier = modifierText)
                    }
                }
                IconButton(
                    onClick = {
                        onNavigateIndicators(
                            AnimalIndicators(
                                id = animalTable.id,
                                table = "Прививки"
                            )
                        )
                        AppMetrica.reportEvent("Животные Прививки")
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_output_24),
                        contentDescription = "Показать меню"
                    )
                }
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
            var i = 1
            if (animalProductTable.isNotEmpty()) {
                animalProductTable.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${i++}) ${it.title}",
                            modifier = modifierText.fillMaxWidth(0.6f)
                        )
                        Text(
                            text = "${formatter(it.priceAll)} ${it.suffix}",
                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 15.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            } else {
                Text(text = "Пока ничего нет :(", modifier = modifierText)
            }
        }


        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Примечание:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (animalTable.note == "") {
                Text(text = "Здесь пока пусто:(", modifier = modifierText)
            } else {
                Text(text = animalTable.note, modifier = modifierText)
            }

        }

    }

}


data class AnimalIndicators(
    val id: Int,
    val table: String
)

