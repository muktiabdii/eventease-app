package com.example.eventease.presentation.myevents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventease.NavDestination
import com.example.eventease.data.domain.model.Event
import com.example.eventease.presentation.common.TopBar
import com.example.eventease.presentation.myevents.comps.CreatedTab
import com.example.eventease.presentation.myevents.comps.DeleteConfirmationDialog
import com.example.eventease.presentation.myevents.comps.JoinedTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyEventsScreen(
    navController: NavController,
    viewModel: MyEventsViewModel,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Created", "Joined")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                title = "My Events",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        contentColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                divider = { },
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Color(0xFF4F46E5)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = title,
                            fontSize = 16.sp
                            )
                        },
                        selectedContentColor = Color(0xFF4F46E5),
                        unselectedContentColor = Color(0xFF9CA3AF)
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Failed to load events: ${uiState.error}",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) { page ->
                        when (page) {
                            0 -> CreatedTab(
                                events = uiState.createdEvents,
                                onViewDetails = { event ->
                                    navController.navigate("${NavDestination.EVENT_DETAIL_ROUTE}/${event.id}")
                                },
                                onDeleteEvent = { event ->
                                    eventToDelete = event
                                    showDeleteDialog = true
                                }
                            )
                            1 -> JoinedTab(
                                events = uiState.joinedEvents,
                                onViewDetails = { event ->
                                    navController.navigate("${NavDestination.EVENT_DETAIL_ROUTE}/${event.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
        if (showDeleteDialog && eventToDelete != null) {
            DeleteConfirmationDialog(
                eventName = eventToDelete!!.title,
                onConfirm = {
                    viewModel.deleteEvent(eventToDelete!!)
                    showDeleteDialog = false
                    eventToDelete = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    eventToDelete = null
                }
            )
        }
    }
}
