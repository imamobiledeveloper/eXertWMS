package com.exert.wms.utils

import androidx.lifecycle.ViewModelStore

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false
    private var map = HashMap<ViewModelStore, Boolean> ()

    // Returns the content if it hasn't been handled yet; otherwise, null.
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun getContentIfNotHandled(viewModelStore: ViewModelStore): T? {
        return if (map.contains(viewModelStore)) {
            null
        } else {
            map[viewModelStore] = true
            content
        }
    }

    // Returns the content, even if it has been handled.
    fun peekContent(): T = content
}