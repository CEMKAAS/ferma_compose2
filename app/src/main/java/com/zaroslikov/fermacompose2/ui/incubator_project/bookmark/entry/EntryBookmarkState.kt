package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class EntryBookmarkState(
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val currentProduct: EntryBookmark = EntryBookmark()
) : EntryNewState()


data class EntryBookmark(
    val title: String = "",
    val type: String = "",
    val countEgg: String = "",
    val date: String = dateToday(),
    val time: String = "",
    val price: String = "",
    val autoPrice: Boolean = false,
    val note: String = "",
    val error: ErrorBookmark = ErrorBookmark()
) : BaseProduct() {
    override val hasAnyError: Boolean = false
}

data class ErrorBookmark(
    val isErrorTitle: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorTitle || isErrorCount
}
