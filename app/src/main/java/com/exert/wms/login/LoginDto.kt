package com.exert.wms.login

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginDto(
    val success:Boolean,
    val UserId:Long,
    val Token:String
){
    companion object
}

@Keep
data class LoginRequestDto(
    @SerializedName("UserName")
    val username:String,
    @SerializedName("Password")
    val password:String,
    @SerializedName("PeriodID")
    val periodID:Long
){
    companion object
}