package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark

import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class BookmarkState(
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : BaseState