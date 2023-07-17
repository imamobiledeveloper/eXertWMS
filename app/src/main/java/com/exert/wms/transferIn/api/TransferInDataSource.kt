package com.exert.wms.transferIn.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransferInDataSource(
    private val transferInDataSourceRemote: TransferInDataSourceRemote
) {
    fun saveTransferInItems(requestDto: TransferInRequestDto): Flow<SaveTransferInResponse> {
        return flow {
            emit(
                transferInDataSourceRemote.saveTransferInItems(requestDto)
            )
        }
    }

}