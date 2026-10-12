package com.zaroslikov.crm.ui.client_edit

import androidx.annotation.StringRes
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.DomainCrmContact

private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

/** Ошибка имени клиента или `null`, если имя годится. */
@StringRes
internal fun validateClientName(name: String): Int? =
    if (name.isBlank()) R.string.crm_error_name_empty else null

/** Ошибка значения контакта или `null`; для email — базовая проверка формата. */
@StringRes
internal fun validateContactValue(type: CrmContactType, value: String): Int? = when {
    value.isBlank() -> R.string.crm_error_contact_empty
    type == CrmContactType.EMAIL && !EMAIL_REGEX.matches(value.trim()) -> R.string.crm_error_email_invalid
    else -> null
}

/** Черновики контактов → модели для `createClientWithContacts` / `updateClientWithContacts`. */
internal fun List<CrmContactDraft>.toDomainContacts(clientId: Long): List<DomainCrmContact> =
    map { DomainCrmContact(clientId = clientId, type = it.type, value = it.value.trim(), isPrimary = it.isPrimary) }

object CrmClientEditReducer {

    fun reducer(state: CrmClientEditState, intent: CrmClientEditIntent): CrmClientEditState =
        when (intent) {
            // Из карточки редактор открывается с itemIdPT = 0 — проект берём у клиента
            is CrmClientEditIntent.Loaded -> state.copy(
                isLoading = false,
                idPT = intent.client.idPT,
                original = intent.client,
                name = intent.client.name,
                isVip = intent.client.isVip,
                isFavorite = intent.client.isFavorite,
                note = intent.client.note,
                contacts = intent.contacts.mapIndexed { index, contact ->
                    CrmContactDraft(
                        key = index + 1L,
                        type = contact.type,
                        value = contact.value,
                        isPrimary = contact.isPrimary
                    )
                }
            )

            is CrmClientEditIntent.NameChange -> state.copy(name = intent.name, nameError = null)
            is CrmClientEditIntent.VipChange -> state.copy(isVip = intent.isVip)
            is CrmClientEditIntent.FavoriteChange -> state.copy(isFavorite = intent.isFavorite)
            is CrmClientEditIntent.NoteChange -> state.copy(note = intent.note)

            CrmClientEditIntent.AddContact -> {
                val key = (state.contacts.maxOfOrNull { it.key } ?: 0L) + 1L
                // Первый контакт сразу основной
                state.copy(contacts = state.contacts + CrmContactDraft(key = key, isPrimary = state.contacts.isEmpty()))
            }

            // Удалили основной — основным становится первый оставшийся
            is CrmClientEditIntent.RemoveContact -> {
                val removed = state.contacts.firstOrNull { it.key == intent.key }
                val contacts = state.contacts.filterNot { it.key == intent.key }
                state.copy(
                    contacts = if (removed?.isPrimary != true || contacts.isEmpty()) contacts
                    else listOf(contacts.first().copy(isPrimary = true)) + contacts.drop(1)
                )
            }

            is CrmClientEditIntent.ContactTypeChange -> state.updateContact(intent.key) {
                it.copy(type = intent.type, error = null)
            }

            is CrmClientEditIntent.ContactValueChange -> state.updateContact(intent.key) {
                it.copy(value = intent.value, error = null)
            }

            // Основной контакт может быть только один
            is CrmClientEditIntent.ContactPrimaryChange -> state.copy(
                contacts = state.contacts.map {
                    when {
                        it.key == intent.key -> it.copy(isPrimary = intent.isPrimary)
                        intent.isPrimary -> it.copy(isPrimary = false)
                        else -> it
                    }
                }
            )

            CrmClientEditIntent.Save -> state.copy(
                nameError = validateClientName(state.name),
                contacts = state.contacts.map { it.copy(error = validateContactValue(it.type, it.value)) }
            )

            is CrmClientEditIntent.NameExists -> state.copy(
                nameError = if (intent.isArchived) R.string.crm_error_name_exists_archived
                else R.string.crm_error_name_exists
            )
            is CrmClientEditIntent.SavingChange -> state.copy(isSaving = intent.isSaving)
        }

    private fun CrmClientEditState.updateContact(
        key: Long,
        update: (CrmContactDraft) -> CrmContactDraft
    ): CrmClientEditState = copy(contacts = contacts.map { if (it.key == key) update(it) else it })
}
