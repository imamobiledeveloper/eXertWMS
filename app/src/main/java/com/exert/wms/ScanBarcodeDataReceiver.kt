package com.exert.wms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.exert.wms.utils.Constants.ACTION_BARCODE_DATA
import com.exert.wms.utils.Constants.DATA_KEY
import com.exert.wms.utils.Constants.VERSION_KEY

class ScanBarcodeDataReceiver(
    private val listener: ScanBarcodeBroadcastListener
) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_BARCODE_DATA) {
            val version = intent.getIntExtra(VERSION_KEY, 0)
            if (version >= 1) {
                val data = intent.getStringExtra(DATA_KEY)
                listener.onDataReceived(data)
            }
        }
    }
}

interface ScanBarcodeBroadcastListener {
    fun onDataReceived(data: String?)
}