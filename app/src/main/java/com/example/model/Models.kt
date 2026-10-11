package com.example.model

enum class DatingIntent(val label: String, val emoji: String, val tagline: String, val badgeColorHex: Long) {
    SERIOUS("Serious / Ndoa Vibes 💍", "💍", "Ready for long term & marriage", 0xFFC82A2A),
    CASUAL("Casual & Fun ✨", "✨", "Let's vibe & see where it goes", 0xFFF5A623),
    COFFEE("Kahawa & Convo ☕", "☕", "Good banter over Nairobi coffee", 0xFF8D6E63),
    ADVENTURE("Safari & Hiking 🏕️", "🏕️", "Ngong hills, road trips, Diani beach", 0xFF2E7D32),
    NIGHTLIFE("Nairobi Sherehe 🥂", "🥂", "Brunch, Rhumba & Westlands vibes", 0xFFE91E63)
}

enum class Gender(val label: String) {
    FEMALE("Woman"),
    MALE("Man"),
    NON_BINARY("Non-Binary")
}

data class Interest(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String
)

val DEFAULT_KENYAN_INTERESTS = listOf(
    Interest("1", "Nyama Choma", "🍖", "Food & Drink"),
    Interest("2", "Nairobi Coffee", "☕", "Food & Drink"),
    Interest("3", "Chai Masala", "🫖", "Food & Drink"),
    Interest("4", "Hiking Ngong Hills", "🥾", "Outdoors"),
    Interest("5", "Diani Beach Trips", "🏖️", "Travel"),
    Interest("6", "Safari & Game Drives", "🦁", "Outdoors"),
    Interest("7", "Rhumba & Live Bands", "🎷", "Music"),
    Interest("8", "Gengetone & Afrobeats", "🎵", "Music"),
    Interest("9", "Mugithi Nights", "🎸", "Music"),
    Interest("10", "Tech in Silicon Savannah", "💻", "Career"),
    Interest("11", "Art Galleries & Poetry", "🎨", "Culture"),
    Interest("12", "Rugby 7s Tournaments", "🏉", "Sports"),
    Interest("13", "Matatu Culture & Graffiti", "🚌", "Culture"),
    Interest("14", "Road Trips to Naivasha", "🚗", "Adventure"),
    Interest("15", "Cooking Pilau & Chapati", "🥘", "Food & Drink"),
    Interest("16", "Fitness & Gym", "💪", "Lifestyle")
)

data class DatingProfile(
    val id: String,
    val name: String,
    val age: Int,
    val dob: String = "1998-05-14",
    val gender: Gender,
    val city: String,
    val neighborhood: String,
    val occupation: String,
    val intent: DatingIntent,
    val bio: String,
    val shengIcebreaker: String,
    val interests: List<String>,
    val imageUrls: List<String>,
    val heightCm: Int = 172,
    val verified: Boolean = false,
    val distanceKm: Int = 4,
    val country: String = "Kenya",
    val isOnline: Boolean = false,
    val lastSeenAt: String? = null
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val isRead: Boolean = true,
    val imageUrl: String? = null
)

data class ChatConversation(
    val matchProfile: DatingProfile,
    val lastMessage: String,
    val lastTimestamp: String,
    val unreadCount: Int = 0,
    val messages: List<ChatMessage>
)

fun generatePenziId(seed: String): String {
    val hash = kotlin.math.abs(seed.hashCode().toLong())
    val digits = (hash % 900000000L) + 100000000L
    return digits.toString().take(8)
}

