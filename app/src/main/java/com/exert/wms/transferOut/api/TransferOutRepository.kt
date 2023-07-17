package com.exert.wms.transferOut.api

import kotlinx.coroutines.flow.Flow

class TransferOutRepository(private val transferOutDataSource: TransferOutDataSource) {

    fun saveTransferOutItems(requestDto: TransferOutRequestDto): Flow<SaveTransferOutResponse> {
        return transferOutDataSource.saveTransferOutItems(requestDto)
    }
}