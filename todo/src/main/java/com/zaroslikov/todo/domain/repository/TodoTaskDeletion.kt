package com.zaroslikov.todo.domain.repository

/** Восстанавливает удалённую задачу с данными не более одного раза. */
fun interface TodoTaskDeletion {
    suspend fun undo()
}
