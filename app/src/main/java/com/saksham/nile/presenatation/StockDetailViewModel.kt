package com.saksham.nile.presenatation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.nile.data.model.StockDetail
import com.saksham.nile.domain.ApiException
import com.saksham.nile.domain.ChartDataPoint
import com.saksham.nile.domain.NetworkException
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

    var chartData by mutableStateOf<List<ChartDataPoint>>(emptyList())

    fun loadStockDetails(symbol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stockDetail = stockRepository.getStockDetails(symbol)
                _uiState.update { it.copy(stockDetail = stockDetail, isLoading = false) }
                loadChartData(TimeRange.ONE_DAY)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = when (e) {
                            is NetworkException -> "Network error: ${e.message}"
                            is ApiException -> {
                                if (e.code == 403) "Authentication error: Please check your API key or permissions."
                                else "API error: ${e.message}"
                            }
                            else -> "An unexpected error occurred: ${e.message}"
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadChartData(timeRange: TimeRange) {
        viewModelScope.launch {
            try {
                chartData = stockRepository.getChartData(_uiState.value.stockDetail!!.symbol, timeRange)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = when (e) {
                            is NetworkException -> "Network error while loading chart data: ${e.message}"
                            is ApiException -> {
                                if (e.code == 403) "Authentication error: Please check your API key or permissions for chart data."
                                else "API error while loading chart data: ${e.message}"
                            }
                            else -> "An unexpected error occurred while loading chart data: ${e.message}"
                        }
                    )
                }
            }
        }
    }
}

data class StockDetailUiState(
    val stockDetail: StockDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

