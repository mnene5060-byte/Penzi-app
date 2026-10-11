package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DatingProfile
import com.example.model.Gender
import com.example.model.UserAccount
import com.example.model.MeSubScreen
import com.example.ui.screens.UserRelationListScreen
import com.example.ui.theme.KenyanSunsetOrange
import com.example.ui.theme.MaasaiBlue
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.SavannahGold
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

@Composable
fun MeScreen(
    user: UserAccount,
    onUpdateProfile: (UserAccount) -> Unit,
    onLogout: () -> Unit,
    activeMeSubScreen: MeSubScreen?,
    onOpenMeSubScreen: (MeSubScreen) -> Unit,
    onCloseMeSubScreen: () -> Unit,
    onVerifyComplete: (() -> Unit)? = null,
    onUploadPhoto: (String, Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, _ -> },
    followingCount: Int = 0,
    followersCount: Int = 0,
    friendsCount: Int = 0,
    followingList: List<DatingProfile> = emptyList(),
    followersList: List<DatingProfile> = emptyList(),
    friendsList: List<DatingProfile> = emptyList(),
    isFollowing: (String) -> Boolean = { false },
    onToggleFollow: (DatingProfile) -> Unit = {},
    onProfileClick: (DatingProfile) -> Unit = {},
    onOpenChat: (DatingProfile) -> Unit = {},
    onExploreClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    if (activeMeSubScreen == MeSubScreen.INCOME) {
        IncomeScreen(
            currentDiamonds = user.diamonds,
            onBack = onCloseMeSubScreen,
            onWithdrawSuccess = { withdrawn ->
                val newDiamonds = (user.diamonds - withdrawn).coerceAtLeast(0)
                onUpdateProfile(user.copy(diamonds = newDiamonds))
            }
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.FOLLOWING || activeMeSubScreen == MeSubScreen.FOLLOWERS || activeMeSubScreen == MeSubScreen.FRIENDS) {
        val relType = when (activeMeSubScreen) {
            MeSubScreen.FOLLOWING -> com.example.model.RelationType.FOLLOWING
            MeSubScreen.FOLLOWERS -> com.example.model.RelationType.FOLLOWERS
            else -> com.example.model.RelationType.FRIENDS
        }
        UserRelationListScreen(
            relationType = relType,
            titleOwnerName = "My Profile",
            followingList = followingList,
            followersList = followersList,
            friendsList = friendsList,
            isFollowing = isFollowing,
            onToggleFollow = onToggleFollow,
            onProfileClick = onProfileClick,
            onOpenChat = onOpenChat,
            onBack = onCloseMeSubScreen,
            onExploreClick = onExploreClick
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.PERSONAL_INFO) {
        PersonalInfoScreen(
            user = user,
            onBack = onCloseMeSubScreen,
            onUpdateProfile = onUpdateProfile,
            onUploadPhoto = onUploadPhoto
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.VERIFICATION) {
        VerificationScreen(
            user = user,
            isAlreadyVerified = user.isVerified,
            onBack = onCloseMeSubScreen,
            onVerifyComplete = {
                onVerifyComplete?.invoke()
                onUpdateProfile(user.copy(isVerified = true))
            }
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.WALLET) {
        WalletScreen(
            currentCoins = user.coins,
            onBack = onCloseMeSubScreen,
            onCoinsAdded = { added ->
                onUpdateProfile(user.copy(coins = user.coins + added))
            }
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.SETTINGS) {
        SystemSettingsScreen(
            onBack = onCloseMeSubScreen,
            onLogout = onLogout
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.SUPPORT) {
        CustomerSupportScreen(
            onBack = onCloseMeSubScreen
        )
        return
    }

    if (activeMeSubScreen == MeSubScreen.TASKS) {
        val userSession = remember { com.example.data.UserSessionManager(context) }
        TaskCenterScreen(
            onBack = onCloseMeSubScreen,
            userId = user.id,
            authToken = userSession.getSavedAuthToken(),
            onCoinsEarned = { added ->
                onUpdateProfile(user.copy(coins = user.coins + added))
            }
        )
        return
    }

    val displayPenziId = if (user.penziId.isNotBlank()) user.penziId else "319720115"
    val displayName = user.name.ifBlank { user.email.substringBefore("@").replaceFirstChar { it.uppercase() } }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // -------------------------------------------------------------
            // TOP HEADER SECTION + DUAL CARDS
            // -------------------------------------------------------------
            val cardHeight = 78.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = PenziHeaderGradient
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 14.dp, bottom = 18.dp)
                ) {
                // Top User Profile Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // User Name with Chevron
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenMeSubScreen(MeSubScreen.PERSONAL_INFO) }
                        ) {
                            Text(
                                text = displayName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Badges Row: ♂ 21 | Kenya | Unverified with Rounded Corners
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Gender & Age Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (user.gender == Gender.FEMALE) Color(0xFFE11D48) else Color(0xFF2563EB)
                            ) {
                                Text(
                                    text = "${if (user.gender == Gender.FEMALE) "♀" else "♂"} ${if (user.age > 0) user.age else 21}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }

                            // Region Badge (e.g., Kilimani / Nairobi)
                            val regionLabel = user.neighborhood.ifBlank { user.city.ifBlank { "Kenya" } }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF059669) // Kenyan Emerald Green
                            ) {
                                Text(
                                    text = "📍 $regionLabel",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }

                            // Verification Badge (Unverified / Verified)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (user.isVerified) Color(0xFF0284C7) else Color(0xFFF59E0B)),
                                modifier = Modifier.clickable {
                                    if (!user.isVerified) {
                                        onOpenMeSubScreen(MeSubScreen.VERIFICATION)
                                    } else {
                                        Toast.makeText(context, "Your profile is verified!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = if (user.isVerified) Color(0xFF0284C7) else Color(0xFFD97706),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (user.isVerified) "Verified" else "Verify",
                                        color = if (user.isVerified) Color(0xFF0284C7) else Color(0xFFD97706),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // User ID & Copy Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Penzi ID", displayPenziId)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "ID $displayPenziId copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(
                                text = "ID:$displayPenziId",
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy ID",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Cosmic Ring Avatar Frame
                    CelestialAvatarFrame(
                        photoUrl = user.photoUrl,
                        modifier = Modifier
                            .size(76.dp)
                            .clickable { onOpenMeSubScreen(MeSubScreen.PERSONAL_INFO) }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Row: Friends | Following | Followers | Visitors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MeStatColumn(
                        count = friendsCount.toString(),
                        label = "Friends",
                        onClick = { onOpenMeSubScreen(MeSubScreen.FRIENDS) }
                    )
                    MeStatColumn(
                        count = followingCount.toString(),
                        label = "Following",
                        onClick = { onOpenMeSubScreen(MeSubScreen.FOLLOWING) }
                    )
                    MeStatColumn(
                        count = followersCount.toString(),
                        label = "Followers",
                        onClick = { onOpenMeSubScreen(MeSubScreen.FOLLOWERS) }
                    )
                    MeStatColumn(
                        count = "12",
                        label = "Visitors",
                        onClick = { Toast.makeText(context, "12 members visited your profile today!", Toast.LENGTH_SHORT).show() }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // -------------------------------------------------------------
                // DUAL CARDS ROW: COINS & INCOME (Reduced size, overlaps 3/4)
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Coins Card (Warm Cream / Pastel Yellow)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(cardHeight)
                            .clickable { onOpenMeSubScreen(MeSubScreen.WALLET) }
                            .testTag("coins_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2CE)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    GoldStarCoin3D(size = 18.dp)
                                    Text(
                                        text = "${user.coins} Coins",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE05300)
                                    )
                                }

                                // Reduced Recharge Pill Button
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFB800),
                                    modifier = Modifier.clickable { onOpenMeSubScreen(MeSubScreen.WALLET) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Recharge",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            GoldStarCoin3D(size = 28.dp)
                        }
                    }

                    // 2. Income Card (Soft Pastel Sky Blue)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(cardHeight)
                            .clickable { onOpenMeSubScreen(MeSubScreen.INCOME) }
                            .testTag("income_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDDF7FF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = "Income",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0C5D7D)
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFC7EFFF)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${user.diamonds}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0369A1)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "💎", fontSize = 9.sp)
                                    }
                                }
                            }

                            FacetedDiamond3D(size = 30.dp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -------------------------------------------------------------
        // QUICK ACTIONS ROW 1: Tasks | Blast | Store | Bag
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExactMeActionButton(
                label = "Tasks",
                onClick = { onOpenMeSubScreen(MeSubScreen.TASKS) }
            ) {
                Task3DIcon()
            }

            ExactMeActionButton(
                label = "Blast",
                onClick = {
                    Toast.makeText(context, "Blast: Broadcast your profile to singles across Kenya!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Blast3DIcon()
            }

            ExactMeActionButton(
                label = "Store",
                onClick = {
                    Toast.makeText(context, "Store: Explore virtual gifts and interactive items!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Store3DIcon()
            }

            ExactMeActionButton(
                label = "Bag",
                onClick = {
                    Toast.makeText(context, "Bag: Check your inventory of received gifts and boosts!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Bag3DIcon()
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // -------------------------------------------------------------
        // "Other" SECTION
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = "Other",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Actions Row: Income | Verify
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExactMeActionButton(
                    label = "Income",
                    onClick = { onOpenMeSubScreen(MeSubScreen.INCOME) }
                ) {
                    IncomeVault3DIcon()
                }

                ExactMeActionButton(
                    label = "Verify",
                    onClick = {
                        onOpenMeSubScreen(MeSubScreen.VERIFICATION)
                    }
                ) {
                    VerifyShield3DIcon()
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // -------------------------------------------------------------
            // SUPPORT & SETTINGS CARD
            // -------------------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Support Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMeSubScreen(MeSubScreen.SUPPORT) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SupportHeadset3DIcon(size = 44.dp)
                            Text(
                                text = "Support",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E1E)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Support",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.8.dp,
                        color = Color(0xFFF2F2F2)
                    )

                    // Settings Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMeSubScreen(MeSubScreen.SETTINGS) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SettingsGear3DIcon(size = 44.dp)
                            Text(
                                text = "Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E1E)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Settings",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }

    // -------------------------------------------------------------
    // STICKY PINNED TOP HEADER: Appears when user scrolls down in Me Screen
    // Keeps user's name at the top and restores to normal when scrolled up
    // -------------------------------------------------------------
    AnimatedVisibility(
        visible = scrollState.value > 120,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier.align(Alignment.TopCenter)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shadowElevation = 6.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PenziHeaderGradient)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.clickable { onOpenMeSubScreen(MeSubScreen.PERSONAL_INFO) }
                    ) {
                        // User Avatar
                        AsyncImage(
                            model = user.photoUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                            contentDescription = displayName,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, SavannahGold, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = displayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (user.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = SavannahGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "ID: $displayPenziId",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onOpenMeSubScreen(MeSubScreen.SETTINGS) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Settings",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
}

// -------------------------------------------------------------
// STAT COLUMN ITEM
// -------------------------------------------------------------
@Composable
private fun MeStatColumn(
    count: String,
    label: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

// -------------------------------------------------------------
// EXACT ACTION BUTTON CONTAINER WITH SQUIRCLE
// -------------------------------------------------------------
@Composable
private fun ExactMeActionButton(
    label: String,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "ActionScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        iconContent()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2B2B2B)
        )
    }
}

// -------------------------------------------------------------
// CELESTIAL SUNBURST AVATAR FRAME
// -------------------------------------------------------------
@Composable
fun CelestialAvatarFrame(
    photoUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Solar Ring Frame with Beads and Top Sparkling Diamond Star
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val ringRadius = size.width * 0.42f

            // Golden Glow Outer Ring
            drawCircle(
                color = Color(0xFFFFD580).copy(alpha = 0.35f),
                radius = ringRadius + 4f,
                style = Stroke(width = 6f)
            )

            // Primary Solar Ring
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFFFFE082),
                        Color(0xFFFFA726),
                        Color(0xFFFFD54F),
                        Color(0xFFFFB74D),
                        Color(0xFFFFE082)
                    )
                ),
                radius = ringRadius,
                style = Stroke(width = 4f)
            )

            // Orbital Beads around the ring
            val beadCount = 12
            for (i in 0 until beadCount) {
                val angle = Math.toRadians((i * (360.0 / beadCount) - 90))
                val bx = center.x + (ringRadius * Math.cos(angle)).toFloat()
                val by = center.y + (ringRadius * Math.sin(angle)).toFloat()

                // Skip top and bottom for special accents
                if (i == 0 || i == 6) continue

                drawCircle(
                    color = Color(0xFFFFE082),
                    radius = 2.2f,
                    center = Offset(bx, by)
                )
            }

            // Bottom Center Glowing Bead
            val bottomAngle = Math.toRadians(90.0)
            val btx = center.x + (ringRadius * Math.cos(bottomAngle)).toFloat()
            val bty = center.y + (ringRadius * Math.sin(bottomAngle)).toFloat()
            drawCircle(
                color = Color(0xFFFF8F00),
                radius = 5.5f,
                center = Offset(btx, bty)
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(btx, bty)
            )

            // Top Apex 4-Point Radiant Diamond Star
            val topAngle = Math.toRadians(-90.0)
            val tx = center.x + (ringRadius * Math.cos(topAngle)).toFloat()
            val ty = center.y + (ringRadius * Math.sin(topAngle)).toFloat()

            // Star Diamond Path
            val starPath = Path().apply {
                val starSize = 13f
                moveTo(tx, ty - starSize)
                quadraticTo(tx, ty, tx + starSize, ty)
                quadraticTo(tx, ty, tx, ty + starSize)
                quadraticTo(tx, ty, tx - starSize, ty)
                quadraticTo(tx, ty, tx, ty - starSize)
                close()
            }
            drawPath(
                path = starPath,
                color = Color.White
            )
            drawCircle(
                color = Color(0xFFFFF9C4),
                radius = 2.5f,
                center = Offset(tx, ty)
            )
        }

        // Inner Avatar Circle
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
        ) {
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Cosmic Mountain Fallback Scene
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Dark celestial night sky
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF4C0519))
                        )
                    )
                    // Red crescent / blood moon
                    drawCircle(
                        color = Color(0xFFDC2626),
                        radius = size.width * 0.22f,
                        center = Offset(size.width * 0.5f, size.height * 0.35f)
                    )
                    // Mountain silhouette
                    val mtnPath = Path().apply {
                        moveTo(0f, size.height)
                        lineTo(size.width * 0.3f, size.height * 0.45f)
                        lineTo(size.width * 0.55f, size.height * 0.35f)
                        lineTo(size.width * 0.85f, size.height * 0.55f)
                        lineTo(size.width, size.height * 0.48f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path = mtnPath, color = Color(0xFF090D16))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3D GOLD STAR COIN
// -------------------------------------------------------------
@Composable
fun GoldStarCoin3D(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = this.size.width * 0.46f

        // Outer 3D Drop Shadow
        drawCircle(
            color = Color(0xFFB45309).copy(alpha = 0.4f),
            radius = radius + 2f,
            center = center + Offset(0f, 2f)
        )

        // Outer Rim Gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFFB45309)),
                center = center - Offset(radius * 0.25f, radius * 0.25f),
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // Inner Inset Ring
        val innerRadius = radius * 0.78f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFDE7), Color(0xFFFBBF24), Color(0xFFD97706)),
                center = center - Offset(innerRadius * 0.2f, innerRadius * 0.2f),
                radius = innerRadius
            ),
            radius = innerRadius,
            center = center
        )

        // Center 5-Point Star
        val starRadius = innerRadius * 0.58f
        val starPath = Path().apply {
            val numPoints = 5
            for (i in 0 until numPoints * 2) {
                val r = if (i % 2 == 0) starRadius else starRadius * 0.45f
                val angle = Math.toRadians((i * 36 - 90).toDouble())
                val x = center.x + (r * Math.cos(angle)).toFloat()
                val y = center.y + (r * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(
            path = starPath,
            brush = Brush.linearGradient(
                listOf(Color.White, Color(0xFFFFF3B0), Color(0xFFF59E0B))
            )
        )

        // Top Specular Highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.8f), Color.Transparent),
                center = center - Offset(radius * 0.35f, radius * 0.35f),
                radius = radius * 0.4f
            ),
            radius = radius * 0.4f,
            center = center - Offset(radius * 0.35f, radius * 0.35f)
        )
    }
}

