package com.example.data.supabase

import android.util.Log
import com.example.model.ChatMessage
import com.example.model.DatingIntent
import com.example.model.DatingProfile
import com.example.model.Gender
import com.example.model.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class SupabaseRepository {
    private val api = SupabaseClient.api
    private var currentAuthToken: String? = null
    private var currentRefreshToken: String? = null
    private var currentUserId: String = UUID.randomUUID().toString()

    var onTokenUpdated: ((accessToken: String, refreshToken: String) -> Unit)? = null

    fun getUserId(): String = currentUserId
    fun getAuthToken(): String? = currentAuthToken
    fun getRefreshToken(): String? = currentRefreshToken

    fun setAuthToken(token: String?) {
        if (!token.isNullOrBlank()) {
            currentAuthToken = token
        }
    }

    fun setTokens(authToken: String?, refreshToken: String?) {
        if (!authToken.isNullOrBlank()) {
            currentAuthToken = authToken
        }
        if (!refreshToken.isNullOrBlank()) {
            currentRefreshToken = refreshToken
        }
    }

    private fun getAuthHeader(): String {
        return if (!currentAuthToken.isNullOrBlank()) {
            "Bearer $currentAuthToken"
        } else {
            "Bearer ${SupabaseConfig.ANON_KEY}"
        }
    }

    suspend fun refreshAuthToken(): Boolean = withContext(Dispatchers.IO) {
        val rToken = currentRefreshToken
        if (rToken.isNullOrBlank()) return@withContext false
        try {
            val response = api.refreshToken(
                body = SupabaseRefreshTokenRequest(refreshToken = rToken)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                currentAuthToken = body.accessToken
                if (!body.refreshToken.isNullOrBlank()) {
                    currentRefreshToken = body.refreshToken
                }
                if (!currentAuthToken.isNullOrBlank()) {
                    onTokenUpdated?.invoke(currentAuthToken!!, currentRefreshToken ?: rToken)
                }
                Log.i("SupabaseRepo", "Successfully refreshed Supabase auth token")
                true
            } else {
                Log.w("SupabaseRepo", "Failed to refresh token: ${response.code()} ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Exception refreshing token: ${e.message}")
            false
        }
    }

    suspend fun signUpWithSupabase(email: String, pass: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        try {
            // Check if user already exists by attempting sign in or checking auth status
            val signInCheck = api.signInWithPassword(
                body = SupabaseAuthRequest(email = email, password = pass)
            )
            if (signInCheck.isSuccessful && signInCheck.body()?.user != null) {
                val existingBody = signInCheck.body()!!
                currentAuthToken = existingBody.accessToken
                currentRefreshToken = existingBody.refreshToken
                currentUserId = existingBody.user?.id ?: currentUserId
                return@withContext Result.failure(Exception("An account with this email already exists. Please tap 'Log In' instead."))
            }

            val response = api.signUp(
                body = SupabaseAuthRequest(
                    email = email,
                    password = pass,
                    data = mapOf("app" to "penzi_kenya")
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // In Supabase, if account already exists, body.accessToken is null or user.identities is empty
                if (body.accessToken == null && body.user?.id != null) {
                    return@withContext Result.failure(Exception("An account with this email already exists. Please tap 'Log In' instead."))
                }
                currentAuthToken = body.accessToken
                currentRefreshToken = body.refreshToken
                val userId = body.user?.id ?: currentUserId
                currentUserId = userId
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                Result.success(
                    UserAccount(
                        id = userId,
                        name = name,
                        email = email,
                        completedOnboarding = false
                    )
                )
            } else {
                val errorStr = response.errorBody()?.string() ?: ""
                Log.w("SupabaseRepo", "Supabase SignUp response: ${response.code()} $errorStr")
                Result.failure(Exception("An account with this email already exists. Please tap 'Log In' instead."))
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepo", "Supabase SignUp exception: ${e.message}", e)
            Result.failure(Exception("Sign up failed. If you already have an account, please tap 'Log In'."))
        }
    }

    suspend fun signInWithSupabase(email: String, pass: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        try {
            val response = api.signInWithPassword(
                body = SupabaseAuthRequest(email = email, password = pass)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                currentAuthToken = body.accessToken
                currentRefreshToken = body.refreshToken
                val userId = body.user?.id ?: currentUserId
                currentUserId = userId
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                Result.success(
                    UserAccount(
                        id = userId,
                        name = name,
                        email = email,
                        completedOnboarding = true
                    )
                )
            } else {
                val errorStr = response.errorBody()?.string() ?: ""
                Log.w("SupabaseRepo", "Supabase SignIn failed (${response.code()}): $errorStr")
                Result.failure(Exception("Account does not exist or password incorrect. Please tap 'Sign Up' if you are new."))
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepo", "Supabase SignIn error: ${e.message}", e)
            Result.failure(Exception("Login failed. Account does not exist or credentials invalid."))
        }
    }

    suspend fun saveProfileToSupabase(user: UserAccount): Boolean = withContext(Dispatchers.IO) {
        try {
            val dto = SupabaseProfileDto(
                id = user.id,
                name = user.name,
                age = user.age,
                dob = user.dob,
                gender = user.gender.name,
                city = user.city,
                neighborhood = user.neighborhood,
                occupation = user.occupation,
                intent = user.intent.name,
                bio = user.bio,
                shengIcebreaker = "Habari yako! Glad to connect.",
                interests = user.interests,
                imageUrls = if (user.photoUrl.isNotBlank()) listOf(user.photoUrl) else emptyList(),
                verified = user.isVerified,
                distanceKm = 3,
                country = user.country,
                social = user.social,
                education = user.education,
                height = user.height,
                relationshipStatus = user.relationshipStatus,
                languages = user.languages,
                musicPreference = user.musicPreference,
                dietaryHabit = user.dietaryHabit,
                sleepHabit = user.sleepHabit,
                smoking = user.smoking,
                liquor = user.liquor,
                superpower = user.superpower,
                albumUrls = user.albumUrls,
                coins = user.coins,
                diamonds = user.diamonds
            )
            var response = api.upsertProfile(
                authorization = getAuthHeader(),
                profile = dto
            )
            if (response.code() == 401) {
                val refreshed = refreshAuthToken()
                if (refreshed) {
                    response = api.upsertProfile(
                        authorization = getAuthHeader(),
                        profile = dto
                    )
                }
            }
            if (response.code() == 401 || !response.isSuccessful) {
                // Fallback with anon key
                response = api.upsertProfile(
                    authorization = "Bearer ${SupabaseConfig.ANON_KEY}",
                    profile = dto
                )
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SupabaseRepo", "Failed to sync profile with Supabase: ${e.message}")
            false
        }
    }

    suspend fun fetchUserProfile(userId: String): SupabaseProfileDto? = withContext(Dispatchers.IO) {
        try {
            var response = api.getProfileById(
                authorization = getAuthHeader(),
                idFilter = "eq.$userId"
            )
            if (response.code() == 401) {
                val refreshed = refreshAuthToken()
                if (refreshed) {
                    response = api.getProfileById(
                        authorization = getAuthHeader(),
                        idFilter = "eq.$userId"
                    )
                }
            }
            if (response.code() == 401 || !response.isSuccessful) {
                response = api.getProfileById(
                    authorization = "Bearer ${SupabaseConfig.ANON_KEY}",
                    idFilter = "eq.$userId"
                )
            }
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                response.body()!!.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Error fetching user profile for $userId: ${e.message}")
            null
        }
    }

    suspend fun fetchProfilesFromSupabase(): List<DatingProfile> = withContext(Dispatchers.IO) {
        try {
            var response = api.getProfiles(
                authorization = getAuthHeader()
            )
            // If 401 Unauthorized (e.g. JWT token expired after 1 hour), attempt refreshing token
            if (response.code() == 401) {
                val refreshed = refreshAuthToken()
                if (refreshed) {
                    response = api.getProfiles(authorization = getAuthHeader())
                }
            }
            // If still 401, fallback to anon key header so public profiles are always loaded
            if (response.code() == 401) {
                Log.w("SupabaseRepo", "Retrying getProfiles with anon key fallback")
                response = api.getProfiles(authorization = "Bearer ${SupabaseConfig.ANON_KEY}")
            }
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.map { dto ->
                    DatingProfile(
                        id = dto.id,
                        name = dto.name,
                        age = dto.age,
                        dob = dto.dob,
                        gender = try { Gender.valueOf(dto.gender) } catch (e: Exception) { Gender.FEMALE },
                        city = dto.city,
                        neighborhood = dto.neighborhood,
                        occupation = dto.occupation,
                        intent = try { DatingIntent.valueOf(dto.intent) } catch (e: Exception) { DatingIntent.SERIOUS },
                        bio = dto.bio,
                        shengIcebreaker = dto.shengIcebreaker,
                        interests = dto.interests,
                        imageUrls = dto.imageUrls,
                        verified = dto.verified,
                        distanceKm = dto.distanceKm,
                        isOnline = dto.isOnline,
                        lastSeenAt = dto.lastSeenAt
                    )
                }
            } else {
                Log.w("SupabaseRepo", "fetchProfiles failed with code: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Error fetching profiles: ${e.message}")
            emptyList()
        }
    }

    suspend fun sendLikeToSupabase(profileId: String, type: String = "LIKE") = withContext(Dispatchers.IO) {
        try {
            var response = api.recordLike(
                authorization = getAuthHeader(),
                like = SupabaseLikeDto(
                    userId = currentUserId,
                    targetProfileId = profileId,
                    type = type
                )
            )
            if (response.code() == 401) {
                if (refreshAuthToken()) {
                    api.recordLike(
                        authorization = getAuthHeader(),
                        like = SupabaseLikeDto(
                            userId = currentUserId,
                            targetProfileId = profileId,
                            type = type
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Error sending like to Supabase: ${e.message}")
        }
    }

    suspend fun syncFollowToSupabase(targetProfileId: String, isFollow: Boolean) = withContext(Dispatchers.IO) {
        try {
            if (isFollow) {
                val followDto = com.example.data.supabase.SupabaseFollowDto(
                    followerId = currentUserId,
                    followingId = targetProfileId
                )
                var response = api.recordFollow(
                    authorization = getAuthHeader(),
                    follow = followDto
                )
                if (response.code() == 401) {
                    if (refreshAuthToken()) {
                        api.recordFollow(authorization = getAuthHeader(), follow = followDto)
                    }
                }
            } else {
                var response = api.removeFollow(
                    authorization = getAuthHeader(),
                    followerId = "eq.$currentUserId",
                    followingId = "eq.$targetProfileId"
                )
                if (response.code() == 401) {
                    if (refreshAuthToken()) {
                        api.removeFollow(
                            authorization = getAuthHeader(),
                            followerId = "eq.$currentUserId",
                            followingId = "eq.$targetProfileId"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Error syncing follow state with Supabase: ${e.message}")
        }
    }

    suspend fun sendMessageToSupabase(receiverId: String, text: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val msgDto = SupabaseMessageDto(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId,
                receiverId = receiverId,
                text = text
            )
            var response = api.sendMessage(
                authorization = getAuthHeader(),
                message = msgDto
            )
            if (response.code() == 401) {
                if (refreshAuthToken()) {
                    response = api.sendMessage(
                        authorization = getAuthHeader(),
                        message = msgDto
                    )
                }
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Error sending message to Supabase: ${e.message}")
            false
        }
    }

    private suspend fun createPhotosBucket() = withContext(Dispatchers.IO) {
        try {
            val bucketUrl = "${SupabaseConfig.URL}/storage/v1/bucket"
            val client = okhttp3.OkHttpClient()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val json = """{"id": "photos", "name": "photos", "public": true}"""
            val requestBody = json.toRequestBody(mediaType)

            val request = okhttp3.Request.Builder()
                .url(bucketUrl)
                .post(requestBody)
                .addHeader("apikey", SupabaseConfig.ANON_KEY)
                .addHeader("Authorization", getAuthHeader())
                .build()

            val response = client.newCall(request).execute()
            Log.i("SupabaseRepo", "Bucket photos creation status: ${response.code}")
            response.close()
        } catch (e: Exception) {
            Log.w("SupabaseRepo", "Failed to create bucket: ${e.message}")
        }
    }

    private fun getBytesFromUriOrUrl(source: String, context: android.content.Context): ByteArray? {
        return try {
            if (source.startsWith("http://") || source.startsWith("https://")) {
                val client = okhttp3.OkHttpClient()
                val request = okhttp3.Request.Builder().url(source).build()
                val response = client.newCall(request).execute()
                val bytes = response.body?.bytes()
                response.close()
                bytes
            } else {
                val uri = android.net.Uri.parse(source)
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepo", "Error getting image bytes: ${e.message}")
            null
        }
    }

    suspend fun uploadPhotoToSupabase(imageUriOrUrl: String, context: android.content.Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Create the photos bucket if not exists
            createPhotosBucket()

            // 2. Read raw bytes
            val bytes = getBytesFromUriOrUrl(imageUriOrUrl, context)
            if (bytes == null) {
                Log.e("SupabaseRepo", "Could not fetch/read image bytes for $imageUriOrUrl")
                return@withContext Result.failure(Exception("Could not read image data"))
            }

            // 3. Upload raw bytes to photos storage bucket
            val filename = "profile_${UUID.randomUUID()}.jpg"
            val uploadUrl = "${SupabaseConfig.URL}/storage/v1/object/photos/$filename"

            val client = okhttp3.OkHttpClient()
            val mediaType = "image/jpeg".toMediaTypeOrNull()
            val requestBody = bytes.toRequestBody(mediaType)

            val request = okhttp3.Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .addHeader("apikey", SupabaseConfig.ANON_KEY)
                .addHeader("Authorization", getAuthHeader())
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.close()
                val publicUrl = "${SupabaseConfig.URL}/storage/v1/object/public/photos/$filename"
                Log.i("SupabaseRepo", "Uploaded successfully! Public URL: $publicUrl")
                Result.success(publicUrl)
            } else {
                val errBody = response.body?.string() ?: ""
                Log.w("SupabaseRepo", "Upload failed: ${response.code} $errBody")
                response.close()
                Result.failure(Exception("Upload failed with code ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("SupabaseRepo", "Upload photo exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
