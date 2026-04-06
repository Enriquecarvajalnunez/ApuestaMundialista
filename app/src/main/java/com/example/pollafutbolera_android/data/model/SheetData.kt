package com.example.pollafutbolera_android.data.model

import com.squareup.moshi.Json

data class ValueRange(
    @Json(name = "range") val range: String = "",
    @Json(name = "majorDimension") val majorDimension: String = "",
    @Json(name = "values") val values: List<List<String>> = emptyList()
)

data class SpreadsheetMetadata(
    @Json(name = "sheets") val sheets: List<SheetMetadata> = emptyList()
)

data class SheetMetadata(
    @Json(name = "properties") val properties: SheetProperties = SheetProperties()
)

data class SheetProperties(
    @Json(name = "title") val title: String = "",
    @Json(name = "index") val index: Int = 0
)

data class BatchValueRange(
    @Json(name = "valueRanges") val valueRanges: List<ValueRange> = emptyList()
)