// -------------------------------------------------------------
// FACETED 3D CYAN DIAMOND
// -------------------------------------------------------------
@Composable
fun FacetedDiamond3D(modifier: Modifier = Modifier, size: Dp = 44.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val cx = w / 2f
        val topY = h * 0.15f
        val tableY = h * 0.38f
        val botY = h * 0.88f
        val leftX = w * 0.12f
        val rightX = w * 0.88f
        val tableLeftX = w * 0.28f
        val tableRightX = w * 0.72f

        // Top Table Facet
        val tablePath = Path().apply {
            moveTo(tableLeftX, topY)
            lineTo(tableRightX, topY)
            lineTo(w * 0.65f, tableY)
            lineTo(w * 0.35f, tableY)
            close()
        }
        drawPath(
            path = tablePath,
            brush = Brush.linearGradient(listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA)))
        )

        // Top Left Crown Facet
        val topLeftPath = Path().apply {
            moveTo(tableLeftX, topY)
            lineTo(leftX, tableY)
            lineTo(w * 0.35f, tableY)
            close()
        }
        drawPath(
            path = topLeftPath,
            brush = Brush.linearGradient(listOf(Color(0xFFB2EBF2), Color(0xFF4DD0E1)))
        )

        // Top Right Crown Facet
        val topRightPath = Path().apply {
            moveTo(tableRightX, topY)
            lineTo(rightX, tableY)
            lineTo(w * 0.65f, tableY)
            close()
        }
        drawPath(
            path = topRightPath,
            brush = Brush.linearGradient(listOf(Color(0xFF80DEEA), Color(0xFF26C6DA)))
        )

        // Bottom Pavilion Center Facet
        val botCenterPath = Path().apply {
            moveTo(w * 0.35f, tableY)
            lineTo(w * 0.65f, tableY)
            lineTo(cx, botY)
            close()
        }
        drawPath(
            path = botCenterPath,
            brush = Brush.linearGradient(listOf(Color(0xFF4DD0E1), Color(0xFF0097A7)))
        )

        // Bottom Pavilion Left Facet
        val botLeftPath = Path().apply {
            moveTo(leftX, tableY)
            lineTo(w * 0.35f, tableY)
            lineTo(cx, botY)
            close()
        }
        drawPath(
            path = botLeftPath,
            brush = Brush.linearGradient(listOf(Color(0xFF26C6DA), Color(0xFF00838F)))
        )

        // Bottom Pavilion Right Facet
        val botRightPath = Path().apply {
            moveTo(rightX, tableY)
            lineTo(w * 0.65f, tableY)
            lineTo(cx, botY)
            close()
        }
        drawPath(
            path = botRightPath,
            brush = Brush.linearGradient(listOf(Color(0xFF00ACC1), Color(0xFF006064)))
        )

        // Facet Outline Glare
        val fullOutline = Path().apply {
            moveTo(tableLeftX, topY)
            lineTo(tableRightX, topY)
            lineTo(rightX, tableY)
            lineTo(cx, botY)
            lineTo(leftX, tableY)
            close()
        }
        drawPath(
            path = fullOutline,
            color = Color.White.copy(alpha = 0.8f),
            style = Stroke(width = 1.8f)
        )

        // Top Left Sparkle
        drawCircle(
            color = Color.White,
            radius = 2.5f,
            center = Offset(tableLeftX + 2f, topY + 2f)
        )
    }
}

