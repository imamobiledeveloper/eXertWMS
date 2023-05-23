package com.exert.wms.itemStocks.api

class ItemStocksDataSourceLocal {
    private var itemStocksResponseDto:ItemStocksResponseDto?=null
    private var warehouseItemStocksResponseDto:ItemStocksResponseDto?=null

    fun getItemStocksInfo(): ItemStocksResponseDto? {
        return itemStocksResponseDto
    }

    fun saveItemStocksInfo(itemStocksResponseDto:ItemStocksResponseDto) {
        this. itemStocksResponseDto= itemStocksResponseDto
    }
    fun saveWarehouseSerialItemDetails(warehouseItemStocksResponseDto:ItemStocksResponseDto) {
        this. warehouseItemStocksResponseDto= warehouseItemStocksResponseDto
    }

    fun getWarehouseSerialItemDetails(): ItemStocksResponseDto? {
        return warehouseItemStocksResponseDto
    }


    fun clear(){
        itemStocksResponseDto= null
        warehouseItemStocksResponseDto= null
    }
}