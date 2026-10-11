package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.DatingIntent
import com.example.model.DatingProfile
import com.example.model.Gender
import com.example.model.UserAccount
import com.example.ui.theme.KenyanRed
import com.example.ui.theme.SavannahGold
import com.example.ui.theme.SavannahGoldLight
import com.example.ui.theme.WarmSandLight
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziPrimaryGradient
import com.example.ui.theme.PenziFullScreenGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// 3D MEGAPHONE WITH ENVELOPE GRAPHIC (Message Blast)
// -------------------------------------------------------------
@Composable
fun Custom3DMegaphoneGraphic(size: Dp = 54.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Soft floor shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.14f),
            topLeft = Offset(w * 0.15f, h * 0.78f),
            size = Size(w * 0.70f, h * 0.18f)
        )

        // Megaphone Cone Body (Red / Coral 3D)
        val conePath = Path().apply {
            moveTo(w * 0.52f, h * 0.44f) // narrow base
            lineTo(w * 0.22f, h * 0.32f) // top rim
            lineTo(w * 0.22f, h * 0.72f) // bottom rim
            lineTo(w * 0.52f, h * 0.60f) // narrow base bottom
            close()
        }
        drawPath(
            path = conePath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF5722), Color(0xFFE64A19), Color(0xFFBF360C)),
                start = Offset(w * 0.2f, h * 0.3f),
                end = Offset(w * 0.6f, h * 0.7f)
            )
        )

        // Megaphone Rim Flare (White / Cream)
        val rimPath = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(w * 0.18f, h * 0.30f, w * 0.26f, h * 0.74f))
        }
        drawPath(
            path = rimPath,
            brush = Brush.horizontalGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFD97706)))
        )

        // Megaphone Handle (Dark Grey 3D)
        val handlePath = Path().apply {
            moveTo(w * 0.44f, h * 0.58f)
            lineTo(w * 0.36f, h * 0.82f)
            lineTo(w * 0.44f, h * 0.84f)
            lineTo(w * 0.50f, h * 0.58f)
            close()
        }
        drawPath(
            path = handlePath,
            brush = Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF1E293B)))
        )

        // Megaphone Base Cylinder (Golden/Amber)
        val baseOval = androidx.compose.ui.geometry.Rect(w * 0.48f, h * 0.42f, w * 0.56f, h * 0.62f)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFB45309)),
                center = Offset(w * 0.52f, h * 0.50f),
                radius = w * 0.12f
            ),
            topLeft = Offset(baseOval.left, baseOval.top),
            size = Size(baseOval.width, baseOval.height)
        )

        // 3D Flying Yellow Envelope popping out
        val envW = w * 0.42f
        val envH = h * 0.30f
        val envLeft = w * 0.54f
        val envTop = h * 0.22f

        // Envelope Shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.12f),
            topLeft = Offset(envLeft + 2f, envTop + 4f),
            size = Size(envW, envH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Envelope Base
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFEF9C3), Color(0xFFFEF08A), Color(0xFFFDE047))),
            topLeft = Offset(envLeft, envTop),
            size = Size(envW, envH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Envelope Flap
        val flapPath = Path().apply {
            moveTo(envLeft, envTop)
            lineTo(envLeft + envW / 2f, envTop + envH * 0.58f)
            lineTo(envLeft + envW, envTop)
            close()
        }
        drawPath(
            path = flapPath,
            brush = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFEF08A)))
        )
        drawPath(
            path = flapPath,
            color = Color(0xFFEAB308).copy(alpha = 0.6f),
            style = Stroke(width = 1.2f)
        )

        // Little Heart Seal on envelope
        drawCircle(
            color = Color(0xFFE11D48),
            radius = 3.2f,
            center = Offset(envLeft + envW / 2f, envTop + envH * 0.55f)
        )

        // Soundwave / Sparkle lines
        drawLine(
            color = Color(0xFFFF8A00).copy(alpha = 0.8f),
            start = Offset(w * 0.14f, h * 0.36f),
            end = Offset(w * 0.06f, h * 0.30f),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFFF8A00).copy(alpha = 0.8f),
            start = Offset(w * 0.14f, h * 0.66f),
            end = Offset(w * 0.06f, h * 0.72f),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )
    }
}