// -------------------------------------------------------------
// EXACT 3D ACTION ICONS
// -------------------------------------------------------------

// 1. Tasks Icon (Mint Squircle + 3D Clipboard with Checklist)
@Composable
fun Task3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8F8F0)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Yellow Clipboard Base
            val boardRect = androidx.compose.ui.geometry.RoundRect(
                left = w * 0.16f,
                top = h * 0.18f,
                right = w * 0.84f,
                bottom = h * 0.94f,
                cornerRadius = CornerRadius(6f, 6f)
            )
            val boardPath = Path().apply { addRoundRect(boardRect) }
            drawPath(
                path = boardPath,
                brush = Brush.verticalGradient(listOf(Color(0xFFFFF3B0), Color(0xFFFBBF24), Color(0xFFD97706)))
            )

            // Inner White Paper Sheet
            val paperRect = androidx.compose.ui.geometry.RoundRect(
                left = w * 0.24f,
                top = h * 0.26f,
                right = w * 0.76f,
                bottom = h * 0.86f,
                cornerRadius = CornerRadius(3f, 3f)
            )
            val paperPath = Path().apply { addRoundRect(paperRect) }
            drawPath(path = paperPath, color = Color.White)

            // Checklist lines & ticks
            val lineBrush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)))
            drawRoundRect(
                brush = lineBrush,
                topLeft = Offset(w * 0.32f, h * 0.42f),
                size = Size(w * 0.36f, 3f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                brush = lineBrush,
                topLeft = Offset(w * 0.32f, h * 0.56f),
                size = Size(w * 0.36f, 3f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                brush = lineBrush,
                topLeft = Offset(w * 0.32f, h * 0.70f),
                size = Size(w * 0.36f, 3f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Top Metallic Orange Binder Clip
            val clipRect = androidx.compose.ui.geometry.RoundRect(
                left = w * 0.34f,
                top = h * 0.10f,
                right = w * 0.66f,
                bottom = h * 0.25f,
                cornerRadius = CornerRadius(4f, 4f)
            )
            val clipPath = Path().apply { addRoundRect(clipRect) }
            drawPath(
                path = clipPath,
                brush = Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFF1F2937)))
            )
        }
    }
}

