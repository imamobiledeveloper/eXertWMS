package com.exert.wms.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityLoginBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.DoneOnEditorActionListener
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
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

        binding.rememberPasswordCheckBox.setOnCheckedChangeListener { _, checked ->
            mViewModel.saveRememberMeStatus(checked)
        }
        binding.rememberForgotPwdLayout.setOnClickListener {
            startActivity<ForgotPasswordActivity>()
        }
        binding.passwordEditText.setOnEditorActionListener(DoneOnEditorActionListener())

        binding.usernameEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextViewText(binding.usernameHintTV, "", View.GONE)
            } else {
                if (binding.usernameEditText.text.isNullOrEmpty()) {
                    setTextViewText(
                        binding.usernameHintTV,
                        getString(R.string.enter_password),
                        View.VISIBLE
                    )
                } else {
                    setTextViewText(binding.usernameHintTV, "", View.GONE)
                }
            }
        }
        binding.passwordEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextViewText(binding.passwordHintTV, "", View.GONE)
            } else {
                if (binding.passwordEditText.text.isNullOrEmpty()) {
                    setTextViewText(
                        binding.passwordHintTV,
                        getString(R.string.enter_password),
                        View.VISIBLE
                    )
                } else {
                    setTextViewText(binding.passwordHintTV, "", View.GONE)
                }
            }
        }
    }

    private fun setTextWatchers() {
        binding.usernameEditText.nextFocusForwardId = binding.passwordEditText.id
        binding.usernameEditText.doOnTextChanged { text, _, _, _ ->
            mViewModel.setUserName(text.toString().trim())
        }
        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            mViewModel.setPassword(text.toString().trim())
        }
    }

    private fun observeViewModel() {
        mViewModel.isLoadingData.observe(this, Observer { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        })

        mViewModel.loginUserStatus.observe(this, Observer {
            if (it) {
                if (!mViewModel.getRememberMeCheckBoxStatus()) {
                    clearFieldsData()
                }
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
        mViewModel.savedUserName.observe(this, Observer {
            binding.usernameEditText.text = it.toEditable()
            setTextViewText(binding.usernameHintTV, "", View.VISIBLE)
        })
        mViewModel.savedUserPassword.observe(this, Observer {
            binding.passwordEditText.text = it.toEditable()
            setTextViewText(binding.passwordHintTV, "", View.VISIBLE)
        })
        mViewModel.rememberMeStatus.observe(this, Observer {
            binding.rememberPasswordCheckBox.isChecked = it
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

    companion object {
        fun relaunch(activity: Activity) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(intent)
            activity.finishAffinity()

        }
    }
}