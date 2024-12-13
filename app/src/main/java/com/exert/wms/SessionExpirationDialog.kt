package com.exert.wms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.exert.wms.databinding.FragmentSessionExpirationDialogBinding
import com.exert.wms.login.LoginActivity
import com.exert.wms.utils.UserDefaults
import org.koin.android.ext.android.inject

class SessionExpirationDialog : AppCompatDialogFragment() {

    private lateinit var binding: FragmentSessionExpirationDialogBinding
    private val userDefaults: UserDefaults by inject()
    private var errorMsg:String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        errorMsg = getString(R.string.session_expired)
        binding = FragmentSessionExpirationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDefaults.getUserLoginError().takeIf { it.isNotEmpty() }?.let {
            errorMsg = getString(R.string.login_error)
            binding.expireMessageTV.text = getString(R.string.login_error_message)
        } ?:{
            errorMsg = getString(R.string.session_expired)
            binding.expireMessageTV.text = getString(R.string.session_expired_message)
        }
        binding.titleTV.text = errorMsg
        binding.okButton.setOnClickListener {
            if(errorMsg == getString(R.string.session_expired)) {
                LoginActivity.relaunch(requireActivity())
            }
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false
    }

    companion object {
        fun newInstance(): SessionExpirationDialog {
            return SessionExpirationDialog()
        }
    }
}