// 2. Blast Icon (Warm Amber Squircle + 3D Megaphone with Stars)
@Composable
fun Blast3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFF7ED)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Megaphone Cone Body
            val conePath = Path().apply {
                moveTo(w * 0.28f, h * 0.44f)
                lineTo(w * 0.72f, h * 0.24f)
                lineTo(w * 0.72f, h * 0.76f)
                lineTo(w * 0.28f, h * 0.56f)
                close()
            }
            drawPath(
                path = conePath,
                brush = Brush.verticalGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFFB91C1C)))
            )

            // Megaphone Front Bell
            drawOval(
                brush = Brush.horizontalGradient(listOf(Color(0xFFFED7AA), Color(0xFFF97316))),
                topLeft = Offset(w * 0.66f, h * 0.24f),
                size = Size(w * 0.14f, h * 0.52f)
            )

            // Megaphone Rear Base & Handle
            drawRoundRect(
                color = Color(0xFF475569),
                topLeft = Offset(w * 0.18f, h * 0.42f),
                size = Size(w * 0.12f, h * 0.16f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            val handlePath = Path().apply {
                moveTo(w * 0.38f, h * 0.54f)
                lineTo(w * 0.34f, h * 0.84f)
                lineTo(w * 0.46f, h * 0.84f)
                lineTo(w * 0.48f, h * 0.54f)
                close()
            }
            drawPath(path = handlePath, color = Color(0xFFF97316))

            // Burst Star / Sound Sparks
            drawCircle(color = Color(0xFFFBBF24), radius = 3f, center = Offset(w * 0.85f, h * 0.28f))
            drawCircle(color = Color(0xFFF59E0B), radius = 2f, center = Offset(w * 0.88f, h * 0.55f))
            drawCircle(color = Color(0xFFFDE047), radius = 2.5f, center = Offset(w * 0.80f, h * 0.75f))
        }
    }
}

