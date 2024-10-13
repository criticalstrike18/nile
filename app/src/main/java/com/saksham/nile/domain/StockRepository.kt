package com.saksham.nile.domain

import android.util.Log
import com.saksham.nile.data.StockApiService
import com.saksham.nile.data.StockDetailResponse
import com.saksham.nile.data.StockSearchResponse
import com.saksham.nile.data.model.StockDetail
import com.saksham.nile.data.model.StockInfo
import com.saksham.nile.presenatation.screens.TimeRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val stockApiService: StockApiService
) {
    suspend fun searchStocks(query: String): List<StockInfo> = withContext(Dispatchers.IO) {
        try {
            val response = stockApiService.searchStocks(query)
            if (response.isSuccessful) {
                response.body()?.map { it.toStockInfo() } ?: emptyList()
            } else {
                throw HttpException(response)
            }
        }  catch (e: Exception) {
            Log.e("StockRepository", "Search error", e)
            throw when (e) {
                is IOException -> NetworkException("Network error occurred: ${e.message}", e)
                is HttpException -> ApiException("API error occurred: ${e.code()} - ${e.message()}", e)
                else -> UnknownException("An unexpected error occurred: ${e.message}", e)
            }
        }
    }

    suspend fun getStockDetails(symbol: String): StockDetail = withContext(Dispatchers.IO) {
        try {
            val response = stockApiService.getStockDetails(symbol)
            if (response.isSuccessful) {
                response.body()?.toStockDetail() ?: throw ApiException("No data available for this stock")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw when (e) {
                is IOException -> NetworkException("Network error occurred", e)
                is HttpException -> ApiException("API error occurred: ${e.code()}", e)
                else -> UnknownException("An unexpected error occurred", e)
            }
        }
    }

    suspend fun getChartData(symbol: String, timeRange: TimeRange): List<Double> = withContext(Dispatchers.IO) {
        try {
            val response = stockApiService.getChartData(symbol, timeRange.toApiParam())
            if (response.isSuccessful) {
                response.body()?.prices ?: emptyList()
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw when (e) {
                is IOException -> NetworkException("Network error occurred", e)
                is HttpException -> ApiException("API error occurred: ${e.code()}", e)
                else -> UnknownException("An unexpected error occurred", e)
            }
        }
    }

    private fun TimeRange.toApiParam(): String = when (this) {
        TimeRange.ONE_DAY -> "1d"
        TimeRange.ONE_WEEK -> "1wk"
        TimeRange.ONE_MONTH -> "1mo"
        TimeRange.ONE_YEAR -> "1y"
    }
}

class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
class UnknownException(message: String, cause: Throwable? = null) : Exception(message, cause)

// Extension functions to map API responses to domain models
private fun StockSearchResponse.toStockInfo() = StockInfo(
    symbol = symbol,
    companyName = shortName ?: longName ?: symbol
)

private fun StockDetailResponse.toStockDetail() = StockDetail(
    symbol = symbol,
    companyName = shortName ?: longName ?: symbol,
    currentPrice = regularMarketPrice,
    percentageChange = (regularMarketPrice - regularMarketPreviousClose) / regularMarketPreviousClose * 100,
    registrationDate = registrationDate ?: "N/A",
    industry = industry ?: "N/A",
    description = longBusinessSummary ?: "No description available"
)

// Add this extension function to make logs more informative
fun Exception.toLogString(): String {
    return "${this::class.simpleName}: $message\n${stackTraceToString()}"
}