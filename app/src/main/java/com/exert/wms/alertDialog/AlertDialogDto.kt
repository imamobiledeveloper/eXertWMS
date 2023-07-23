package com.exert.wms.alertDialog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlertDialogDto(
    val title: String,
    val message: String,
    val positiveButtonText: String = "OK",
    val negativeButtonText: String = "Cancel",
    val showNegativeButton: Boolean = false
) : Parcelable
