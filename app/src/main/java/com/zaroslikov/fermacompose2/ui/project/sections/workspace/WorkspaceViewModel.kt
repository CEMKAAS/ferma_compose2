package com.zaroslikov.fermacompose2.ui.project.sections.workspace

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SectionWorkspaceViewModel @Inject constructor(
    private val qrNavigationManager: QrNavigationManager
) : ViewModel() {

    var initialPage by mutableIntStateOf(2)
        private set

    init {
        checkQrPayload()
    }

    private fun checkQrPayload() {
        viewModelScope.launch {
            val payload = qrNavigationManager.peek() ?: return@launch

            Log.i("payload", "checkQrPayload: $payload")
            initialPage = when (payload.templateType) {
                TemplateType.WRITE_OFF -> 0
                TemplateType.SALE -> 1
                TemplateType.ADD -> 2
                TemplateType.EXPENSES -> 3
            }
        }
    }
}