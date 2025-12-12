package com.example.pa_pam.data.repositories

import com.example.pa_pam.data.remote.SupabaseHolder
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Long? = null,
    val user_id: String,
    val title: String,
    val time: String
)

class TodoRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest

    suspend fun getTodos(userId: String): List<Todo> {
        return postgrest["todo"]
            .select { filter { eq("user_id", userId) } }
            .decodeList()
    }

    suspend fun addTodo(userId: String, title: String, time: String) {
        postgrest["todo"].insert(
            Todo(user_id = userId, title = title, time = time)
        )
    }

    suspend fun deleteTodo(id: Long) {
        postgrest["todo"].delete {
            filter { eq("id", id) }
        }
    }
}
