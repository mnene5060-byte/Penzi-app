package com.example.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.supabase.SupabaseRepository
import com.example.model.ChatConversation
import com.example.model.ChatMessage
import com.example.model.DatingIntent
import com.example.model.DatingProfile
import com.example.model.Gender
import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

import com.example.model.toDatingProfile
import com.example.model.MeSubScreen
import com.example.model.getFallbackKenyanProfiles

sealed class AuthState {
    object Splash : AuthState()
    object LoggedOut : AuthState()
    object Onboarding : AuthState()
    data class Authenticated(val user: UserAccount) : AuthState()
}

data class DatingUiState(
    val authState: AuthState = AuthState.Splash,
    val currentDeck: List<DatingProfile> = emptyList(),
    val selectedExploreIntent: DatingIntent? = null,
    val exploreProfiles: List<DatingProfile> = emptyList(),
    val matchedProfiles: List<DatingProfile> = emptyList(),
    val likedProfileIds: Set<String> = emptySet(),
    val followingIds: Set<String> = emptySet(),
    val followersIds: Set<String> = emptySet(),
    val friendsIds: Set<String> = emptySet(),
    val activeRelationType: com.example.model.RelationType? = null,
    val activeRelationProfile: DatingProfile? = null,
    val conversations: List<ChatConversation> = emptyList(),
    val activeChatPartnerId: String? = null,
    val selectedProfileDetail: DatingProfile? = null,
    val recentMatchOverlay: DatingProfile? = null,
    val activeMeSubScreen: MeSubScreen? = null,
    val activeTab: String = "discover", // discover, explore, chat, me
    val tabBackStack: List<String> = listOf("discover"),
    val isSupabaseConnected: Boolean = true,
    val isLoading: Boolean = false
) {
    val followingProfiles: List<DatingProfile> get() {
        val allProfiles = (exploreProfiles + currentDeck + com.example.model.getFallbackKenyanProfiles()).distinctBy { it.id }
        return allProfiles.filter { followingIds.contains(it.id) }
    }
    val followersProfiles: List<DatingProfile> get() {
        val allProfiles = (exploreProfiles + currentDeck + com.example.model.getFallbackKenyanProfiles()).distinctBy { it.id }
        return allProfiles.filter { followersIds.contains(it.id) }
    }
    val friendsProfiles: List<DatingProfile> get() {
        val allProfiles = (exploreProfiles + currentDeck + com.example.model.getFallbackKenyanProfiles()).distinctBy { it.id }
        return allProfiles.filter { friendsIds.contains(it.id) }
    }
}

class DatingViewModel : ViewModel() {
    private val supabaseRepo = SupabaseRepository()
    private val _uiState = MutableStateFlow(
        DatingUiState(
            currentDeck = emptyList(),
            exploreProfiles = emptyList()
        )
    )
    val uiState: StateFlow<DatingUiState> = _uiState.asStateFlow()
    private var sessionManager: UserSessionManager? = null

    init {
        loadProfilesFromSupabase()
    }

    fun initSession(context: Context) {
        val isFirstInit = sessionManager == null
        if (isFirstInit) {
            val manager = UserSessionManager(context.applicationContext)
            sessionManager = manager

            supabaseRepo.onTokenUpdated = { access, refresh ->
                manager.saveTokens(access, refresh)
            }

            val savedToken = manager.getSavedAuthToken()
            val savedRefreshToken = manager.getSavedRefreshToken()
            if (!savedToken.isNullOrBlank() || !savedRefreshToken.isNullOrBlank()) {
                supabaseRepo.setTokens(savedToken, savedRefreshToken)
            }
            val savedUser = manager.getSavedUserSession()
            val savedFollowing = manager.getFollowingIds()
            val savedFollowers = manager.getFollowersIds()
            val savedFriends = manager.getFriendsIds()

            if (savedUser != null) {
                _uiState.update {
                    it.copy(
                        authState = AuthState.Authenticated(savedUser),
                        followingIds = savedFollowing,
                        followersIds = savedFollowers,
                        friendsIds = savedFriends,
                        isLoading = false
                    )
                }
                refreshCurrentUserProfile()
            } else {
                _uiState.update {
                    it.copy(
                        authState = AuthState.LoggedOut,
                        followingIds = savedFollowing,
                        followersIds = savedFollowers,
                        friendsIds = savedFriends,
                        isLoading = false
                    )
                }
            }
            loadProfilesFromSupabase()
        }
    }

