package com.exert.wms.mvvmbase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import com.exert.wms.LogoutManager
import com.exert.wms.R
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
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        // The usage of an interface lets you inject your own implementation
//        val menuHost: MenuHost = requireActivity()
//
//        // Add menu items without using the Fragment Menu APIs
//        // Note how we can tie the MenuProvider to the viewLifecycleOwner
//        // and an optional Lifecycle.State (here, RESUMED) to indicate when
//        // the menu should be visible
//        menuHost.addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                // Add menu items here
//                menuInflater.inflate(R.menu.menu_home, menu)
//
//                for (i in 0 until menu!!.size()) {
//                    if (menu.getItem(i).title == getString(R.string.home)) {
//                        menu.getItem(i).isVisible = showHomeButton == 1
////                hideBackButton()
//                    } else if (showHomeButton == 0) {
//                        menu.getItem(i).isVisible = showHomeButton != 1
////                hideBackButton()
//                    } else {
//                        menu.getItem(i).isVisible = false
////                        showBackButton()
//                    }
//                }
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                // Handle the menu selection
//                return when (menuItem.itemId) {
//                    R.id.home_menu -> {
//                        // clearCompletedTasks()
//                        true
//                    }
//                    R.id.notifications_menu -> {
//                        // loadTasks(true)
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
//    }

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
        }
    }

    fun disableErrorMessage(
        textInputLayout: TextInputLayout, editTextLayout: TextInputEditText
    ) {
        textInputLayout.isErrorEnabled = false
        editTextLayout.isSelected = false
        textInputLayout.clearFocus()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_home, menu)
//        for (i in 0 until menu!!.size()) {
//            if (menu.getItem(i).title == getString(R.string.home)) {
//                menu.getItem(i).isVisible = showHomeButton == 1
////                hideBackButton()
//            } else if (showHomeButton == 0) {
//                menu.getItem(i).isVisible = showHomeButton != 1
////                hideBackButton()
//            } else {
//                menu.getItem(i).isVisible = false
//                showBackButton()
//            }
//        }
//
//        return true
//    }

    fun clearTextInputEditText(editText: TextInputEditText) {
        editText.text=getString(R.string.empty).toEditable()
    }
}