package com.zaroslikov.fermacompose2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryAppViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    var isFirstLaunch by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val appSettings = appSettingsRepository.getAppSettings().first()
            isFirstLaunch = appSettings.isFirstLaunch
        }
    }
}