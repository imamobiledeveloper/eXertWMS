package com.exert.wms.mvvmbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.exert.wms.R
import com.exert.wms.utils.appSnackbar
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    @StringRes
    open val title: Int? = null
    open val titleString: String? = null

    private val baseActivity get() = activity as ExertBaseActivity

    fun showBriefToastMessage(
        message: String,
        coordinatorLayout: CoordinatorLayout?,
        bgColor: Int = baseActivity.getColor(R.color.orange)
    ) {
        coordinatorLayout?.let { layout ->
            baseActivity.appSnackbar(layout, message, Snackbar.LENGTH_LONG, bgColor = bgColor)?.setDuration(4000)?.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        title?.let { activity?.setTitle(it) }
        setUpToolBar()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setUpToolBar() {
        (activity as AppCompatActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.show()
        }
    }

}