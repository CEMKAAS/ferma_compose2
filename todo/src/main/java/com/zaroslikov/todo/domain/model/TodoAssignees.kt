package com.zaroslikov.todo.domain.model

/** Имена исполнителей без пустых значений и повторов; порядок первого добавления сохраняется. */
fun normalizeTodoAssignees(names: List<String>): List<String> = names
    .map(String::trim)
    .filter(String::isNotEmpty)
    .distinctBy { it.lowercase() }
