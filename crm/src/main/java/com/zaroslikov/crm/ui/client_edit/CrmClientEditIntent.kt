package com.zaroslikov.crm.ui.client_edit

import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact

sealed interface CrmClientEditIntent {
    /** Загружен существующий клиент с контактами. */
    data class Loaded(val client: DomainCrmClient, val contacts: List<DomainCrmContact>) : CrmClientEditIntent

    data class NameChange(val name: String) : CrmClientEditIntent
    data class VipChange(val isVip: Boolean) : CrmClientEditIntent
    data class FavoriteChange(val isFavorite: Boolean) : CrmClientEditIntent
    data class NoteChange(val note: String) : CrmClientEditIntent

    data object AddContact : CrmClientEditIntent
    data class RemoveContact(val key: Long) : CrmClientEditIntent
    data class ContactTypeChange(val key: Long, val type: CrmContactType) : CrmClientEditIntent
    data class ContactValueChange(val key: Long, val value: String) : CrmClientEditIntent
    data class ContactPrimaryChange(val key: Long, val isPrimary: Boolean) : CrmClientEditIntent

    /** Проверка полей; само сохранение — побочный эффект ViewModel. */
    data object Save : CrmClientEditIntent

    /** Клиент с таким ключом продаж уже есть в проекте; [isArchived] — он в архиве. */
    data class NameExists(val isArchived: Boolean) : CrmClientEditIntent

    data class SavingChange(val isSaving: Boolean) : CrmClientEditIntent
}
