package com.zaroslikov.crm.ui.client_edit

import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CrmClientEditReducerTest {

    private fun reduce(state: CrmClientEditState, vararg intents: CrmClientEditIntent) =
        intents.fold(state) { acc, intent -> CrmClientEditReducer.reducer(acc, intent) }

    private val newState = CrmClientEditState(isLoading = false, idPT = 1)

    @Test
    fun `пустое имя даёт ошибку при сохранении`() {
        val state = reduce(newState, CrmClientEditIntent.NameChange("   "), CrmClientEditIntent.Save)
        assertEquals(R.string.crm_error_name_empty, state.nameError)
        assertTrue(state.hasErrors)
    }

    @Test
    fun `заполненное имя без контактов проходит проверку`() {
        val state = reduce(newState, CrmClientEditIntent.NameChange("Иван"), CrmClientEditIntent.Save)
        assertNull(state.nameError)
        assertFalse(state.hasErrors)
    }

    @Test
    fun `ввод имени сбрасывает ошибку`() {
        val state = reduce(
            newState,
            CrmClientEditIntent.Save,
            CrmClientEditIntent.NameChange("И")
        )
        assertNull(state.nameError)
    }

    @Test
    fun `дубликат имени выставляет ошибку уже существует`() {
        val state = reduce(newState, CrmClientEditIntent.NameChange("Иван"), CrmClientEditIntent.NameExists(isArchived = false))
        assertEquals(R.string.crm_error_name_exists, state.nameError)
    }

    @Test
    fun `дубликат имени в архиве подсказывает восстановить клиента`() {
        val state = reduce(newState, CrmClientEditIntent.NameChange("Иван"), CrmClientEditIntent.NameExists(isArchived = true))
        assertEquals(R.string.crm_error_name_exists_archived, state.nameError)
        assertTrue(state.hasErrors)
    }

    @Test
    fun `удаление основного контакта делает основным первый оставшийся`() {
        val state = reduce(
            newState,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.RemoveContact(1)
        )
        assertEquals(listOf(2L, 3L), state.contacts.map { it.key })
        assertEquals(listOf(true, false), state.contacts.map { it.isPrimary })
    }

    @Test
    fun `удаление неосновного контакта не меняет основной`() {
        val state = reduce(
            newState,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.ContactPrimaryChange(3, true),
            CrmClientEditIntent.RemoveContact(1)
        )
        assertEquals(listOf(false, true), state.contacts.map { it.isPrimary })
    }

    @Test
    fun `удаление последнего контакта оставляет пустой список`() {
        val state = reduce(newState, CrmClientEditIntent.AddContact, CrmClientEditIntent.RemoveContact(1))
        assertTrue(state.contacts.isEmpty())
    }

    @Test
    fun `пустой контакт и неверный email дают ошибки`() {
        val state = reduce(
            newState,
            CrmClientEditIntent.NameChange("Иван"),
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.ContactTypeChange(2, CrmContactType.EMAIL),
            CrmClientEditIntent.ContactValueChange(2, "ivan@mail"),
            CrmClientEditIntent.Save
        )
        assertEquals(R.string.crm_error_contact_empty, state.contacts[0].error)
        assertEquals(R.string.crm_error_email_invalid, state.contacts[1].error)
        assertTrue(state.hasErrors)
    }

    @Test
    fun `корректный email и телефон проходят проверку`() {
        assertNull(validateContactValue(CrmContactType.EMAIL, " ivan@mail.ru "))
        assertNull(validateContactValue(CrmContactType.PHONE, "+7 900 000-00-00"))
        assertEquals(R.string.crm_error_email_invalid, validateContactValue(CrmContactType.EMAIL, "ivan mail.ru"))
    }

    @Test
    fun `первый контакт основной, основной может быть только один`() {
        val added = reduce(newState, CrmClientEditIntent.AddContact, CrmClientEditIntent.AddContact)
        assertTrue(added.contacts[0].isPrimary)
        assertFalse(added.contacts[1].isPrimary)

        val switched = reduce(added, CrmClientEditIntent.ContactPrimaryChange(2, true))
        assertFalse(switched.contacts[0].isPrimary)
        assertTrue(switched.contacts[1].isPrimary)
    }

    @Test
    fun `удаление контакта и уникальные ключи`() {
        val state = reduce(
            newState,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.AddContact,
            CrmClientEditIntent.RemoveContact(1),
            CrmClientEditIntent.AddContact
        )
        assertEquals(listOf(2L, 3L), state.contacts.map { it.key })
    }

    @Test
    fun `загрузка заполняет форму клиента`() {
        val client = DomainCrmClient(
            id = 5, idPT = 1, name = "Пётр", salesKey = "Пётр", source = CrmClientSource.SALES,
            isVip = true, note = "Берёт молоко", isFavorite = true, createdDate = "2026-01-01"
        )
        val contacts = listOf(DomainCrmContact(id = 9, clientId = 5, type = CrmContactType.PHONE, value = "123", isPrimary = true))
        val state = reduce(
            CrmClientEditState(idPT = 0, clientId = 5),
            CrmClientEditIntent.Loaded(client, contacts)
        )
        assertFalse(state.isLoading)
        assertEquals(1L, state.idPT)
        assertFalse(state.isNew)
        assertEquals("Пётр", state.name)
        assertTrue(state.isVip)
        assertTrue(state.isFavorite)
        assertEquals("Берёт молоко", state.note)
        assertEquals(CrmContactType.PHONE, state.contacts.single().type)
        assertTrue(state.contacts.single().isPrimary)
    }

    @Test
    fun `черновики контактов обрезают пробелы и получают id клиента`() {
        val drafts = listOf(CrmContactDraft(key = 1, type = CrmContactType.VK, value = "  id1 ", isPrimary = true))
        val contact = drafts.toDomainContacts(clientId = 7).single()
        assertEquals(7L, contact.clientId)
        assertEquals("id1", contact.value)
        assertEquals(CrmContactType.VK, contact.type)
        assertTrue(contact.isPrimary)
    }
}
