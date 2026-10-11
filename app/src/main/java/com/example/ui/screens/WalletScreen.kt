package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

data class CoinPackage(
    val coins: Int,
    val kesPrice: Int,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    currentCoins: Int,
    onBack: () -> Unit,
    onCoinsAdded: (Int) -> Unit
) {
    val context = LocalContext.current
    var selectedPackageIndex by remember { mutableStateOf<Int?>(null) } // No auto-selected package
    var showMpesaPrompt by remember { mutableStateOf(false) }
    var showAgentChatDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("07") }
    var isProcessingPayment by remember { mutableStateOf(false) }

    val packages = listOf(
        CoinPackage(coins = 10, kesPrice = 1),
        CoinPackage(coins = 500, kesPrice = 80, isPopular = true),
        CoinPackage(coins = 1000, kesPrice = 160),
        CoinPackage(coins = 2000, kesPrice = 320),
        CoinPackage(coins = 5000, kesPrice = 800),
        CoinPackage(coins = 10000, kesPrice = 1600),
        CoinPackage(coins = 12500, kesPrice = 2000)
    )

    val selectedPkg = selectedPackageIndex?.let { packages.getOrNull(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient)
    ) {
        // -------------------------------------------------------------
        // SEAMLESS TOP APP BAR
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Wallet",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = { showHistoryDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Transaction History",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // SCROLLABLE CONTENT
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // -------------------------------------------------------------
            // TOP BALANCE CARD (Yellow/Gold Border & 3D Coin)
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDEB)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFE082)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        GoldStarCoin3D(size = 52.dp)

                        Column {
                            Text(
                                text = "$currentCoins",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF111827),
                                lineHeight = 36.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Current Coin Balance",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    // Currency Pill Badge: 🇰🇪 KES 🔄
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF111827),
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Currency set to Kenya Shillings (KES)", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🇰🇪 KES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Currency",
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------------------------------------------
            // SECTION HEADER: "Select Coin Package" & "Instant Delivery"
            // -------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Coin Package",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = "Instant Delivery",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // -------------------------------------------------------------
            // COIN PACKAGES GRID (Row 1, Row 2, Row 3)
            // -------------------------------------------------------------
            // Row 1: 10, 500, 1000
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in 0..2) {
                    CoinPackageGridItem(
                        pkg = packages[i],
                        isSelected = selectedPackageIndex == i,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPackageIndex = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: 2000, 5000, 10000
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in 3..5) {
                    CoinPackageGridItem(
                        pkg = packages[i],
                        isSelected = selectedPackageIndex == i,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPackageIndex = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: 12500 (Wide Centered Card)
            CoinPackageWideItem(
                pkg = packages[6],
                isSelected = selectedPackageIndex == 6,
                onClick = { selectedPackageIndex = 6 }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // -------------------------------------------------------------
            // SECTION: "Choose Payment Method"
            // -------------------------------------------------------------
            Text(
                text = "Choose Payment Method",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Coin Seller Card (Official Appointed Agents)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAgentChatDialog = true }
                    .testTag("coin_seller_option"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFC107)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Orange-Gold Squircle Handshake Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFF9800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = "Coin Seller",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Coin Seller",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        text = "Verified ✓",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Official appointed agents • Chat & buy",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    // Yellow "Chat" Pill Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFD600),
                        modifier = Modifier.clickable { showAgentChatDialog = true }
                    ) {
                        Text(
                            text = "Chat",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF111827),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. M-Pesa Express Instant Top-up Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMpesaPrompt = true }
                    .testTag("mpesa_express_option"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Green M-Pesa Squircle Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MpesaGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "M-Pesa",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "M-Pesa STK Push",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Instant prompt to Safaricom line",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    // Green "Pay" Pill Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedPkg != null) MpesaGreen else Color(0xFF9CA3AF),
                        modifier = Modifier.clickable {
                            if (selectedPkg != null) {
                                showMpesaPrompt = true
                            } else {
                                Toast.makeText(context, "Please select a coin package above first.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(
                            text = if (selectedPkg != null) "Pay KES ${selectedPkg.kesPrice}" else "Select Package",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // -------------------------------------------------------------
    // M-PESA STK PUSH PAYMENT DIALOG
    // -------------------------------------------------------------
    if (showMpesaPrompt && selectedPkg != null) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingPayment) showMpesaPrompt = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GoldStarCoin3D(size = 32.dp)
                    Column {
                        Text(
                            text = "Top Up ${selectedPkg.coins} Coins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "KES ${selectedPkg.kesPrice} • Safaricom M-Pesa",
                            fontSize = 12.sp,
                            color = MpesaGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Enter your Safaricom mobile number to receive an M-Pesa PIN prompt on your phone:",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563)
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("M-Pesa Number") },
                        placeholder = { Text("0712345678") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MpesaGreen,
                            focusedLabelColor = MpesaGreen,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    if (isProcessingPayment) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MpesaGreen,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Waiting for PIN entry...",
                                fontSize = 13.sp,
                                color = Color(0xFF374151),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isProcessingPayment = true
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            isProcessingPayment = false
                            showMpesaPrompt = false
                            onCoinsAdded(selectedPkg.coins)
                            Toast.makeText(
                                context,
                                "M-Pesa payment of KES ${selectedPkg.kesPrice} successful! ${selectedPkg.coins} Coins credited.",
                                Toast.LENGTH_LONG
                            ).show()
                        }, 1600)
                    },
                    enabled = !isProcessingPayment && phoneNumber.length >= 10,
                    colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Send STK Push", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showMpesaPrompt = false },
                    enabled = !isProcessingPayment,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------------------------------------------------------------
    // COIN SELLER AGENT CHAT DIALOG
    // -------------------------------------------------------------
    if (showAgentChatDialog) {
        AlertDialog(
            onDismissRequest = { showAgentChatDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9800)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text("Verified Coin Agent", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("24/7 Agent Support", fontSize = 11.sp, color = Color(0xFF2E7D32))
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Official certified Kenyan coin reseller agents can fulfill bulk coin orders via M-Pesa, Airtel Money, or bank transfer at discounted rates.",
                        fontSize = 13.sp,
                        color = Color(0xFF374151)
                    )
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFECB3))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Agent ID: AGENT_KE_0884", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFE65100))
                            Text("WhatsApp/Call: +254 700 888 999", fontSize = 12.sp, color = Color(0xFF5D4037))
                            Text("Status: Online & Ready", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAgentChatDialog = false
                        Toast.makeText(context, "Opening direct support chat with Agent...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Start Chat", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAgentChatDialog = false },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }

    // -------------------------------------------------------------
    // TRANSACTION HISTORY DIALOG
    // -------------------------------------------------------------
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFE65100))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coin History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("+300 Coins", "M-Pesa STK Recharge", "Today, 08:15 AM"),
                        Triple("-15 Coins", "Chat Message", "Yesterday"),
                        Triple("+500 Coins", "Welcome Bonus", "Aug 20")
                    ).forEach { (amt, desc, date) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(desc, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF111827))
                                Text(date, fontSize = 11.sp, color = Color(0xFF9CA3AF))
                            }
                            Text(
                                text = amt,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (amt.startsWith("+")) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHistoryDialog = false },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// GRID PACKAGE CARD ITEM
// -------------------------------------------------------------
@Composable
private fun CoinPackageGridItem(
    pkg: CoinPackage,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "PackageScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .height(118.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag("package_${pkg.coins}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFFDEB) else Color.White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB800))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEDEDED))
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GoldStarCoin3D(size = 30.dp)

            Text(
                text = "${pkg.coins}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827)
            )

            Text(
                text = "KES ${pkg.kesPrice}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
        }
    }
}

// -------------------------------------------------------------
// WIDE CENTERED PACKAGE CARD (12500 Coins)
// -------------------------------------------------------------
@Composable
private fun CoinPackageWideItem(
    pkg: CoinPackage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "WidePackageScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .height(112.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag("package_${pkg.coins}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFFDEB) else Color.White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB800))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEDEDED))
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GoldStarCoin3D(size = 32.dp)

            Text(
                text = "${pkg.coins}",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827)
            )

            Text(
                text = "KES ${pkg.kesPrice}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
        }
    }
}