// -------------------------------------------------------------
// 3D GAME CONTROLLER GRAPHIC (Game Center)
// -------------------------------------------------------------
@Composable
fun Custom3DGameControllerGraphic(size: Dp = 54.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Soft floor shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(w * 0.12f, h * 0.72f),
            size = Size(w * 0.76f, h * 0.22f)
        )

        // Controller Main Body (Sleek Matte Black with 3D Bevel)
        val bodyPath = Path().apply {
            val cx = w * 0.5f
            val cy = h * 0.50f
            val rw = w * 0.40f
            val rh = h * 0.24f

            moveTo(cx - rw * 0.6f, cy - rh)
            lineTo(cx + rw * 0.6f, cy - rh)
            cubicTo(cx + rw * 1.1f, cy - rh, cx + rw * 1.15f, cy + rh * 0.2f, cx + rw * 0.95f, cy + rh * 0.9f)
            cubicTo(cx + rw * 0.75f, cy + rh * 1.35f, cx + rw * 0.45f, cy + rh * 1.25f, cx + rw * 0.35f, cy + rh * 0.75f)
            cubicTo(cx + rw * 0.2f, cy + rh * 0.4f, cx - rw * 0.2f, cy + rh * 0.4f, cx - rw * 0.35f, cy + rh * 0.75f)
            cubicTo(cx - rw * 0.45f, cy + rh * 1.25f, cx - rw * 0.75f, cy + rh * 1.35f, cx - rw * 0.95f, cy + rh * 0.9f)
            cubicTo(cx - rw * 1.15f, cy + rh * 0.2f, cx - rw * 1.1f, cy - rh, cx - rw * 0.6f, cy - rh)
            close()
        }

        // Body Gradient (Metallic Charcoal / Obsidian)
        val bodyBrush = Brush.radialGradient(
            colors = listOf(Color(0xFF374151), Color(0xFF1F2937), Color(0xFF111827), Color(0xFF030712)),
            center = Offset(w * 0.5f, h * 0.42f),
            radius = w * 0.45f
        )
        drawPath(path = bodyPath, brush = bodyBrush)

        // Top Rim Highlight
        drawPath(
            path = bodyPath,
            brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.45f), Color.Transparent, Color.Black.copy(alpha = 0.6f))),
            style = Stroke(width = 1.6f)
        )

        // Left D-Pad (Cross)
        val dpadX = w * 0.30f
        val dpadY = h * 0.48f
        val arm = w * 0.055f
        val thick = w * 0.042f
        // Horizontal arm
        drawRoundRect(
            color = Color(0xFF4B5563),
            topLeft = Offset(dpadX - arm, dpadY - thick / 2f),
            size = Size(arm * 2f, thick),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )
        // Vertical arm
        drawRoundRect(
            color = Color(0xFF4B5563),
            topLeft = Offset(dpadX - thick / 2f, dpadY - arm),
            size = Size(thick, arm * 2f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )

        // Right Colorful Action Buttons (X, Y, A, B)
        val btnX = w * 0.70f
        val btnY = h * 0.48f
        val offset = w * 0.065f
        val btnR = w * 0.032f

        // Top Button (Yellow)
        drawCircle(color = Color(0xFFFBBF24), radius = btnR, center = Offset(btnX, btnY - offset))
        // Right Button (Red)
        drawCircle(color = Color(0xFFEF4444), radius = btnR, center = Offset(btnX + offset, btnY))
        // Bottom Button (Green)
        drawCircle(color = Color(0xFF10B981), radius = btnR, center = Offset(btnX, btnY + offset))
        // Left Button (Blue)
        drawCircle(color = Color(0xFF3B82F6), radius = btnR, center = Offset(btnX - offset, btnY))

        // Dual Thumbsticks (Center-Bottom)
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFF6B7280), Color(0xFF1F2937))),
            radius = w * 0.062f,
            center = Offset(w * 0.42f, h * 0.58f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFF6B7280), Color(0xFF1F2937))),
            radius = w * 0.062f,
            center = Offset(w * 0.58f, h * 0.58f)
        )
    }
}

