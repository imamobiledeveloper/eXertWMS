package com.exert.wms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.exert.wms.databinding.FragmentSessionExpirationDialogBinding
import com.exert.wms.login.LoginActivity

class SessionExpirationDialog : AppCompatDialogFragment() {

    private lateinit var binding: FragmentSessionExpirationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionExpirationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.okButton.setOnClickListener {
            LoginActivity.relaunch(requireActivity())
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