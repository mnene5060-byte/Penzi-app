package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AuthState
import com.example.data.DatingViewModel
import com.example.model.DatingProfile
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileDetailScreen
import com.example.ui.screens.UserRelationListScreen
import com.example.ui.theme.KenyanRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PenziFullScreenGradient
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziPrimaryGradient
import com.example.ui.theme.SavannahGold
import com.example.ui.theme.SavannahGoldLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PenziApp()
            }
        }
    }
}

data class NavTabItem(
    val id: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun ThreeDNavIcon(
    tabId: String,
    isSelected: Boolean,
    unreadCount: Int = 0
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 420f),
        label = "nav_icon_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected && tabId == "discover") {
            // Yellow oval highlight pill matching the screenshot
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFEF08A))
                    .padding(horizontal = 14.dp, vertical = 2.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Custom3DHomeHouseIcon(isSelected = true, size = 26.dp)
            }
        } else {
            // Custom 3D Icon Rendering per tab (compact 28.dp size)
            when (tabId) {
                "discover" -> Custom3DHomeHouseIcon(isSelected = isSelected, size = 28.dp)
                "explore" -> Custom3DPartyDiscoIcon(isSelected = isSelected, size = 28.dp)
                "chat" -> Custom3DChatBubbleIcon(isSelected = isSelected, size = 28.dp)
                else -> Custom3DProfileCrownIcon(isSelected = isSelected, size = 28.dp)
            }
        }

        // 3D Jewel Unread Counter Badge
        if (tabId == "chat" && unreadCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = (-2).dp, top = (-3).dp)
                    .size(17.dp)
                    .shadow(3.dp, CircleShape, spotColor = Color(0xFFDC2626)),
                shape = CircleShape,
                color = Color(0xFFB91C1C),
                border = BorderStroke(1.2.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. CUSTOM 3D HOME HOUSE ICON
// -------------------------------------------------------------
@Composable
fun Custom3DHomeHouseIcon(isSelected: Boolean, size: Dp = 28.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Roof Path (Warm 3D Terracotta / Orange)
        val roofPath = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.90f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.22f, h * 0.48f)
            lineTo(w * 0.10f, h * 0.48f)
            close()
        }

        // Roof Main Body
        val roofBrush = if (isSelected) {
            Brush.verticalGradient(
                listOf(Color(0xFFFF8A00), Color(0xFFEA580C), Color(0xFFC2410C))
            )
        } else {
            Brush.verticalGradient(
                listOf(Color(0xFF94A3B8), Color(0xFF64748B), Color(0xFF475569))
            )
        }
        drawPath(path = roofPath, brush = roofBrush)

        // Chimney
        val chimneyPath = Path().apply {
            moveTo(w * 0.68f, h * 0.22f)
            lineTo(w * 0.76f, h * 0.22f)
            lineTo(w * 0.76f, h * 0.38f)
            lineTo(w * 0.68f, h * 0.30f)
            close()
        }
        drawPath(
            path = chimneyPath,
            brush = if (isSelected) Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706))) else Brush.verticalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF64748B)))
        )

        // House Body (Warm Yellow / Cream)
        val houseBodyPath = Path().apply {
            moveTo(w * 0.22f, h * 0.46f)
            lineTo(w * 0.78f, h * 0.46f)
            lineTo(w * 0.78f, h * 0.88f)
            lineTo(w * 0.22f, h * 0.88f)
            close()
        }
        val bodyBrush = if (isSelected) {
            Brush.verticalGradient(
                listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFF59E0B))
            )
        } else {
            Brush.verticalGradient(
                listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0), Color(0xFF94A3B8))
            )
        }
        drawPath(path = houseBodyPath, brush = bodyBrush)

        // Door (Amber / Brown 3D)
        val doorPath = Path().apply {
            moveTo(w * 0.42f, h * 0.60f)
            lineTo(w * 0.58f, h * 0.60f)
            lineTo(w * 0.58f, h * 0.88f)
            lineTo(w * 0.42f, h * 0.88f)
            close()
        }
        drawPath(
            path = doorPath,
            brush = if (isSelected) Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFF9A3412))) else Brush.verticalGradient(listOf(Color(0xFF64748B), Color(0xFF334155)))
        )

        // Door Knob
        drawCircle(
            color = if (isSelected) Color(0xFFFEF08A) else Color.White,
            radius = 2.2f,
            center = Offset(w * 0.53f, h * 0.74f)
        )

        // Specular Glow on Roof
        drawCircle(
            color = Color.White.copy(alpha = if (isSelected) 0.8f else 0.4f),
            radius = 2.5f,
            center = Offset(w * 0.48f, h * 0.20f)
        )
    }
}

