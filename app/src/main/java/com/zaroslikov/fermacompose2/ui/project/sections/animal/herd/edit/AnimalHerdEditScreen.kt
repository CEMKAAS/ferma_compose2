@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.AnimalNameOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.warehouse.MainSettingsCard

object AnimalHerdEditDestination : NavigationDestination {
    override val route = "animal_herd_edit"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalHerdEditScreen(
    navigateBack: () -> Unit,
    viewModel: AnimalHerdEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            if (event is UiEvent.NavigateBack) navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.animal_herd_edit_title,
                onNavigateBackClick = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(modifier = Modifier.padding(innerPadding))
        else
            Column(
                modifier = Modifier.modifierScreen(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MainSettingsCard(
                    currentIcon = state.currentIcon ?: AppIcon.PETS,
                    imagePath = state.imagePath,
                    iconList = state.iconList,
                    iconBoxColor = price_green_2,
                    onImageSelected = { viewModel.onIntent(AnimalHerdEditIntent.ImagePathClicked(it)) },
                    onIconSelected = { viewModel.onIntent(AnimalHerdEditIntent.IconClicked(it)) },
                )
                AnimalNameOutlinedTextNew(
                    value = state.name,
                    onValueChange = { viewModel.onIntent(AnimalHerdEditIntent.NameChanged(it)) },
                    isAnimalGroup = true,
                    isErrorTitle = state.name.isBlank()
                )
                WarningCard(
                    colorBackground = blue_3,
                    colorBorder = blue_9,
                    colorIcon = blue_1,
                    colorIconBackground = blue_13,
                    colorTitle = blue_14,
                    colorText = blue_8,
                    icon = R.drawable.outline_info_24,
                    title = R.string.animal_composite_edit_title,
                    text = R.string.animal_composite_edit_text
                )
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_text_edit_title),
                    onClick = { viewModel.onIntent(AnimalHerdEditIntent.Save) },
                    colors = listOf(price_green, green_shamrock),
                    enabled = state.canSave,
                    paddingValues = PaddingValues(vertical = 14.dp)
                )
            }
    }
}
