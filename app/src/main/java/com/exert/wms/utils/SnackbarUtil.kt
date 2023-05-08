package com.exert.wms.utils

import android.app.Activity
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.exert.wms.R
import com.google.android.material.snackbar.Snackbar

fun Activity.appSnackbar(
    coordinatorLayout: CoordinatorLayout,
    message: String,
    duration: Int, retryCallback: ((View) -> Unit)? = null,
    bgColor: Int = this.getColor(R.color.black)
) =
    ContextCompat.getDrawable(this@appSnackbar, R.drawable.ic_launcher_foreground)?.let {
        Snackbar.make(
            coordinatorLayout,
            SpannableStringBuilder(message).setColor(this, android.R.color.white),
            duration
        ).apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                .setTextColor(ContextCompat.getColor(this@appSnackbar, R.color.white))

            setAction("") { retryCallback?.invoke(view) }
        }.setIcon(it, resources.getColor(android.R.color.white, theme))
            .setBackgroundTint(bgColor)

    }

fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar {
    return this.apply {
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.text = ""
        drawable.setTint(colorTint)
        drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}