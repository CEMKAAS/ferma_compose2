package com.zaroslikov.crm.ui.navigation

// Все аргументы — path, NavType.LongType, читаются во ViewModel из SavedStateHandle

object CrmDestination {
    const val route = "crm"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "crm/{itemIdPT}"
}

object CrmClientsDestination {
    const val route = "crm_clients"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "crm_clients/{itemIdPT}"
}

object CrmClientCardDestination {
    const val route = "crm_client"
    const val clientId = "clientId"
    const val routeWithArgs = "crm_client/{clientId}"
}

/** `clientId = -1` — создание нового клиента. */
object CrmClientEditDestination {
    const val route = "crm_client_edit"
    const val itemIdPT = "itemIdPT"
    const val clientId = "clientId"
    const val routeWithArgs = "crm_client_edit/{itemIdPT}/{clientId}"
}

object CrmTasksDestination {
    const val route = "crm_tasks"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "crm_tasks/{itemIdPT}"
}
