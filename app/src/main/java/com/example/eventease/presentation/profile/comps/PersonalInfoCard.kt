package com.example.eventease.presentation.profile.comps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PersonalInfoCard(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ProfileTextField(
                label = "Full Name",
                value = fullName,
                onValueChange = onFullNameChange
            )

            ProfileTextField(
                label = "Email Address",
                value = email,
                onValueChange = onEmailChange,
                readOnly = true
            )

            ProfileTextField(
                label = "Phone Number",
                value = phone,
                onValueChange = onPhoneChange,
                readOnly = true
            )
        }
    }
}

/**
 * Composable kustom privat untuk field di halaman profil.
 * Terdiri dari Text (label) di atas OutlinedTextField.
 */
@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Jarak antara label & field
    ) {
        // 1. Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium, // Anda bisa sesuaikan style
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 2. Field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            textStyle = TextStyle(
                fontSize = 16.sp,
                // Teks menjadi abu-abu jika readOnly
                color = if (readOnly) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp), // Sudut lebih bulat
            colors = OutlinedTextFieldDefaults.colors(
                // Latar belakang field putih
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                // Warna border
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) // Border abu-abu tipis
            ),
            singleLine = true
        )
    }
}