package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination

import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet

object AnimalDestination : NavigationDestination {
    override val route = "animal"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemCard: (Int) -> Unit,
    navigateToItemAdd: (Int) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    isFirstStart:Boolean,
    viewModel: AnimalViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val animalUiState by viewModel.animalUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val idProject = viewModel.itemId

    val coroutineScope = rememberCoroutineScope()

    val showBottomSheetFilter = remember { mutableStateOf(false) }

    var openFirstDialog by rememberSaveable { mutableStateOf(isFirstStart)}

    if(openFirstDialog){
        AlertDialogExample(
            onDismissRequest = { openFirstDialog =  false },
            onConfirmation = { openFirstDialog  = false },
            dialogTitle = "Мои Животные",
            dialogText = "В этом разделе отображаются Ваши животные. Для добавления нажмите на знак «+» в правом нижнем углу. Рекомендуем начать с добавления животных для корректной работы приложения. После перейдите  в раздел Моя Продукция",
            icon = Icons.Default.Info
        )
    }




    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = navigateToStart,
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                7,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarFerma(
                    title = "Мои Животные",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigateToItemAdd(idProject) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.item_entry_title) // TODO Преименовать
                    )
                }
            }
        ) { innerPadding ->
            AnimalBody(
                itemList = animalUiState.itemList,
                onItemClick = navigateToItemCard,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding
            )
        }
    }
}


@Composable
private fun AnimalBody(
    itemList: List<AnimalTable>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemList.isEmpty()) {
            Column(modifier = modifier.padding(contentPadding).padding(15.dp)) {
                Text(
                    text = "Добро пожаловать в раздел \"Мои Животные!\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "В этом разделе вы можете регистрировать животных, находящихся на вашей ферме! Каждое животное можно добавить как по отдельности, так и в группе. Для группового добавления укажите количество, а для отдельных животных — их вес и размер. Для всех животных необходимо указать имя, вид, информацию о прививках и дополнительные примечания. При добавлении товара в разделе \"Мои Товары\" внутри карточки животного, если указать конкретное животное, Вы сможете отслеживать объем произведенного им товара.",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "Сейчас нет животных:(\nНажмите + чтобы добавить.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                )
            }
        } else {
            AnimalList(
                itemList = itemList,
                onItemClick = onItemClick,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun AnimalList(
    itemList: List<AnimalTable>,
    onItemClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = itemList, key = { it.id }) { item ->
            AnimalCard(animalTable = item,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onItemClick(item.id) })
        }
    }
}

@Composable
fun AnimalCard(
    animalTable: AnimalTable,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(
                    text = animalTable.name,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Тип: ${animalTable.type}", modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 3.dp, horizontal = 6.dp)
                )
                if (!animalTable.groop) {
                    Text(
                        text = "Пол: ${animalTable.sex}", modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                Text(
                    text = "Дата: ${animalTable.data}", modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 3.dp, horizontal = 6.dp)
                )
            }
        }
    }
}