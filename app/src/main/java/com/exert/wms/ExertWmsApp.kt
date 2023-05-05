package com.exert.wms

import android.app.Application
import com.exert.wms.mvvmbase.modules.*
import com.exert.wms.utils.UserDefaults
import com.exert.wms.utils.UserDefaultsFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ExertWmsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        initialiseKoin()
        UserDefaultsFactory.userDefaults = UserDefaults(this)
    }

    private fun initialiseKoin() {
        startKoin {
            androidContext(this@ExertWmsApp)
            modules(
                listOf(
                    networkModule,
                    repositoryModule,
                    viewModelModule,
                    cacheModule,
                    utilsModule
                )
            )
        }
    }

    companion object {
        lateinit var instance: ExertWmsApp
    }
}