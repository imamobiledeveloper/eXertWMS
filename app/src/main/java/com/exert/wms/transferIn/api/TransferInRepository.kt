package com.exert.wms.transferIn.api

import kotlinx.coroutines.flow.Flow

class TransferInRepository(private val stockAdjustmentDataSource: TransferInDataSource) {

    fun saveTransferInItems(requestDto: TransferInRequestDto): Flow<SaveTransferInResponse> {
        return stockAdjustmentDataSource.saveTransferInItems(requestDto)
    }
}