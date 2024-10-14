package com.saksham.nile.data.model


data class StockInfo(
    val symbol: String,
    val companyName: String
)
data class StockDetail(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val percentageChange: Double,
    val registrationDate: String,
    val industry: String,
    val description: String
)