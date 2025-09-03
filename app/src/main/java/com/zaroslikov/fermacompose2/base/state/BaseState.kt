package com.zaroslikov.fermacompose2.base.state

import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

interface BaseState{
     val isLoading: Boolean
     val navigate: UiEvent?
}