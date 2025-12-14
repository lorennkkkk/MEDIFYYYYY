package com.example.medifyyyyy.ui.nav


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medifyyyyy.ui.pages.AddBankObatScreen
import com.example.medifyyyyy.ui.pages.BerandaFitur
import com.example.medifyyyyy.ui.pages.DetailScreen
import com.example.medifyyyyy.ui.pages.HomeScreen
import com.example.medifyyyyy.ui.pages.LoginScreen
import com.example.medifyyyyy.ui.pages.ProfileScreen
import com.example.medifyyyyy.ui.pages.RegisterScreen
import com.example.medifyyyyy.ui.pages.TodoScreen
import com.example.medifyyyyy.ui.viewmodel.AuthViewModel
import com.example.medifyyyyy.ui.viewmodel.ProfileViewModel


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
    ,authViewModel: AuthViewModel = viewModel()
) {
    val nav = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val startDestination = if (isLoggedIn) {
        Screen.Beranda.route
    } else {
        Screen.Login.route
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            nav.navigate(Screen.Beranda.route) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            nav.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = nav, startDestination = startDestination, modifier = modifier) {

        composable(Screen.Login.route) {
            LoginScreen(
                onSignUpClick = {nav.navigate(Screen.Register.route)},

                onLoginSuccess = {
                    nav.navigate(Screen.Beranda.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
          )
        }

        composable(Screen.Register.route) {
            RegisterScreen (
                onRegisterSuccess = {
                    nav.navigate(Screen.Beranda.route){
                        popUpTo(Screen.Register.route) { inclusive=true }

                    }
                },
                onLoginClick = {
                    nav.navigate(Screen.Login.route){
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Beranda.route){
            BerandaFitur(
                onNavigateBankObat = { nav.navigate(Screen.Home.route) },
                onNavigateJadwal = { nav.navigate(Screen.Jadwal.route) },
                onNavigateProfile = { nav.navigate(Screen.Profile.route) },
                onLogout = {
                    authViewModel.logout()
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onAdd = { nav.navigate(Screen.Add.route) },
                onDetail = { id -> nav.navigate(Screen.Detail.build(id)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(Screen.Add.route) {
            AddBankObatScreen(onDone = { nav.popBackStack() }, onBack = { nav.popBackStack() })
        }
        composable(Screen.Detail.route) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            DetailScreen(id = id, onBack = { nav.popBackStack() })
        }

        composable (Screen.Profile.route) {
            ProfileScreen(
                vm = profileViewModel,
                onBack = { nav.popBackStack() },
                onLogoutNavigate = {
                    authViewModel.logout()
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route){inclusive=false}
                    }
                }
            )
        }

        composable(Screen.Jadwal.route) {
            TodoScreen  (
                onEditProfileClick ={
                    nav.navigate(Screen.Profile.route){
                        popUpTo(Screen.Jadwal.route){inclusive=false}
                    }
                }
            )
        }
    }
}