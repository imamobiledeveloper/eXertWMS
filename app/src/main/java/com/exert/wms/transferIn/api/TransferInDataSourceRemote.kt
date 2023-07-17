package com.exert.wms.transferIn.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class TransferInDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveTransferInItems(requestDto: TransferInRequestDto): SaveTransferInResponse {
        return exertWmsApi.saveTransferInItems(requestDto)
    }
}