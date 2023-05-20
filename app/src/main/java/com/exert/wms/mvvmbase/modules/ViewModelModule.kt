package com.exert.wms.mvvmbase.modules

import com.exert.wms.home.HomeViewModel
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.login.LoginViewModel
import com.exert.wms.splash.SplashViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val viewModelModule = module {

    viewModel {
        SplashViewModel()
    }

    viewModel {
        LoginViewModel(get(), get(), get())
    }

    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        ItemStocksViewModel(get(),get())
    }

}