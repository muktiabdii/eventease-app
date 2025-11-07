package com.example.eventease.presentation.detail.comps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EventAboutSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "About Event",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Join us for the most anticipated technology " +
                    "conference of the year! The Tech Innovation " +
                    "Summit brings together industry leaders, " +
                    "entrepreneurs, and innovators to explore the " +
                    "latest trends in artificial intelligence, " +
                    "blockchain, and emerging technologies.\n\n" +
                    "This full-day event features keynote " +
                    "presentations, interactive workshops, " +
                    "networking sessions, and product " +
                    "demonstrations from cutting-edge startups " +
                    "and established tech giants.",
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
        )
    }
}