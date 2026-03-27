package com.example.pollafutbolera_android.data.model

data class BetEntry(
    val grupo: String,
    val equipoA: String,
    val equipoB: String,
    val marcadorA: Int,
    val marcadorB: Int
)

data class BetRequest(
    val nombre: String,
    val identificacion: String,
    val grupo: String,
    val equipoA: String,
    val equipoB: String,
    val marcadorA: Int,
    val marcadorB: Int
)

data class BetResponse(
    val success: Boolean,
    val jugadorId: Int? = null,
    val error: String? = null
)
