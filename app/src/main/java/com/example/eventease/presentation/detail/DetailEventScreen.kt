package com.example.eventease.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventease.presentation.detail.comps.EventAboutSection
import com.example.eventease.presentation.detail.comps.EventHeaderImage
import com.example.eventease.presentation.detail.comps.EventInfoSection
import com.example.eventease.presentation.detail.comps.EventOrganizerSection
import com.example.eventease.presentation.detail.comps.EventParticipantsSection
import com.example.eventease.ui.theme.EventeaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEventScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { /* TODO: Handle Attend Click */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Attend Later",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(top = 0.dp)
        ) {
            item {
                EventHeaderImage()
            }

            item {
                EventInfoSection(
                    modifier = Modifier.padding(16.dp)
                )
            }

            item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }

            item {
                EventAboutSection(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }

            item {
                EventParticipantsSection(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }

            item {
                EventOrganizerSection(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailEventScreenPreview() {
    EventeaseTheme {
        DetailEventScreen(
            navController = rememberNavController()
        )
    }
}
