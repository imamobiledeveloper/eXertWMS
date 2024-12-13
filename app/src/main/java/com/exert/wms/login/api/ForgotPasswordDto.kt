package com.exert.wms.login.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ForgotPasswordDto(
    val success: Boolean,
    val UserID: Long,
    val ErrorMessage: String,
    val UserName: String
) {
    companion object
}

@Keep
data class ForgotPasswordRequestDto(
    @SerializedName("UserID")
    val userID: Long,
    @SerializedName("Password")
    val password: String
) {
    companion object
}

@Keep
data class SuccessResponse(
    val success: Boolean,
    val ErrorMessage: String
) {
    companion object
}