// 3. Store Icon (Soft Sky-Blue Squircle + 3D Market Storefront)
@Composable
fun Store3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE6F4FE)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Store Box Base
            val storeBase = androidx.compose.ui.geometry.RoundRect(
                left = w * 0.18f,
                top = h * 0.44f,
                right = w * 0.82f,
                bottom = h * 0.88f,
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawPath(
                path = Path().apply { addRoundRect(storeBase) },
                brush = Brush.verticalGradient(listOf(Color(0xFFFFF3B0), Color(0xFFF59E0B)))
            )

            // Storefront Display Window
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(w * 0.28f, h * 0.54f),
                size = Size(w * 0.44f, h * 0.26f),
                cornerRadius = CornerRadius(3f, 3f)
            )

            // Striped Awning Canopy (Red & Yellow Stripes)
            val awningPath = Path().apply {
                moveTo(w * 0.12f, h * 0.44f)
                lineTo(w * 0.20f, h * 0.22f)
                lineTo(w * 0.80f, h * 0.22f)
                lineTo(w * 0.88f, h * 0.44f)
                close()
            }
            drawPath(
                path = awningPath,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFEF4444), Color(0xFFFBBF24),
                        Color(0xFFEF4444), Color(0xFFFBBF24),
                        Color(0xFFEF4444)
                    )
                )
            )
        }
    }
}