// -------------------------------------------------------------
// 2. CUSTOM 3D PARTY DISCO ICON
// -------------------------------------------------------------
@Composable
fun Custom3DPartyDiscoIcon(isSelected: Boolean, size: Dp = 28.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height * 0.56f)
        val radius = this.size.width * 0.36f
        val w = this.size.width
        val h = this.size.height

        // Top suspension chain
        drawLine(
            color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF94A3B8),
            start = Offset(center.x, h * 0.08f),
            end = Offset(center.x, center.y - radius),
            strokeWidth = 2.5f
        )

        // 3D Metallic Disco Sphere
        val sphereBrush = if (isSelected) {
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFFDE047),
                    Color(0xFFE2E8F0),
                    Color(0xFF64748B),
                    Color(0xFF1E293B)
                ),
                center = center - Offset(radius * 0.3f, radius * 0.3f),
                radius = radius * 1.1f
            )
        } else {
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFCBD5E1),
                    Color(0xFF64748B),
                    Color(0xFF334155)
                ),
                center = center - Offset(radius * 0.3f, radius * 0.3f),
                radius = radius * 1.1f
            )
        }
        drawCircle(brush = sphereBrush, radius = radius, center = center)

        // Grid facets
        val facetColor = if (isSelected) Color(0xFFF59E0B).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.3f)
        for (i in -2..2) {
            val yOffset = i * (radius * 0.32f)
            val lineLen = kotlin.math.sqrt((radius * radius - yOffset * yOffset).coerceAtLeast(0f))
            drawLine(
                color = facetColor,
                start = Offset(center.x - lineLen * 0.8f, center.y + yOffset),
                end = Offset(center.x + lineLen * 0.8f, center.y + yOffset),
                strokeWidth = 1.2f
            )
        }

        // Sparkle stars
        drawCircle(
            color = Color.White,
            radius = 2.5f,
            center = center - Offset(radius * 0.45f, radius * 0.45f)
        )
        if (isSelected) {
            drawCircle(
                color = Color(0xFFFEF08A),
                radius = 2f,
                center = center + Offset(radius * 0.5f, -radius * 0.2f)
            )
        }
    }
}

