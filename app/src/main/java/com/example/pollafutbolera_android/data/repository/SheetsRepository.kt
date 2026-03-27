package com.example.pollafutbolera_android.data.repository

import android.content.Context
import com.example.pollafutbolera_android.data.model.ValueRange
import com.example.pollafutbolera_android.data.remote.SheetsApiClient
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SheetsRepository(private val context: Context) {

    companion object {
        private const val SPREADSHEET_ID = "1WB0W5mQkT2uIKL62fuwP4-8iUgMzAE9oQSBcixxc1vw"
        private const val RANGE = "Hoja%201!A1:B2"
        private const val SCOPE = "oauth2:https://www.googleapis.com/auth/spreadsheets.readonly"
    }

    suspend fun fetchSheetData(): ValueRange = withContext(Dispatchers.IO) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
            ?: error("User not signed in")
        val token = GoogleAuthUtil.getToken(context, account.account!!, SCOPE)
        SheetsApiClient.service.getValues(
            authorization = "Bearer $token",
            spreadsheetId = SPREADSHEET_ID,
            range = RANGE
        )
    }
}
