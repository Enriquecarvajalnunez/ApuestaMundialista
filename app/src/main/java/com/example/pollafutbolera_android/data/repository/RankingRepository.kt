package com.example.pollafutbolera_android.data.repository

import com.example.pollafutbolera_android.data.model.RankingEntry
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class RankingRepository {

    private data class ScriptRankingEntry(
        val posicion: Int,
        val nombre: String,
        val cedula: String,
        val puntos: Int
    )

    private data class ScriptVerRankingResponse(
        val success: Boolean,
        val ranking: List<ScriptRankingEntry>? = null,
        val error: String? = null
    )

    private data class ScriptCalcRankingResponse(
        val success: Boolean,
        val totalJugadores: Int? = null,
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

    private val verRankingAdapter = moshi.adapter(ScriptVerRankingResponse::class.java)
    private val calcRankingAdapter = moshi.adapter(ScriptCalcRankingResponse::class.java)

    /** Lee el ranking ya calculado desde la hoja "Ranking" (accion=verRanking) */
    suspend fun fetchRanking(): List<RankingEntry> = withContext(Dispatchers.IO) {
        val rawBody = get("${BetSubmitRepository.SCRIPT_URL}?accion=verRanking")
        val parsed = try {
            verRankingAdapter.fromJson(rawBody)
        } catch (e: Exception) {
            error("Respuesta no es JSON válido: ${rawBody.take(300)}")
        } ?: error("Formato de respuesta inválido")

        if (!parsed.success) error(parsed.error ?: "Error desconocido")

        parsed.ranking?.map { e ->
            RankingEntry(e.posicion, e.nombre, e.cedula, e.puntos)
        } ?: emptyList()
    }

    /** Calcula el ranking y lo guarda en la hoja "Ranking" (accion=calcularRanking) */
    suspend fun calcularRanking(): Int = withContext(Dispatchers.IO) {
        val rawBody = get("${BetSubmitRepository.SCRIPT_URL}?accion=calcularRanking")
        val parsed = try {
            calcRankingAdapter.fromJson(rawBody)
        } catch (e: Exception) {
            error("Respuesta no es JSON válido: ${rawBody.take(300)}")
        } ?: error("Formato de respuesta inválido")

        if (!parsed.success) error(parsed.error ?: "Error desconocido")
        parsed.totalJugadores ?: 0
    }

    private fun get(url: String): String {
        var request = Request.Builder().url(url).get().build()
        var response = client.newCall(request).execute()

        if (response.code in 300..399) {
            val location = response.header("Location")
                ?: error("Redirect sin Location header")
            response.close()
            request = Request.Builder().url(location).get().build()
            response = client.newCall(request).execute()
        }

        return response.body?.string() ?: error("Respuesta vacía del servidor")
    }
}
