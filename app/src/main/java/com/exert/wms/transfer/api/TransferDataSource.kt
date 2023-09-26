package com.exert.wms.transfer.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransferDataSource(
    private val transferDataSourceRemote: TransferDataSourceRemote
) {
    fun saveTransferOutItems(requestDto: TransferOutRequestDto): Flow<SaveTransferOutResponse> {
        return flow {
            emit(
                transferDataSourceRemote.saveTransferOutItems(requestDto)
            )
        }
    }

    fun saveTransferInItems(requestDto: SaveTransferInRequestDto): Flow<SaveTransferInResponse> {
        return flow {
            emit(
                transferDataSourceRemote.saveTransferInItems(requestDto)
            )
        }
    }

    fun getTransferOutNumbers(requestDto: TransferOutNumbersRequestDto): Flow<TransferOutNumbersResponseDto> {
        return flow {
            emit(
                transferDataSourceRemote.getTransferOutNumbers(requestDto)
            )
        }
    }

    fun getTransferInItemsList(requestDto: TransferOutItemsRequestDto): Flow<TransferInItemsResponseDto> {
        return flow {
            emit(
                transferDataSourceRemote.getTransferInItemsList(requestDto)
            )
        }
    }

}