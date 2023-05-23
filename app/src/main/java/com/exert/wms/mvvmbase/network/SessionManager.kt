package com.exert.wms.mvvmbase.network

class SessionManager {
    val invalidationListeners: MutableList<SessionInvalidationListener> = mutableListOf()

    fun invalidateSession() {
        invalidationListeners.forEach { it.reset() }
    }
}

interface SessionInvalidationListener {
    fun reset()
}