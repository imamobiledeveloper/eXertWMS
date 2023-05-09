package com.exert.wms.itemStocks.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ItemStocksDataSource(
    private val itemStocksDataSourceRemote: ItemStocksDataSourceRemote
) {
    fun getOnlineSalesItems(requestDto: ItemStocksRequestDto): Flow<ItemStocksResponseDto> {
        return flow {
            emit(
                itemStocksDataSourceRemote.getOnlineSalesItems(requestDto)
            )
        }
    }
}