package com.zaroslikov.fermacompose2.base.intent

import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent

interface BaseIntent

sealed interface BaseIntent2 : BaseIntent {
    data object Insert
    data object Update
    data object Delete
}