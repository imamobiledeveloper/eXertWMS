package com.exert.wms.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

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

inline fun <reified T : Activity> Activity.startActivity() {
    startActivity(createIntent<T>())
}

inline fun <reified T : Activity> Context.createIntent() =
    Intent(this, T::class.java)

fun Spinner.selected(action: (parent: AdapterView<*>?, position: Int) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            action(parent, position)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
    }
}