package com.zaroslikov.fermacompose2.ui.animal.list_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CircularProgress
import com.zaroslikov.fermacompose2.ui.composeElement.FloatButton
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndText
import com.zaroslikov.fermacompose2.ui.composeElement.MessageNoData
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
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
    navigateToItemCard: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: (Long) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    isFirstStart: Boolean,
    viewModel: AnimalViewModel = hiltViewModel()
) {
    val idProject = viewModel.itemId

    val animalUiState by viewModel.animalUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutineScope = rememberCoroutineScope()

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
                TopAppBarNavigation(
                    title = R.string.animals_screen_title,
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatButton { navigateToItemAdd(idProject) }
            }
        ) { innerPadding ->
            if (isLoading)
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
             else
                AnimalBody(
                    modifier = Modifier
                        .modifierScreenLazy(innerPadding),
                    itemList = animalUiState.itemList,
                    onItemClick = { navigateToItemCard(Pair(idProject, it)) },
                    navigateToItemAdd = { navigateToItemAdd(idProject) }
                )

        }
    }
}


@Composable
private fun AnimalBody(
    modifier: Modifier = Modifier,
    itemList: List<DomainAnimalWithCount>,
    onItemClick: (Long) -> Unit,
    navigateToItemAdd: () -> Unit
) {
    if (itemList.isNotEmpty())
        AnimalList(
            itemList = itemList,
            onItemClick = onItemClick,
            modifier = modifier
        )
    else MessageNoData(
        modifier = modifier,
        onClick = navigateToItemAdd,
        titleRes = R.string.message_no_date_title_animals,
        messageRes = R.string.message_no_date_message_animals,
        supportRes = R.string.message_no_date_support_text_animals,
        buttonRes = R.string.button_animals_message_no_data
    )
}

@Composable
private fun AnimalList(
    modifier: Modifier = Modifier,
    itemList: List<DomainAnimalWithCount>,
    onItemClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier, verticalArrangement = Arrangement.Top
    ) {
        items(items = itemList, key = { it.id }) { item ->
            AnimalCard(
                animalTable = item,
                modifier = Modifier.clickable { onItemClick(item.id) })
        }
    }
}

@Composable
fun AnimalCard(
    modifier: Modifier = Modifier,
    animalTable: DomainAnimalWithCount
) {
    CardField(
        modifier = modifier,
        row = false,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = animalTable.name,
            style = textBold_16
        )
        IconAndText(
            iconRes = R.drawable.baseline_pets_24,
            valueString = animalTable.type
        )
        if (!animalTable.group)
            IconAndText(
                iconRes = if (animalTable.sex) R.drawable.baseline_male_24 else R.drawable.baseline_female_24,
                valueString = stringResource(
                    if (animalTable.sex) R.string.animal_entry_screen_sex_man else R.string.animal_entry_screen_sex_woman
                )
            )
        else
            IconAndText(
                iconRes = R.drawable.baseline_spoke_24,
                valueString = "${animalTable.count} ${animalTable.suffix}"
            )

        IconAndText(
            iconRes = if (animalTable.dateFactory == null) R.drawable.baseline_calendar_month_24 else R.drawable.baseline_event_24,
            valueString = animalTable.dateFactory ?: animalTable.date
        )
    }
}

