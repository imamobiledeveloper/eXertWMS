package com.exert.wms.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.databinding.FragmentNavigationBinding
import com.exert.wms.mvvmbase.NavigationFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class HomeNavigationFragment : NavigationFragment<HomeViewModel, FragmentNavigationBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChildFragment(HomeFragment())
    }

    override val mViewModel by lazy {
        getViewModel<HomeViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNavigationBinding {
        return FragmentNavigationBinding.inflate(inflater, container, false)
    }

    override fun onBindData(binding: FragmentNavigationBinding) {
    }
}