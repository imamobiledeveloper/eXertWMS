package com.exert.wms.mvvmbase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.exert.wms.R
import com.exert.wms.home.HomeActivity
import com.exert.wms.login.LoginActivity
import com.exert.wms.login.api.LoginDataSource
import com.exert.wms.utils.UserDefaults
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.inject

abstract class BaseActivity<VM : BaseViewModel, VB : ViewDataBinding> : ExertBaseActivity() {

    protected abstract val mViewModel: VM

    override val showHomeButton: Int = 0

    lateinit var binding: VB

    @LayoutRes
    abstract fun getLayoutID(): Int

    fun getViewDataBinding(): VB = binding

    abstract fun getBindingVariable(): Int

    abstract fun onBindData(binding: VB)

    override val title: Int = R.string.app_name

    fun showHomeButtonVariable(): Boolean = showHomeButton == 1

    private val loginDataSource: LoginDataSource by inject()

    private val userDefaults: UserDefaults by inject()

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
        menuInflater.inflate(R.menu.menu_home, menu)
        for (i in 0 until menu!!.size()) {
            if (menu.getItem(i).title == getString(R.string.home)) {
                menu.getItem(i).isVisible = showHomeButton == 1
                hideBackButton()
            } else if (showHomeButton == 0) {
                menu.getItem(i).isVisible = showHomeButton != 1
                hideBackButton()
            } else {
                menu.getItem(i).isVisible = false
                showBackButton()
            }
        }

        return true
    }

    private fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun hideBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.home_menu -> {
                HomeActivity.relaunch(this)
                true
            }
            R.id.logout_menu -> {
                logOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut() {
        clearCaches()
        mViewModel.onCleared()
        launchActivity(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    public fun launchActivity(intentFlags: Int = 0) {
        val intent = Intent(this, LoginActivity::class.java)
        if (intentFlags > 0) {
            intent.addFlags(intentFlags)
        }
        startActivity(intent)
    }

//    inline fun <reified T : Activity> Context.startActivity() =
//        startActivity(Intent(this, T::class.java))

    inline fun <reified T : Activity> Context.createIntent(vararg extras: Pair<String, Any?>) =
        Intent(this, T::class.java).apply { putExtras(bundleOf(*extras)) }

    inline fun <reified T: Activity> Context.createIntent() =
        Intent(this, T::class.java)

    inline fun <reified T : Activity> Context.startActivity() =
        startActivity(createIntent<T>())

    inline fun <reified T : Activity> Context.startActivity(vararg extras: Pair<String, Any?>) =
        startActivity(createIntent<T>(*extras))

    private fun clearCaches() {
        loginDataSource.clearLoginCache()
        userDefaults.clear()
    }

    fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun showToolBar() {
        supportActionBar?.show()
    }

    fun hideToolBar() {
        supportActionBar?.hide()
    }

    fun enableErrorMessage(
        textInputLayout: TextInputLayout,
        editTextLayout: TextInputEditText,
        message: String
    ) {
        if (!textInputLayout.isErrorEnabled) {
            textInputLayout.isErrorEnabled = true
            editTextLayout.isSelected = true
            textInputLayout.error = message
            editTextLayout.requestFocus()
            editTextLayout.text?.let { editTextLayout.setSelection(it.length) }
        }
    }

    fun disableErrorMessage(
        textInputLayout: TextInputLayout,
        editTextLayout: TextInputEditText
    ) {
        textInputLayout.isErrorEnabled = false
        editTextLayout.isSelected = false
        textInputLayout.clearFocus()
    }
}