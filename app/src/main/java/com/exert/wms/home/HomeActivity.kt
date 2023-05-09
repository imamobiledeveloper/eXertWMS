package com.exert.wms.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityHomeBinding
import com.exert.wms.databinding.ActivityLoginBinding
import com.exert.wms.itemStocks.ItemStocksActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.startActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override val title = R.string.home

    override val showHomeButton: Int = 0

    override fun getLayoutID(): Int = R.layout.activity_home

    override val mViewModel by lazy {
        getViewModel<HomeViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        setFeaturesList()
        observeViewModel()
    }

    private fun setFeaturesList() {
        val featuresList = resources.getStringArray(R.array.features_list).toList()
        binding.featuresRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.featuresRecyclerView.adapter = FeaturesListAdapter(featuresList) {
            navigateToFeature(it)
        }
    }

    private fun observeViewModel() {
        mViewModel.loggedOut.observe(this) { status ->
            when {
                status -> finish()
                else -> {}
            }
        }
    }

    private fun navigateToFeature(featureName: String) {
        when (featureName) {
            "itemStocks" -> startActivity<ItemStocksActivity>()
            else -> startActivity(Intent(this@HomeActivity, ItemStocksActivity::class.java))
        }
    }

    override fun onBindData(binding: ActivityHomeBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        hideKeyBoard()
        super.onBackPressed()
    }

    companion object {
        fun relaunch(activity: Activity) {
            val intent = Intent(activity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(intent)
            activity.finishAffinity()
        }
    }
}