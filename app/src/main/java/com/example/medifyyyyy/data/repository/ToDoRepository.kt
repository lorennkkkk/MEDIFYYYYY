package com.example.medifyyyyy.data.repository

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.model.Profile
import com.example.medifyyyyy.domain.model.Todo
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.InputStream

class TodoRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest

    suspend fun getTodos(userId: String): List<Todo> {
        return postgrest["todo"]
            .select { filter { eq("user_id", userId) } }
            .decodeList()
    }

    suspend fun addTodo(userId: String, title: String, time: String) {
        postgrest["todo"].insert(
            mapOf(
                "user_id" to userId,
                "title" to title,
                "time" to time
            )
        )
    }


    suspend fun deleteTodo(id: Long) {
        postgrest["todo"].delete {
            filter { eq("id", id) }
        }
    }
}