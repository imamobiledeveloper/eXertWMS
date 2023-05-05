package com.exert.wms.utils

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

fun SpannableStringBuilder.setColor(
    context: Context?,
    colorId: Int,
    range: IntRange = 0..this.length
): SpannableStringBuilder {
    val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context!!, colorId))
    setSpan(colorSpan, range.first, range.last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}