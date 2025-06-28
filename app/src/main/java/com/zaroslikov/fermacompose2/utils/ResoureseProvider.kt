package com.zaroslikov.fermacompose2.utils

import android.content.Context

interface ResourceProvider {
    fun getString(id: Int): String
}

class ResourceProviderImpl(context: Context) : ResourceProvider {
    private val resources = context.resources
    override fun getString(id: Int) = resources.getString(id)
}
