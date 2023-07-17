package com.exert.wms.transferOut.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransferOutDataSource(
    private val transferOutDataSourceRemote: TransferOutDataSourceRemote
) {
    fun saveTransferOutItems(requestDto: TransferOutRequestDto): Flow<SaveTransferOutResponse> {
        return flow {
            emit(
                transferOutDataSourceRemote.saveTransferOutItems(requestDto)
            )
        }
    }

}