package com.zaroslikov.fermacompose2

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class DefaultProcessLifecycleObserver(
    private val onProcessCaneForeground: () -> Unit
): DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner){
        onProcessCaneForeground()
    }
}