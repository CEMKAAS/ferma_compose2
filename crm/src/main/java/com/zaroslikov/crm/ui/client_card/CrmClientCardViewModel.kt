package com.zaroslikov.crm.ui.client_card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmContactRepository
import com.zaroslikov.crm.domain.repository.CrmInteractionRepository
import com.zaroslikov.crm.domain.repository.CrmTaskRepository
import com.zaroslikov.crm.ui.base.CrmBaseViewModel
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.navigation.CrmClientCardDestination
import com.zaroslikov.crm.util.crmCurrency
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.crm.CrmSalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CrmClientCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clientRepository: CrmClientRepository,
    private val contactRepository: CrmContactRepository,
    private val interactionRepository: CrmInteractionRepository,
    private val taskRepository: CrmTaskRepository,
    private val salesRepository: CrmSalesRepository,
    private val settingsRepository: SettingsRepository
) : CrmBaseViewModel<CrmClientCardState, CrmClientCardIntent>(
    initialState = CrmClientCardState(),
    reducer = CrmClientCardReducer::reducer
) {

    private val clientId: Long = checkNotNull(savedStateHandle[CrmClientCardDestination.clientId])

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val first = try {
                clientRepository.getClient(clientId).first()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onLoadError()
                return@launch
            }
            if (first == null) {
                emitEvent(CrmUiEvent.NavigateBack)
                return@launch
            }
            // idPT и salesKey неизменны — запросы продаж строим один раз
            val idPT = first.idPT
            val buyer = first.salesKey
            val lastMonth = YearMonth.now()
            val dateBegin = lastMonth.minusMonths(CRM_CARD_MONTHS - 1L).atDay(1).toString()
            val dateEnd = lastMonth.atEndOfMonth().toString()

            val clientPart = combine(
                clientRepository.getClient(clientId),
                contactRepository.getContacts(clientId),
                interactionRepository.getForClient(clientId),
                taskRepository.getForClient(clientId)
            ) { client, contacts, interactions, tasks ->
                ClientPart(client, contacts, interactions, tasks)
            }
            val salesPart = combine(
                salesRepository.getBuyerStats(idPT).map { list -> list.firstOrNull { it.buyer == buyer } },
                salesRepository.getBuyerSales(idPT, buyer),
                salesRepository.getBuyerProducts(idPT, buyer),
                salesRepository.getMonthlyRevenueByBuyer(idPT, buyer, dateBegin, dateEnd),
                settingsRepository.crmCurrency(idPT)
            ) { stats, sales, products, monthly, currency ->
                CrmClientCardData(
                    client = first,
                    contacts = emptyList(),
                    interactions = emptyList(),
                    tasks = emptyList(),
                    stats = stats,
                    sales = sales,
                    products = products,
                    monthly = crmMonthSeries(monthly, lastMonth),
                    currency = currency
                )
            }

            var failed = false
            combine(clientPart, salesPart) { client, sales -> client to sales }
                // Клиента удалили навсегда — уходим с экрана и прекращаем сбор
                .takeWhile { (client, _) -> client.client != null }
                .catch {
                    failed = true
                    onLoadError()
                }
                .collect { (part, sales) ->
                    sendIntent(
                        CrmClientCardIntent.Loaded(
                            sales.copy(
                                client = checkNotNull(part.client),
                                contacts = part.contacts,
                                interactions = part.interactions,
                                tasks = part.tasks
                            )
                        )
                    )
                }
            // После ошибки остаёмся на экране с последними данными
            if (!failed) emitEvent(CrmUiEvent.NavigateBack)
        }
    }

    /** Загрузка упала: убираем индикатор и сообщаем об ошибке. */
    private fun onLoadError() {
        updateState { it.copy(isLoading = false) }
        emitEvent(CrmUiEvent.Message(R.string.crm_msg_load_error))
    }

    override fun onIntent(intent: CrmClientCardIntent) {
        // Reducer сбрасывает подтверждаемое взаимодействие — запоминаем его заранее
        val interactionToDelete = getState().interactionToDelete
        sendIntent(intent)
        when (intent) {
            CrmClientCardIntent.ToggleFavorite -> getState().data?.client?.let { client ->
                // Состояние уже переключено reducer'ом — сохраняем его
                launchSafe { clientRepository.updateClient(client) }
            }

            CrmClientCardIntent.ConfirmArchive -> launchSafe {
                clientRepository.archiveClient(clientId)
                emitEvent(CrmUiEvent.NavigateBack)
            }

            CrmClientCardIntent.SaveInteraction -> saveInteraction()
            CrmClientCardIntent.ConfirmDeleteInteraction -> interactionToDelete?.let(::deleteInteraction)
            CrmClientCardIntent.SaveTask -> saveTask()
            is CrmClientCardIntent.ToggleTaskDone -> launchSafe {
                taskRepository.setDone(intent.task.id, !intent.task.isDone)
            }

            else -> Unit
        }
    }

    private fun deleteInteraction(interaction: DomainCrmInteraction) {
        launchSafe {
            interactionRepository.deleteInteraction(interaction)
            emitEvent(CrmUiEvent.Message(R.string.crm_msg_interaction_deleted))
        }
    }

    private fun saveInteraction() {
        val draft = getState().interactionSheet ?: return
        if (!draft.isValid) return
        updateState { it.copy(interactionSheet = null) }
        launchSafe {
            interactionRepository.insertInteraction(
                DomainCrmInteraction(
                    clientId = clientId,
                    type = draft.type,
                    date = draft.date,
                    note = draft.note.trim()
                )
            )
            emitEvent(CrmUiEvent.Message(R.string.crm_msg_interaction_saved))
        }
    }

    private fun saveTask() {
        val draft = getState().taskSheet ?: return
        val idPT = getState().data?.client?.idPT ?: return
        if (!draft.isValid) return
        updateState { it.copy(taskSheet = null) }
        launchSafe {
            taskRepository.upsertTask(
                DomainCrmTask(
                    idPT = idPT,
                    clientId = clientId,
                    title = draft.title.trim(),
                    dueDate = draft.dueDate,
                    note = draft.note.trim()
                )
            )
            emitEvent(CrmUiEvent.Message(R.string.crm_msg_task_saved))
        }
    }

    private data class ClientPart(
        val client: DomainCrmClient?,
        val contacts: List<DomainCrmContact>,
        val interactions: List<DomainCrmInteraction>,
        val tasks: List<DomainCrmTask>
    )
}
