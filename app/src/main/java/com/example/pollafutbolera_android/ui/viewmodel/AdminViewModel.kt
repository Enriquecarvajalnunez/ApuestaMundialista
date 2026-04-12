package com.example.pollafutbolera_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    sealed interface RankingState {
        object Idle : RankingState
        object Loading : RankingState
        data class Success(val totalJugadores: Int) : RankingState
        data class Error(val message: String) : RankingState
    }

    private val _rankingState = MutableStateFlow<RankingState>(RankingState.Idle)
    val rankingState: StateFlow<RankingState> = _rankingState.asStateFlow()

    private val repository = RankingRepository()

    fun calcularRanking() {
        viewModelScope.launch {
            _rankingState.value = RankingState.Loading
            try {
                val total = repository.calcularRanking()
                _rankingState.value = RankingState.Success(total)
            } catch (e: Exception) {
                _rankingState.value = RankingState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
