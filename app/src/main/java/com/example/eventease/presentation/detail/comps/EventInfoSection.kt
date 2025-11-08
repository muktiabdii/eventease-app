package com.example.eventease.presentation.detail.comps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventease.data.domain.model.Event

@Composable
fun EventInfoSection(
    event: Event, // <-- Terima objek Event
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = event.title, // <-- Data Dinamis
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Pisahkan tanggal dan waktu dari string "event.date"
        val (date, time) = try {
            val parts = event.date.split(" at ")
            (parts.getOrNull(0) ?: "N/A") to (parts.getOrNull(1) ?: "N/A")
        } catch (e: Exception) {
            "N/A" to "N/A"
        }

        EventInfoRow(
            icon = Icons.Default.DateRange,
            text = date // <-- Data Dinamis
        )
        EventInfoRow(
            icon = Icons.Default.AccessTime,
            text = time // <-- Data Dinamis
        )
        EventInfoRow(
            icon = Icons.Default.LocationOn,
            text = event.location // <-- Data Dinamis
        )
    }
}

@Composable
private fun EventInfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4B5563)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}