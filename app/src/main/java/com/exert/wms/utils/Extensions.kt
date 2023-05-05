package com.exert.wms.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

inline fun <reified T: Activity> Activity.startActivity() {
    startActivity(createIntent<T>())
}
inline fun <reified T: Activity> Context.createIntent() =
    Intent(this, T::class.java)