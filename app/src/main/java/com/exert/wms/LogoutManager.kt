package com.exert.wms

import com.exert.wms.mvvmbase.network.SessionManager

class LogoutManager(private val sessionManager: SessionManager) {

    fun logout(){
//        AuthenticationRepository.logout()
        sessionManager.invalidateSession()
    }

    fun sessionExpired(){
        SessionExpirationObject.sessionExpired()
        logout()
    }
}