// -------------------------------------------------------------
// 3. CUSTOM 3D CHAT BUBBLE ICON
// -------------------------------------------------------------
@Composable
fun Custom3DChatBubbleIcon(isSelected: Boolean, size: Dp = 28.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 3D Speech Bubble Shape with Tail
        val bubblePath = Path().apply {
            val rx = w * 0.10f
            val ry = h * 0.12f
            val rw = w * 0.80f
            val rh = h * 0.62f
            val r = 24f

            moveTo(rx + r, ry)
            lineTo(rx + rw - r, ry)
            quadraticTo(rx + rw, ry, rx + rw, ry + r)
            lineTo(rx + rw, ry + rh - r)
            quadraticTo(rx + rw, ry + rh, rx + rw - r, ry + rh)
            // Tail start
            lineTo(rx + rw * 0.45f, ry + rh)
            lineTo(rx + rw * 0.20f, ry + rh + h * 0.16f)
            lineTo(rx + rw * 0.26f, ry + rh)
            lineTo(rx + r, ry + rh)
            quadraticTo(rx, ry + rh, rx, ry + rh - r)
            lineTo(rx, ry + r)
            quadraticTo(rx, ry, rx + r, ry)
            close()
        }

        // 3D Drop Shadow
        drawPath(
            path = bubblePath,
            color = if (isSelected) Color(0xFF1E1B4B).copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f)
        )

        // 3D Gradient Surface
        val bubbleBrush = if (isSelected) {
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFF818CF8),
                    Color(0xFF6366F1),
                    Color(0xFF4F46E5),
                    Color(0xFF312E81)
                ),
                center = Offset(w * 0.35f, h * 0.25f),
                radius = w * 0.7f
            )
        } else {
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFCBD5E1),
                    Color(0xFF64748B),
                    Color(0xFF334155)
                ),
                center = Offset(w * 0.35f, h * 0.25f),
                radius = w * 0.7f
            )
        }
        drawPath(path = bubblePath, brush = bubbleBrush)

        // 3D Rim Bevel
        drawPath(
            path = bubblePath,
            brush = Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.85f), Color.Transparent, Color(0xFF1E1B4B).copy(alpha = 0.6f))
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )

        // 3 Embossed Conversation Dots
        val dotY = h * 0.42f
        val dotRadius = 3.2f
        val dotOffsets = listOf(w * 0.32f, w * 0.50f, w * 0.68f)

        dotOffsets.forEach { dotX ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, if (isSelected) Color(0xFFC7D2FE) else Color(0xFFE2E8F0)),
                    center = Offset(dotX - 1f, dotY - 1f),
                    radius = dotRadius
                ),
                radius = dotRadius,
                center = Offset(dotX, dotY)
            )
        }

        // Top Specular Highlight
        drawOval(
            brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)),
            topLeft = Offset(w * 0.20f, h * 0.18f),
            size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.10f)
        )
    }
}

