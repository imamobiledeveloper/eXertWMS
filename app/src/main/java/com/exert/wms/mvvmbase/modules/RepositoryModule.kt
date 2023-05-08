package com.exert.wms.mvvmbase.modules

import com.exert.wms.login.LoginRepository
import org.koin.dsl.module

val repositoryModule = module(override = true) {

    single {
        LoginRepository(loginDatasource = get())
    }
}