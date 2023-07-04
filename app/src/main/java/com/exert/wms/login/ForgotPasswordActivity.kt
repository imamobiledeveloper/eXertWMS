package com.exert.wms.login

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityForgotPasswordBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ForgotPasswordActivity :
    BaseActivity<LoginViewModel, ActivityForgotPasswordBinding>() {

    override fun getLayoutID(): Int = R.layout.activity_forgot_password

    override val showHomeButton: Int = -1

    override val mViewModel by lazy {
        getViewModel<LoginViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideToolBar()
        observeViewModel()

        binding.forgotPasswordButton.setOnClickListener {
            hideKeyBoard()
            mViewModel.checkEmailOrUsername(
                binding.emailEditText.text.toString()
            )
        }

        binding.backIV.setOnClickListener {
            finish()
        }
        binding.resetPasswordButton.setOnClickListener {
            mViewModel.resetPassword(
                binding.newPasswordEditText.text.toString(),
                binding.confirmPasswordEditText.text.toString()
            )
        }

        binding.emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextViewText(binding.emailHintTV, "", View.GONE)
            } else {
                if (binding.emailEditText.text.isNullOrEmpty()) {
                    setTextViewText(
                        binding.emailHintTV,
                        getString(R.string.enter_username_or_email),
                        View.VISIBLE
                    )
                } else {
                    setTextViewText(binding.emailHintTV, "", View.GONE)
                }
            }
        }

        binding.newPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextViewText(binding.newPasswordHintTV, "", View.GONE)
            } else {
                if (binding.newPasswordEditText.text.isNullOrEmpty()) {
                    setTextViewText(
                        binding.newPasswordHintTV,
                        getString(R.string.new_password),
                        View.VISIBLE
                    )
                } else {
                    setTextViewText(binding.newPasswordHintTV, "", View.GONE)
                }
            }
        }
        binding.confirmPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setTextViewText(binding.confirmPasswordHintTV, "", View.GONE)
            } else {
                if (binding.confirmPasswordEditText.text.isNullOrEmpty()) {
                    setTextViewText(
                        binding.confirmPasswordHintTV,
                        getString(R.string.confirm_password),
                        View.VISIBLE
                    )
                } else {
                    setTextViewText(binding.confirmPasswordHintTV, "", View.GONE)
                }
            }
        }
    }

    private fun observeViewModel() {
        setTextClickable()

        binding.usernameEmailLayout.show()
        binding.createNewPasswordLayout.hide()

//        binding.usernameEmailLayout.hide()
//        binding.createNewPasswordLayout.show()

        mViewModel.errorNewPasswordMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                enableErrorMessage(
                    binding.newPasswordEditTextLayout,
                    binding.newPasswordEditText,
                    msg
                )
            } else {
                disableErrorMessage(
                    binding.newPasswordEditTextLayout,
                    binding.newPasswordEditText,
                )
            }
        }

        mViewModel.errorConfirmPasswordMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                enableErrorMessage(
                    binding.confirmPasswordEditTextLayout,
                    binding.confirmPasswordEditText,
                    msg
                )
            } else {
                disableErrorMessage(
                    binding.confirmPasswordEditTextLayout,
                    binding.confirmPasswordEditText,
                )
            }
        }
        mViewModel.errorUsernameEmailMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                enableErrorMessage(
                    binding.emailEditTextLayout,
                    binding.emailEditText,
                    msg
                )
            } else {
                disableErrorMessage(
                    binding.emailEditTextLayout,
                    binding.emailEditText,
                )
            }
        }

    }

    override fun onBindData(binding: ActivityForgotPasswordBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

    private fun setTextClickable() {
        val ss = SpannableString(getString(R.string.remember_password))

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }

            override fun onClick(view: View) {
                finish()
            }
        }
        ss.setSpan(clickableSpan, 19, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.rememberMePasswordTV.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}