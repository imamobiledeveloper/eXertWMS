package com.exert.wms.transfer.api

import kotlinx.coroutines.flow.Flow

class TransferRepository(private val transferDataSource: TransferDataSource) {

    fun saveTransferOutItems(requestDto: TransferOutRequestDto): Flow<SaveTransferOutResponse> {
        return transferDataSource.saveTransferOutItems(requestDto)
    }

    fun saveTransferInItems(requestDto: SaveTransferInRequestDto): Flow<SaveTransferInResponse> {
        return transferDataSource.saveTransferInItems(requestDto)
    }

    fun getTransferOutNumbers(requestDto: TransferOutNumbersRequestDto): Flow<TransferOutNumbersResponseDto> {
        return transferDataSource.getTransferOutNumbers(requestDto)
    }

    fun getTransferInItemsList(requestDto: TransferOutItemsRequestDto): Flow<TransferInItemsResponseDto> {
        return transferDataSource.getTransferInItemsList(requestDto)
    }
}