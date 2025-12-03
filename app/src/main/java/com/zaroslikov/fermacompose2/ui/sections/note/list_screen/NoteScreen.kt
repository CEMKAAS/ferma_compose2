package com.zaroslikov.fermacompose2.ui.sections.note.list_screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet


object NoteDestination : NavigationDestination {
    override val route = "Note"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val idProject = state.idPT
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
           /* TopAppBarNavigation(
                title = R.string.note_screen_title,
                scope = coroutineScope,
                drawerState = drawerState,
                scrollBehavior = scrollBehavior
            )*/
        },
        floatingActionButton = { FloatButton { navigateToItemAdd(idProject) } },
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = modifier.padding(innerPadding),
            )
        else
            NoteBody(
                modifier = modifier
                    .modifierScreenLazy(innerPadding),
                itemList = state.list,
                onItemClick = navigateToItemUpdate,
                navigateToItemAdd = { navigateToItemAdd(idProject) }
            )
    }
}

@Composable
private fun NoteBody(
    itemList: List<DomainNoteTable>,
    onItemClick: (Pair<Long, Long>) -> Unit,
    modifier: Modifier = Modifier,
    navigateToItemAdd: () -> Unit
) {
    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            onItemClick = { onItemClick(it.idPT to it.id) },
            modifier = modifier
        )
    else MessageNoData(
        modifier = modifier,
        onClick = navigateToItemAdd,
        titleRes = R.string.message_no_date_title_note,
        messageRes = R.string.message_no_date_message_note,
        supportRes = R.string.message_no_date_support_text_note,
        buttonRes = R.string.button_note_message_no_data
    )
}


@Composable
private fun InventoryList(
    modifier: Modifier = Modifier,
    itemList: List<DomainNoteTable>,
    onItemClick: (DomainNoteTable) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = itemList, key = { it.id }) { item ->
            NoteCard(
                noteTable = item,
                modifier = Modifier
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
fun NoteCard(
    noteTable: DomainNoteTable,
    modifier: Modifier = Modifier
) {
    CardField(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(start = 3.dp, bottom = 10.dp),
                text = noteTable.title,
                style = textBold_20,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconAndText(
                iconRes = R.drawable.baseline_sticky_note_2_24,
                valueString = noteTable.note
            )
            IconAndText(
                iconRes = R.drawable.baseline_calendar_month_24,
                valueString = noteTable.date
            )
        }
    }
}

