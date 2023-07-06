package com.exert.wms.deliveryNote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentDeliveryNoteBaseBinding
import com.exert.wms.mvvmbase.MVVMFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryNoteBaseFragment :
    MVVMFragment<DeliveryNoteBaseViewModel, FragmentDeliveryNoteBaseBinding>() {

    override val title = R.string.delivery_note

    override val mViewModel by lazy {
        getViewModel<DeliveryNoteBaseViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeliveryNoteBaseBinding {
        return FragmentDeliveryNoteBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentDeliveryNoteBaseBinding) {

    }

    private fun observeViewModel() {

    }


}