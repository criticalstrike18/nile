package com.saksham.nile.presenatation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.saksham.nile.data.model.StockInfo
import com.saksham.nile.presenatation.StockViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: StockViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                viewModel.searchStocks(it)
            },
            onSearch = { },
            placeholder = { Text("Search stocks") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            active = false,
            onActiveChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) { }

        when {
            viewModel.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            viewModel.error != null -> Text(
                text = viewModel.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            viewModel.searchResults.isNotEmpty() -> {
                LazyColumn {
                    items(viewModel.searchResults.size) { stock ->
                        StockItem(viewModel.searchResults[stock]) {
                            navController.navigate("stockDetail/${viewModel.searchResults[stock].symbol}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockItem(stock: StockInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stock.companyName, fontWeight = FontWeight.Bold)
                Text(text = stock.symbol, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "View details")
        }
    }
}