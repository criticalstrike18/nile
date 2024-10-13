package com.saksham.nile.presenatation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.nile.data.model.StockDetailUiState
import com.saksham.nile.domain.StockRepository
import com.saksham.nile.presenatation.screens.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockDetailUiState())
    val uiState: StateFlow<StockDetailUiState> = _uiState

    var chartData by mutableStateOf<List<Double>>(emptyList())

    fun loadStockDetails(symbol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stockDetail = stockRepository.getStockDetails(symbol)
                _uiState.update { it.copy(stockDetail = stockDetail, isLoading = false) }
                loadChartData(TimeRange.ONE_DAY)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An unknown error occurred", isLoading = false) }
            }
        }
    }

    fun loadChartData(timeRange: TimeRange) {
        viewModelScope.launch {
            try {
                chartData = stockRepository.getChartData(_uiState.value.stockDetail!!.symbol, timeRange)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred while loading chart data") }
            }
        }
    }
}

