package com.example.pa_pam.nav

sealed class NavRoutes(val route: String) {

    object Register : NavRoutes("register")
    object Login : NavRoutes("login")
    object Home : NavRoutes("home")
    object Profile : NavRoutes("profile")

}
