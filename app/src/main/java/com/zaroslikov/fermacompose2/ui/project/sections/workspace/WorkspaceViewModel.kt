package com.zaroslikov.fermacompose2.ui.project.sections.workspace

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page
import com.zaroslikov.fermacompose2.utils.IncubatorChicksManager
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SectionWorkspaceViewModel @Inject constructor(
    private val qrNavigationManager: QrNavigationManager,
    private val incubatorChicksManager: IncubatorChicksManager,
) : ViewModel() {

    /**
     * Разовый запрос на переключение вкладки (QR-шаблон, птенцы из «Инкубатора»).
     * После выполнения сбрасывается в null, иначе при возврате с вложенного экрана
     * (например, с карточки животного) пейджер снова уезжал бы на эту вкладку.
     */
    var pageRequest by mutableStateOf<Int?>(null)
        private set

    fun onPageRequestHandled() {
        pageRequest = null
    }

    init {
        checkQrPayload()
    }

    private fun checkQrPayload() {
        viewModelScope.launch {
            // Только что записали птенцов из «Инкубатора» — открываем проект там, где их
            // видно: на странице «Животные».
            if (incubatorChicksManager.consumeAnimalsPage()) {
                pageRequest = Page.ANIMAL.ordinal
                return@launch
            }

            val payload = qrNavigationManager.peek() ?: return@launch

            Log.i("payload", "checkQrPayload: $payload")
            pageRequest = when (payload.templateType) {
                TemplateType.WRITE_OFF -> 0
                TemplateType.SALE -> 1
                TemplateType.ADD -> 2
                TemplateType.EXPENSES -> 3
            }
        }
    }
}