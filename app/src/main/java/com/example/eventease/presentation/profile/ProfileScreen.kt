package com.example.eventease.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventease.presentation.common.TopBar
import com.example.eventease.presentation.profile.comps.PersonalInfoCard
import com.example.eventease.presentation.profile.comps.ProfileHeader
import com.example.eventease.presentation.profile.comps.SecurityCard

@Composable
fun ProfileScreen(
    navController: NavController
) {
    var fullName by rememberSaveable { mutableStateOf("Sarah Johnson") }
    var email by rememberSaveable { mutableStateOf("sarah.johnson@email.com") }
    var phone by rememberSaveable { mutableStateOf("+1 (555) 123-4567") }
    var password by rememberSaveable { mutableStateOf("••••••••••") }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            TopBar(
                title = "Profile",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                ProfileHeader(
                    name = fullName,
                    email = email,
                    onEditPhoto = { /* TODO: Handle Ganti Foto */ }
                )
            }

            item {
                PersonalInfoCard(
                    fullName = fullName,
                    onFullNameChange = { fullName = it },
                    email = email,
                    onEmailChange = { email = it },
                    phone = phone,
                    onPhoneChange = { phone = it }
                )
            }

            item {
                SecurityCard(
                    password = password,
                    onPasswordChange = { password = it },
                    onChangeClick = { /* TODO: Navigasi ke ganti password */ }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { /* TODO: Handle Save */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5)
                        )
                    ) {
                        Text(
                            text = "Save Changes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { /* TODO: Handle Logout */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}