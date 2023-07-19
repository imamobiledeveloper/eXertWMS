package com.exert.wms.delivery.api

import com.exert.wms.mvvmbase.network.ExertWmsApi
import com.exert.wms.returns.api.DeliveryNoteItemsRequestDto
import com.exert.wms.returns.api.DeliveryReceiptItemsRequestDto
import com.exert.wms.returns.api.SaveDeliveryNoteItemsResponse
import com.exert.wms.returns.api.SaveDeliveryReceiptItemsResponse

class DeliveryDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): SaveDeliveryReceiptItemsResponse {
        return exertWmsApi.saveDeliveryReceiptItems(requestDto)
    }

    suspend fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsRequestDto): SaveDeliveryNoteItemsResponse {
        return exertWmsApi.saveDeliveryNoteItems(requestDto)
    }
}