// -------------------------------------------------------------
// 3D CLIPBOARD CHECKLIST GRAPHIC (Tasks Center)
// -------------------------------------------------------------
@Composable
fun Custom3DClipboardGraphic(size: Dp = 54.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Soft floor shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.12f),
            topLeft = Offset(w * 0.16f, h * 0.78f),
            size = Size(w * 0.68f, h * 0.18f)
        )

        // Clipboard Board (Golden / Warm Amber Wood)
        val boardLeft = w * 0.22f
        val boardTop = h * 0.16f
        val boardW = w * 0.56f
        val boardH = h * 0.68f

        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFFD97706))),
            topLeft = Offset(boardLeft, boardTop),
            size = Size(boardW, boardH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
        )

        // White Paper Sheet
        val paperLeft = boardLeft + w * 0.05f
        val paperTop = boardTop + h * 0.09f
        val paperW = boardW - w * 0.10f
        val paperH = boardH - h * 0.13f

        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFF8FAFC), Color(0xFFE2E8F0))),
            topLeft = Offset(paperLeft, paperTop),
            size = Size(paperW, paperH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )

        // Top Metallic Clamp / Clip (Golden Chrome)
        val clipW = w * 0.28f
        val clipH = h * 0.12f
        val clipLeft = w * 0.50f - clipW / 2f
        val clipTop = boardTop - h * 0.04f

        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFBBF24), Color(0xFFB45309))),
            topLeft = Offset(clipLeft, clipTop),
            size = Size(clipW, clipH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )

        // Checklist lines on paper
        val lineStartY = paperTop + h * 0.14f
        for (i in 0..2) {
            val curY = lineStartY + i * (h * 0.10f)
            // Yellow check badge on first line
            if (i == 0) {
                drawCircle(
                    color = Color(0xFFFBBF24),
                    radius = 4.5f,
                    center = Offset(paperLeft + w * 0.08f, curY)
                )
                // Checkmark in badge
                val tickPath = Path().apply {
                    moveTo(paperLeft + w * 0.06f, curY)
                    lineTo(paperLeft + w * 0.075f, curY + 2.5f)
                    lineTo(paperLeft + w * 0.10f, curY - 2.5f)
                }
                drawPath(path = tickPath, color = Color.White, style = Stroke(width = 1.5f, cap = StrokeCap.Round))
            } else {
                drawCircle(
                    color = Color(0xFFCBD5E1),
                    radius = 3.5f,
                    center = Offset(paperLeft + w * 0.08f, curY)
                )
            }

            // Task line bar
            drawRoundRect(
                color = if (i == 0) Color(0xFFF59E0B).copy(alpha = 0.6f) else Color(0xFFCBD5E1),
                topLeft = Offset(paperLeft + w * 0.15f, curY - 2f),
                size = Size(paperW * 0.65f, 4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
    }
}

// -------------------------------------------------------------
// 3D FLOATING GAMES BADGE (Bottom Right)
// -------------------------------------------------------------
@Composable
fun Custom3DFloatingGamesBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = Color(0xFFEA580C),
                ambientColor = Color(0xFFC2410C)
            )
            .size(72.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF8A00),
                        Color(0xFFEA580C),
                        Color(0xFFC2410C),
                        Color(0xFF7C2D12)
                    ),
                    center = Offset(36f, 30f),
                    radius = 56f
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color(0xFFFED7AA),
                        Color(0xFFEA580C).copy(alpha = 0.5f)
                    )
                ),
                shape = CircleShape
            )
            .clickable { onClick() }
            .testTag("floating_games_button"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Custom3DGameControllerGraphic(size = 32.dp)
            Text(
                text = "GAMES",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.6.sp
            )
        }
    }
}

