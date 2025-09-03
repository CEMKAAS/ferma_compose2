package com.zaroslikov.fermacompose2.base.state


abstract class EntryState : BaseState {
    abstract val isEntry: Boolean
    abstract val error: BaseError
    abstract val hasAnyError: Boolean
}

interface BaseError