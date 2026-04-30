package com.example.pollafutbolera_android.data.repository
import com.example.pollafutbolera_android.data.model.BetEntry
import com.example.pollafutbolera_android.data.model.BetResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class BetSubmitRepository {
    companion object {
        const val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbz_mtswqX3QmmCwDghUd8FXPRIeIpX9z5AuiWQbIAn4t57pbAiFvMlfmbqnLeKITNtx/exec"
    }
    private data class BetBatchRequest(
        val nombre: String,
        val identificacion: String,
        val bets: List<BetEntry>
    )
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    private val requestAdapter = moshi.adapter(BetBatchRequest::class.java)
    private val responseAdapter = moshi.adapter(BetResponse::class.java)
    suspend fun submitBets(
        nombre: String,
        identificacion: String,
        bets: List<BetEntry>
    ): BetResponse = withContext(Dispatchers.IO) {
        val batchRequest = BetBatchRequest(
            nombre = nombre,
            identificacion = identificacion,
            bets = bets
        )
        postToScript(batchRequest)
    }
    private fun postToScript(request: BetBatchRequest): BetResponse {
        val json = requestAdapter.toJson(request)
        val body = json.toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder()
            .url(SCRIPT_URL)
            .post(body)
            .build()
        val response = client.newCall(httpRequest).execute()
        val responseBody = response.body?.string()
            ?: return BetResponse(success = false, error = "Respuesta vacía del servidor")
        return try {
            responseAdapter.fromJson(responseBody)
                ?: BetResponse(success = false, error = "Formato de respuesta inválido")
        } catch (e: Exception) {
            BetResponse(success = false, error = "Respuesta inesperada: ${responseBody.take(200)}")
        }
    }
}
