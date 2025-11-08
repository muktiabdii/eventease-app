package com.example.eventease.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventease.presentation.detail.comps.EventAboutSection
import com.example.eventease.presentation.detail.comps.EventHeaderImage
import com.example.eventease.presentation.detail.comps.EventInfoSection
import com.example.eventease.presentation.detail.comps.EventOrganizerSection
import com.example.eventease.presentation.detail.comps.EventParticipantsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEventScreen(
    navController: NavController,
    viewModel: DetailEventViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAttending by viewModel.isAttending.collectAsState()

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
            if (uiState is DetailEventState.Success) {
                val buttonText = if (isAttending) "Cancel Attendance" else "Attend Later"

                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = {
                            if (isAttending) {
                                viewModel.onCancelAttendanceClicked()
                            } else {
                                viewModel.onAttendClicked()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = if (isAttending) {
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF3F4F6),
                                contentColor = Color(0xFF4B5563)
                            )
                        } else {
                            ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                        }
                    ) {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (val state = uiState) {
                is DetailEventState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DetailEventState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DetailEventState.Success -> {
                    val event = state.event
                    val organizer = state.organizer
                    val participants = state.participants

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 0.dp)
                    ) {
                        item { EventHeaderImage(imageUrl = event.imageUrl) }

                        item {
                            EventInfoSection(
                                event = event,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFD1D5DB)
                            )
                        }

                        item {
                            EventAboutSection(
                                description = event.description,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFD1D5DB)
                            )
                        }

                        item {
                            EventParticipantsSection(
                                participants = participants,
                                totalCapacity = event.capacity.toIntOrNull() ?: 0,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFD1D5DB)
                            )
                        }

                        item {
                            EventOrganizerSection(
                                organizer = organizer,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}
