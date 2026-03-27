package com.example.pollafutbolera_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.model.BetEntry
import com.example.pollafutbolera_android.data.repository.BetSubmitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BetRegistrationViewModel : ViewModel() {

    private val repository = BetSubmitRepository()

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object Success : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun submit(nombre: String, identificacion: String, bets: List<BetEntry>) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repository.submitBets(nombre, identificacion, bets)
                _uiState.value = if (result.success) UiState.Success
                else UiState.Error(result.error ?: "Error desconocido")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