// 4. Bag Icon (Pink Squircle + 3D Red Gift Handbag with Gold Lock)
@Composable
fun Bag3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFCEEF0)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Gold Handle Arc
            val handlePath = Path().apply {
                moveTo(w * 0.36f, h * 0.36f)
                cubicTo(w * 0.36f, h * 0.12f, w * 0.64f, h * 0.12f, w * 0.64f, h * 0.36f)
            }
            drawPath(
                path = handlePath,
                brush = Brush.linearGradient(listOf(Color(0xFFFFE082), Color(0xFFFFA000))),
                style = Stroke(width = 3.5f)
            )

            // Red/Coral Bag Trapezoid
            val bagPath = Path().apply {
                moveTo(w * 0.26f, h * 0.34f)
                lineTo(w * 0.74f, h * 0.34f)
                lineTo(w * 0.84f, h * 0.88f)
                lineTo(w * 0.16f, h * 0.88f)
                close()
            }
            drawPath(
                path = bagPath,
                brush = Brush.verticalGradient(listOf(Color(0xFFFF6B6B), Color(0xFFE53935), Color(0xFFB71C1C)))
            )

            // Gold Center Clasp / Padlock
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFB300))),
                radius = 3.5f,
                center = Offset(w * 0.5f, h * 0.58f)
            )
        }
    }
}

