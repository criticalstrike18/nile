package com.saksham.nile.data

import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    @GET("search")
    suspend fun searchStocks(
        @Query("q") query: String,
        @Query("token") apiKey: String
    ): StockSearchResponse

    @GET("stock/profile2")
    suspend fun getStockDetails(
        @Query("symbol") symbol: String,
        @Query("token") apiKey: String
    ): StockDetailResponse

    @GET("stock/candle")
    suspend fun getChartData(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("token") apiKey: String
    ): ChartDataResponse
}

data class StockSearchResponse(
    val count: Int,
    val result: List<StockSearchResult>
)

data class StockSearchResult(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)

data class StockDetailResponse(
    val country: String?,
    val currency: String?,
    val exchange: String?,
    val ipo: String?,
    val marketCapitalization: Double?,
    val name: String?,
    val phone: String?,
    val shareOutstanding: Double?,
    val ticker: String,
    val weburl: String?,
    val logo: String?,
    val finnhubIndustry: String?
)

data class ChartDataResponse(
    val c: List<Double>,  // Close prices
    val h: List<Double>,  // High prices
    val l: List<Double>,  // Low prices
    val o: List<Double>,  // Open prices
    val t: List<Long>,    // Timestamps
    val v: List<Long>     // Volumes
)