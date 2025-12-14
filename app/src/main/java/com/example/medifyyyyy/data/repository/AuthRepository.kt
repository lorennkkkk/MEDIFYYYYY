package com.example.medifyyyyy.data.repositories

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.model.Profile
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

//class AuthRepository {
//    private val auth: Auth get() = SupabaseHolder.client.auth
//
//    suspend fun register(email: String, password: String) {
//        auth.signUpWith(Email) {
//            this.email = email
//            this.password = password
//        }
//    }
//
//    suspend fun login(email: String, password: String) {
//        auth.signInWith(Email) {
//            this.email = email
//            this.password = password
//        }
//    }
//
//    suspend fun logout() {
//        auth.signOut()
//    }
//
//    val sessionStatus: Flow<SessionStatus>
//        get() {
//            // Gunakan operator onEach untuk melakukan logging
//            return auth.sessionStatus.onEach { status ->
//                when (status) {
//                    is SessionStatus.Authenticated -> {
//                        // Log sumber sesi di sini, di dalam Repository
//                        Log.d("AuthRepo", "Authenticated from source: ${status.source}")
//                    }
//                    is SessionStatus.NotAuthenticated -> {
//                        Log.d("AuthRepo", "Status: Not authenticated. Signed out: ${status.isSignOut}")
//                    }
//                    is SessionStatus.NetworkError -> {
//                        Log.e("AuthRepo", "Status: Network error.")
//                    }
//                    is SessionStatus.LoadingFromStorage -> {
//                        Log.d("AuthRepo", "Status: Loading from storage")
//                    }
//                    else -> {
//                        Log.d("AuthRepo", "Unknown status: $status")
//                    }
//                }
//            }
//        }
//
//
//
//    fun currentSession(): UserSession? = SupabaseHolder.session()
//}


class AuthRepository {
    private val auth: Auth get() = SupabaseHolder.client.auth
    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("profile-images")

    suspend fun register(firstname: String, lastname: String, username: String, email: String, password: String) {

        // 1. SIGN UP
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("firstname", firstname)
                put("lastname", lastname)
                put("username", username)
            }
        }

        // 2. SIGN IN (agar session terisi!)
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        // 3. BARU AMBIL SESSION
        val userId = auth.currentSessionOrNull()?.user?.id ?: return

        // 4. INSERT PROFILE
        postgrest["profile"].insert(
            mapOf(
                "id" to userId,
                "first_name" to firstname,
                "last_name" to lastname,
                "username" to username,
                "email" to email
            )
        )
    }


    suspend fun login(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

//    suspend fun getEmailByUsername(username: String): String? {
//        val result = postgrest["profile"]
//            .select {
//                filter { eq("username", username) }
//            }
//            .decodeList<Profile>()
//
//        return result.firstOrNull()?.email
//    }


    suspend fun getProfile(userId: String): Profile? {
        val result = postgrest["profile"]
            .select {
                filter { eq("id", userId) }
            }
            .decodeList<Profile>()

        return result.firstOrNull()
    }


    suspend fun logout() {
        auth.signOut()
    }

    suspend fun uploadProfileImage(userId: String, image: ByteArray): String {
        val filePath = "$userId/profile.jpg"

        storage.upload(
            path = filePath,
            data = image,
            upsert = true
        )

        return storage.publicUrl(filePath)
    }

    suspend fun updateProfile(
        userId: String,
        firstname: String,
        lastname: String,
        username: String,
        imageUrl: String? = null
    ) {
        val session = SupabaseHolder.session() ?: return
        val updateData = mutableMapOf<String, String>(
            "first_name" to firstname,
            "last_name" to lastname,
            "username" to username
        )

        if (imageUrl != null) {
            updateData["profile_image_url"] = imageUrl
        }

        postgrest["profile"].update(updateData) {
            filter{eq("id", userId)}
        }

    }

    suspend fun updateAuthMetadata(
        firstname: String,
        lastname: String,
        username: String,
        imageUrl: String?
    ) {
        auth.updateUser {
            data = buildJsonObject {
                put("firstname", firstname)
                put("lastname", lastname)
                put("username", username)
                if (imageUrl != null) put("profile_image_url", imageUrl)
            }
        }
    }

    val sessionStatus: Flow<SessionStatus>
        get() = auth.sessionStatus.onEach { status ->
            when (status) {
                is SessionStatus.Authenticated ->
                    Log.d("AuthRepo", "Authenticated source = ${status.source}")

                is SessionStatus.NotAuthenticated ->
                    Log.d("AuthRepo", "Not authenticated")

                is SessionStatus.NetworkError ->
                    Log.e("AuthRepo", "Network error")

                is SessionStatus.LoadingFromStorage ->
                    Log.d("AuthRepo", "Loading from storage")
            }
        }

    suspend fun saveProfileChanges(
        firstname: String,
        lastname: String,
        username: String,
        imageBytes: ByteArray?
    ) {
        val user = currentSession()?.user ?: return
        val userId = user.id

        var imageUrl: String? = null

        // upload foto jika ada
        if (imageBytes != null) {
            imageUrl = uploadProfileImage(userId, imageBytes)
        }

        // update row di postgres
        updateProfile(userId, firstname, lastname, username, imageUrl)

        // update metadata auth
        updateAuthMetadata(firstname, lastname, username, imageUrl)
    }


    fun currentSession(): UserSession? = SupabaseHolder.session()
}