// 5. Wallet Icon (Lavender Squircle + 3D Leather Wallet with Gold Coin)
@Composable
fun Wallet3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF2EEFD)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Emerging Gold Coin
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFB300))),
                radius = 6.5f,
                center = Offset(w * 0.62f, h * 0.34f)
            )

            // Wallet Body (Dark Leather Navy/Black with Brown Tint)
            val walletRect = androidx.compose.ui.geometry.RoundRect(
                left = w * 0.14f,
                top = h * 0.32f,
                right = w * 0.86f,
                bottom = h * 0.84f,
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawPath(
                path = Path().apply { addRoundRect(walletRect) },
                brush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A)))
            )

            // Wallet Flap with Gold Button
            val flapPath = Path().apply {
                moveTo(w * 0.14f, h * 0.40f)
                lineTo(w * 0.62f, h * 0.40f)
                cubicTo(w * 0.72f, h * 0.40f, w * 0.72f, h * 0.68f, w * 0.62f, h * 0.68f)
                lineTo(w * 0.14f, h * 0.68f)
                close()
            }
            drawPath(
                path = flapPath,
                brush = Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
            )
            drawCircle(
                color = Color(0xFFFFFBEB),
                radius = 2.5f,
                center = Offset(w * 0.58f, h * 0.54f)
            )
        }
    }
}

// 6. VIP Store Icon (Cream Squircle + 3D Royal Golden Crown)
@Composable
fun VipCrown3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFF8E6)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // 3-Peak Golden Crown Path
            val crownPath = Path().apply {
                moveTo(w * 0.14f, h * 0.38f) // Left tip
                lineTo(w * 0.28f, h * 0.58f) // Left valley
                lineTo(w * 0.50f, h * 0.28f) // Center highest tip
                lineTo(w * 0.72f, h * 0.58f) // Right valley
                lineTo(w * 0.86f, h * 0.38f) // Right tip
                lineTo(w * 0.80f, h * 0.82f) // Bottom right
                lineTo(w * 0.20f, h * 0.82f) // Bottom left
                close()
            }
            drawPath(
                path = crownPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFA000), Color(0xFFE65100))
                )
            )

            // Gemstones on Crown Tips
            drawCircle(color = Color(0xFFEF4444), radius = 2.5f, center = Offset(w * 0.14f, h * 0.38f))
            drawCircle(color = Color(0xFF3B82F6), radius = 3f, center = Offset(w * 0.50f, h * 0.28f))
            drawCircle(color = Color(0xFFEF4444), radius = 2.5f, center = Offset(w * 0.86f, h * 0.38f))

            // Crown Base Gem Inlays
            drawCircle(color = Color(0xFFEF4444), radius = 2f, center = Offset(w * 0.35f, h * 0.75f))
            drawCircle(color = Color(0xFF10B981), radius = 2f, center = Offset(w * 0.50f, h * 0.75f))
            drawCircle(color = Color(0xFF3B82F6), radius = 2f, center = Offset(w * 0.65f, h * 0.75f))
        }
    }
}

// 7. Income Icon (Mint Squircle + 3D Treasure Chest / Vault with Gem Glow)
@Composable
fun IncomeVault3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8F8F4)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Vault Body
            val chestRect = androidx.compose.ui.geometry.RoundRect(
                left = w * 0.16f,
                top = h * 0.34f,
                right = w * 0.84f,
                bottom = h * 0.88f,
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawPath(
                path = Path().apply { addRoundRect(chestRect) },
                brush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A)))
            )

            // Gold Strapping Bands
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFFFA000))),
                topLeft = Offset(w * 0.22f, h * 0.34f),
                size = Size(w * 0.14f, h * 0.54f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFFFA000))),
                topLeft = Offset(w * 0.64f, h * 0.34f),
                size = Size(w * 0.14f, h * 0.54f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Top Glowing Blue Diamond
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFE0F7FA), Color(0xFF00E5FF), Color(0xFF0091EA))),
                radius = 5.5f,
                center = Offset(w * 0.50f, h * 0.30f)
            )

            // Golden Keyhole
            drawCircle(color = Color(0xFFFFD54F), radius = 2.5f, center = Offset(w * 0.50f, h * 0.58f))
            drawRect(color = Color(0xFFFFD54F), topLeft = Offset(w * 0.48f, h * 0.58f), size = Size(w * 0.04f, h * 0.10f))
        }
    }
}

