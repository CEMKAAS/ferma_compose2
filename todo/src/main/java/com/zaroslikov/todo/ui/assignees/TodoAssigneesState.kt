package com.zaroslikov.todo.ui.assignees

import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile

data class TodoAssigneesState(
    val isLoading: Boolean = true,
    val assignees: List<DomainTodoAssignee> = emptyList(),
    val viewedName: String? = null,
    val form: TodoAssigneeForm? = null,
    val nameToDelete: String? = null,
    val isSaving: Boolean = false
) {
    val viewedAssignee: DomainTodoAssignee?
        get() = assignees.find { it.name == viewedName }
}

data class TodoAssigneeForm(
    val originalName: String? = null,
    val profile: TodoAssigneeProfile = TodoAssigneeProfile(),
    val error: Int? = null,
    val phoneError: Int? = null,
    val emailError: Int? = null,
    val initialProfile: TodoAssigneeProfile = profile
)

sealed interface TodoAssigneesIntent {
    data class Loaded(val assignees: List<DomainTodoAssignee>) : TodoAssigneesIntent
    data class OpenProfile(val name: String) : TodoAssigneesIntent
    data class OpenForm(val name: String? = null) : TodoAssigneesIntent
    data class ProfileChanged(val profile: TodoAssigneeProfile) : TodoAssigneesIntent
    data class Save(val fallbackName: String) : TodoAssigneesIntent
    data object Saved : TodoAssigneesIntent
    data class SaveFailed(val message: Int) : TodoAssigneesIntent
    data class RequestDelete(val name: String) : TodoAssigneesIntent
    data object Delete : TodoAssigneesIntent
    data object Dismiss : TodoAssigneesIntent
}

internal fun todoAssigneesReduce(
    state: TodoAssigneesState,
    intent: TodoAssigneesIntent
): TodoAssigneesState = when (intent) {
    is TodoAssigneesIntent.Loaded -> state.copy(isLoading = false, assignees = intent.assignees)
    is TodoAssigneesIntent.OpenProfile -> state.copy(viewedName = intent.name)
    is TodoAssigneesIntent.OpenForm -> {
        val form = if (intent.name == null) {
            TodoAssigneeForm()
        } else {
            state.assignees.find { it.name == intent.name }?.let { TodoAssigneeForm(it.name, it.profile) }
        }
        state.copy(form = form)
    }
    is TodoAssigneesIntent.ProfileChanged -> state.copy(
        form = state.form?.withProfile(intent.profile)
    )
    is TodoAssigneesIntent.Save -> {
        if (state.form == null) {
            state
        } else {
            val form = state.form
            val nameKey = form.profile.displayName.lowercase()
            val duplicate = nameKey.isNotEmpty() && state.assignees.any {
                it.name != form.originalName && it.name.lowercase() == nameKey
            }
            val invalidPhone = form.profile.phone != form.initialProfile.phone &&
                !isValidTodoAssigneePhone(form.profile.phone)
            val invalidEmail = form.profile.email != form.initialProfile.email &&
                !isValidTodoAssigneeEmail(form.profile.email)
            state.copy(
                form = form.copy(
                    error = R.string.todo_assignee_exists.takeIf { duplicate },
                    phoneError = R.string.todo_assignee_phone_invalid.takeIf { invalidPhone },
                    emailError = R.string.todo_assignee_email_invalid.takeIf { invalidEmail }
                ),
                isSaving = !duplicate && !invalidPhone && !invalidEmail
            )
        }
    }
    TodoAssigneesIntent.Saved -> state.copy(form = null, viewedName = null, isSaving = false)
    is TodoAssigneesIntent.SaveFailed -> state.copy(
        form = state.form?.copy(error = intent.message),
        isSaving = false
    )
    is TodoAssigneesIntent.RequestDelete -> state.copy(nameToDelete = intent.name)
    TodoAssigneesIntent.Delete -> state.copy(nameToDelete = null, isSaving = state.nameToDelete != null)
    TodoAssigneesIntent.Dismiss -> when {
        state.nameToDelete != null -> state.copy(nameToDelete = null)
        state.form != null -> state.copy(form = null)
        else -> state.copy(viewedName = null)
    }
}

private fun TodoAssigneeForm.withProfile(updated: TodoAssigneeProfile): TodoAssigneeForm {
    val phone = updateTodoAssigneePhone(profile.phone, updated.phone)
    val email = if (updated.email == profile.email) profile.email else sanitizeTodoAssigneeEmail(updated.email)
    return copy(
        profile = updated.copy(phone = phone, email = email),
        error = null,
        phoneError = phoneError.takeIf { phone == profile.phone },
        emailError = emailError.takeIf { email == profile.email }
    )
}
