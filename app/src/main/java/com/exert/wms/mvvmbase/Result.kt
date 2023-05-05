package com.exert.wms.mvvmbase

sealed class Result {
    data class Success<T>(val content: T) : Result()

    data class Error(val exception: Throwable) : Result()

    object Loading : Result()
}
