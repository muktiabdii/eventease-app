package com.example.eventease

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.repository.AuthRepositoryImpl
import com.example.eventease.data.repository.EventRepositoryImpl
import com.example.eventease.data.repository.UserRepositoryImpl
import com.example.eventease.di.Injection
import com.example.eventease.di.ViewModelFactory
import com.example.eventease.domain.usecase.AuthUseCase
import com.example.eventease.domain.usecase.EventUseCase
import com.example.eventease.domain.usecase.UserUseCase
import com.example.eventease.presentation.auth.AuthViewModel
import com.example.eventease.presentation.auth.LoginScreen
import com.example.eventease.presentation.auth.RegisterScreen
import com.example.eventease.presentation.common.NavigationBar
import com.example.eventease.presentation.create.CreateEventScreen
import com.example.eventease.presentation.create.CreateEventViewModel
import com.example.eventease.presentation.detail.DetailEventScreen
import com.example.eventease.presentation.detail.DetailEventViewModel
import com.example.eventease.presentation.home.HomeScreen
import com.example.eventease.presentation.home.HomeViewModel
import com.example.eventease.presentation.myevents.MyEventsScreen
import com.example.eventease.presentation.myevents.MyEventsViewModel
import com.example.eventease.presentation.profile.ProfileScreen
import com.example.eventease.presentation.profile.UserViewModel
import com.example.eventease.presentation.splash.SplashScreen
import com.example.eventease.presentation.splash.SplashViewModel
import com.example.eventease.ui.theme.EventeaseTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModelFactory: ViewModelFactory

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = Injection.provideApiService(applicationContext)
        val userPreferencesManager = Injection.provideUserPreferencesManager(applicationContext)

        val userRepo = UserRepositoryImpl(
            userPreferencesManager = userPreferencesManager,
            context = applicationContext,
            apiService = apiService
        )
        val authRepo = AuthRepositoryImpl(
            apiService = apiService,
            userPreferencesManager = userPreferencesManager
        )
        val eventRepo = EventRepositoryImpl(
            context = applicationContext,
            apiService = apiService
        )

        val userUseCase = UserUseCase(userRepo)
        val authUseCase = AuthUseCase(authRepo)
        val eventUseCase = EventUseCase(eventRepo)

        viewModelFactory = ViewModelFactory(
            authUseCase = authUseCase,
            userUseCase = userUseCase,
            eventUseCase = eventUseCase
        )

        setContent {
            val navController = rememberNavController()
            EventeaseTheme {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val screensWithBottomBar = listOf(
                    NavDestination.HOME,
                    NavDestination.MY_EVENTS,
                    NavDestination.PROFILE
                )
                val showBottomBar = currentRoute in screensWithBottomBar

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                currentRoute = currentRoute ?: NavDestination.HOME,
                                onItemClick = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = NavDestination.SPLASH,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavDestination.SPLASH) {
                            val splashViewModel: SplashViewModel = viewModel(factory = viewModelFactory)
                            SplashScreen(
                                viewModel = splashViewModel,
                                onNavigateToHome = {
                                    navController.navigate(NavDestination.HOME) {
                                        popUpTo(NavDestination.SPLASH) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(NavDestination.LOGIN) {
                                        popUpTo(NavDestination.SPLASH) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(NavDestination.LOGIN) {
                            val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                            LoginScreen(
                                onNavigateToHome = {
                                    navController.navigate(NavDestination.HOME) {
                                        popUpTo(NavDestination.LOGIN) { inclusive = true }
                                    }
                                },
                                onRegisterClick = {
                                    navController.navigate(NavDestination.REGISTER) {
                                        popUpTo(NavDestination.LOGIN) { inclusive = true }
                                    }
                                },
                                viewModel = authViewModel
                            )
                        }
                        composable(NavDestination.REGISTER) {
                            val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                            RegisterScreen(
                                onLoginClick = {
                                    navController.navigate(NavDestination.LOGIN) {
                                        popUpTo(NavDestination.REGISTER) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(NavDestination.LOGIN) {
                                        popUpTo(NavDestination.LOGIN) { inclusive = true }
                                    }
                                },
                                viewModel = authViewModel
                            )
                        }
                        composable(NavDestination.HOME) {
                            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                            HomeScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = homeViewModel,
                                onNavigateToCreate = {
                                    navController.navigate(NavDestination.CREATE_EVENT)
                                },
                                onEventClick = { eventId ->
                                    navController.navigate("${NavDestination.EVENT_DETAIL_ROUTE}/$eventId")
                                }
                            )
                        }

                        composable(NavDestination.CREATE_EVENT) {
                            val createEventViewModel: CreateEventViewModel = viewModel(factory = viewModelFactory)
                            CreateEventScreen(
                                modifier = Modifier.fillMaxSize(),
                                navController = navController,
                                viewModel = createEventViewModel
                            )
                        }
                        composable(
                            route = NavDestination.EVENT_DETAIL,
                            arguments = listOf(navArgument(NavDestination.EVENT_DETAIL_ID_ARG) {
                                type = NavType.StringType
                            })
                        ) {
                            val detailViewModel: DetailEventViewModel = viewModel(factory = viewModelFactory)
                            DetailEventScreen(
                                navController = navController,
                                viewModel = detailViewModel
                            )
                        }
                        composable(NavDestination.MY_EVENTS) {
                            val myEventsViewModel: MyEventsViewModel = viewModel(factory = viewModelFactory)
                            MyEventsScreen(
                                navController = navController,
                                viewModel = myEventsViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        composable(NavDestination.PROFILE) {
                            val userViewModel: UserViewModel = viewModel(factory = viewModelFactory)
                            ProfileScreen(
                                navController = navController,
                                rootNavController = navController,
                                viewModel = userViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}