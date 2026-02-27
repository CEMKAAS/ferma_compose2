package com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.TopAppBarNewFilter
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_11
import com.zaroslikov.fermacompose2.orang_12
import com.zaroslikov.fermacompose2.orang_13
import com.zaroslikov.fermacompose2.orang_14
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.SearchBar
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.dateBuilder
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.DetailBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet


object NoteDestination : NavigationDestination {
    override val route = "Note"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val colors = listOf(orang_11, orang_12)

    val query = state.textSearch.trim().lowercase()
    val searchList = if (query.isBlank()) state.list
    else
        state.list.filter { item ->
            val date = item.date.split(".")
            val monthText = stringResource(id = monthToResString(date[1].toInt()))
            item.title.lowercase().contains(query) ||
                    item.note.lowercase().contains(query) ||
                    dateBuilder(date[0].toInt(), monthText, date[2].toInt()).lowercase()
                        .contains(query)
        }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNewFilter(
                title = stringResource(R.string.note_screen_title),
                navigateBack = navigateBack,
                scrollBehavior = scrollBehavior,
                content = {
                    SearchBar(
                        value = state.textSearch,
                        onValueChange = { viewModel.onIntent(NoteListIntent.SearchChanged(it)) },
                        intRes = R.string.note_screen_search_note
                    )
                }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(NoteListIntent.OpenBottomSheetEntry(true))
            }
        },
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = modifier.padding(innerPadding),
            )
        else
            NoteContainer(
                modifier = modifier
                    .modifierScreenLazy(innerPadding),
                itemList = state.list,
                color = colors.first(),
                searchList = searchList,
                onInsertClick = { viewModel.onIntent(NoteListIntent.OpenBottomSheetEntry(true)) },
                onEditClick = { viewModel.onIntent(NoteListIntent.OpenBottomSheetEntry(true, it)) },
                onDeleteClick = { viewModel.onIntent(NoteListIntent.Delete(it)) },
                onDetailClick = {
                    viewModel.onIntent(NoteListIntent.OpenBottomSheetDetail(true, it))
                }
            )
        if (state.openBottomSheetEntry)
            NoteEntryBottomSheet(
                colors = colors,
                state = state.currentProduct,
                onIntent = viewModel::onIntent
            )
        if (state.openBottomSheetDetail)
            NoteDetailBottomSheet(
                state = state.detailDomainNoteTable,
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
private fun NoteContainer(
    modifier: Modifier = Modifier,
    color: Color = blue_1,
    itemList: List<DomainNoteTable>,
    searchList: List<DomainNoteTable>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainNoteTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailClick: (Int) -> Unit,
) {
    InventoryBody(
        modifier = modifier,
        details = true,
        itemList = itemList,
        searchList = searchList,
        brieflyList = emptyList<Int>(),
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailsClick = {},
        detailCard = { index, item ->
            NoteCard(
                title = item.title,
                note = item.note,
                date = item.date,
                color = color,
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
                onDetailClick = { onDetailClick(index) }
            )
        },
        brieflyCard = {},
        titleRes = R.string.message_no_data_title_note,
        messageRes = R.string.message_no_data_message_note,
        iconRes = R.drawable.icon_warning,
        iconColor = orang_11,
        backgroundColor = orang_12
    )
}

@Composable
fun NoteCard(
    title: String,
    note: String,
    date: String,
    color: Color,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val date = date.split(".")
    val monthText = stringResource(id = monthToResString(date[1].toInt()))
    val date2 = dateBuilder(date[0].toInt(), monthText, date[2].toInt())
    CardFieldNew(
        onClick = onDetailClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconTransaction2(
                        icon = R.drawable.baseline_sticky_note_2_24,
                        colorIcon = color,
                        color = orang_13
                    )
                    Text(
                        text = title,
                        style = text_16,
                        color = black_2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                DropdownMenuEdit(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
            Text(
                text = note, style = text_16, color = marengo, maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            IconAndTextNew(
                iconRes = R.drawable.baseline_calendar_month_24,
                valueString = date2,
                iconColor = color
            )
        }
    }
}

@Composable
private fun NoteEntryBottomSheet(
    colors: List<Color>,
    state: NoteEntryState2,
    onIntent: (NoteListIntent) -> Unit
) {
    EntryBottomSheet(
        modifier = Modifier,
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(
                NoteListIntent.OpenBottomSheetEntry(false)
            )
        },
        onInsertClick = { onIntent(NoteListIntent.Insert) },
        onUpdateClick = { onIntent(NoteListIntent.Update) }
    ) {
        OutlinedTextNew(
            value = state.title,
            onValueChange = { onIntent(NoteListIntent.TitleChanged(it)) },
            isError = state.error.isErrorTitle,
            labelIntRes = R.string.note_screen_note_title,
            supportingText = R.string.note_screen_note_support_title
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(NoteListIntent.NoteChanged(it)) },
            labelIntRes = R.string.note_screen_note_text,
            supportingText = R.string.note_screen_note_support_text,
            minLines = 6
        )
    }
}

@Composable
private fun NoteDetailBottomSheet(
    state: DomainNoteTable,
    onIntent: (NoteListIntent) -> Unit
) {
    val date = state.date.split(".")
    val monthText = stringResource(id = monthToResString(date[1].toInt()))

    DetailBottomSheet(
        modifier = Modifier,
        onDismissRequest = {
            onIntent(NoteListIntent.OpenBottomSheetDetail(false))
        },
        title = state.title,
        date = dateBuilder(date[0].toInt(), monthText, date[2].toInt()),
        onDeleteClick = {
            onIntent(NoteListIntent.Delete(state.id))
            onIntent(NoteListIntent.OpenBottomSheetDetail(false))
        },
        onUpdateClick = { onIntent(NoteListIntent.OpenBottomSheetEntry(true, state)) },
    ) {
        BorderCard(
            modifier = Modifier.fillMaxWidth(),
            padding = PaddingValues(26.dp),
            shape = RoundedCornerShape(16.dp),
            borderWidth = 2.dp,
            borderColor = orang_14,
            containerColor = orang_13.copy(alpha = 0.5f)
        ) {
            Text(text = state.note, style = text_16, color = dark, textAlign = TextAlign.Justify)
        }
    }
}




