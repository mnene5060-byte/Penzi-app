package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import com.example.ui.components.InAppGalleryPickerSheet
import com.example.ui.theme.KenyanRed
import com.example.ui.theme.KenyanRedDark
import com.example.data.KenyaLocation
import com.example.data.KenyanRegions
import com.example.model.DEFAULT_KENYAN_INTERESTS
import com.example.model.DatingIntent
import com.example.model.Gender
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: com.example.data.DatingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onComplete: (
        name: String,
        dob: String,
        gender: Gender,
        city: String,
        neighborhood: String,
        occupation: String,
        intent: DatingIntent,
        bio: String,
        interests: List<String>,
        photoUrl: String
    ) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSavingProfile by remember { mutableStateOf(false) }

    // Multi-step Navigation State
    var currentStep by remember { mutableStateOf(1) }

    // STEP 1: BASICS (Unselected / null by default)
    var name by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }

    // STEP 2: LOCATION & CAREER
    var selectedLocation by remember { mutableStateOf(KenyaLocation("", "")) }
    var isCountyPickerOpen by remember { mutableStateOf(false) }
    var isRegionPickerOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }

    // STEP 3: RELATIONSHIP INTENT & BIO
    var selectedIntent by remember { mutableStateOf(DatingIntent.SERIOUS) }
    var bio by remember { mutableStateOf("") }
    var shengIcebreaker by remember { mutableStateOf("") }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    // STEP 4: PHOTO UPLOAD & CROP
    var customPhotoUrl by remember { mutableStateOf("") }
    var selectedAvatarUrl by remember { mutableStateOf("") }
    var isGallerySheetOpen by remember { mutableStateOf(false) }
    
    // Cropper & Upload local states
    var pickedGalleryUri by remember { mutableStateOf<String?>(null) }
    var cropZoom by remember { mutableStateOf(1f) }
    var cropRotation by remember { mutableStateOf(0f) }
    var uploadProgress by remember { mutableStateOf(false) }
    var uploadStatusMsg by remember { mutableStateOf("") }

    var dayExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val maxYear = currentYear - 18
    val minYear = 1940

    val days = (1..31).toList()
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val years = (maxYear downTo minYear).toList()

    val femaleAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80"
    )

    val maleAvatars = listOf(
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=500&auto=format&fit=crop&q=80"
    )

    val popularOccupations = listOf(
        "Software Engineer", "Entrepreneur", "Banker & Finance",
        "Doctor / Healthcare", "Teacher / Lecturer", "Creative Designer",
        "Lawyer", "Marketer / Sales", "Civil Servant", "Student"
    )

    // Set default avatar based on gender selection
    LaunchedEffect(selectedGender) {
        if (selectedAvatarUrl.isBlank() && selectedGender != null) {
            selectedAvatarUrl = if (selectedGender == Gender.MALE) maleAvatars[0] else femaleAvatars[0]
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFBF7)) // Warm Sand light backdrop
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Progress bar indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    IconButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(36.dp))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "STEP $currentStep OF 4",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KenyanRed,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = when (currentStep) {
                            1 -> "Basic Details"
                            2 -> "Location & Work"
                            3 -> "About Me"
                            else -> "Profile Photo"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E130F)
                    )
                }

                Spacer(modifier = Modifier.width(36.dp))
            }

            // Beautiful progress line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..4).forEach { stepNum ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (stepNum <= currentStep) KenyanRed else Color(0xFFEADBCE))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // STEP 1 CONTENT: BASICS
            // -------------------------------------------------------------
            if (currentStep == 1) {
                OnboardingSectionCard(
                    title = "Who are you?",
                    subtitle = "Introduce yourself with your real name",
                    icon = Icons.Default.Person
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Full Name",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("e.g. Wangari Mwangi", color = Color(0xFF9CA3AF)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_name"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = KenyanRed,
                                unfocusedBorderColor = Color(0xFFEADBCE),
                                focusedTextColor = Color(0xFF1E130F),
                                unfocusedTextColor = Color(0xFF1E130F)
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OnboardingSectionCard(
                    title = "Your Birthday",
                    subtitle = "Must be 18+ to connect on Penzi Kenya",
                    icon = Icons.Default.CalendarMonth
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Date of Birth",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // DAY DROPDOWN
                            Box(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEADBCE)),
                                    color = Color.White,
                                    modifier = Modifier.clickable { dayExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedDay?.toString() ?: "Day",
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedDay != null) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedDay != null) Color(0xFF1E130F) else Color(0xFF9E9187)
                                        )
                                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                                DropdownMenu(
                                    expanded = dayExpanded,
                                    onDismissRequest = { dayExpanded = false },
                                    modifier = Modifier.height(280.dp).background(Color.White)
                                ) {
                                    days.forEach { d ->
                                        DropdownMenuItem(
                                            text = { Text(d.toString(), color = Color(0xFF1E130F)) },
                                            onClick = {
                                                selectedDay = d
                                                dayExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // MONTH DROPDOWN
                            Box(modifier = Modifier.weight(1.2f)) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEADBCE)),
                                    color = Color.White,
                                    modifier = Modifier.clickable { monthExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedMonth?.let { monthNames[it - 1] } ?: "Month",
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedMonth != null) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedMonth != null) Color(0xFF1E130F) else Color(0xFF9E9187)
                                        )
                                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                                DropdownMenu(
                                    expanded = monthExpanded,
                                    onDismissRequest = { monthExpanded = false },
                                    modifier = Modifier.height(280.dp).background(Color.White)
                                ) {
                                    monthNames.forEachIndexed { index, mName ->
                                        DropdownMenuItem(
                                            text = { Text(mName, color = Color(0xFF1E130F)) },
                                            onClick = {
                                                selectedMonth = index + 1
                                                monthExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // YEAR DROPDOWN
                            Box(modifier = Modifier.weight(1.2f)) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEADBCE)),
                                    color = Color.White,
                                    modifier = Modifier.clickable { yearExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedYear?.toString() ?: "Year",
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedYear != null) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedYear != null) Color(0xFF1E130F) else Color(0xFF9E9187)
                                        )
                                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                                DropdownMenu(
                                    expanded = yearExpanded,
                                    onDismissRequest = { yearExpanded = false },
                                    modifier = Modifier.height(280.dp).background(Color.White)
                                ) {
                                    years.forEach { y ->
                                        DropdownMenuItem(
                                            text = { Text(y.toString(), color = Color(0xFF1E130F)) },
                                            onClick = {
                                                selectedYear = y
                                                yearExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OnboardingSectionCard(
                    title = "Gender Selection",
                    subtitle = "This cannot be changed easily later",
                    icon = Icons.Default.Favorite
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "I identify as:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Female Card
                            val isFemaleSelected = selectedGender == Gender.FEMALE
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isFemaleSelected) 2.5.dp else 1.dp,
                                        color = if (isFemaleSelected) KenyanRed else Color(0xFFEADBCE),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedGender = Gender.FEMALE
                                    },
                                color = if (isFemaleSelected) Color(0xFFFFF1F2) else Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isFemaleSelected) KenyanRed else Color(0xFFFFF1F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "♀",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFemaleSelected) Color.White else KenyanRed
                                        )
                                    }
                                    Text(
                                        text = "Female",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFemaleSelected) KenyanRed else Color(0xFF4B5563)
                                    )
                                }
                            }

                            // Male Card
                            val isMaleSelected = selectedGender == Gender.MALE
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isMaleSelected) 2.5.dp else 1.dp,
                                        color = if (isMaleSelected) Color(0xFF3B82F6) else Color(0xFFEADBCE),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedGender = Gender.MALE
                                    },
                                color = if (isMaleSelected) Color(0xFFEFF6FF) else Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isMaleSelected) Color(0xFF3B82F6) else Color(0xFFEFF6FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "♂",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMaleSelected) Color.White else Color(0xFF3B82F6)
                                        )
                                    }
                                    Text(
                                        text = "Male",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMaleSelected) Color(0xFF1E40AF) else Color(0xFF4B5563)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // NEXT BUTTON
                val isStep1Valid = name.isNotBlank() && selectedGender != null && selectedDay != null && selectedMonth != null && selectedYear != null
                Button(
                    onClick = { if (isStep1Valid) currentStep = 2 },
                    enabled = isStep1Valid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KenyanRed,
                        disabledContainerColor = Color(0xFFEADBCE),
                        contentColor = Color.White
                    )
                ) {
                    Text("Continue to Location & Work", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // -------------------------------------------------------------
            // STEP 2 CONTENT: LOCATION & WORK
            // -------------------------------------------------------------
            if (currentStep == 2) {
                OnboardingSectionCard(
                    title = "Location",
                    subtitle = "Matches are calculated based on your active County",
                    icon = Icons.Default.LocationOn
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select County",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFEADBCE), RoundedCornerShape(16.dp))
                                .clickable { isCountyPickerOpen = true },
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFF1F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = KenyanRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = selectedLocation.county.ifBlank { "Select County" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedLocation.county.isBlank()) Color.Gray else Color(0xFF1E130F)
                                        )
                                        Text(
                                            text = "County",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFF1F2)
                                ) {
                                    Text(
                                        text = if (selectedLocation.county.isBlank()) "Choose" else "Change",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KenyanRed,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Select Area / Region",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFEADBCE), RoundedCornerShape(16.dp))
                                .clickable {
                                    if (selectedLocation.county.isBlank()) {
                                        Toast.makeText(context, "Please select your county first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        isRegionPickerOpen = true
                                    }
                                },
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFF1F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = KenyanRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = selectedLocation.area.ifBlank { "Select Region / Area" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedLocation.area.isBlank()) Color.Gray else Color(0xFF1E130F)
                                        )
                                        Text(
                                            text = if (selectedLocation.county.isBlank()) "Area / Region" else "Area within ${selectedLocation.county}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFF1F2)
                                ) {
                                    Text(
                                        text = if (selectedLocation.area.isBlank()) "Choose" else "Change",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KenyanRed,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OnboardingSectionCard(
                    title = "Profession / Career",
                    subtitle = "Let others know what you do for a living",
                    icon = Icons.Default.Work
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Occupation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(popularOccupations) { job ->
                                val isSelected = occupation.contains(job, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) KenyanRed else Color(0xFFFFF1F2),
                                    modifier = Modifier.clickable { occupation = job }
                                ) {
                                    Text(
                                        text = job,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else KenyanRed,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = occupation,
                            onValueChange = { occupation = it },
                            placeholder = { Text("e.g. Consultant @ Westlands", color = Color(0xFF9CA3AF)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = KenyanRed,
                                unfocusedBorderColor = Color(0xFFEADBCE),
                                focusedTextColor = Color(0xFF1E130F),
                                unfocusedTextColor = Color(0xFF1E130F)
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                val isStep2Valid = occupation.isNotBlank()
                Button(
                    onClick = { if (isStep2Valid) currentStep = 3 },
                    enabled = isStep2Valid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KenyanRed,
                        disabledContainerColor = Color(0xFFEADBCE),
                        contentColor = Color.White
                    )
                ) {
                    Text("Continue to About Me", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // -------------------------------------------------------------
            // STEP 3 CONTENT: BIO & INTERESTS
            // -------------------------------------------------------------
            if (currentStep == 3) {
                OnboardingSectionCard(
                    title = "Relationship Intent",
                    subtitle = "Define your true dating boundaries clearly",
                    icon = Icons.Default.Favorite
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DatingIntent.values().forEach { intent ->
                            val isSelected = selectedIntent == intent
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) KenyanRed else Color(0xFFEADBCE),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedIntent = intent },
                                color = if (isSelected) Color(0xFFFFF1F2) else Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = intent.emoji, fontSize = 22.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = intent.label,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Text(
                                            text = intent.tagline,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = KenyanRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OnboardingSectionCard(
                    title = "Bio & Conversations",
                    subtitle = "Introduce yourself with custom bio prompts",
                    icon = Icons.Default.FormatQuote
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tap to add a bio template snippet:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            val suggestions = listOf(
                                "Coffee lover ☕ & sunset chaser at KICC 🌅",
                                "Tech enthusiast 💻 & Rhumba live music 🎷",
                                "Foodie searching for the best Nyama Choma 🍖"
                            )
                            items(suggestions) { sugg ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFFFF1F2),
                                    modifier = Modifier.clickable { bio = sugg }
                                ) {
                                    Text(
                                        text = "+ $sugg",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = KenyanRed,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            placeholder = { Text("Write something genuine about yourself...", color = Color(0xFF9CA3AF)) },
                            label = { Text("Short Bio") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = KenyanRed,
                                unfocusedBorderColor = Color(0xFFEADBCE),
                                focusedTextColor = Color(0xFF1E130F),
                                unfocusedTextColor = Color(0xFF1E130F)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = shengIcebreaker,
                            onValueChange = { shengIcebreaker = it },
                            placeholder = { Text("e.g. Sasa! Glad to connect on Penzi", color = Color(0xFF9CA3AF)) },
                            label = { Text("First Message Prompt (Sheng / English)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = KenyanRed,
                                unfocusedBorderColor = Color(0xFFEADBCE),
                                focusedTextColor = Color(0xFF1E130F),
                                unfocusedTextColor = Color(0xFF1E130F)
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OnboardingSectionCard(
                    title = "My Interests (${selectedInterests.size} selected)",
                    subtitle = "Select items that represent your Kenyan lifestyle",
                    icon = Icons.Default.AutoAwesome
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DEFAULT_KENYAN_INTERESTS.forEach { interest ->
                            val itemText = "${interest.emoji} ${interest.name}"
                            val isSelected = selectedInterests.contains(interest.name)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) KenyanRed else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) KenyanRed else Color(0xFFEADBCE)
                                ),
                                modifier = Modifier.clickable {
                                    val newSet = selectedInterests.toMutableSet()
                                    if (isSelected) newSet.remove(interest.name) else newSet.add(interest.name)
                                    selectedInterests = newSet
                                }
                            ) {
                                Text(
                                    text = itemText,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF374151),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                val isStep3Valid = bio.isNotBlank() && selectedInterests.isNotEmpty()
                Button(
                    onClick = { if (isStep3Valid) currentStep = 4 },
                    enabled = isStep3Valid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KenyanRed,
                        disabledContainerColor = Color(0xFFEADBCE),
                        contentColor = Color.White
                    )
                ) {
                    Text("Continue to Profile Photo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // -------------------------------------------------------------
            // STEP 4 CONTENT: PHOTO SELECTION & INTEGRATED GALLERY UPLOADING
            // -------------------------------------------------------------
            if (currentStep == 4) {
                OnboardingSectionCard(
                    title = "Profile Media Center",
                    subtitle = "Choose from standard templates or secure private gallery",
                    icon = Icons.Default.CameraAlt
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Standard Avatars block
                        val currentAvatars = if (selectedGender == Gender.MALE) maleAvatars else femaleAvatars
                        
                        Text(
                            text = "A) Standard Avatars",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            currentAvatars.forEach { url ->
                                val isSelected = selectedAvatarUrl == url && pickedGalleryUri == null
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) KenyanRed else Color.Transparent,
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                        .clickable {
                                            selectedAvatarUrl = url
                                            pickedGalleryUri = null
                                            customPhotoUrl = ""
                                        }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Avatar option",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Custom Upload Block
                        Text(
                            text = "B) Custom Gallery Photo (Recommended 🇰🇪)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Button(
                            onClick = { isGallerySheetOpen = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E130F),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pick Photo from Gallery", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // If gallery image chosen, show crop adjusters
                        pickedGalleryUri?.let { uri ->
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, KenyanRed),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Crop Adjuster ✂️",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = KenyanRed,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Slide zoom/rotation to fit perfectly",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Cropping boundary frame simulator
                                    Box(
                                        modifier = Modifier
                                            .size(160.dp)
                                            .border(1.5.dp, Color(0xFFEADBCE), RoundedCornerShape(12.dp))
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Selected Photo",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(2.dp, KenyanRed, RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        
                                        // Simple Overlay Grid for precision cropping guide
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(0.5.dp, Color.White.copy(alpha = 0.5f))
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Slider 1: Zoom Scale (1x to 3x)
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Scale Zoom", fontSize = 11.sp, color = Color.Gray)
                                            Text(String.format("%.1fx", cropZoom), fontSize = 11.sp, color = KenyanRed, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = cropZoom,
                                            onValueChange = { cropZoom = it },
                                            valueRange = 1f..3f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = KenyanRed,
                                                activeTrackColor = KenyanRed,
                                                inactiveTrackColor = Color(0xFFEADBCE)
                                            )
                                        )
                                    }

                                    // Slider 2: Rotation (0 to 360)
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Rotation Angle", fontSize = 11.sp, color = Color.Gray)
                                            Text("${cropRotation.toInt()}°", fontSize = 11.sp, color = KenyanRed, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = cropRotation,
                                            onValueChange = { cropRotation = it },
                                            valueRange = 0f..360f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = KenyanRed,
                                                activeTrackColor = KenyanRed,
                                                inactiveTrackColor = Color(0xFFEADBCE)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        if (pickedGalleryUri == null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = customPhotoUrl,
                                onValueChange = { customPhotoUrl = it },
                                placeholder = { Text("https://... (or leave blank to use avatar)", color = Color(0xFF9CA3AF)) },
                                label = { Text("Custom Web Photo URL (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = KenyanRed,
                                    unfocusedBorderColor = Color(0xFFEADBCE),
                                    focusedTextColor = Color(0xFF1E130F),
                                    unfocusedTextColor = Color(0xFF1E130F)
                                ),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // SAVE & COMPLETE SUBMIT BUTTON
                if (uploadProgress) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = KenyanRed)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uploadStatusMsg.ifBlank { "Uploading cropped photo to secure Penzi storage..." },
                            fontSize = 12.sp,
                            color = Color(0xFF1E130F),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                uploadProgress = true
                                isSavingProfile = true
                                val finalName = if (name.isNotBlank()) name.trim() else "Kenyan Member"
                                val d = selectedDay ?: 15
                                val m = selectedMonth ?: 8
                                val y = selectedYear ?: 1998
                                val dobString = String.format("%04d-%02d-%02d", y, m, d)
                                val finalGender = selectedGender ?: Gender.FEMALE

                                // Determine photo source
                                if (pickedGalleryUri != null) {
                                    uploadStatusMsg = "Uploading cropped photo to secure 'photos' bucket..."
                                    viewModel.uploadOnboardingPhoto(
                                        uriOrUrl = pickedGalleryUri!!,
                                        context = context,
                                        onSuccess = { uploadedUrl ->
                                            uploadProgress = false
                                            onComplete(
                                                finalName,
                                                dobString,
                                                finalGender,
                                                selectedLocation.county,
                                                selectedLocation.area,
                                                occupation.ifBlank { "Professional" },
                                                selectedIntent,
                                                bio.ifBlank { "Excited to connect with authentic people in Kenya 🇰🇪" },
                                                selectedInterests.toList(),
                                                uploadedUrl
                                            )
                                        },
                                        onError = { errMsg ->
                                            uploadProgress = false
                                            Toast.makeText(context, "Upload failed, using standard avatar: $errMsg", Toast.LENGTH_LONG).show()
                                            
                                            val fallbackPhoto = selectedAvatarUrl
                                            onComplete(
                                                finalName,
                                                dobString,
                                                finalGender,
                                                selectedLocation.county,
                                                selectedLocation.area,
                                                occupation.ifBlank { "Professional" },
                                                selectedIntent,
                                                bio.ifBlank { "Excited to connect with authentic people in Kenya 🇰🇪" },
                                                selectedInterests.toList(),
                                                fallbackPhoto
                                            )
                                        }
                                    )
                                } else {
                                    val finalPhoto = customPhotoUrl.ifBlank { selectedAvatarUrl }
                                    delay(1000)
                                    onComplete(
                                        finalName,
                                        dobString,
                                        finalGender,
                                        selectedLocation.county,
                                        selectedLocation.area,
                                        occupation.ifBlank { "Professional" },
                                        selectedIntent,
                                        bio.ifBlank { "Excited to connect with authentic people in Kenya 🇰🇪" },
                                        selectedInterests.toList(),
                                        finalPhoto
                                    )
                                    uploadProgress = false
                                }
                            }
                        },
                        enabled = !isSavingProfile && !uploadProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("save_details_button"),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KenyanRed,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Save Profile & Start Swiping 🇰🇪",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // In-App Gallery Picker Sheet
    if (isGallerySheetOpen) {
        InAppGalleryPickerSheet(
            onDismiss = { isGallerySheetOpen = false },
            onPhotoSelected = { chosenUri ->
                pickedGalleryUri = chosenUri
                isGallerySheetOpen = false
            }
        )
    }

    // County Picker Dialog
    if (isCountyPickerOpen) {
        Dialog(onDismissRequest = { isCountyPickerOpen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Select County",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Counties of Kenya 🇰🇪",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(onClick = { isCountyPickerOpen = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search county...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3F4F6),
                            unfocusedContainerColor = Color(0xFFF3F4F6),
                            focusedBorderColor = KenyanRed,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredCounties = remember(searchQuery) {
                        val allCounties = KenyanRegions.ALL_LOCATIONS.map { it.county }.distinct().sorted()
                        if (searchQuery.isBlank()) {
                            allCounties
                        } else {
                            allCounties.filter { it.contains(searchQuery, ignoreCase = true) }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCounties) { countyName ->
                            val isSelected = countyName.equals(selectedLocation.county, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLocation = KenyaLocation(countyName, "")
                                        isCountyPickerOpen = false
                                        searchQuery = ""
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFFFF1F2) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) KenyanRed else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = countyName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) KenyanRed else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Region Picker Dialog
    if (isRegionPickerOpen) {
        Dialog(onDismissRequest = { isRegionPickerOpen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Select Area / Region",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Areas inside ${selectedLocation.county}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(onClick = { isRegionPickerOpen = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search area...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3F4F6),
                            unfocusedContainerColor = Color(0xFFF3F4F6),
                            focusedBorderColor = KenyanRed,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredAreas = remember(selectedLocation.county, searchQuery) {
                        val realAreas = KenyanRegions.ALL_LOCATIONS
                            .filter { it.county.equals(selectedLocation.county, ignoreCase = true) }
                            .map { it.area }
                            .distinct()
                            .sorted()
                        val combined = listOf("Rather not say") + realAreas
                        if (searchQuery.isBlank()) {
                            combined
                        } else {
                            combined.filter { it.contains(searchQuery, ignoreCase = true) }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredAreas) { areaName ->
                            val isSelected = areaName.equals(selectedLocation.area, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLocation = selectedLocation.copy(area = areaName)
                                        isRegionPickerOpen = false
                                        searchQuery = ""
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFFFF1F2) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) KenyanRed else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = areaName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) KenyanRed else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF1F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = KenyanRed,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}
