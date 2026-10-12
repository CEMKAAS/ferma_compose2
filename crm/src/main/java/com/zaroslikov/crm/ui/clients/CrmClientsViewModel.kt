package com.zaroslikov.crm.ui.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmContactRepository
import com.zaroslikov.crm.domain.usecase.CrmSyncClientsUseCase
import com.zaroslikov.crm.ui.base.CrmBaseViewModel
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.navigation.CrmClientsDestination
import com.zaroslikov.crm.util.crmCurrency
import com.zaroslikov.crm.util.today
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.crm.CrmSalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrmClientsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clientRepository: CrmClientRepository,
    contactRepository: CrmContactRepository,
    salesRepository: CrmSalesRepository,
    settingsRepository: SettingsRepository,
    syncClients: CrmSyncClientsUseCase
) : CrmBaseViewModel<CrmClientsState, CrmClientsIntent>(CrmClientsState(), ::crmClientsReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[CrmClientsDestination.itemIdPT])

    init {
        viewModelScope.launch {
            // Сначала заводим клиентов из продаж, чтобы список сразу был полным
            try {
                syncClients(idPT)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Без синхронизации показываем уже заведённых клиентов
            }
            combine(
                clientRepository.getClients(idPT),
                clientRepository.getArchivedClients(idPT),
                salesRepository.getBuyerStats(idPT),
                contactRepository.getContactsForProject(idPT),
                settingsRepository.crmCurrency(idPT)
            ) { clients, archived, stats, contacts, suffix ->
                val now = today()
                CrmClientsIntent.DataLoaded(
                    rows = crmClientRows(clients, stats, now),
                    archivedRows = crmClientRows(archived, stats, now),
                    contacts = contacts.groupBy({ it.clientId }, { it.value }),
                    currency = suffix
                )
            }
                .catch {
                    // Без индикатора навсегда: показываем то, что есть, и сообщение об ошибке
                    updateState { it.copy(isLoading = false) }
                    emitEvent(CrmUiEvent.Message(R.string.crm_msg_load_error))
                }
                .collect { sendIntent(it) }
        }
    }

    override fun onIntent(intent: CrmClientsIntent) {
        // Клиента для удаления берём до редьюсера — он его сбрасывает
        val clientToDelete = getState().clientToDelete
        sendIntent(intent)
        when (intent) {
            is CrmClientsIntent.ToggleFavorite -> launchSafe {
                clientRepository.updateClient(intent.client.copy(isFavorite = !intent.client.isFavorite))
            }

            is CrmClientsIntent.Restore -> launchSafe {
                clientRepository.restoreClient(intent.client.id)
                emitEvent(CrmUiEvent.Message(R.string.crm_msg_client_restored))
            }

            // Клиент из продаж вернулся бы при следующей синхронизации — навсегда удаляем только ручных
            CrmClientsIntent.ConfirmDelete ->
                if (clientToDelete != null && clientToDelete.source == CrmClientSource.MANUAL)
                    launchSafe {
                        clientRepository.deleteClientForever(clientToDelete.id)
                        emitEvent(CrmUiEvent.Message(R.string.crm_msg_client_deleted))
                    }

            else -> Unit
        }
    }
}
