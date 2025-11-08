package com.example.eventease.presentation.create

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventease.presentation.common.TopBar
import com.example.eventease.presentation.create.comps.EventDateTimePicker
import com.example.eventease.presentation.create.comps.EventFormSection
import com.example.eventease.presentation.create.comps.PosterPickerSection
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    viewModel: CreateEventViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImagePicked(uri)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateEventState.Success -> {
                Toast.makeText(context, "Event Created Successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.popBackStack()
            }
            is CreateEventState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDateChange(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            modifier = Modifier.fillMaxWidth(),
            title = { Text("Select Time", style = MaterialTheme.typography.titleLarge) },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onTimeChange(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                    imageUri = viewModel.imageUri,
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    }
                )
            }

            item {
                EventFormSection(
                    label = "Event Title",
                    value = viewModel.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    placeholder = "Enter event title"
                )
            }

            item {
                EventFormSection(
                    label = "Description",
                    value = viewModel.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    placeholder = "Describe your event...",
                    multiLine = true
                )
            }

            item {
                EventFormSection(
                    label = "Location",
                    value = viewModel.location,
                    onValueChange = { viewModel.onLocationChange(it) },
                    placeholder = "Enter event location",
                    leadingIcon = Icons.Outlined.LocationOn
                )
            }

            item {
                EventDateTimePicker(
                    dateText = viewModel.dateText,
                    timeText = viewModel.timeText,
                    onDateClick = { showDatePicker = true },
                    onTimeClick = { showTimePicker = true }
                )
            }

            item {
                EventFormSection(
                    label = "Capacity",
                    value = viewModel.capacity,
                    onValueChange = { viewModel.onCapacityChange(it) },
                    placeholder = "Maximum attendees",
                    leadingIcon = Icons.Outlined.Group
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.saveEvent() },
                    enabled = uiState != CreateEventState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5)
                    )
                ) {
                    if (uiState == CreateEventState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Save Event",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
