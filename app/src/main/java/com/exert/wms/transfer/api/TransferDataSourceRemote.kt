package com.exert.wms.transfer.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class TransferDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveTransferOutItems(requestDto: TransferOutRequestDto): SaveTransferOutResponse {
        return exertWmsApi.saveTransferOutItems(requestDto)
    }

    suspend fun saveTransferInItems(requestDto: TransferInRequestDto): SaveTransferInResponse {
        return exertWmsApi.saveTransferInItems(requestDto)
    }
}