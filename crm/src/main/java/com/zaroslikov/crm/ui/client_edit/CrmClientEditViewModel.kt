package com.zaroslikov.crm.ui.client_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.crmSalesKey
import com.zaroslikov.crm.domain.model.crmToday
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmContactRepository
import com.zaroslikov.crm.ui.base.CrmBaseViewModel
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.navigation.CrmClientEditDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrmClientEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clientRepository: CrmClientRepository,
    private val contactRepository: CrmContactRepository
) : CrmBaseViewModel<CrmClientEditState, CrmClientEditIntent>(
    initialState = CrmClientEditState(
        idPT = checkNotNull(savedStateHandle[CrmClientEditDestination.itemIdPT]),
        clientId = checkNotNull(savedStateHandle[CrmClientEditDestination.clientId])
    ),
    reducer = CrmClientEditReducer::reducer
) {

    init {
        if (getState().isNew)
            updateState { it.copy(isLoading = false) }
        else
            loadClient()
    }

    private fun loadClient() {
        viewModelScope.launch {
            val clientId = getState().clientId
            val client = clientRepository.getClient(clientId).first()
            if (client == null) {
                emitEvent(CrmUiEvent.NavigateBack)
                return@launch
            }
            val contacts = contactRepository.getContacts(clientId).first()
            sendIntent(CrmClientEditIntent.Loaded(client, contacts))
        }
    }

    override fun onIntent(intent: CrmClientEditIntent) {
        // Повторный тап «Сохранить» во время записи игнорируем
        if (intent == CrmClientEditIntent.Save && getState().isSaving) return
        sendIntent(intent)
        if (intent == CrmClientEditIntent.Save) save()
    }

    private fun save() {
        val state = getState()
        if (state.isLoading || state.hasErrors) return
        sendIntent(CrmClientEditIntent.SavingChange(true))
        viewModelScope.launch {
            try {
                // Ключ связи с продажами считаем от исходного ввода тем же правилом, что и SQL (TRIM только пробелов)
                val salesKey = crmSalesKey(state.name)
                val name = state.name.trim()
                val original = state.original
                if (original == null) {
                    val existing = clientRepository.findBySalesKey(state.idPT, salesKey)
                    if (existing != null) {
                        sendIntent(CrmClientEditIntent.NameExists(isArchived = existing.isArchived))
                        sendIntent(CrmClientEditIntent.SavingChange(false))
                        return@launch
                    }
                    clientRepository.createClientWithContacts(
                        DomainCrmClient(
                            idPT = state.idPT,
                            name = name,
                            salesKey = salesKey,
                            source = CrmClientSource.MANUAL,
                            isVip = state.isVip,
                            note = state.note.trim(),
                            isFavorite = state.isFavorite,
                            createdDate = crmToday()
                        ),
                        // clientId проставит репозиторий после вставки клиента
                        state.contacts.toDomainContacts(0)
                    )
                } else {
                    clientRepository.updateClientWithContacts(
                        original.copy(
                            name = name,
                            isVip = state.isVip,
                            note = state.note.trim(),
                            isFavorite = state.isFavorite
                        ),
                        state.contacts.toDomainContacts(original.id)
                    )
                }
                // isSaving остаётся true: экран закрывается, повторная запись не нужна
                emitEvent(CrmUiEvent.NavigateBack)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                sendIntent(CrmClientEditIntent.SavingChange(false))
                emitEvent(CrmUiEvent.Message(R.string.crm_msg_error))
            }
        }
    }
}
