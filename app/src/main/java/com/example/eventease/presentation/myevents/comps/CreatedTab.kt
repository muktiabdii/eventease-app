package com.example.eventease.presentation.myevents.comps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventease.data.domain.model.Event
import com.example.eventease.presentation.common.EventCard

@Composable
fun CreatedTab(
    events: List<Event>,
    onViewDetails: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events, key = { it.title }) { event ->
            EventCard(
                event = event,
                onViewDetails = { onViewDetails(event) },
                onDelete = { onDeleteEvent(event) }
            )
        }
    }
}