package com.exert.wms.mvvmbase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.exert.wms.LogoutManager
import com.exert.wms.R
import com.exert.wms.SessionExpirationDialog
import com.exert.wms.SessionExpirationObject
import com.exert.wms.home.HomeActivity
import com.exert.wms.login.LoginActivity
import com.exert.wms.login.api.LoginDataSource
import com.exert.wms.utils.Constants
import com.exert.wms.utils.UserDefaults
import com.exert.wms.utils.toEditable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import java.io.Serializable
import java.util.concurrent.TimeUnit

abstract class BaseActivity<VM : BaseViewModel, VB : ViewDataBinding> : ExertBaseActivity() {

    protected abstract val mViewModel: VM

    override val showHomeButton: Int = 0

    lateinit var binding: VB

    private var sessionExpirationDialog: Disposable? = null

    private val logoutManager: LogoutManager by inject()

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
            } else if (showHomeButton == 0) {
                menu.getItem(i).isVisible = showHomeButton != 1
            } else {
                menu.getItem(i).isVisible = false
                showBackButton()
            }
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        sessionExpirationDialog?.dispose()
    }

    override fun onResume() {
        super.onResume()
        sessionExpirationDialog =
            SessionExpirationObject.observable.debounce(1, TimeUnit.MINUTES).subscribe {
                SessionExpirationDialog.newInstance()
                    .show(this.supportFragmentManager, SessionExpirationDialog::class.java.name)
            }
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

            R.id.notifications_menu -> {
//                logOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logOut() {
        clearCaches()
        logoutManager.logout()
        mViewModel.onCleared()
        launchActivity(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    private fun launchActivity(intentFlags: Int = 0) {
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

    inline fun <reified T : Activity> Context.createIntent() =
        Intent(this, T::class.java)

    inline fun <reified T : Activity> Context.startActivity() =
        startActivity(createIntent<T>())

    inline fun <reified T : Activity> Context.startActivity(options: Bundle) =
        startActivity(createIntent<T>(options))

    inline fun <reified T : Activity> Context.createIntent(bundle: Bundle) =
        Intent(this, T::class.java).apply { putExtras(bundle) }

//    inline fun <reified T : Activity> Context.createIntent(vararg extras: Pair<String, Any?>) =
//        Intent(this, T::class.java).apply { putExtras(bundleOf(*extras)) }

    fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)!!
        else
            activity.intent.getSerializableExtra(name) as T
    }


    fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializableExtra(key, m_class)!!
        else
            this.getSerializableExtra(key) as T
    }

    private fun clearCaches() {
        loginDataSource.clearLoginCache()
//        userDefaults.clear()
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
        message: String,
        requestFocus: Boolean = true
    ) {
        if (!textInputLayout.isErrorEnabled) {
            textInputLayout.isErrorEnabled = true
            editTextLayout.isSelected = true
            textInputLayout.error = message
            if (requestFocus) {
                editTextLayout.requestFocus()
            }
            editTextLayout.text?.let { editTextLayout.setSelection(it.length) }
            editTextLayout.isSelected = true
        }
    }

    fun disableErrorMessage(
        textInputLayout: TextInputLayout, editTextLayout: TextInputEditText
    ) {
        textInputLayout.isErrorEnabled = false
        textInputLayout.clearFocus()
        editTextLayout.isSelected = false
    }

    fun disableErrorMessageWhileEditing(
        textInputLayout: TextInputLayout, editTextLayout: TextInputEditText
    ) {
        textInputLayout.isErrorEnabled = false
        editTextLayout.isSelected = false
        editTextLayout.isFocusable = true
    }

    fun setTextViewText(textView: TextView, text: String, visible: Int) {
        textView.text = text
        textView.visibility = visible
    }

    fun setTextViewVisibility(textView: TextView, visible: Int) {
        textView.visibility = visible
    }

    fun clearTextInputEditText(editText: TextInputEditText, hintTV: TextView) {
        editText.text = getString(R.string.empty).toEditable()
        hintTV.visibility = View.VISIBLE
    }

    fun getEditTextText(editText: TextInputEditText): Editable? = editText.text

    fun edittextTextWatcher(textInputLayout: TextInputLayout, editText: TextInputEditText) =
        object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val isTextEmpty = getEditTextText(editText)?.trim()?.isNotEmpty()
                if (isTextEmpty == true) {
                    disableErrorMessageWhileEditing(
                        textInputLayout, editText
                    )
                }
            }
        }

    fun triggerScanner(context: Context) {
        context.sendBroadcast(
            Intent(Constants.EXTRA_CONTROL)
                .setPackage(Constants.EXTRA_SCANNER_APP_PACKAGE)
                .putExtra(Constants.EXTRA_SCAN, true)
        )
//        Toast.makeText(this, "Releasing the Scanner", Toast.LENGTH_SHORT).show()
    }

    fun claimScanner(context: Context) {
        val properties = Bundle()
        properties.putBoolean(Constants.DPR_DATA_INTENT_KEY, true)
        properties.putString(Constants.ACTION_BARCODE_DATA_KEY, Constants.ACTION_BARCODE_DATA)
        context.sendBroadcast(
            Intent(Constants.ACTION_CLAIM_SCANNER)
                .putExtra(Constants.EXTRA_SCANNER, Constants.EXTRA_SCANNER_VALUE)
                .putExtra(Constants.EXTRA_PROFILE, Constants.EXTRA_PROFILE_VALUE)
                .putExtra(Constants.EXTRA_PROPERTIES, properties)
        )
    }

    fun releaseScanner(context: Context) {
        context.sendBroadcast(Intent(Constants.ACTION_RELEASE_SCANNER))
    }

    private fun bytesToHexString(arr: ByteArray?): String {
        var s = "[]"
        if (arr != null) {
            s = "["
            for (i in arr.indices) {
                s += "0x" + Integer.toHexString(arr[i].toInt()) + ", "
            }
            s = s.substring(0, s.length - 2) + "]"
        }
        return s
    }
}