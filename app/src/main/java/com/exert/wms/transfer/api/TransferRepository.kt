package com.exert.wms.transfer.api

import kotlinx.coroutines.flow.Flow

class TransferRepository(private val transferDataSource: TransferDataSource) {

    fun saveTransferOutItems(requestDto: TransferOutRequestDto): Flow<SaveTransferOutResponse> {
        return transferDataSource.saveTransferOutItems(requestDto)
    }

    fun saveTransferInItems(requestDto: TransferInRequestDto): Flow<SaveTransferInResponse> {
        return transferDataSource.saveTransferInItems(requestDto)
    }
}