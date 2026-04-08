package com.example.pollafutbolera_android.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.model.BetResult
import com.example.pollafutbolera_android.data.repository.ViewBetsRepository
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import retrofit2.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewBetsViewModel(application: Application) : AndroidViewModel(application) {

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        object NoAdminSession : UiState
        data class Success(val playerName: String, val bets: List<BetResult>) : UiState
        data class Error(val message: String) : UiState
        data class NeedConsent(val intent: Intent) : UiState
    }

    private val repository = ViewBetsRepository(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var pendingIdentificacion: String = ""

    fun searchBets(identificacion: String) {
        if (identificacion.isBlank()) return
        if (GoogleSignIn.getLastSignedInAccount(getApplication()) == null) {
            _uiState.value = UiState.NoAdminSession
            return
        }
        pendingIdentificacion = identificacion
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                val (nombre, bets) = repository.fetchBetsForPlayer(identificacion)
                UiState.Success(playerName = nombre, bets = bets)
            } catch (e: UserRecoverableAuthException) {
                e.intent?.let { UiState.NeedConsent(it) }
                    ?: UiState.Error(e.message ?: "Se requiere autorización adicional")
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string() ?: "sin cuerpo"
                UiState.Error("HTTP ${e.code()}: $body")
            } catch (e: Exception) {
                UiState.Error("${e.javaClass.simpleName}: ${e.message}")
            }
        }
    }

    fun retryAfterConsent() {
        searchBets(pendingIdentificacion)
    }

    fun reset() {
        _uiState.value = UiState.Idle
    }
}
