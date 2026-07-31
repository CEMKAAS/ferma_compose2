@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.baseComposable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TemplateFab
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeCreateBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrScannerScreen
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.TemplatesBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.WarningDeleteTemplateBottomSheet

@Composable
fun BaseSectionScreen(
    state: SectionState,
    onGroupModeClick: (Boolean) -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddProductClick: (Boolean) -> Unit,
    onQrCodeIntent: (QrCodeIntent) -> Unit,
    onTemplateIntent: (TemplateIntent) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.searchState.searchQuery,
                isGroup = state.mainList.isGroupMode,
                onValueChange = onSearchChanged,
                onClick = onGroupModeClick,
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchive)
                GroupButtonFabs(
                    colors = state.ui.colors,
                    onTemplateClick = {
                        onTemplateIntent(
                            TemplateIntent.OpenPatternsBottomSheetClick(
                                true
                            )
                        )
                    },
                    onClick = { onAddProductClick(true) }
                )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else content(innerPadding)

        if (state.bottomSheetState.isOpenCreateQrCode)
            QrCodeCreateBottomSheet(
                colors = state.ui.colors,
                qrCodeData = state.qrCodeState,
            ) { onQrCodeIntent(QrCodeIntent.OpenQrCodeBottomSheetClick(false)) } // TODO можно вынести

        if (state.bottomSheetState.isOpenWarningQrCode)
            QrCodeWarningBottomSheet(
                backupData = state.qrCodeWarning.templateBackup,
                qrCodeWarningType = state.qrCodeWarning.warningType,
                onDismissRequest = {
                    onQrCodeIntent(QrCodeIntent.OpenWarningQrCodeBottomSheetClick(false))
                },
                onScannerClick = {
                    onQrCodeIntent(QrCodeIntent.OpenScannerQrCodeBottomSheetClick(true))
                },
                onRecoverClick = {
                    onQrCodeIntent(QrCodeIntent.RecoverClick(it))
                }
            )
        if (state.bottomSheetState.isOpenScannerQrCode)
            QrScannerScreen(
                onQrDetected = { onQrCodeIntent(QrCodeIntent.QrDetected(it)) },
                onDismissRequest = {
                    onQrCodeIntent(QrCodeIntent.OpenScannerQrCodeBottomSheetClick(false))
                }
            )

        if (state.bottomSheetState.isOpenTemplateDelete)
            WarningDeleteTemplateBottomSheet(
                iconRes = state.ui.iconRes,
                onDismissRequest = {
                    onTemplateIntent(TemplateIntent.OpenTemplateDeleteBottomSheet(null))
                },
                onDeleteClick = { onTemplateIntent(TemplateIntent.DeleteTemplate) },
                templateItem = state.templatesState.templateToDelete,
                iconColor = state.ui.colors.first(),
                iconBorderColor = ghostly_white,
            )

        if (state.bottomSheetState.isOpenTemplates)
            TemplatesBottomSheet(
                list = state.templatesState.templatesList,
                iconRes = state.ui.iconRes,
                colors = state.ui.colors,
                iconColor = state.ui.colors.first(),
                iconBorderColor = ghostly_white,
                onSetPinnedClick = {
                    onTemplateIntent(TemplateIntent.SetPinOfTemplateClick(it))
                },
                onDismissRequest = {
                    onTemplateIntent(TemplateIntent.OpenPatternsBottomSheetClick(value = false))
                },
                onCreatePatternClick = {
                    onTemplateIntent(
                        TemplateIntent.OpenTemplateEditor(isOpen = true, isTemplate = true)
                    )
                },
                onChoicePatternClick = {
                    onTemplateIntent(TemplateIntent.LoadDataForTemplateBottomSheetClick(it))
                },
                onEditTemplateClick = {
                    onTemplateIntent(
                        TemplateIntent.OpenTemplateEditor(
                            isOpen = true,
                            id = it,
                            isTemplate = true
                        )
                    )
                },
                onCreateQrCodeClick = { onQrCodeIntent(QrCodeIntent.CreateQrCodeClick(it)) },
                onDeleteTemplateClick = {
                    onTemplateIntent(TemplateIntent.OpenTemplateDeleteBottomSheet(it))
                }
            )
    }
}

@Composable
private fun GroupButtonFabs(
    colors: List<Color>,
    onTemplateClick: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TemplateFab(color = colors.first()) { onTemplateClick() }
        NeonGlowFab(
            colors = colors,
            onClick = onClick,
            onLongClick = onClick
        )
    }
}