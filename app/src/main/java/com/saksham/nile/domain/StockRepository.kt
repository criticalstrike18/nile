package com.saksham.nile.domain

import com.saksham.nile.data.ChartDataResponse
import com.saksham.nile.data.StockApiService
import com.saksham.nile.data.StockDetailResponse
import com.saksham.nile.data.StockSearchResult
import com.saksham.nile.data.model.StockDetail
import com.saksham.nile.data.model.StockInfo
import com.saksham.nile.presenatation.screens.TimeRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val stockApiService: StockApiService,
    @Named("apiKey") private val apiKey: String
) {
    suspend fun searchStocks(query: String): List<StockInfo> = withContext(Dispatchers.IO) {
        try {
            val response = stockApiService.searchStocks(query = query, apiKey = apiKey)
            response.result.map { it.toStockInfo() }
        } catch (e: Exception) {
            handleException(e, "Search error")
        }
    }

    suspend fun getStockDetails(symbol: String): StockDetail = withContext(Dispatchers.IO) {
        try {
            val response = stockApiService.getStockDetails(symbol = symbol, apiKey = apiKey)
            response.toStockDetail()
        } catch (e: Exception) {
            handleException(e, "Error fetching stock details")
        }
    }

    suspend fun getChartData(symbol: String, timeRange: TimeRange): List<ChartDataPoint> = withContext(Dispatchers.IO) {
        try {
            val to = Instant.now().epochSecond
            val from = when (timeRange) {
                TimeRange.ONE_DAY -> Instant.now().minus(1, ChronoUnit.DAYS)
                TimeRange.ONE_WEEK -> Instant.now().minus(7, ChronoUnit.DAYS)
                TimeRange.ONE_MONTH -> Instant.now().minus(30, ChronoUnit.DAYS)
                TimeRange.ONE_YEAR -> Instant.now().minus(365, ChronoUnit.DAYS)
            }.epochSecond

            val response = stockApiService.getChartData(
                symbol = symbol,
                resolution = timeRange.toResolution(),
                from = from,
                to = to,
                apiKey = apiKey
            )

            response.toChartDataPoints()
        } catch (e: Exception) {
            handleException(e, "Error fetching chart data")
        }
    }

    private fun TimeRange.toResolution(): String = when (this) {
        TimeRange.ONE_DAY -> "5" // 5 minute intervals
        TimeRange.ONE_WEEK -> "60" // 1 hour intervals
        TimeRange.ONE_MONTH -> "D" // Daily
        TimeRange.ONE_YEAR -> "W" // Weekly
    }

    private fun handleException(e: Exception, message: String): Nothing {
        throw when (e) {
            is IOException -> NetworkException("Network error occurred: ${e.message}", e)
            is HttpException -> ApiException("API error occurred: ${e.code()} - ${e.message()}", e)
            else -> UnknownException("An unexpected error occurred: ${e.message}", e)
        }
    }
}

// Extension functions to map Finnhub API responses to domain models
private fun StockSearchResult.toStockInfo() = StockInfo(
    symbol = symbol,
    companyName = description
)

private fun StockDetailResponse.toStockDetail() = StockDetail(
    symbol = ticker,
    companyName = name ?: ticker,
    currentPrice = 0.0, // Finnhub doesn't provide current price in this endpoint
    percentageChange = 0.0, // Finnhub doesn't provide percentage change in this endpoint
    registrationDate = ipo ?: "N/A",
    industry = finnhubIndustry ?: "N/A",
    description = "Market Cap: $${marketCapitalization ?: "N/A"}, Currency: ${currency ?: "N/A"}"
)

private fun ChartDataResponse.toChartDataPoints(): List<ChartDataPoint> {
    return t.mapIndexed { index, timestamp ->
        ChartDataPoint(
            timestamp = timestamp,
            open = o[index],
            high = h[index],
            low = l[index],
            close = c[index],
            volume = v[index]
        )
    }
}

// Domain models
data class StockInfo(val symbol: String, val companyName: String)
data class StockDetail(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val percentageChange: Double,
    val registrationDate: String,
    val industry: String,
    val description: String
)
data class ChartDataPoint(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

enum class TimeRange {
    ONE_DAY, ONE_WEEK, ONE_MONTH, ONE_YEAR
}

// Exception classes remain the same
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
class UnknownException(message: String, cause: Throwable? = null) : Exception(message, cause)