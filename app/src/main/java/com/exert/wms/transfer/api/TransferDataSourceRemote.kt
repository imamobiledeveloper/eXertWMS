package com.exert.wms.transfer.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class TransferDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveTransferOutItems(requestDto: TransferOutRequestDto): SaveTransferOutResponse {
        return exertWmsApi.saveTransferOutItems(requestDto)
    }

    suspend fun saveTransferInItems(requestDto: SaveTransferInRequestDto): SaveTransferInResponse {
        return exertWmsApi.saveTransferInItems(requestDto)
    }

    suspend fun getTransferOutNumbers(requestDto: TransferOutNumbersRequestDto): TransferOutNumbersResponseDto {
        return exertWmsApi.getTransferOutNumbers(ToWarehouseID = requestDto.ToWarehouseID)//requestDto)
    }

    suspend fun getTransferOutItemsList(requestDto: TransferOutItemsRequestDto): TransferInItemsResponseDto {
        return exertWmsApi.getTransferOutItemsList(ExternalTransferID = requestDto.ExternalTransferID)
    }
}