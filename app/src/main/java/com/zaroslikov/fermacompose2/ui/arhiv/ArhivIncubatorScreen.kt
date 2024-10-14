package com.zaroslikov.fermacompose2.ui.arhiv

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorViewModel
import com.zaroslikov.fermacompose2.ui.incubator.MyRowIncubatorSettting
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object IncubatorArhivDestination : NavigationDestination {
    override val route = "IncubatorArhiv"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun IncubatorArhivScreen(
    navigateBack: () -> Unit,
    navigateStart: () -> Unit,
    viewModel: IncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val incubator by viewModel.incubatorUiState.collectAsState()
    val projectState by viewModel.homeUiState.collectAsState()
    val project = viewModel.itemUiState

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = projectState.project.titleProject,
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            incubator = incubator.itemList,
            projectTable = project,
            deleteInc = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateStart()
                }
            }
        )
    }
}


@SuppressLint("SimpleDateFormat")
@Composable
fun IncubatorContainer(
    modifier: Modifier,
    incubator: List<Incubator>,
    projectTable: IncubatorProjectEditState,
    deleteInc: () -> Unit
) {

    LazyColumn(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            IncubatorCard(
                projectTable, modifier = Modifier
                    .padding(6.dp)
            )
        }
        items(incubator.size) {
            MyRowIncubatorSettting(
                incubator[it],
                modifier = Modifier
                    .padding(6.dp),
                borderStroke = null,
                typeBird = projectTable.type,
                navigateOvos = {}
            )
        }
        item {
            Button(
                onClick = { deleteInc()},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(text = "Удалить")
            }
        }
    }
}


@Composable
fun IncubatorCard(projectTable: IncubatorProjectEditState, modifier: Modifier) {

    val modifierText = Modifier
        .wrapContentSize()
        .padding(vertical = 3.dp, horizontal = 6.dp)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Text(
            text = "Данные",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Text(text = "Вид птицы: ${projectTable.type}", modifier = modifierText)
        Text(text = "Заложено яиц: ${projectTable.eggAll}", modifier = modifierText)
        Text(text = "Вылупилось птенцов: ${projectTable.eggAllEND}", modifier = modifierText)
        Text(text = "Дата закладки: ${projectTable.data}", modifier = modifierText)
        Text(text = "Дата вылупения: ${projectTable.dateEnd}", modifier = modifierText)
    }
}

