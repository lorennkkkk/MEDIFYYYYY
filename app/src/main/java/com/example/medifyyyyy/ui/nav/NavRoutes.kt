package com.example.medifyyyyy.ui.nav

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Beranda : Screen ("beranda")
    object Home : Screen("home")
    object Add : Screen("add")
    object Detail : Screen("detail/{id}") {
        fun build(id: String) = "detail/$id"
    }
    object  Profile: Screen("profile")
    object  Jadwal: Screen("todo")
}