// -------------------------------------------------------------
// 4. CUSTOM 3D PROFILE CROWN / CREST ICON
// -------------------------------------------------------------
@Composable
fun Custom3DProfileCrownIcon(isSelected: Boolean, size: Dp = 28.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 3-Peak Golden Crown with 3D Depth
        val crownPath = Path().apply {
            moveTo(w * 0.16f, h * 0.34f)
            lineTo(w * 0.32f, h * 0.54f)
            lineTo(w * 0.50f, h * 0.22f) // Center tallest peak
            lineTo(w * 0.68f, h * 0.54f)
            lineTo(w * 0.84f, h * 0.34f)
            lineTo(w * 0.78f, h * 0.78f)
            lineTo(w * 0.22f, h * 0.78f)
            close()
        }

        // 3D Shadow
        drawPath(
            path = crownPath,
            color = if (isSelected) Color(0xFF78350F).copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.2f)
        )

        // 3D Gold Gradient Body
        val crownBrush = if (isSelected) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFFFBEB),
                    Color(0xFFFDE68A),
                    Color(0xFFF59E0B),
                    Color(0xFFD97706),
                    Color(0xFF92400E)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFF1F5F9),
                    Color(0xFFCBD5E1),
                    Color(0xFF64748B),
                    Color(0xFF334155)
                )
            )
        }
        drawPath(path = crownPath, brush = crownBrush)

        // Golden Base Cushion Ring
        val baseRect = androidx.compose.ui.geometry.RoundRect(
            left = w * 0.18f,
            top = h * 0.74f,
            right = w * 0.82f,
            bottom = h * 0.86f,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )
        drawPath(
            path = Path().apply { addRoundRect(baseRect) },
            brush = if (isSelected) Brush.verticalGradient(
                listOf(Color(0xFFFFE082), Color(0xFFFFA000), Color(0xFFB45309))
            ) else Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFF475569)))
        )

        // Gemstones on Crown Tips (Ruby, Diamond, Emerald)
        drawCircle(
            color = if (isSelected) Color(0xFFEF4444) else Color(0xFF94A3B8),
            radius = 3.2f,
            center = Offset(w * 0.16f, h * 0.34f)
        )
        drawCircle(
            color = if (isSelected) Color(0xFF38BDF8) else Color.White,
            radius = 4.2f,
            center = Offset(w * 0.50f, h * 0.22f)
        )
        drawCircle(
            color = if (isSelected) Color(0xFF10B981) else Color(0xFF94A3B8),
            radius = 3.2f,
            center = Offset(w * 0.84f, h * 0.34f)
        )

        // Center Gem Sparkle
        drawCircle(
            color = Color.White,
            radius = 1.6f,
            center = Offset(w * 0.49f, h * 0.21f)
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun PenziApp(viewModel: DatingViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.initSession(context)
    }
    val uiState by viewModel.uiState.collectAsState()

    Crossfade(
        targetState = uiState.authState,
        animationSpec = androidx.compose.animation.core.tween(220),
        label = "AuthFlow"
    ) { state ->
        when (state) {
            is AuthState.Splash -> {
                SplashScreen()
            }
            is AuthState.LoggedOut -> {
                AuthScreen(
                    onGoogleSignIn = { viewModel.signInWithGoogle() },
                    onEmailSignIn = { email, pass, onError -> viewModel.signInWithEmail(email, pass, onError) },
                    onStartRegistration = { email, pass, onError -> viewModel.startRegistration(email, pass, onError) }
                )
            }
            is AuthState.Onboarding -> {
                OnboardingScreen(
                    onComplete = { name, dob, gender, city, neighborhood, occ, intent, bio, interests, photo ->
                        viewModel.completeOnboarding(
                            name = name,
                            dob = dob,
                            gender = gender,
                            city = city,
                            neighborhood = neighborhood,
                            occupation = occ,
                            intent = intent,
                            bio = bio,
                            interests = interests,
                            photoUrl = photo
                        )
                    }
                )
            }
            is AuthState.Authenticated -> {
                val canNavigateBack = uiState.activeRelationType != null ||
                        uiState.selectedProfileDetail != null ||
                        uiState.activeChatPartnerId != null ||
                        uiState.recentMatchOverlay != null ||
                        uiState.activeMeSubScreen != null ||
                        uiState.selectedExploreIntent != null ||
                        uiState.tabBackStack.size > 1

                BackHandler(enabled = canNavigateBack) {
                    viewModel.handleBackPress()
                }

                if (uiState.activeRelationType != null) {
                    UserRelationListScreen(
                        relationType = uiState.activeRelationType,
                        titleOwnerName = uiState.activeRelationProfile?.name ?: "My Profile",
                        followingList = uiState.followingProfiles,
                        followersList = uiState.followersProfiles,
                        friendsList = uiState.friendsProfiles,
                        isFollowing = { id -> uiState.followingIds.contains(id) },
                        onToggleFollow = { profile -> viewModel.toggleFollow(profile) },
                        onProfileClick = { profile ->
                            viewModel.closeRelationScreen()
                            viewModel.openProfileDetail(profile)
                        },
                        onOpenChat = { profile ->
                            viewModel.closeRelationScreen()
                            viewModel.openChatWithMatch(profile)
                        },
                        onBack = { viewModel.closeRelationScreen() },
                        onExploreClick = {
                            viewModel.closeRelationScreen()
                            viewModel.selectTab("discover")
                        }
                    )
                } else if (uiState.selectedProfileDetail != null) {
                    ProfileDetailScreen(
                        profile = uiState.selectedProfileDetail!!,
                        onBack = { viewModel.closeProfileDetail() },
                        onChat = { viewModel.openChatWithMatch(uiState.selectedProfileDetail!!) },
                        isFollowed = uiState.followingIds.contains(uiState.selectedProfileDetail!!.id),
                        onFollow = { viewModel.toggleFollow(uiState.selectedProfileDetail!!) },
                        onOpenRelation = { relType ->
                            viewModel.openRelationScreen(relType, uiState.selectedProfileDetail)
                        }
                    )
                } else {
                    val totalUnread = uiState.conversations.sumOf { it.unreadCount }
                    val isInConversation = uiState.activeTab == "chat" && uiState.activeChatPartnerId != null
                    val isMeSubScreenActive = uiState.activeTab == "me" && uiState.activeMeSubScreen != null
                    val isInExploreCategory = uiState.activeTab == "explore" && uiState.selectedExploreIntent != null
                    val showBottomBar = !isInConversation && !isMeSubScreenActive && !isInExploreCategory

                    val navItems = listOf(
                        NavTabItem("discover", "Home", Icons.Default.Home, Icons.Outlined.Home, "nav_discover"),
                        NavTabItem("chat", "Chat", Icons.Default.Chat, Icons.Outlined.Chat, "nav_chat"),
                        NavTabItem("me", "Me", Icons.Default.Person, Icons.Outlined.Person, "nav_me")
                    )

                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            if (showBottomBar) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RectangleShape,
                                            spotColor = Color(0x14000000),
                                            ambientColor = Color(0x0A000000)
                                        ),
                                    shape = RectangleShape,
                                    color = Color.White
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .navigationBarsPadding()
                                    ) {
                                        // Top Hairline Divider
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color(0xFFE5E7EB).copy(alpha = 0.4f),
                                                            Color(0xFFD1D5DB),
                                                            Color(0xFFE5E7EB).copy(alpha = 0.4f)
                                                        )
                                                    )
                                                )
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp)
                                                .padding(horizontal = 6.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            navItems.forEach { item ->
                                                val isSelected = uiState.activeTab == item.id

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable { viewModel.selectTab(item.id) }
                                                        .padding(vertical = 2.dp)
                                                        .testTag(item.testTag),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    ThreeDNavIcon(
                                                        tabId = item.id,
                                                        isSelected = isSelected,
                                                        unreadCount = if (item.id == "chat") totalUnread else 0
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = item.label,
                                                        fontSize = 11.5.sp,
                                                        lineHeight = 14.sp,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                        color = if (isSelected) KenyanRed else Color(0xFF64748B)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            when (uiState.activeTab) {
                                "discover" -> {
                                    HomeScreen(
                                        userAccount = state.user,
                                        deck = uiState.currentDeck,
                                        matchedOverlay = uiState.recentMatchOverlay,
                                        likedProfileIds = uiState.likedProfileIds,
                                        isLoading = uiState.isLoading,
                                        onLike = { viewModel.swipeLike(it) },
                                        onPass = { viewModel.swipePass(it) },
                                        onSuperLike = { viewModel.swipeSuperLike(it) },
                                        onDismissMatch = { viewModel.dismissMatchOverlay() },
                                        onOpenChat = { viewModel.openChatWithMatch(it) },
                                        onSayHi = { profile ->
                                            val greeting = viewModel.sendSayHi(profile, openChat = true)
                                            android.widget.Toast.makeText(
                                                context,
                                                "👋 Sent to ${profile.name}: \"$greeting\"",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onOpenProfileDetail = { viewModel.openProfileDetail(it) },
                                        onRefreshProfiles = { viewModel.loadProfilesFromSupabase() },
                                        onOpenTaskCenter = {
                                            viewModel.selectTab("me")
                                            viewModel.openMeSubScreen(com.example.model.MeSubScreen.TASKS)
                                        }
                                    )
                                }
                                "explore" -> {
                                    ExploreScreen(
                                        selectedIntent = uiState.selectedExploreIntent,
                                        profiles = uiState.exploreProfiles,
                                        likedProfileIds = uiState.likedProfileIds,
                                        onSelectIntent = { viewModel.selectExploreCategory(it) },
                                        onProfileClick = { viewModel.openProfileDetail(it) },
                                        onLikeProfile = { viewModel.swipeLike(it) }
                                    )
                                }
                                "chat" -> {
                                    ChatScreen(
                                        conversations = uiState.conversations,
                                        matchedProfiles = uiState.matchedProfiles,
                                        activePartnerId = uiState.activeChatPartnerId,
                                        onOpenConversation = { viewModel.openChatConversation(it) },
                                        onCloseConversation = { viewModel.closeChatConversation() },
                                        onSendMessage = { id, txt, img -> viewModel.sendChatMessage(id, txt, img) },
                                        onRefreshConversations = {
                                            viewModel.loadProfilesFromSupabase()
                                            viewModel.refreshCurrentUserProfile()
                                        }
                                    )
                                }
                                "me" -> {
                                    MeScreen(
                                        user = state.user,
                                        onUpdateProfile = { viewModel.updateMyProfile(it) },
                                        onLogout = { viewModel.logout() },
                                        activeMeSubScreen = uiState.activeMeSubScreen,
                                        onOpenMeSubScreen = { viewModel.openMeSubScreen(it) },
                                        onCloseMeSubScreen = { viewModel.closeMeSubScreen() },
                                        onVerifyComplete = { viewModel.verifyCurrentUser() },
                                        onUploadPhoto = viewModel::uploadOnboardingPhoto,
                                        followingCount = uiState.followingProfiles.size.coerceAtLeast(uiState.followingIds.size),
                                        followersCount = uiState.followersProfiles.size.coerceAtLeast(uiState.followersIds.size),
                                        friendsCount = uiState.friendsProfiles.size.coerceAtLeast(uiState.friendsIds.size),
                                        followingList = uiState.followingProfiles,
                                        followersList = uiState.followersProfiles,
                                        friendsList = uiState.friendsProfiles,
                                        isFollowing = { id -> uiState.followingIds.contains(id) },
                                        onToggleFollow = { profile -> viewModel.toggleFollow(profile) },
                                        onProfileClick = { profile -> viewModel.openProfileDetail(profile) },
                                        onOpenChat = { profile -> viewModel.openChatWithMatch(profile) },
                                        onExploreClick = { viewModel.selectTab("discover") }
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
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_anim")
    
    // Heartbeat scale animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.09f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )

    // Ambient glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Ring expansion
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glow circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            
            // Outer ambient pulse ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEA580C).copy(alpha = 0.25f * glowAlpha),
                        Color(0xFF991B1B).copy(alpha = 0.12f * glowAlpha),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.75f * ringScale
                ),
                center = center,
                radius = size.width * 0.75f * ringScale
            )

            // Inner warm amber glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF59E0B).copy(alpha = 0.30f * glowAlpha),
                        Color(0xFFB91C1C).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.45f
                ),
                center = center,
                radius = size.width * 0.45f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Glowing Heart Logo Icon with Dual Golden Halo
            Box(
                modifier = Modifier
                    .size(116.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Pulse Ring
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .scale(ringScale)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    SavannahGold.copy(alpha = 0.6f * glowAlpha),
                                    Color(0xFFEA580C).copy(alpha = 0.2f),
                                    SavannahGold.copy(alpha = 0.6f * glowAlpha)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Main 3D Glowing Card
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .scale(scale)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = Color(0xFFF59E0B),
                            ambientColor = Color(0xFFEA580C)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEA580C),
                                    Color(0xFFB91C1C),
                                    Color(0xFF7F1D1D)
                                )
                            )
                        )
                        .border(
                            width = 1.8.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    SavannahGoldLight.copy(alpha = 0.9f),
                                    SavannahGold.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Penzi",
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                spotColor = SavannahGold
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // App Brand Name with Gold Accent Shadow
            Text(
                text = "Penzi",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tagline
            Text(
                text = "KENYAN DATING & SOCIAL",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SavannahGoldLight,
                letterSpacing = 2.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Glass Capsule with Kenyan Flag & Rounded Corners
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, SavannahGold.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🇰🇪", fontSize = 16.sp)
                    Text(
                        text = "Find Your Kenyan Match",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }
            }
        }

        // Attribution Badge at bottom with Rounded Corners
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.35f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Penzi Kenya",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SavannahGoldLight
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "by MCc",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