data class UserAccount(
    val id: String = "current_user",
    val penziId: String = generatePenziId("current_user"),
    val name: String = "Wangari Mwangi",
    val email: String = "wangari@example.com",
    val dob: String = "1997-09-21",
    val age: Int = 26,
    val gender: Gender = Gender.FEMALE,
    val lookingForGender: Gender = Gender.MALE,
    val city: String = "Nairobi",
    val neighborhood: String = "Kilimani",
    val occupation: String = "Product Designer @ Tech Savannah",
    val intent: DatingIntent = DatingIntent.SERIOUS,
    val bio: String = "Chai lover, sunset chaser at KICC rooftop, and amateur salsa dancer. Looking for someone genuine with good banter!",
    val interests: List<String> = listOf("Nairobi Coffee", "Hiking Ngong Hills", "Rhumba & Live Bands", "Cooking Pilau & Chapati"),
    val photoUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
    val isVerified: Boolean = false,
    val completedOnboarding: Boolean = true,
    
    // Album and new details
    val albumUrls: List<String> = emptyList(),
    val country: String = "Kenya",
    val social: String = "@wangari_mwangi",
    val education: String = "University Graduate",
    val height: String = "168 cm",
    val relationshipStatus: String = "Single",
    val languages: String = "English, Swahili, Sheng",
    val musicPreference: String = "Rhumba & Afrobeats",
    val dietaryHabit: String = "Omnivore",
    val sleepHabit: String = "Night Owl",
    val smoking: String = "No",
    val liquor: String = "Socially",
    val superpower: String = "Expert Banter & Pilau Cooking",
    val coins: Int = 300,
    val diamonds: Int = 500
)

fun UserAccount.toDatingProfile(): DatingProfile {
    val calculatedAge = try {
        val year = dob.split("-").firstOrNull()?.toIntOrNull() ?: 1998
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        (currentYear - year).coerceAtLeast(18)
    } catch (e: Exception) {
        age
    }
    return DatingProfile(
        id = id,
        name = name,
        age = calculatedAge,
        dob = dob,
        gender = gender,
        city = city,
        neighborhood = neighborhood,
        occupation = occupation,
        intent = intent,
        bio = bio,
        shengIcebreaker = "Habari Yako! Mimi ni $name.",
        interests = interests,
        imageUrls = if (photoUrl.isNotBlank()) listOf(photoUrl) else listOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500"),
        verified = isVerified,
        distanceKm = 1
    )
}

enum class RelationType {
    FOLLOWING, FOLLOWERS, FRIENDS
}

enum class MeSubScreen {
    SETTINGS, WALLET, SUPPORT, TASKS, VERIFICATION, PERSONAL_INFO, FOLLOWING, FOLLOWERS, FRIENDS, INCOME, SECURITY_PRIVACY, BLOCKED_LIST
}

