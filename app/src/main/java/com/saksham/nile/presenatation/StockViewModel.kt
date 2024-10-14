package com.saksham.nile.presenatation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.nile.data.model.StockInfo
import com.saksham.nile.domain.ApiException
import com.saksham.nile.domain.NetworkException
import com.saksham.nile.domain.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    var searchResults by mutableStateOf<List<StockInfo>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun searchStocks(query: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                searchResults = stockRepository.searchStocks(query)
            } catch (e: Exception) {
                error = when (e) {
                    is NetworkException -> "Network error: ${e.message}"
                    is ApiException -> "API error: ${e.message}"
                    else -> "An unexpected error occurred: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }
}