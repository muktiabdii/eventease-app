package com.example.eventease

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.repository.AuthRepositoryImpl
import com.example.eventease.data.repository.UserRepositoryImpl
import com.example.eventease.domain.usecase.AuthUseCase
import com.example.eventease.domain.usecase.UserUseCase
import com.example.eventease.presentation.auth.AuthViewModel
import com.example.eventease.presentation.auth.LoginScreen
import com.example.eventease.presentation.auth.RegisterScreen
import com.example.eventease.presentation.splash.SplashScreen
import com.example.eventease.presentation.splash.SplashViewModel
import com.example.eventease.ui.theme.EventeaseTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            EventeaseTheme {

                // initiate user
                val userRepo = UserRepositoryImpl(UserPreferencesManager(this))
                val userUseCase = UserUseCase(userRepo)

                // initiate auth and splash
                val authRepo = AuthRepositoryImpl()
                val authUseCase = AuthUseCase(authRepo)
                val authViewModel = AuthViewModel(authUseCase, userUseCase)
                val splashViewModel = SplashViewModel(userUseCase)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavDestination.SPLASH
                    ) {
                        composable(NavDestination.SPLASH) {
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
                            Homescreensementara(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}
