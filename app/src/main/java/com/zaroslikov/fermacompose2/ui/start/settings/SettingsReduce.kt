package com.zaroslikov.fermacompose2.ui.start.settings

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class SettingsReduce : BaseReducer<SettingsState, SettingsIntent>() {
    override fun reducer(
        state: SettingsState,
        intent: SettingsIntent
    ): SettingsState {
        return when (intent) {
            is SettingsIntent.OpenExportBottomSheetClick -> state.updateOpenExportBottomSheet(intent.value)
            is SettingsIntent.OpenImportBottomSheetClick -> state.updateOpenImportBottomSheet(intent.value)
            is SettingsIntent.OpenDeleteBottomSheetClick -> state.updateOpenDeleteBottomSheet(intent.value)
            else -> state
        }
    }

    private fun SettingsState.updateOpenExportBottomSheet(isOpenExportBottomSheet: Boolean): SettingsState {
        return copy(
            isOpenExportBottomSheet = isOpenExportBottomSheet
        )
    }

    private fun SettingsState.updateOpenImportBottomSheet(isOpenImportBottomSheet: Boolean): SettingsState {
        return copy(
            isOpenImportBottomSheet = isOpenImportBottomSheet
        )
    }

    private fun SettingsState.updateOpenDeleteBottomSheet(isOpenDeleteBottomSheet: Boolean): SettingsState {
        return copy(
            isOpenDeleteBottomSheet = isOpenDeleteBottomSheet
        )
    }
}