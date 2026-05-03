package com.example.pollafutbolera_android.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class ClockRepository {

    private data class ClockResponse(
        val success: Boolean,
        val abierto: Boolean? = null,
        val fechalimite: String? = null,
        val error: String? = null
    )

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val responseAdapter = moshi.adapter(ClockResponse::class.java)

    /**
     * Consulta la hoja "Reloj" del sheet para verificar si la fecha límite
     * para registrar apuestas ya pasó.
     *
     * @return true si la fecha/hora actual es menor que la fecha límite (se puede registrar)
     * @throws Exception si hay error de red o el servidor reporta error
     */
    suspend fun verificarReloj(): Boolean = withContext(Dispatchers.IO) {
        val url = "${BetSubmitRepository.SCRIPT_URL}?accion=verificarReloj"

        var request = Request.Builder().url(url).get().build()
        var response = client.newCall(request).execute()

        // Apps Script redirige GET con 302
        if (response.code in 300..399) {
            val location = response.header("Location")
                ?: error("Redirect sin Location header")
            response.close()
            request = Request.Builder().url(location).get().build()
            response = client.newCall(request).execute()
        }

        val rawBody = response.body?.string()
            ?: error("Respuesta vacía del servidor")

        val parsed = try {
            responseAdapter.fromJson(rawBody)
        } catch (e: Exception) {
            error("Respuesta no es JSON válido. Código HTTP: ${response.code}. Body: ${rawBody.take(500)}")
        } ?: error("Formato de respuesta inválido")

        if (!parsed.success) error(parsed.error ?: "Error desconocido del servidor")

        parsed.abierto ?: false
    }
}