package com.exert.wms.mvvmbase

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.ViewDataBinding
import com.exert.wms.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class MVVMBottomSheetDialogFragment<VM : BaseViewModel, VB : ViewDataBinding> :
    BottomSheetDialogFragment() {

    protected abstract val mViewModel: VM

    lateinit var binding: VB

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    private val baseActivity get() = activity as ExertBaseActivity

    open val coordinateLayout: CoordinatorLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
//        binding.lifecycleOwner = { lifecycle }
        onBindData(binding)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val layout: FrameLayout =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            layout.setBackgroundResource(android.R.color.transparent)
            val bottomSheetBehavior:BottomSheetBehavior<*> = BottomSheetBehavior.from(layout)
            setUpFullHeight(layout)
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    private fun setUpFullHeight(layout: View) {
        val layoutParams=layout.layoutParams
        layoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT
        layout.layoutParams=layoutParams
    }

    abstract fun onBindData(binding: VB)

    fun showBriefToastMessage(
        message: String,
        coordinatorLayout: CoordinatorLayout?,
        bgColor: Int = requireActivity().getColor(R.color.red)
    ) {
        baseActivity.showBriefToastMessage(message, coordinatorLayout, bgColor)
    }

}