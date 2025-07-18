package com.realkarim.terra.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class Navigator {
    private val _navigationEventFlow = MutableSharedFlow<NavigationEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val navigationEventFlow = _navigationEventFlow.asSharedFlow()

    fun navigate(event: NavigationEvent) {
        _navigationEventFlow.tryEmit(event)
    }
}