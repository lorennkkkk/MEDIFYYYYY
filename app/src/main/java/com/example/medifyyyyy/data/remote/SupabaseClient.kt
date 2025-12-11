package com.example.medifyyyyy.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseHolder {
    // Ganti dengan URL & anon/public key Supabase Anda
    private const val SUPABASE_URL = "https://nfkgjohwiagasxsakfui.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ma2dqb2h3aWFnYXN4c2FrZnVpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjUzMzkzNDcsImV4cCI6MjA4MDkxNTM0N30.AkPIyMixA8_gBp09iS9xk1qif7CQnOFJ2wZ4AYQ0-PA"


    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest.Companion)
        install(Storage.Companion)
    }

    fun session(): UserSession? = client.auth.currentSessionOrNull()
}