package com.example.shubham_240326

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shubham_240326.ui.theme.Shubham_240326Theme

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    // just a very simple empty screen for now
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    Shubham_240326Theme {
        DashboardScreen()
    }
}


