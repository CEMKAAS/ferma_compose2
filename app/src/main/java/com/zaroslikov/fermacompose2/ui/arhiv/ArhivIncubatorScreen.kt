package com.zaroslikov.fermacompose2.ui.arhiv

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.ferma.IncubatorTemp
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorUIList
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorViewModel
import com.zaroslikov.fermacompose2.ui.incubator.MyRowIncubatorSettting
import com.zaroslikov.fermacompose2.ui.incubator.massList
import com.zaroslikov.fermacompose2.ui.incubator.massList2
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


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
    val temp by viewModel.tempState.collectAsState()
    val damp by viewModel.dampState.collectAsState()
    val over by viewModel.overState.collectAsState()
    val airng by viewModel.airingState.collectAsState()
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
            incubatorTemp = temp.list,
            incubatorDamp = damp.list,
            incubatorOver = over.list,
            incubatorAiring = airng.list,
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
    incubatorTemp: IncubatorTemp,
    incubatorDamp: IncubatorUIList,
    incubatorOver: IncubatorUIList,
    incubatorAiring: IncubatorUIList,
    projectTable: IncubatorProjectEditState,
    deleteInc: () -> Unit
) {
    val tempList = massList2(incubatorTemp)
    val dampList = massList(incubatorDamp)
    val overList = massList(incubatorOver)
    val airingList = massList(incubatorAiring)

    LazyColumn(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            IncubatorCard(
                projectTable, modifier = Modifier
                    .padding(6.dp)
            )
        }
        items(tempList.size) {
            MyRowIncubatorSettting(
                it,
                tempList[it],
                dampList[it],
                overList[it],
                airingList[it],
                modifier = Modifier
                    .padding(6.dp),
                borderStroke = null,
                typeBird = projectTable.type,
                navigateOvos = {}
            )
        }
        item {
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(text = "Завершить")
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

