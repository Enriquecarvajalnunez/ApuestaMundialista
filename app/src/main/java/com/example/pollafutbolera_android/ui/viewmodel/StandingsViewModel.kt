package com.example.pollafutbolera_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.model.RankingEntry
import com.example.pollafutbolera_android.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StandingsViewModel : ViewModel() {

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Success(val ranking: List<RankingEntry>) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val repository = RankingRepository()

    fun loadRanking() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val ranking = repository.fetchRanking()
                _uiState.value = UiState.Success(ranking)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
