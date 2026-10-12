package com.zaroslikov.crm.ui.client_card

import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerProduct
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.enums.Suffix

/** Сколько покупок показывать до «Показать все». */
const val CRM_CARD_SALES_PREVIEW = 10

/** Точка графика выручки: месяц и сумма (с нулём, если продаж не было). */
data class CrmMonthPoint(
    val year: Int,
    val month: Int,
    val total: Double
)

/** Всё, что экран получает из БД одним снимком. */
data class CrmClientCardData(
    val client: DomainCrmClient,
    val contacts: List<DomainCrmContact>,
    val interactions: List<DomainCrmInteraction>,
    val tasks: List<DomainCrmTask>,
    val stats: DomainCrmBuyerStats?,
    val sales: List<DomainSaleTable>,
    val products: List<DomainCrmBuyerProduct>,
    val monthly: List<CrmMonthPoint>,
    val currency: Suffix
)

/** Форма нового взаимодействия в нижнем листе. */
data class CrmInteractionDraft(
    val type: CrmInteractionType = CrmInteractionType.CALL,
    val date: String,
    val note: String = "",
    val noteError: Boolean = false,
    val dateError: Boolean = false
) {
    val isValid: Boolean get() = !noteError && !dateError
}

/** Форма новой задачи клиента в нижнем листе. */
data class CrmTaskDraft(
    val title: String = "",
    val dueDate: String,
    val note: String = "",
    val titleError: Boolean = false,
    val dateError: Boolean = false
) {
    val isValid: Boolean get() = !titleError && !dateError
}

data class CrmClientCardState(
    val isLoading: Boolean = true,
    val data: CrmClientCardData? = null,
    val showAllSales: Boolean = false,
    val showArchiveDialog: Boolean = false,
    val interactionToDelete: DomainCrmInteraction? = null,
    /** `null` — лист закрыт. */
    val interactionSheet: CrmInteractionDraft? = null,
    val taskSheet: CrmTaskDraft? = null
)
