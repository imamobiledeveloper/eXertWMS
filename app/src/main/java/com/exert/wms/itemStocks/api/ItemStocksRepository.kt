package com.exert.wms.itemStocks.api

import kotlinx.coroutines.flow.Flow

class ItemStocksRepository(private val itemStocksDataSource: ItemStocksDataSource) {

    fun getOnlineSalesItems(requestDto: ItemStocksRequestDto): Flow<ItemStocksResponseDto> {
        return itemStocksDataSource.getOnlineSalesItems(requestDto)
    }

    fun getWarehouseSerialNosList(requestDto: WarehouseSerialItemsRequestDto): Flow<ItemStocksResponseDto> {
        return itemStocksDataSource.getWarehouseSerialNosList(requestDto)
    }
}