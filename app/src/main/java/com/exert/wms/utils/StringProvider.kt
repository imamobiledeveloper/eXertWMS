package com.exert.wms.utils

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {
    fun getString(@StringRes resId: Int): String = context.getString(resId)
    fun getString(@StringRes resId: Int, vararg args: Any): String = context.getString(resId, *args)
}