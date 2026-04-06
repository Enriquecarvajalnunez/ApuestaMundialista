package com.example.pollafutbolera_android.data.repository

import android.content.Context
import com.example.pollafutbolera_android.data.model.BetResult
import com.example.pollafutbolera_android.data.model.ValueRange
import com.example.pollafutbolera_android.data.remote.SheetsApiClient
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ViewBetsRepository(private val context: Context) {

    companion object {
        private const val SPREADSHEET_ID = "15r-Yw5WvfSIu4hlV1CaBIAUb8mPZ_0i741XU1lSfkrk"
        private const val SCOPE = "oauth2:https://www.googleapis.com/auth/spreadsheets"
    }

    suspend fun fetchBetsForPlayer(identificacion: String): Pair<String, List<BetResult>> =
        withContext(Dispatchers.IO) {
            val account = GoogleSignIn.getLastSignedInAccount(context)
                ?: error("Usuario no autenticado")
            val token = GoogleAuthUtil.getToken(context, account.account!!, SCOPE)
            val auth = "Bearer $token"

            // Paso 1: leer Jugadores y Apuestas en una sola llamada (batchGet usa @Query, evita encoding en path)
            val batch = SheetsApiClient.service.batchGetValues(
                authorization = auth,
                spreadsheetId = SPREADSHEET_ID,
                ranges = listOf("Jugadores!A2:C1000", "Apuestas!A1:CZ500")
            )

            // Paso 2: buscar jugador por cédula
            val jugadoresRows = batch.valueRanges.getOrNull(0)?.values ?: emptyList()
            val jugadorRow = jugadoresRows.firstOrNull { row ->
                row.getOrNull(2)?.trim() == identificacion.trim()
            } ?: error("No se encontró ningún jugador con identificación: $identificacion")

            val jugadorId = jugadorRow.getOrNull(0)?.trim()
                ?: error("El jugador encontrado no tiene un ID válido")
            val jugadorNombre = jugadorRow.getOrNull(1) ?: identificacion

            // Paso 3: armar las claves de columna para este jugador
            val headerKeyA = "pronosticoJugadorID${jugadorId}_A"
            val headerKeyB = "pronosticoJugadorID${jugadorId}_B"

            // Paso 4: extraer pronósticos del jugador por partido
            val rows = batch.valueRanges.getOrNull(1)?.values ?: emptyList()
            if (rows.size < 2) return@withContext Pair(jugadorNombre, emptyList<BetResult>())

            val headerRow = rows[0]
            val colA0 = headerRow.indexOfFirst { it.trim() == headerKeyA }
            val colB0 = headerRow.indexOfFirst { it.trim() == headerKeyB }

            if (colA0 < 0 || colB0 < 0) {
                error("No se encontraron columnas '$headerKeyA'/'$headerKeyB' en la hoja Apuestas")
            }

            val bets = rows.drop(1).mapNotNull { row ->
                val grupo   = row.getOrNull(0)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val equipoA = row.getOrNull(1)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val equipoB = row.getOrNull(2)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val marcadorA = row.getOrNull(colA0)?.takeIf { it.isNotBlank() } ?: "-"
                val marcadorB = row.getOrNull(colB0)?.takeIf { it.isNotBlank() } ?: "-"
                BetResult(grupo, equipoA, equipoB, marcadorA, marcadorB)
            }
            Pair(jugadorNombre, bets)
        }
}