    fun loadProfilesFromSupabase() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val remoteProfiles = supabaseRepo.fetchProfilesFromSupabase()
            val localCreated = sessionManager?.getCreatedProfilesLocally() ?: emptyList()
            val authUser = (uiState.value.authState as? AuthState.Authenticated)?.user
            val currentUserId = authUser?.id ?: supabaseRepo.getUserId()
            val authUserName = authUser?.name?.trim()

            // Strictly real profiles from Supabase and local created accounts (filtering out current user)
            val realProfiles = (remoteProfiles + localCreated)
                .distinctBy { it.id }
                .filter { it.id != currentUserId && (authUserName.isNullOrBlank() || !it.name.equals(authUserName, ignoreCase = true)) }

            _uiState.update {
                it.copy(
                    currentDeck = realProfiles,
                    exploreProfiles = if (it.selectedExploreIntent != null) {
                        realProfiles.filter { p -> p.intent == it.selectedExploreIntent }
                    } else {
                        realProfiles
                    },
                    isLoading = false
                )
            }
            refreshCurrentUserProfile()
        }
    }

    // Authentication Handlers with Supabase
    fun signInWithEmail(email: String, pass: String, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = supabaseRepo.signInWithSupabase(email, pass)
            result.onSuccess { user ->
                sessionManager?.saveUserSession(user, supabaseRepo.getAuthToken(), supabaseRepo.getRefreshToken())
                _uiState.update {
                    it.copy(
                        authState = AuthState.Authenticated(user),
                        activeMeSubScreen = null,
                        activeTab = "discover",
                        isLoading = false
                    )
                }
                loadProfilesFromSupabase()
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                val msg = exception.message ?: "Account does not exist or invalid credentials."
                onError?.invoke(msg)
            }
        }
    }

    fun startRegistration(email: String, pass: String, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = supabaseRepo.signUpWithSupabase(email, pass)
            result.onSuccess { user ->
                sessionManager?.saveTokens(supabaseRepo.getAuthToken(), supabaseRepo.getRefreshToken())
                _uiState.update {
                    it.copy(
                        authState = AuthState.Onboarding,
                        isLoading = false
                    )
                }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                val msg = exception.message ?: "Registration failed."
                onError?.invoke(msg)
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val user = UserAccount(
                id = supabaseRepo.getUserId(),
                name = "Kenyan Member",
                email = "user@penzi.ke",
                dob = "1998-01-01",
                gender = Gender.FEMALE,
                city = "Nairobi",
                neighborhood = "Westlands",
                occupation = "Member",
                intent = DatingIntent.SERIOUS,
                bio = "Authentic Kenyan connections.",
                interests = listOf("Nairobi Coffee", "Rhumba & Live Bands"),
                photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
                completedOnboarding = true
            )
            sessionManager?.saveUserSession(user)
            supabaseRepo.saveProfileToSupabase(user)
            _uiState.update { it.copy(authState = AuthState.Authenticated(user)) }
            loadProfilesFromSupabase()
        }
    }

    fun completeOnboarding(
        name: String,
        dob: String,
        gender: Gender,
        city: String,
        neighborhood: String,
        occupation: String,
        intent: DatingIntent,
        bio: String,
        interests: List<String>,
        photoUrl: String,
        shengIcebreaker: String = ""
    ) {
        viewModelScope.launch {
            val userId = supabaseRepo.getUserId()
            // Anti-fraud device validation: only 1 welcome reward of 300 coins per physical device
            val (bonusCoins, _) = sessionManager?.claimDeviceWelcomeBonus(userId) ?: Pair(300, true)
            
            val user = UserAccount(
                id = userId,
                name = if (name.isNotBlank()) name else "Kenyan Soul",
                dob = dob,
                gender = gender,
                city = city,
                neighborhood = neighborhood,
                occupation = occupation.ifBlank { "Professional" },
                intent = intent,
                bio = bio.ifBlank { "Excited to connect with authentic people in Kenya 🇰🇪" },
                interests = if (interests.isNotEmpty()) interests else listOf("Nairobi Coffee", "Rhumba & Live Bands"),
                photoUrl = photoUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                completedOnboarding = true,
                coins = bonusCoins,
                diamonds = 500
            )
            val createdProfile = user.toDatingProfile().copy(
                shengIcebreaker = shengIcebreaker.ifBlank { "Habari yako! Mimi ni ${user.name}." }
            )
            sessionManager?.saveUserSession(user, supabaseRepo.getAuthToken(), supabaseRepo.getRefreshToken())
            sessionManager?.saveCreatedProfileLocally(createdProfile)

            // Persist directly to Supabase table
            supabaseRepo.saveProfileToSupabase(user)

            _uiState.update { state ->
                val filteredDeck = state.currentDeck.filter { p -> p.id != user.id }
                state.copy(
                    authState = AuthState.Authenticated(user),
                    currentDeck = filteredDeck,
                    exploreProfiles = filteredDeck,
                    activeMeSubScreen = null,
                    activeTab = "discover"
                )
            }
            loadProfilesFromSupabase()
        }
    }

    fun uploadOnboardingPhoto(
        uriOrUrl: String,
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = supabaseRepo.uploadPhotoToSupabase(uriOrUrl, context)
            result.onSuccess { publicUrl ->
                onSuccess(publicUrl)
            }.onFailure { exception ->
                onError(exception.message ?: "Upload failed")
            }
        }
    }

    fun logout() {
        sessionManager?.clearSession()
        _uiState.update {
            it.copy(
                authState = AuthState.LoggedOut,
                activeTab = "discover",
                tabBackStack = listOf("discover"),
                activeMeSubScreen = null,
                activeChatPartnerId = null,
                selectedProfileDetail = null,
                selectedExploreIntent = null,
                currentDeck = emptyList(),
                conversations = emptyList()
            )
        }
    }

    fun selectTab(tab: String) {
        _uiState.update { state ->
            val updatedStack = if (state.activeTab == tab) {
                state.tabBackStack
            } else {
                state.tabBackStack.filter { it != tab } + tab
            }
            state.copy(
                activeTab = tab,
                activeChatPartnerId = if (tab != "chat") null else state.activeChatPartnerId,
                activeMeSubScreen = if (tab == "me") null else state.activeMeSubScreen,
                selectedProfileDetail = null,
                tabBackStack = updatedStack
            )
        }
    }

    fun openMeSubScreen(subScreen: MeSubScreen) {
        _uiState.update { it.copy(activeMeSubScreen = subScreen) }
    }

    fun closeMeSubScreen() {
        _uiState.update { it.copy(activeMeSubScreen = null) }
    }

    fun openRelationScreen(type: com.example.model.RelationType, targetProfile: DatingProfile? = null) {
        _uiState.update {
            it.copy(
                activeRelationType = type,
                activeRelationProfile = targetProfile
            )
        }
    }

    fun closeRelationScreen() {
        _uiState.update {
            it.copy(
                activeRelationType = null,
                activeRelationProfile = null
            )
        }
    }

    fun toggleFollow(profile: DatingProfile) {
        val currentFollowing = _uiState.value.followingIds.toMutableSet()
        val currentFollowers = _uiState.value.followersIds
        val isNowFollowing = if (currentFollowing.contains(profile.id)) {
            currentFollowing.remove(profile.id)
            false
        } else {
            currentFollowing.add(profile.id)
            true
        }

        val updatedFriends = currentFollowing.intersect(currentFollowers)

        _uiState.update {
            it.copy(
                followingIds = currentFollowing,
                friendsIds = updatedFriends
            )
        }

        // Persist locally
        sessionManager?.saveFollowingIds(currentFollowing)

        // Sync with Supabase asynchronously
        viewModelScope.launch {
            supabaseRepo.syncFollowToSupabase(profile.id, isNowFollowing)
        }
    }

    fun handleBackPress(): Boolean {
        val state = _uiState.value
        when {
            state.activeRelationType != null -> {
                closeRelationScreen()
                return true
            }
            state.activeMeSubScreen != null -> {
                _uiState.update { it.copy(activeMeSubScreen = null) }
                return true
            }
            state.selectedProfileDetail != null -> {
                _uiState.update { it.copy(selectedProfileDetail = null) }
                return true
            }
            state.selectedExploreIntent != null -> {
                _uiState.update { it.copy(selectedExploreIntent = null) }
                return true
            }
            state.activeChatPartnerId != null -> {
                _uiState.update { it.copy(activeChatPartnerId = null) }
                return true
            }
            state.recentMatchOverlay != null -> {
                _uiState.update { it.copy(recentMatchOverlay = null) }
                return true
            }
            state.tabBackStack.size > 1 -> {
                val updatedStack = state.tabBackStack.dropLast(1)
                val previousTab = updatedStack.last()
                _uiState.update {
                    it.copy(
                        activeTab = previousTab,
                        tabBackStack = updatedStack,
                        activeChatPartnerId = if (previousTab != "chat") null else it.activeChatPartnerId
                    )
                }
                return true
            }
            else -> {
                // At root "discover" tab with no screens open, let system handle exit
                return false
            }
        }
    }

    // Like action connected to Supabase - saves like and keeps profile on screen
    fun swipeLike(profile: DatingProfile) {
        val wasLiked = _uiState.value.likedProfileIds.contains(profile.id)
        val newLiked = !wasLiked

        viewModelScope.launch {
            if (newLiked) {
                supabaseRepo.sendLikeToSupabase(profile.id, "LIKE")
            } else {
                supabaseRepo.sendLikeToSupabase(profile.id, "UNLIKE")
            }
        }

        val updatedLikedIds = if (newLiked) {
            _uiState.value.likedProfileIds + profile.id
        } else {
            _uiState.value.likedProfileIds - profile.id
        }

        val isMatch = newLiked
        val updatedMatches = if (isMatch) {
            (_uiState.value.matchedProfiles + profile).distinctBy { it.id }
        } else {
            _uiState.value.matchedProfiles
        }

        val existingConvo = _uiState.value.conversations.find { it.matchProfile.id == profile.id }
        val updatedConvos = if (existingConvo == null && isMatch) {
            _uiState.value.conversations + ChatConversation(
                matchProfile = profile,
                lastMessage = "You liked ${profile.name}! Say Jambo 👋",
                lastTimestamp = "Just now",
                unreadCount = 0,
                messages = listOf(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        senderId = profile.id,
                        text = profile.shengIcebreaker.ifBlank { "Niaje! Habari yako? 👋" },
                        timestamp = getCurrentTimeFormatted(),
                        isFromMe = false
                    )
                )
            )
        } else _uiState.value.conversations

        _uiState.update {
            it.copy(
                likedProfileIds = updatedLikedIds,
                matchedProfiles = updatedMatches,
                conversations = updatedConvos
            )
        }
    }

    fun swipePass(profile: DatingProfile) {
        viewModelScope.launch {
            supabaseRepo.sendLikeToSupabase(profile.id, "PASS")
        }
        val updatedDeck = _uiState.value.currentDeck.filter { it.id != profile.id }
        _uiState.update {
            it.copy(
                currentDeck = updatedDeck
            )
        }
    }

    fun swipeSuperLike(profile: DatingProfile) {
        viewModelScope.launch {
            supabaseRepo.sendLikeToSupabase(profile.id, "SUPER_LIKE")
        }
        swipeLike(profile)
    }

    fun dismissMatchOverlay() {
        _uiState.update { it.copy(recentMatchOverlay = null) }
    }

    fun openProfileDetail(profile: DatingProfile) {
        _uiState.update { it.copy(selectedProfileDetail = profile) }
    }

    fun closeProfileDetail() {
        _uiState.update { it.copy(selectedProfileDetail = null) }
    }

    fun openChatWithMatch(profile: DatingProfile) {
        dismissMatchOverlay()
        val existingConvo = _uiState.value.conversations.find { it.matchProfile.id == profile.id }
        val updatedConvos = if (existingConvo == null) {
            _uiState.value.conversations + ChatConversation(
                matchProfile = profile,
                lastMessage = "",
                lastTimestamp = "Just now",
                unreadCount = 0,
                messages = emptyList()
            )
        } else {
            _uiState.value.conversations
        }
        _uiState.update {
            it.copy(
                activeTab = "chat",
                activeChatPartnerId = profile.id,
                conversations = updatedConvos,
                selectedProfileDetail = null
            )
        }
    }

    // Category / Explore filter
    fun selectExploreCategory(intent: DatingIntent?) {
        val all = _uiState.value.currentDeck
        val filtered = if (intent == null) {
            all
        } else {
            all.filter { it.intent == intent }
        }
        _uiState.update {
            it.copy(
                selectedExploreIntent = intent,
                exploreProfiles = filtered
            )
        }
    }

    // Chat functionality connected to Supabase
    fun openChatConversation(partnerId: String) {
        _uiState.update { it.copy(activeChatPartnerId = partnerId) }
    }

    fun closeChatConversation() {
        _uiState.update { it.copy(activeChatPartnerId = null) }
    }

    fun sendChatMessage(partnerId: String, text: String, imageUrl: String? = null, onInsufficientCoins: (() -> Unit)? = null) {
        if (text.isBlank() && imageUrl == null) return

        val authUser = (_uiState.value.authState as? AuthState.Authenticated)?.user
        if (authUser != null && authUser.gender == Gender.MALE) {
            // Male users charged 15 coins per text
            if (authUser.coins < 15) {
                onInsufficientCoins?.invoke()
                return
            }
            // Deduct 15 coins
            val updatedUser = authUser.copy(coins = (authUser.coins - 15).coerceAtLeast(0))
            updateMyProfile(updatedUser)
        }
        // Female users: 100% free of charge

        val timestamp = getCurrentTimeFormatted()
        val displayText = if (text.isNotBlank()) text.trim() else "📷 Photo"
        val myMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = supabaseRepo.getUserId(),
            text = displayText,
            timestamp = timestamp,
            isFromMe = true,
            imageUrl = imageUrl
        )

        // Save real message to Supabase
        viewModelScope.launch {
            supabaseRepo.sendMessageToSupabase(partnerId, displayText)
        }

        _uiState.update { state ->
            val exists = state.conversations.any { it.matchProfile.id == partnerId }
            val updatedList = if (exists) {
                state.conversations.map { convo ->
                    if (convo.matchProfile.id == partnerId) {
                        convo.copy(
                            lastMessage = displayText,
                            lastTimestamp = timestamp,
                            messages = convo.messages + myMessage
                        )
                    } else convo
                }
            } else {
                val partner = state.currentDeck.find { it.id == partnerId }
                    ?: state.exploreProfiles.find { it.id == partnerId }
                    ?: state.matchedProfiles.find { it.id == partnerId }
                if (partner != null) {
                    state.conversations + ChatConversation(
                        matchProfile = partner,
                        lastMessage = displayText,
                        lastTimestamp = timestamp,
                        unreadCount = 0,
                        messages = listOf(myMessage)
                    )
                } else {
                    state.conversations
                }
            }
            state.copy(conversations = updatedList)
        }
    }

    fun getRandomHiText(profile: DatingProfile): String {
        val firstName = profile.name.split(" ").firstOrNull()?.trim().takeIf { !it.isNullOrBlank() } ?: profile.name
        val greetings = listOf(
            "Hey $firstName! 👋 Hope you're having an amazing day!",
            "Hi $firstName! 😊 Your profile caught my eye, how are you doing?",
            "Sasa $firstName! 👋 Niaje leo? Hope your week is going well!",
            "Hey there! 👋 Just wanted to say hi and introduce myself!",
            "Mambo vipi $firstName! 😊 Couldn't scroll past without saying hello!",
            "Hi $firstName! 👋 Love your vibe and photos! How's your day going?",
            "Niaje $firstName! 👋 Hope you're having a lovely day! Form ni gani?",
            "Hey $firstName! ✨ Happy to connect with you!",
            "Hello $firstName! 😊 Just dropping by to say hi, how's everything?",
            "Hey $firstName! 👋 Your smile on your profile is radiant, had to say hi!",
            "Sasa! 👋 Hope you're having a great time. Would love to get to know you!",
            "Habari yako $firstName! 👋 Wishing you a wonderful day ahead!"
        )
        return greetings.random()
    }

    fun sendSayHi(profile: DatingProfile, openChat: Boolean = true): String {
        dismissMatchOverlay()
        val text = getRandomHiText(profile)
        val timestamp = getCurrentTimeFormatted()
        val myMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = supabaseRepo.getUserId(),
            text = text,
            timestamp = timestamp,
            isFromMe = true
        )

        val authUser = (_uiState.value.authState as? AuthState.Authenticated)?.user
        if (authUser != null && authUser.gender == Gender.MALE && authUser.coins >= 15) {
            val updatedUser = authUser.copy(coins = (authUser.coins - 15).coerceAtLeast(0))
            updateMyProfile(updatedUser)
        }

        // Save real message to Supabase
        viewModelScope.launch {
            supabaseRepo.sendMessageToSupabase(profile.id, text)
        }

        val existingConvo = _uiState.value.conversations.find { it.matchProfile.id == profile.id }
        val updatedConvos = if (existingConvo == null) {
            _uiState.value.conversations + ChatConversation(
                matchProfile = profile,
                lastMessage = text,
                lastTimestamp = timestamp,
                unreadCount = 0,
                messages = listOf(myMessage)
            )
        } else {
            _uiState.value.conversations.map { convo ->
                if (convo.matchProfile.id == profile.id) {
                    convo.copy(
                        lastMessage = text,
                        lastTimestamp = timestamp,
                        messages = convo.messages + myMessage
                    )
                } else convo
            }
        }

        val updatedMatched = if (_uiState.value.matchedProfiles.any { it.id == profile.id }) {
            _uiState.value.matchedProfiles
        } else {
            _uiState.value.matchedProfiles + profile
        }

        _uiState.update { state ->
            state.copy(
                conversations = updatedConvos,
                matchedProfiles = updatedMatched,
                activeTab = if (openChat) "chat" else state.activeTab,
                activeChatPartnerId = if (openChat) profile.id else state.activeChatPartnerId,
                selectedProfileDetail = if (openChat) null else state.selectedProfileDetail,
                recentMatchOverlay = null
            )
        }

        return text
    }

    fun verifyCurrentUser() {
        val authUser = (_uiState.value.authState as? AuthState.Authenticated)?.user ?: return
        val verifiedUser = authUser.copy(isVerified = true)
        updateMyProfile(verifiedUser)
    }

    fun addCoins(amount: Int) {
        val authUser = (_uiState.value.authState as? AuthState.Authenticated)?.user ?: return
        val newCoins = authUser.coins + amount
        val updatedUser = authUser.copy(coins = newCoins)
        updateMyProfile(updatedUser)
    }

    fun refreshCurrentUserProfile() {
        val authUser = (_uiState.value.authState as? AuthState.Authenticated)?.user ?: return
        viewModelScope.launch {
            val remoteProfile = supabaseRepo.fetchUserProfile(authUser.id)
            if (remoteProfile != null) {
                val updatedUser = authUser.copy(
                    coins = remoteProfile.coins,
                    diamonds = remoteProfile.diamonds,
                    isVerified = remoteProfile.verified,
                    name = if (remoteProfile.name.isNotBlank()) remoteProfile.name else authUser.name,
                    bio = if (remoteProfile.bio.isNotBlank()) remoteProfile.bio else authUser.bio
                )
                sessionManager?.saveUserSession(updatedUser, supabaseRepo.getAuthToken(), supabaseRepo.getRefreshToken())
                _uiState.update { state ->
                    if (state.authState is AuthState.Authenticated) {
                        state.copy(authState = AuthState.Authenticated(updatedUser))
                    } else state
                }
            }
        }
    }

    fun updateMyProfile(updatedUser: UserAccount) {
        viewModelScope.launch {
            supabaseRepo.saveProfileToSupabase(updatedUser)
        }
        sessionManager?.saveUserSession(updatedUser, supabaseRepo.getAuthToken(), supabaseRepo.getRefreshToken())
        _uiState.update { it.copy(authState = AuthState.Authenticated(updatedUser)) }
    }

    private fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
