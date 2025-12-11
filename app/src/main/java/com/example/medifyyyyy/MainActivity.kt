package com.example.medifyyyyy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medifyyyyy.ui.nav.AppNavHost
import com.example.medifyyyyy.ui.theme.MedifyyyyyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedifyyyyyTheme {
                AppNavHost()
            }
        }
    }
}