package com.exert.wms.alertDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.exert.wms.databinding.DialogAlertLayoutBinding

class AlertDialogWithCallBack(
    private val dialogDto: AlertDialogDto,
    private val onPositiveButtonCallBack: () -> Unit,
    private val onNegativeButtonCallBack: () -> Unit,
) : DialogFragment() {

    private lateinit var binding: DialogAlertLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAlertLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.alertDto = dialogDto

        binding.closeButton.setOnClickListener {
            dismiss()
        }
        binding.positiveButton.setOnClickListener {
            onPositiveButtonCallBack.invoke()
            dismiss()
        }
        binding.negativeButton.setOnClickListener {
            onNegativeButtonCallBack.invoke()
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            dialogDto: AlertDialogDto,
            onPositiveButtonCallBack: () -> Unit = {},
            onNegativeButtonCallBack: () -> Unit = {}
        ) = AlertDialogWithCallBack(dialogDto, onPositiveButtonCallBack, onNegativeButtonCallBack)
    }
}