package com.exert.wms.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.exert.wms.R
import com.exert.wms.databinding.FragmentHomeBinding
import com.exert.wms.mvvmbase.MVVMFragment
import com.google.android.material.navigation.NavigationView
import org.koin.androidx.viewmodel.ext.android.getViewModel

class HomeFragment : MVVMFragment<HomeViewModel, FragmentHomeBinding>() {

    var navigationView: NavigationView? = null

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

        navigationView = (activity as HomeActivity).getNavigationView()
    }

    private fun observeViewModel() {
        mViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.userNameTV.text = getString(R.string.user_name, name)
        }
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
            featuresList[0] -> (activity as HomeActivity).setSelectedItem(1)
            featuresList[1] -> (activity as HomeActivity).setSelectedItem(2)
            featuresList[2] -> (activity as HomeActivity).setSelectedItem(3)
            featuresList[3] -> (activity as HomeActivity).setSelectedItem(4)
            featuresList[4] -> (activity as HomeActivity).setSelectedItem(5)
            featuresList[5] -> (activity as HomeActivity).setSelectedItem(6)
            featuresList[6] -> (activity as HomeActivity).setSelectedItem(7)
            featuresList[7] -> (activity as HomeActivity).setSelectedItem(8)
            featuresList[8] -> (activity as HomeActivity).setSelectedItem(9)
            else ->  (activity as HomeActivity).logOut()//(activity as HomeActivity).setSelectedItem(1)

        }
    }

}