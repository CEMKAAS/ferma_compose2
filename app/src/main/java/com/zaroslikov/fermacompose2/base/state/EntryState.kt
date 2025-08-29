package com.zaroslikov.fermacompose2.base.state


abstract class EntryState : BaseState {
    abstract val error: BaseError
}

interface BaseError {
    val hasAnyError: Boolean
}