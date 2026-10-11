package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MaasaiBlue
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

data class IncomeTransaction(
    val id: String,
    val title: String,
    val date: String,
    val amountDiamonds: Int,
    val amountKes: Int,
    val isWithdrawal: Boolean,
    val mpesaRef: String? = null
)

@Composable
fun IncomeScreen(
    currentDiamonds: Int,
    onBack: () -> Unit,
    onWithdrawSuccess: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    var diamondBalance by remember { mutableIntStateOf(currentDiamonds) }
    var selectedPresetAmount by remember { mutableIntStateOf(250) }
    var customAmountText by remember { mutableStateOf("250") }
    var mpesaPhoneNumber by remember { mutableStateOf("0712345678") }
    var isProcessingWithdrawal by remember { mutableStateOf(false) }
    var withdrawalSuccessMessage by remember { mutableStateOf<String?>(null) }

    val transactions = remember {
        mutableListOf(
            IncomeTransaction("tx_1", "Gift: Diani Sunset Cocktail", "Today, 11:20 AM", 150, 150, false),
            IncomeTransaction("tx_2", "Daily Banter Reward", "Yesterday, 8:45 PM", 50, 50, false),
            IncomeTransaction("tx_3", "M-Pesa Payout to $mpesaPhoneNumber", "Aug 24, 2026", 400, 400, true, "QKD8921X7"),
            IncomeTransaction("tx_4", "Super Like Diamond Tip", "Aug 22, 2026", 200, 200, false),
            IncomeTransaction("tx_5", "Task Center Bonus", "Aug 20, 2026", 100, 100, false)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // -------------------------------------------------------------
            // SEAMLESS TOP HEADER
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("income_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "INCOME & DIAMONDS",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // -------------------------------------------------------------
            // HERO CARD: BALANCE & M-PESA CONVERSION
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Available Balance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$diamondBalance",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0C5D7D)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "💎", fontSize = 24.sp)
                            }
                        }

                        FacetedDiamond3D(size = 56.dp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Equivalent KES info box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = MpesaGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Equivalent Cash:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF065F46)
                                )
                            }
                            Text(
                                text = "KES ${diamondBalance}.00",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MpesaGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Conversion: 1 💎 = KES 1.00",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = "Disbursement: Safaricom M-Pesa",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MpesaGreen
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // WITHDRAW TO M-PESA SECTION
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MpesaGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "M-Pesa",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Withdraw to M-Pesa",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = "Instant payout to any Safaricom line",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Select Withdrawal Amount (KES)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Amount Chips
                    val presets = listOf(100, 250, 500, diamondBalance.coerceAtLeast(100))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.distinct().forEach { amt ->
                            val isSelected = selectedPresetAmount == amt
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedPresetAmount = amt
                                        customAmountText = amt.toString()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MpesaGreen.copy(alpha = 0.12f) else Color(0xFFF3F4F6),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MpesaGreen) else null
                            ) {
                                Text(
                                    text = if (amt == diamondBalance && amt > 500) "All ($amt)" else "KES $amt",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) MpesaGreen else Color(0xFF4B5563),
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Amount Text Field
                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { input ->
                            customAmountText = input.filter { it.isDigit() }
                            selectedPresetAmount = customAmountText.toIntOrNull() ?: 0
                        },
                        label = { Text("Withdraw Amount (KES / 💎)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MpesaGreen,
                            focusedLabelColor = MpesaGreen,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // M-Pesa Phone Number Field
                    OutlinedTextField(
                        value = mpesaPhoneNumber,
                        onValueChange = { mpesaPhoneNumber = it },
                        label = { Text("M-Pesa Registered Number") },
                        placeholder = { Text("0712345678") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MpesaGreen,
                            focusedLabelColor = MpesaGreen,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Withdraw Button
                    Button(
                        onClick = {
                            val withdrawAmt = customAmountText.toIntOrNull() ?: 0
                            if (withdrawAmt <= 0) {
                                Toast.makeText(context, "Please enter a valid withdrawal amount.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (withdrawAmt > diamondBalance) {
                                Toast.makeText(context, "Insufficient diamonds balance (You have $diamondBalance 💎)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (mpesaPhoneNumber.length < 10) {
                                Toast.makeText(context, "Please enter a valid 10-digit M-Pesa phone number.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isProcessingWithdrawal = true
                            // Simulate instant B2C disbursement
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isProcessingWithdrawal = false
                                diamondBalance -= withdrawAmt
                                onWithdrawSuccess(withdrawAmt)
                                val ref = "QK" + (100000..999999).random()
                                transactions.add(
                                    0,
                                    IncomeTransaction(
                                        id = "tx_${System.currentTimeMillis()}",
                                        title = "M-Pesa Payout to $mpesaPhoneNumber",
                                        date = "Just now",
                                        amountDiamonds = withdrawAmt,
                                        amountKes = withdrawAmt,
                                        isWithdrawal = true,
                                        mpesaRef = ref
                                    )
                                )
                                withdrawalSuccessMessage = "KES $withdrawAmt.00 successfully sent to $mpesaPhoneNumber (Ref: $ref)!"
                                Toast.makeText(context, "M-Pesa Payout of KES $withdrawAmt dispatched!", Toast.LENGTH_LONG).show()
                            }, 1500)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_mpesa_withdrawal"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                        enabled = !isProcessingWithdrawal
                    ) {
                        if (isProcessingWithdrawal) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Processing Payout...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Withdraw KES ${customAmountText.ifBlank { "0" }} to M-Pesa",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Success Feedback Alert
                    withdrawalSuccessMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MpesaGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = msg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------------------------------------------
            // TRANSACTION & EARNING HISTORY
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaasaiBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Earnings & Payout History",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    transactions.forEachIndexed { index, tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (tx.mpesaRef != null) "${tx.date} • Ref: ${tx.mpesaRef}" else tx.date,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Text(
                                text = if (tx.isWithdrawal) "- KES ${tx.amountKes}" else "+ ${tx.amountDiamonds} 💎",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (tx.isWithdrawal) Color(0xFFDC2626) else MpesaGreen
                            )
                        }

                        if (index < transactions.size - 1) {
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = Color(0xFFF3F4F6)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
