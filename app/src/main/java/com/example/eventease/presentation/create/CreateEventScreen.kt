package com.example.eventease.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
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
import com.example.eventease.presentation.create.comps.EventDateTimePicker
import com.example.eventease.presentation.create.comps.EventFormSection
import com.example.eventease.presentation.create.comps.PosterPickerSection

@Composable
fun CreateEventScreen(
    navController: NavController
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var capacity by rememberSaveable { mutableStateOf("") }
    val dateText by rememberSaveable { mutableStateOf("mm/dd/yyyy") }
    val timeText by rememberSaveable { mutableStateOf("--:-- --") }

    Scaffold(
        topBar = {
            TopBar(
                title = "Create Event",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                PosterPickerSection(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { /* TODO: Handle Image Picker */ }
                )
            }

            item {
                EventFormSection(
                    label = "Event Title",
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Enter event title"
                )
            }

            item {
                EventFormSection(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Describe your event...",
                    multiLine = true
                )
            }

            item {
                EventFormSection(
                    label = "Location",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Enter event location",
                    leadingIcon = Icons.Outlined.LocationOn
                )
            }

            item {
                EventDateTimePicker(
                    dateText = dateText,
                    timeText = timeText,
                    onDateClick = { /* TODO: Show Date Picker */ },
                    onTimeClick = { /* TODO: Show Time Picker */ }
                )
            }

            item {
                EventFormSection(
                    label = "Capacity",
                    value = capacity,
                    onValueChange = { capacity = it },
                    placeholder = "Maximum attendees",
                    leadingIcon = Icons.Outlined.Group
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Handle Save Event */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5)
                    )
                ) {
                    Text(
                        text = "Save Event",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEventScreenPreview() {
    CreateEventScreen(navController = rememberNavController())
}