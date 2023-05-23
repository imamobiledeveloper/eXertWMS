package com.exert.wms.itemStocks.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ItemStocksDataSource(
    private val itemStocksDataSourceRemote: ItemStocksDataSourceRemote,
    private val itemStocksDataSourceLocal: ItemStocksDataSourceLocal
) {
    fun getOnlineSalesItems(requestDto: ItemStocksRequestDto): Flow<ItemStocksResponseDto> {
        return flow {
            emit(
                itemStocksDataSourceLocal.getItemStocksInfo() ?:
                itemStocksDataSourceRemote.getOnlineSalesItems(requestDto)
            )
        }
    }

    fun getWarehouseSerialNosList(requestDto: WarehouseSerialItemsRequestDto): Flow<ItemStocksResponseDto> {
        return flow {
            emit(
                itemStocksDataSourceLocal.getWarehouseSerialItemDetails() ?:
                itemStocksDataSourceRemote.getWarehouseSerialItemDetails(requestDto)
            )
        }
    }
}