// 8. Verify Icon (Sky-Blue Squircle + 3D Orange Shield with Checkmark)
@Composable
fun VerifyShield3DIcon(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8F4FE)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(26.dp)) {
            val w = this.size.width
            val h = this.size.height

            // 3D Orange Shield Path
            val shieldPath = Path().apply {
                moveTo(w * 0.50f, h * 0.14f)
                lineTo(w * 0.84f, h * 0.26f)
                lineTo(w * 0.84f, h * 0.58f)
                cubicTo(w * 0.84f, h * 0.78f, w * 0.50f, h * 0.90f, w * 0.50f, h * 0.90f)
                cubicTo(w * 0.50f, h * 0.90f, w * 0.16f, h * 0.78f, w * 0.16f, h * 0.58f)
                lineTo(w * 0.16f, h * 0.26f)
                close()
            }
            drawPath(
                path = shieldPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800), Color(0xFFE65100)),
                    center = Offset(w * 0.45f, h * 0.35f),
                    radius = w * 0.5f
                )
            )

            // Inner Gold Checkmark
            val checkPath = Path().apply {
                moveTo(w * 0.34f, h * 0.50f)
                lineTo(w * 0.46f, h * 0.64f)
                lineTo(w * 0.68f, h * 0.38f)
            }
            drawPath(
                path = checkPath,
                color = Color.White,
                style = Stroke(width = 3.5f)
            )
        }
    }
}

// 9. Support Headset Icon (Round Mint Container + 3D Colorful Headset)
@Composable
fun SupportHeadset3DIcon(modifier: Modifier = Modifier, size: Dp = 38.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFE6F8F0)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val w = this.size.width
            val h = this.size.height

            // Yellow Headband Arc
            val bandPath = Path().apply {
                moveTo(w * 0.22f, h * 0.54f)
                cubicTo(w * 0.22f, h * 0.14f, w * 0.78f, h * 0.14f, w * 0.78f, h * 0.54f)
            }
            drawPath(
                path = bandPath,
                brush = Brush.horizontalGradient(listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))),
                style = Stroke(width = 3.5f)
            )

            // Left & Right Ear Cushions (Red/Orange)
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFFF7A70), Color(0xFFEF4444))),
                topLeft = Offset(w * 0.14f, h * 0.44f),
                size = Size(w * 0.16f, h * 0.32f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFFF7A70), Color(0xFFEF4444))),
                topLeft = Offset(w * 0.70f, h * 0.44f),
                size = Size(w * 0.16f, h * 0.32f),
                cornerRadius = CornerRadius(3f, 3f)
            )

            // Microphone Boom
            val micPath = Path().apply {
                moveTo(w * 0.74f, h * 0.68f)
                cubicTo(w * 0.74f, h * 0.88f, w * 0.46f, h * 0.88f, w * 0.46f, h * 0.82f)
            }
            drawPath(
                path = micPath,
                color = Color(0xFF475569),
                style = Stroke(width = 2f)
            )
            drawCircle(color = Color(0xFF1E293B), radius = 2f, center = Offset(w * 0.46f, h * 0.82f))
        }
    }
}

// 10. Settings Gear Icon (Round Light Grey Container + 3D Gold Gear)
@Composable
fun SettingsGear3DIcon(modifier: Modifier = Modifier, size: Dp = 38.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFF0F4F8)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerR = this.size.width * 0.44f
            val innerR = outerR * 0.75f

            // Gear Teeth
            val teethCount = 8
            for (i in 0 until teethCount) {
                val angle = Math.toRadians((i * (360.0 / teethCount)))
                val tx = center.x + (outerR * Math.cos(angle)).toFloat()
                val ty = center.y + (outerR * Math.sin(angle)).toFloat()
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 3f,
                    center = Offset(tx, ty)
                )
            }

            // Gear Body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFFD97706)),
                    center = center - Offset(2.5f, 2.5f)
                ),
                radius = innerR,
                center = center
            )

            // Center Bearing Hole
            drawCircle(
                color = Color(0xFF1E293B),
                radius = innerR * 0.42f,
                center = center
            )
        }
    }
}
