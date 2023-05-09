package com.exert.wms.login

import android.content.Intent
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityLoginBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.toEditable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.getViewModel


class LoginActivity :
    BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override fun getLayoutID(): Int = R.layout.activity_login

    override val showHomeButton: Int = -1

    override val mViewModel by lazy {
        getViewModel<LoginViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideToolBar()
        setTextWatchers()
        observeViewModel()

        binding.loginButton.setOnClickListener {
            hideKeyBoard()
            mViewModel.getFinancialToken(
                binding.usernameEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }
    }

    private fun setTextWatchers() {
        binding.usernameEditText.nextFocusForwardId = binding.passwordEditText.id
        binding.usernameEditText.doOnTextChanged { text, _, _, _ ->
            mViewModel.setUserName(text.toString())
        }
        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            mViewModel.setPassword(text.toString())
        }
    }

    private fun observeViewModel() {
        mViewModel.loginUserStatus.observe(this, Observer {
            if (it) {
                showBriefToastMessage("LoggedIn successfully", coordinateLayout)
                clearFieldsData()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                showBriefToastMessage(getString(R.string.error_login_message), coordinateLayout)
            }
        })

        mViewModel.errorLoginMessage.observe(this, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
        })
        mViewModel.errorUserNameMessage.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.usernameEditTextLayout,
                    binding.usernameEditText,
                    getString(R.string.error_username_empty)
                )
            } else {
                disableErrorMessage(
                    binding.usernameEditTextLayout,
                    binding.usernameEditText,
                )
            }
        })
        mViewModel.errorPasswordMessage.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.passwordEditTextLayout,
                    binding.passwordEditText,
                    getString(R.string.error_password_empty)
                )
            } else {
                disableErrorMessage(
                    binding.passwordEditTextLayout,
                    binding.passwordEditText,
                )
            }
        })
    }

    private fun clearFieldsData() {
        binding.usernameEditText.text = resources.getString(R.string.emptyString).toEditable()
        binding.passwordEditText.text = resources.getString(R.string.emptyString).toEditable()
    }

    override fun onBindData(binding: ActivityLoginBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

    private fun enableErrorMessage(
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

    private fun disableErrorMessage(
        textInputLayout: TextInputLayout,
        editTextLayout: TextInputEditText
    ) {
        textInputLayout.isErrorEnabled = false
        editTextLayout.isSelected = false
        textInputLayout.clearFocus()
    }
}