@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.GroupConfirmDialog
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.NoteWidget

object AnimalHerdCardDestination : NavigationDestination {
    override val route = "animalHerdCard"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

/**
 * Карточка сборной группы: шапка с итогами по членам, «Состав группы» (→ корректировка), продукция
 * и примечание.
 * Название и фото меняются на экране редактирования (шестерёнка), поголовье — только итог.
 */
@Composable
fun AnimalHerdCardScreen(
    navigateBack: () -> Unit,
    onNavigateEdit: (Pair<Long, Long>) -> Unit,
    onNavigateCount: (Pair<Long, Long>) -> Unit,
    onNavigateAnimalCard: (Pair<Long, Long>) -> Unit,
    viewModel: AnimalHerdCardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Срабатывает и когда экран вернулся на вершину стека после закрытия в фоне
    LaunchedEffect(state.isClosed) {
        if (state.isClosed) navigateBack()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                title = state.herd.name,
                onNavigateBackClick = navigateBack,
                onSettingsClick = if (state.isArchive) null else {
                    { onNavigateEdit(state.herd.idPT to state.herd.id) }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(modifier = Modifier.padding(innerPadding))
        else
            AnimalHerdCardContainer(
                modifier = Modifier.modifierScreen(innerPadding),
                state = state,
                onIntent = viewModel::onIntent,
                onAdjustClick = { onNavigateCount(state.herd.idPT to state.herd.id) },
                onMemberClick = { onNavigateAnimalCard(state.herd.idPT to it) }
            )
    }
}

@Composable
private fun AnimalHerdCardContainer(
    modifier: Modifier,
    state: AnimalHerdCardState,
    onIntent: (AnimalHerdCardIntent) -> Unit,
    onAdjustClick: () -> Unit,
    onMemberClick: (Long) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimalHerdHeader(herd = state.herd, weight = state.weight)
        AnimalHerdMembers(
            herd = state.herd,
            canAdjust = !state.isArchive,
            onAdjustClick = onAdjustClick,
            onMemberClick = onMemberClick
        )
        AnimalHerdProducts(products = state.products)
        AnimalHerdProductsByMember(
            products = state.products,
            productsByMember = state.productsByMember,
            selectedProduct = state.selectedProduct,
            onProductSelected = { onIntent(AnimalHerdCardIntent.ProductSelected(it)) },
            onMemberClick = onMemberClick
        )
        if (!state.isArchive || state.herd.note.isNotEmpty())
            NoteWidget(note = state.herd.note, isArchive = state.isArchive) {
                onIntent(AnimalHerdCardIntent.NoteChanged(it))
            }
        if (!state.isArchive)
            AnimalHerdActions(
                canDepersonalize = state.individualsCount > 0,
                enabled = !state.isOperationRunning,
                onDepersonalizeClick = { onIntent(AnimalHerdCardIntent.OpenDepersonalizeDialog(true)) },
                onDisbandClick = { onIntent(AnimalHerdCardIntent.OpenDisbandDialog(true)) }
            )
    }

    if (state.isOpenDepersonalizeDialog)
        GroupConfirmDialog(
            titleRes = R.string.animal_group_depersonalize_title,
            iconRes = R.drawable.ic_herd_depersonalize,
            text = pluralStringResource(
                R.plurals.animal_group_depersonalize_text,
                state.individualsCount,
                state.individualsCount
            ),
            textButtonRes = R.string.animal_group_button_depersonalize,
            enabled = !state.isOperationRunning,
            onDismissRequest = { onIntent(AnimalHerdCardIntent.OpenDepersonalizeDialog(false)) },
            onConfirm = { onIntent(AnimalHerdCardIntent.DepersonalizeConfirmed) }
        )
    if (state.isOpenDisbandDialog)
        GroupConfirmDialog(
            titleRes = R.string.animal_group_disband_title,
            iconRes = R.drawable.ic_herd_disband,
            text = stringResource(R.string.animal_group_disband_text),
            textButtonRes = R.string.animal_group_button_disband,
            enabled = !state.isOperationRunning,
            onDismissRequest = { onIntent(AnimalHerdCardIntent.OpenDisbandDialog(false)) },
            onConfirm = { onIntent(AnimalHerdCardIntent.DisbandConfirmed) }
        )
}
