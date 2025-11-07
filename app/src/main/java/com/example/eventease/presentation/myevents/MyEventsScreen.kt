package com.example.eventease.presentation.myevents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eventease.data.domain.model.Event
import com.example.eventease.presentation.common.TopBar
import com.example.eventease.presentation.myevents.comps.CreatedTab
import com.example.eventease.presentation.myevents.comps.JoinedTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyEventsScreen(
    navController: NavController
) {
    val tabs = listOf("Created", "Joined")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val createdEvents = remember {
        listOf(
            Event(
                id = 1,
                imageRes = 0,
                title = "Tech Innovation Summit 2024",
                date = "Dec 15, 2024",
                location = "San Francisco, CA"
            )
        )
    }
    val joinedEvents = remember {
        listOf(
            Event(
                id = 2,
                imageRes = 0,
                title = "Summer Music Festival",
                date = "Aug 20, 2024",
                location = "Central Park, NY"
            ),
            Event(
                id = 3,
                imageRes = 0,
                title = "Business Networking Night",
                date = "Nov 5, 2024",
                location = "Downtown Hotel, LA"
            )
        )
    }

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
                containerColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> CreatedTab(
                        events = createdEvents,
                        onViewDetails = { /* TODO: Navigasi ke detail */ },
                        onDeleteEvent = { event -> /* TODO: Handle Hapus Event */ }
                    )
                    1 -> JoinedTab(
                        events = joinedEvents,
                        onViewDetails = { /* TODO: Navigasi ke detail */ }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyEventsScreenPreview() {
    MyEventsScreen(navController = rememberNavController())
}