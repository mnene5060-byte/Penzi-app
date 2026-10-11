package com.example.data.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object SupabaseConfig {
    const val URL: String = "https://vghvjlsnuvkbmhnymlvf.supabase.co"
    const val ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZnaHZqbHNudXZrYm1obnltbHZmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODc1ODM1NjksImV4cCI6MjEwMzE1OTU2OX0.smyzOP8gwlfW1YWEbYj3euh4Pqm6-t_DEqT7Hp_OqiI"
}

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "data") val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseRefreshTokenRequest(
    @Json(name = "refresh_token") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "user") val user: SupabaseUser? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseProfileDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "age") val age: Int = 25,
    @Json(name = "dob") val dob: String = "1998-01-01",
    @Json(name = "gender") val gender: String = "FEMALE",
    @Json(name = "city") val city: String = "Nairobi",
    @Json(name = "neighborhood") val neighborhood: String = "Kilimani",
    @Json(name = "occupation") val occupation: String = "Professional",
    @Json(name = "intent") val intent: String = "SERIOUS",
    @Json(name = "bio") val bio: String = "",
    @Json(name = "sheng_icebreaker") val shengIcebreaker: String = "",
    @Json(name = "interests") val interests: List<String> = emptyList(),
    @Json(name = "image_urls") val imageUrls: List<String> = emptyList(),
    @Json(name = "verified") val verified: Boolean = false,
    @Json(name = "distance_km") val distanceKm: Int = 5,
    @Json(name = "is_online") val isOnline: Boolean = true,
    @Json(name = "last_seen_at") val lastSeenAt: String? = null,
    
    // Add custom storage fields
    @Json(name = "country") val country: String = "Kenya",
    @Json(name = "social") val social: String = "",
    @Json(name = "education") val education: String = "University Graduate",
    @Json(name = "height") val height: String = "168 cm",
    @Json(name = "relationship_status") val relationshipStatus: String = "Single",
    @Json(name = "languages") val languages: String = "English, Swahili, Sheng",
    @Json(name = "music_preference") val musicPreference: String = "Rhumba & Afrobeats",
    @Json(name = "dietary_habit") val dietaryHabit: String = "Omnivore",
    @Json(name = "sleep_habit") val sleepHabit: String = "Night Owl",
    @Json(name = "smoking") val smoking: String = "No",
    @Json(name = "liquor") val liquor: String = "Socially",
    @Json(name = "superpower") val superpower: String = "Expert Banter & Pilau Cooking",
    @Json(name = "album_urls") val albumUrls: List<String> = emptyList(),
    @Json(name = "coins") val coins: Int = 300,
    @Json(name = "diamonds") val diamonds: Int = 500
)

@JsonClass(generateAdapter = true)
data class SupabaseMessageDto(
    @Json(name = "id") val id: String,
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "receiver_id") val receiverId: String,
    @Json(name = "text") val text: String,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseLikeDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "target_profile_id") val targetProfileId: String,
    @Json(name = "type") val type: String = "LIKE"
)

@JsonClass(generateAdapter = true)
data class SupabaseFollowDto(
    @Json(name = "follower_id") val followerId: String,
    @Json(name = "following_id") val followingId: String,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseToggleFollowRequest(
    @Json(name = "target_id") val targetId: String
)

@JsonClass(generateAdapter = true)
data class SupabaseFollowStatsResponse(
    @Json(name = "following_count") val followingCount: Int = 0,
    @Json(name = "followers_count") val followersCount: Int = 0,
    @Json(name = "friends_count") val friendsCount: Int = 0,
    @Json(name = "is_following") val isFollowing: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SupabaseClaimTaskRequest(
    @Json(name = "task_id") val taskId: String,
    @Json(name = "client_timezone") val clientTimezone: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseClaimTaskResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "already_claimed") val alreadyClaimed: Boolean = false,
    @Json(name = "task_id") val taskId: String? = null,
    @Json(name = "coins_reward") val coinsReward: Int = 0,
    @Json(name = "new_streak") val newStreak: Int = 1,
    @Json(name = "server_date") val serverDate: String? = null,
    @Json(name = "message") val message: String? = null
)

interface SupabaseApiService {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer ${SupabaseConfig.ANON_KEY}",
        @Body body: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    suspend fun signInWithPassword(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer ${SupabaseConfig.ANON_KEY}",
        @Body body: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshToken(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String = "Bearer ${SupabaseConfig.ANON_KEY}",
        @Body body: SupabaseRefreshTokenRequest
    ): Response<SupabaseAuthResponse>

    @GET("rest/v1/profiles?select=*")
    suspend fun getProfiles(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String
    ): Response<List<SupabaseProfileDto>>

    @GET("rest/v1/profiles?select=*")
    suspend fun getProfileById(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Query("id") idFilter: String
    ): Response<List<SupabaseProfileDto>>

    @Headers(
        "Content-Type: application/json",
        "Prefer: return=representation,resolution=merge-duplicates"
    )
    @POST("rest/v1/profiles")
    suspend fun upsertProfile(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Body profile: SupabaseProfileDto
    ): Response<List<SupabaseProfileDto>>

    @Headers(
        "Content-Type: application/json",
        "Prefer: return=minimal"
    )
    @POST("rest/v1/likes")
    suspend fun recordLike(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Body like: SupabaseLikeDto
    ): Response<Unit>

    @Headers(
        "Content-Type: application/json",
        "Prefer: return=minimal"
    )
    @POST("rest/v1/follows")
    suspend fun recordFollow(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Body follow: SupabaseFollowDto
    ): Response<Unit>

    @DELETE("rest/v1/follows")
    suspend fun removeFollow(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @retrofit2.http.Query("follower_id") followerId: String,
        @retrofit2.http.Query("following_id") followingId: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("rest/v1/rpc/toggle_follow")
    suspend fun rpcToggleFollow(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Body body: SupabaseToggleFollowRequest
    ): Response<SupabaseFollowStatsResponse>

    @GET("rest/v1/messages?select=*&order=created_at.asc")
    suspend fun getMessages(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String
    ): Response<List<SupabaseMessageDto>>

    @Headers(
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    @POST("rest/v1/messages")
    suspend fun sendMessage(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Body message: SupabaseMessageDto
    ): Response<List<SupabaseMessageDto>>

    @Headers("Content-Type: application/json")
    @POST("functions/v1/claim-task")
    suspend fun claimTask(
        @Header("apikey") apiKey: String = SupabaseConfig.ANON_KEY,
        @Header("Authorization") authorization: String,
        @Body body: SupabaseClaimTaskRequest
    ): Response<SupabaseClaimTaskResponse>
}

object SupabaseClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: SupabaseApiService by lazy {
        val baseUrl = if (SupabaseConfig.URL.endsWith("/")) SupabaseConfig.URL else "${SupabaseConfig.URL}/"
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}
