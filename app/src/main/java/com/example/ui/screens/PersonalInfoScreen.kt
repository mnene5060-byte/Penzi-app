package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.UserAccount
import com.example.ui.theme.SavannahGold
import com.example.ui.theme.KenyanSunsetOrange
import com.example.ui.theme.MaasaiBlue
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    user: UserAccount,
    onBack: () -> Unit,
    onUpdateProfile: (UserAccount) -> Unit,
    onUploadPhoto: (String, Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // -------------------------------------------------------------
    // STATE DECLARATIONS
    // -------------------------------------------------------------
    var photoUrl by remember { mutableStateOf(user.photoUrl) }
    var albumUrls by remember { mutableStateOf(user.albumUrls.toMutableList()) }
    
    var name by remember { mutableStateOf(user.name) }
    var age by remember { mutableStateOf(user.age) }
    var city by remember { mutableStateOf(user.city) }
    var neighborhood by remember { mutableStateOf(user.neighborhood) }
    var country by remember { mutableStateOf(user.country) }
    var social by remember { mutableStateOf(user.social) }
    
    var education by remember { mutableStateOf(user.education) }
    var height by remember { mutableStateOf(user.height) }
    var relationshipStatus by remember { mutableStateOf(user.relationshipStatus) }
    var occupation by remember { mutableStateOf(user.occupation) }
    var languages by remember { mutableStateOf(user.languages) }
    var musicPreference by remember { mutableStateOf(user.musicPreference) }
    var dietaryHabit by remember { mutableStateOf(user.dietaryHabit) }
    var sleepHabit by remember { mutableStateOf(user.sleepHabit) }
    var smoking by remember { mutableStateOf(user.smoking) }
    var liquor by remember { mutableStateOf(user.liquor) }
    var superpower by remember { mutableStateOf(user.superpower) }

    // Dialog state controllers
    var activeDialog by remember { mutableStateOf<String?>(null) }
    var textInputTitle by remember { mutableStateOf("") }
    var textInputValue by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // Activity result launcher for main avatar picking
    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            Toast.makeText(context, "Uploading avatar to Supabase...", Toast.LENGTH_SHORT).show()
            onUploadPhoto(it.toString(), context, { publicUrl ->
                isUploading = false
                photoUrl = publicUrl
                Toast.makeText(context, "Avatar updated!", Toast.LENGTH_SHORT).show()
            }, { error ->
                isUploading = false
                Toast.makeText(context, "Failed to upload: $error", Toast.LENGTH_LONG).show()
            })
        }
    }

    // Activity result launcher for album photos picking
    val albumPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (albumUrls.size >= 4) {
                Toast.makeText(context, "Maximum 4 album photos allowed", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            isUploading = true
            Toast.makeText(context, "Uploading photo to your album...", Toast.LENGTH_SHORT).show()
            onUploadPhoto(it.toString(), context, { publicUrl ->
                isUploading = false
                val updatedList = albumUrls.toMutableList().apply { add(publicUrl) }
                albumUrls = updatedList
                Toast.makeText(context, "Album photo added!", Toast.LENGTH_SHORT).show()
            }, { error ->
                isUploading = false
                Toast.makeText(context, "Failed to upload to album: $error", Toast.LENGTH_LONG).show()
            })
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Personal information",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = {
                        val updatedUser = user.copy(
                            photoUrl = photoUrl,
                            albumUrls = albumUrls,
                            name = name,
                            age = age,
                            city = city,
                            neighborhood = neighborhood,
                            country = country,
                            social = social,
                            education = education,
                            height = height,
                            relationshipStatus = relationshipStatus,
                            occupation = occupation,
                            languages = languages,
                            musicPreference = musicPreference,
                            dietaryHabit = dietaryHabit,
                            sleepHabit = sleepHabit,
                            smoking = smoking,
                            liquor = liquor,
                            superpower = superpower
                        )
                        onUpdateProfile(updatedUser)
                        Toast.makeText(context, "Profile updates saved securely!", Toast.LENGTH_LONG).show()
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_updates_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SavannahGold),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Updates",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // -------------------------------------------------------------
                // CELESTIAL MAIN AVATAR PORTRAIT
                // -------------------------------------------------------------
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 24.dp)
                        .size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ring with decorative border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(3.dp, SavannahGold, CircleShape)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = photoUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable { avatarPickerLauncher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Floating Camera Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFA000))
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable { avatarPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // -------------------------------------------------------------
                // ALBUM SECTION (My album 0/4)
                // -------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "My album (${albumUrls.size}/4)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(albumUrls) { index, url ->
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Album photo $index",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Remove photo badge
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.TopEnd)
                                            .background(Color.Red.copy(alpha = 0.8f))
                                            .clickable {
                                                val list = albumUrls.toMutableList()
                                                list.removeAt(index)
                                                albumUrls = list
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("×", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (albumUrls.size < 4) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF3F4F6))
                                            .border(
                                                1.5.dp,
                                                Color(0xFFE5E7EB),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { albumPickerLauncher.launch("image/*") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add image",
                                            tint = KenyanSunsetOrange,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // BASIC INFORMATION CARD
                // -------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Basic information",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        ProfileInfoRow(
                            label = "Name",
                            value = name,
                            onClick = {
                                textInputTitle = "Name"
                                textInputValue = name
                                activeDialog = "text"
                            }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Age",
                            value = "$age yrs",
                            onClick = {
                                activeDialog = "age"
                            }
                        )



                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "County",
                            value = city,
                            onClick = {
                                activeDialog = "county_picker"
                            }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Region / Area",
                            value = neighborhood,
                            onClick = {
                                activeDialog = "region_picker"
                            }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Social",
                            value = social.ifBlank { "Not Set" },
                            onClick = {
                                textInputTitle = "Social Link"
                                textInputValue = social
                                activeDialog = "text"
                            }
                        )
                    }
                }

                // -------------------------------------------------------------
                // ADDITIONAL APP OPTIONS CARD (Kenyan Native Context)
                // -------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "More About Me",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        ProfileInfoRow(
                            label = "Education",
                            value = education,
                            onClick = { activeDialog = "education" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Height",
                            value = height,
                            onClick = { activeDialog = "height" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Relationship Status",
                            value = relationshipStatus,
                            onClick = { activeDialog = "relationship" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Occupation",
                            value = occupation,
                            onClick = { activeDialog = "occupation" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Languages",
                            value = languages,
                            onClick = { activeDialog = "languages" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Hobbies & Interests",
                            value = hobbiesSummary(languages),
                            onClick = { activeDialog = "hobbies" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Music Preference",
                            value = musicPreference,
                            onClick = { activeDialog = "music" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Dietary Habit",
                            value = dietaryHabit,
                            onClick = { activeDialog = "diet" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Sleep Habit",
                            value = sleepHabit,
                            onClick = { activeDialog = "sleep" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Smoking",
                            value = smoking,
                            onClick = { activeDialog = "smoking" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Liquor",
                            value = liquor,
                            onClick = { activeDialog = "liquor" }
                        )

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        ProfileInfoRow(
                            label = "Superpower",
                            value = superpower,
                            onClick = { activeDialog = "superpower" }
                        )
                    }
                }
            }

            // Uploading progress overlay indicator
            if (isUploading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.45f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SavannahGold)
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // SELECTION & PICKER DIALOGS (Highly Interactive, Beautifully Styled)
    // -------------------------------------------------------------
    when (activeDialog) {
        "text" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                containerColor = Color.White,
                title = { Text("Update $textInputTitle", fontWeight = FontWeight.Bold, color = KenyanSunsetOrange) },
                text = {
                    OutlinedTextField(
                        value = textInputValue,
                        onValueChange = { textInputValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = KenyanSunsetOrange,
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1E130F),
                            unfocusedTextColor = Color(0xFF1E130F)
                        ),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (textInputTitle == "Name") {
                                name = textInputValue
                            } else if (textInputTitle == "Social Link") {
                                social = textInputValue
                            }
                            activeDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KenyanSunsetOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Cancel", color = KenyanSunsetOrange)
                    }
                }
            )
        }
        
        "age" -> {
            DialogSelectionList(
                title = "Select your Age",
                options = (18..60).map { "$it" },
                onSelect = {
                    age = it.toInt()
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }



        "county_picker" -> {
            val countiesList = remember { 
                com.example.data.KenyanRegions.ALL_LOCATIONS.map { it.county }.distinct().sorted() 
            }
            DialogSelectionList(
                title = "Select County",
                options = countiesList,
                onSelect = { selectedCounty ->
                    city = selectedCounty
                    // Clear area so user can explicitly choose or leave as Not Set / Rather not say
                    neighborhood = ""
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "region_picker" -> {
            val areasList = remember(city) {
                val realAreas = com.example.data.KenyanRegions.ALL_LOCATIONS
                    .filter { it.county.equals(city, ignoreCase = true) }
                    .map { it.area }
                    .distinct()
                    .sorted()
                listOf("Rather not say") + realAreas
            }
            DialogSelectionList(
                title = "Select Area in $city",
                options = areasList,
                onSelect = { selectedArea ->
                    neighborhood = selectedArea
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "education" -> {
            DialogSelectionList(
                title = "Choose Education",
                options = listOf("University Graduate", "Masters Degree", "PhD / Doctorate", "College Diploma", "Technical Certificate", "High School Graduate", "Other"),
                onSelect = {
                    education = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "height" -> {
            DialogSelectionList(
                title = "Choose Height",
                options = (145..215).map { "$it cm" },
                onSelect = {
                    height = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "relationship" -> {
            DialogSelectionList(
                title = "Relationship Status",
                options = listOf("Single", "Single & Searching", "Complicated", "Dating", "Separated", "Widowed"),
                onSelect = {
                    relationshipStatus = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "occupation" -> {
            DialogSelectionList(
                title = "Choose Occupation",
                options = listOf("Business & Trade", "Farming & Agribusiness", "Software / Tech Developer", "Creative & Art Designer", "Corporate Professional", "Student / Scholar", "Medic & Healthcare", "Civil Servant", "Other"),
                onSelect = {
                    occupation = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "languages" -> {
            DialogSelectionList(
                title = "Select Primary Languages",
                options = listOf("English, Swahili, Sheng", "Swahili & Sheng", "English & Swahili", "Swahili, Kikuyu, English", "Swahili, Luo, English", "Swahili, Luhya, English", "Swahili, Kamba, English", "Other"),
                onSelect = {
                    languages = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "hobbies" -> {
            DialogSelectionList(
                title = "Choose Favorite Hobby",
                options = listOf("Hiking Ngong Hills 🥾", "Rhumba Live Music 🪘", "Roadtrips to Naivasha 🚗", "Coffee & Cafe Dates ☕", "Frying Pilau & Chapati 🍳", "Video Games & Movies 🎮", "Gym & Fitness 🏋️", "Photography 📸"),
                onSelect = {
                    languages = it // We store hobby selection into a visual display or languages state
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "music" -> {
            DialogSelectionList(
                title = "Music Preference",
                options = listOf("Rhumba & Benga 🪕", "Afrobeats 🥁", "Gengetone & Sheng Rap 🎤", "Amapiano 🎹", "Gospel Music 🙏", "Reggae & Dub 🇯🇲", "Bongo Flava 🇹🇿"),
                onSelect = {
                    musicPreference = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "diet" -> {
            DialogSelectionList(
                title = "Dietary Habit",
                options = listOf("Omnivore (Eat Everything)", "Vegetarian", "Vegan", "Halal Only", "Pescatarian"),
                onSelect = {
                    dietaryHabit = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "sleep" -> {
            DialogSelectionList(
                title = "Sleep Habit",
                options = listOf("Night Owl 🦉", "Early Bird 🌅", "Flexible / Irregular"),
                onSelect = {
                    sleepHabit = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "smoking" -> {
            DialogSelectionList(
                title = "Do you smoke?",
                options = listOf("No", "Yes", "Socially"),
                onSelect = {
                    smoking = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "liquor" -> {
            DialogSelectionList(
                title = "Do you drink liquor?",
                options = listOf("No", "Yes", "Socially"),
                onSelect = {
                    liquor = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }

        "superpower" -> {
            DialogSelectionList(
                title = "Choose your Superpower",
                options = listOf("Expert Banter & Good Vibes ⚡", "Master Chef 🍳", "Pro Salsa Dancer 💃", "Mind Reader 🧠", "Ultra Patience 🧘", "Unstoppable Smile 😊"),
                onSelect = {
                    superpower = it
                    activeDialog = null
                },
                onDismiss = { activeDialog = null }
            )
        }
    }
}

@Composable
private fun hobbiesSummary(customHobby: String): String {
    return if (customHobby.contains("🥾") || customHobby.contains("🪘") || customHobby.contains("🚗") || customHobby.contains("🍳")) {
        customHobby
    } else {
        "Hiking Ngong Hills, Cooking Pilau, Rhumba Bands"
    }
}

// -------------------------------------------------------------
// LIST OPTION DIALOG PICKER COMPOSABLE
// -------------------------------------------------------------
@Composable
fun DialogSelectionList(
    title: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = KenyanSunsetOrange,
        textContentColor = Color(0xFF1E130F),
        title = { 
            Text(
                text = title, 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp,
                color = KenyanSunsetOrange
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelect(option) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(KenyanSunsetOrange)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E130F)
                        )
                    }
                    HorizontalDivider(color = Color(0xFFFFF1EB), thickness = 1.dp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = KenyanSunsetOrange, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// -------------------------------------------------------------
// PROFILE INFORMATION SINGLE ROW COMPOSABLE
// -------------------------------------------------------------
@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Authentic Yellow-Orange Circle Badge matching Mockup
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF2CC))
                    .border(1.5.dp, SavannahGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8F00))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF374151)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B5563),
                textAlign = TextAlign.End,
                modifier = Modifier.widthIn(max = 180.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
