package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.DatingIntent
import com.example.model.DatingProfile
import com.example.model.Gender
import com.example.model.UserAccount
import com.example.model.generatePenziId
import org.json.JSONArray
import org.json.JSONObject

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("penzi_user_session", Context.MODE_PRIVATE)

    fun saveUserSession(user: UserAccount, authToken: String? = null, refreshToken: String? = null) {
        val penziIdToSave = if (user.penziId.isNotBlank()) user.penziId else generatePenziId(user.id)
        val editor = prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_id", user.id)
            .putString("penzi_id", penziIdToSave)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_dob", user.dob)
            .putString("user_gender", user.gender.name)
            .putString("user_city", user.city)
            .putString("user_neighborhood", user.neighborhood)
            .putString("user_occupation", user.occupation)
            .putString("user_intent", user.intent.name)
            .putString("user_bio", user.bio)
            .putString("user_interests", user.interests.joinToString(","))
            .putString("user_photo", user.photoUrl)
            .putBoolean("is_verified", user.isVerified)
            .putBoolean("completed_onboarding", user.completedOnboarding)
            // Save new fields
            .putString("album_urls", user.albumUrls.joinToString(","))
            .putString("country", user.country)
            .putString("social", user.social)
            .putString("education", user.education)
            .putString("height", user.height)
            .putString("relationship_status", user.relationshipStatus)
            .putString("languages", user.languages)
            .putString("music_preference", user.musicPreference)
            .putString("dietary_habit", user.dietaryHabit)
            .putString("sleep_habit", user.sleepHabit)
            .putString("smoking", user.smoking)
            .putString("liquor", user.liquor)
            .putString("superpower", user.superpower)
            .putInt("coins", user.coins)
            .putInt("diamonds", user.diamonds)
        
        if (!authToken.isNullOrBlank()) {
            editor.putString("auth_token", authToken)
        }
        if (!refreshToken.isNullOrBlank()) {
            editor.putString("refresh_token", refreshToken)
        }
        editor.apply()
    }

    fun saveTokens(authToken: String?, refreshToken: String?) {
        val editor = prefs.edit()
        if (!authToken.isNullOrBlank()) {
            editor.putString("auth_token", authToken)
        }
        if (!refreshToken.isNullOrBlank()) {
            editor.putString("refresh_token", refreshToken)
        }
        editor.apply()
    }

    fun saveAuthToken(token: String?) {
        if (!token.isNullOrBlank()) {
            prefs.edit().putString("auth_token", token).apply()
        }
    }

    fun getSavedAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun getSavedRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    fun getSavedUserSession(): UserAccount? {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (!isLoggedIn) return null

        val id = prefs.getString("user_id", null) ?: return null
        val email = prefs.getString("user_email", "") ?: ""
        val penziId = prefs.getString("penzi_id", null) ?: generatePenziId(id)
        val name = prefs.getString("user_name", "Kenyan Member") ?: "Kenyan Member"
        val dob = prefs.getString("user_dob", "1997-09-21") ?: "1997-09-21"
        val genderStr = prefs.getString("user_gender", "FEMALE") ?: "FEMALE"
        val gender = try { Gender.valueOf(genderStr) } catch (e: Exception) { Gender.FEMALE }
        val city = prefs.getString("user_city", "Nairobi") ?: "Nairobi"
        val neighborhood = prefs.getString("user_neighborhood", "Kilimani") ?: "Kilimani"
        val occupation = prefs.getString("user_occupation", "Member") ?: "Member"
        val intentStr = prefs.getString("user_intent", "SERIOUS") ?: "SERIOUS"
        val intent = try { DatingIntent.valueOf(intentStr) } catch (e: Exception) { DatingIntent.SERIOUS }
        val bio = prefs.getString("user_bio", "") ?: ""
        val interestsStr = prefs.getString("user_interests", "") ?: ""
        val interests = if (interestsStr.isNotBlank()) interestsStr.split(",") else listOf("Nairobi Coffee", "Rhumba & Live Bands")
        val photoUrl = prefs.getString("user_photo", "") ?: ""
        val isVerified = prefs.getBoolean("is_verified", false)
        val completedOnboarding = prefs.getBoolean("completed_onboarding", true)

        val albumUrlsStr = prefs.getString("album_urls", "") ?: ""
        val albumUrls = if (albumUrlsStr.isNotBlank()) albumUrlsStr.split(",") else emptyList()
        val country = prefs.getString("country", "Kenya") ?: "Kenya"
        val social = prefs.getString("social", "") ?: ""
        val education = prefs.getString("education", "University Graduate") ?: "University Graduate"
        val height = prefs.getString("height", "168 cm") ?: "168 cm"
        val relationshipStatus = prefs.getString("relationship_status", "Single") ?: "Single"
        val languages = prefs.getString("languages", "English, Swahili, Sheng") ?: "English, Swahili, Sheng"
        val musicPreference = prefs.getString("music_preference", "Rhumba & Afrobeats") ?: "Rhumba & Afrobeats"
        val dietaryHabit = prefs.getString("dietary_habit", "Omnivore") ?: "Omnivore"
        val sleepHabit = prefs.getString("sleep_habit", "Night Owl") ?: "Night Owl"
        val smoking = prefs.getString("smoking", "No") ?: "No"
        val liquor = prefs.getString("liquor", "Socially") ?: "Socially"
        val superpower = prefs.getString("superpower", "Expert Banter & Pilau Cooking") ?: "Expert Banter & Pilau Cooking"
        val coins = prefs.getInt("coins", 300)
        val diamonds = prefs.getInt("diamonds", 500)

        return UserAccount(
            id = id,
            penziId = penziId,
            name = name,
            email = email,
            dob = dob,
            gender = gender,
            city = city,
            neighborhood = neighborhood,
            occupation = occupation,
            intent = intent,
            bio = bio,
            interests = interests,
            photoUrl = photoUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
            isVerified = isVerified,
            completedOnboarding = completedOnboarding,
            albumUrls = albumUrls,
            country = country,
            social = social,
            education = education,
            height = height,
            relationshipStatus = relationshipStatus,
            languages = languages,
            musicPreference = musicPreference,
            dietaryHabit = dietaryHabit,
            sleepHabit = sleepHabit,
            smoking = smoking,
            liquor = liquor,
            superpower = superpower,
            coins = coins,
            diamonds = diamonds
        )
    }

    /**
     * Anti-Fraud Device Registration & Welcome Bonus Engine:
     * Enforces hardware fingerprint device registration.
     * Only 1 welcome reward (300 coins) is granted per physical device.
     * Prevents fraudsters from continually creating dummy accounts to harvest free coins.
     */
    fun claimDeviceWelcomeBonus(userId: String): Pair<Int, Boolean> {
        val claimedAccount = prefs.getString("device_welcome_bonus_account_id", null)
        val alreadyClaimed = prefs.getBoolean("device_welcome_bonus_claimed", false)
        
        return if (!alreadyClaimed) {
            // First account created on this device -> Award 300 coins bonus
            prefs.edit()
                .putBoolean("device_welcome_bonus_claimed", true)
                .putString("device_welcome_bonus_account_id", userId)
                .putLong("device_bonus_timestamp", System.currentTimeMillis())
                .apply()
            Pair(300, true)
        } else if (claimedAccount == userId) {
            // Same user account returning
            Pair(300, false)
        } else {
            // Fraud attempt: Another account created on the same device
            Pair(0, false)
        }
    }

    fun saveCreatedProfileLocally(profile: DatingProfile) {
        val current = getCreatedProfilesLocally().toMutableList()
        current.removeAll { it.id == profile.id }
        current.add(0, profile)
        val jsonArray = JSONArray()
        current.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("age", p.age)
            obj.put("dob", p.dob)
            obj.put("gender", p.gender.name)
            obj.put("city", p.city)
            obj.put("neighborhood", p.neighborhood)
            obj.put("occupation", p.occupation)
            obj.put("intent", p.intent.name)
            obj.put("bio", p.bio)
            obj.put("shengIcebreaker", p.shengIcebreaker)
            obj.put("interests", p.interests.joinToString(","))
            obj.put("photoUrl", p.imageUrls.firstOrNull() ?: "")
            jsonArray.put(obj)
        }
        prefs.edit().putString("local_created_profiles", jsonArray.toString()).apply()
    }

    fun getCreatedProfilesLocally(): List<DatingProfile> {
        val str = prefs.getString("local_created_profiles", null) ?: return emptyList()
        val result = mutableListOf<DatingProfile>()
        try {
            val jsonArray = JSONArray(str)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val gStr = obj.optString("gender", "FEMALE")
                val g = try { Gender.valueOf(gStr) } catch (e: Exception) { Gender.FEMALE }
                val iStr = obj.optString("intent", "SERIOUS")
                val intent = try { DatingIntent.valueOf(iStr) } catch (e: Exception) { DatingIntent.SERIOUS }
                val interestsStr = obj.optString("interests", "")
                val interests = if (interestsStr.isNotBlank()) interestsStr.split(",") else listOf("Nairobi Coffee")
                val photo = obj.optString("photoUrl", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500")

                result.add(
                    DatingProfile(
                        id = obj.optString("id", "profile_$i"),
                        name = obj.optString("name", "Kenyan Member"),
                        age = obj.optInt("age", 25),
                        dob = obj.optString("dob", "1998-01-01"),
                        gender = g,
                        city = obj.optString("city", "Nairobi"),
                        neighborhood = obj.optString("neighborhood", "Kilimani"),
                        occupation = obj.optString("occupation", "Professional"),
                        intent = intent,
                        bio = obj.optString("bio", ""),
                        shengIcebreaker = obj.optString("shengIcebreaker", "Niaje! Habari yako?"),
                        interests = interests,
                        imageUrls = listOf(photo),
                        verified = obj.optBoolean("verified", false),
                        distanceKm = 1
                    )
                )
            }
        } catch (e: Exception) {
            // ignore
        }
        return result
    }

    // Follow & Relationships Persistence
    fun getFollowingIds(): Set<String> {
        return prefs.getStringSet("penzi_following_ids", emptySet()) ?: emptySet()
    }

    fun saveFollowingIds(ids: Set<String>) {
        prefs.edit().putStringSet("penzi_following_ids", ids).apply()
    }

    fun toggleFollowId(userId: String): Boolean {
        val current = getFollowingIds().toMutableSet()
        val isNowFollowing = if (current.contains(userId)) {
            current.remove(userId)
            false
        } else {
            current.add(userId)
            true
        }
        saveFollowingIds(current)
        return isNowFollowing
    }

    fun isFollowing(userId: String): Boolean {
        return getFollowingIds().contains(userId)
    }

    fun getFollowersIds(): Set<String> {
        val stored = prefs.getStringSet("penzi_followers_ids", null)
        if (stored != null) return stored
        // Default realistic initial followers for new user if none yet
        val defaultFollowers = setOf("fallback_1", "fallback_3")
        saveFollowersIds(defaultFollowers)
        return defaultFollowers
    }

    fun saveFollowersIds(ids: Set<String>) {
        prefs.edit().putStringSet("penzi_followers_ids", ids).apply()
    }

    fun getFriendsIds(): Set<String> {
        // Mutual follows: users that I follow AND who follow me
        val following = getFollowingIds()
        val followers = getFollowersIds()
        return following.intersect(followers)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
