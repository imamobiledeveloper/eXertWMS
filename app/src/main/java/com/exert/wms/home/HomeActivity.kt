package com.exert.wms.home

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityHomeBinding
import com.exert.wms.itemStocks.ItemStocksFragment
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.StockAdjustmentBaseFragment
import com.exert.wms.stockReconciliation.StockReconciliationFragment
import com.google.android.material.navigation.NavigationView
import org.koin.androidx.viewmodel.ext.android.getViewModel

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override val title = R.string.home

    private var showNavigationButton: Int = 0

    override val showHomeButton: Int = showNavigationButton

    lateinit var drawerLayout: DrawerLayout
    var navViewListener: NavigationView.OnNavigationItemSelectedListener? = null

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.homeFragmentContainerView) as NavHostFragment).navController
    }

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

        setUpNavigationDrawer()
        observeViewModel()
        setFirstItemNavigationView(R.id.homeScreen)
    }

    override fun setTitle(title: CharSequence?) {
        supportActionBar?.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = getNavHostFragment()
        return if (navHost.childFragmentManager.backStackEntryCount < 1) {
            finish()
            false
        } else {
            super.onBackPressed()
            false
        }
    }

    private fun getNavHostFragment(): NavHostFragment {
        return supportFragmentManager.findFragmentById(R.id.homeFragmentContainerView) as NavHostFragment
    }

    private fun setUpNavigationDrawer() {
        drawerLayout = binding.myDrawerLayout
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navViewListener = NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showNavigationButton=0
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, HomeFragment()).commit()
                }
                R.id.nav_item_stocks -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, ItemStocksFragment()).commit()
                }
                R.id.nav_stock_adjustment -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockAdjustmentBaseFragment())
                        .commit()
                }
                R.id.nav_stock_reconciliation -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_transfer_out -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_transfer_in -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_delivery_receipt -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_delivery_note -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_purchase_return -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_sales_return -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, StockReconciliationFragment())
                        .commit()
                }
                R.id.nav_logout -> {
                    logOut()
                }
                else -> {
                    showNavigationButton=1
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFragmentContainerView, HomeFragment())
                        .commit()
                }
            }

            item.isChecked = true
            setTitle(item.title)
            drawerLayout.closeDrawer(GravityCompat.START)
            true

        }
        binding.navigationView.setNavigationItemSelectedListener(navViewListener)
    }

    private fun setFirstItemNavigationView(screenResourceId: Int) {
        binding.navigationView.setCheckedItem(screenResourceId)
        binding.navigationView.menu.performIdentifierAction(screenResourceId, 0)
    }

    fun getNavigationView() = binding.navigationView

    fun setSelectedItem(id: Int) {
        navViewListener?.let {
            it.onNavigationItemSelected(binding.navigationView.menu.getItem(id))
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

    override fun onBindData(binding: ActivityHomeBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        hideKeyBoard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        fun relaunch(activity: Activity) {
//            val intent = Intent(activity, HomeActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            activity.startActivity(intent)
//            activity.finishAffinity()
        }
    }
}