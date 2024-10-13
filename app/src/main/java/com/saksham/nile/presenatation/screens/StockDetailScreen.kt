package com.saksham.nile.presenatation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saksham.nile.presenatation.StockChart
import com.saksham.nile.presenatation.StockDetailViewModel

@Composable
fun StockDetailScreen(
    symbol: String,
    viewModel: StockDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadStockDetails(symbol)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            uiState.error != null -> Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            uiState.stockDetail != null -> {
                val stock = uiState.stockDetail!!

                Text(text = stock.companyName, style = MaterialTheme.typography.headlineMedium)
                Text(text = stock.symbol, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Current Price: $${stock.currentPrice}", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${stock.percentageChange}%",
                    color = if (stock.percentageChange >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                ChartSection(viewModel)

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Company Information", style = MaterialTheme.typography.titleLarge)
                Text(text = "Registration Date: ${stock.registrationDate}")
                Text(text = "Industry: ${stock.industry}")
                Text(text = "Description: ${stock.description}")
            }
        }
    }
}

@Composable
fun ChartSection(viewModel: StockDetailViewModel) {
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ONE_DAY) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimeRange.values().forEach { timeRange ->
                Button(
                    onClick = {
                        selectedTimeRange = timeRange
                        viewModel.loadChartData(timeRange)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTimeRange == timeRange)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = timeRange.label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StockChart(
            data = viewModel.chartData,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineColor = MaterialTheme.colorScheme.primary
        )
    }
}

enum class TimeRange(val label: String) {
    ONE_DAY("1D"),
    ONE_WEEK("1W"),
    ONE_MONTH("1M"),
    ONE_YEAR("1Y")
}