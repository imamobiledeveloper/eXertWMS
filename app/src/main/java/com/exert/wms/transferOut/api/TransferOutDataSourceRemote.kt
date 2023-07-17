package com.exert.wms.transferOut.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class TransferOutDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveTransferOutItems(requestDto: TransferOutRequestDto): SaveTransferOutResponse {
        return exertWmsApi.saveTransferOutItems(requestDto)
    }
}