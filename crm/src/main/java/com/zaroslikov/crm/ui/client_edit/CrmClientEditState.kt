package com.zaroslikov.crm.ui.client_edit

import androidx.annotation.StringRes
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.DomainCrmClient

/** Черновик контакта в редакторе; [key] — локальный ключ строки, не id в БД. */
data class CrmContactDraft(
    val key: Long,
    val type: CrmContactType = CrmContactType.PHONE,
    val value: String = "",
    val isPrimary: Boolean = false,
    @StringRes val error: Int? = null
)

data class CrmClientEditState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    /** Для нового клиента — из аргумента навигации, для существующего — из загруженного клиента. */
    val idPT: Long = 0,
    val clientId: Long = NEW_CLIENT_ID,
    /** Клиент из БД; `null` для нового. */
    val original: DomainCrmClient? = null,
    val name: String = "",
    @StringRes val nameError: Int? = null,
    val isVip: Boolean = false,
    val isFavorite: Boolean = false,
    val note: String = "",
    val contacts: List<CrmContactDraft> = emptyList()
) {
    val isNew: Boolean get() = clientId == NEW_CLIENT_ID

    val hasErrors: Boolean get() = nameError != null || contacts.any { it.error != null }

    companion object {
        const val NEW_CLIENT_ID = -1L
    }
}
