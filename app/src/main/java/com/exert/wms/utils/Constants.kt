package com.exert.wms.utils

object Constants {

    const val MANUFACTURE_DATE_FORMAT: String="dd/MM/yyyy"
    const val MANUFACTURE_DATE_API_FORMAT: String="dd MMM yyyy"
    const val MONTH: String = "MMM"
    const val SHOW_CHECKBOX: String = "SHOW_CHECKBOX"
    const val ADJUSTMENT_TYPE: String = "ADJUSTMENT_TYPE"
    const val WAREHOUSE: String = "WAREHOUSE"
    const val ITEM_ID: String = "ITEM_ID"
    const val ITEM_DTO: String = "ITEM_DTO"
    const val ITEM_WAREHOUSE_ID: String = "ITEM_WAREHOUSE_ID"
    const val USER_SELECTED_WAREHOUSE_LIST: String = "USER_SELECTED_WAREHOUSE_LIST"
    const val ITEM_PART_CODE: String = "ITEM_PART_CODE"
    const val ITEM_SERIAL_NO: String = "ITEM_SERIAL_NO"
    const val WAREHOUSE_STOCK_DETAILS: String = "WAREHOUSE_STOCK_DETAILS"
    const val CHECKED_SERIAL_ITEMS: String = "CHECKED_SERIAL_ITEMS"
    const val RECONCILIATION_CHECKED_SERIAL_ITEMS: String = "RECONCILIATION_CHECKED_SERIAL_ITEMS"
    const val STOCK_ITEMS_DETAILS_DTO: String = "STOCK_ITEMS_DETAILS_DTO"
    const val ITEM_QUANTITY: String = "ITEM_QUANTITY"
    const val READ_TIMEOUT: Long = 60
    const val CONNECTION_TIMEOUT: Long = 60

    const val ACTION_BARCODE_DATA_KEY = "DPR_DATA_INTENT_ACTION"
    const val ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA"

    /**
     * Honeywell DataCollection Intent API
     * Claim scanner
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    const val ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER"

    /**
     * Honeywell DataCollection Intent API
     * Release scanner claim
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    const val ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER"

    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the scanner to claim. If scanner is not available or if extra is not used,
     * DataCollection will choose an available scanner.
     * Values : String
     * "dcs.scanner.imager" : Uses the internal scanner
     * "dcs.scanner.ring" : Uses the external ring scanner
     */
    const val EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER"
    const val EXTRA_SCANNER_VALUE = "dcs.scanner.imager"

    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the profile to use. If profile is not available or if extra is not used,
     * the scanner will use factory default properties (not "DEFAULT" profile properties).
     * Values : String
     */
    const val EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE"
    const val EXTRA_PROFILE_VALUE = "MyProfile1"

    /**
     * Honeywell DataCollection Intent API
     * Optional. Overrides the profile properties (non-persistent) until the next scanner claim.
     * Values : Bundle
     */
    const val EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES"

    const val EXTRA_CONTROL = "com.honeywell.aidc.action.ACTION_CONTROL_SCANNER"

    const val EXTRA_SCAN = "com.honeywell.aidc.extra.EXTRA_SCAN"
    const val EXTRA_SCANNER_APP_PACKAGE = "com.intermec.datacollectionservice"
    const val DPR_DATA_INTENT_KEY = "DPR_DATA_INTENT"
    const val DATA_KEY = "data"
    const val VERSION_KEY = "version"
}