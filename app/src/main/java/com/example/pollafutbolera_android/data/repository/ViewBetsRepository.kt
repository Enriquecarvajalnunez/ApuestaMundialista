package com.example.pollafutbolera_android.data.repository

import com.example.pollafutbolera_android.data.model.BetResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class ViewBetsRepository {

    private data class ScriptBetResult(
        val grupo: String,
        val equipoA: String,
        val equipoB: String,
        val marcadorA: String,
        val marcadorB: String
    )

    private data class ScriptResponse(
        val success: Boolean,
        val nombre: String? = null,
        val bets: List<ScriptBetResult>? = null,
        val error: String? = null
    )

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // followRedirects=false igual que BetSubmitRepository: Apps Script redirige GET también
    private val client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val responseAdapter = moshi.adapter(ScriptResponse::class.java)

    suspend fun fetchBetsForPlayer(identificacion: String): Pair<String, List<BetResult>> =
        withContext(Dispatchers.IO) {
            val url = "${BetSubmitRepository.SCRIPT_URL}?accion=verApuestas&cedula=${identificacion.trim()}"

            var request = Request.Builder().url(url).get().build()
            var response = client.newCall(request).execute()

            // Apps Script puede devolver 302 en GET igual que en POST
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
                // Muestra los primeros 300 chars del body para diagnóstico
                error("Respuesta no es JSON válido: ${rawBody.take(300)}")
            } ?: error("Formato de respuesta inválido")

            if (!parsed.success) error(parsed.error ?: "Error desconocido")

            val nombre = parsed.nombre ?: identificacion
            val bets = parsed.bets?.map { r ->
                BetResult(r.grupo, r.equipoA, r.equipoB, r.marcadorA, r.marcadorB)
            } ?: emptyList()

            Pair(nombre, bets)
        }
}
