package com.example.pollafutbolera_android.data.remote

import com.example.pollafutbolera_android.data.model.ValueRange
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SheetsApiService {
    @GET("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getValues(
        @Header("Authorization") authorization: String,
        @Path("spreadsheetId") spreadsheetId: String,
        @Path(value = "range", encoded = true) range: String
    ): ValueRange
}
