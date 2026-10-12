package com.zaroslikov.crm.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmInteractionRepository
import com.zaroslikov.crm.domain.repository.CrmTaskRepository
import com.zaroslikov.crm.domain.usecase.CrmSyncClientsUseCase
import com.zaroslikov.crm.ui.base.CrmBaseViewModel
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.navigation.CrmDestination
import com.zaroslikov.crm.util.crmCurrency
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.crm.CrmSalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/** Ближайших задач на дашборде. */
private const val UPCOMING_TASKS = 3

/** Последних взаимодействий на дашборде. */
private const val RECENT_INTERACTIONS = 5

@HiltViewModel
class CrmDashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: CrmTaskRepository,
    clientRepository: CrmClientRepository,
    interactionRepository: CrmInteractionRepository,
    salesRepository: CrmSalesRepository,
    settingsRepository: SettingsRepository,
    syncClients: CrmSyncClientsUseCase
) : CrmBaseViewModel<CrmDashboardState, CrmDashboardIntent>(CrmDashboardState(), ::crmDashboardReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[CrmDestination.itemIdPT])

    /** Данные из CRM-базы и продаж, собранные первым `combine` (у него максимум пять потоков). */
    private class Sources(
        val stats: List<DomainCrmBuyerStats>,
        val clients: List<DomainCrmClient>,
        val monthRevenue: List<DomainCrmMonthRevenue>,
        val upcomingTasks: List<DomainCrmTask>,
        val interactions: List<DomainCrmInteraction>
    )

    init {
        viewModelScope.launch {
            // Сначала заводим клиентов из продаж, чтобы показатели считались по полному списку
            try {
                syncClients(idPT)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Без синхронизации показываем уже заведённых клиентов
            }

            val today = LocalDate.now()
            val months = crmLastMonths(today)
            val sources = combine(
                salesRepository.getBuyerStats(idPT),
                clientRepository.getClients(idPT),
                salesRepository.getMonthlyRevenue(
                    idPT,
                    dateBegin = months.first().atDay(1).toString(),
                    dateEnd = months.last().atEndOfMonth().toString()
                ),
                taskRepository.getUpcoming(idPT, UPCOMING_TASKS),
                interactionRepository.getRecentForProject(idPT, RECENT_INTERACTIONS),
                ::Sources
            )
            val currency = settingsRepository.crmCurrency(idPT)

            combine(sources, salesRepository.getSalesWithoutBuyerCount(idPT), currency) { s, withoutBuyer, suffix ->
                CrmDashboardIntent.DataLoaded(
                    data = crmDashboardData(
                        stats = s.stats,
                        clients = s.clients,
                        monthRevenue = s.monthRevenue,
                        months = months,
                        upcomingTasks = s.upcomingTasks,
                        recentInteractions = s.interactions,
                        salesWithoutBuyer = withoutBuyer,
                        currency = suffix,
                        today = today
                    ),
                    clients = s.clients,
                    today = today.toString()
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

    override fun onIntent(intent: CrmDashboardIntent) {
        sendIntent(intent)
        when (intent) {
            is CrmDashboardIntent.ToggleTask -> launchSafe {
                taskRepository.setDone(intent.task.id, !intent.task.isDone)
            }

            is CrmDashboardIntent.CreateTask -> launchSafe {
                taskRepository.upsertTask(
                    DomainCrmTask(
                        idPT = idPT,
                        clientId = intent.clientId,
                        title = intent.title.trim(),
                        dueDate = intent.dueDate,
                        note = intent.note.trim()
                    )
                )
                emitEvent(CrmUiEvent.Message(R.string.crm_msg_task_saved))
            }

            else -> Unit
        }
    }
}
