package com.example.medifyyyyy.ui.nav

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Beranda : Screen ("beranda")

    // Aisyah
    object Home : Screen("home")
    object Add : Screen("add")
    object Detail : Screen("detail/{id}") {
        fun build(id: String) = "detail/$id"
    }
    // Ajeng
    object ListSertif : Screen("list_sertif")
    object TambahSertif : Screen("add_sertif")
    object DetailSertif : Screen("detail_sertif/{id}") {
        fun build(id: String) = "detail_sertif/$id"
    }

    //Nava
    object AllergyFoodList : Screen("allergy_list")
    object AddAllergyFood : Screen("add_allergy")
    object DetailAllergyFood : Screen("detail_allergy/{id}") {
        fun build(id: String) = "detail_allergy/$id"
    }

    object SideEffectDashboard : Screen("side_effect_dashboard")
    object SideEffectLog : Screen("side_effect_log")
    
    // Rute Tambah (tanpa ID)
    object AddLog : Screen("add_log")
    
    // Rute Edit (dengan ID)
    object EditLog : Screen("edit_log/{id}") {
        fun build(id: String) = "edit_log/$id"
    }

    // Rute Detail (dengan ID)
    object DetailLog : Screen("detail_log/{id}") {
        fun build(id: String) = "detail_log/$id"
    }
}
