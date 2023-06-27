package com.exert.wms.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.exert.wms.R
import com.exert.wms.login.api.LoginDto
import com.exert.wms.login.api.LoginRepository
import com.exert.wms.login.api.LoginRequestDto
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.utils.StringProvider
import com.exert.wms.utils.UserDefaults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userDefaults: UserDefaults,
    private val stringProvider: StringProvider,
    private val loginRepo: LoginRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    private var rememberMe: Boolean = false
    private var userName: String = ""
    private var password: String = ""

    private var coroutineJob: Job? = null

    private val _loginUserStatus = MutableLiveData<Boolean>()
    val loginUserStatus: LiveData<Boolean> = _loginUserStatus

    private val _errorLoginMessage = MutableLiveData<String>()
    val errorLoginMessage: LiveData<String> = _errorLoginMessage

    private val _errorUserNameMessage = MutableLiveData<Boolean>()
    val errorUserNameMessage: LiveData<Boolean> = _errorUserNameMessage

    private val _errorPasswordMessage = MutableLiveData<Boolean>()
    val errorPasswordMessage: LiveData<Boolean> = _errorPasswordMessage

    private val _rememberMeStatus = MutableLiveData<Boolean>()
    val rememberMeStatus: LiveData<Boolean> = _rememberMeStatus

    private val _errorUsernameEmailMessage = MutableLiveData<String>()
    val errorUsernameEmailMessage: LiveData<String> = _errorUsernameEmailMessage

    private val _errorNewPasswordMessage = MutableLiveData<String>()
    val errorNewPasswordMessage: LiveData<String> = _errorNewPasswordMessage

    private val _errorConfirmPasswordMessage = MutableLiveData<String>()
    val errorConfirmPasswordMessage: LiveData<String> = _errorConfirmPasswordMessage

    private val _savedUserName = MutableLiveData<String>()
    val savedUserName: LiveData<String> = _savedUserName

    private val _savedUserPassword = MutableLiveData<String>()
    val savedUserPassword: LiveData<String> = _savedUserPassword

    init {
        rememberMe = getRememberMeCheckBoxStatus()
        _rememberMeStatus.postValue(rememberMe)
        if (rememberMe) {
            userName = getUserName()
            password = getUserPassword()
        }
        _savedUserName.value = (userName)
        _savedUserPassword.value = (password)
    }

    private fun getUserName() = userDefaults.getUserName()
    private fun getUserPassword() = userDefaults.getUserPassword()
    fun getRememberMeCheckBoxStatus() = userDefaults.getRememberMeStatus()

    private fun loginUser(userName1: String, pwd1: String, financialPeriodId: Long) {
        userName = userName1
        password = pwd1
        val requestDto =
            LoginRequestDto(username = userName, password = password, periodID = financialPeriodId)
        coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
            loginRepo.authenticateUser(requestDto)
                .collect { response ->
                    hideProgressIndicator()
                    if (response != null) {
                        processLoginResponse(response)
                        _loginUserStatus.postValue(response.success)
                    } else {
                        _loginUserStatus.postValue(false)
                    }
                }

        }
    }

    private fun processLoginResponse(response: LoginDto) {
        userDefaults.saveUserId(response.UserId)
        userDefaults.saveUserToken(response.Token)
    }

    fun getFinancialToken(uName: String, pwd: String) {
        userName = uName
        password = pwd

        if (validateUserDetails(uName, pwd)) {
            showProgressIndicator()
            checkRememberMeStatusAndSaveDetails()
            coroutineJob = viewModelScope.launch(dispatcher + exceptionHandler) {
                loginRepo.getFinancialPeriod()
                    .collect { dto ->
                        if (dto != null && dto.CurrentPeriod > 0) {
                            userDefaults.saveFinancialPeriod(dto.CurrentPeriod)
                            loginUser(uName, pwd, dto.CurrentPeriod)
                        } else {
                            hideProgressIndicator()
                            _errorLoginMessage.postValue(
                                stringProvider.getString(
                                    R.string.error_period_id_message
                                )
                            )
                        }
                    }

            }
        }
    }

    private fun checkRememberMeStatusAndSaveDetails() {
        userDefaults.saveRememberMeStatus(rememberMe)
        if (rememberMe) {
            userDefaults.saveUserName(uname = userName)
            userDefaults.saveUserPassword(pwd = password)
        }
    }

    private fun validateUserDetails(userName: String, pwd: String): Boolean {
        return if (userName.isEmpty()) {
            _errorUserNameMessage.postValue(true)
            _errorPasswordMessage.postValue(false)
            false
        } else if (pwd.isEmpty()) {
            _errorUserNameMessage.postValue(false)
            _errorPasswordMessage.postValue(true)
            false
        } else {
            _errorUserNameMessage.value = (false)
            _errorPasswordMessage.value = (false)
            true
        }
    }

    override fun handleException(throwable: Throwable) {
        hideProgressIndicator()
        _errorLoginMessage.postValue(
            if (throwable.message?.isNotEmpty() == true) throwable.message else stringProvider.getString(
                R.string.error_login_message
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob?.cancel()
    }

    fun setUserName(uName: String) {
        userName = uName
    }

    fun setPassword(pwd: String) {
        password = pwd
    }

    fun saveRememberMeStatus(checked: Boolean) {
        rememberMe = checked
    }

    fun resetPassword(newPwd: String, confirmPwd: String) {
        if (newPwd.isEmpty()) {
            _errorNewPasswordMessage.postValue(stringProvider.getString(R.string.error_new_password_empty))
        } else if (confirmPwd.isEmpty()) {
            _errorNewPasswordMessage.postValue("")
            _errorConfirmPasswordMessage.postValue(stringProvider.getString(R.string.error_confirm_password_empty))
        } else if (newPwd != confirmPwd) {
            _errorConfirmPasswordMessage.postValue(stringProvider.getString(R.string.error_new_confirm_password_not_equal))
        }
    }

    fun checkEmailOrUsername(username: String) {
        if (username.isEmpty()) {
            _errorUsernameEmailMessage.postValue(stringProvider.getString(R.string.error_email_or_username_empty))
        } else {
            _errorUsernameEmailMessage.postValue("")
        }
    }
}