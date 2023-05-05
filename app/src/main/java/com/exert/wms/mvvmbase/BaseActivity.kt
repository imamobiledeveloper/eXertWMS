package com.exert.wms.mvvmbase

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.exert.wms.R
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.mvvmbase.ExertBaseActivity

abstract class BaseActivity<VM : BaseViewModel, VB : ViewDataBinding> : ExertBaseActivity() {

    protected abstract val mViewModel: VM

    override val showHomeButton: Int = 0

    lateinit var binding: VB

    @LayoutRes
    abstract fun getLayoutID(): Int

    fun getViewDataBinding(): VB = binding

    abstract fun getBindingVariable(): Int

    abstract fun onBindData(binding: VB)

    fun showHomeButtonVariable(): Boolean = showHomeButton == 1

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        performDataBinding()
    }

    private fun performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutID())
        binding.setVariable(getBindingVariable(), mViewModel)
        binding.executePendingBindings()
        onBindData(binding)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        if (showHomeButton > 0) {
        menuInflater.inflate(R.menu.menu_home, menu)
        for (i in 0 until menu!!.size()) {
            if (menu.getItem(i).title == getString(R.string.home)) {
                menu.getItem(i).isVisible = showHomeButton == 1
            } else if (showHomeButton == 0) {
                menu.getItem(i).isVisible = showHomeButton != 1
            } else {
                menu.getItem(i).isVisible = false
            }
        }
//        }
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
        return when (item.itemId) {
//            android.R.id.home -> onBackPressed()
            R.id.home_menu -> {
                HomeActivity.relaunch(this)
                true
            }
            R.id.logout_menu -> {
                showBriefToastMessage("You are Logged out", coordinateLayout)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun showToolBar(toolbar: View) {
//        toolbar.show()
    }

    fun hideToolBar(toolbar: View) {
//        toolbar.hide()
    }
}