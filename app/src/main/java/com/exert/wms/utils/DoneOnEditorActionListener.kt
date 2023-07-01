package com.exert.wms.utils

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

import android.widget.TextView

import android.widget.TextView.OnEditorActionListener


internal open class DoneOnEditorActionListener : OnEditorActionListener {
    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val imm: InputMethodManager =
                v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            return true
        }
        return false
    }
}