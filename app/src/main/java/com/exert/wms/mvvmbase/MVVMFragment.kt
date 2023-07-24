package com.exert.wms.mvvmbase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import com.exert.wms.LogoutManager
import com.exert.wms.R
import com.exert.wms.utils.Constants.ACTION_BARCODE_DATA
import com.exert.wms.utils.Constants.ACTION_BARCODE_DATA_KEY
import com.exert.wms.utils.Constants.ACTION_CLAIM_SCANNER
import com.exert.wms.utils.Constants.ACTION_RELEASE_SCANNER
import com.exert.wms.utils.Constants.DPR_DATA_INTENT_KEY
import com.exert.wms.utils.Constants.EXTRA_CONTROL
import com.exert.wms.utils.Constants.EXTRA_PROFILE
import com.exert.wms.utils.Constants.EXTRA_PROFILE_VALUE
import com.exert.wms.utils.Constants.EXTRA_PROPERTIES
import com.exert.wms.utils.Constants.EXTRA_SCAN
import com.exert.wms.utils.Constants.EXTRA_SCANNER
import com.exert.wms.utils.Constants.EXTRA_SCANNER_APP_PACKAGE
import com.exert.wms.utils.Constants.EXTRA_SCANNER_VALUE
import com.exert.wms.utils.toEditable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.inject
import java.io.Serializable

abstract class MVVMFragment<VM : BaseViewModel, VB : ViewDataBinding> : BaseFragment() {

    protected abstract val mViewModel: VM

    open val showHomeButton: Int = 0
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    private val logoutManager: LogoutManager by inject()

    open val coordinateLayout: CoordinatorLayout? = null

    lateinit var binding: VB

    abstract fun onBindData(binding: VB)

    fun showHomeButtonVariable(): Boolean = showHomeButton == 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
//        binding.lifecycleOwner{ lifecycle }
        onBindData(binding)

        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
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

    fun hideKeyBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    inline fun <reified T : Activity> Context.createIntent(vararg extras: Pair<String, Any?>) =
        Intent(this, T::class.java).apply { putExtras(bundleOf(*extras)) }

    inline fun <reified T : Activity> Context.createIntent() = Intent(this, T::class.java)

    inline fun <reified T : Activity> Context.startActivity() = startActivity(createIntent<T>())

    inline fun <reified T : Activity> Context.startActivity(options: Bundle) =
        startActivity(createIntent<T>(options))

    inline fun <reified T : Activity> Context.createIntent(bundle: Bundle) =
        Intent(this, T::class.java).apply { putExtras(bundle) }

//    inline fun <reified T : Activity> Context.createIntent(vararg extras: Pair<String, Any?>) =
//        Intent(this, T::class.java).apply { putExtras(bundleOf(*extras)) }

    fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) activity.intent.getSerializableExtra(
            name,
            clazz
        )!!
        else activity.intent.getSerializableExtra(name) as T
    }


    fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) this.getSerializableExtra(
            key,
            m_class
        )!!
        else this.getSerializableExtra(key) as T
    }

    fun setTextViewText(textView: TextView, text: String, visible: Int) {
        textView.text = text
        textView.visibility = visible
    }

    fun setTextViewVisibility(textView: TextView, visible: Int) {
        textView.visibility = visible
    }

//    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
////        super.onCreateOptionsMenu(menu, inflater)
//        menuInflater.inflate(R.menu.menu_home, menu)
//        for (i in 0 until menu.size()) {
//            if (menu.getItem(i).title == getString(R.string.home)) {
//                menu.getItem(i).isVisible = showHomeButton == 1
//            } else if (showHomeButton == 0) {
//                menu.getItem(i).isVisible = showHomeButton != 1
//            } else {
//                menu.getItem(i).isVisible = false
////                showBackButton()
//            }
//        }
//
//        super.onCreateOptionsMenu(menu, menuInflater)
//    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        val item = menu.findItem(R.id.home_menu)
//        if (item != null) item.isVisible = true
//        val notificationsitem = menu.findItem(R.id.notifications_menu)
//        if (notificationsitem != null) notificationsitem.isVisible = false
//    }

    fun clearTextInputEditText(editText: TextInputEditText, hintTV: TextView) {
        editText.text = getString(R.string.empty).toEditable()
        hintTV.visibility = View.VISIBLE
    }

    fun getEditTextText(editText: TextInputEditText): Editable? = editText.text

    fun edittextTextWatcher(textInputLayout: TextInputLayout, editText: TextInputEditText) =
        object : TextWatcher {
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

    fun edittextFocusChangeListener(
        hintTextView: TextView,
        editText: TextInputEditText,
        errorMessage: String
    ) =
        View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextViewVisibility(
                    hintTextView,
                    View.GONE
                )
            } else {
                if (getEditTextText(editText)?.trim().isNullOrEmpty()) {
                    setTextViewVisibility(
                        hintTextView,
                        View.VISIBLE
                    )
                } else {
                    setTextViewVisibility(
                        hintTextView,
                        View.GONE
                    )
                }
            }

        }

    fun triggerScanner(context: Context) {
        context.sendBroadcast(
            Intent(EXTRA_CONTROL)
                .setPackage(EXTRA_SCANNER_APP_PACKAGE)
                .putExtra(EXTRA_SCAN, true)
        )
    }

    fun claimScanner(context: Context) {
        val properties = Bundle()
        properties.putBoolean(DPR_DATA_INTENT_KEY, true)
        properties.putString(ACTION_BARCODE_DATA_KEY, ACTION_BARCODE_DATA)
        context.sendBroadcast(
            Intent(ACTION_CLAIM_SCANNER)
                .putExtra(EXTRA_SCANNER, EXTRA_SCANNER_VALUE)
                .putExtra(EXTRA_PROFILE, EXTRA_PROFILE_VALUE)
                .putExtra(EXTRA_PROPERTIES, properties)
        )
    }

    fun releaseScanner(context: Context) {
        context.sendBroadcast(Intent(ACTION_RELEASE_SCANNER))
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