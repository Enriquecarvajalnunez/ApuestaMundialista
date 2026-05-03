package com.example.pollafutbolera_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.model.BetEntry
import com.example.pollafutbolera_android.data.model.BetResult
import com.example.pollafutbolera_android.data.repository.BetSubmitRepository
import com.example.pollafutbolera_android.data.repository.ClockRepository
import com.example.pollafutbolera_android.data.repository.ViewBetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BetRegistrationViewModel : ViewModel() {

    private val repository = BetSubmitRepository()
    private val viewBetsRepository = ViewBetsRepository()
    private val clockRepository = ClockRepository()

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class ExistingBetsFound(val playerName: String, val bets: List<BetResult>) : UiState
        data object NoExistingBets : UiState
        data object Success : UiState
        data class Error(val message: String) : UiState
        data object DeadlinePassed : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /** Verifica si la fecha límite aún está abierta. Se llama al entrar a la pantalla. */
    fun checkDeadline() {
        viewModelScope.launch {
            try {
                val abierto = clockRepository.verificarReloj()
                if (!abierto) {
                    _uiState.value = UiState.DeadlinePassed
                }
            } catch (_: Exception) {
                // Si hay error de red al verificar, dejamos pasar y se validará al submit
            }
        }
    }

    fun searchExistingBets(identificacion: String) {
        if (identificacion.length < 6) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val (nombre, bets) = viewBetsRepository.fetchBetsForPlayer(identificacion)
                if (bets.isNotEmpty()) {
                    _uiState.value = UiState.ExistingBetsFound(playerName = nombre, bets = bets)
                } else {
                    _uiState.value = UiState.NoExistingBets
                }
            } catch (_: Exception) {
                _uiState.value = UiState.NoExistingBets
            }
        }
    }

    /** Verifica la fecha límite y, si está abierta, procede a registrar la apuesta. */
    fun checkDeadlineAndSubmit(nombre: String, identificacion: String, bets: List<BetEntry>) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val abierto = clockRepository.verificarReloj()
                if (!abierto) {
                    _uiState.value = UiState.DeadlinePassed
                    return@launch
                }
                val result = repository.submitBets(nombre, identificacion, bets)
                _uiState.value = if (result.success) UiState.Success
                else UiState.Error(result.error ?: "Error desconocido")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    /** Mantiene el método anterior por si se necesita, pero es preferible usar checkDeadlineAndSubmit */
    @Deprecated("Usar checkDeadlineAndSubmit en su lugar", ReplaceWith("checkDeadlineAndSubmit(nombre, identificacion, bets)"))
    fun submit(nombre: String, identificacion: String, bets: List<BetEntry>) {
        checkDeadlineAndSubmit(nombre, identificacion, bets)
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}