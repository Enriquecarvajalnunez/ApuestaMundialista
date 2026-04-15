package com.example.pollafutbolera_android.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    sealed interface RankingState {
        object Idle : RankingState
        object Loading : RankingState
        data class Success(val totalJugadores: Int) : RankingState
        data class Error(val message: String) : RankingState
    }

    sealed interface EmpatesState {
        object Idle : EmpatesState
        object Loading : EmpatesState
        data class Success(val totalGrupos: Int, val totalJugadores: Int) : EmpatesState
        data class Error(val message: String) : EmpatesState
    }

    private val _rankingState = MutableStateFlow<RankingState>(RankingState.Idle)
    val rankingState: StateFlow<RankingState> = _rankingState.asStateFlow()

    private val _empatesState = MutableStateFlow<EmpatesState>(EmpatesState.Idle)
    val empatesState: StateFlow<EmpatesState> = _empatesState.asStateFlow()

    private val repository = RankingRepository()

    private val prefs = application.getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)
    private val prefsKey = "extra_admins"

    val defaultAdmins = setOf(
        "gfguerrerod@gmail.com",
        "enrique.carvajal.nunez@gmail.com"
    )

    private val _allowedAdmins = MutableStateFlow<List<String>>(loadAdmins())
    val allowedAdmins: StateFlow<List<String>> = _allowedAdmins.asStateFlow()

    private fun loadAdmins(): List<String> {
        val extras = prefs.getStringSet(prefsKey, emptySet()) ?: emptySet()
        return (defaultAdmins + extras).toList().sorted()
    }

    fun isEmailAllowed(email: String): Boolean =
        _allowedAdmins.value.any { it.equals(email, ignoreCase = true) }

    fun addAdmin(email: String) {
        val trimmed = email.trim().lowercase()
        if (trimmed.isBlank() || isEmailAllowed(trimmed)) return
        val extras = (prefs.getStringSet(prefsKey, emptySet()) ?: emptySet()) + trimmed
        prefs.edit().putStringSet(prefsKey, extras).apply()
        _allowedAdmins.value = loadAdmins()
    }

    fun removeAdmin(email: String) {
        if (defaultAdmins.contains(email.lowercase())) return // no eliminar defaults
        val extras = (prefs.getStringSet(prefsKey, emptySet()) ?: emptySet()) - email.lowercase()
        prefs.edit().putStringSet(prefsKey, extras).apply()
        _allowedAdmins.value = loadAdmins()
    }

    sealed interface ResolverEmpatesState {
        object Idle : ResolverEmpatesState
    }

    private val _resolverEmpatesState = MutableStateFlow<ResolverEmpatesState>(ResolverEmpatesState.Idle)
    val resolverEmpatesState: StateFlow<ResolverEmpatesState> = _resolverEmpatesState.asStateFlow()

    fun resolverEmpates() {
        // TODO: implementar lógica de resolución de empates
    }

    fun buscarEmpates() {
        viewModelScope.launch {
            _empatesState.value = EmpatesState.Loading
            try {
                val result = repository.buscarEmpates()
                _empatesState.value = EmpatesState.Success(result.totalGruposEmpate, result.totalJugadoresEmpatados)
            } catch (e: Exception) {
                _empatesState.value = EmpatesState.Error(e.message ?: "Error desconocido")
            }
        }
    }

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
