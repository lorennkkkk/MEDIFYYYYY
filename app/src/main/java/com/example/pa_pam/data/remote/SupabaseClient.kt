package com.example.pa_pam.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseHolder {
    private const val SUPABASE_URL = "https://ppiuhcpmiahtyasgktdb.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBwaXVoY3BtaWFodHlhc2drdGRiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NDk4NjgsImV4cCI6MjA4MTAyNTg2OH0.HO5HadWk2CzL_DiwsTRSR4xIh5kkxRt3m1NUX8rsr-Q"


    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest.Companion)
        install(Storage.Companion)
        install(Postgrest)
    }


    fun session(): UserSession? = client.auth.currentSessionOrNull()
}


