package com.example.pa_pam.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pa_pam.RegisterScreen
import com.example.pa_pam.LoginScreen
import com.example.pa_pam.ProfileScreen
import com.example.pa_pam.TodoScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Register.route
    ) {

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onSignUpClick = {
                    navController.navigate(NavRoutes.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }



        composable(NavRoutes.Home.route) {
            TodoScreen(
                onEditProfileClick = {
                    navController.navigate(NavRoutes.Profile.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                    }
                }

            )
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onLogoutNavigate = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Profile.route) {inclusive = false}
                    }
                }
            )
        }

    }
}
