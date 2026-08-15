package com.zaroslikov.fermacompose2.base.viewModel

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.template.TemplateRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.base.reduce.SectionReducer
import com.zaroslikov.fermacompose2.base.state.BaseProductState
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.toQrPayload
import com.zaroslikov.fermacompose2.utils.QrCodeDecoder
import com.zaroslikov.fermacompose2.utils.QrCodeEncoder
import com.zaroslikov.fermacompose2.utils.QrGenerator
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.text.contains
import kotlin.text.isNotBlank

abstract class EntryNewViewModel3<STATE : SectionState, INTENT : BaseIntent, REDUCER : SectionReducer<STATE, INTENT>>(
    initialState: STATE, private val reducer: REDUCER,
    private val appSettingsRepository: AppSettingsRepository,
    private val qrGenerator: QrGenerator,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val qrNavigationManager: QrNavigationManager,
    private val templateRepository: TemplateRepository,
) : BaseViewModel<STATE, INTENT>(initialState) {

    protected abstract fun loadData()
    protected abstract fun insert()
    protected abstract fun update()
    protected abstract fun delete()

    protected abstract fun insertTemplate()
    protected abstract fun updateTemplate()
    protected abstract fun deleteTemplate()
    abstract fun onIntent(intent: INTENT)
    abstract fun onQrCodeIntent(intent: QrCodeIntent)

    fun onTemplateIntent(intent: TemplateIntent) {
        sendTemplateIntent(intent)
        when (intent) {
            is TemplateIntent.InsertTemplate -> insertTemplate()
            is TemplateIntent.UpdateTemplate -> updateTemplate()
            is TemplateIntent.DeleteTemplate -> deleteTemplate()

            is TemplateIntent.SetPinOfTemplateClick -> setPin(intent.value)

            is TemplateIntent.LoadDataForTemplateBottomSheetClick ->
                loadDataForTemplateBottomSheet(intent.value)

            is TemplateIntent.OpenPatternsBottomSheetClick ->
                loadDataForTemplatesBottomSheet(intent.value)

            is TemplateIntent.OpenTemplateEditor ->
                loadDataForEntryOrEdit(intent.isOpen, intent.id, isTemplate = intent.isTemplate)

            else -> Unit
        }
    }

    protected fun sendIntent(intent: INTENT) {
        updateState { reducer.reducer(it, intent) }
    }

    protected fun sendQrCodeIntent(intent: QrCodeIntent) {
        updateState { reducer.qrReducer(it, intent) }
    }

    protected fun sendTemplateIntent(intent: TemplateIntent) {
        updateState { reducer.templateReducer(it, intent) }
    }


    abstract fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        id: Long?,
        isSaveStateForBottomSheet: Boolean = false,
        isTemplate: Boolean = false
    )

    protected abstract suspend fun loadDataForPickList(id: Long? = null): BaseProductState


    protected abstract fun recover(domainTemplateTable: DomainTemplateTable)

    protected abstract fun loadDataForTemplatesBottomSheet(isOpen: Boolean)


    protected abstract fun loadDataForTemplateBottomSheet(id: Long, qrPayload: QrPayload? = null)
    protected fun loadTemplate(templateType: TemplateType) {
        val payload = qrNavigationManager.peek() ?: return

        Log.i("payload", "loadTemplate:$payload state : ${state.value.currentProduct} ")
        if (payload.templateType != templateType) return

        when {
            payload.isMultiProjectTemplate || payload.templateType == TemplateType.EXPENSES
                -> payload.backupData?.let { recover(payload.backupData) }

            else -> loadDataForTemplateBottomSheet(payload.itemId, payload)
        }
        qrNavigationManager.clear()
    }

    protected fun setPin(pair: Pair<Boolean, Long>) {
        viewModelScope.launch {
            templateRepository.setPinById(pin = pair.first, id = pair.second)
        }
    }

    protected fun qrScanner(uri: String, templateType: TemplateType, itemIdPT: Long) {
        viewModelScope.launch {
            val payload = QrCodeDecoder.decodeForUri(uri.toUri())
                ?: return@launch sendQrCodeIntent(
                    QrCodeIntent.OpenWarningQrCodeBottomSheetClick(
                        true,
                        QrCodeWarningType.GLOBAL
                    )
                )

            if (payload.isMultiProjectTemplate) {
                qrNavigationManager.put(payload)
                navigateTo(UiEvent.NavigateBack)
                return@launch
            }

            val projectExists = projectRepository
                .getIsProject(payload.idPT)
                .first()

            if (!projectExists) {
                sendQrCodeIntent(
                    QrCodeIntent.OpenWarningQrCodeBottomSheetClick(true, QrCodeWarningType.GLOBAL)
                )
                return@launch
            }

            if (payload.templateType == templateType && payload.idPT == itemIdPT)
                loadDataForTemplateBottomSheet(payload.itemId, payload)
            else {
                qrNavigationManager.put(payload)
                navigateTo(UiEvent.Navigate(payload.idPT))
            }
            sendQrCodeIntent(QrCodeIntent.OpenScannerQrCodeBottomSheetClick(false))
        }
    }

    protected fun generateQrCode(id: Long) {
        viewModelScope.launch {
            val template =
                templateRepository.getTemplateItem(id).first()
                    ?: return@launch

            val deviceId = appSettingsRepository.getAppSettings().first().deviceId

            val qrContent = QrCodeEncoder.encode(template.toQrPayload(deviceId))

            val bitmap = qrGenerator.generate(qrContent)
            val bitmapWhichLogo = qrGenerator.generateWhichLogo(qrContent)

            sendQrCodeIntent(
                QrCodeIntent.OpenQrCodeBottomSheetClick(
                    true,
                    QrCodeData(
                        template,
                        bitmap,
                        bitmapWhichLogo
                    )
                )
            )
        }
    }

    protected fun DomainAddTemplateDto.toTemplateItemUi(): TemplateItem {
        val description = listOfNotNull(
            title?.takeIf { it.isNotBlank() },
            count?.formatNumber(),
            countSuffix?.let { resourceProvider.getString(it.toResId()) },
            category?.takeIf { !it.contains(resourceProvider.getString(R.string.support_text_no_category)) && it.isNotBlank() },
            nameAnimal?.takeIf { it.isNotBlank() },
            note?.takeIf { it.isNotBlank() }
        ).joinToString(" · ")
        return TemplateItem(
            id = id,
            name = nameTemplate,
            description = description,
            isPinned = pin,
            isMultiProject = isMultiProject
        )
    }
}