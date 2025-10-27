package com.zaroslikov.fermacompose2.base.state


abstract class EntryNewState : BaseState {
    abstract val isEntry: Boolean
    abstract val currentProduct: BaseProduct
}


abstract class BaseProduct {
    abstract val hasAnyError: Boolean
}

