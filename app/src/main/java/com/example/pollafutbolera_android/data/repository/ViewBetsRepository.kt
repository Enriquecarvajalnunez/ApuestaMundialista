package com.example.pollafutbolera_android.data.repository

import android.content.Context
import com.example.pollafutbolera_android.data.model.BetResult
import com.example.pollafutbolera_android.data.remote.SheetsApiClient
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ViewBetsRepository(private val context: Context) {

    companion object {
        private const val SPREADSHEET_ID = "1WB0W5mQkT2uIKL62fuwP4-8iUgMzAE9oQSBcixxc1vw"
        private const val SCOPE = "oauth2:https://www.googleapis.com/auth/spreadsheets.readonly"
    }

    suspend fun fetchBetsForPlayer(identificacion: String): Pair<String, List<BetResult>> =
        withContext(Dispatchers.IO) {
            val account = GoogleSignIn.getLastSignedInAccount(context)
                ?: error("Usuario no autenticado")
            val token = GoogleAuthUtil.getToken(context, account.account!!, SCOPE)
            val auth = "Bearer $token"

            // Paso 1: buscar jugadorId en hoja Jugadores (columna C = Identificacion)
            val jugadoresData = SheetsApiClient.service.getValues(
                authorization = auth,
                spreadsheetId = SPREADSHEET_ID,
                range = "Jugadores!A2:C1000"
            )
            val jugadorRow = jugadoresData.values.firstOrNull { row ->
                row.getOrNull(2)?.trim() == identificacion.trim()
            } ?: error("No se encontró ningún jugador con identificación: $identificacion")

            val jugadorId = jugadorRow.getOrNull(0)?.toIntOrNull()
                ?: error("El jugador encontrado no tiene un ID válido")
            val jugadorNombre = jugadorRow.getOrNull(1) ?: identificacion

            // Paso 2: calcular columnas del jugador (misma lógica que el Apps Script)
            // colA (1-based) = 6 + (jugadorId - 1) * 2
            // colA (0-based) = 5 + (jugadorId - 1) * 2
            val colA0 = 5 + (jugadorId - 1) * 2
            val colB0 = colA0 + 1

            // Paso 3: leer hoja Apuestas (filas 2 en adelante = datos reales)
            val apuestasData = SheetsApiClient.service.getValues(
                authorization = auth,
                spreadsheetId = SPREADSHEET_ID,
                range = "Apuestas!A1:CZ500"
            )
            val rows = apuestasData.values
            if (rows.size < 2) return@withContext Pair(jugadorNombre, emptyList<BetResult>())

            // Paso 4: extraer pronósticos del jugador por partido
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
