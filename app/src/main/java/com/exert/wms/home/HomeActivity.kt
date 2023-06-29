package com.exert.wms.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityHomeBinding
import com.exert.wms.itemStocks.ItemStocksActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.StockAdjustmentBaseActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override val title = R.string.home

    override val showHomeButton: Int = 0
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = binding.myDrawerLayout
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        setFeaturesList()

        observeViewModel()
    }

    private fun setFeaturesList() {
        val featuresList= FeaturesListDto().getFeaturesList()
        binding.homeDashboardLayout.featuresRecyclerView.layoutManager= GridLayoutManager(this,3)
        binding.homeDashboardLayout.featuresRecyclerView.adapter = FeaturesListAdapter(featuresList) {
            navigateToFeature(it.name)
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
        val featuresList = resources.getStringArray(R.array.features_list)
        when (featureName) {
            featuresList[0] -> startActivity<ItemStocksActivity>()
            featuresList[1]  -> startActivity<StockAdjustmentBaseActivity>()
            else -> startActivity(Intent(this@HomeActivity, ItemStocksActivity::class.java))
        }
    }

    override fun onBindData(binding: ActivityHomeBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        hideKeyBoard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
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