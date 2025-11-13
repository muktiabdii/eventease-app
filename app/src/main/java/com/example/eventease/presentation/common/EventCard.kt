package com.example.eventease.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.eventease.data.domain.model.Event
// --- TAMBAHKAN IMPORT INI ---
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
// -----------------------------

@Composable
fun EventCard(
    event: Event,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                EventImage(event = event)
                EventContent(
                    event = event,
                    onViewDetails = onViewDetails
                )
            }
        }

        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp) // Beri sedikit padding agar tidak terpotong
                    .size(32.dp) // Sedikit lebih besar
                    .background(Color(0xFFE53935).copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Event",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp) // Sesuaikan ikon
                )
            }
        }
    }
}

@Composable
private fun EventImage(event: Event) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        SubcomposeAsyncImage(
            model = event.imageUrl,
            contentDescription = event.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                GradientPlaceholder()
            },
            error = {
                GradientPlaceholder()
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun GradientPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6),
                        Color(0xFFEC4899)
                    )
                )
            )
    )
}

@Composable
private fun EventContent(
    event: Event,
    onViewDetails: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = event.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1, // Pastikan judul tidak terlalu panjang
        )

        Spacer(modifier = Modifier.height(12.dp))

        EventDateInfo(date = event.date) // <-- Panggil fungsi yang sudah diformat

        Spacer(modifier = Modifier.height(8.dp))

        EventLocationInfo(location = event.location)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onViewDetails,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4F46E5)
            )
        ) {
            Text(
                text = "View Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// --- FUNGSI INI DIUBAH TOTAL ---
@Composable
private fun EventDateInfo(date: String) {
    // Format input dari API: "2025-11-20T14:30:00.000Z"
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    // Format output yang Anda inginkan: "July 15, 2024"
    val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)

    val formattedDate = try {
        val dateObject = inputFormat.parse(date)
        if (dateObject != null) {
            outputFormat.format(dateObject)
        } else {
            "Invalid Date" // Fallback
        }
    } catch (e: Exception) {
        "Invalid Date" // Fallback jika parsing gagal
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.DateRange,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = formattedDate, // Gunakan tanggal yang sudah diformat
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
// -----------------------------

@Composable
private fun EventLocationInfo(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = location,
            fontSize = 14.sp,
            color = Color.Gray,
            maxLines = 1 // Pastikan lokasi tidak terlalu panjang
        )
    }
}