fun getFallbackKenyanProfiles(): List<DatingProfile> {
    return listOf(
        DatingProfile(
            id = "fallback_1",
            name = "Amina Cherono",
            age = 24,
            dob = "2002-04-12",
            gender = Gender.FEMALE,
            city = "Mombasa",
            neighborhood = "Nyali",
            occupation = "Public Relations Officer",
            intent = DatingIntent.SERIOUS,
            bio = "Mombasa native who loves sandy beaches, live rhumba, and cooking Swahili biryani. Let's build something beautiful and long term.",
            shengIcebreaker = "Sasa! Niko na hamu ya kukujua zaidi, niaje?",
            interests = listOf("Diani Beach Trips", "Rhumba & Live Bands", "Cooking Pilau & Chapati"),
            imageUrls = listOf("https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=600&auto=format&fit=crop&q=80"),
            verified = true,
            distanceKm = 4,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_2",
            name = "Kevin Mwangi",
            age = 27,
            dob = "1999-07-22",
            gender = Gender.MALE,
            city = "Nairobi",
            neighborhood = "Kilimani",
            occupation = "Software Engineer",
            intent = DatingIntent.COFFEE,
            bio = "Tech enthusiast working in Silicon Savannah. Love brewing fresh coffee, talking startup ideas, and hiking Ngong Hills.",
            shengIcebreaker = "Form ya kahawa leo jioni ikoje?",
            interests = listOf("Nairobi Coffee", "Hiking Ngong Hills", "Tech in Silicon Savannah"),
            imageUrls = listOf("https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=600&auto=format&fit=crop&q=80"),
            verified = false,
            distanceKm = 2,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_3",
            name = "Grace Wanjiku",
            age = 25,
            dob = "2001-09-05",
            gender = Gender.FEMALE,
            city = "Nairobi",
            neighborhood = "Westlands",
            occupation = "Fashion Designer",
            intent = DatingIntent.NIGHTLIFE,
            bio = "Sherehe is a lifestyle! Brunch lover, rhumba enthusiast, and deep conversation partner. Looking for positive vibes only.",
            shengIcebreaker = "Kuja tusherekee hii wikendi!",
            interests = listOf("Rhumba & Live Bands", "Gengetone & Afrobeats", "Matatu Culture & Graffiti"),
            imageUrls = listOf("https://images.unsplash.com/photo-1531123897727-8f129e1688ce?w=600&auto=format&fit=crop&q=80"),
            verified = true,
            distanceKm = 5,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_4",
            name = "Brian Kiprop",
            age = 28,
            dob = "1998-11-18",
            gender = Gender.MALE,
            city = "Eldoret",
            neighborhood = "Kapsoya",
            occupation = "Athletics Coach",
            intent = DatingIntent.ADVENTURE,
            bio = "Love the outdoors, running in the morning, and road trips. Looking for someone adventurous to explore Kenya with.",
            shengIcebreaker = "Oya! Twende hike Ngong Hills?",
            interests = listOf("Hiking Ngong Hills", "Safari & Game Drives", "Road Trips to Naivasha"),
            imageUrls = listOf("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80"),
            verified = false,
            distanceKm = 8,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_5",
            name = "Joan Akoth",
            age = 23,
            dob = "2003-01-29",
            gender = Gender.FEMALE,
            city = "Kisumu",
            neighborhood = "Milimani",
            occupation = "Medical Student",
            intent = DatingIntent.CASUAL,
            bio = "Chill vibes, lakeside views, and fish lovers only. Medical student but I make time for adventure, cooking, and good music.",
            shengIcebreaker = "Mambo vipi? Karibu Kisumu tukunywe chai.",
            interests = listOf("Chai Masala", "Cooking Pilau & Chapati", "Diani Beach Trips"),
            imageUrls = listOf("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&auto=format&fit=crop&q=80"),
            verified = true,
            distanceKm = 6,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_6",
            name = "David Otieno",
            age = 29,
            dob = "1997-06-14",
            gender = Gender.MALE,
            city = "Nairobi",
            neighborhood = "South B",
            occupation = "Financial Analyst",
            intent = DatingIntent.SERIOUS,
            bio = "Nairobi-based professional, serious about life but young at heart. Let's connect, share some nyama choma, and build a real future.",
            shengIcebreaker = "Mambo! Ungependa tuchome nyama?",
            interests = listOf("Nyama Choma", "Rugby 7s Tournaments", "Nairobi Coffee"),
            imageUrls = listOf("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=600&auto=format&fit=crop&q=80"),
            verified = false,
            distanceKm = 3,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_7",
            name = "Shiko Nduta",
            age = 26,
            dob = "2000-02-28",
            gender = Gender.FEMALE,
            city = "Nakuru",
            neighborhood = "Milimani",
            occupation = "Florist & Creator",
            intent = DatingIntent.ADVENTURE,
            bio = "Plant mom, road trip enthusiast, and matatu graffiti fan. Let's drive down to Naivasha and create unforgettable memories.",
            shengIcebreaker = "Sasa! Form ya Naivasha ni gani?",
            interests = listOf("Road Trips to Naivasha", "Matatu Culture & Graffiti", "Art Galleries & Poetry"),
            imageUrls = listOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80"),
            verified = true,
            distanceKm = 10,
            isOnline = false
        ),
        DatingProfile(
            id = "fallback_8",
            name = "Josphat Karani",
            age = 31,
            dob = "1995-10-15",
            gender = Gender.MALE,
            city = "Nairobi",
            neighborhood = "Karen",
            occupation = "Architect",
            intent = DatingIntent.COFFEE,
            bio = "Appreciator of fine architecture, classical Mugithi music, and warm tea. Looking for a down-to-earth partner for meaningful chats.",
            shengIcebreaker = "Niaje boss? Karibu kahawa.",
            interests = listOf("Mugithi Nights", "Chai Masala", "Art Galleries & Poetry"),
            imageUrls = listOf("https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=600&auto=format&fit=crop&q=80"),
            verified = false,
            distanceKm = 7,
            isOnline = false
        )
    )
}

