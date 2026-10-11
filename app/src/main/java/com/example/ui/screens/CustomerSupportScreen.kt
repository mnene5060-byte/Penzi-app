package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

@Composable
fun CustomerSupportScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isWhatsAppLoading by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showSafetyDialog by remember { mutableStateOf(false) }

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
            // SEAMLESS TOP HEADER WITH STATUS BAR PADDING
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("support_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Customer Support",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // HERO GREEN BANNER CARD
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF059669)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Center Headset Icon in Circle
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headset,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "We're Here to Help!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Get 24/7 instant live assistance via WhatsApp or Official Line.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------------------------------------------
            // CONTACT CHANNELS SECTION
            // -------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Contact Channels",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. WhatsApp Support
                SupportChannelRow(
                    icon = Icons.Default.Chat,
                    iconBg = Color(0xFF10B981),
                    iconTint = Color.White,
                    title = "Official WhatsApp Support",
                    subtitle = "+254 713 934 404 (Live 24/7)",
                    onClick = {
                        val whatsappUrl = "https://api.whatsapp.com/send?phone=254713934404&text=Hello%20Penzi%20Support"
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp Support: +254 713 934 404", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                SupportDivider()

                // 2. Email Helpdesk
                SupportChannelRow(
                    icon = Icons.Default.Email,
                    iconBg = Color(0xFFFFF7ED),
                    iconTint = Color(0xFFF97316),
                    title = "Email Helpdesk",
                    subtitle = "support@penzi.app",
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@penzi.app"))
                        try {
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Email support: support@penzi.app", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                SupportDivider()

                // 3. Help Center & FAQs
                SupportChannelRow(
                    icon = Icons.Default.Info,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    title = "Help Center & FAQs",
                    subtitle = "Browse frequently asked questions",
                    onClick = { showFaqDialog = true }
                )

                SupportDivider()

                // 4. Account & Safety Center
                SupportChannelRow(
                    icon = Icons.Default.Shield,
                    iconBg = Color(0xFFFFF7ED),
                    iconTint = Color(0xFFEA580C),
                    title = "Account & Safety Center",
                    subtitle = "Report an issue or account safety query",
                    onClick = { showSafetyDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // -------------------------------------------------------------
            // STICKY BOTTOM BUTTON: OPEN LIVE WHATSAPP CHAT
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            isWhatsAppLoading = true
                            delay(1000)
                            val whatsappUrl = "https://api.whatsapp.com/send?phone=254713934404&text=Hello%20Penzi%20Customer%20Support"
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opening WhatsApp Support: +254 713 934 404", Toast.LENGTH_LONG).show()
                            }
                            isWhatsAppLoading = false
                        }
                    },
                    enabled = !isWhatsAppLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("whatsapp_live_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    )
                ) {
                    if (isWhatsAppLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = "Open Live WhatsApp Chat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text("Frequently Asked Questions", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• How do I get verified badge?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Go to Profile -> Verify Shield and complete facial identity check.", fontSize = 12.sp, color = Color.DarkGray)
                    Text("• How do Penzi Coins work?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Use coins to send virtual gifts, unlock direct chat messages, and boost your profile.", fontSize = 12.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showFaqDialog = false }) {
                    Text("Close", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyDialog = false },
            title = { Text("Safety & Reporting", fontWeight = FontWeight.Bold) },
            text = {
                Text("Penzi maintains a strict zero-tolerance policy for harassment. Report suspicious accounts directly on profile or chat.", fontSize = 13.sp, color = Color.DarkGray)
            },
            confirmButton = {
                TextButton(onClick = { showSafetyDialog = false }) {
                    Text("Understood", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SupportChannelRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
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

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SupportDivider() {
    HorizontalDivider(
        thickness = 0.8.dp,
        color = Color(0xFFF3F4F6)
    )
}
