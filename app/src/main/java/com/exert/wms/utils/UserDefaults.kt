package com.exert.wms.utils

import android.content.Context
import android.content.SharedPreferences

class UserDefaults(context: Context) {
    companion object {
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_USER_TOKEN = "USER_TOKEN"
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_USER_PASSWORD = "KEY_USER_PASSWORD"
        private const val KEY_FINANCIAL_PERIOD = "KEY_FINANCIAL_PERIOD"
        private const val KEY_LOGIN_REMEMBER_ME = "KEY_LOGIN_REMEMBER_ME"
    }

    private val preferences =
        context.getSharedPreferences(UserDefaults::class.simpleName, Context.MODE_PRIVATE)
    private val preferencesEditor: SharedPreferences.Editor
        get() {
            return preferences.edit()
        }

    internal fun saveUserId(id: Long) =
        preferencesEditor.putLong(KEY_USER_ID, id).commit()

    internal fun saveUserToken(token: String) =
        preferencesEditor.putString(KEY_USER_TOKEN, token).commit()

    internal fun saveFinancialPeriod(periodId: Long) =
        preferencesEditor.putLong(KEY_FINANCIAL_PERIOD, periodId).commit()

    internal fun saveUserName(uname: String) =
        preferencesEditor.putString(KEY_USER_NAME, uname).commit()

    internal fun saveUserPassword(pwd: String) =
        preferencesEditor.putString(KEY_USER_PASSWORD, pwd).commit()

    internal fun saveRememberMeStatus(status: Boolean) =
        preferencesEditor.putBoolean(KEY_LOGIN_REMEMBER_ME, status).commit()
    internal fun getUserName() = preferences.getString(KEY_USER_NAME, "") ?: ""

    internal fun getUserPassword() = preferences.getString(KEY_USER_PASSWORD, "") ?: ""
    internal fun getRememberMeStatus() = preferences.getBoolean(KEY_LOGIN_REMEMBER_ME, false)

    internal fun getUserToken() = preferences.getString(KEY_USER_TOKEN, "") ?: ""

    internal fun getUserId() = preferences.getLong(KEY_USER_ID, 0) ?: ""

    internal fun getFinancialPeriod() = preferences.getLong(KEY_FINANCIAL_PERIOD, 0) ?: ""

    fun clear() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}

object UserDefaultsFactory {
    lateinit var userDefaults: UserDefaults
}