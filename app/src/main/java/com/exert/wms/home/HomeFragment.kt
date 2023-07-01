package com.exert.wms.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.exert.wms.R
import com.exert.wms.databinding.FragmentHomeBinding
import com.exert.wms.mvvmbase.MVVMFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class HomeFragment : MVVMFragment<HomeViewModel, FragmentHomeBinding>() {

    override val mViewModel by lazy {
        getViewModel<HomeViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFeaturesList()
        observeViewModel()
    }

    private fun observeViewModel() {

    }

    override fun onBindData(binding: FragmentHomeBinding) {
        binding.viewModel = mViewModel
    }

    private fun setFeaturesList() {
        val featuresList = FeaturesListDto().getFeaturesList()
        binding.featuresRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.featuresRecyclerView.adapter = FeaturesListAdapter(featuresList) {
            navigateToFeature(it.name)
        }
    }

    private fun navigateToFeature(featureName: String) {
        val featuresList = resources.getStringArray(R.array.features_list)
        when (featureName) {
//            featuresList[0] -> startActivity<ItemStocksActivity>()
//            featuresList[1]  -> startActivity<StockAdjustmentBaseActivity>()
//            else -> startActivity(Intent(this@HomeActivity, ItemStocksActivity::class.java))
        }
    }


}