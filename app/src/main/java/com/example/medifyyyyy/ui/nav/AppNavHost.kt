package com.example.medifyyyyy.ui.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.pages.AddAllergyScreen
import com.example.medifyyyyy.ui.pages.AddBankObatScreen
import com.example.medifyyyyy.ui.pages.AddVaksinScreen
import com.example.medifyyyyy.ui.pages.AllergyListScreen
import com.example.medifyyyyy.ui.pages.BerandaFitur
import com.example.medifyyyyy.ui.pages.DetailAllergyScreen
import com.example.medifyyyyy.ui.pages.DetailScreen
import com.example.medifyyyyy.ui.pages.HomeScreen
import com.example.medifyyyyy.ui.pages.LoginScreen
import com.example.medifyyyyy.ui.pages.RegisterScreen
import com.example.medifyyyyy.ui.pages.SertifikatVaksin
import com.example.medifyyyyy.ui.pages.VaksinScreen
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel
import com.example.medifyyyyy.ui.viewmodel.AuthViewModel


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
    ,authViewModel: AuthViewModel = viewModel()
) {
    val nav = rememberNavController()
    //  val authState by authViewModel.authState.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()



    // 4. Gunakan LaunchedEffect untuk bereaksi terhadap perubahan state SETELAH aplikasi berjalan
    LaunchedEffect(isAuthenticated, nav) {


        // Jika state berubah menjadi "tidak login"...
        if (!isAuthenticated) {
            Log.d("AppNavHost", "DEBUG: User authenticated → Login")
            // ...paksa navigasi ke Login dan bersihkan semua back stack
            nav.navigate(Screen.Login.route) {
                popUpTo(nav.graph.id) {
                    inclusive = true
                }
            }
        }
        // Jika state berubah menjadi "login berhasil"...
        else {
            // ...paksa navigasi ke Home
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
                // onLoginSuccess dihapus, karena sudah ditangani oleh LaunchedEffect di atas
            )
        }
        composable(Screen.Register.route) {

            // Sama seperti Login, RegisterScreen tidak perlu lagi menavigasi
            RegisterScreen(
                viewModel = authViewModel
                // onRegisterSuccess juga dihapus
            )

//            RegisterScreen(onRegisterSuccess = {
//                nav.navigate(Screen.Home.route) {
//                    popUpTo(Screen.Login.route) { inclusive = true }
//                }
//            })
        }
        composable(Screen.Beranda.route){
            BerandaFitur(
                onNavigateBankObat = { nav.navigate(Screen.Home.route) },
                onNavigateFoodAllergy = { nav.navigate(Screen.AllergyFoodList.route) },
                onNavigateSertifVaksin = { nav.navigate(Screen.ListSertif.route) },
                onNavigateDrugAllergy = {
                    // Sementara dikosongkan dulu atau arahkan ke halaman yg sesuai
                    // nav.navigate(Screen.AllergyList.route)
                },
                onLogout = {

                    authViewModel.logout()
//                    nav.navigate(Screen.Login.route) {
//                        popUpTo(Screen.Home.route) { inclusive = true }
//                    }
                }
            )
        }
        // Aisyah
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

        // Ajeng
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

        // ... kode sebelumnya ...

        composable(route = Screen.AddAllergyFood.route) {
            // TAMBAHKAN BARIS INI (Membuat variabel baru khusus untuk halaman ini)
            val allergyViewModel: AllergyFoodViewModel = viewModel()

            AddAllergyScreen(
                viewModel = allergyViewModel, // Sekarang ini tidak akan merah lagi
                onBack = {
                    nav.popBackStack()
                }
            )
        }

        composable(route = Screen.DetailAllergyFood.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""

            // TAMBAHKAN JUGA DI SINI
            val allergyViewModel: AllergyFoodViewModel = viewModel()

            DetailAllergyScreen(
                allergyId = id,
                viewModel = allergyViewModel, // Ini juga akan sembuh
                onBack = { nav.popBackStack() }
            )
        }
    }
}