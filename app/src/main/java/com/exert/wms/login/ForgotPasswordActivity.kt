package com.exert.wms.login

import android.graphics.Color
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
    }

    private fun observeViewModel() {
        setTextClickable()

        binding.usernameEmailLayout.show()
        binding.createNewPasswordLayout.hide()

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
        ss.setSpan(clickableSpan, 20, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.rememberMePasswordTV.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.BLUE
        }
    }
}