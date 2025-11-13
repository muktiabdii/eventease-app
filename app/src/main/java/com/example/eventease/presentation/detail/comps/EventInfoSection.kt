package com.example.eventease.presentation.detail.comps

import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun EventInfoSection(
    event: Event,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        val outputTimeFormat = SimpleDateFormat("hh:mm aa", Locale.US)

        val (date, time) = try {
            val dateObject = inputFormat.parse(event.date)
            if (dateObject != null) {
                outputDateFormat.format(dateObject) to outputTimeFormat.format(dateObject)
            } else {
                "Invalid Date" to "Invalid Time"
            }
        } catch (e: Exception) {
            Log.e("EventDetailDebug", "Parsing gagal: ${e.message}")
            "Invalid Date" to "Invalid Time"
        }

        EventInfoRow(
            icon = Icons.Default.DateRange,
            text = date
        )
        EventInfoRow(
            icon = Icons.Default.AccessTime,
            text = time
        )
        EventInfoRow(
            icon = Icons.Default.LocationOn,
            text = event.location
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