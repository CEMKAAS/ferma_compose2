package com.zaroslikov.fermacompose2.ui.start.settings

import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class SettingsIntent : BaseIntent {
    data object DeleteDatabasePress : SettingsIntent()
    data class ImportDatabasePress(val value: String?) : SettingsIntent()
    data object ExportDatabasePress : SettingsIntent()
    data class OpenExportBottomSheetClick(val value: Boolean) : SettingsIntent()
    data class OpenImportBottomSheetClick(val value: Boolean) : SettingsIntent()
    data class OpenDeleteBottomSheetClick(val value: Boolean) : SettingsIntent()
}