// -------------------------------------------------------------
// MESSAGE BLAST DIALOG
// -------------------------------------------------------------
@Composable
fun MysteryNoteScreen(
    deck: List<DatingProfile>,
    onDismiss: () -> Unit,
    onBroadcastSent: (String) -> Unit
) {
    var broadcastText by remember { mutableStateOf("") }
    var blastSent by remember { mutableStateOf(false) }
    var recipientCount by remember { mutableStateOf(10) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val presetMessages = listOf(
        "Sasa! Niko na chai moto ☕ looking for good vibes!",
        "Swahili coastal breeze or Nairobi rooftop vibes? 🌆",
        "Looking for a genuine soul to connect with in Kenya ❤️",
        "Sunday brunch & live rhumba band anyone? 🎷✨",
        "Niaje! Your smile on Penzi made my entire day 😊"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1014))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Custom3DMegaphoneGraphic(size = 38.dp)
                    Column {
                        Text(
                            text = "Mystery Note 💌",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Broadcast to 10 online Kenyan singles",
                            fontSize = 11.5.sp,
                            color = SavannahGoldLight
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (blastSent) {
                // Success View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF22C55E).copy(alpha = 0.2f),
                        border = BorderStroke(1.5.dp, Color(0xFF22C55E)),
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🚀", fontSize = 36.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Mystery Note Launched!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your greeting was delivered to $recipientCount active Kenyan singles near you. Watch your Chat tab for instant replies!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                    ) {
                        Text("Awesome! Check Chats", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Blast Input View
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.07f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Your Greeting Message:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SavannahGoldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = broadcastText,
                            onValueChange = { broadcastText = it },
                            placeholder = { Text("Write your icebreaker or pick one below...", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = SavannahGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedContainerColor = Color.Black.copy(alpha = 0.25f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.25f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Quick Icebreaker Templates:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetMessages.forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { broadcastText = preset },
                            shape = RoundedCornerShape(10.dp),
                            color = if (broadcastText == preset) Color(0xFFE11D48).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
                            border = BorderStroke(0.8.dp, if (broadcastText == preset) Color(0xFFFB7185) else Color.White.copy(alpha = 0.10f))
                        ) {
                            Text(
                                text = preset,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val msg = broadcastText.ifBlank { "Sasa! Niko na chai moto ☕ looking for good vibes!" }
                        blastSent = true
                        Toast.makeText(context, "🚀 Mystery Note sent to 10 Kenyan singles!", Toast.LENGTH_SHORT).show()
                        onBroadcastSent(msg)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📢", fontSize = 16.sp)
                        Text(
                            text = "Send Mystery Note Now",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// GAME CENTER / PARTY GAMES DIALOG
// -------------------------------------------------------------
@Composable
fun GameCenterScreen(
    deck: List<DatingProfile>,
    onDismiss: () -> Unit,
    onOpenChatWithMatch: (DatingProfile) -> Unit
) {
    var selectedGameTab by remember { mutableStateOf(0) }
    var currentPromptIndex by remember { mutableStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val truthOrDareList = listOf(
        "Truth: What is your biggest green flag in a partner?" to "Dare: Send a voice note singing your favorite Kenyan song 🎤",
        "Truth: Who was your first celebrity crush in East Africa?" to "Dare: Share the most adventurous road trip you've ever taken 🚙",
        "Truth: What's the weirdest food combo you actually love?" to "Dare: Ask your match out for coffee in Swahili right now! ☕",
        "Truth: What's your secret hidden talent?" to "Dare: Send a selfie making your funniest face 🤪",
        "Truth: Would you rather date someone who talks too much or too little?" to "Dare: Send a 10-second dance video to Rhumba 💃"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF171324))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Custom3DGameControllerGraphic(size = 38.dp)
                        Column {
                            Text(
                                text = "Game Center 🎮",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Play Icebreakers & Party Games",
                                fontSize = 11.5.sp,
                                color = SavannahGoldLight
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Game Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Truth or Dare", "Compatibility", "Spin Bottle").forEachIndexed { index, name ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedGameTab = index },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedGameTab == index) Color(0xFF7C3AED) else Color.Transparent
                        ) {
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = if (selectedGameTab == index) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                val currentCard = truthOrDareList[currentPromptIndex % truthOrDareList.size]

                when (selectedGameTab) {
                    0 -> {
                        // TRUTH OR DARE
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF231B38),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("🎭 TRUTH:", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(currentCard.first, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Color.White)

                                Spacer(modifier = Modifier.height(14.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                                Spacer(modifier = Modifier.height(14.dp))

                                Text("🔥 DARE:", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFFB7185))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(currentCard.second, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { currentPromptIndex++ },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f))
                            ) {
                                Text("Next Card 🎲", color = Color.White, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val randomMatch = deck.firstOrNull()
                                    if (randomMatch != null) {
                                        onDismiss()
                                        onOpenChatWithMatch(randomMatch)
                                        Toast.makeText(context, "Playing Truth or Dare with ${randomMatch.name}!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) {
                                Text("Play in Chat 💬", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                    1 -> {
                        // COMPATIBILITY QUIZ
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF231B38),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("💘 Love Vibe Match", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your Kenya Dating Match Score: 98%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SavannahGoldLight
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "You both love weekend road trips, coastal sunsets, and authentic Kenyan chai! ☕🏖️",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val randomMatch = deck.firstOrNull()
                                if (randomMatch != null) {
                                    onDismiss()
                                    onOpenChatWithMatch(randomMatch)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(23.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                        ) {
                            Text("Find 98% Match in Chat ❤️", fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        // SPIN THE BOTTLE
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF231B38),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🍾", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                val luckyMatch = deck.getOrNull(currentPromptIndex % deck.size.coerceAtLeast(1))
                                Text(
                                    text = "Bottle Pointed to: ${luckyMatch?.name ?: "Faith"}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SavannahGoldLight
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Location: ${luckyMatch?.neighborhood?.ifBlank { luckyMatch.city } ?: "Nairobi"}",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { currentPromptIndex++ },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f))
                            ) {
                                Text("Spin Again 🍾", color = Color.White, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val luckyMatch = deck.getOrNull(currentPromptIndex % deck.size.coerceAtLeast(1))
                                    if (luckyMatch != null) {
                                        onDismiss()
                                        onOpenChatWithMatch(luckyMatch)
                                    }
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) {
                                Text("Say Hi 👋", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun MatchCelebrationDialog(
    matchProfile: DatingProfile,
    onDismiss: () -> Unit,
    onSendMessage: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NI PENZI! ❤️🇰🇪",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = KenyanRed,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "You and ${matchProfile.name} liked each other!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(4.dp, Brush.sweepGradient(listOf(KenyanRed, SavannahGold, Color(0xFF1F2937))), CircleShape)
                ) {
                    AsyncImage(
                        model = matchProfile.imageUrls.firstOrNull() ?: "",
                        contentDescription = matchProfile.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${matchProfile.name}, ${matchProfile.age}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("match_send_message_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KenyanRed)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Say Jambo to ${matchProfile.name}", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ElevatedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Keep Exploring", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDetailDialog(
    profile: DatingProfile,
    onDismiss: () -> Unit,
    onLike: () -> Unit,
    onChat: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp)
                ) {
                    // Large Image Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        AsyncImage(
                            model = profile.imageUrls.firstOrNull() ?: "",
                            contentDescription = profile.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        // Close Button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }

                        // Bottom Overlay Title inside Photo
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${profile.name}, ${profile.age}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (profile.verified) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = Color(0xFFFDB813),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            val locationParts = listOf(profile.neighborhood, profile.city).filter { it.isNotBlank() }
                            if (locationParts.isNotEmpty()) {
                                Text(
                                    text = "📍 ${locationParts.joinToString(", ")}",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Content Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Intent & Occupation Badge
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFF7ED),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD8A8))
                            ) {
                                Text(
                                    text = "${profile.intent.emoji} ${profile.intent.label}",
                                    color = Color(0xFFC82A2A),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF0FDF4)
                            ) {
                                Text(
                                    text = "💼 ${profile.occupation}",
                                    color = Color(0xFF166534),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Bio
                        if (profile.bio.isNotBlank()) {
                            Column {
                                Text(
                                    text = "About Me",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = profile.bio,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF4A4A4A)
                                )
                            }
                        }

                        // Interests FlowRow
                        if (profile.interests.isNotEmpty()) {
                            Column {
                                Text(
                                    text = "Interests & Passions",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    profile.interests.forEach { interest ->
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFFF3F4F6)
                                        ) {
                                            Text(
                                                text = interest,
                                                fontSize = 13.sp,
                                                color = Color(0xFF374151),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Sticky Bottom Action Bar inside Dialog
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                onLike()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Like", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                onChat()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))
                        ) {
                            Icon(Icons.Default.WavingHand, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Say Hi 👋", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userAccount: UserAccount?,
    deck: List<DatingProfile>,
    matchedOverlay: DatingProfile?,
    likedProfileIds: Set<String> = emptySet(),
    isLoading: Boolean = false,
    onLike: (DatingProfile) -> Unit,
    onPass: (DatingProfile) -> Unit,
    onSuperLike: (DatingProfile) -> Unit,
    onDismissMatch: () -> Unit,
    onOpenChat: (DatingProfile) -> Unit,
    onSayHi: (DatingProfile) -> Unit = { onOpenChat(it) },
    onOpenProfileDetail: (DatingProfile) -> Unit,
    onRefreshProfiles: () -> Unit = {},
    onOpenTaskCenter: () -> Unit = {}
) {
    var activeSubTab by remember { mutableStateOf("Recommend") }
    var isRefreshing by remember { mutableStateOf(false) }
    var showMessageBlastDialog by remember { mutableStateOf(false) }
    var showGameCenterDialog by remember { mutableStateOf(false) }
    var showMysteryNoteDialog by remember { mutableStateOf(false) }
    var showTaskCenterScreen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Always ensure profiles are loaded immediately when Home screen is displayed
    LaunchedEffect(Unit) {
        onRefreshProfiles()
    }

    val filteredProfiles = remember(deck, activeSubTab, userAccount) {
        when (activeSubTab) {
            "Nearby" -> {
                val myCounty = userAccount?.city.orEmpty().trim()
                val myRegion = userAccount?.neighborhood.orEmpty().trim()
                if (myCounty.isNotEmpty() || myRegion.isNotEmpty()) {
                    deck.filter { profile ->
                        profile.city.equals(myCounty, ignoreCase = true) ||
                        profile.neighborhood.equals(myRegion, ignoreCase = true)
                    }.sortedBy { it.distanceKm }
                } else {
                    deck.sortedBy { it.distanceKm }
                }
            }
            "Verified" -> deck.filter { it.verified }
            else -> deck // Recommend shows all Kenyan singles
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        @OptIn(ExperimentalFoundationApi::class)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                onRefreshProfiles()
                coroutineScope.launch {
                    kotlinx.coroutines.delay(800)
                    isRefreshing = false
                }
            },
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = rememberPullToRefreshState(),
                    isRefreshing = isRefreshing,
                    containerColor = Color.White,
                    color = SavannahGold,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. TOP HEADER WITH 3 ACTION CARDS (Color matching status bar, extending 3/4 way down buttons and fading to white)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color(0xFFFF5722),
                                    0.45f to Color(0xFFFF5722),
                                    0.75f to Color(0xFFFF8A50),
                                    0.90f to Color(0xFFFFF2ED),
                                    1.0f to Color.White
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 12.dp)
                        ) {
                            // THREE TOP ACTION CARDS: Message Blast | Game Center | Tasks Center
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Message Blast Card
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(102.dp)
                                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x33000000))
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { showMessageBlastDialog = true }
                                        .testTag("button_message_blast"),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0xFFFFF1F2)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp)) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Mystery\nNote",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                lineHeight = 16.sp,
                                                color = Color(0xFFE11D48)
                                            )
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.BottomEnd
                                            ) {
                                                Custom3DMegaphoneGraphic(size = 63.dp)
                                            }
                                        }
                                    }
                                }

                                // 2. Game Center Card
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(102.dp)
                                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x33000000))
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { showGameCenterDialog = true }
                                        .testTag("button_game_center"),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp)) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Game\nCenter",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                lineHeight = 16.sp,
                                                color = Color(0xFFB45309)
                                            )
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.BottomEnd
                                            ) {
                                                Custom3DGameControllerGraphic(size = 63.dp)
                                            }
                                        }
                                    }
                                }

                                // 3. Tasks Center Card
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(102.dp)
                                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x33000000))
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { showTaskCenterScreen = true }
                                        .testTag("button_tasks_center"),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0xFFF3E8FF)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp)) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Tasks\nCenter",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                lineHeight = 16.sp,
                                                color = Color(0xFF7C3AED)
                                            )
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.BottomEnd
                                            ) {
                                                Custom3DClipboardGraphic(size = 63.dp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. SUB-TABS ROW: Recommend | Nearby (Clean White with Yellow Bar)
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Recommend Tab
                        Column(
                            modifier = Modifier
                                .clickable { activeSubTab = "Recommend" }
                                .testTag("tab_recommend"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Recommend",
                                fontSize = 20.sp,
                                fontWeight = if (activeSubTab == "Recommend") FontWeight.Black else FontWeight.Bold,
                                color = if (activeSubTab == "Recommend") Color(0xFF111827) else Color(0xFF9CA3AF)
                            )
                            if (activeSubTab == "Recommend") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(34.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFFBBF24))
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Nearby Tab
                        Column(
                            modifier = Modifier
                                .clickable { activeSubTab = "Nearby" }
                                .testTag("tab_nearby"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nearby",
                                fontSize = 20.sp,
                                fontWeight = if (activeSubTab == "Nearby") FontWeight.Black else FontWeight.Bold,
                                color = if (activeSubTab == "Nearby") Color(0xFF111827) else Color(0xFF9CA3AF)
                            )
                            if (activeSubTab == "Nearby") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(34.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFFBBF24))
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // 3. Profiles List / Loading State
                if (isLoading && filteredProfiles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = SavannahGold,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading Kenyan singles...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF4B5563)
                                )
                            }
                        }
                    }
                } else if (filteredProfiles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp)
                                .padding(horizontal = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF3F4F6),
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Color(0xFFB91C1C),
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = "No other real profiles yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "When new members complete registration in the app, their real accounts will appear here in real-time.",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { onRefreshProfiles() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = KenyanRed
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text("Refresh Feed", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    val chunked = filteredProfiles.chunked(2)
                    items(chunked, key = { chunk -> chunk.map { it.id }.joinToString("-") }) { rowProfiles ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowProfiles.forEach { profile ->
                                Box(modifier = Modifier.weight(1f)) {
                                    DiscoverUserCard(
                                        profile = profile,
                                        onChatClick = { onOpenChat(profile) },
                                        onSayHi = { onSayHi(profile) },
                                        onCardClick = { onOpenProfileDetail(profile) }
                                    )
                                }
                            }
                            if (rowProfiles.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Match Overlay celebration dialog if triggered
        if (matchedOverlay != null) {
            MatchCelebrationDialog(
                matchProfile = matchedOverlay,
                onDismiss = onDismissMatch,
                onSendMessage = { onSayHi(matchedOverlay) }
            )
        }

        // Mystery Note Fullscreen Screen
        if (showMessageBlastDialog) {
            Dialog(
                onDismissRequest = { showMessageBlastDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                MysteryNoteScreen(
                    deck = deck,
                    onDismiss = { showMessageBlastDialog = false },
                    onBroadcastSent = { msg ->
                        // Optionally forward to recent matches
                    }
                )
            }
        }

        // Game Center Fullscreen Screen
        if (showGameCenterDialog) {
            Dialog(
                onDismissRequest = { showGameCenterDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                GameCenterScreen(
                    deck = deck,
                    onDismiss = { showGameCenterDialog = false },
                    onOpenChatWithMatch = { profile ->
                        showGameCenterDialog = false
                        onSayHi(profile)
                    }
                )
            }
        }

        // Tasks Center Fullscreen Screen
        if (showTaskCenterScreen) {
            Dialog(
                onDismissRequest = { showTaskCenterScreen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                val userSession = remember { com.example.data.UserSessionManager(context) }
                TaskCenterScreen(
                    onBack = { showTaskCenterScreen = false },
                    userId = userAccount?.id ?: "current_user",
                    authToken = userSession.getSavedAuthToken(),
                    onCoinsEarned = {}
                )
            }
        }

        // Mystery Note Dialog
        if (showMysteryNoteDialog) {
            MysteryNoteDialog(
                deck = deck,
                onDismiss = { showMysteryNoteDialog = false },
                onOpenChat = { profile ->
                    showMysteryNoteDialog = false
                    onSayHi(profile)
                }
            )
        }
    }
}

@Composable
fun MysteryNoteDialog(
    deck: List<DatingProfile>,
    onDismiss: () -> Unit,
    onOpenChat: (DatingProfile) -> Unit
) {
    var activeTab by remember { mutableStateOf(0) }
    var currentNoteIndex by remember { mutableStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }
    var userNoteText by remember { mutableStateOf("") }
    var castSuccess by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val sampleNotes = remember {
        listOf(
            "Tabasamu yako ilinifanya nitabasamu leo nzima! Tukutane Westlands tukunywe kahawa? ☕" to ("Brenda" to "Kilimani, Nairobi"),
            "Looking for a partner in crime for weekend Ngong Hills hikes & road trips to Naivasha! 🚙🏞️" to ("Brian" to "Westlands, Nairobi"),
            "Kama penzi ni wimbo wa rhumba, hebu tucheze pamoja Diani beach! 🎷✨" to ("Amina" to "Nyali, Mombasa"),
            "You are the chapati to my madondo ❤️ Let's grab some dinner & vibe!" to ("Eric" to "Lavington, Nairobi"),
            "Niaje! Nilitaka tu kusema uko na positive energy sana. Karibu tuchat 😊" to ("Faith" to "Karen, Nairobi"),
            "Swahili coastal breeze meets Nairobi ambition. Let's make genuine memories together 🌊🏙️" to ("Halima" to "Bamburi, Mombasa"),
            "Coffee, deep conversations & hilarious laughter. Is that your vibe too? ☕✨" to ("Kevin" to "Kileleshwa, Nairobi"),
            "Wewe ni mrembo sana! Sijawahi kuona mtu mwenye macho yanayovutia kama yako 💖" to ("Mercy" to "Kisumu City"),
            "Form ya Sunday brunch & live acoustic band ikoje? Niambie kama uko ready! 🥂" to ("Dennis" to "Nakuru Town")
        )
    }

    val currentNote = sampleNotes[currentNoteIndex % sampleNotes.size]
    val matchedProfile = deck.find { it.name.contains(currentNote.second.first, ignoreCase = true) } ?: deck.firstOrNull()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF181313))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💌", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "Penzi Mystery Note",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Secret romantic confessions across Kenya 🇰🇪",
                                fontSize = 11.5.sp,
                                color = SavannahGold
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Tab Switcher: [ Open Secret Note | Cast a Note ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeTab = 0; castSuccess = false },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeTab == 0) Color(0xFFE11D48) else Color.Transparent
                    ) {
                        Text(
                            text = "💌 Secret Notes",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeTab = 1 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (activeTab == 1) Color(0xFFE11D48) else Color.Transparent
                    ) {
                        Text(
                            text = "✍️ Cast a Note",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (activeTab == 0) {
                    // TAB 0: OPEN SECRET NOTE
                    if (!isRevealed) {
                        // Mystery Envelope/Bottle Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    isRevealed = true
                                    Toast.makeText(context, "💌 Mystery Note Opened! +20 Coins Bonus!", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF2A1520),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFB7185), SavannahGold)))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Brush.radialGradient(listOf(Color(0xFFE11D48), Color(0xFF881337)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💌", fontSize = 40.sp)
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Tap to Unseal Secret Note",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "A secret admirer from ${currentNote.second.second} sent a message to the Penzi Ocean!",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = SavannahGold.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SavannahGold)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("🎁", fontSize = 13.sp)
                                        Text(
                                            text = "Reward: +20 Coins on Unseal",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SavannahGoldLight
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Revealed Secret Note Content
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF261820),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFB7185).copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (matchedProfile != null && matchedProfile.imageUrls.isNotEmpty()) {
                                        AsyncImage(
                                            model = matchedProfile.imageUrls.first(),
                                            contentDescription = matchedProfile.name,
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, Color(0xFFFB7185), CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE11D48)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🌸", fontSize = 20.sp)
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "${currentNote.second.first}, ${matchedProfile?.age ?: 24}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "📍 ${currentNote.second.second}",
                                            fontSize = 12.sp,
                                            color = SavannahGoldLight
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF191015)
                                ) {
                                    Text(
                                        text = "\"${currentNote.first}\"",
                                        fontSize = 14.5.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        lineHeight = 22.sp,
                                        color = Color(0xFFFFF1F2),
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (matchedProfile != null) {
                                        Button(
                                            onClick = { onOpenChat(matchedProfile) },
                                            modifier = Modifier.weight(1f).height(44.dp),
                                            shape = RoundedCornerShape(22.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                                        ) {
                                            Text("Say Hi 💬", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    ElevatedButton(
                                        onClick = {
                                            currentNoteIndex++
                                            isRevealed = false
                                        },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(22.dp),
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = Color.White.copy(alpha = 0.12f),
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Next Note 🔀", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 1: CAST A NOTE
                    if (castSuccess) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌊💌", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Note Cast into Penzi Ocean!",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your secret note will float to Kenyan singles near you.",
                                fontSize = 12.5.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = {
                                    castSuccess = false
                                    userNoteText = ""
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                            ) {
                                Text("Write Another Note", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Cast an Anonymous Sweet Note",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Write a pickup line, coffee invite, or compliment for someone in Kenya to find.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            androidx.compose.material3.OutlinedTextField(
                                value = userNoteText,
                                onValueChange = { userNoteText = it },
                                placeholder = {
                                    Text("e.g. Tukutane Java Westlands for a warm latte and good conversation? ☕", color = Color.Gray, fontSize = 13.sp)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFB7185),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF22151B),
                                    unfocusedContainerColor = Color(0xFF22151B)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick Ideas Chips
                            Text("Quick ideas:", fontSize = 11.sp, color = SavannahGoldLight)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Chai date? ☕", "Rhumba night 💃", "SGR to Coast 🏖️").forEach { idea ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(alpha = 0.08f),
                                        modifier = Modifier.clickable { userNoteText = idea }
                                    ) {
                                        Text(
                                            text = idea,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    if (userNoteText.isNotBlank()) {
                                        castSuccess = true
                                        Toast.makeText(context, "🚀 Note launched into Penzi Ocean!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Please write a short note first!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(23.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                            ) {
                                Text("Cast into Penzi Ocean 🌊", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    badgeText: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp,
                    color = Color(0xFF1E1E1E)
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (badgeText != null) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = iconTint
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverUserCard(
    profile: DatingProfile,
    onChatClick: () -> Unit,
    onSayHi: () -> Unit = onChatClick,
    onCardClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x22000000))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // User Photo
            AsyncImage(
                model = profile.imageUrls.firstOrNull() ?: "",
                contentDescription = profile.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Top Right 3D-styled Vibrant "HI" Button
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0xFFFF5722))
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onSayHi()
                    }
                    .testTag("hi_button_${profile.id}"),
                shape = RoundedCornerShape(14.dp),
                color = Color.Transparent,
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFF7A00),
                                    Color(0xFFFF4500)
                                )
                            )
                        )
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "HI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Bottom Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.45f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Profile info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 9.dp)
            ) {
                // Name & Amber Verified Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = profile.name.lowercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Amber verified badge
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Badges Row (Sky Blue Gender/Age + Lime Kenya Tag)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gender & Age Tag (Sky Blue pill)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0284C7)
                    ) {
                        Text(
                            text = "${if (profile.gender == Gender.FEMALE) "♀" else "♂"} ${profile.age}",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Location / Country Tag (Lime Green pill)
                    val locationTag = profile.neighborhood.ifBlank { profile.city.ifBlank { "Kenya" } }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFA3E635)
                    ) {
                        Text(
                            text = locationTag,
                            color = Color(0xFF111827),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Realtime Online Activity Indicator
            if (profile.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(9.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                }
            }
        }
    }
}
