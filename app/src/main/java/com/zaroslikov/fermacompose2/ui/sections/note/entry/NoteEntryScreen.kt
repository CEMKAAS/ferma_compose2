//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package com.zaroslikov.fermacompose2.ui.sections.note.entry
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.zaroslikov.fermacompose2.R
//import com.zaroslikov.fermacompose2.ui.elements.OutlinedText
//import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
//import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
//import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
//import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
//import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
//import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
//
//
//object NoteEntryDestination : NavigationDestination {
//    override val route = "NoteEntry"
//    override val titleRes = R.string.app_name
//    const val itemIdPT = "itemIdPT"
//    const val itemId = "itemId"
//    val routeWithArgs = "${route}?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
//}
//
//
//@Composable
//fun NoteEntryProduct(
//    navigateBack: () -> Unit,
//    onNavigateUp: () -> Unit,
//    viewModel: NoteEntryViewModel = hiltViewModel()
//) {
//    val eventFlow = viewModel.navigation
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
//
//    LaunchedEffect(Unit) {
//        eventFlow.collect { event ->
//            when (event) {
//                is UiEvent.NavigateBack -> navigateBack()
//            }
//        }
//    }
//
//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            TopAppBarBack(
//                intRes = R.string.note_screen_title,
//                onNavigateBackClick = navigateBack,
//                scrollBehavior = scrollBehavior
//            )
//        }
//    ) { innerPadding ->
//        NoteEntryContainer(
//            modifier = Modifier
//                .modifierScreen(innerPadding),
//            state = state,
//            onIntent = viewModel::onIntent,
//        )
//    }
//}
//
//@Composable
//fun NoteEntryContainer(
//    modifier: Modifier,
//    state: NoteEntryState,
//    onIntent: (NoteEntryIntent) -> Unit
//) {
//    Column(modifier = modifier) {
//        OutlinedText(
//            value = state.domainNoteTable.title,
//            isError = state.error.isErrorTitle,
//            intRes = R.string.outlined_text_title,
//            intResSup = R.string.support_text_title,
//            intResError = R.string.error_no_title,
//            onValueChange = { onIntent(NoteEntryIntent.TitleChanged(it)) },
//        )
//        OutlinedTextNote(
//            value = state.domainNoteTable.note,
//            label = R.string.outlined_text_note_note,
//            onValueChange = { onIntent(NoteEntryIntent.NoteChanged(it)) },
//        )
//        ButtonPanel(
//            isEntry = state.isEntry,
//            entryButton = R.string.button_note,
//            onClickInsert = { onIntent(NoteEntryIntent.Insert) },
//            onClickUpdate = { onIntent(NoteEntryIntent.Update) },
//            onClickDelete = { onIntent(NoteEntryIntent.Delete) },
//        )
//    }
//}
