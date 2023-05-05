package com.exert.wms.login

class LoginDataSourceLocal {
    private var loginDto: LoginDto? = null
    fun saveLoginInfo(loginDto: LoginDto) {
        this.loginDto = loginDto
    }

    fun getLoginInfo(): LoginDto? = loginDto
}