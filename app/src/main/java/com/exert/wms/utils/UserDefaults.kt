package com.exert.wms.utils

import android.content.Context
import android.content.SharedPreferences

class UserDefaults(context: Context) {
    companion object {
        private const val KEY_USER_ID = "USERID"
        private const val KEY_USER_TOKEN = "USERTOKEN"
        private const val KEY_FINANCIAL_PERIOD = "KEY_FINANCIAL_PERIOD"
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