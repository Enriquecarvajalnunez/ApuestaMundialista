package com.example.pollafutbolera_android.data.remote

import com.example.pollafutbolera_android.data.model.BatchValueRange
import com.example.pollafutbolera_android.data.model.SpreadsheetMetadata
import com.example.pollafutbolera_android.data.model.ValueRange
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SheetsApiService {

    @GET("v4/spreadsheets/{spreadsheetId}")
    suspend fun getSpreadsheetMetadata(
        @Header("Authorization") authorization: String,
        @Path("spreadsheetId") spreadsheetId: String
    ): SpreadsheetMetadata

    @GET("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getValues(
        @Header("Authorization") authorization: String,
        @Path("spreadsheetId") spreadsheetId: String,
        @Path(value = "range", encoded = true) range: String
    ): ValueRange

    @GET("v4/spreadsheets/{spreadsheetId}/values:batchGet")
    suspend fun batchGetValues(
        @Header("Authorization") authorization: String,
        @Path("spreadsheetId") spreadsheetId: String,
        @Query("ranges") ranges: List<String>
    ): BatchValueRange

    @PUT("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun updateValues(
        @Header("Authorization") authorization: String,
        @Path("spreadsheetId") spreadsheetId: String,
        @Path(value = "range", encoded = true) range: String,
        @Query("valueInputOption") valueInputOption: String,
        @Body body: ValueRange
    ): ValueRange
}
