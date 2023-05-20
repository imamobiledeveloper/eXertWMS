package com.exert.wms.splash

import android.content.Intent
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivitySplashBinding
import com.exert.wms.login.LoginActivity
import com.exert.wms.mvvmbase.BaseActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*


class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    override fun getLayoutID(): Int = R.layout.activity_splash

    override val showHomeButton: Int = -1

    override val mViewModel by lazy {
        getViewModel<SplashViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideToolBar()
        launchLoginScreen()
    }

    private fun launchLoginScreen() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }, 2000)
    }

    override fun onBindData(binding: ActivitySplashBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}