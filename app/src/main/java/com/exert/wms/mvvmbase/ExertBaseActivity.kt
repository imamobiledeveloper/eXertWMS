package com.exert.wms.mvvmbase

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.R
import com.exert.wms.utils.appSnackbar
//import com.exert.wms.utils.appSnackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG

abstract class ExertBaseActivity : BaseAppCompatActivity() {

    open val coordinateLayout: CoordinatorLayout?=null

    fun showBriefToastMessage(
        message: String,
        coordinatorLayout: CoordinatorLayout?,
        bgColor: Int= getColor(R.color.orange)
    ) {
        coordinatorLayout?.let { layout ->
            appSnackbar(layout, message, LENGTH_LONG, bgColor = bgColor)?.setDuration(4000)?.show()
        }
    }

}