package com.example.pollafutbolera_android.data.model

import com.squareup.moshi.Json

data class ValueRange(
    @Json(name = "range") val range: String = "",
    @Json(name = "majorDimension") val majorDimension: String = "",
    @Json(name = "values") val values: List<List<String>> = emptyList()
)
