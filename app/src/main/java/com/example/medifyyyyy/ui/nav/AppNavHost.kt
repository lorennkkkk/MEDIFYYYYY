package com.example.medifyyyyy.ui.nav


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medifyyyyy.ui.pages.AddAllergyScreen
import com.example.medifyyyyy.ui.pages.AddBankObatScreen
import com.example.medifyyyyy.ui.pages.AddVaksinScreen
import com.example.medifyyyyy.ui.pages.AllergyListScreen
import com.example.medifyyyyy.ui.pages.BerandaFitur
import com.example.medifyyyyy.ui.pages.DetailScreen
import com.example.medifyyyyy.ui.pages.DetailLogScreen // Import DetailLogScreen
import com.example.medifyyyyy.ui.pages.HomeScreen
import com.example.medifyyyyy.ui.pages.HomeDrugAllergy
import com.example.medifyyyyy.ui.pages.LoginScreen
import com.example.medifyyyyy.ui.pages.ProfileScreen
import com.example.medifyyyyy.ui.pages.RegisterScreen
import com.example.medifyyyyy.ui.pages.TodoScreen
import com.example.medifyyyyy.ui.pages.VaksinScreen
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel
import com.example.medifyyyyy.ui.viewmodel.AuthViewModel
import com.example.medifyyyyy.ui.viewmodel.ProfileViewModel
import com.example.medifyyyyy.ui.pages.SideEffectListScreen
import com.example.medifyyyyy.ui.pages.AddLogScreen
import com.example.medifyyyyy.ui.viewmodel.BankObatViewModel
import com.example.medifyyyyy.ui.viewmodel.LogViewModel


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
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
        composable(Screen.Beranda.route) {
            BerandaFitur(
                onNavigateBankObat = { nav.navigate(Screen.Home.route) },
                onNavigateJadwal = { nav.navigate(Screen.Jadwal.route) },
                onNavigateProfile = { nav.navigate(Screen.Profile.route) },
                onNavigateFoodAllergy = { nav.navigate(Screen.AllergyFoodList.route) },
                onNavigateSertifVaksin = { nav.navigate(Screen.ListSertif.route) },
                // MENGARAH KE DASHBOARD LOG (HomeDrugAllergy)
                onNavigateDrugAllergy = { nav.navigate(Screen.SideEffectDashboard.route) },
                onLogout = {
                    authViewModel.logout()
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Aisyah - Bank Obat (HomeScreen Asli)
        composable(Screen.Home.route) {
            HomeScreen(
                onAdd = { nav.navigate(Screen.Add.route) },
                onDetail = { id: String -> nav.navigate(Screen.Detail.build(id)) },
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

        //nopas profile
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
        //nopas jadwal towdo
        composable(Screen.Jadwal.route) {
            TodoScreen  (
                onEditProfileClick ={
                    nav.navigate(Screen.Profile.route){
                        popUpTo(Screen.Jadwal.route){inclusive=false}
                    }
                }
            )
        }



        // Ajeng - Vaksin
        composable(Screen.ListSertif.route) {
            VaksinScreen(navController = nav, onAddVaksin = { nav.navigate(Screen.TambahSertif.route) })
        }
        composable(Screen.TambahSertif.route) {
            AddVaksinScreen(navController = nav)
        }

        //Nava
        composable(Screen.AllergyFoodList.route) {
            // Ganti tipe datanya jadi AllergyFoodViewModel
            val allergyViewModel: AllergyFoodViewModel = viewModel()

            AllergyListScreen(
                viewModel = allergyViewModel,
                onNavigateToAdd = {
                    nav.navigate(Screen.AddAllergyFood.route)
                },
                onNavigateToDetail = { id ->
                    nav.navigate(Screen.DetailAllergyFood.build(id))
                },

                // --- TAMBAHKAN BARIS INI AGAR TIDAK ERROR ---
                onBack = {
                    nav.popBackStack()
                }
                // -------------------------------------------
            )
        }

        composable(Screen.AddAllergyFood.route) {
            // Kita buat ViewModel baru atau pakai yang ada
            val allergyViewModel: AllergyFoodViewModel = viewModel()

            AddAllergyScreen(
                viewModel = allergyViewModel,
                onBack = {
                    nav.popBackStack()
                }
            )
        }

        // Loren - HomeDrugAllergy (Dashboard Log)
        composable(Screen.SideEffectDashboard.route) {
            val logViewModel: LogViewModel = viewModel()
            HomeDrugAllergy(navController = nav, viewModel = logViewModel)
        }

        // Loren - List Riwayat
        composable(Screen.SideEffectLog.route) {
            val logViewModel: LogViewModel = viewModel()
            SideEffectListScreen(navController = nav, viewModel = logViewModel)
        }

        // Loren - Tambah Log (CREATE)
        composable(Screen.AddLog.route) {
            val logViewModel: LogViewModel = viewModel()
            AddLogScreen(navController = nav, viewModel = logViewModel)
        }

        // Loren - Detail Log (READ SINGLE & DELETE ACCESS)
        composable("detail_log/{id}") { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            val id = idString?.toLongOrNull()

            if (id != null) {
                val logViewModel: LogViewModel = viewModel()
                DetailLogScreen(navController = nav, logId = id, viewModel = logViewModel)
            }
        }

        // Loren - Edit Log (UPDATE)
        composable("edit_log/{id}") { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            val id = idString?.toLongOrNull()

            if (id != null) {
                val logViewModel: LogViewModel = viewModel()
                AddLogScreen(navController = nav, viewModel = logViewModel, logId = id)
            }
        }
    }
}

