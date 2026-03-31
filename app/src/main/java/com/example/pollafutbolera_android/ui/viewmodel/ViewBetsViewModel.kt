package com.example.pollafutbolera_android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.model.BetResult
import com.example.pollafutbolera_android.data.repository.ViewBetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewBetsViewModel(application: Application) : AndroidViewModel(application) {

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Success(val playerName: String, val bets: List<BetResult>) : UiState
        data class Error(val message: String) : UiState
    }

    private val repository = ViewBetsRepository(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun searchBets(identificacion: String) {
        if (identificacion.isBlank()) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                val (nombre, bets) = repository.fetchBetsForPlayer(identificacion)
                UiState.Success(playerName = nombre, bets = bets)
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun reset() {
        _uiState.value = UiState.Idle
    }
}
