package com.exert.wms.mvvmbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.ViewDataBinding
import com.exert.wms.LogoutManager
import org.koin.android.ext.android.inject

abstract class MVVMFragment<VM : BaseViewModel, VB : ViewDataBinding> : BaseFragment() {

    protected abstract val mViewModel: VM

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    private val logoutManager: LogoutManager by inject()

    open val coordinateLayout: CoordinatorLayout? = null

    lateinit var binding: VB

    abstract fun onBindData(binding: VB)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
//        binding.lifecycleOwner{ lifecycle }
        onBindData(binding)

        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

}