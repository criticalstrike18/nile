package com.example.nile.presenatation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nile.presenatation.LocationViewModel
import com.example.nile.presenatation.components.FilledButton
import com.example.nile.presenatation.components.NormalTextField
import com.example.nile.ui.theme.fontFamily

@Composable
fun HomeScreen() {
    var to by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("") }
    val viewModel = LocationViewModel()
    val locationState = viewModel.locationState
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text(
            text = "Nile",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.padding(8.dp))
        NormalTextField(value = to, labelValue = "To" , keyboardType = KeyboardType.Text, onValueChange = {to = it})
        Spacer(modifier = Modifier.padding(4.dp))
        NormalTextField(value = from, labelValue = "From", keyboardType = KeyboardType.Text, onValueChange = {from = it})
        Spacer(modifier = Modifier.padding(8.dp))
        FilledButton(text = "Book Ride", onClick = {viewModel.getCurrentLocation()})
    }
}
