package com.zaroslikov.todo.domain.model

/** Задачу нельзя завершить, пока не выполнены все задачи, которые её блокируют. */
class TodoTaskBlockedException : IllegalStateException("Сначала завершите блокирующие задачи")
