package com.exert.wms.mvvmbase

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

abstract class BaseAppCompatActivity : AppCompatActivity(){

    @StringRes
    open val title: Int? = null
    open val titleString: String? = null
    open val showHomeButton: Int? = null
}