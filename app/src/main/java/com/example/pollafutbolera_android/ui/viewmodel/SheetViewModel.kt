package com.example.pollafutbolera_android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollafutbolera_android.data.model.ValueRange
import com.example.pollafutbolera_android.data.repository.SheetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SheetUiState {
    object Idle : SheetUiState
    object Loading : SheetUiState
    data class Success(val data: ValueRange) : SheetUiState
    data class Error(val message: String) : SheetUiState
}

class SheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SheetsRepository(application)

    private val _uiState = MutableStateFlow<SheetUiState>(SheetUiState.Idle)
    val uiState: StateFlow<SheetUiState> = _uiState

    fun loadSheet() {
        viewModelScope.launch {
            _uiState.value = SheetUiState.Loading
            _uiState.value = try {
                val data = repository.fetchSheetData()
                SheetUiState.Success(data)
            } catch (e: Exception) {
                SheetUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
