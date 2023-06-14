package com.exert.wms.itemStocks.api

import kotlinx.coroutines.flow.Flow

class ItemStocksRepository(private val itemStocksDataSource: ItemStocksDataSource) {

    fun getItemWarehouseList(requestDto: ItemStocksRequestDto): Flow<ItemStocksResponseDto> {
        return itemStocksDataSource.getItemWarehouseList(requestDto)
    }

    fun getWarehouseSerialNosList(requestDto: WarehouseSerialItemsRequestDto): Flow<ItemStocksResponseDto> {
        return itemStocksDataSource.getWarehouseSerialNosList(requestDto)
    }
}