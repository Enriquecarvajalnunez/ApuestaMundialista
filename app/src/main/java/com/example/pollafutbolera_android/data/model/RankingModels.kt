package com.example.pollafutbolera_android.data.model

data class RankingEntry(
    val posicion: Int,
    val nombre: String,
    val cedula: String,
    val puntos: Int
)

data class RankingResponse(
    val success: Boolean,
    val ranking: List<RankingEntry>? = null,
    val error: String? = null
)
