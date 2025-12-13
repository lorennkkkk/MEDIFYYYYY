package com.example.medifyyyyy.ui.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medifyyyyy.ui.pages.AddBankObatScreen
import com.example.medifyyyyy.ui.pages.AddVaksinScreen
import com.example.medifyyyyy.ui.pages.AllergyListScreen
import com.example.medifyyyyy.ui.pages.BerandaFitur
import com.example.medifyyyyy.ui.pages.DetailScreen
import com.example.medifyyyyy.ui.pages.HomeScreen
import com.example.medifyyyyy.ui.pages.HomeDrugAllergy
import com.example.medifyyyyy.ui.pages.LoginScreen
import com.example.medifyyyyy.ui.pages.RegisterScreen
import com.example.medifyyyyy.ui.pages.VaksinScreen
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel
import com.example.medifyyyyy.ui.viewmodel.AuthViewModel
import com.example.medifyyyyy.ui.pages.SideEffectListScreen
import com.example.medifyyyyy.ui.pages.AddLogScreen
import com.example.medifyyyyy.ui.viewmodel.LogViewModel


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val nav = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isAuthenticated, nav) {
        if (!isAuthenticated) {
            Log.d("AppNavHost", "DEBUG: User authenticated → Login")
            nav.navigate(Screen.Login.route) {
                popUpTo(nav.graph.id) { inclusive = true }
            }
        } else {
            Log.d("AppNavHost", "DEBUG: User authenticated → Home")
            nav.navigate(Screen.Beranda.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(navController = nav, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = { nav.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(viewModel = authViewModel)
        }
        composable(Screen.Beranda.route) {
            BerandaFitur(
                onNavigateBankObat = { nav.navigate(Screen.Home.route) },
                onNavigateFoodAllergy = { nav.navigate(Screen.AllergyFoodList.route) },
                onNavigateSertifVaksin = { nav.navigate(Screen.ListSertif.route) },
                // MENGARAH KE DASHBOARD LOG (HomeDrugAllergy)
                onNavigateDrugAllergy = { nav.navigate(Screen.SideEffectDashboard.route) },
                onLogout = { authViewModel.logout() }
            )
        }

        // Aisyah - Bank Obat (HomeScreen Asli)
        composable(Screen.Home.route) {
            // Kita hapus explicit injection jika menyebabkan masalah, biarkan default value bekerja
            // atau gunakan named argument yang benar sesuai definisi fungsi HomeScreen
            HomeScreen(
                // BankObatViewModel = viewModel(), // Biarkan default value bekerja
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

        // Ajeng - Vaksin
        composable(Screen.ListSertif.route) {
            VaksinScreen(navController = nav, onAddVaksin = { nav.navigate(Screen.TambahSertif.route) })
        }
        composable(Screen.TambahSertif.route) {
            AddVaksinScreen(navController = nav)
        }

        // Nava - Alergi Makanan
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
                }
            )
        }

        // Loren - HomeDrugAllergy (Dahulu LogDashboardScreen)
        composable(Screen.SideEffectDashboard.route) {
            val logViewModel: LogViewModel = viewModel()
            HomeDrugAllergy(navController = nav, viewModel = logViewModel)
        }

        // Loren - List Riwayat
        composable(Screen.SideEffectLog.route) {
            val logViewModel: LogViewModel = viewModel()
            SideEffectListScreen(navController = nav, viewModel = logViewModel)
        }

        // Loren - Tambah Log
        composable(Screen.AddLog.route) {
            AddLogScreen(navController = nav)
        }
    }
}
