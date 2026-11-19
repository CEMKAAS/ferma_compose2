package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TodoAssigneeProfile(
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val birthday: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val description: String = ""
) {
    val displayName: String
        get() = listOf(firstName, lastName, middleName)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .joinToString(" ")
}

internal fun TodoAssigneeProfile.trimmed(): TodoAssigneeProfile = copy(
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    middleName = middleName.trim(),
    birthday = parseTodoDate(birthday.trim())?.takeIf { it.year in 1..9999 }?.toString().orEmpty(),
    phone = phone.trim(),
    email = email.trim(),
    address = address.trim(),
    description = description.trim()
)
