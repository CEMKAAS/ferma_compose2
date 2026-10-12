package com.zaroslikov.fermacompose2.utils

import android.content.Context

interface ResourceProvider {
    fun getString(id: Int): String

    /** Строка из `<plurals>` для [quantity] с подстановкой [formatArgs]. */
    fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String
}

class ResourceProviderImpl(context: Context) : ResourceProvider {
    private val resources = context.resources
    override fun getString(id: Int) = resources.getString(id)

    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String =
        resources.getQuantityString(id, quantity, *formatArgs)
}
