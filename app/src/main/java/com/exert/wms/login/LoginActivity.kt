package com.exert.wms.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityLoginBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.BaseActivity
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

        setTextWatchers()
        observeViewModel()

        binding.loginButton.setOnClickListener {
            mViewModel.getFinancialToken(
                binding.userNameLayout.editText.text.toString(),
                binding.passwordLayout.editText.text.toString()
            )
        }
    }

    private fun setTextWatchers() {
        binding.userNameLayout.editText.inputType = InputType.TYPE_CLASS_TEXT
        binding.passwordLayout.editText.inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD

        binding.userNameLayout.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                mViewModel.setUserName(editable.toString())
            }

        })

        binding.passwordLayout.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                mViewModel.setPassword(editable.toString())
            }

        })
    }

    private fun observeViewModel() {
        mViewModel.loginUserStatus.observe(this, Observer {
            if (it) {
                showBriefToastMessage("LoggedIn successfully", coordinateLayout)
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
                    binding.userNameLayout.editTextLayout,
                    binding.userNameLayout.editText,
                    getString(R.string.error_username_empty)
                )
            } else {
                disableErrorMessage(
                    binding.userNameLayout.editTextLayout,
                    binding.userNameLayout.editText
                )
            }
        })
        mViewModel.errorPasswordMessage.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.passwordLayout.editTextLayout,
                    binding.passwordLayout.editText,
                    getString(R.string.error_password_empty)
                )
            } else {
                disableErrorMessage(
                    binding.passwordLayout.editTextLayout,
                    binding.passwordLayout.editText
                )
            }
        })
    }

    override fun onBindData(binding: ActivityLoginBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
//        observeViewModel()


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