package com.saksham.nile.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    @GET("v1/finance/search")
    suspend fun searchStocks(@Query("q") query: String): Response<List<StockSearchResponse>>

    @GET("v10/finance/quoteSummary")
    suspend fun getStockDetails(@Query("symbol") symbol: String): Response<StockDetailResponse>

    @GET("v8/finance/chart")
    suspend fun getChartData(
        @Query("symbol") symbol: String,
        @Query("range") range: String,
        @Query("interval") interval: String = "1d"
    ): Response<ChartDataResponse>
}

data class StockSearchResponse(
    val symbol: String,
    val shortName: String?,
    val longName: String?
)

data class StockDetailResponse(
    val symbol: String,
    val shortName: String?,
    val longName: String?,
    val regularMarketPrice: Double,
    val regularMarketPreviousClose: Double,
    val registrationDate: String?,
    val industry: String?,
    val longBusinessSummary: String?
)

data class ChartDataResponse(
    val prices: List<Double>
)