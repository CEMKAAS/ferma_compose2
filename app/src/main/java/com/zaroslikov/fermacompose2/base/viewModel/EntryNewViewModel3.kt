package com.zaroslikov.fermacompose2.base.viewModel

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.template.AddTemplateRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
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

abstract class EntryNewViewModel3<STATE : SectionState, INTENT : BaseIntent, REDUCER : BaseReducer<STATE, INTENT>>(
    initialState: STATE, private val reducer: REDUCER,
    private val qrGenerator: QrGenerator,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val qrNavigationManager: QrNavigationManager,
    private val addTemplateRepository: AddTemplateRepository,
) : BaseViewModel<STATE, INTENT>(initialState) {

    protected abstract fun loadData()
    protected abstract fun insert()
    protected abstract fun update()
    protected abstract fun delete()

    protected abstract fun insertTemplate()
    protected abstract fun updateTemplate()
    protected abstract fun deleteTemplate()
    abstract fun onIntent(intent: INTENT)

    protected fun sendIntent(intent: INTENT) {
        _state.value = reducer.reducer(_state.value, intent)
    }

    protected abstract fun createOpenQrIntent(
        data: QrCodeData
    ): INTENT


    protected abstract fun recover(domainTemplateTable: DomainTemplateTable)

    protected abstract fun loadDataForTemplatesBottomSheet(isOpen: Boolean)

    protected fun generateQrCode(id: Long) {
        viewModelScope.launch {
            val template =
                addTemplateRepository.getAddTemplateItem(id).first()
                    ?: return@launch

            val qrContent = QrCodeEncoder.encode(template.toQrPayload())

            val bitmap = qrGenerator.generate(qrContent)
            val bitmapWhichLogo = qrGenerator.generateWhichLogo(qrContent)

            sendIntent(
                createOpenQrIntent(
                    QrCodeData(
                        template,
                        bitmap,
                        bitmapWhichLogo
                    )
                )
            )
        }
    }

    protected fun setPin(pair: Pair<Boolean, Long>) {
        viewModelScope.launch {
            addTemplateRepository.setPinById(pin = pair.first, id = pair.second)
        }
    }

    protected abstract fun createOpenQrWarningIntent(
        warning: QrCodeWarningType
    ): INTENT

    protected fun qrScanner(uri: String) {
        viewModelScope.launch {
            val payload = QrCodeDecoder.decodeForUri(uri.toUri())
                ?: return@launch sendIntent(createOpenQrWarningIntent(QrCodeWarningType.GLOBAL))

            if (payload.isMultiProjectTemplate) {
                qrNavigationManager.put(payload)
                navigateTo(UiEvent.NavigateBack)
                return@launch
            }

            val projectExists = projectRepository
                .getIsProject(payload.idPT)
                .first()

            if (!projectExists) {
                sendIntent(createOpenQrWarningIntent(QrCodeWarningType.GLOBAL))
                return@launch
            }
            onQrPayloadReceived(payload)
        }
    }

    protected abstract suspend fun onQrPayloadReceived(
        payload: QrPayload
    )

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