package com.example.pollafutbolera_android.data.repository

import com.example.pollafutbolera_android.data.model.BetEntry
import com.example.pollafutbolera_android.data.model.BetRequest
import com.example.pollafutbolera_android.data.model.BetResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor

class BetSubmitRepository {

    companion object {
        // Reemplaza con la URL de tu Apps Script Web App desplegada
        const val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbz_mtswqX3QmmCwDghUd8FXPRIeIpX9z5AuiWQbIAn4t57pbAiFvMlfmbqnLeKITNtx/exec"
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // followRedirects=false para manejar manualmente el redirect 302 de Apps Script
    private val client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val requestAdapter = moshi.adapter(BetRequest::class.java)
    private val responseAdapter = moshi.adapter(BetResponse::class.java)

    suspend fun submitBets(
        nombre: String,
        identificacion: String,
        bets: List<BetEntry>
    ): BetResponse = withContext(Dispatchers.IO) {
        for (bet in bets) {
            val request = BetRequest(
                nombre = nombre,
                identificacion = identificacion,
                grupo = bet.grupo,
                equipoA = bet.equipoA,
                equipoB = bet.equipoB,
                marcadorA = bet.marcadorA,
                marcadorB = bet.marcadorB
            )
            val result = postToScript(request)
            if (!result.success) return@withContext result
        }
        BetResponse(success = true)
    }

    private fun postToScript(request: BetRequest): BetResponse {
        val json = requestAdapter.toJson(request)
        val body = json.toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder()
            .url(SCRIPT_URL)
            .post(body)
            .build()

        // Apps Script retorna 302 en POST — el redirect se sigue como GET
        var response = client.newCall(httpRequest).execute()
        if (response.code in 300..399) {
            val location = response.header("Location")
                ?: return BetResponse(success = false, error = "Redirect sin Location header")
            response.close()
            val redirectRequest = Request.Builder().url(location).get().build()
            response = client.newCall(redirectRequest).execute()
        }

        val responseBody = response.body?.string()
            ?: return BetResponse(success = false, error = "Respuesta vacía del servidor")

        // Captura el body crudo si no es JSON válido para facilitar diagnóstico
        return try {
            responseAdapter.fromJson(responseBody)
                ?: BetResponse(success = false, error = "Respuesta con formato inválido")
        } catch (e: Exception) {
            BetResponse(success = false, error = "Respuesta inesperada: ${responseBody.take(200)}")
        }
    }
}
